package com.sun.tools.javac.file;

import com.sun.tools.javac.util.Context;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;

public class FSInfo {
   public static FSInfo instance(Context var0) {
      FSInfo var1 = (FSInfo)var0.get(FSInfo.class);
      if (var1 == null) {
         var1 = new FSInfo();
      }

      return var1;
   }

   protected FSInfo() {
   }

   protected FSInfo(Context var1) {
      var1.put((Class)FSInfo.class, (Object)this);
   }

   public File getCanonicalFile(File var1) {
      try {
         return var1.getCanonicalFile();
      } catch (IOException var3) {
         return var1.getAbsoluteFile();
      }
   }

   public boolean exists(File var1) {
      return var1.exists();
   }

   public boolean isDirectory(File var1) {
      return var1.isDirectory();
   }

   public boolean isFile(File var1) {
      return var1.isFile();
   }

   public List getJarClassPath(File var1) throws IOException {
      String var2 = var1.getParent();
      JarFile var3 = new JarFile(var1);

      List var5;
      try {
         Manifest var4 = var3.getManifest();
         if (var4 != null) {
            Attributes var14 = var4.getMainAttributes();
            if (var14 == null) {
               List var15 = Collections.emptyList();
               return var15;
            }

            String var6 = var14.getValue(Name.CLASS_PATH);
            if (var6 == null) {
               List var16 = Collections.emptyList();
               return var16;
            }

            ArrayList var7 = new ArrayList();
            StringTokenizer var8 = new StringTokenizer(var6);

            while(var8.hasMoreTokens()) {
               String var9 = var8.nextToken();
               File var10 = var2 == null ? new File(var9) : new File(var2, var9);
               var7.add(var10);
            }

            ArrayList var17 = var7;
            return var17;
         }

         var5 = Collections.emptyList();
      } finally {
         var3.close();
      }

      return var5;
   }
}
