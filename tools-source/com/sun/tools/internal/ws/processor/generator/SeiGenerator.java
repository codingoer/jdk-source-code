package com.sun.tools.internal.ws.processor.generator;

import com.sun.codemodel.internal.ClassType;
import com.sun.codemodel.internal.JAnnotationArrayMember;
import com.sun.codemodel.internal.JAnnotationUse;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JClassAlreadyExistsException;
import com.sun.codemodel.internal.JCommentPart;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JDocComment;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.ws.api.TJavaGeneratorExtension;
import com.sun.tools.internal.ws.processor.model.AsyncOperation;
import com.sun.tools.internal.ws.processor.model.Block;
import com.sun.tools.internal.ws.processor.model.Fault;
import com.sun.tools.internal.ws.processor.model.Message;
import com.sun.tools.internal.ws.processor.model.Model;
import com.sun.tools.internal.ws.processor.model.Operation;
import com.sun.tools.internal.ws.processor.model.Parameter;
import com.sun.tools.internal.ws.processor.model.Port;
import com.sun.tools.internal.ws.processor.model.Request;
import com.sun.tools.internal.ws.processor.model.Response;
import com.sun.tools.internal.ws.processor.model.Service;
import com.sun.tools.internal.ws.processor.model.java.JavaInterface;
import com.sun.tools.internal.ws.processor.model.java.JavaMethod;
import com.sun.tools.internal.ws.processor.model.java.JavaParameter;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBType;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBTypeAndAnnotation;
import com.sun.tools.internal.ws.resources.GeneratorMessages;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.Options;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wsdl.document.PortType;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.WebParam.Mode;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import org.xml.sax.Locator;

public class SeiGenerator extends GeneratorBase {
   private TJavaGeneratorExtension extension;
   private List extensionHandlers;
   private boolean isDocStyle = true;
   private boolean sameParamStyle = true;

   public static void generate(Model model, WsimportOptions options, ErrorReceiver receiver, TJavaGeneratorExtension... extensions) {
      SeiGenerator seiGenerator = new SeiGenerator();
      seiGenerator.init(model, options, receiver, extensions);
      seiGenerator.doGeneration();
   }

   public void init(Model model, WsimportOptions options, ErrorReceiver receiver, TJavaGeneratorExtension... extensions) {
      this.init(model, options, receiver);
      this.extensionHandlers = new ArrayList();
      if (options.target.isLaterThan(Options.Target.V2_2)) {
         this.register(new W3CAddressingJavaGeneratorExtension());
      }

      TJavaGeneratorExtension[] var5 = extensions;
      int var6 = extensions.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         TJavaGeneratorExtension j = var5[var7];
         this.register(j);
      }

