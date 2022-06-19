package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.util.Map;
import java.util.WeakHashMap;
import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;

abstract class DocFileFactory {
   private static final Map factories = new WeakHashMap();
   protected Configuration configuration;

   static synchronized DocFileFactory getFactory(Configuration var0) {
      Object var1 = (DocFileFactory)factories.get(var0);
      if (var1 == null) {
         JavaFileManager var2 = var0.getFileManager();
         if (var2 instanceof StandardJavaFileManager) {
            var1 = new StandardDocFileFactory(var0);
         } else {
            try {
               Class var3 = Class.forName("com.sun.tools.javac.nio.PathFileManager");
               if (var3.isAssignableFrom(var2.getClass())) {
                  var1 = new PathDocFileFactory(var0);
               }
            } catch (Throwable var4) {
               throw new IllegalStateException(var4);
            }
         }

         factories.put(var0, var1);
      }

      return (DocFileFactory)var1;
   }

   protected DocFileFactory(Configuration var1) {
      this.configuration = var1;
   }

   abstract DocFile createFileForDirectory(String var1);

   abstract DocFile createFileForInput(String var1);

   abstract DocFile createFileForOutput(DocPath var1);

   abstract Iterable list(JavaFileManager.Location var1, DocPath var2);
}
