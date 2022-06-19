package com.sun.tools.extcheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import sun.net.www.ParseUtil;

public class ExtCheck {
   private static final boolean DEBUG = false;
   private String targetSpecTitle;
   private String targetSpecVersion;
   private String targetSpecVendor;
   private String targetImplTitle;
   private String targetImplVersion;
   private String targetImplVendor;
   private String targetsealed;
   private boolean verboseFlag;

   static ExtCheck create(File var0, boolean var1) {
      return new ExtCheck(var0, var1);
   }

   private ExtCheck(File var1, boolean var2) {
      this.verboseFlag = var2;
      this.investigateTarget(var1);
   }

   private void investigateTarget(File var1) {
      this.verboseMessage("Target file:" + var1);
      Manifest var2 = null;

      try {
         File var3 = new File(var1.getCanonicalPath());
         URL var4 = ParseUtil.fileToEncodedURL(var3);
         if (var4 != null) {
            JarLoader var5 = new JarLoader(var4);
            JarFile var6 = var5.getJarFile();
            var2 = var6.getManifest();
         }
      } catch (MalformedURLException var7) {
         error("Malformed URL ");
      } catch (IOException var8) {
         error("IO Exception ");
      }

      if (var2 == null) {
         error("No manifest available in " + var1);
      }

      Attributes var9 = var2.getMainAttributes();
      if (var9 != null) {
         this.targetSpecTitle = var9.getValue(Name.SPECIFICATION_TITLE);
         this.targetSpecVersion = var9.getValue(Name.SPECIFICATION_VERSION);
         this.targetSpecVendor = var9.getValue(Name.SPECIFICATION_VENDOR);
         this.targetImplTitle = var9.getValue(Name.IMPLEMENTATION_TITLE);
         this.targetImplVersion = var9.getValue(Name.IMPLEMENTATION_VERSION);
         this.targetImplVendor = var9.getValue(Name.IMPLEMENTATION_VENDOR);
         this.targetsealed = var9.getValue(Name.SEALED);
      } else {
         error("No attributes available in the manifest");
      }

      if (this.targetSpecTitle == null) {
         error("The target file does not have a specification title");
      }

      if (this.targetSpecVersion == null) {
         error("The target file does not have a specification version");
      }

      this.verboseMessage("Specification title:" + this.targetSpecTitle);
      this.verboseMessage("Specification version:" + this.targetSpecVersion);
      if (this.targetSpecVendor != null) {
         this.verboseMessage("Specification vendor:" + this.targetSpecVendor);
      }

      if (this.targetImplVersion != null) {
         this.verboseMessage("Implementation version:" + this.targetImplVersion);
      }

      if (this.targetImplVendor != null) {
         this.verboseMessage("Implementation vendor:" + this.targetImplVendor);
      }

      this.verboseMessage("");
   }

   boolean checkInstalledAgainstTarget() {
      String var1 = System.getProperty("java.ext.dirs");
      File[] var2;
      int var4;
      if (var1 != null) {
         StringTokenizer var3 = new StringTokenizer(var1, File.pathSeparator);
         var4 = var3.countTokens();
         var2 = new File[var4];

         for(int var5 = 0; var5 < var4; ++var5) {
            var2[var5] = new File(var3.nextToken());
         }
      } else {
         var2 = new File[0];
      }

      boolean var12 = true;

      for(var4 = 0; var4 < var2.length; ++var4) {
         String[] var13 = var2[var4].list();
         if (var13 != null) {
            for(int var6 = 0; var6 < var13.length; ++var6) {
               try {
                  File var7 = new File(var2[var4], var13[var6]);
                  File var8 = new File(var7.getCanonicalPath());
                  URL var9 = ParseUtil.fileToEncodedURL(var8);
                  if (var9 != null) {
                     var12 = var12 && this.checkURLRecursively(1, var9);
                  }
               } catch (MalformedURLException var10) {
                  error("Malformed URL");
               } catch (IOException var11) {
                  error("IO Exception");
               }
            }
         }
      }

      if (var12) {
         this.generalMessage("No conflicting installed jar found.");
      } else {
         this.generalMessage("Conflicting installed jar found.  Use -verbose for more information.");
      }

      return var12;
   }

