package sun.tools.java;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ClassPath {
   static final char dirSeparator;
   String pathstr;
   private ClassPathEntry[] path;
   private final String fileSeparatorChar;

   public ClassPath(String var1) {
      this.fileSeparatorChar = "" + File.separatorChar;
      this.init(var1);
   }

   public ClassPath(String[] var1) {
      this.fileSeparatorChar = "" + File.separatorChar;
      this.init(var1);
   }

   public ClassPath() {
      this.fileSeparatorChar = "" + File.separatorChar;
      String var1 = System.getProperty("sun.boot.class.path");
      String var2 = System.getProperty("env.class.path");
      if (var2 == null) {
         var2 = ".";
      }

      String var3 = var1 + File.pathSeparator + var2;
      this.init(var3);
   }

   private void init(String var1) {
      this.pathstr = var1;
      if (var1.length() == 0) {
         this.path = new ClassPathEntry[0];
      }

      int var4 = 0;

      int var2;
      for(var2 = 0; (var2 = var1.indexOf(dirSeparator, var2)) != -1; ++var2) {
         ++var4;
      }

      ClassPathEntry[] var5 = new ClassPathEntry[var4 + 1];
      int var6 = var1.length();
      var4 = 0;

      int var3;
      for(var2 = 0; var2 < var6; var2 = var3 + 1) {
         if ((var3 = var1.indexOf(dirSeparator, var2)) == -1) {
            var3 = var6;
         }

         if (var2 == var3) {
            var5[var4] = new ClassPathEntry();
            var5[var4++].dir = new File(".");
         } else {
            File var7 = new File(var1.substring(var2, var3));
            if (var7.isFile()) {
               try {
                  ZipFile var8 = new ZipFile(var7);
                  var5[var4] = new ClassPathEntry();
                  var5[var4++].zip = var8;
               } catch (ZipException var9) {
               } catch (IOException var10) {
               }
            } else {
               var5[var4] = new ClassPathEntry();
               var5[var4++].dir = var7;
            }
         }
      }

      this.path = new ClassPathEntry[var4];
      System.arraycopy(var5, 0, this.path, 0, var4);
   }

   private void init(String[] var1) {
      int var3;
      if (var1.length == 0) {
         this.pathstr = "";
      } else {
         StringBuilder var2 = new StringBuilder(var1[0]);

         for(var3 = 1; var3 < var1.length; ++var3) {
            var2.append(File.pathSeparatorChar);
            var2.append(var1[var3]);
         }

         this.pathstr = var2.toString();
      }

      ClassPathEntry[] var12 = new ClassPathEntry[var1.length];
      var3 = 0;
      String[] var4 = var1;
      int var5 = var1.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         File var8 = new File(var7);
         if (var8.isFile()) {
            try {
               ZipFile var9 = new ZipFile(var8);
               var12[var3] = new ClassPathEntry();
               var12[var3++].zip = var9;
            } catch (ZipException var10) {
            } catch (IOException var11) {
            }
         } else {
            var12[var3] = new ClassPathEntry();
            var12[var3++].dir = var8;
         }
      }

      this.path = new ClassPathEntry[var3];
      System.arraycopy(var12, 0, this.path, 0, var3);
   }

   public ClassFile getDirectory(String var1) {
      return this.getFile(var1, true);
   }

   public ClassFile getFile(String var1) {
      return this.getFile(var1, false);
   }

   private ClassFile getFile(String var1, boolean var2) {
      String var3 = var1;
      String var4 = "";
      int var5;
      if (!var2) {
         var5 = var1.lastIndexOf(File.separatorChar);
         var3 = var1.substring(0, var5 + 1);
         var4 = var1.substring(var5 + 1);
      } else if (!var1.equals("") && !var1.endsWith(this.fileSeparatorChar)) {
         var3 = var1 + File.separatorChar;
         var1 = var3;
      }

      for(var5 = 0; var5 < this.path.length; ++var5) {
         if (this.path[var5].zip != null) {
            String var9 = var1.replace(File.separatorChar, '/');
            ZipEntry var10 = this.path[var5].zip.getEntry(var9);
            if (var10 != null) {
               return new ClassFile(this.path[var5].zip, var10);
            }
         } else {
            File var6 = new File(this.path[var5].dir.getPath(), var1);
            String[] var7 = this.path[var5].getFiles(var3);
            if (var2) {
               if (var7.length > 0) {
                  return new ClassFile(var6);
               }
            } else {
               for(int var8 = 0; var8 < var7.length; ++var8) {
                  if (var4.equals(var7[var8])) {
                     return new ClassFile(var6);
                  }
               }
            }
         }
      }

      return null;
   }

   public Enumeration getFiles(String var1, String var2) {
      Hashtable var3 = new Hashtable();
      int var4 = this.path.length;

      while(true) {
         while(true) {
            --var4;
            if (var4 < 0) {
               return var3.elements();
            }

            String var7;
            if (this.path[var4].zip != null) {
               Enumeration var9 = this.path[var4].zip.entries();

               while(var9.hasMoreElements()) {
                  ZipEntry var10 = (ZipEntry)var9.nextElement();
                  var7 = var10.getName();
                  var7 = var7.replace('/', File.separatorChar);
                  if (var7.startsWith(var1) && var7.endsWith(var2)) {
                     var3.put(var7, new ClassFile(this.path[var4].zip, var10));
                  }
               }
            } else {
               String[] var5 = this.path[var4].getFiles(var1);

               for(int var6 = 0; var6 < var5.length; ++var6) {
                  var7 = var5[var6];
                  if (var7.endsWith(var2)) {
                     var7 = var1 + File.separatorChar + var7;
                     File var8 = new File(this.path[var4].dir.getPath(), var7);
                     var3.put(var7, new ClassFile(var8));
                  }
               }
            }
         }
      }
   }

   public void close() throws IOException {
      int var1 = this.path.length;

      while(true) {
         --var1;
         if (var1 < 0) {
            return;
         }

         if (this.path[var1].zip != null) {
            this.path[var1].zip.close();
         }
      }
   }

   public String toString() {
      return this.pathstr;
   }

   static {
      dirSeparator = File.pathSeparatorChar;
   }
}
