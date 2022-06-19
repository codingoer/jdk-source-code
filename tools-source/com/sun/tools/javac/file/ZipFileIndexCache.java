package com.sun.tools.javac.file;

import com.sun.tools.javac.util.Context;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ZipFileIndexCache {
   private final Map map = new HashMap();
   private static ZipFileIndexCache sharedInstance;

   public static synchronized ZipFileIndexCache getSharedInstance() {
      if (sharedInstance == null) {
         sharedInstance = new ZipFileIndexCache();
      }

      return sharedInstance;
   }

   public static ZipFileIndexCache instance(Context var0) {
      ZipFileIndexCache var1 = (ZipFileIndexCache)var0.get(ZipFileIndexCache.class);
      if (var1 == null) {
         var0.put((Class)ZipFileIndexCache.class, (Object)(var1 = new ZipFileIndexCache()));
      }

      return var1;
   }

   public List getZipFileIndexes() {
      return this.getZipFileIndexes(false);
   }

   public synchronized List getZipFileIndexes(boolean var1) {
      ArrayList var2 = new ArrayList();
      var2.addAll(this.map.values());
      if (var1) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            ZipFileIndex var4 = (ZipFileIndex)var3.next();
            if (!var4.isOpen()) {
               var2.remove(var4);
            }
         }
      }

      return var2;
   }

   public synchronized ZipFileIndex getZipFileIndex(File var1, RelativePath.RelativeDirectory var2, boolean var3, String var4, boolean var5) throws IOException {
      ZipFileIndex var6 = this.getExistingZipIndex(var1);
      if (var6 == null || var6 != null && var1.lastModified() != var6.zipFileLastModified) {
         var6 = new ZipFileIndex(var1, var2, var5, var3, var4);
         this.map.put(var1, var6);
      }

      return var6;
   }

   public synchronized ZipFileIndex getExistingZipIndex(File var1) {
      return (ZipFileIndex)this.map.get(var1);
   }

   public synchronized void clearCache() {
      this.map.clear();
   }

   public synchronized void clearCache(long var1) {
      Iterator var3 = this.map.keySet().iterator();

      while(true) {
         File var4;
         ZipFileIndex var5;
         long var6;
         do {
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (File)var3.next();
               var5 = (ZipFileIndex)this.map.get(var4);
            } while(var5 == null);

            var6 = var5.lastReferenceTimeStamp + var1;
         } while(var6 >= var5.lastReferenceTimeStamp && System.currentTimeMillis() <= var6);

         this.map.remove(var4);
      }
   }

   public synchronized void removeFromCache(File var1) {
      this.map.remove(var1);
   }

   public synchronized void setOpenedIndexes(List var1) throws IllegalStateException {
      if (this.map.isEmpty()) {
         String var4 = "Setting opened indexes should be called only when the ZipFileCache is empty. Call JavacFileManager.flush() before calling this method.";
         throw new IllegalStateException(var4);
      } else {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            ZipFileIndex var3 = (ZipFileIndex)var2.next();
            this.map.put(var3.zipFile, var3);
         }

      }
   }
}