   private boolean checkURLRecursively(int var1, URL var2) throws IOException {
      this.verboseMessage("Comparing with " + var2);
      JarLoader var3 = new JarLoader(var2);
      JarFile var4 = var3.getJarFile();
      Manifest var5 = var4.getManifest();
      if (var5 != null) {
         Attributes var6 = var5.getMainAttributes();
         if (var6 != null) {
            String var7 = var6.getValue(Name.SPECIFICATION_TITLE);
            String var8 = var6.getValue(Name.SPECIFICATION_VERSION);
            String var9 = var6.getValue(Name.SPECIFICATION_VENDOR);
            String var10 = var6.getValue(Name.IMPLEMENTATION_TITLE);
            String var11 = var6.getValue(Name.IMPLEMENTATION_VERSION);
            String var12 = var6.getValue(Name.IMPLEMENTATION_VENDOR);
            String var13 = var6.getValue(Name.SEALED);
            if (var7 != null && var7.equals(this.targetSpecTitle) && var8 != null && (var8.equals(this.targetSpecVersion) || this.isNotOlderThan(var8, this.targetSpecVersion))) {
               this.verboseMessage("");
               this.verboseMessage("CONFLICT DETECTED ");
               this.verboseMessage("Conflicting file:" + var2);
               this.verboseMessage("Installed Version:" + var8);
               if (var10 != null) {
                  this.verboseMessage("Implementation Title:" + var10);
               }

               if (var11 != null) {
                  this.verboseMessage("Implementation Version:" + var11);
               }

               if (var12 != null) {
                  this.verboseMessage("Implementation Vendor:" + var12);
               }

               return false;
            }
         }
      }

      boolean var14 = true;
      URL[] var15 = var3.getClassPath();
      if (var15 != null) {
         for(int var16 = 0; var16 < var15.length; ++var16) {
            if (var2 != null) {
               boolean var17 = this.checkURLRecursively(var1 + 1, var15[var16]);
               var14 = var17 && var14;
            }
         }
      }

      return var14;
   }

   private boolean isNotOlderThan(String var1, String var2) throws NumberFormatException {
      if (var1 != null && var1.length() >= 1) {
         StringTokenizer var3 = new StringTokenizer(var2, ".", true);
         StringTokenizer var4 = new StringTokenizer(var1, ".", true);

         while(var3.hasMoreTokens() || var4.hasMoreTokens()) {
            int var5;
            if (var3.hasMoreTokens()) {
               var5 = Integer.parseInt(var3.nextToken());
            } else {
               var5 = 0;
            }

            int var6;
            if (var4.hasMoreTokens()) {
               var6 = Integer.parseInt(var4.nextToken());
            } else {
               var6 = 0;
            }

            if (var6 < var5) {
               return false;
            }

            if (var6 > var5) {
               return true;
            }

            if (var3.hasMoreTokens()) {
               var3.nextToken();
            }

            if (var4.hasMoreTokens()) {
               var4.nextToken();
            }
         }

         return true;
      } else {
         throw new NumberFormatException("Empty version string");
      }
   }

   void verboseMessage(String var1) {
      if (this.verboseFlag) {
         System.err.println(var1);
      }

   }

   void generalMessage(String var1) {
      System.err.println(var1);
   }

   static void error(String var0) throws RuntimeException {
      throw new RuntimeException(var0);
   }

   private static class JarLoader {
      private final URL base;
      private JarFile jar;
      private URL csu;

      JarLoader(URL var1) {
         String var2 = var1 + "!/";
         URL var3 = null;

         try {
            var3 = new URL("jar", "", var2);
            this.jar = this.findJarFile(var1);
            this.csu = var1;
         } catch (MalformedURLException var5) {
            ExtCheck.error("Malformed url " + var2);
         } catch (IOException var6) {
            ExtCheck.error("IO Exception occurred");
         }

         this.base = var3;
      }

      URL getBaseURL() {
         return this.base;
      }

      JarFile getJarFile() {
         return this.jar;
      }

      private JarFile findJarFile(URL var1) throws IOException {
         if ("file".equals(var1.getProtocol())) {
            String var4 = var1.getFile().replace('/', File.separatorChar);
            File var3 = new File(var4);
            if (!var3.exists()) {
               throw new FileNotFoundException(var4);
            } else {
               return new JarFile(var4);
            }
         } else {
            URLConnection var2 = this.getBaseURL().openConnection();
            return ((JarURLConnection)var2).getJarFile();
         }
      }

      URL[] getClassPath() throws IOException {
         Manifest var1 = this.jar.getManifest();
         if (var1 != null) {
            Attributes var2 = var1.getMainAttributes();
            if (var2 != null) {
               String var3 = var2.getValue(Name.CLASS_PATH);
               if (var3 != null) {
                  return this.parseClassPath(this.csu, var3);
               }
            }
         }

         return null;
      }

      private URL[] parseClassPath(URL var1, String var2) throws MalformedURLException {
         StringTokenizer var3 = new StringTokenizer(var2);
         URL[] var4 = new URL[var3.countTokens()];

         for(int var5 = 0; var3.hasMoreTokens(); ++var5) {
            String var6 = var3.nextToken();
            var4[var5] = new URL(var1, var6);
         }

         return var4;
      }
   }
}
