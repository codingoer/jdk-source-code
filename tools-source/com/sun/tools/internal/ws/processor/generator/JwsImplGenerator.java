package com.sun.tools.internal.ws.processor.generator;

import com.sun.codemodel.internal.ClassType;
import com.sun.codemodel.internal.JAnnotationUse;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JClassAlreadyExistsException;
import com.sun.codemodel.internal.JCommentPart;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JDocComment;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.processor.model.Fault;
import com.sun.tools.internal.ws.processor.model.Model;
import com.sun.tools.internal.ws.processor.model.Operation;
import com.sun.tools.internal.ws.processor.model.Port;
import com.sun.tools.internal.ws.processor.model.Service;
import com.sun.tools.internal.ws.processor.model.java.JavaInterface;
import com.sun.tools.internal.ws.processor.model.java.JavaMethod;
import com.sun.tools.internal.ws.processor.model.java.JavaParameter;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBTypeAndAnnotation;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wsdl.document.Binding;
import com.sun.tools.internal.ws.wsdl.document.Definitions;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAP12Binding;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import org.xml.sax.Locator;

public final class JwsImplGenerator extends GeneratorBase {
   private static final Map TRANSLATION_MAP = new HashMap(1);
   private final List implFiles = new ArrayList();

   public static List generate(Model model, WsimportOptions options, ErrorReceiver receiver) {
      if (options.implDestDir == null) {
         return null;
      } else {
         JwsImplGenerator jwsImplGenerator = new JwsImplGenerator();
         jwsImplGenerator.init(model, options, receiver);
         jwsImplGenerator.doGeneration();
         if (jwsImplGenerator.implFiles.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            if (options.implServiceName != null) {
               msg.append("serviceName=[").append(options.implServiceName).append("] ");
            }

            if (options.implPortName != null) {
               msg.append("portName=[").append(options.implPortName).append("] ");
            }

            if (msg.length() > 0) {
               msg.append(", Not found in wsdl file.\n");
            }

            msg.append("No impl files generated!");
            receiver.warning((Locator)null, msg.toString());
         }

         return jwsImplGenerator.implFiles;
      }
   }

   public static boolean moveToImplDestDir(List gImplFiles, WsimportOptions options, ErrorReceiver receiver) {
      if (options.implDestDir != null && gImplFiles != null && !gImplFiles.isEmpty()) {
         List generatedImplFiles = JwsImplGenerator.ImplFile.toImplFiles(gImplFiles);

         try {
            File implDestDir = makePackageDir(options);
            Iterator var7 = generatedImplFiles.iterator();

            while(var7.hasNext()) {
               ImplFile implF = (ImplFile)var7.next();
               File movedF = findFile(options, implF.qualifiedName);
               if (movedF == null) {
                  receiver.warning((Locator)null, "Class " + implF.qualifiedName + " is not generated. Not moving.");
                  return false;
               }

               File f = new File(implDestDir, implF.name);
               if (!movedF.equals(f)) {
                  if (f.exists() && !f.delete()) {
                     receiver.error((String)("Class " + implF.qualifiedName + " has existed in destImplDir, and it can not be written!"), (Exception)null);
                  }

                  if (!movedF.renameTo(f)) {
                     throw new Exception();
                  }
               }
            }

            return true;
         } catch (Exception var9) {
            receiver.error("Moving WebService Impl files failed!", var9);
            return false;
         }
      } else {
         return true;
      }
   }

   private JwsImplGenerator() {
      this.donotOverride = true;
   }

