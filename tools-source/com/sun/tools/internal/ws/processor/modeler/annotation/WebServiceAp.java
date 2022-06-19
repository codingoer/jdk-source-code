package com.sun.tools.internal.ws.processor.modeler.annotation;

import com.sun.istack.internal.logging.Logger;
import com.sun.tools.internal.ws.processor.generator.GeneratorUtil;
import com.sun.tools.internal.ws.processor.modeler.ModelerException;
import com.sun.tools.internal.ws.resources.WebserviceapMessages;
import com.sun.tools.internal.ws.wscompile.AbortException;
import com.sun.tools.internal.ws.wscompile.WsgenOptions;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.jws.WebService;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceProvider;

@SupportedAnnotationTypes({"javax.jws.HandlerChain", "javax.jws.Oneway", "javax.jws.WebMethod", "javax.jws.WebParam", "javax.jws.WebResult", "javax.jws.WebService", "javax.jws.soap.InitParam", "javax.jws.soap.SOAPBinding", "javax.jws.soap.SOAPMessageHandler", "javax.jws.soap.SOAPMessageHandlers", "javax.xml.ws.BindingType", "javax.xml.ws.RequestWrapper", "javax.xml.ws.ResponseWrapper", "javax.xml.ws.ServiceMode", "javax.xml.ws.WebEndpoint", "javax.xml.ws.WebFault", "javax.xml.ws.WebServiceClient", "javax.xml.ws.WebServiceProvider", "javax.xml.ws.WebServiceRef"})
@SupportedOptions({"doNotOverWrite", "ignoreNoWebServiceFoundWarning"})
public class WebServiceAp extends AbstractProcessor implements ModelBuilder {
   private static final Logger LOGGER = Logger.getLogger(WebServiceAp.class);
   public static final String DO_NOT_OVERWRITE = "doNotOverWrite";
   public static final String IGNORE_NO_WEB_SERVICE_FOUND_WARNING = "ignoreNoWebServiceFoundWarning";
   private WsgenOptions options;
   protected AnnotationProcessorContext context;
   private File sourceDir;
   private boolean doNotOverWrite;
   private boolean ignoreNoWebServiceFoundWarning = false;
   private TypeElement remoteElement;
   private TypeMirror remoteExceptionElement;
   private TypeMirror exceptionElement;
   private TypeMirror runtimeExceptionElement;
   private TypeElement defHolderElement;
   private boolean isCommandLineInvocation;
   private PrintStream out;
   private Collection processedTypeElements = new HashSet();

   public WebServiceAp() {
      this.context = new AnnotationProcessorContext();
   }

   public WebServiceAp(WsgenOptions options, PrintStream out) {
      this.options = options;
      this.sourceDir = options != null ? options.sourceDir : null;
      this.doNotOverWrite = options != null && options.doNotOverWrite;
      this.context = new AnnotationProcessorContext();
      this.out = out;
   }

   public synchronized void init(ProcessingEnvironment processingEnv) {
      super.init(processingEnv);
      this.remoteElement = processingEnv.getElementUtils().getTypeElement(Remote.class.getName());
      this.remoteExceptionElement = processingEnv.getElementUtils().getTypeElement(RemoteException.class.getName()).asType();
      this.exceptionElement = processingEnv.getElementUtils().getTypeElement(Exception.class.getName()).asType();
      this.runtimeExceptionElement = processingEnv.getElementUtils().getTypeElement(RuntimeException.class.getName()).asType();
      this.defHolderElement = processingEnv.getElementUtils().getTypeElement(Holder.class.getName());
      if (this.options == null) {
         this.options = new WsgenOptions();
         this.out = new PrintStream(new ByteArrayOutputStream());
         this.doNotOverWrite = this.getOption("doNotOverWrite");
         this.ignoreNoWebServiceFoundWarning = this.getOption("ignoreNoWebServiceFoundWarning");
         String classDir = this.parseArguments();
         String property = System.getProperty("java.class.path");
         this.options.classpath = classDir + File.pathSeparator + (property != null ? property : "");
         this.isCommandLineInvocation = true;
      }

      this.options.filer = processingEnv.getFiler();
   }

   private String parseArguments() {
      String classDir = null;

      try {
         ClassLoader cl = WebServiceAp.class.getClassLoader();
         Class javacProcessingEnvironmentClass = Class.forName("com.sun.tools.javac.processing.JavacProcessingEnvironment", false, cl);
         if (javacProcessingEnvironmentClass.isInstance(this.processingEnv)) {
            Method getContextMethod = javacProcessingEnvironmentClass.getDeclaredMethod("getContext");
            Object tmpContext = getContextMethod.invoke(this.processingEnv);
            Class optionsClass = Class.forName("com.sun.tools.javac.util.Options", false, cl);
            Class contextClass = Class.forName("com.sun.tools.javac.util.Context", false, cl);
            Method instanceMethod = optionsClass.getDeclaredMethod("instance", contextClass);
            Object tmpOptions = instanceMethod.invoke((Object)null, tmpContext);
            if (tmpOptions != null) {
               Method getMethod = optionsClass.getDeclaredMethod("get", String.class);
               Object result = getMethod.invoke(tmpOptions, "-s");
               if (result != null) {
                  classDir = (String)result;
               }

               this.options.verbose = getMethod.invoke(tmpOptions, "-verbose") != null;
            }
         }
      } catch (Exception var12) {
         this.processWarning(WebserviceapMessages.WEBSERVICEAP_PARSING_JAVAC_OPTIONS_ERROR());
         this.report(var12.getMessage());
      }

      if (classDir == null) {
         String property = System.getProperty("sun.java.command");
         if (property != null) {
            Scanner scanner = new Scanner(property);
            boolean sourceDirNext = false;

            while(scanner.hasNext()) {
               String token = scanner.next();
               if (sourceDirNext) {
                  classDir = token;
                  sourceDirNext = false;
               } else if ("-verbose".equals(token)) {
                  this.options.verbose = true;
               } else if ("-s".equals(token)) {
                  sourceDirNext = true;
               }
            }
         }
      }

      if (classDir != null) {
         this.sourceDir = new File(classDir);
      }

      return classDir;
   }

