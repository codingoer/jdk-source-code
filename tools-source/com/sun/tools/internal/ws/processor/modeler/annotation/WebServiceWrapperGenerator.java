package com.sun.tools.internal.ws.processor.modeler.annotation;

import com.sun.codemodel.internal.ClassType;
import com.sun.codemodel.internal.CodeWriter;
import com.sun.codemodel.internal.JAnnotationArrayMember;
import com.sun.codemodel.internal.JAnnotationUse;
import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JClassAlreadyExistsException;
import com.sun.codemodel.internal.JCodeModel;
import com.sun.codemodel.internal.JCommentPart;
import com.sun.codemodel.internal.JDefinedClass;
import com.sun.codemodel.internal.JDocComment;
import com.sun.codemodel.internal.JExpr;
import com.sun.codemodel.internal.JFieldVar;
import com.sun.codemodel.internal.JMethod;
import com.sun.codemodel.internal.JType;
import com.sun.codemodel.internal.JVar;
import com.sun.codemodel.internal.writer.ProgressCodeWriter;
import com.sun.tools.internal.jxc.ap.InlineAnnotationReaderImpl;
import com.sun.tools.internal.jxc.model.nav.ApNavigator;
import com.sun.tools.internal.ws.ToolVersion;
import com.sun.tools.internal.ws.processor.generator.GeneratorBase;
import com.sun.tools.internal.ws.processor.generator.GeneratorConstants;
import com.sun.tools.internal.ws.processor.generator.Names;
import com.sun.tools.internal.ws.processor.modeler.ModelerException;
import com.sun.tools.internal.ws.processor.util.DirectoryUtil;
import com.sun.tools.internal.ws.resources.WebserviceapMessages;
import com.sun.tools.internal.ws.util.ClassNameInfo;
import com.sun.tools.internal.ws.wscompile.FilerCodeWriter;
import com.sun.tools.internal.ws.wscompile.WsgenOptions;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPStyle;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.ws.model.AbstractWrapperBeanGenerator;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import com.sun.xml.internal.ws.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttachmentRef;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebFault;
import javax.xml.ws.WebServiceException;

public class WebServiceWrapperGenerator extends WebServiceVisitor {
   private Set wrapperNames;
   private Set processedExceptions;
   private JCodeModel cm;
   private final MakeSafeTypeVisitor makeSafeVisitor;
   private static final FieldFactory FIELD_FACTORY = new FieldFactory();
   private final AbstractWrapperBeanGenerator ap_generator;

   public WebServiceWrapperGenerator(ModelBuilder builder, AnnotationProcessorContext context) {
      super(builder, context);
      this.ap_generator = new ApWrapperBeanGenerator(InlineAnnotationReaderImpl.theInstance, new ApNavigator(this.builder.getProcessingEnvironment()), FIELD_FACTORY);
      this.makeSafeVisitor = new MakeSafeTypeVisitor(builder.getProcessingEnvironment());
   }

   protected void processWebService(WebService webService, TypeElement d) {
      this.cm = new JCodeModel();
      this.wrapperNames = new HashSet();
      this.processedExceptions = new HashSet();
   }

   protected void postProcessWebService(WebService webService, TypeElement d) {
      super.postProcessWebService(webService, d);
      this.doPostProcessWebService(webService, d);
   }

   protected void doPostProcessWebService(WebService webService, TypeElement d) {
      if (this.cm != null) {
         File sourceDir = this.builder.getSourceDir();

         assert sourceDir != null;

         WsgenOptions options = this.builder.getOptions();

         try {
            CodeWriter cw = new FilerCodeWriter(sourceDir, options);
            if (options.verbose) {
               cw = new ProgressCodeWriter((CodeWriter)cw, System.out);
            }

            this.cm.build((CodeWriter)cw);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

   }

   protected void processMethod(ExecutableElement method, WebMethod webMethod) {
      this.builder.log("WrapperGen - method: " + method);
      this.builder.log("method.getDeclaringType(): " + method.asType());
      if (this.wrapped && this.soapStyle.equals(SOAPStyle.DOCUMENT)) {
         this.generateWrappers(method, webMethod);
      }

      this.generateExceptionBeans(method);
   }

   private boolean generateExceptionBeans(ExecutableElement method) {
      String beanPackage = this.packageName + WebServiceConstants.PD_JAXWS_PACKAGE_PD.getValue();
      if (this.packageName.length() == 0) {
         beanPackage = WebServiceConstants.JAXWS_PACKAGE_PD.getValue();
      }

      boolean beanGenerated = false;

      boolean tmp;
      for(Iterator var4 = method.getThrownTypes().iterator(); var4.hasNext(); beanGenerated = beanGenerated || tmp) {
         TypeMirror thrownType = (TypeMirror)var4.next();
         TypeElement typeDecl = (TypeElement)((DeclaredType)thrownType).asElement();
         if (typeDecl == null) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_COULD_NOT_FIND_TYPEDECL(thrownType.toString(), this.context.getRound()));
            return false;
         }

         tmp = this.generateExceptionBean(typeDecl, beanPackage);
      }

      return beanGenerated;
   }

