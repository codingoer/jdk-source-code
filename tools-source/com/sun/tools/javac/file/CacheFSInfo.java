package com.sun.tools.javac.file;

import com.sun.tools.javac.util.Context;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheFSInfo extends FSInfo {
   private Map cache = new ConcurrentHashMap();

   public static void preRegister(Context var0) {
      var0.put(FSInfo.class, new Context.Factory() {
         public FSInfo make(Context var1) {
            CacheFSInfo var2 = new CacheFSInfo();
            var1.put((Class)FSInfo.class, (Object)var2);
            return var2;
         }
      });
   }

   public void clearCache() {
      this.cache.clear();
   }

   public File getCanonicalFile(File var1) {
      Entry var2 = this.getEntry(var1);
      return var2.canonicalFile;
   }

   public boolean exists(File var1) {
      Entry var2 = this.getEntry(var1);
      return var2.exists;
   }

   public boolean isDirectory(File var1) {
      Entry var2 = this.getEntry(var1);
      return var2.isDirectory;
   }

   public boolean isFile(File var1) {
      Entry var2 = this.getEntry(var1);
      return var2.isFile;
   }

   public List getJarClassPath(File var1) throws IOException {
      Entry var2 = this.getEntry(var1);
      if (var2.jarClassPath == null) {
         var2.jarClassPath = super.getJarClassPath(var1);
      }

      return var2.jarClassPath;
   }

   private Entry getEntry(File var1) {
      Entry var2 = (Entry)this.cache.get(var1);
      if (var2 == null) {
         var2 = new Entry();
         var2.canonicalFile = super.getCanonicalFile(var1);
         var2.exists = super.exists(var1);
         var2.isDirectory = super.isDirectory(var1);
         var2.isFile = super.isFile(var1);
         this.cache.put(var1, var2);
      }

      return var2;
   }

   private static class Entry {
      File canonicalFile;
      boolean exists;
      boolean isFile;
      boolean isDirectory;
      List jarClassPath;

      private Entry() {
      }

      // $FF: synthetic method
      Entry(Object var1) {
         this();
      }
   }
}
