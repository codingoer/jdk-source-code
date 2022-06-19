package com.sun.tools.internal.ws.wscompile;

import com.sun.codemodel.internal.CodeWriter;
import com.sun.codemodel.internal.writer.ProgressCodeWriter;
import com.sun.istack.internal.tools.DefaultAuthenticator;
import com.sun.istack.internal.tools.ParallelWorldClassLoader;
import com.sun.tools.internal.ws.ToolVersion;
import com.sun.tools.internal.ws.api.TJavaGeneratorExtension;
import com.sun.tools.internal.ws.processor.generator.CustomExceptionGenerator;
import com.sun.tools.internal.ws.processor.generator.GeneratorBase;
import com.sun.tools.internal.ws.processor.generator.JwsImplGenerator;
import com.sun.tools.internal.ws.processor.generator.SeiGenerator;
import com.sun.tools.internal.ws.processor.generator.ServiceGenerator;
import com.sun.tools.internal.ws.processor.model.Model;
import com.sun.tools.internal.ws.processor.modeler.wsdl.ConsoleErrorReporter;
import com.sun.tools.internal.ws.processor.modeler.wsdl.WSDLModeler;
import com.sun.tools.internal.ws.processor.util.DirectoryUtil;
import com.sun.tools.internal.ws.resources.WscompileMessages;
import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.util.WSDLFetcher;
import com.sun.tools.internal.ws.wsdl.parser.MetadataFinder;
import com.sun.tools.internal.ws.wsdl.parser.WSDLInternalizationLogic;
import com.sun.tools.internal.xjc.util.NullStream;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import javax.xml.bind.JAXBPermission;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.EndpointContext;
import org.xml.sax.EntityResolver;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class WsimportTool {
   private static final String WSIMPORT = "wsimport";
   private final PrintStream out;
   private final Container container;
   protected WsimportOptions options;

   public WsimportTool(OutputStream out) {
      this(out, (Container)null);
   }

   public WsimportTool(OutputStream logStream, Container container) {
      this.options = new WsimportOptions();
      this.out = logStream instanceof PrintStream ? (PrintStream)logStream : new PrintStream(logStream);
      this.container = container;
   }

   public boolean run(String[] args) {
      Listener listener = new Listener();
      Receiver receiver = new Receiver(listener);
      return this.run(args, listener, receiver);
   }

   protected boolean run(String[] args, Listener listener, Receiver receiver) {
      String[] var4 = args;
      int var5 = args.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String arg = var4[var6];
         if (arg.equals("-version")) {
            listener.message(WscompileMessages.WSIMPORT_VERSION(ToolVersion.VERSION.MAJOR_VERSION));
            return true;
         }

         if (arg.equals("-fullversion")) {
            listener.message(WscompileMessages.WSIMPORT_FULLVERSION(ToolVersion.VERSION.toString()));
            return true;
         }
      }

      try {
         boolean var23;
         try {
            this.parseArguments(args, listener, receiver);

            try {
               Model wsdlModel = this.buildWsdlModel(listener, receiver);
               if (wsdlModel == null) {
                  var23 = false;
                  return var23;
               }

               if (!this.generateCode(listener, receiver, wsdlModel, true)) {
                  var23 = false;
                  return var23;
               }
            } catch (IOException var16) {
               receiver.error(var16);
               var23 = false;
               return var23;
            } catch (XMLStreamException var17) {
               receiver.error(var17);
               var23 = false;
               return var23;
            }

            if (!this.options.nocompile && !this.compileGeneratedClasses(receiver, listener)) {
               listener.message(WscompileMessages.WSCOMPILE_COMPILATION_FAILED());
               boolean var22 = false;
               return var22;
            } else {
               try {
                  if (this.options.clientjar != null) {
                     this.addClassesToGeneratedFiles();
                     this.jarArtifacts(listener);
                  }

                  return !receiver.hadError();
               } catch (IOException var15) {
                  receiver.error(var15);
                  var23 = false;
                  return var23;
               }
            }
         } catch (Options.WeAreDone var18) {
            this.usage(var18.getOptions());
            return !receiver.hadError();
         } catch (BadCommandLineException var19) {
            if (var19.getMessage() != null) {
               System.out.println(var19.getMessage());
               System.out.println();
            }

            this.usage(var19.getOptions());
            var23 = false;
            return var23;
         }
      } finally {
         this.deleteGeneratedFiles();
         if (!this.options.disableAuthenticator) {
            DefaultAuthenticator.reset();
         }

      }
   }

   private void deleteGeneratedFiles() {
      Set trackedRootPackages = new HashSet();
      if (this.options.clientjar != null) {
         Iterable generatedFiles = this.options.getGeneratedFiles();
         File parentPkg;
         boolean deleted;
         synchronized(generatedFiles) {
            Iterator var4 = generatedFiles.iterator();

            while(true) {
               if (!var4.hasNext()) {
                  break;
               }

               parentPkg = (File)var4.next();
               if (!parentPkg.getName().endsWith(".java")) {
                  deleted = parentPkg.delete();
                  if (this.options.verbose && !deleted) {
                     System.out.println(MessageFormat.format("{0} could not be deleted.", parentPkg));
                  }

                  trackedRootPackages.add(parentPkg.getParentFile());
               }
            }
         }

         Iterator var3 = trackedRootPackages.iterator();

         while(var3.hasNext()) {
            for(File pkg = (File)var3.next(); pkg.list() != null && pkg.list().length == 0 && !pkg.equals(this.options.destDir); pkg = parentPkg) {
               parentPkg = pkg.getParentFile();
               deleted = pkg.delete();
               if (this.options.verbose && !deleted) {
                  System.out.println(MessageFormat.format("{0} could not be deleted.", pkg));
               }
            }
         }
      }

      if (!this.options.keep) {
         this.options.removeGeneratedFiles();
      }

   }

   private void addClassesToGeneratedFiles() throws IOException {
      Iterable generatedFiles = this.options.getGeneratedFiles();
      final List trackedClassFiles = new ArrayList();
      Iterator var3 = generatedFiles.iterator();

      File f;
      while(var3.hasNext()) {
         f = (File)var3.next();
         if (f.getName().endsWith(".java")) {
            String relativeDir = DirectoryUtil.getRelativePathfromCommonBase(f.getParentFile(), this.options.sourceDir);
            final String className = f.getName().substring(0, f.getName().indexOf(".java"));
            File classDir = new File(this.options.destDir, relativeDir);
            if (classDir.exists()) {
               classDir.listFiles(new FilenameFilter() {
                  public boolean accept(File dir, String name) {
                     if (!name.equals(className + ".class") && (!name.startsWith(className + "$") || !name.endsWith(".class"))) {
                        return false;
                     } else {
                        trackedClassFiles.add(new File(dir, name));
                        return true;
                     }
                  }
               });
            }
         }
      }

      var3 = trackedClassFiles.iterator();

      while(var3.hasNext()) {
         f = (File)var3.next();
         this.options.addGeneratedFile(f);
      }

   }

   private void jarArtifacts(WsimportListener listener) throws IOException {
      File zipFile = new File(this.options.clientjar);
      if (!zipFile.isAbsolute()) {
         zipFile = new File(this.options.destDir, this.options.clientjar);
      }

      if (!this.options.quiet) {
         listener.message(WscompileMessages.WSIMPORT_ARCHIVING_ARTIFACTS(zipFile));
      }

      BufferedInputStream bis = null;
      FileInputStream fi = null;
      FileOutputStream fos = new FileOutputStream(zipFile);
      JarOutputStream jos = new JarOutputStream(fos);

      try {
         String base = this.options.destDir.getCanonicalPath();
         Iterator var8 = this.options.getGeneratedFiles().iterator();

         while(true) {
            File f;
            do {
               if (!var8.hasNext()) {
                  return;
               }

               f = (File)var8.next();
            } while(f.getName().endsWith(".java"));

            if (this.options.verbose) {
               listener.message(WscompileMessages.WSIMPORT_ARCHIVE_ARTIFACT(f, this.options.clientjar));
            }

            String entry = f.getCanonicalPath().substring(base.length() + 1).replace(File.separatorChar, '/');
            fi = new FileInputStream(f);
            bis = new BufferedInputStream(fi);
            JarEntry jarEntry = new JarEntry(entry);
            jos.putNextEntry(jarEntry);
            byte[] buffer = new byte[1024];

            int bytesRead;
            while((bytesRead = bis.read(buffer)) != -1) {
               jos.write(buffer, 0, bytesRead);
            }
         }
      } finally {
         try {
            if (bis != null) {
               bis.close();
            }
         } finally {
            if (jos != null) {
               jos.close();
            }

            if (fi != null) {
               fi.close();
            }

         }

      }
   }

   protected void parseArguments(String[] args, Listener listener, Receiver receiver) throws BadCommandLineException {
      this.options.parseArguments(args);
      this.options.validate();
      if (this.options.debugMode) {
         listener.enableDebugging();
      }

      this.options.parseBindings(receiver);
   }

   protected Model buildWsdlModel(Listener listener, final Receiver receiver) throws BadCommandLineException, XMLStreamException, IOException {
      if (!this.options.disableAuthenticator) {
         DefaultAuthenticator da = DefaultAuthenticator.getAuthenticator();
         if (this.options.proxyAuth != null) {
            da.setProxyAuth(this.options.proxyAuth);
         }

         class AuthListener implements DefaultAuthenticator.Receiver {
            private final boolean isFatal;

            AuthListener(boolean isFatal) {
               this.isFatal = isFatal;
            }

            public void onParsingError(String text, Locator loc) {
               this.error(new SAXParseException(WscompileMessages.WSIMPORT_ILLEGAL_AUTH_INFO(text), loc));
            }

            public void onError(Exception e, Locator loc) {
               if (e instanceof FileNotFoundException) {
                  this.error(new SAXParseException(WscompileMessages.WSIMPORT_AUTH_FILE_NOT_FOUND(loc.getSystemId(), WsimportOptions.defaultAuthfile), (Locator)null));
               } else {
                  this.error(new SAXParseException(WscompileMessages.WSIMPORT_FAILED_TO_PARSE(loc.getSystemId(), e.getMessage()), loc));
               }

            }

            private void error(SAXParseException e) {
               if (this.isFatal) {
                  receiver.error(e);
               } else {
                  receiver.debug(e);
               }

            }
         }

         if (this.options.authFile != null) {
            da.setAuth(this.options.authFile, new AuthListener(true));
         } else {
            da.setAuth(new File(WsimportOptions.defaultAuthfile), new AuthListener(false));
         }
      }

      if (!this.options.quiet) {
         listener.message(WscompileMessages.WSIMPORT_PARSING_WSDL());
      }

      MetadataFinder forest = new MetadataFinder(new WSDLInternalizationLogic(), this.options, receiver);
      forest.parseWSDL();
      if (forest.isMexMetadata) {
         receiver.reset();
      }

      WSDLModeler wsdlModeler = new WSDLModeler(this.options, receiver, forest);
      Model wsdlModel = wsdlModeler.buildModel();
      if (wsdlModel == null) {
         listener.message(WsdlMessages.PARSING_PARSE_FAILED());
      }

      if (this.options.clientjar != null) {
         if (!this.options.quiet) {
            listener.message(WscompileMessages.WSIMPORT_FETCHING_METADATA());
         }

         this.options.wsdlLocation = (new WSDLFetcher(this.options, listener)).fetchWsdls(forest);
      }

      return wsdlModel;
   }

   protected boolean generateCode(Listener listener, Receiver receiver, Model wsdlModel, boolean generateService) throws IOException {
      if (!this.options.quiet) {
         listener.message(WscompileMessages.WSIMPORT_GENERATING_CODE());
      }

      TJavaGeneratorExtension[] genExtn = (TJavaGeneratorExtension[])ServiceFinder.find(TJavaGeneratorExtension.class).toArray();
      CustomExceptionGenerator.generate(wsdlModel, this.options, receiver);
      SeiGenerator.generate(wsdlModel, this.options, receiver, genExtn);
      if (receiver.hadError()) {
         throw new AbortException();
      } else {
         if (generateService) {
            ServiceGenerator.generate(wsdlModel, this.options, receiver);
         }

         Iterator var6 = ServiceFinder.find(GeneratorBase.class).iterator();

         while(var6.hasNext()) {
            GeneratorBase g = (GeneratorBase)var6.next();
            g.init(wsdlModel, this.options, receiver);
            g.doGeneration();
         }

         List implFiles = null;
         if (this.options.isGenerateJWS) {
            implFiles = JwsImplGenerator.generate(wsdlModel, this.options, receiver);
         }

         Iterator var12 = this.options.activePlugins.iterator();

         while(var12.hasNext()) {
            Plugin plugin = (Plugin)var12.next();

            try {
               plugin.run(wsdlModel, this.options, receiver);
            } catch (SAXException var10) {
               return false;
            }
         }

         Object cw;
         if (this.options.filer != null) {
            cw = new FilerCodeWriter(this.options.sourceDir, this.options);
         } else {
            cw = new WSCodeWriter(this.options.sourceDir, this.options);
         }

         if (this.options.verbose) {
            cw = new ProgressCodeWriter((CodeWriter)cw, this.out);
         }

         this.options.getCodeModel().build((CodeWriter)cw);
         return this.options.isGenerateJWS ? JwsImplGenerator.moveToImplDestDir(implFiles, this.options, receiver) : true;
      }
   }

   public void setEntityResolver(EntityResolver resolver) {
      this.options.entityResolver = resolver;
   }

   private static boolean useBootClasspath(Class clazz) {
      try {
         ParallelWorldClassLoader.toJarUrl(clazz.getResource('/' + clazz.getName().replace('.', '/') + ".class"));
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   protected boolean compileGeneratedClasses(ErrorReceiver receiver, WsimportListener listener) {
      List sourceFiles = new ArrayList();
      Iterator var4 = this.options.getGeneratedFiles().iterator();

      while(var4.hasNext()) {
         File f = (File)var4.next();
         if (f.exists() && f.getName().endsWith(".java")) {
            sourceFiles.add(f.getAbsolutePath());
         }
      }

      if (sourceFiles.size() <= 0) {
         return true;
      } else {
         String classDir = this.options.destDir.getAbsolutePath();
         String classpathString = this.createClasspathString();
         boolean bootCP = useBootClasspath(EndpointContext.class) || useBootClasspath(JAXBPermission.class);
         List args = new ArrayList();
         args.add("-d");
         args.add(classDir);
         args.add("-classpath");
         args.add(classpathString);
         if (bootCP) {
            args.add("-Xbootclasspath/p:" + JavaCompilerHelper.getJarFile(EndpointContext.class) + File.pathSeparator + JavaCompilerHelper.getJarFile(JAXBPermission.class));
         }

         if (this.options.debug) {
            args.add("-g");
         }

         if (this.options.encoding != null) {
            args.add("-encoding");
            args.add(this.options.encoding);
         }

         if (this.options.javacOptions != null) {
            args.addAll(this.options.getJavacOptions(args, listener));
         }

         for(int i = 0; i < sourceFiles.size(); ++i) {
            args.add(sourceFiles.get(i));
         }

         listener.message(WscompileMessages.WSIMPORT_COMPILING_CODE());
         if (this.options.verbose) {
            StringBuilder argstr = new StringBuilder();
            Iterator var9 = args.iterator();

            while(var9.hasNext()) {
               String arg = (String)var9.next();
               argstr.append(arg).append(" ");
            }

            listener.message("javac " + argstr.toString());
         }

         return JavaCompilerHelper.compile((String[])args.toArray(new String[args.size()]), this.out, receiver);
      }
   }

   private String createClasspathString() {
      StringBuilder classpathStr = new StringBuilder(System.getProperty("java.class.path"));
      Iterator var2 = this.options.cmdlineJars.iterator();

      while(var2.hasNext()) {
         String s = (String)var2.next();
         classpathStr.append(File.pathSeparator);
         classpathStr.append((new File(s)).toString());
      }

      return classpathStr.toString();
   }

   protected void usage(Options options) {
      System.out.println(WscompileMessages.WSIMPORT_HELP("wsimport"));
      System.out.println(WscompileMessages.WSIMPORT_USAGE_EXTENSIONS());
      System.out.println(WscompileMessages.WSIMPORT_USAGE_EXAMPLES());
   }

   protected class Receiver extends ErrorReceiverFilter {
      private Listener listener;

      public Receiver(Listener listener) {
         super(listener);
         this.listener = listener;
      }

      public void info(SAXParseException exception) {
         if (WsimportTool.this.options.verbose) {
            super.info(exception);
         }

      }

      public void warning(SAXParseException exception) {
         if (!WsimportTool.this.options.quiet) {
            super.warning(exception);
         }

      }

      public void pollAbort() throws AbortException {
         if (this.listener.isCanceled()) {
            throw new AbortException();
         }
      }

      public void debug(SAXParseException exception) {
         if (WsimportTool.this.options.debugMode) {
            this.listener.debug(exception);
         }

      }
   }

   protected class Listener extends WsimportListener {
      ConsoleErrorReporter cer;

      protected Listener() {
         this.cer = new ConsoleErrorReporter(WsimportTool.this.out == null ? new PrintStream(new NullStream()) : WsimportTool.this.out);
      }

      public void generatedFile(String fileName) {
         this.message(fileName);
      }

      public void message(String msg) {
         WsimportTool.this.out.println(msg);
      }

      public void error(SAXParseException exception) {
         this.cer.error(exception);
      }

      public void fatalError(SAXParseException exception) {
         this.cer.fatalError(exception);
      }

      public void warning(SAXParseException exception) {
         this.cer.warning(exception);
      }

      public void debug(SAXParseException exception) {
         this.cer.debug(exception);
      }

      public void info(SAXParseException exception) {
         this.cer.info(exception);
      }

      public void enableDebugging() {
         this.cer.enableDebugging();
      }
   }
}
