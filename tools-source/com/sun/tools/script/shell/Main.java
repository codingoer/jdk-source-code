package com.sun.tools.script.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Main {
   private static final int EXIT_SUCCESS = 0;
   private static final int EXIT_CMD_NO_CLASSPATH = 1;
   private static final int EXIT_CMD_NO_FILE = 2;
   private static final int EXIT_CMD_NO_SCRIPT = 3;
   private static final int EXIT_CMD_NO_LANG = 4;
   private static final int EXIT_CMD_NO_ENCODING = 5;
   private static final int EXIT_CMD_NO_PROPNAME = 6;
   private static final int EXIT_UNKNOWN_OPTION = 7;
   private static final int EXIT_ENGINE_NOT_FOUND = 8;
   private static final int EXIT_NO_ENCODING_FOUND = 9;
   private static final int EXIT_SCRIPT_ERROR = 10;
   private static final int EXIT_FILE_NOT_FOUND = 11;
   private static final int EXIT_MULTIPLE_STDIN = 12;
   private static final String DEFAULT_LANGUAGE = "js";
   private static List scripts = new ArrayList();
   private static ScriptEngineManager engineManager;
   private static Map engines = new HashMap();
   private static ResourceBundle msgRes;
   private static String BUNDLE_NAME = "com.sun.tools.script.shell.messages";
   private static String PROGRAM_NAME = "jrunscript";

   public static void main(String[] var0) {
      String[] var1 = processOptions(var0);
      Iterator var2 = scripts.iterator();

      while(var2.hasNext()) {
         Command var3 = (Command)var2.next();
         var3.run(var1);
      }

      System.exit(0);
   }

   private static String[] processOptions(String[] var0) {
      String var1 = "js";
      String var2 = null;
      checkClassPath(var0);
      boolean var3 = false;
      boolean var4 = false;

      for(int var5 = 0; var5 < var0.length; ++var5) {
         String var6 = var0[var5];
         if (!var6.equals("-classpath") && !var6.equals("-cp")) {
            int var8;
            if (!var6.startsWith("-")) {
               int var12;
               if (var3) {
                  var12 = var0.length - var5;
                  var8 = var5;
               } else {
                  var12 = var0.length - var5 - 1;
                  var8 = var5 + 1;
                  ScriptEngine var9 = getScriptEngine(var1);
                  addFileSource(var9, var0[var5], var2);
               }

               String[] var13 = new String[var12];
               System.arraycopy(var0, var8, var13, 0, var12);
               return var13;
            }

            if (var6.startsWith("-D")) {
               String var11 = var6.substring(2);
               var8 = var11.indexOf(61);
               if (var8 != -1) {
                  System.setProperty(var11.substring(0, var8), var11.substring(var8 + 1));
               } else if (!var11.equals("")) {
                  System.setProperty(var11, "");
               } else {
                  usage(6);
               }
            } else {
               if (!var6.equals("-?") && !var6.equals("-help")) {
                  ScriptEngine var7;
                  if (var6.equals("-e")) {
                     var3 = true;
                     ++var5;
                     if (var5 == var0.length) {
                        usage(3);
                     }

                     var7 = getScriptEngine(var1);
                     addStringSource(var7, var0[var5]);
                     continue;
                  }

                  if (var6.equals("-encoding")) {
                     ++var5;
                     if (var5 == var0.length) {
                        usage(5);
                     }

                     var2 = var0[var5];
                     continue;
                  }

                  if (var6.equals("-f")) {
                     var3 = true;
                     ++var5;
                     if (var5 == var0.length) {
                        usage(2);
                     }

                     var7 = getScriptEngine(var1);
                     if (var0[var5].equals("-")) {
                        if (var4) {
                           usage(12);
                        } else {
                           var4 = true;
                        }

                        addInteractiveMode(var7);
                     } else {
                        addFileSource(var7, var0[var5], var2);
                     }
                     continue;
                  }

                  if (var6.equals("-l")) {
                     ++var5;
                     if (var5 == var0.length) {
                        usage(4);
                     }

                     var1 = var0[var5];
                     continue;
                  }

                  if (var6.equals("-q")) {
                     listScriptEngines();
                  }
               } else {
                  usage(0);
               }

               usage(7);
            }
         } else {
            ++var5;
         }
      }

      if (!var3) {
         ScriptEngine var10 = getScriptEngine(var1);
         addInteractiveMode(var10);
      }

      return new String[0];
   }

   private static void addInteractiveMode(final ScriptEngine var0) {
      scripts.add(new Command() {
         public void run(String[] var1) {
            Main.setScriptArguments(var0, var1);
            Main.processSource(var0, "-", (String)null);
         }
      });
   }

   private static void addFileSource(final ScriptEngine var0, final String var1, final String var2) {
      scripts.add(new Command() {
         public void run(String[] var1x) {
            Main.setScriptArguments(var0, var1x);
            Main.processSource(var0, var1, var2);
         }
      });
   }

   private static void addStringSource(final ScriptEngine var0, final String var1) {
      scripts.add(new Command() {
         public void run(String[] var1x) {
            Main.setScriptArguments(var0, var1x);
            String var2 = Main.setScriptFilename(var0, "<string>");

            try {
               Main.evaluateString(var0, var1);
            } finally {
               Main.setScriptFilename(var0, var2);
            }

         }
      });
   }

   private static void listScriptEngines() {
      List var0 = engineManager.getEngineFactories();
      Iterator var1 = var0.iterator();

      while(var1.hasNext()) {
         ScriptEngineFactory var2 = (ScriptEngineFactory)var1.next();
         getError().println(getMessage("engine.info", new Object[]{var2.getLanguageName(), var2.getLanguageVersion(), var2.getEngineName(), var2.getEngineVersion()}));
      }

      System.exit(0);
   }

   private static void processSource(ScriptEngine var0, String var1, String var2) {
      if (var1.equals("-")) {
         BufferedReader var3 = new BufferedReader(new InputStreamReader(getIn()));
         boolean var4 = false;
         String var5 = getPrompt(var0);
         var0.put("javax.script.filename", "<STDIN>");

         while(!var4) {
            getError().print(var5);
            String var6 = "";

            try {
               var6 = var3.readLine();
            } catch (IOException var9) {
               getError().println(var9.toString());
            }

            if (var6 == null) {
               var4 = true;
               break;
            }

            Object var7 = evaluateString(var0, var6, false);
            if (var7 != null) {
               String var11 = var7.toString();
               if (var11 == null) {
                  var11 = "null";
               }

               getError().println(var11);
            }
         }
      } else {
         FileInputStream var10 = null;

         try {
            var10 = new FileInputStream(var1);
         } catch (FileNotFoundException var8) {
            getError().println(getMessage("file.not.found", new Object[]{var1}));
            System.exit(11);
         }

         evaluateStream(var0, var10, var1, var2);
      }

   }

   private static Object evaluateString(ScriptEngine var0, String var1, boolean var2) {
      try {
         return var0.eval(var1);
      } catch (ScriptException var4) {
         getError().println(getMessage("string.script.error", new Object[]{var4.getMessage()}));
         if (var2) {
            System.exit(10);
         }
      } catch (Exception var5) {
         var5.printStackTrace(getError());
         if (var2) {
            System.exit(10);
         }
      }

      return null;
   }

   private static void evaluateString(ScriptEngine var0, String var1) {
      evaluateString(var0, var1, true);
   }

   private static Object evaluateReader(ScriptEngine var0, Reader var1, String var2) {
      String var3 = setScriptFilename(var0, var2);

      try {
         Object var4 = var0.eval(var1);
         return var4;
      } catch (ScriptException var9) {
         getError().println(getMessage("file.script.error", new Object[]{var2, var9.getMessage()}));
         System.exit(10);
      } catch (Exception var10) {
         var10.printStackTrace(getError());
         System.exit(10);
      } finally {
         setScriptFilename(var0, var3);
      }

      return null;
   }

   private static Object evaluateStream(ScriptEngine var0, InputStream var1, String var2, String var3) {
      BufferedReader var4 = null;
      if (var3 != null) {
         try {
            var4 = new BufferedReader(new InputStreamReader(var1, var3));
         } catch (UnsupportedEncodingException var6) {
            getError().println(getMessage("encoding.unsupported", new Object[]{var3}));
            System.exit(9);
         }
      } else {
         var4 = new BufferedReader(new InputStreamReader(var1));
      }

      return evaluateReader(var0, var4, var2);
   }

   private static void usage(int var0) {
      getError().println(getMessage("main.usage", new Object[]{PROGRAM_NAME}));
      System.exit(var0);
   }

   private static String getPrompt(ScriptEngine var0) {
      List var1 = var0.getFactory().getNames();
      return (String)var1.get(0) + "> ";
   }

   private static String getMessage(String var0, Object[] var1) {
      return MessageFormat.format(msgRes.getString(var0), var1);
   }

   private static InputStream getIn() {
      return System.in;
   }

   private static PrintStream getError() {
      return System.err;
   }

   private static ScriptEngine getScriptEngine(String var0) {
      ScriptEngine var1 = (ScriptEngine)engines.get(var0);
      if (var1 == null) {
         var1 = engineManager.getEngineByName(var0);
         if (var1 == null) {
            getError().println(getMessage("engine.not.found", new Object[]{var0}));
            System.exit(8);
         }

         initScriptEngine(var1);
         engines.put(var0, var1);
      }

      return var1;
   }

   private static void initScriptEngine(ScriptEngine var0) {
      var0.put("engine", var0);
      List var1 = var0.getFactory().getExtensions();
      InputStream var2 = null;
      ClassLoader var3 = Thread.currentThread().getContextClassLoader();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var2 = var3.getResourceAsStream("com/sun/tools/script/shell/init." + var5);
         if (var2 != null) {
            break;
         }
      }

      if (var2 != null) {
         evaluateStream(var0, var2, "<system-init>", (String)null);
      }

   }

   private static void checkClassPath(String[] var0) {
      String var1 = null;

      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var0[var2].equals("-classpath") || var0[var2].equals("-cp")) {
            ++var2;
            if (var2 == var0.length) {
               usage(1);
            } else {
               var1 = var0[var2];
            }
         }
      }

      if (var1 != null) {
         ClassLoader var5 = Main.class.getClassLoader();
         URL[] var3 = pathToURLs(var1);
         URLClassLoader var4 = new URLClassLoader(var3, var5);
         Thread.currentThread().setContextClassLoader(var4);
      }

      engineManager = new ScriptEngineManager();
   }

   private static URL[] pathToURLs(String var0) {
      String[] var1 = var0.split(File.pathSeparator);
      URL[] var2 = new URL[var1.length];
      int var3 = 0;

      while(var3 < var1.length) {
         URL var4 = fileToURL(new File(var1[var3]));
         if (var4 != null) {
            var2[var3++] = var4;
         }
      }

      if (var2.length != var3) {
         URL[] var5 = new URL[var3];
         System.arraycopy(var2, 0, var5, 0, var3);
         var2 = var5;
      }

      return var2;
   }

   private static URL fileToURL(File var0) {
      String var1;
      try {
         var1 = var0.getCanonicalPath();
      } catch (IOException var4) {
         var1 = var0.getAbsolutePath();
      }

      var1 = var1.replace(File.separatorChar, '/');
      if (!var1.startsWith("/")) {
         var1 = "/" + var1;
      }

      if (!var0.isFile()) {
         var1 = var1 + "/";
      }

      try {
         return new URL("file", "", var1);
      } catch (MalformedURLException var3) {
         throw new IllegalArgumentException("file");
      }
   }

   private static void setScriptArguments(ScriptEngine var0, String[] var1) {
      var0.put("arguments", var1);
      var0.put("javax.script.argv", var1);
   }

   private static String setScriptFilename(ScriptEngine var0, String var1) {
      String var2 = (String)var0.get("javax.script.filename");
      var0.put("javax.script.filename", var1);
      return var2;
   }

   static {
      msgRes = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault());
   }

   private interface Command {
      void run(String[] var1);
   }
}
