package com.sun.tools.internal.ws.processor.generator;

import com.sun.codemodel.internal.ClassType;
import com.sun.codemodel.internal.JAnnotationUse;
import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JCatchBlock;
import com.sun.codemodel.internal.JClass;
import com.sun.codemodel.internal.JClassAlreadyExistsException;
import com.sun.codemodel.internal.JCommentPart;
import com.sun.codemodel.internal.JConditional;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JDocComment;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JInvocation;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JTryBlock;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.ws.processor.model.Model;
import com.sun.tools.internal.ws.processor.model.Port;
import com.sun.tools.internal.ws.processor.model.Service;
import com.sun.tools.internal.ws.processor.model.java.JavaInterface;
import com.sun.tools.internal.ws.resources.GeneratorMessages;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.Options;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wsdl.document.PortType;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.Locator;

public class ServiceGenerator extends GeneratorBase {
   public static void generate(Model model, WsimportOptions options, ErrorReceiver receiver) {
      ServiceGenerator serviceGenerator = new ServiceGenerator(model, options, receiver);
      serviceGenerator.doGeneration();
   }

   private ServiceGenerator(Model model, WsimportOptions options, ErrorReceiver receiver) {
      this.init(model, options, receiver);
   }

   public void visit(Service service) {
      JavaInterface intf = service.getJavaInterface();
      String className = Names.customJavaTypeClassName(intf);
      if (this.donotOverride && GeneratorUtil.classExists(this.options, className)) {
         this.log("Class " + className + " exists. Not overriding.");
      } else {
         JDefinedClass cls;
         try {
            cls = this.getClass(className, ClassType.CLASS);
         } catch (JClassAlreadyExistsException var24) {
            this.receiver.error(service.getLocator(), GeneratorMessages.GENERATOR_SERVICE_CLASS_ALREADY_EXIST(className, service.getName()));
            return;
         }

         cls._extends(javax.xml.ws.Service.class);
         String serviceFieldName = BindingHelper.mangleNameToClassName(service.getName().getLocalPart()).toUpperCase(Locale.ENGLISH);
         String wsdlLocationName = serviceFieldName + "_WSDL_LOCATION";
         JFieldVar urlField = cls.field(28, (Class)URL.class, wsdlLocationName);
         JFieldVar exField = cls.field(28, (Class)WebServiceException.class, serviceFieldName + "_EXCEPTION");
         String serviceName = serviceFieldName + "_QNAME";
         cls.field(28, (Class)QName.class, serviceName, JExpr._new(this.cm.ref(QName.class)).arg(service.getName().getNamespaceURI()).arg(service.getName().getLocalPart()));
         JClass qNameCls = this.cm.ref(QName.class);
         JInvocation inv = JExpr._new(qNameCls);
         inv.arg("namespace");
         inv.arg("localpart");
         if (this.options.useBaseResourceAndURLToLoadWSDL) {
            this.writeClassLoaderBaseResourceWSDLLocation(className, cls, urlField, exField);
         } else if (!this.wsdlLocation.startsWith("http://") && !this.wsdlLocation.startsWith("https://") && !this.wsdlLocation.startsWith("file:/")) {
            if (this.wsdlLocation.startsWith("META-INF/")) {
               this.writeClassLoaderResourceWSDLLocation(className, cls, urlField, exField);
            } else {
               this.writeResourceWSDLLocation(className, cls, urlField, exField);
            }
         } else {
            this.writeAbsWSDLLocation(cls, urlField, exField);
         }

         JDocComment comment = cls.javadoc();
         if (service.getJavaDoc() != null) {
            comment.add(service.getJavaDoc());
            comment.add("\n\n");
         }

         Iterator var13 = this.getJAXWSClassComment().iterator();

         String constructor1Str;
         while(var13.hasNext()) {
            constructor1Str = (String)var13.next();
            comment.add(constructor1Str);
         }

         JMethod constructor1 = cls.constructor(1);
         constructor1Str = String.format("super(__getWsdlLocation(), %s);", serviceName);
         constructor1.body().directStatement(constructor1Str);
         JMethod constructor5;
         String constructor4Str;
         if (this.options.target.isLaterThan(Options.Target.V2_2)) {
            constructor5 = cls.constructor(1);
            constructor5.varParam(WebServiceFeature.class, "features");
            constructor4Str = String.format("super(__getWsdlLocation(), %s, features);", serviceName);
            constructor5.body().directStatement(constructor4Str);
         }

         if (this.options.target.isLaterThan(Options.Target.V2_2)) {
            constructor5 = cls.constructor(1);
            constructor5.param(URL.class, "wsdlLocation");
            constructor4Str = String.format("super(wsdlLocation, %s);", serviceName);
            constructor5.body().directStatement(constructor4Str);
         }

         if (this.options.target.isLaterThan(Options.Target.V2_2)) {
            constructor5 = cls.constructor(1);
            constructor5.param(URL.class, "wsdlLocation");
            constructor5.varParam(WebServiceFeature.class, "features");
            constructor4Str = String.format("super(wsdlLocation, %s, features);", serviceName);
            constructor5.body().directStatement(constructor4Str);
         }

         constructor5 = cls.constructor(1);
         constructor5.param(URL.class, "wsdlLocation");
         constructor5.param(QName.class, "serviceName");
         constructor5.body().directStatement("super(wsdlLocation, serviceName);");
         if (this.options.target.isLaterThan(Options.Target.V2_2)) {
            JMethod constructor6 = cls.constructor(1);
            constructor6.param(URL.class, "wsdlLocation");
            constructor6.param(QName.class, "serviceName");
            constructor6.varParam(WebServiceFeature.class, "features");
            constructor6.body().directStatement("super(wsdlLocation, serviceName, features);");
         }

         JAnnotationUse webServiceClientAnn = cls.annotate(this.cm.ref(WebServiceClient.class));
         this.writeWebServiceClientAnnotation(service, webServiceClientAnn);
         Iterator var17 = ServiceFinder.find(GeneratorExtension.class).iterator();

         while(var17.hasNext()) {
            GeneratorExtension f = (GeneratorExtension)var17.next();
            f.writeWebServiceClientAnnotation(this.options, this.cm, cls);
         }

         this.writeHandlerConfig(Names.customJavaTypeClassName(service.getJavaInterface()), cls, this.options);
         var17 = service.getPorts().iterator();

         while(var17.hasNext()) {
            Port port = (Port)var17.next();
            if (!port.isProvider()) {
               JDefinedClass retType;
               try {
                  retType = this.getClass(port.getJavaInterface().getName(), ClassType.INTERFACE);
               } catch (JClassAlreadyExistsException var25) {
                  QName portTypeName = (QName)port.getProperty("com.sun.xml.internal.ws.processor.model.WSDLPortTypeName");
                  Locator loc = null;
                  if (portTypeName != null) {
                     PortType pt = (PortType)port.portTypes.get(portTypeName);
                     if (pt != null) {
                        loc = pt.getLocator();
                     }
                  }

                  this.receiver.error(loc, GeneratorMessages.GENERATOR_SEI_CLASS_ALREADY_EXIST(port.getJavaInterface().getName(), portTypeName));
                  return;
               }

               this.writeDefaultGetPort(port, retType, cls);
               if (this.options.target.isLaterThan(Options.Target.V2_1)) {
                  this.writeGetPort(port, retType, cls);
               }
            }
         }

         this.writeGetWsdlLocation(this.cm.ref(URL.class), cls, urlField, exField);
      }
   }