   public void visit(Service service) {
      QName serviceName = service.getName();
      if (this.options.implServiceName == null || equalsNSOptional(this.options.implServiceName, serviceName)) {
         Iterator var3 = service.getPorts().iterator();

         while(true) {
            Port port;
            QName portName;
            do {
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  port = (Port)var3.next();
               } while(port.isProvider());

               portName = port.getName();
            } while(this.options.implPortName != null && !equalsNSOptional(this.options.implPortName, portName));

            String simpleClassName = serviceName.getLocalPart() + "_" + portName.getLocalPart() + "Impl";
            String className = this.makePackageQualified(simpleClassName);
            this.implFiles.add(className);
            if (this.donotOverride && GeneratorUtil.classExists(this.options, className)) {
               this.log("Class " + className + " exists. Not overriding.");
               return;
            }

            JDefinedClass cls = null;

            try {
               cls = this.getClass(className, ClassType.CLASS);
            } catch (JClassAlreadyExistsException var27) {
               this.log("Class " + className + " generates failed. JClassAlreadyExistsException[" + className + "].");
               return;
            }

            JavaInterface portIntf = port.getJavaInterface();
            String portClassName = Names.customJavaTypeClassName(portIntf);
            JDefinedClass portCls = null;

            try {
               portCls = this.getClass(portClassName, ClassType.INTERFACE);
            } catch (JClassAlreadyExistsException var26) {
               this.log("Class " + className + " generates failed. JClassAlreadyExistsException[" + portClassName + "].");
               return;
            }

            cls._implements((JClass)portCls);
            cls.constructor(1);
            JDocComment comment = cls.javadoc();
            if (service.getJavaDoc() != null) {
               comment.add(service.getJavaDoc());
               comment.add("\n\n");
            }

            Iterator var13 = this.getJAXWSClassComment().iterator();

            while(var13.hasNext()) {
               String doc = (String)var13.next();
               comment.add(doc);
            }

            JAnnotationUse webServiceAnn = cls.annotate(this.cm.ref(WebService.class));
            this.writeWebServiceAnnotation(service, port, webServiceAnn);
            JAnnotationUse bindingTypeAnn = cls.annotate(this.cm.ref(BindingType.class));
            this.writeBindingTypeAnnotation(port, bindingTypeAnn);
            Iterator var15 = ServiceFinder.find(GeneratorExtension.class).iterator();

            while(var15.hasNext()) {
               GeneratorExtension f = (GeneratorExtension)var15.next();
               f.writeWebServiceAnnotation(this.model, this.cm, cls, port);
            }

            var15 = port.getOperations().iterator();

            while(var15.hasNext()) {
               Operation operation = (Operation)var15.next();
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

               JClass holder = this.cm.ref(Holder.class);

               JVar var;
               for(Iterator var32 = method.getParametersList().iterator(); var32.hasNext(); methodDoc.addParam(var)) {
                  JavaParameter parameter = (JavaParameter)var32.next();
                  JAXBTypeAndAnnotation paramType = parameter.getType().getType();
                  if (parameter.isHolder()) {
                     var = m.param((JType)holder.narrow(paramType.getType().boxify()), parameter.getName());
                  } else {
                     var = m.param(paramType.getType(), parameter.getName());
                  }
               }

               com.sun.tools.internal.ws.wsdl.document.Operation wsdlOp = operation.getWSDLPortTypeOperation();
               Iterator var34 = operation.getFaultsSet().iterator();

               while(var34.hasNext()) {
                  Fault fault = (Fault)var34.next();
                  m._throws(fault.getExceptionClass());
                  methodDoc.addThrows(fault.getExceptionClass());
                  wsdlOp.putFault(fault.getWsdlFaultName(), fault.getExceptionClass());
               }

               m.body().block().directStatement("//replace with your impl here");
               m.body().block().directStatement(this.getReturnString(method.getReturnType().getName()));
            }
         }
      }
   }

   private String getReturnString(String type) {
      String nullReturnStr = "return null;";
      if (type.indexOf(46) <= -1 && type.indexOf(91) <= -1) {
         if (type.equals("void")) {
            return "return;";
         } else if (type.equals("boolean")) {
            return "return false;";
         } else if (!type.equals("int") && !type.equals("byte") && !type.equals("short") && !type.equals("long") && !type.equals("double") && !type.equals("float")) {
            return type.equals("char") ? "return '0';" : "return null;";
         } else {
            return "return 0;";
         }
      } else {
         return "return null;";
      }
   }

   private void writeWebServiceAnnotation(Service service, Port port, JAnnotationUse webServiceAnn) {
      webServiceAnn.param("portName", port.getName().getLocalPart());
      webServiceAnn.param("serviceName", service.getName().getLocalPart());
      webServiceAnn.param("targetNamespace", service.getName().getNamespaceURI());
      webServiceAnn.param("wsdlLocation", this.wsdlLocation);
      webServiceAnn.param("endpointInterface", port.getJavaInterface().getName());
   }

   private String transToValidJavaIdentifier(String s) {
      if (s == null) {
         return null;
      } else {
         int len = s.length();
         StringBuilder retSB = new StringBuilder();
         if (len != 0 && Character.isJavaIdentifierStart(s.charAt(0))) {
            retSB.append(s.charAt(0));
         } else {
            retSB.append("J");
         }

         for(int i = 1; i < len; ++i) {
            if (Character.isJavaIdentifierPart(s.charAt(i))) {
               retSB.append(s.charAt(i));
            }
         }

         return retSB.toString();
      }
   }

   private String makePackageQualified(String s) {
      s = this.transToValidJavaIdentifier(s);
      return this.options.defaultPackage != null && !this.options.defaultPackage.equals("") ? this.options.defaultPackage + "." + s : s;
   }

   private void writeBindingTypeAnnotation(Port port, JAnnotationUse bindingTypeAnn) {
      QName bName = (QName)port.getProperty("com.sun.xml.internal.ws.processor.model.WSDLBindingName");
      if (bName != null) {
         String v = this.getBindingType(bName);
         if (v != null) {
            bindingTypeAnn.param("value", v);
         }

      }
   }

   private String resolveBindingValue(TWSDLExtension wsdlext) {
      Iterator var3;
      GeneratorExtension f;
      String bindingValue;
      if (wsdlext.getClass().equals(SOAPBinding.class)) {
         SOAPBinding sb = (SOAPBinding)wsdlext;
         if ("http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true".equals(sb.getTransport())) {
            return "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true";
         } else {
            var3 = ServiceFinder.find(GeneratorExtension.class).iterator();

            do {
               if (!var3.hasNext()) {
                  return "http://schemas.xmlsoap.org/wsdl/soap/http";
               }

               f = (GeneratorExtension)var3.next();
               bindingValue = f.getBindingValue(sb.getTransport(), SOAPVersion.SOAP_11);
            } while(bindingValue == null);

            return bindingValue;
         }
      } else if (wsdlext.getClass().equals(SOAP12Binding.class)) {
         SOAP12Binding sb = (SOAP12Binding)wsdlext;
         if ("http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true".equals(sb.getTransport())) {
            return "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true";
         } else {
            var3 = ServiceFinder.find(GeneratorExtension.class).iterator();

            do {
               if (!var3.hasNext()) {
                  return "http://www.w3.org/2003/05/soap/bindings/HTTP/";
               }

               f = (GeneratorExtension)var3.next();
               bindingValue = f.getBindingValue(sb.getTransport(), SOAPVersion.SOAP_12);
            } while(bindingValue == null);

            return bindingValue;
         }
      } else {
         return null;
      }
   }

   private String getBindingType(QName bName) {
      String value = null;
      Definitions definitions;
      if (this.model.getEntity() instanceof Definitions) {
         definitions = (Definitions)this.model.getEntity();
         if (definitions != null) {
            Iterator bindings = definitions.bindings();
            if (bindings != null) {
               label47:
               while(bindings.hasNext()) {
                  Binding binding = (Binding)bindings.next();
                  if (bName.getLocalPart().equals(binding.getName()) && bName.getNamespaceURI().equals(binding.getNamespaceURI())) {
                     List bindextends = (List)binding.extensions();
                     Iterator var7 = bindextends.iterator();

                     while(true) {
                        if (!var7.hasNext()) {
                           break label47;
                        }

                        TWSDLExtension wsdlext = (TWSDLExtension)var7.next();
                        value = this.resolveBindingValue(wsdlext);
                        if (value != null) {
                           break label47;
                        }
                     }
                  }
               }
            }
         }
      }

      if (value == null && this.model.getEntity() instanceof Definitions) {
         definitions = (Definitions)this.model.getEntity();
         Binding b = (Binding)definitions.resolveBindings().get(bName);
         if (b != null) {
            List bindextends = (List)b.extensions();
            Iterator var11 = bindextends.iterator();

            while(var11.hasNext()) {
               TWSDLExtension wsdlext = (TWSDLExtension)var11.next();
               value = this.resolveBindingValue(wsdlext);
               if (value != null) {
                  break;
               }
            }
         }
      }

      return value;
   }

   private static File makePackageDir(WsimportOptions options) {
      File ret = null;
      if (options.defaultPackage != null && !options.defaultPackage.equals("")) {
         String subDir = options.defaultPackage.replace('.', '/');
         ret = new File(options.implDestDir, subDir);
      } else {
         ret = options.implDestDir;
      }

      boolean created = ret.mkdirs();
      if (options.verbose && !created) {
         System.out.println(MessageFormat.format("Directory not created: {0}", ret));
      }

      return ret;
   }

   private static String getQualifiedFileName(String canonicalBaseDir, File f) throws IOException {
      String fp = f.getCanonicalPath();
      if (fp == null) {
         return null;
      } else {
         fp = fp.replace(canonicalBaseDir, "");
         fp = fp.replace('\\', '.');
         fp = fp.replace('/', '.');
         if (fp.startsWith(".")) {
            fp = fp.substring(1);
         }

         return fp;
      }
   }

   private static File findFile(WsimportOptions options, String qualifiedFileName) throws IOException {
      String baseDir = options.sourceDir.getCanonicalPath();
      String fp = null;
      Iterator var4 = options.getGeneratedFiles().iterator();

      File f;
      do {
         if (!var4.hasNext()) {
            return null;
         }

         f = (File)var4.next();
         fp = getQualifiedFileName(baseDir, f);
      } while(!qualifiedFileName.equals(fp));

      return f;
   }

   private static boolean equalsNSOptional(String strQName, QName checkQN) {
      if (strQName == null) {
         return false;
      } else {
         strQName = strQName.trim();
         QName reqQN = QName.valueOf(strQName);
         return reqQN.getNamespaceURI() != null && !reqQN.getNamespaceURI().equals("") ? reqQN.equals(checkQN) : reqQN.getLocalPart().equals(checkQN.getLocalPart());
      }
   }

   static {
      TRANSLATION_MAP.put("http://schemas.xmlsoap.org/soap/http", "http://schemas.xmlsoap.org/wsdl/soap/http");
   }

   static final class ImplFile {
      public String qualifiedName;
      public String name;

      private ImplFile(String qualifiedClassName) {
         this.qualifiedName = qualifiedClassName + ".java";
         String simpleClassName = qualifiedClassName;
         int i = qualifiedClassName.lastIndexOf(".");
         if (i != -1) {
            simpleClassName = qualifiedClassName.substring(i + 1);
         }

         this.name = simpleClassName + ".java";
      }

      public static List toImplFiles(List qualifiedClassNames) {
         List ret = new ArrayList();
         Iterator var2 = qualifiedClassNames.iterator();

         while(var2.hasNext()) {
            String qualifiedClassName = (String)var2.next();
            ret.add(new ImplFile(qualifiedClassName));
         }

         return ret;
      }
   }
}