   private boolean getOption(String key) {
      String value = (String)this.processingEnv.getOptions().get(key);
      return value != null ? Boolean.valueOf(value) : false;
   }

   public boolean process(Set annotations, RoundEnvironment roundEnv) {
      if (this.context.getRound() != 1) {
         return true;
      } else {
         this.context.incrementRound();
         WebServiceVisitor webServiceVisitor = new WebServiceWrapperGenerator(this, this.context);
         boolean processedEndpoint = false;
         Collection classes = new ArrayList();
         this.filterClasses(classes, roundEnv.getRootElements());
         Iterator var8 = classes.iterator();

         while(var8.hasNext()) {
            TypeElement element = (TypeElement)var8.next();
            WebServiceProvider webServiceProvider = (WebServiceProvider)element.getAnnotation(WebServiceProvider.class);
            WebService webService = (WebService)element.getAnnotation(WebService.class);
            if (webServiceProvider != null) {
               if (webService != null) {
                  this.processError(WebserviceapMessages.WEBSERVICEAP_WEBSERVICE_AND_WEBSERVICEPROVIDER(element.getQualifiedName()));
               }

               processedEndpoint = true;
            }

            if (webService != null) {
               element.accept(webServiceVisitor, (Object)null);
               processedEndpoint = true;
            }
         }

         if (!processedEndpoint) {
            if (this.isCommandLineInvocation) {
               if (!this.ignoreNoWebServiceFoundWarning) {
                  this.processWarning(WebserviceapMessages.WEBSERVICEAP_NO_WEBSERVICE_ENDPOINT_FOUND());
               }
            } else {
               this.processError(WebserviceapMessages.WEBSERVICEAP_NO_WEBSERVICE_ENDPOINT_FOUND());
            }
         }

         return true;
      }
   }

   private void filterClasses(Collection classes, Collection elements) {
      Iterator var3 = elements.iterator();

      while(var3.hasNext()) {
         Element element = (Element)var3.next();
         if (element.getKind().equals(ElementKind.CLASS)) {
            classes.add((TypeElement)element);
            this.filterClasses(classes, ElementFilter.typesIn(element.getEnclosedElements()));
         }
      }

   }

   public void processWarning(String message) {
      if (this.isCommandLineInvocation) {
         this.processingEnv.getMessager().printMessage(Kind.WARNING, message);
      } else {
         this.report(message);
      }

   }

   protected void report(String msg) {
      if (this.out == null) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "No output set for web service annotation processor reporting.");
         }

      } else {
         this.out.println(msg);
         this.out.flush();
      }
   }

   public void processError(String message) {
      if (this.isCommandLineInvocation) {
         this.processingEnv.getMessager().printMessage(Kind.ERROR, message);
         throw new AbortException();
      } else {
         throw new ModelerException(message);
      }
   }

   public void processError(String message, Element element) {
      if (this.isCommandLineInvocation) {
         this.processingEnv.getMessager().printMessage(Kind.ERROR, message, element);
      } else {
         throw new ModelerException(message);
      }
   }

   public boolean canOverWriteClass(String className) {
      return !this.doNotOverWrite || !GeneratorUtil.classExists(this.options, className);
   }

   public File getSourceDir() {
      return this.sourceDir;
   }

   public boolean isRemote(TypeElement typeElement) {
      return this.processingEnv.getTypeUtils().isSubtype(typeElement.asType(), this.remoteElement.asType());
   }

   public boolean isServiceException(TypeMirror typeMirror) {
      return this.processingEnv.getTypeUtils().isSubtype(typeMirror, this.exceptionElement) && !this.processingEnv.getTypeUtils().isSubtype(typeMirror, this.runtimeExceptionElement) && !this.processingEnv.getTypeUtils().isSubtype(typeMirror, this.remoteExceptionElement);
   }

   public TypeMirror getHolderValueType(TypeMirror type) {
      return TypeModeler.getHolderValueType(type, this.defHolderElement, this.processingEnv);
   }

   public boolean checkAndSetProcessed(TypeElement typeElement) {
      if (!this.processedTypeElements.contains(typeElement)) {
         this.processedTypeElements.add(typeElement);
         return false;
      } else {
         return true;
      }
   }

   public void log(String message) {
      if (this.options != null && this.options.verbose) {
         message = '[' + message + ']';
         this.processingEnv.getMessager().printMessage(Kind.NOTE, message);
      }

   }

   public WsgenOptions getOptions() {
      return this.options;
   }

   public ProcessingEnvironment getProcessingEnvironment() {
      return this.processingEnv;
   }

   public String getOperationName(Name messageName) {
      return messageName != null ? messageName.toString() : null;
   }

   public SourceVersion getSupportedSourceVersion() {
      return SourceVersion.latest();
   }
}