   private void writeGetPort(Port port, JType retType, JDefinedClass cls) {
      JMethod m = cls.method(1, (JType)retType, port.getPortGetter());
      JDocComment methodDoc = m.javadoc();
      if (port.getJavaDoc() != null) {
         methodDoc.add(port.getJavaDoc());
      }

      JCommentPart ret = methodDoc.addReturn();
      JCommentPart paramDoc = methodDoc.addParam("features");
      paramDoc.append("A list of ");
      paramDoc.append("{@link " + WebServiceFeature.class.getName() + "}");
      paramDoc.append("to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.");
      ret.add("returns " + retType.name());
      m.varParam(WebServiceFeature.class, "features");
      JBlock body = m.body();
      StringBuilder statement = new StringBuilder("return ");
      statement.append("super.getPort(new QName(\"").append(port.getName().getNamespaceURI()).append("\", \"").append(port.getName().getLocalPart()).append("\"), ");
      statement.append(retType.name());
      statement.append(".class, features);");
      body.directStatement(statement.toString());
      this.writeWebEndpoint(port, m);
   }

   private void writeAbsWSDLLocation(JDefinedClass cls, JFieldVar urlField, JFieldVar exField) {
      JBlock staticBlock = cls.init();
      JVar urlVar = staticBlock.decl(this.cm.ref(URL.class), "url", JExpr._null());
      JVar exVar = staticBlock.decl(this.cm.ref(WebServiceException.class), "e", JExpr._null());
      JTryBlock tryBlock = staticBlock._try();
      tryBlock.body().assign(urlVar, JExpr._new(this.cm.ref(URL.class)).arg(this.wsdlLocation));
      JCatchBlock catchBlock = tryBlock._catch(this.cm.ref(MalformedURLException.class));
      catchBlock.param("ex");
      catchBlock.body().assign(exVar, JExpr._new(this.cm.ref(WebServiceException.class)).arg((JExpression)JExpr.ref("ex")));
      staticBlock.assign(urlField, urlVar);
      staticBlock.assign(exField, exVar);
   }

