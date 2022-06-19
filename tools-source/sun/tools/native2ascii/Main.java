package sun.tools.native2ascii;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Main {
   String inputFileName = null;
   String outputFileName = null;
   File tempFile = null;
   boolean reverse = false;
   static String encodingString = null;
   static String defaultEncoding = null;
   static CharsetEncoder encoder = null;
   private static ResourceBundle rsrc;

   public synchronized boolean convert(String[] var1) {
      ArrayList var2 = new ArrayList(2);
      File var3 = null;
      boolean var4 = false;

      for(int var5 = 0; var5 < var1.length; ++var5) {
         if (var1[var5].equals("-encoding")) {
            if (var5 + 1 >= var1.length) {
               this.error(this.getMsg("err.bad.arg"));
               this.usage();
               return false;
            }

            ++var5;
            encodingString = var1[var5];
         } else if (var1[var5].equals("-reverse")) {
            this.reverse = true;
         } else {
            if (var2.size() > 1) {
               this.usage();
               return false;
            }

            var2.add(var1[var5]);
         }
      }

      if (encodingString == null) {
         defaultEncoding = Charset.defaultCharset().name();
      }

      char[] var94 = System.getProperty("line.separator").toCharArray();

      try {
         initializeConverter();
         if (var2.size() == 1) {
            this.inputFileName = (String)var2.get(0);
         }

         if (var2.size() == 2) {
            this.inputFileName = (String)var2.get(0);
            this.outputFileName = (String)var2.get(1);
            var4 = true;
         }

         if (var4) {
            var3 = new File(this.outputFileName);
            if (var3.exists() && !var3.canWrite()) {
               throw new Exception(this.formatMsg("err.cannot.write", this.outputFileName));
            }
         }

         BufferedReader var6;
         Throwable var7;
         Throwable var9;
         String var10;
         if (this.reverse) {
            var6 = this.getA2NInput(this.inputFileName);
            var7 = null;

            try {
               Writer var8 = this.getA2NOutput(this.outputFileName);
               var9 = null;

               try {
                  while((var10 = var6.readLine()) != null) {
                     var8.write(var10.toCharArray());
                     var8.write(var94);
                     if (this.outputFileName == null) {
                        var8.flush();
                     }
                  }
               } catch (Throwable var89) {
                  var9 = var89;
                  throw var89;
               } finally {
                  if (var8 != null) {
                     if (var9 != null) {
                        try {
                           var8.close();
                        } catch (Throwable var84) {
                           var9.addSuppressed(var84);
                        }
                     } else {
                        var8.close();
                     }
                  }

               }
            } catch (Throwable var91) {
               var7 = var91;
               throw var91;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var83) {
                        var7.addSuppressed(var83);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } else {
            var6 = this.getN2AInput(this.inputFileName);
            var7 = null;

            try {
               BufferedWriter var95 = this.getN2AOutput(this.outputFileName);
               var9 = null;

               try {
                  while((var10 = var6.readLine()) != null) {
                     var95.write(var10.toCharArray());
                     var95.write(var94);
                     if (this.outputFileName == null) {
                        var95.flush();
                     }
                  }
               } catch (Throwable var85) {
                  var9 = var85;
                  throw var85;
               } finally {
                  if (var95 != null) {
                     if (var9 != null) {
                        try {
                           var95.close();
                        } catch (Throwable var82) {
                           var9.addSuppressed(var82);
                        }
                     } else {
                        var95.close();
                     }
                  }

               }
            } catch (Throwable var87) {
               var7 = var87;
               throw var87;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var81) {
                        var7.addSuppressed(var81);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         }

         if (var4) {
            if (var3.exists()) {
               var3.delete();
            }

            this.tempFile.renameTo(var3);
         }

         return true;
      } catch (Exception var93) {
         this.error(var93.toString());
         return false;
      }
   }

   private void error(String var1) {
      System.out.println(var1);
   }

   private void usage() {
      System.out.println(this.getMsg("usage"));
   }

   private BufferedReader getN2AInput(String var1) throws Exception {
      Object var2;
      if (var1 == null) {
         var2 = System.in;
      } else {
         File var3 = new File(var1);
         if (!var3.canRead()) {
            throw new Exception(this.formatMsg("err.cannot.read", var3.getName()));
         }

         try {
            var2 = new FileInputStream(var1);
         } catch (IOException var5) {
            throw new Exception(this.formatMsg("err.cannot.read", var3.getName()));
         }
      }

      BufferedReader var6 = encodingString != null ? new BufferedReader(new InputStreamReader((InputStream)var2, encodingString)) : new BufferedReader(new InputStreamReader((InputStream)var2));
      return var6;
   }

   private BufferedWriter getN2AOutput(String var1) throws Exception {
      Object var2;
      if (var1 == null) {
         var2 = new OutputStreamWriter(System.out, "US-ASCII");
      } else {
         File var4 = new File(var1);
         File var5 = var4.getParentFile();
         if (var5 == null) {
            var5 = new File(System.getProperty("user.dir"));
         }

         this.tempFile = File.createTempFile("_N2A", ".TMP", var5);
         this.tempFile.deleteOnExit();

         try {
            var2 = new FileWriter(this.tempFile);
         } catch (IOException var7) {
            throw new Exception(this.formatMsg("err.cannot.write", this.tempFile.getName()));
         }
      }

      BufferedWriter var3 = new BufferedWriter(new N2AFilter((Writer)var2));
      return var3;
   }

   private BufferedReader getA2NInput(String var1) throws Exception {
      Object var2;
      if (var1 == null) {
         var2 = new InputStreamReader(System.in, "US-ASCII");
      } else {
         File var4 = new File(var1);
         if (!var4.canRead()) {
            throw new Exception(this.formatMsg("err.cannot.read", var4.getName()));
         }

         try {
            var2 = new FileReader(var1);
         } catch (Exception var6) {
            throw new Exception(this.formatMsg("err.cannot.read", var4.getName()));
         }
      }

      BufferedReader var3 = new BufferedReader(new A2NFilter((Reader)var2));
      return var3;
   }

   private Writer getA2NOutput(String var1) throws Exception {
      OutputStreamWriter var2 = null;
      Object var3 = null;
      if (var1 == null) {
         var3 = System.out;
      } else {
         File var4 = new File(var1);
         File var5 = var4.getParentFile();
         if (var5 == null) {
            var5 = new File(System.getProperty("user.dir"));
         }

         this.tempFile = File.createTempFile("_N2A", ".TMP", var5);
         this.tempFile.deleteOnExit();

         try {
            var3 = new FileOutputStream(this.tempFile);
         } catch (IOException var7) {
            throw new Exception(this.formatMsg("err.cannot.write", this.tempFile.getName()));
         }
      }

      var2 = encodingString != null ? new OutputStreamWriter((OutputStream)var3, encodingString) : new OutputStreamWriter((OutputStream)var3);
      return var2;
   }

   private static Charset lookupCharset(String var0) {
      if (Charset.isSupported(var0)) {
         try {
            return Charset.forName(var0);
         } catch (UnsupportedCharsetException var2) {
            throw new Error(var2);
         }
      } else {
         return null;
      }
   }

   public static boolean canConvert(char var0) {
      return encoder != null && encoder.canEncode(var0);
   }

   private static void initializeConverter() throws UnsupportedEncodingException {
      Charset var0 = null;

      try {
         var0 = encodingString == null ? lookupCharset(defaultEncoding) : lookupCharset(encodingString);
         encoder = var0 != null ? var0.newEncoder() : null;
      } catch (IllegalCharsetNameException var2) {
         throw new Error(var2);
      }
   }

   private String getMsg(String var1) {
      try {
         return rsrc.getString(var1);
      } catch (MissingResourceException var3) {
         throw new Error("Error in  message file format.");
      }
   }

   private String formatMsg(String var1, String var2) {
      String var3 = this.getMsg(var1);
      return MessageFormat.format(var3, var2);
   }

   public static void main(String[] var0) {
      Main var1 = new Main();
      System.exit(var1.convert(var0) ? 0 : 1);
   }

   static {
      try {
         rsrc = ResourceBundle.getBundle("sun.tools.native2ascii.resources.MsgNative2ascii");
      } catch (MissingResourceException var1) {
         throw new Error("Missing message file.");
      }
   }
}
