package com.sun.tools.internal.jxc;

import com.sun.tools.internal.jxc.ap.Options;
import com.sun.tools.internal.xjc.BadCommandLineException;
import com.sun.xml.internal.bind.util.Which;
import java.io.File;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.OptionChecker;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.xml.bind.JAXBContext;

public class SchemaGenerator {
   public static void main(String[] args) throws Exception {
      System.exit(run(args));
   }

   public static int run(String[] args) throws Exception {
      try {
         ClassLoader cl = SecureLoader.getClassClassLoader(SchemaGenerator.class);
         if (cl == null) {
            cl = SecureLoader.getSystemClassLoader();
         }

         return run(args, cl);
      } catch (Exception var2) {
         System.err.println(var2.getMessage());
         return -1;
      }
   }

   public static int run(String[] args, ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
      Options options = new Options();
      if (args.length == 0) {
         usage();
         return -1;
      } else {
         String[] var3 = args;
         int var4 = args.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String arg = var3[var5];
            if (arg.equals("-help")) {
               usage();
               return -1;
            }

            if (arg.equals("-version")) {
               System.out.println(Messages.VERSION.format());
               return -1;
            }

            if (arg.equals("-fullversion")) {
               System.out.println(Messages.FULLVERSION.format());
               return -1;
            }
         }

         try {
            options.parseArguments(args);
         } catch (BadCommandLineException var7) {
            System.out.println(var7.getMessage());
            System.out.println();
            usage();
            return -1;
         }

         Class schemagenRunner = classLoader.loadClass(Runner.class.getName());
         Method compileMethod = schemagenRunner.getDeclaredMethod("compile", String[].class, File.class);
         List aptargs = new ArrayList();
         if (options.encoding != null) {
            aptargs.add("-encoding");
            aptargs.add(options.encoding);
         }

         aptargs.add("-cp");
         aptargs.add(setClasspath(options.classpath));
         if (options.targetDir != null) {
            aptargs.add("-d");
            aptargs.add(options.targetDir.getPath());
         }

         aptargs.addAll(options.arguments);
         String[] argsarray = (String[])aptargs.toArray(new String[aptargs.size()]);
         return (Boolean)compileMethod.invoke((Object)null, argsarray, options.episodeFile) ? 0 : 1;
      }
   }

   private static String setClasspath(String givenClasspath) {
      StringBuilder cp = new StringBuilder();
      appendPath(cp, givenClasspath);

      for(ClassLoader cl = Thread.currentThread().getContextClassLoader(); cl != null; cl = cl.getParent()) {
         if (cl instanceof URLClassLoader) {
            URL[] var3 = ((URLClassLoader)cl).getURLs();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               URL url = var3[var5];
               appendPath(cp, url.getPath());
            }
         }
      }

      appendPath(cp, findJaxbApiJar());
      return cp.toString();
   }

   private static void appendPath(StringBuilder cp, String url) {
      if (url != null && !url.trim().isEmpty()) {
         if (cp.length() != 0) {
            cp.append(File.pathSeparatorChar);
         }

         cp.append(url);
      }
   }

   private static String findJaxbApiJar() {
      String url = Which.which(JAXBContext.class);
      if (url == null) {
         return null;
      } else if (url.startsWith("jar:") && url.lastIndexOf(33) != -1) {
         String jarFileUrl = url.substring(4, url.lastIndexOf(33));
         if (!jarFileUrl.startsWith("file:")) {
            return null;
         } else {
            try {
               File f = new File((new URL(jarFileUrl)).toURI());
               if (f.exists() && f.getName().endsWith(".jar")) {
                  return f.getPath();
               }

               f = new File((new URL(jarFileUrl)).getFile());
               if (f.exists() && f.getName().endsWith(".jar")) {
                  return f.getPath();
               }
            } catch (URISyntaxException var3) {
               Logger.getLogger(SchemaGenerator.class.getName()).log(Level.SEVERE, (String)null, var3);
            } catch (MalformedURLException var4) {
               Logger.getLogger(SchemaGenerator.class.getName()).log(Level.SEVERE, (String)null, var4);
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private static void usage() {
      System.out.println(Messages.USAGE.format());
   }

   private static final class JavacOptions {
      private final List recognizedOptions;
      private final List classNames;
      private final List files;
      private final List unrecognizedOptions;

      private JavacOptions(List recognizedOptions, List classNames, List files, List unrecognizedOptions) {
         this.recognizedOptions = recognizedOptions;
         this.classNames = classNames;
         this.files = files;
         this.unrecognizedOptions = unrecognizedOptions;
      }

      public static JavacOptions parse(OptionChecker primary, OptionChecker secondary, String... arguments) {
         List recognizedOptions = new ArrayList();
         List unrecognizedOptions = new ArrayList();
         List classNames = new ArrayList();
         List files = new ArrayList();

         for(int i = 0; i < arguments.length; ++i) {
            String argument = arguments[i];
            int optionCount = primary.isSupportedOption(argument);
            if (optionCount < 0) {
               optionCount = secondary.isSupportedOption(argument);
            }

            if (optionCount < 0) {
               File file = new File(argument);
               if (file.exists()) {
                  files.add(file);
               } else if (SourceVersion.isName(argument)) {
                  classNames.add(argument);
               } else {
                  unrecognizedOptions.add(argument);
               }
            } else {
               for(int j = 0; j < optionCount + 1; ++j) {
                  int index = i + j;
                  if (index == arguments.length) {
                     throw new IllegalArgumentException(argument);
                  }

                  recognizedOptions.add(arguments[index]);
               }

               i += optionCount;
            }
         }

         return new JavacOptions(recognizedOptions, classNames, files, unrecognizedOptions);
      }

      public List getRecognizedOptions() {
         return Collections.unmodifiableList(this.recognizedOptions);
      }

      public List getFiles() {
         return Collections.unmodifiableList(this.files);
      }

      public List getClassNames() {
         return Collections.unmodifiableList(this.classNames);
      }

      public List getUnrecognizedOptions() {
         return Collections.unmodifiableList(this.unrecognizedOptions);
      }

      public String toString() {
         return String.format("recognizedOptions = %s; classNames = %s; files = %s; unrecognizedOptions = %s", this.recognizedOptions, this.classNames, this.files, this.unrecognizedOptions);
      }
   }

   public static final class Runner {
      public static boolean compile(String[] args, File episode) throws Exception {
         JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
         DiagnosticCollector diagnostics = new DiagnosticCollector();
         StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, (Locale)null, (Charset)null);
         JavacOptions options = SchemaGenerator.JavacOptions.parse(compiler, fileManager, args);
         List unrecognizedOptions = options.getUnrecognizedOptions();
         if (!unrecognizedOptions.isEmpty()) {
            Logger.getLogger(SchemaGenerator.class.getName()).log(Level.WARNING, "Unrecognized options found: {0}", unrecognizedOptions);
         }

         Iterable compilationUnits = fileManager.getJavaFileObjectsFromFiles(options.getFiles());
         JavaCompiler.CompilationTask task = compiler.getTask((Writer)null, fileManager, diagnostics, options.getRecognizedOptions(), options.getClassNames(), compilationUnits);
         com.sun.tools.internal.jxc.ap.SchemaGenerator r = new com.sun.tools.internal.jxc.ap.SchemaGenerator();
         if (episode != null) {
            r.setEpisodeFile(episode);
         }

         task.setProcessors(Collections.singleton(r));
         boolean res = task.call();
         Iterator var11 = diagnostics.getDiagnostics().iterator();

         while(var11.hasNext()) {
            Diagnostic d = (Diagnostic)var11.next();
            System.err.println(d.toString());
         }

         return res;
      }
   }
}