   private void writeResourceWSDLLocation(String className, JDefinedClass cls, JFieldVar urlField, JFieldVar exField) {
      JBlock staticBlock = cls.init();
      staticBlock.assign(urlField, JExpr.dotclass(this.cm.ref(className)).invoke("getResource").arg(this.wsdlLocation));
      JVar exVar = staticBlock.decl(this.cm.ref(WebServiceException.class), "e", JExpr._null());
      JConditional ifBlock = staticBlock._if(urlField.eq(JExpr._null()));
      ifBlock._then().assign(exVar, JExpr._new(this.cm.ref(WebServiceException.class)).arg("Cannot find " + JExpr.quotify('\'', this.wsdlLocation) + " wsdl. Place the resource correctly in the classpath."));
      staticBlock.assign(exField, exVar);
   }

   private void writeClassLoaderResourceWSDLLocation(String className, JDefinedClass cls, JFieldVar urlField, JFieldVar exField) {
      JBlock staticBlock = cls.init();
      staticBlock.assign(urlField, JExpr.dotclass(this.cm.ref(className)).invoke("getClassLoader").invoke("getResource").arg(this.wsdlLocation));
      JVar exVar = staticBlock.decl(this.cm.ref(WebServiceException.class), "e", JExpr._null());
      JConditional ifBlock = staticBlock._if(urlField.eq(JExpr._null()));
      ifBlock._then().assign(exVar, JExpr._new(this.cm.ref(WebServiceException.class)).arg("Cannot find " + JExpr.quotify('\'', this.wsdlLocation) + " wsdl. Place the resource correctly in the classpath."));
      staticBlock.assign(exField, exVar);
   }

   private void writeClassLoaderBaseResourceWSDLLocation(String className, JDefinedClass cls, JFieldVar urlField, JFieldVar exField) {
      JBlock staticBlock = cls.init();
      JVar exVar = staticBlock.decl(this.cm.ref(WebServiceException.class), "e", JExpr._null());
      JVar urlVar = staticBlock.decl(this.cm.ref(URL.class), "url", JExpr._null());
      JTryBlock tryBlock = staticBlock._try();
      tryBlock.body().assign(urlVar, JExpr._new(this.cm.ref(URL.class)).arg((JExpression)JExpr.dotclass(this.cm.ref(className)).invoke("getResource").arg(".")).arg(this.wsdlLocation));
      JCatchBlock catchBlock = tryBlock._catch(this.cm.ref(MalformedURLException.class));
      JVar murlVar = catchBlock.param("murl");
      catchBlock.body().assign(exVar, JExpr._new(this.cm.ref(WebServiceException.class)).arg((JExpression)murlVar));
      staticBlock.assign(urlField, urlVar);
      staticBlock.assign(exField, exVar);
   }

   private void writeGetWsdlLocation(JType retType, JDefinedClass cls, JFieldVar urlField, JFieldVar exField) {
      JMethod m = cls.method(20, (JType)retType, "__getWsdlLocation");
      JConditional ifBlock = m.body()._if(exField.ne(JExpr._null()));
      ifBlock._then()._throw(exField);
      m.body()._return(urlField);
   }

   private void writeDefaultGetPort(Port port, JType retType, JDefinedClass cls) {
      String portGetter = port.getPortGetter();
      JMethod m = cls.method(1, (JType)retType, portGetter);
      JDocComment methodDoc = m.javadoc();
      if (port.getJavaDoc() != null) {
         methodDoc.add(port.getJavaDoc());
      }

      JCommentPart ret = methodDoc.addReturn();
      ret.add("returns " + retType.name());
      JBlock body = m.body();
      StringBuilder statement = new StringBuilder("return ");
      statement.append("super.getPort(new QName(\"").append(port.getName().getNamespaceURI()).append("\", \"").append(port.getName().getLocalPart()).append("\"), ");
      statement.append(retType.name());
      statement.append(".class);");
      body.directStatement(statement.toString());
      this.writeWebEndpoint(port, m);
   }

   private void writeWebServiceClientAnnotation(Service service, JAnnotationUse wsa) {
      String serviceName = service.getName().getLocalPart();
      String serviceNS = service.getName().getNamespaceURI();
      wsa.param("name", serviceName);
      wsa.param("targetNamespace", serviceNS);
      wsa.param("wsdlLocation", this.wsdlLocation);
   }

   private void writeWebEndpoint(Port port, JMethod m) {
      JAnnotationUse webEndpointAnn = m.annotate(this.cm.ref(WebEndpoint.class));
      webEndpointAnn.param("name", port.getName().getLocalPart());
   }
}
