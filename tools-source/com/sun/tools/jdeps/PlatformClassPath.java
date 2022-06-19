package com.sun.tools.jdeps;

import com.sun.tools.classfile.Annotation;
import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.Dependencies;
import com.sun.tools.classfile.RuntimeAnnotations_attribute;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PlatformClassPath {
   private static final List NON_PLATFORM_JARFILES = Arrays.asList("alt-rt.jar", "ant-javafx.jar", "javafx-mx.jar");
   private static final List javaHomeArchives = init();

   static List getArchives() {
      return javaHomeArchives;
   }

   private static List init() {
      ArrayList var0 = new ArrayList();
      Path var1 = Paths.get(System.getProperty("java.home"));

      try {
         Path var2;
         if (var1.endsWith("jre")) {
            var0.addAll(addJarFiles(var1.resolve("lib")));
            if (var1.getParent() != null) {
               var2 = var1.getParent().resolve("lib");
               if (Files.exists(var2, new LinkOption[0])) {
                  var0.addAll(addJarFiles(var2));
               }
            }
         } else {
            if (!Files.exists(var1.resolve("lib"), new LinkOption[0])) {
               throw new RuntimeException("\"" + var1 + "\" not a JDK home");
            }

            var2 = var1.resolve("classes");
            if (Files.isDirectory(var2, new LinkOption[0])) {
               var0.add(new JDKArchive(var2));
            }

            var0.addAll(addJarFiles(var1.resolve("lib")));
         }

         return var0;
      } catch (IOException var3) {
         throw new Error(var3);
      }
   }

   private static List addJarFiles(final Path var0) throws IOException {
      final ArrayList var1 = new ArrayList();
      final Path var2 = var0.resolve("ext");
      Files.walkFileTree(var0, new SimpleFileVisitor() {
         public FileVisitResult preVisitDirectory(Path var1x, BasicFileAttributes var2x) throws IOException {
            return !var1x.equals(var0) && !var1x.equals(var2) ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
         }

         public FileVisitResult visitFile(Path var1x, BasicFileAttributes var2x) throws IOException {
            String var3 = var1x.getFileName().toString();
            if (var3.endsWith(".jar")) {
               var1.add(PlatformClassPath.NON_PLATFORM_JARFILES.contains(var3) ? Archive.getInstance(var1x) : new JDKArchive(var1x));
            }

            return FileVisitResult.CONTINUE;
         }
      });
      return var1;
   }

   static class JDKArchive extends Archive {
      private static List PROFILE_JARS = Arrays.asList("rt.jar", "jce.jar");
      private static List EXPORTED_PACKAGES = Arrays.asList("javax.jnlp", "org.w3c.dom.css", "org.w3c.dom.html", "org.w3c.dom.stylesheets", "org.w3c.dom.xpath");
      private final Map exportedPackages = new HashMap();
      private final Map exportedTypes = new HashMap();
      private static final String JDK_EXPORTED_ANNOTATION = "Ljdk/Exported;";

      public static boolean isProfileArchive(Archive var0) {
         return var0 instanceof JDKArchive ? PROFILE_JARS.contains(var0.getName()) : false;
      }

      JDKArchive(Path var1) throws IOException {
         super(var1, ClassFileReader.newInstance(var1));
      }

      public boolean isExported(String var1) {
         int var2 = var1.lastIndexOf(46);
         String var3 = var2 > 0 ? var1.substring(0, var2) : "";
         boolean var4 = this.isExportedPackage(var3);
         return this.exportedTypes.containsKey(var1) ? (Boolean)this.exportedTypes.get(var1) : var4;
      }

      public boolean isExportedPackage(String var1) {
         if (Profile.getProfile(var1) != null) {
            return true;
         } else if (!EXPORTED_PACKAGES.contains(var1) && !var1.startsWith("javafx.")) {
            return this.exportedPackages.containsKey(var1) ? (Boolean)this.exportedPackages.get(var1) : false;
         } else {
            return true;
         }
      }

      private Boolean isJdkExported(ClassFile var1) throws ConstantPoolException {
         RuntimeAnnotations_attribute var2 = (RuntimeAnnotations_attribute)var1.attributes.get("RuntimeVisibleAnnotations");
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.annotations.length; ++var3) {
               Annotation var4 = var2.annotations[var3];
               String var5 = var1.constant_pool.getUTF8Value(var4.type_index);
               if ("Ljdk/Exported;".equals(var5)) {
                  boolean var6 = true;

                  for(int var7 = 0; var7 < var4.num_element_value_pairs; ++var7) {
                     Annotation.element_value_pair var8 = var4.element_value_pairs[var7];
                     Annotation.Primitive_element_value var9 = (Annotation.Primitive_element_value)var8.value;
                     ConstantPool.CONSTANT_Integer_info var10 = (ConstantPool.CONSTANT_Integer_info)var1.constant_pool.get(var9.const_value_index);
                     var6 = var10.value != 0;
                  }

                  return var6;
               }
            }
         }

         return null;
      }

      void processJdkExported(ClassFile var1) throws IOException {
         try {
            String var2 = var1.getName();
            String var3 = var2.substring(0, var2.lastIndexOf(47)).replace('/', '.');
            Boolean var4 = this.isJdkExported(var1);
            if (var4 != null) {
               this.exportedTypes.put(var2.replace('/', '.'), var4);
            }

            if (!this.exportedPackages.containsKey(var3)) {
               Boolean var5 = null;
               ClassFile var6 = this.reader().getClassFile(var2.substring(0, var2.lastIndexOf(47) + 1) + "package-info");
               if (var6 != null) {
                  var5 = this.isJdkExported(var6);
               }

               if (var5 != null) {
                  this.exportedPackages.put(var3, var5);
               }
            }

         } catch (ConstantPoolException var7) {
            throw new Dependencies.ClassFileError(var7);
         }
      }
   }
}
