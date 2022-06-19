package com.sun.tools.internal.ws.wscompile;

import com.sun.tools.internal.ws.Invoker;
import com.sun.tools.internal.ws.resources.WscompileMessages;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.annotation.processing.Filer;

public class Options {
   public boolean verbose;
   public boolean quiet;
   public boolean keep;
   public File destDir = new File(".");
   public File sourceDir;
   public Filer filer;
   public String encoding;
   public String classpath = System.getProperty("java.class.path");
   public List javacOptions;
   public boolean nocompile;
   public boolean disableXmlSecurity;
   public Target target;
   public static final int STRICT = 1;
   public static final int EXTENSION = 2;
   public int compatibilityMode;
   public boolean debug;
   public boolean debugMode;
   private final List generatedFiles;
   private ClassLoader classLoader;

   public Options() {
      this.target = Options.Target.V2_2;
      this.compatibilityMode = 1;
      this.debug = false;
      this.debugMode = false;
      this.generatedFiles = new ArrayList();
   }

   public boolean isExtensionMode() {
      return this.compatibilityMode == 2;
   }

   public void addGeneratedFile(File file) {
      this.generatedFiles.add(file);
   }

   public void removeGeneratedFiles() {
      Iterator var1 = this.generatedFiles.iterator();

      while(var1.hasNext()) {
         File file = (File)var1.next();
         if (file.getName().endsWith(".java")) {
            boolean deleted = file.delete();
            if (this.verbose && !deleted) {
               System.out.println(MessageFormat.format("{0} could not be deleted.", file));
            }
         }
      }

      this.generatedFiles.clear();
   }

   public Iterable getGeneratedFiles() {
      return this.generatedFiles;
   }

   public void deleteGeneratedFiles() {
      synchronized(this.generatedFiles) {
         Iterator var2 = this.generatedFiles.iterator();

         while(var2.hasNext()) {
            File file = (File)var2.next();
            if (file.getName().endsWith(".java")) {
               boolean deleted = file.delete();
               if (this.verbose && !deleted) {
                  System.out.println(MessageFormat.format("{0} could not be deleted.", file));
               }
            }
         }

         this.generatedFiles.clear();
      }
   }

   public void parseArguments(String[] args) throws BadCommandLineException {
      for(int i = 0; i < args.length; ++i) {
         if (args[i].length() == 0) {
            throw new BadCommandLineException();
         }

         if (args[i].charAt(0) == '-') {
            int j = this.parseArguments(args, i);
            if (j == 0) {
               throw new BadCommandLineException(WscompileMessages.WSCOMPILE_INVALID_OPTION(args[i]));
            }

            i += j - 1;
         } else {
            this.addFile(args[i]);
         }
      }

      if (this.destDir == null) {
         this.destDir = new File(".");
      }

      if (this.sourceDir == null) {
         this.sourceDir = this.destDir;
      }

   }

   protected void addFile(String arg) throws BadCommandLineException {
   }

   protected int parseArguments(String[] args, int i) throws BadCommandLineException {
      if (args[i].equals("-g")) {
         this.debug = true;
         return 1;
      } else if (args[i].equals("-Xdebug")) {
         this.debugMode = true;
         return 1;
      } else if (args[i].equals("-Xendorsed")) {
         return 1;
      } else if (args[i].equals("-verbose")) {
         this.verbose = true;
         return 1;
      } else if (args[i].equals("-quiet")) {
         this.quiet = true;
         return 1;
      } else if (args[i].equals("-keep")) {
         this.keep = true;
         return 1;
      } else if (args[i].equals("-target")) {
         ++i;
         String token = this.requireArgument("-target", args, i);
         this.target = Options.Target.parse(token);
         if (this.target == null) {
            throw new BadCommandLineException(WscompileMessages.WSIMPORT_ILLEGAL_TARGET_VERSION(token));
         } else {
            return 2;
         }
      } else if (!args[i].equals("-classpath") && !args[i].equals("-cp")) {
         if (args[i].equals("-d")) {
            ++i;
            this.destDir = new File(this.requireArgument("-d", args, i));
            if (!this.destDir.exists()) {
               throw new BadCommandLineException(WscompileMessages.WSCOMPILE_NO_SUCH_DIRECTORY(this.destDir.getPath()));
            } else {
               return 2;
            }
         } else if (args[i].equals("-s")) {
            ++i;
            this.sourceDir = new File(this.requireArgument("-s", args, i));
            this.keep = true;
            if (!this.sourceDir.exists()) {
               throw new BadCommandLineException(WscompileMessages.WSCOMPILE_NO_SUCH_DIRECTORY(this.sourceDir.getPath()));
            } else {
               return 2;
            }
         } else if (args[i].equals("-extension")) {
            this.compatibilityMode = 2;
            return 1;
         } else if (args[i].startsWith("-help")) {
            WeAreDone done = new WeAreDone();
            done.initOptions(this);
            throw done;
         } else if (args[i].equals("-Xnocompile")) {
            this.nocompile = true;
            this.keep = true;
            return 1;
         } else if (args[i].equals("-encoding")) {
            ++i;
            this.encoding = this.requireArgument("-encoding", args, i);

            try {
               if (!Charset.isSupported(this.encoding)) {
                  throw new BadCommandLineException(WscompileMessages.WSCOMPILE_UNSUPPORTED_ENCODING(this.encoding));
               } else {
                  return 2;
               }
            } catch (IllegalCharsetNameException var4) {
               throw new BadCommandLineException(WscompileMessages.WSCOMPILE_UNSUPPORTED_ENCODING(this.encoding));
            }
         } else if (args[i].equals("-disableXmlSecurity")) {
            this.disableXmlSecurity();
            return 1;
         } else if (args[i].startsWith("-J")) {
            if (this.javacOptions == null) {
               this.javacOptions = new ArrayList();
            }

            this.javacOptions.add(args[i].substring(2));
            return 1;
         } else {
            return 0;
         }
      } else {
         StringBuilder var10001 = new StringBuilder();
         ++i;
         this.classpath = var10001.append(this.requireArgument("-classpath", args, i)).append(File.pathSeparator).append(System.getProperty("java.class.path")).toString();
         return 2;
      }
   }

