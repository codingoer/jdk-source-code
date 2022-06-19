package com.sun.tools.internal.ws.processor.modeler.annotation;

import com.sun.tools.internal.ws.processor.model.Port;
import com.sun.tools.internal.ws.resources.WebserviceapMessages;
import com.sun.tools.internal.ws.util.ClassNameInfo;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPStyle;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.WebParam.Mode;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.SimpleElementVisitor6;
import javax.lang.model.util.SimpleTypeVisitor6;
import javax.lang.model.util.Types;

public abstract class WebServiceVisitor extends SimpleElementVisitor6 {
   protected ModelBuilder builder;
   protected String wsdlNamespace;
   protected String typeNamespace;
   protected Stack soapBindingStack;
   protected SOAPBinding typeElementSoapBinding;
   protected SOAPStyle soapStyle;
   protected boolean wrapped;
   protected Port port;
   protected Name serviceImplName;
   protected Name endpointInterfaceName;
   protected AnnotationProcessorContext context;
   protected AnnotationProcessorContext.SeiContext seiContext;
   protected boolean processingSei;
   protected String serviceName;
   protected Name packageName;
   protected String portName;
   protected boolean endpointReferencesInterface;
   protected boolean hasWebMethods;
   protected TypeElement typeElement;
   protected Set processedMethods;
   protected boolean pushedSoapBinding;
   private static final NoTypeVisitor NO_TYPE_VISITOR = new NoTypeVisitor();

   public WebServiceVisitor(ModelBuilder builder, AnnotationProcessorContext context) {
      this.soapStyle = SOAPStyle.DOCUMENT;
      this.wrapped = true;
      this.processingSei = false;
      this.endpointReferencesInterface = false;
      this.hasWebMethods = false;
      this.pushedSoapBinding = false;
      this.builder = builder;
      this.context = context;
      this.soapBindingStack = new Stack();
      this.processedMethods = new HashSet();
   }

   public Void visitType(TypeElement e, Object o) {
      WebService webService = (WebService)e.getAnnotation(WebService.class);
      if (!this.shouldProcessWebService(webService, e)) {
         return null;
      } else if (this.builder.checkAndSetProcessed(e)) {
         return null;
      } else {
         this.typeElement = e;
         switch (e.getKind()) {
            case INTERFACE:
               if (this.endpointInterfaceName != null && !this.endpointInterfaceName.equals(e.getQualifiedName())) {
                  this.builder.processError(WebserviceapMessages.WEBSERVICEAP_ENDPOINTINTERFACES_DO_NOT_MATCH(this.endpointInterfaceName, e.getQualifiedName()), e);
               }

               this.verifySeiAnnotations(webService, e);
               this.endpointInterfaceName = e.getQualifiedName();
               this.processingSei = true;
               this.preProcessWebService(webService, e);
               this.processWebService(webService, e);
               this.postProcessWebService(webService, e);
               break;
            case CLASS:
               this.typeElementSoapBinding = (SOAPBinding)e.getAnnotation(SOAPBinding.class);
               if (this.serviceImplName == null) {
                  this.serviceImplName = e.getQualifiedName();
               }

               String endpointInterfaceName = webService != null ? webService.endpointInterface() : null;
               if (endpointInterfaceName != null && endpointInterfaceName.length() > 0) {
                  this.checkForInvalidImplAnnotation(e, SOAPBinding.class);
                  if (webService.name().length() > 0) {
                     this.builder.processError(WebserviceapMessages.WEBSERVICEAP_ENDPOINTINTEFACE_PLUS_ELEMENT("name"), e);
                  }

                  this.endpointReferencesInterface = true;
                  this.verifyImplAnnotations(e);
                  this.inspectEndpointInterface(endpointInterfaceName, e);
                  this.serviceImplName = null;
                  return null;
               }

               this.processingSei = false;
               this.preProcessWebService(webService, e);
               this.processWebService(webService, e);
               this.serviceImplName = null;
               this.postProcessWebService(webService, e);
               this.serviceImplName = null;
         }

         return null;
      }
   }