   private boolean duplicateName(String name) {
      Iterator var2 = this.wrapperNames.iterator();

      String str;
      do {
         if (!var2.hasNext()) {
            this.wrapperNames.add(name);
            return false;
         }

         str = (String)var2.next();
      } while(!str.equalsIgnoreCase(name));

      return true;
   }

   private boolean generateWrappers(ExecutableElement method, WebMethod webMethod) {
      boolean isOneway = method.getAnnotation(Oneway.class) != null;
      String beanPackage = this.packageName + WebServiceConstants.PD_JAXWS_PACKAGE_PD.getValue();
      if (this.packageName.length() == 0) {
         beanPackage = WebServiceConstants.JAXWS_PACKAGE_PD.getValue();
      }

      Name methodName = method.getSimpleName();
      String operationName = this.builder.getOperationName(methodName);
      operationName = webMethod != null && webMethod.operationName().length() > 0 ? webMethod.operationName() : operationName;
      String reqName = operationName;
      String resName = operationName + WebServiceConstants.RESPONSE.getValue();
      String reqNamespace = this.typeNamespace;
      String resNamespace = this.typeNamespace;
      String requestClassName = beanPackage + StringUtils.capitalize(method.getSimpleName().toString());
      RequestWrapper reqWrapper = (RequestWrapper)method.getAnnotation(RequestWrapper.class);
      if (reqWrapper != null) {
         if (reqWrapper.className().length() > 0) {
            requestClassName = reqWrapper.className();
         }

         if (reqWrapper.localName().length() > 0) {
            reqName = reqWrapper.localName();
         }

         if (reqWrapper.targetNamespace().length() > 0) {
            reqNamespace = reqWrapper.targetNamespace();
         }
      }

      this.builder.log("requestWrapper: " + requestClassName);
      File file = new File(DirectoryUtil.getOutputDirectoryFor(requestClassName, this.builder.getSourceDir()), Names.stripQualifier(requestClassName) + GeneratorConstants.JAVA_SRC_SUFFIX.getValue());
      this.builder.getOptions().addGeneratedFile(file);
      boolean canOverwriteRequest = this.builder.canOverWriteClass(requestClassName);
      if (!canOverwriteRequest) {
         this.builder.log("Class " + requestClassName + " exists. Not overwriting.");
      }

      if (this.duplicateName(requestClassName) && canOverwriteRequest) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_METHOD_REQUEST_WRAPPER_BEAN_NAME_NOT_UNIQUE(this.typeElement.getQualifiedName(), method.toString()));
      }

      String responseClassName = null;
      boolean canOverwriteResponse = canOverwriteRequest;
      if (!isOneway) {
         responseClassName = beanPackage + StringUtils.capitalize(method.getSimpleName().toString()) + WebServiceConstants.RESPONSE.getValue();
         ResponseWrapper resWrapper = (ResponseWrapper)method.getAnnotation(ResponseWrapper.class);
         if (resWrapper != null) {
            if (resWrapper.className().length() > 0) {
               responseClassName = resWrapper.className();
            }

            if (resWrapper.localName().length() > 0) {
               resName = resWrapper.localName();
            }

            if (resWrapper.targetNamespace().length() > 0) {
               resNamespace = resWrapper.targetNamespace();
            }
         }

         canOverwriteResponse = this.builder.canOverWriteClass(responseClassName);
         if (!canOverwriteResponse) {
            this.builder.log("Class " + responseClassName + " exists. Not overwriting.");
         }

         if (this.duplicateName(responseClassName) && canOverwriteResponse) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_METHOD_RESPONSE_WRAPPER_BEAN_NAME_NOT_UNIQUE(this.typeElement.getQualifiedName(), method.toString()));
         }

         file = new File(DirectoryUtil.getOutputDirectoryFor(responseClassName, this.builder.getSourceDir()), Names.stripQualifier(responseClassName) + GeneratorConstants.JAVA_SRC_SUFFIX.getValue());
         this.builder.getOptions().addGeneratedFile(file);
      }

      WrapperInfo reqWrapperInfo = new WrapperInfo(requestClassName);
      WrapperInfo resWrapperInfo = null;
      if (!isOneway) {
         resWrapperInfo = new WrapperInfo(responseClassName);
      }

      this.seiContext.setReqWrapperOperation(method, reqWrapperInfo);
      if (!isOneway) {
         this.seiContext.setResWrapperOperation(method, resWrapperInfo);
      }

      try {
         if (!canOverwriteRequest && !canOverwriteResponse) {
            return false;
         } else {
            JDefinedClass reqCls = null;
            if (canOverwriteRequest) {
               reqCls = this.getCMClass(requestClassName, ClassType.CLASS);
            }

            JDefinedClass resCls = null;
            if (!isOneway && canOverwriteResponse) {
               resCls = this.getCMClass(responseClassName, ClassType.CLASS);
            }

            this.writeXmlElementDeclaration(reqCls, reqName, reqNamespace);
            this.writeXmlElementDeclaration(resCls, resName, resNamespace);
            List reqMembers = this.ap_generator.collectRequestBeanMembers(method);
            List resMembers = this.ap_generator.collectResponseBeanMembers(method);
            this.writeXmlTypeDeclaration(reqCls, reqName, reqNamespace, reqMembers);
            this.writeXmlTypeDeclaration(resCls, resName, resNamespace, resMembers);
            this.writeMembers(reqCls, reqMembers);
            this.writeMembers(resCls, resMembers);
            return true;
         }
      } catch (Exception var23) {
         throw new ModelerException("modeler.nestedGeneratorError", new Object[]{var23});
      }
   }

   private TypeMirror getSafeType(TypeMirror type) {
      return (TypeMirror)this.makeSafeVisitor.visit(type, this.builder.getProcessingEnvironment().getTypeUtils());
   }

   private JType getType(TypeMirror typeMirror) {
      String type = typeMirror.toString();

      try {
         return this.cm.parseType(type);
      } catch (ClassNotFoundException var4) {
         return this.cm.ref(type);
      }
   }

   private void writeMembers(JDefinedClass cls, Collection members) {
      if (cls != null) {
         Iterator var3 = members.iterator();

         MemberInfo memInfo;
         while(var3.hasNext()) {
            memInfo = (MemberInfo)var3.next();
            JType type = this.getType(memInfo.getParamType());
            JFieldVar field = cls.field(4, (JType)type, memInfo.getParamName());
            this.annotateParameterWithJaxbAnnotations(memInfo, field);
         }

         var3 = members.iterator();

         while(var3.hasNext()) {
            memInfo = (MemberInfo)var3.next();
            this.writeMember(cls, memInfo.getParamType(), memInfo.getParamName());
         }

      }
   }

   private void annotateParameterWithJaxbAnnotations(MemberInfo memInfo, JFieldVar field) {
      List jaxbAnnotations = memInfo.getJaxbAnnotations();
      Iterator var4 = jaxbAnnotations.iterator();

      while(true) {
         while(true) {
            while(var4.hasNext()) {
               Annotation ann = (Annotation)var4.next();
               JAnnotationUse jaxbAnn;
               if (!(ann instanceof XmlMimeType)) {
                  if (ann instanceof XmlJavaTypeAdapter) {
                     jaxbAnn = field.annotate(XmlJavaTypeAdapter.class);
                     XmlJavaTypeAdapter ja = (XmlJavaTypeAdapter)ann;

                     try {
                        ja.value();
                        throw new AssertionError();
                     } catch (MirroredTypeException var9) {
                        jaxbAnn.param("value", this.getType(var9.getTypeMirror()));
                     }
                  } else if (ann instanceof XmlAttachmentRef) {
                     field.annotate(XmlAttachmentRef.class);
                  } else if (ann instanceof XmlList) {
                     field.annotate(XmlList.class);
                  } else {
                     if (!(ann instanceof XmlElement)) {
                        throw new WebServiceException("SEI Parameter cannot have this JAXB annotation: " + ann);
                     }

                     XmlElement elemAnn = (XmlElement)ann;
                     JAnnotationUse jAnn = field.annotate(XmlElement.class);
                     jAnn.param("name", elemAnn.name());
                     jAnn.param("namespace", elemAnn.namespace());
                     if (elemAnn.nillable()) {
                        jAnn.param("nillable", true);
                     }

                     if (elemAnn.required()) {
                        jAnn.param("required", true);
                     }
                  }
               } else {
                  jaxbAnn = field.annotate(XmlMimeType.class);
                  jaxbAnn.param("value", ((XmlMimeType)ann).value());
               }
            }

            return;
         }
      }
   }

   protected JDefinedClass getCMClass(String className, ClassType type) {
      JDefinedClass cls;
      try {
         cls = this.cm._class(className, type);
      } catch (JClassAlreadyExistsException var5) {
         cls = this.cm._getClass(className);
      }

      return cls;
   }

   private boolean generateExceptionBean(TypeElement thrownDecl, String beanPackage) {
      if (!this.builder.isServiceException(thrownDecl.asType())) {
         return false;
      } else {
         String exceptionName = ClassNameInfo.getName(thrownDecl.getQualifiedName().toString());
         if (this.processedExceptions.contains(exceptionName)) {
            return false;
         } else {
            this.processedExceptions.add(exceptionName);
            WebFault webFault = (WebFault)thrownDecl.getAnnotation(WebFault.class);
            String className = beanPackage + exceptionName + WebServiceConstants.BEAN.getValue();
            Collection members = this.ap_generator.collectExceptionBeanMembers(thrownDecl);
            boolean isWSDLException = this.isWSDLException(members, thrownDecl);
            String namespace = this.typeNamespace;
            String name = exceptionName;
            FaultInfo faultInfo;
            if (isWSDLException) {
               TypeMirror beanType = this.getFaultInfoMember(members).getParamType();
               faultInfo = new FaultInfo(TypeMonikerFactory.getTypeMoniker(beanType), true);
               namespace = webFault.targetNamespace().length() > 0 ? webFault.targetNamespace() : namespace;
               name = webFault.name().length() > 0 ? webFault.name() : exceptionName;
               faultInfo.setElementName(new QName(namespace, name));
               this.seiContext.addExceptionBeanEntry(thrownDecl.getQualifiedName(), faultInfo, this.builder);
               return false;
            } else {
               if (webFault != null) {
                  namespace = webFault.targetNamespace().length() > 0 ? webFault.targetNamespace() : namespace;
                  name = webFault.name().length() > 0 ? webFault.name() : exceptionName;
                  className = webFault.faultBean().length() > 0 ? webFault.faultBean() : className;
               }

               JDefinedClass cls = this.getCMClass(className, ClassType.CLASS);
               faultInfo = new FaultInfo(className, false);
               if (this.duplicateName(className)) {
                  this.builder.processError(WebserviceapMessages.WEBSERVICEAP_METHOD_EXCEPTION_BEAN_NAME_NOT_UNIQUE(this.typeElement.getQualifiedName(), thrownDecl.getQualifiedName()));
               }

               boolean canOverWriteBean = this.builder.canOverWriteClass(className);
               if (!canOverWriteBean) {
                  this.builder.log("Class " + className + " exists. Not overwriting.");
                  this.seiContext.addExceptionBeanEntry(thrownDecl.getQualifiedName(), faultInfo, this.builder);
                  return false;
               } else if (this.seiContext.getExceptionBeanName(thrownDecl.getQualifiedName()) != null) {
                  return false;
               } else {
                  JDocComment comment = cls.javadoc();
                  Iterator var14 = GeneratorBase.getJAXWSClassComment(ToolVersion.VERSION.MAJOR_VERSION).iterator();

                  String xmlTypeName;
                  while(var14.hasNext()) {
                     xmlTypeName = (String)var14.next();
                     comment.add(xmlTypeName);
                  }

                  this.writeXmlElementDeclaration(cls, name, namespace);
                  XmlType xmlType = (XmlType)thrownDecl.getAnnotation(XmlType.class);
                  xmlTypeName = xmlType != null && !xmlType.name().equals("##default") ? xmlType.name() : exceptionName;
                  String xmlTypeNamespace = xmlType != null && !xmlType.namespace().equals("##default") ? xmlType.namespace() : this.typeNamespace;
                  this.writeXmlTypeDeclaration(cls, xmlTypeName, xmlTypeNamespace, members);
                  this.writeMembers(cls, members);
                  this.seiContext.addExceptionBeanEntry(thrownDecl.getQualifiedName(), faultInfo, this.builder);
                  return true;
               }
            }
         }
      }
   }

   protected boolean isWSDLException(Collection members, TypeElement thrownDecl) {
      WebFault webFault = (WebFault)thrownDecl.getAnnotation(WebFault.class);
      return webFault != null && members.size() == 2 && this.getFaultInfoMember(members) != null;
   }

   private MemberInfo getFaultInfoMember(Collection members) {
      Iterator var2 = members.iterator();

      MemberInfo member;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         member = (MemberInfo)var2.next();
      } while(!member.getParamName().equals(WebServiceConstants.FAULT_INFO.getValue()));

      return member;
   }

   private void writeXmlElementDeclaration(JDefinedClass cls, String elementName, String namespaceUri) {
      if (cls != null) {
         JAnnotationUse xmlRootElementAnn = cls.annotate(XmlRootElement.class);
         xmlRootElementAnn.param("name", elementName);
         if (namespaceUri.length() > 0) {
            xmlRootElementAnn.param("namespace", namespaceUri);
         }

         JAnnotationUse xmlAccessorTypeAnn = cls.annotate(this.cm.ref(XmlAccessorType.class));
         xmlAccessorTypeAnn.param("value", (Enum)XmlAccessType.FIELD);
      }
   }

   private void writeXmlTypeDeclaration(JDefinedClass cls, String typeName, String namespaceUri, Collection members) {
      if (cls != null) {
         JAnnotationUse xmlTypeAnn = cls.annotate(this.cm.ref(XmlType.class));
         xmlTypeAnn.param("name", typeName);
         xmlTypeAnn.param("namespace", namespaceUri);
         if (members.size() > 1) {
            JAnnotationArrayMember paramArray = xmlTypeAnn.paramArray("propOrder");
            Iterator var7 = members.iterator();

            while(var7.hasNext()) {
               MemberInfo memInfo = (MemberInfo)var7.next();
               paramArray.param(memInfo.getParamName());
            }
         }

      }
   }

   private void writeMember(JDefinedClass cls, TypeMirror paramType, String paramName) {
      if (cls != null) {
         String accessorName = BindingHelper.mangleNameToPropertyName(paramName);
         String getterPrefix = paramType.toString().equals("boolean") ? "is" : "get";
         JType propType = this.getType(paramType);
         JMethod m = cls.method(1, (JType)propType, getterPrefix + accessorName);
         JDocComment methodDoc = m.javadoc();
         JCommentPart ret = methodDoc.addReturn();
         ret.add("returns " + propType.name());
         JBlock body = m.body();
         body._return(JExpr._this().ref(paramName));
         m = cls.method(1, (JType)this.cm.VOID, "set" + accessorName);
         JVar param = m.param(propType, paramName);
         methodDoc = m.javadoc();
         JCommentPart part = methodDoc.addParam(paramName);
         part.add("the value for the " + paramName + " property");
         body = m.body();
         body.assign(JExpr._this().ref(paramName), param);
      }
   }

   private static final class FieldFactory implements AbstractWrapperBeanGenerator.BeanMemberFactory {
      private FieldFactory() {
      }

      public MemberInfo createWrapperBeanMember(TypeMirror paramType, String paramName, List jaxb) {
         return new MemberInfo(paramType, paramName, jaxb);
      }

      // $FF: synthetic method
      FieldFactory(Object x0) {
         this();
      }
   }

   private final class ApWrapperBeanGenerator extends AbstractWrapperBeanGenerator {
      protected ApWrapperBeanGenerator(AnnotationReader annReader, Navigator nav, AbstractWrapperBeanGenerator.BeanMemberFactory beanMemberFactory) {
         super(annReader, nav, beanMemberFactory);
      }

      protected TypeMirror getSafeType(TypeMirror type) {
         return WebServiceWrapperGenerator.this.getSafeType(type);
      }

      protected TypeMirror getHolderValueType(TypeMirror paramType) {
         return WebServiceWrapperGenerator.this.builder.getHolderValueType(paramType);
      }

      protected boolean isVoidType(TypeMirror type) {
         return type != null && type.getKind().equals(TypeKind.VOID);
      }
   }
}