   protected void disableXmlSecurity() {
      this.disableXmlSecurity = true;
   }

   public String requireArgument(String optionName, String[] args, int i) throws BadCommandLineException {
      if (args[i].startsWith("-")) {
         throw new BadCommandLineException(WscompileMessages.WSCOMPILE_MISSING_OPTION_ARGUMENT(optionName));
      } else {
         return args[i];
      }
   }

   List getJavacOptions(List existingOptions, WsimportListener listener) {
      List result = new ArrayList();
      Iterator var4 = this.javacOptions.iterator();

      while(true) {
         while(var4.hasNext()) {
            String o = (String)var4.next();
            if (o.contains("=") && !o.startsWith("A")) {
               int i = o.indexOf(61);
               String key = o.substring(0, i);
               if (existingOptions.contains(key)) {
                  listener.message(WscompileMessages.WSCOMPILE_EXISTING_OPTION(key));
               } else {
                  result.add(key);
                  result.add(o.substring(i + 1));
               }
            } else if (existingOptions.contains(o)) {
               listener.message(WscompileMessages.WSCOMPILE_EXISTING_OPTION(o));
            } else {
               result.add(o);
            }
         }

         return result;
      }
   }

   public ClassLoader getClassLoader() {
      if (this.classLoader == null) {
         this.classLoader = new URLClassLoader(pathToURLs(this.classpath), this.getClass().getClassLoader());
      }

      return this.classLoader;
   }

   public static URL[] pathToURLs(String path) {
      StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
      URL[] urls = new URL[st.countTokens()];
      int count = 0;

      while(st.hasMoreTokens()) {
         URL url = fileToURL(new File(st.nextToken()));
         if (url != null) {
            urls[count++] = url;
         }
      }

      if (urls.length != count) {
         URL[] tmp = new URL[count];
         System.arraycopy(urls, 0, tmp, 0, count);
         urls = tmp;
      }

      return urls;
   }

   public static URL fileToURL(File file) {
      String name;
      try {
         name = file.getCanonicalPath();
      } catch (IOException var4) {
         name = file.getAbsolutePath();
      }

      name = name.replace(File.separatorChar, '/');
      if (!name.startsWith("/")) {
         name = "/" + name;
      }

      if (!file.isFile()) {
         name = name + "/";
      }

      try {
         return new URL("file", "", name);
      } catch (MalformedURLException var3) {
         throw new IllegalArgumentException("file");
      }
   }

   public static final class WeAreDone extends BadCommandLineException {
   }

   public static enum Target {
      V2_0,
      V2_1,
      V2_2;

      private static final Target LOADED_API_VERSION;

      public boolean isLaterThan(Target t) {
         return this.ordinal() >= t.ordinal();
      }

      public static Target parse(String token) {
         if (token.equals("2.0")) {
            return V2_0;
         } else if (token.equals("2.1")) {
            return V2_1;
         } else {
            return token.equals("2.2") ? V2_2 : null;
         }
      }

      public String getVersion() {
         switch (this) {
            case V2_0:
               return "2.0";
            case V2_1:
               return "2.1";
            case V2_2:
               return "2.2";
            default:
               return null;
         }
      }

      public static Target getDefault() {
         return V2_2;
      }

      public static Target getLoadedAPIVersion() {
         return LOADED_API_VERSION;
      }

      static {
         if (Invoker.checkIfLoading22API()) {
            LOADED_API_VERSION = V2_2;
         } else if (Invoker.checkIfLoading21API()) {
            LOADED_API_VERSION = V2_1;
         } else {
            LOADED_API_VERSION = V2_0;
         }

      }
   }
}