   protected void verifySeiAnnotations(WebService webService, TypeElement d) {
      if (webService.endpointInterface().length() > 0) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_ENDPOINTINTERFACE_ON_INTERFACE(d.getQualifiedName(), webService.endpointInterface()), d);
      }

      if (webService.serviceName().length() > 0) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_INVALID_SEI_ANNOTATION_ELEMENT("serviceName", d.getQualifiedName()), d);
      }

      if (webService.portName().length() > 0) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_INVALID_SEI_ANNOTATION_ELEMENT("portName", d.getQualifiedName()), d);
      }

   }

   protected void verifyImplAnnotations(TypeElement d) {
      Iterator var2 = ElementFilter.methodsIn(d.getEnclosedElements()).iterator();

      while(var2.hasNext()) {
         ExecutableElement method = (ExecutableElement)var2.next();
         this.checkForInvalidImplAnnotation(method, WebMethod.class);
         this.checkForInvalidImplAnnotation(method, Oneway.class);
         this.checkForInvalidImplAnnotation(method, WebResult.class);
         Iterator var4 = method.getParameters().iterator();

         while(var4.hasNext()) {
            VariableElement param = (VariableElement)var4.next();
            this.checkForInvalidImplAnnotation(param, WebParam.class);
         }
      }

   }

   protected void checkForInvalidSeiAnnotation(TypeElement element, Class annotationClass) {
      Object annotation = element.getAnnotation(annotationClass);
      if (annotation != null) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_INVALID_SEI_ANNOTATION(annotationClass.getName(), element.getQualifiedName()), element);
      }

   }

   protected void checkForInvalidImplAnnotation(Element element, Class annotationClass) {
      Object annotation = element.getAnnotation(annotationClass);
      if (annotation != null) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_ENDPOINTINTEFACE_PLUS_ANNOTATION(annotationClass.getName()), element);
      }

   }

   protected void preProcessWebService(WebService webService, TypeElement element) {
      this.processedMethods = new HashSet();
      this.seiContext = this.context.getSeiContext(element);
      String targetNamespace = null;
      if (webService != null) {
         targetNamespace = webService.targetNamespace();
      }

      PackageElement packageElement = this.builder.getProcessingEnvironment().getElementUtils().getPackageOf(element);
      if (targetNamespace == null || targetNamespace.length() == 0) {
         String packageName = packageElement.getQualifiedName().toString();
         if (packageName == null || packageName.length() == 0) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_NO_PACKAGE_CLASS_MUST_HAVE_TARGETNAMESPACE(element.getQualifiedName()), element);
         }

         targetNamespace = RuntimeModeler.getNamespace(packageName);
      }

      this.seiContext.setNamespaceUri(targetNamespace);
      if (this.serviceImplName == null) {
         this.serviceImplName = this.seiContext.getSeiImplName();
      }

      if (this.serviceImplName != null) {
         this.seiContext.setSeiImplName(this.serviceImplName);
         this.context.addSeiContext(this.serviceImplName, this.seiContext);
      }

      this.portName = ClassNameInfo.getName(element.getSimpleName().toString().replace('$', '_'));
      this.packageName = packageElement.getQualifiedName();
      this.portName = webService != null && webService.name() != null && webService.name().length() > 0 ? webService.name() : this.portName;
      this.serviceName = ClassNameInfo.getName(element.getQualifiedName().toString()) + WebServiceConstants.SERVICE.getValue();
      this.serviceName = webService != null && webService.serviceName() != null && webService.serviceName().length() > 0 ? webService.serviceName() : this.serviceName;
      this.wsdlNamespace = this.seiContext.getNamespaceUri();
      this.typeNamespace = this.wsdlNamespace;
      SOAPBinding soapBinding = (SOAPBinding)element.getAnnotation(SOAPBinding.class);
      if (soapBinding != null) {
         this.pushedSoapBinding = this.pushSoapBinding(soapBinding, element, element);
      } else if (element.equals(this.typeElement)) {
         this.pushedSoapBinding = this.pushSoapBinding(new MySoapBinding(), element, element);
      }

   }

   public static boolean sameStyle(SOAPBinding.Style style, SOAPStyle soapStyle) {
      return style.equals(Style.DOCUMENT) && soapStyle.equals(SOAPStyle.DOCUMENT) || style.equals(Style.RPC) && soapStyle.equals(SOAPStyle.RPC);
   }

   protected boolean pushSoapBinding(SOAPBinding soapBinding, Element bindingElement, TypeElement classElement) {
      boolean changed = false;
      if (!sameStyle(soapBinding.style(), this.soapStyle)) {
         changed = true;
         if (this.pushedSoapBinding) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_MIXED_BINDING_STYLE(classElement.getQualifiedName()), bindingElement);
         }
      }

      if (soapBinding.style().equals(Style.RPC)) {
         this.soapStyle = SOAPStyle.RPC;
         this.wrapped = true;
         if (soapBinding.parameterStyle().equals(ParameterStyle.BARE)) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_RPC_LITERAL_MUST_NOT_BE_BARE(classElement.getQualifiedName()), bindingElement);
         }
      } else {
         this.soapStyle = SOAPStyle.DOCUMENT;
         if (this.wrapped != soapBinding.parameterStyle().equals(ParameterStyle.WRAPPED)) {
            this.wrapped = soapBinding.parameterStyle().equals(ParameterStyle.WRAPPED);
            changed = true;
         }
      }

      if (soapBinding.use().equals(Use.ENCODED)) {
         String style = "rpc";
         if (soapBinding.style().equals(Style.DOCUMENT)) {
            style = "document";
         }

         this.builder.processError(WebserviceapMessages.WEBSERVICE_ENCODED_NOT_SUPPORTED(classElement.getQualifiedName(), style), bindingElement);
      }

      if (changed || this.soapBindingStack.empty()) {
         this.soapBindingStack.push(soapBinding);
         this.pushedSoapBinding = true;
      }

      return changed;
   }

   protected SOAPBinding popSoapBinding() {
      if (this.pushedSoapBinding) {
         this.soapBindingStack.pop();
      }

      SOAPBinding soapBinding = null;
      if (!this.soapBindingStack.empty()) {
         soapBinding = (SOAPBinding)this.soapBindingStack.peek();
         if (soapBinding.style().equals(Style.RPC)) {
            this.soapStyle = SOAPStyle.RPC;
            this.wrapped = true;
         } else {
            this.soapStyle = SOAPStyle.DOCUMENT;
            this.wrapped = soapBinding.parameterStyle().equals(ParameterStyle.WRAPPED);
         }
      } else {
         this.pushedSoapBinding = false;
      }

      return soapBinding;
   }

   protected String getNamespace(PackageElement packageElement) {
      return RuntimeModeler.getNamespace(packageElement.getQualifiedName().toString());
   }

   protected boolean shouldProcessWebService(WebService webService, TypeElement element) {
      SOAPBinding soapBinding;
      switch (element.getKind()) {
         case INTERFACE:
            this.hasWebMethods = false;
            if (webService == null) {
               this.builder.processError(WebserviceapMessages.WEBSERVICEAP_ENDPOINTINTERFACE_HAS_NO_WEBSERVICE_ANNOTATION(element.getQualifiedName()), element);
            }

            soapBinding = (SOAPBinding)element.getAnnotation(SOAPBinding.class);
            if (soapBinding != null && soapBinding.style() == Style.RPC && soapBinding.parameterStyle() == ParameterStyle.BARE) {
               this.builder.processError(WebserviceapMessages.WEBSERVICEAP_INVALID_SOAPBINDING_PARAMETERSTYLE(soapBinding, element), element);
               return false;
            }

            return this.isLegalSei(element);
         case CLASS:
            if (webService == null) {
               return false;
            } else {
               this.hasWebMethods = this.hasWebMethods(element);
               soapBinding = (SOAPBinding)element.getAnnotation(SOAPBinding.class);
               if (soapBinding != null && soapBinding.style() == Style.RPC && soapBinding.parameterStyle() == ParameterStyle.BARE) {
                  this.builder.processError(WebserviceapMessages.WEBSERVICEAP_INVALID_SOAPBINDING_PARAMETERSTYLE(soapBinding, element), element);
                  return false;
               }

               return this.isLegalImplementation(webService, element);
            }
         default:
            throw new IllegalArgumentException("Class or Interface was expecting. But element: " + element);
      }
   }

   protected abstract void processWebService(WebService var1, TypeElement var2);

   protected void postProcessWebService(WebService webService, TypeElement element) {
      this.processMethods(element);
      this.popSoapBinding();
   }

   protected boolean hasWebMethods(TypeElement element) {
      if (element.getQualifiedName().toString().equals(Object.class.getName())) {
         return false;
      } else {
         Iterator var3 = ElementFilter.methodsIn(element.getEnclosedElements()).iterator();

         while(var3.hasNext()) {
            ExecutableElement method = (ExecutableElement)var3.next();
            WebMethod webMethod = (WebMethod)method.getAnnotation(WebMethod.class);
            if (webMethod != null) {
               if (!webMethod.exclude()) {
                  return true;
               }

               if (webMethod.operationName().length() > 0) {
                  this.builder.processError(WebserviceapMessages.WEBSERVICEAP_INVALID_WEBMETHOD_ELEMENT_WITH_EXCLUDE("operationName", element.getQualifiedName(), method.toString()), method);
               }

               if (webMethod.action().length() > 0) {
                  this.builder.processError(WebserviceapMessages.WEBSERVICEAP_INVALID_WEBMETHOD_ELEMENT_WITH_EXCLUDE("action", element.getQualifiedName(), method.toString()), method);
               }
            }
         }

         return false;
      }
   }

   protected void processMethods(TypeElement element) {
      Iterator var2;
      ExecutableElement method;
      switch (element.getKind()) {
         case INTERFACE:
            this.builder.log("ProcessedMethods Interface: " + element);
            this.hasWebMethods = false;
            var2 = ElementFilter.methodsIn(element.getEnclosedElements()).iterator();

            while(var2.hasNext()) {
               method = (ExecutableElement)var2.next();
               method.accept(this, (Object)null);
            }

            var2 = element.getInterfaces().iterator();

            while(var2.hasNext()) {
               TypeMirror superType = (TypeMirror)var2.next();
               this.processMethods((TypeElement)((DeclaredType)superType).asElement());
            }

            return;
         case CLASS:
            this.builder.log("ProcessedMethods Class: " + element);
            this.hasWebMethods = this.hasWebMethods(element);
            if (element.getQualifiedName().toString().equals(Object.class.getName())) {
               return;
            }

            if (element.getAnnotation(WebService.class) != null) {
               var2 = ElementFilter.methodsIn(element.getEnclosedElements()).iterator();

               while(var2.hasNext()) {
                  method = (ExecutableElement)var2.next();
                  method.accept(this, (Object)null);
               }
            }

            TypeMirror superclass = element.getSuperclass();
            if (!superclass.getKind().equals(TypeKind.NONE)) {
               this.processMethods((TypeElement)((DeclaredType)superclass).asElement());
            }
      }

   }

   private TypeElement getEndpointInterfaceElement(String endpointInterfaceName, TypeElement element) {
      TypeElement intTypeElement = null;
      Iterator var4 = element.getInterfaces().iterator();

      while(var4.hasNext()) {
         TypeMirror interfaceType = (TypeMirror)var4.next();
         if (endpointInterfaceName.equals(interfaceType.toString())) {
            intTypeElement = (TypeElement)((DeclaredType)interfaceType).asElement();
            this.seiContext = this.context.getSeiContext(intTypeElement.getQualifiedName());

            assert this.seiContext != null;

            this.seiContext.setImplementsSei(true);
            break;
         }
      }

      if (intTypeElement == null) {
         intTypeElement = this.builder.getProcessingEnvironment().getElementUtils().getTypeElement(endpointInterfaceName);
      }

      if (intTypeElement == null) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_ENDPOINTINTERFACE_CLASS_NOT_FOUND(endpointInterfaceName));
      }

      return intTypeElement;
   }

   private void inspectEndpointInterface(String endpointInterfaceName, TypeElement d) {
      TypeElement intTypeElement = this.getEndpointInterfaceElement(endpointInterfaceName, d);
      if (intTypeElement != null) {
         intTypeElement.accept(this, (Object)null);
      }

   }

   public Void visitExecutable(ExecutableElement method, Object o) {
      if (!method.getModifiers().contains(Modifier.PUBLIC)) {
         return null;
      } else if (this.processedMethod(method)) {
         return null;
      } else {
         WebMethod webMethod = (WebMethod)method.getAnnotation(WebMethod.class);
         if (webMethod != null && webMethod.exclude()) {
            return null;
         } else {
            SOAPBinding soapBinding = (SOAPBinding)method.getAnnotation(SOAPBinding.class);
            if (soapBinding == null && !method.getEnclosingElement().equals(this.typeElement) && method.getEnclosingElement().getKind().equals(ElementKind.CLASS)) {
               soapBinding = (SOAPBinding)method.getEnclosingElement().getAnnotation(SOAPBinding.class);
               if (soapBinding != null) {
                  this.builder.log("using " + method.getEnclosingElement() + "'s SOAPBinding.");
               } else {
                  soapBinding = new MySoapBinding();
               }
            }

            boolean newBinding = false;
            if (soapBinding != null) {
               newBinding = this.pushSoapBinding((SOAPBinding)soapBinding, method, this.typeElement);
            }

            try {
               if (this.shouldProcessMethod(method, webMethod)) {
                  this.processMethod(method, webMethod);
               }
            } finally {
               if (newBinding) {
                  this.popSoapBinding();
               }

            }

            return null;
         }
      }
   }

   protected boolean processedMethod(ExecutableElement method) {
      String id = method.toString();
      if (this.processedMethods.contains(id)) {
         return true;
      } else {
         this.processedMethods.add(id);
         return false;
      }
   }

   protected boolean shouldProcessMethod(ExecutableElement method, WebMethod webMethod) {
      this.builder.log("should process method: " + method.getSimpleName() + " hasWebMethods: " + this.hasWebMethods + " ");
      Collection modifiers = method.getModifiers();
      boolean staticFinal = modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.FINAL);
      if (staticFinal) {
         if (webMethod != null) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_WEBSERVICE_METHOD_IS_STATIC_OR_FINAL(method.getEnclosingElement(), method), method);
         }

         return false;
      } else {
         boolean result = this.endpointReferencesInterface || method.getEnclosingElement().equals(this.typeElement) || method.getEnclosingElement().getAnnotation(WebService.class) != null;
         this.builder.log("endpointReferencesInterface: " + this.endpointReferencesInterface);
         this.builder.log("declaring class has WebService: " + (method.getEnclosingElement().getAnnotation(WebService.class) != null));
         this.builder.log("returning: " + result);
         return result;
      }
   }

   protected abstract void processMethod(ExecutableElement var1, WebMethod var2);

   protected boolean isLegalImplementation(WebService webService, TypeElement classElement) {
      boolean isStateful = this.isStateful(classElement);
      Collection modifiers = classElement.getModifiers();
      if (!modifiers.contains(Modifier.PUBLIC)) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_WEBSERVICE_CLASS_NOT_PUBLIC(classElement.getQualifiedName()), classElement);
         return false;
      } else if (modifiers.contains(Modifier.FINAL) && !isStateful) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_WEBSERVICE_CLASS_IS_FINAL(classElement.getQualifiedName()), classElement);
         return false;
      } else if (modifiers.contains(Modifier.ABSTRACT) && !isStateful) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_WEBSERVICE_CLASS_IS_ABSTRACT(classElement.getQualifiedName()), classElement);
         return false;
      } else {
         boolean hasDefaultConstructor = false;
         Iterator var6 = ElementFilter.constructorsIn(classElement.getEnclosedElements()).iterator();

         while(var6.hasNext()) {
            ExecutableElement constructor = (ExecutableElement)var6.next();
            if (constructor.getModifiers().contains(Modifier.PUBLIC) && constructor.getParameters().isEmpty()) {
               hasDefaultConstructor = true;
               break;
            }
         }

         if (!hasDefaultConstructor && !isStateful) {
            if (classElement.getEnclosingElement() != null && !modifiers.contains(Modifier.STATIC)) {
               this.builder.processError(WebserviceapMessages.WEBSERVICEAP_WEBSERVICE_CLASS_IS_INNERCLASS_NOT_STATIC(classElement.getQualifiedName()), classElement);
               return false;
            } else {
               this.builder.processError(WebserviceapMessages.WEBSERVICEAP_WEBSERVICE_NO_DEFAULT_CONSTRUCTOR(classElement.getQualifiedName()), classElement);
               return false;
            }
         } else {
            if (webService.endpointInterface().isEmpty()) {
               if (!this.methodsAreLegal(classElement)) {
                  return false;
               }
            } else {
               TypeElement interfaceElement = this.getEndpointInterfaceElement(webService.endpointInterface(), classElement);
               if (!this.classImplementsSei(classElement, interfaceElement)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private boolean isStateful(TypeElement classElement) {
      try {
         return classElement.getAnnotation(Class.forName("com.sun.xml.internal.ws.developer.Stateful")) != null;
      } catch (ClassNotFoundException var3) {
         return false;
      }
   }

   protected boolean classImplementsSei(TypeElement classElement, TypeElement interfaceElement) {
      Iterator var3 = classElement.getInterfaces().iterator();

      while(var3.hasNext()) {
         TypeMirror interfaceType = (TypeMirror)var3.next();
         if (((DeclaredType)interfaceType).asElement().equals(interfaceElement)) {
            return true;
         }
      }

      List classMethods = getClassMethods(classElement);
      Iterator var5 = ElementFilter.methodsIn(interfaceElement.getEnclosedElements()).iterator();

      ExecutableElement interfaceMethod;
      boolean implementsMethod;
      do {
         if (!var5.hasNext()) {
            return true;
         }

         interfaceMethod = (ExecutableElement)var5.next();
         implementsMethod = false;
         Iterator var7 = classMethods.iterator();

         while(var7.hasNext()) {
            ExecutableElement classMethod = (ExecutableElement)var7.next();
            if (this.sameMethod(interfaceMethod, classMethod)) {
               implementsMethod = true;
               classMethods.remove(classMethod);
               break;
            }
         }
      } while(implementsMethod);

      this.builder.processError(WebserviceapMessages.WEBSERVICEAP_METHOD_NOT_IMPLEMENTED(interfaceElement.getSimpleName(), classElement.getSimpleName(), interfaceMethod), interfaceMethod);
      return false;
   }

   private static List getClassMethods(TypeElement classElement) {
      if (classElement.getQualifiedName().toString().equals(Object.class.getName())) {
         return null;
      } else {
         TypeElement superclassElement = (TypeElement)((DeclaredType)classElement.getSuperclass()).asElement();
         List superclassesMethods = getClassMethods(superclassElement);
         List classMethods = ElementFilter.methodsIn(classElement.getEnclosedElements());
         if (superclassesMethods == null) {
            return classMethods;
         } else {
            superclassesMethods.addAll(classMethods);
            return superclassesMethods;
         }
      }
   }

   protected boolean sameMethod(ExecutableElement method1, ExecutableElement method2) {
      if (!method1.getSimpleName().equals(method2.getSimpleName())) {
         return false;
      } else {
         Types typeUtils = this.builder.getProcessingEnvironment().getTypeUtils();
         if (!typeUtils.isSameType(method1.getReturnType(), method2.getReturnType()) && !typeUtils.isSubtype(method2.getReturnType(), method1.getReturnType())) {
            return false;
         } else {
            List parameters1 = method1.getParameters();
            List parameters2 = method2.getParameters();
            if (parameters1.size() != parameters2.size()) {
               return false;
            } else {
               for(int i = 0; i < parameters1.size(); ++i) {
                  if (!typeUtils.isSameType(((VariableElement)parameters1.get(i)).asType(), ((VariableElement)parameters2.get(i)).asType())) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   protected boolean isLegalSei(TypeElement interfaceElement) {
      Iterator var2 = ElementFilter.fieldsIn(interfaceElement.getEnclosedElements()).iterator();

      VariableElement field;
      do {
         if (!var2.hasNext()) {
            return this.methodsAreLegal(interfaceElement);
         }

         field = (VariableElement)var2.next();
      } while(field.getConstantValue() == null);

      this.builder.processError(WebserviceapMessages.WEBSERVICEAP_SEI_CANNOT_CONTAIN_CONSTANT_VALUES(interfaceElement.getQualifiedName(), field.getSimpleName()));
      return false;
   }

   protected boolean methodsAreLegal(TypeElement element) {
      Iterator var2;
      ExecutableElement method;
      switch (element.getKind()) {
         case INTERFACE:
            this.hasWebMethods = false;
            var2 = ElementFilter.methodsIn(element.getEnclosedElements()).iterator();

            do {
               if (!var2.hasNext()) {
                  var2 = element.getInterfaces().iterator();

                  TypeMirror superInterface;
                  do {
                     if (!var2.hasNext()) {
                        return true;
                     }

                     superInterface = (TypeMirror)var2.next();
                  } while(this.methodsAreLegal((TypeElement)((DeclaredType)superInterface).asElement()));

                  return false;
               }

               method = (ExecutableElement)var2.next();
            } while(this.isLegalMethod(method, element));

            return false;
         case CLASS:
            this.hasWebMethods = this.hasWebMethods(element);
            var2 = ElementFilter.methodsIn(element.getEnclosedElements()).iterator();

            while(var2.hasNext()) {
               method = (ExecutableElement)var2.next();
               if (method.getModifiers().contains(Modifier.PUBLIC) && !this.isLegalMethod(method, element)) {
                  return false;
               }
            }

            DeclaredType superClass = (DeclaredType)element.getSuperclass();
            TypeElement tE = (TypeElement)superClass.asElement();
            return tE.getQualifiedName().toString().equals(Object.class.getName()) || this.methodsAreLegal(tE);
         default:
            throw new IllegalArgumentException("Class or interface was expecting. But element: " + element);
      }
   }

   protected boolean isLegalMethod(ExecutableElement method, TypeElement typeElement) {
      WebMethod webMethod = (WebMethod)method.getAnnotation(WebMethod.class);
      if (typeElement.getKind().equals(ElementKind.INTERFACE) && webMethod != null && webMethod.exclude()) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_INVALID_SEI_ANNOTATION_ELEMENT_EXCLUDE("exclude=true", typeElement.getQualifiedName(), method.toString()), method);
      }

      if (this.hasWebMethods && webMethod == null) {
         return true;
      } else if (webMethod != null && webMethod.exclude()) {
         return true;
      } else {
         TypeMirror returnType = method.getReturnType();
         if (!this.isLegalType(returnType)) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_METHOD_RETURN_TYPE_CANNOT_IMPLEMENT_REMOTE(typeElement.getQualifiedName(), method.getSimpleName(), returnType), method);
         }

         boolean isOneWay = method.getAnnotation(Oneway.class) != null;
         if (isOneWay && !this.isValidOneWayMethod(method, typeElement)) {
            return false;
         } else {
            SOAPBinding soapBinding = (SOAPBinding)method.getAnnotation(SOAPBinding.class);
            if (soapBinding != null && soapBinding.style().equals(Style.RPC)) {
               this.builder.processError(WebserviceapMessages.WEBSERVICEAP_RPC_SOAPBINDING_NOT_ALLOWED_ON_METHOD(typeElement.getQualifiedName(), method.toString()), method);
            }

            int paramIndex = 0;
            Iterator var8 = method.getParameters().iterator();

            VariableElement parameter;
            do {
               if (!var8.hasNext()) {
                  if (!this.isDocLitWrapped() && this.soapStyle.equals(SOAPStyle.DOCUMENT)) {
                     VariableElement outParam = this.getOutParameter(method);
                     int inParams = this.getModeParameterCount(method, Mode.IN);
                     int outParams = this.getModeParameterCount(method, Mode.OUT);
                     if (inParams != 1) {
                        this.builder.processError(WebserviceapMessages.WEBSERVICEAP_DOC_BARE_AND_NO_ONE_IN(typeElement.getQualifiedName(), method.toString()), method);
                     }

                     if ((Boolean)returnType.accept(NO_TYPE_VISITOR, (Object)null)) {
                        if (outParam == null && !isOneWay) {
                           this.builder.processError(WebserviceapMessages.WEBSERVICEAP_DOC_BARE_NO_OUT(typeElement.getQualifiedName(), method.toString()), method);
                        }

                        if (outParams != 1 && !isOneWay && outParams != 0) {
                           this.builder.processError(WebserviceapMessages.WEBSERVICEAP_DOC_BARE_NO_RETURN_AND_NO_OUT(typeElement.getQualifiedName(), method.toString()), method);
                        }
                     } else if (outParams > 0) {
                        this.builder.processError(WebserviceapMessages.WEBSERVICEAP_DOC_BARE_RETURN_AND_OUT(typeElement.getQualifiedName(), method.toString()), outParam);
                     }
                  }

                  return true;
               }

               parameter = (VariableElement)var8.next();
            } while(this.isLegalParameter(parameter, method, typeElement, paramIndex++));

            return false;
         }
      }
   }

   protected boolean isLegalParameter(VariableElement param, ExecutableElement method, TypeElement typeElement, int paramIndex) {
      if (!this.isLegalType(param.asType())) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_METHOD_PARAMETER_TYPES_CANNOT_IMPLEMENT_REMOTE(typeElement.getQualifiedName(), method.getSimpleName(), param.getSimpleName(), param.asType().toString()), param);
         return false;
      } else {
         TypeMirror holderType = this.builder.getHolderValueType(param.asType());
         WebParam webParam = (WebParam)param.getAnnotation(WebParam.class);
         WebParam.Mode mode = null;
         if (webParam != null) {
            mode = webParam.mode();
         }

         if (holderType != null) {
            if (mode != null && mode == Mode.IN) {
               this.builder.processError(WebserviceapMessages.WEBSERVICEAP_HOLDER_PARAMETERS_MUST_NOT_BE_IN_ONLY(typeElement.getQualifiedName(), method.toString(), paramIndex), param);
            }
         } else if (mode != null && mode != Mode.IN) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_NON_IN_PARAMETERS_MUST_BE_HOLDER(typeElement.getQualifiedName(), method.toString(), paramIndex), param);
         }

         return true;
      }
   }

   protected boolean isDocLitWrapped() {
      return this.soapStyle.equals(SOAPStyle.DOCUMENT) && this.wrapped;
   }

   protected boolean isValidOneWayMethod(ExecutableElement method, TypeElement typeElement) {
      boolean valid = true;
      if (!(Boolean)method.getReturnType().accept(NO_TYPE_VISITOR, (Object)null)) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_ONEWAY_OPERATION_CANNOT_HAVE_RETURN_TYPE(typeElement.getQualifiedName(), method.toString()), method);
         valid = false;
      }

      VariableElement outParam = this.getOutParameter(method);
      if (outParam != null) {
         this.builder.processError(WebserviceapMessages.WEBSERVICEAP_ONEWAY_AND_OUT(typeElement.getQualifiedName(), method.toString()), outParam);
         valid = false;
      }

      if (!this.isDocLitWrapped() && this.soapStyle.equals(SOAPStyle.DOCUMENT)) {
         int inCnt = this.getModeParameterCount(method, Mode.IN);
         if (inCnt != 1) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_ONEWAY_AND_NOT_ONE_IN(typeElement.getQualifiedName(), method.toString()), method);
            valid = false;
         }
      }

      Iterator var8 = method.getThrownTypes().iterator();

      while(var8.hasNext()) {
         TypeMirror thrownType = (TypeMirror)var8.next();
         TypeElement thrownElement = (TypeElement)((DeclaredType)thrownType).asElement();
         if (this.builder.isServiceException(thrownType)) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_ONEWAY_OPERATION_CANNOT_DECLARE_EXCEPTIONS(typeElement.getQualifiedName(), method.toString(), thrownElement.getQualifiedName()), method);
            valid = false;
         }
      }

      return valid;
   }

   protected int getModeParameterCount(ExecutableElement method, WebParam.Mode mode) {
      int cnt = 0;
      Iterator var5 = method.getParameters().iterator();

      while(var5.hasNext()) {
         VariableElement param = (VariableElement)var5.next();
         WebParam webParam = (WebParam)param.getAnnotation(WebParam.class);
         if (webParam != null) {
            if (!webParam.header() && this.isEquivalentModes(mode, webParam.mode())) {
               ++cnt;
            }
         } else if (this.isEquivalentModes(mode, Mode.IN)) {
            ++cnt;
         }
      }

      return cnt;
   }

   protected boolean isEquivalentModes(WebParam.Mode mode1, WebParam.Mode mode2) {
      if (mode1.equals(mode2)) {
         return true;
      } else {
         assert mode1 == Mode.IN || mode1 == Mode.OUT;

         return mode1 == Mode.IN && mode2 != Mode.OUT || mode1 == Mode.OUT && mode2 != Mode.IN;
      }
   }

   protected boolean isHolder(VariableElement param) {
      return this.builder.getHolderValueType(param.asType()) != null;
   }

   protected boolean isLegalType(TypeMirror type) {
      if (type != null && type.getKind().equals(TypeKind.DECLARED)) {
         TypeElement tE = (TypeElement)((DeclaredType)type).asElement();
         if (tE == null) {
            this.builder.processError(WebserviceapMessages.WEBSERVICEAP_COULD_NOT_FIND_TYPEDECL(type.toString(), this.context.getRound()));
         }

         return !this.builder.isRemote(tE);
      } else {
         return true;
      }
   }

   protected VariableElement getOutParameter(ExecutableElement method) {
      Iterator var3 = method.getParameters().iterator();

      WebParam webParam;
      VariableElement param;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         param = (VariableElement)var3.next();
         webParam = (WebParam)param.getAnnotation(WebParam.class);
      } while(webParam == null || webParam.mode() == Mode.IN);

      return param;
   }

   protected static class MySoapBinding implements SOAPBinding {
      public SOAPBinding.Style style() {
         return Style.DOCUMENT;
      }

      public SOAPBinding.Use use() {
         return Use.LITERAL;
      }

      public SOAPBinding.ParameterStyle parameterStyle() {
         return ParameterStyle.WRAPPED;
      }

      public Class annotationType() {
         return SOAPBinding.class;
      }
   }

   private static final class NoTypeVisitor extends SimpleTypeVisitor6 {
      private NoTypeVisitor() {
      }

      public Boolean visitNoType(NoType t, Void o) {
         return true;
      }

      protected Boolean defaultAction(TypeMirror e, Void aVoid) {
         return false;
      }

      // $FF: synthetic method
      NoTypeVisitor(Object x0) {
         this();
      }
   }
}