      this.extension = new JavaGeneratorExtensionFacade((TJavaGeneratorExtension[])this.extensionHandlers.toArray(new TJavaGeneratorExtension[this.extensionHandlers.size()]));
   }

   private void write(Port port) {
      JavaInterface intf = port.getJavaInterface();
      String className = Names.customJavaTypeClassName(intf);
      if (this.donotOverride && GeneratorUtil.classExists(this.options, className)) {
         this.log("Class " + className + " exists. Not overriding.");
      } else {
         JDefinedClass cls;
         try {
            cls = this.getClass(className, ClassType.INTERFACE);
         } catch (JClassAlreadyExistsException var20) {
            QName portTypeName = (QName)port.getProperty("com.sun.xml.internal.ws.processor.model.WSDLPortTypeName");
            Locator loc = null;
            if (portTypeName != null) {
               PortType pt = (PortType)port.portTypes.get(portTypeName);
               if (pt != null) {
                  loc = pt.getLocator();
               }
            }

            this.receiver.error(loc, GeneratorMessages.GENERATOR_SEI_CLASS_ALREADY_EXIST(intf.getName(), portTypeName));
            return;
         }

         if (cls.methods().isEmpty()) {
            JDocComment comment = cls.javadoc();
            String ptDoc = intf.getJavaDoc();
            if (ptDoc != null) {
               comment.add(ptDoc);
               comment.add("\n\n");
            }

            Iterator var22 = this.getJAXWSClassComment().iterator();

            while(var22.hasNext()) {
               String doc = (String)var22.next();
               comment.add(doc);
            }

            JAnnotationUse webServiceAnn = cls.annotate(this.cm.ref(WebService.class));
            this.writeWebServiceAnnotation(port, webServiceAnn);
            this.writeHandlerConfig(Names.customJavaTypeClassName(port.getJavaInterface()), cls, this.options);
            this.writeSOAPBinding(port, cls);
            if (this.options.target.isLaterThan(Options.Target.V2_1)) {
               this.writeXmlSeeAlso(cls);
            }

            Iterator var25 = port.getOperations().iterator();

            while(var25.hasNext()) {
               Operation operation = (Operation)var25.next();
               JavaMethod method = operation.getJavaMethod();
               String methodJavaDoc = operation.getJavaDoc();
               JMethod m;
               JDocComment methodDoc;
               if (method.getReturnType().getName().equals("void")) {
                  m = cls.method(1, (Class)Void.TYPE, method.getName());
                  methodDoc = m.javadoc();
               } else {
                  JAXBTypeAndAnnotation retType = method.getReturnType().getType();
                  m = cls.method(1, (JType)retType.getType(), method.getName());
                  retType.annotate(m);
                  methodDoc = m.javadoc();
                  JCommentPart ret = methodDoc.addReturn();
                  ret.add("returns " + retType.getName());
               }

               if (methodJavaDoc != null) {
                  methodDoc.add(methodJavaDoc);
               }

               this.writeWebMethod(operation, m);
               JClass holder = this.cm.ref(Holder.class);
               Iterator var27 = method.getParametersList().iterator();

               while(var27.hasNext()) {
                  JavaParameter parameter = (JavaParameter)var27.next();
                  JAXBTypeAndAnnotation paramType = parameter.getType().getType();
                  JVar var;
                  if (parameter.isHolder()) {
                     var = m.param((JType)holder.narrow(paramType.getType().boxify()), parameter.getName());
                  } else {
                     var = m.param(paramType.getType(), parameter.getName());
                  }

                  paramType.annotate(var);
                  methodDoc.addParam(var);
                  JAnnotationUse paramAnn = var.annotate(this.cm.ref(WebParam.class));
                  this.writeWebParam(operation, parameter, paramAnn);
               }

               com.sun.tools.internal.ws.wsdl.document.Operation wsdlOp = operation.getWSDLPortTypeOperation();
               Iterator var29 = operation.getFaultsSet().iterator();

               while(var29.hasNext()) {
                  Fault fault = (Fault)var29.next();
                  m._throws(fault.getExceptionClass());
                  methodDoc.addThrows(fault.getExceptionClass());
                  wsdlOp.putFault(fault.getWsdlFaultName(), fault.getExceptionClass());
               }

               this.extension.writeMethodAnnotations(wsdlOp, m);
            }

         }
      }
   }

   private void writeXmlSeeAlso(JDefinedClass cls) {
      if (this.model.getJAXBModel().getS2JJAXBModel() != null) {
         List objectFactories = this.model.getJAXBModel().getS2JJAXBModel().getAllObjectFactories();
         if (objectFactories.isEmpty()) {
            return;
         }

         JAnnotationUse xmlSeeAlso = cls.annotate(this.cm.ref(XmlSeeAlso.class));
         JAnnotationArrayMember paramArray = xmlSeeAlso.paramArray("value");

         JClass of;
         for(Iterator var5 = objectFactories.iterator(); var5.hasNext(); paramArray = paramArray.param((JType)of)) {
            of = (JClass)var5.next();
         }
      }

   }

   private void writeWebMethod(Operation operation, JMethod m) {
      Response response = operation.getResponse();
      JAnnotationUse webMethodAnn = m.annotate(this.cm.ref(WebMethod.class));
      String operationName = operation instanceof AsyncOperation ? ((AsyncOperation)operation).getNormalOperation().getName().getLocalPart() : operation.getName().getLocalPart();
      if (!m.name().equals(operationName)) {
         webMethodAnn.param("operationName", operationName);
      }

      if (operation.getSOAPAction() != null && operation.getSOAPAction().length() > 0) {
         webMethodAnn.param("action", operation.getSOAPAction());
      }

      Block block;
      if (operation.getResponse() == null) {
         m.annotate(Oneway.class);
      } else if (!operation.getJavaMethod().getReturnType().getName().equals("void") && operation.getResponse().getParametersList().size() > 0) {
         String resultName = null;
         String nsURI = null;
         if (operation.getResponse().getBodyBlocks().hasNext()) {
            block = (Block)operation.getResponse().getBodyBlocks().next();
            resultName = block.getName().getLocalPart();
            if (this.isDocStyle || block.getLocation() == 2) {
               nsURI = block.getName().getNamespaceURI();
            }
         }

         Iterator var9 = operation.getResponse().getParametersList().iterator();

         label115:
         while(true) {
            Parameter parameter;
            do {
               do {
                  if (!var9.hasNext()) {
                     break label115;
                  }

                  parameter = (Parameter)var9.next();
               } while(parameter.getParameterIndex() != -1);

               if (!operation.isWrapped() && this.isDocStyle) {
                  if (this.isDocStyle) {
                     JAXBType t = (JAXBType)parameter.getType();
                     resultName = t.getName().getLocalPart();
                     nsURI = t.getName().getNamespaceURI();
                  }
               } else {
                  if (parameter.getBlock().getLocation() == 2) {
                     resultName = parameter.getBlock().getName().getLocalPart();
                  } else {
                     resultName = parameter.getName();
                  }

                  if (this.isDocStyle || parameter.getBlock().getLocation() == 2) {
                     nsURI = parameter.getType().getName().getNamespaceURI();
                  }
               }
            } while(operation instanceof AsyncOperation);

            JAnnotationUse wr = null;
            if (!resultName.equals("return")) {
               wr = m.annotate(WebResult.class);
               wr.param("name", resultName);
            }

            if (nsURI != null || this.isDocStyle && operation.isWrapped()) {
               if (wr == null) {
                  wr = m.annotate(WebResult.class);
               }

               wr.param("targetNamespace", nsURI);
            }

            if (!this.isDocStyle || !operation.isWrapped() || parameter.getBlock().getLocation() == 2) {
               if (wr == null) {
                  wr = m.annotate(WebResult.class);
               }

               wr.param("partName", parameter.getName());
            }

            if (parameter.getBlock().getLocation() == 2) {
               if (wr == null) {
                  wr = m.annotate(WebResult.class);
               }

               wr.param("header", true);
            }
         }
      }

      if (!this.sameParamStyle && !operation.isWrapped()) {
         JAnnotationUse sb = m.annotate(SOAPBinding.class);
         sb.param("parameterStyle", (Enum)ParameterStyle.BARE);
      }

      if (operation.isWrapped() && operation.getStyle().equals(SOAPStyle.DOCUMENT)) {
         block = (Block)operation.getRequest().getBodyBlocks().next();
         JAnnotationUse reqW = m.annotate(RequestWrapper.class);
         reqW.param("localName", block.getName().getLocalPart());
         reqW.param("targetNamespace", block.getName().getNamespaceURI());
         reqW.param("className", block.getType().getJavaType().getName());
         if (response != null) {
            JAnnotationUse resW = m.annotate(ResponseWrapper.class);
            Block resBlock = (Block)response.getBodyBlocks().next();
            resW.param("localName", resBlock.getName().getLocalPart());
            resW.param("targetNamespace", resBlock.getName().getNamespaceURI());
            resW.param("className", resBlock.getType().getJavaType().getName());
         }
      }

   }

   private boolean isMessageParam(Parameter param, Message message) {
      Block block = param.getBlock();
      return message.getBodyBlockCount() > 0 && block.equals(message.getBodyBlocks().next()) || message.getHeaderBlockCount() > 0 && block.equals(message.getHeaderBlocks().next());
   }

   private boolean isHeaderParam(Parameter param, Message message) {
      if (message.getHeaderBlockCount() == 0) {
         return false;
      } else {
         Iterator var3 = message.getHeaderBlocksMap().values().iterator();

         Block headerBlock;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            headerBlock = (Block)var3.next();
         } while(!param.getBlock().equals(headerBlock));

         return true;
      }
   }

   private boolean isAttachmentParam(Parameter param, Message message) {
      if (message.getAttachmentBlockCount() == 0) {
         return false;
      } else {
         Iterator var3 = message.getAttachmentBlocksMap().values().iterator();

         Block attBlock;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            attBlock = (Block)var3.next();
         } while(!param.getBlock().equals(attBlock));

         return true;
      }
   }

   private boolean isUnboundParam(Parameter param, Message message) {
      if (message.getUnboundBlocksCount() == 0) {
         return false;
      } else {
         Iterator var3 = message.getUnboundBlocksMap().values().iterator();

         Block unboundBlock;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            unboundBlock = (Block)var3.next();
         } while(!param.getBlock().equals(unboundBlock));

         return true;
      }
   }

   private void writeWebParam(Operation operation, JavaParameter javaParameter, JAnnotationUse paramAnno) {
      Parameter param = javaParameter.getParameter();
      Request req = operation.getRequest();
      Response res = operation.getResponse();
      boolean header = this.isHeaderParam(param, req) || res != null && this.isHeaderParam(param, res);
      boolean isWrapped = operation.isWrapped();
      String name;
      if (param.getBlock().getLocation() != 2 && (!this.isDocStyle || isWrapped)) {
         name = param.getName();
      } else {
         name = param.getBlock().getName().getLocalPart();
      }

      paramAnno.param("name", name);
      String ns = null;
      if (this.isDocStyle) {
         ns = param.getBlock().getName().getNamespaceURI();
         if (isWrapped) {
            ns = param.getType().getName().getNamespaceURI();
         }
      } else if (header) {
         ns = param.getBlock().getName().getNamespaceURI();
      }

      if (ns != null || this.isDocStyle && isWrapped) {
         paramAnno.param("targetNamespace", ns);
      }

      if (header) {
         paramAnno.param("header", true);
      }

      if (param.isINOUT()) {
         paramAnno.param("mode", (Enum)Mode.INOUT);
      } else if (res != null && (this.isMessageParam(param, res) || this.isHeaderParam(param, res) || this.isAttachmentParam(param, res) || this.isUnboundParam(param, res) || param.isOUT())) {
         paramAnno.param("mode", (Enum)Mode.OUT);
      }

      if (!this.isDocStyle || !isWrapped || header) {
         paramAnno.param("partName", javaParameter.getParameter().getName());
      }

   }

   private void writeSOAPBinding(Port port, JDefinedClass cls) {
      JAnnotationUse soapBindingAnn = null;
      this.isDocStyle = port.getStyle() == null || port.getStyle().equals(SOAPStyle.DOCUMENT);
      if (!this.isDocStyle) {
         soapBindingAnn = cls.annotate(SOAPBinding.class);
         soapBindingAnn.param("style", (Enum)Style.RPC);
         port.setWrapped(true);
      }

      if (this.isDocStyle) {
         boolean first = true;
         boolean isWrapper = true;
         Iterator var6 = port.getOperations().iterator();

         while(var6.hasNext()) {
            Operation operation = (Operation)var6.next();
            if (first) {
               isWrapper = operation.isWrapped();
               first = false;
            } else {
               this.sameParamStyle = isWrapper == operation.isWrapped();
               if (!this.sameParamStyle) {
                  break;
               }
            }
         }

         if (this.sameParamStyle) {
            port.setWrapped(isWrapper);
         }
      }

      if (this.sameParamStyle && !port.isWrapped()) {
         if (soapBindingAnn == null) {
            soapBindingAnn = cls.annotate(SOAPBinding.class);
         }

         soapBindingAnn.param("parameterStyle", (Enum)ParameterStyle.BARE);
      }

   }

   private void writeWebServiceAnnotation(Port port, JAnnotationUse wsa) {
      QName name = (QName)port.getProperty("com.sun.xml.internal.ws.processor.model.WSDLPortTypeName");
      wsa.param("name", name.getLocalPart());
      wsa.param("targetNamespace", name.getNamespaceURI());
   }

   public void visit(Model model) throws Exception {
      Iterator var2 = model.getServices().iterator();

      while(var2.hasNext()) {
         Service s = (Service)var2.next();
         s.accept(this);
      }

   }

   public void visit(Service service) throws Exception {
      String jd = this.model.getJavaDoc();
      if (jd != null) {
         JPackage pkg = this.cm._package(this.options.defaultPackage);
         pkg.javadoc().add(jd);
      }

      Iterator var5 = service.getPorts().iterator();

      while(var5.hasNext()) {
         Port p = (Port)var5.next();
         this.visitPort(service, p);
      }

   }

   private void visitPort(Service service, Port port) {
      if (!port.isProvider()) {
         this.write(port);
      }
   }

   private void register(TJavaGeneratorExtension h) {
      this.extensionHandlers.add(h);
   }
}
