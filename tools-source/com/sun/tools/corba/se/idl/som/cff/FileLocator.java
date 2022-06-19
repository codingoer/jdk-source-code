package com.sun.tools.corba.se.idl.som.cff;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public abstract class FileLocator {
   static final Properties pp = System.getProperties();
   static final String classPath;
   static final String pathSeparator;

   public static DataInputStream locateClassFile(String var0) throws FileNotFoundException, IOException {
      boolean var1 = true;
      String var3 = "";
      File var5 = null;
      StringTokenizer var2 = new StringTokenizer(classPath, pathSeparator, false);
      String var4 = var0.replace('.', File.separatorChar) + ".class";

      NamedDataInputStream var6;
      int var7;
      String var8;
      while(var2.hasMoreTokens() && var1) {
         try {
            var3 = var2.nextToken();
         } catch (NoSuchElementException var13) {
            break;
         }

         var7 = var3.length();
         var8 = var7 > 3 ? var3.substring(var7 - 4) : "";
         if (!var8.equalsIgnoreCase(".zip") && !var8.equalsIgnoreCase(".jar")) {
            try {
               var5 = new File(var3 + File.separator + var4);
            } catch (NullPointerException var12) {
               continue;
            }

            if (var5 != null && var5.exists()) {
               var1 = false;
            }
         } else {
            try {
               var6 = locateInZipFile(var3, var0, true, true);
               if (var6 != null) {
                  return var6;
               }
            } catch (ZipException var10) {
            } catch (IOException var11) {
            }
         }
      }

      if (var1) {
         var7 = var0.lastIndexOf(46);
         var8 = var7 >= 0 ? var0.substring(var7 + 1) : var0;
         var6 = new NamedDataInputStream(new BufferedInputStream(new FileInputStream(var8 + ".class")), var8 + ".class", false);
         return var6;
      } else {
         var6 = new NamedDataInputStream(new BufferedInputStream(new FileInputStream(var5)), var3 + File.separator + var4, false);
         return var6;
      }
   }

   public static DataInputStream locateLocaleSpecificFileInClassPath(String var0) throws FileNotFoundException, IOException {
      String var1 = "_" + Locale.getDefault().toString();
      int var2 = var0.lastIndexOf(47);
      int var3 = var0.lastIndexOf(46);
      DataInputStream var6 = null;
      boolean var7 = false;
      String var4;
      String var5;
      if (var3 > 0 && var3 > var2) {
         var4 = var0.substring(0, var3);
         var5 = var0.substring(var3);
      } else {
         var4 = var0;
         var5 = "";
      }

      while(true) {
         if (var7) {
            var6 = locateFileInClassPath(var0);
         } else {
            try {
               var6 = locateFileInClassPath(var4 + var1 + var5);
            } catch (Exception var9) {
            }
         }

         if (var6 != null || var7) {
            return var6;
         }

         int var8 = var1.lastIndexOf(95);
         if (var8 > 0) {
            var1 = var1.substring(0, var8);
         } else {
            var7 = true;
         }
      }
   }

   public static DataInputStream locateFileInClassPath(String var0) throws FileNotFoundException, IOException {
      boolean var1 = true;
      String var3 = "";
      File var4 = null;
      String var6 = File.separatorChar == '/' ? var0 : var0.replace(File.separatorChar, '/');
      String var7 = File.separatorChar == '/' ? var0 : var0.replace('/', File.separatorChar);
      StringTokenizer var2 = new StringTokenizer(classPath, pathSeparator, false);

      NamedDataInputStream var5;
      int var8;
      String var9;
      while(var2.hasMoreTokens() && var1) {
         try {
            var3 = var2.nextToken();
         } catch (NoSuchElementException var14) {
            break;
         }

         var8 = var3.length();
         var9 = var8 > 3 ? var3.substring(var8 - 4) : "";
         if (!var9.equalsIgnoreCase(".zip") && !var9.equalsIgnoreCase(".jar")) {
            try {
               var4 = new File(var3 + File.separator + var7);
            } catch (NullPointerException var13) {
               continue;
            }

            if (var4 != null && var4.exists()) {
               var1 = false;
            }
         } else {
            try {
               var5 = locateInZipFile(var3, var6, false, false);
               if (var5 != null) {
                  return var5;
               }
            } catch (ZipException var11) {
            } catch (IOException var12) {
            }
         }
      }

      if (var1) {
         var8 = var7.lastIndexOf(File.separator);
         var9 = var8 >= 0 ? var7.substring(var8 + 1) : var7;
         var5 = new NamedDataInputStream(new BufferedInputStream(new FileInputStream(var9)), var9, false);
         return var5;
      } else {
         var5 = new NamedDataInputStream(new BufferedInputStream(new FileInputStream(var4)), var3 + File.separator + var7, false);
         return var5;
      }
   }

   public static String getFileNameFromStream(DataInputStream var0) {
      return var0 instanceof NamedDataInputStream ? ((NamedDataInputStream)var0).fullyQualifiedFileName : "";
   }

   public static boolean isZipFileAssociatedWithStream(DataInputStream var0) {
      return var0 instanceof NamedDataInputStream ? ((NamedDataInputStream)var0).inZipFile : false;
   }

   private static NamedDataInputStream locateInZipFile(String var0, String var1, boolean var2, boolean var3) throws ZipException, IOException {
      ZipFile var4 = new ZipFile(var0);
      if (var4 == null) {
         return null;
      } else {
         String var6 = var2 ? var1.replace('.', '/') + ".class" : var1;
         ZipEntry var5 = var4.getEntry(var6);
         if (var5 == null) {
            var4.close();
            var4 = null;
            return null;
         } else {
            Object var7 = var4.getInputStream(var5);
            if (var3) {
               var7 = new BufferedInputStream((InputStream)var7);
            }

            return new NamedDataInputStream((InputStream)var7, var0 + '(' + var6 + ')', true);
         }
      }
   }

   static {
      classPath = pp.getProperty("java.class.path", ".");
      pathSeparator = pp.getProperty("path.separator", ";");
   }
}
