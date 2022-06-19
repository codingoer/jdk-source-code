package com.sun.tools.jdeps;

import com.sun.tools.classfile.Annotation;
import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.RuntimeAnnotations_attribute;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarFile;

enum Profile {
   COMPACT1("compact1", 1),
   COMPACT2("compact2", 2),
   COMPACT3("compact3", 3),
   FULL_JRE("Full JRE", 4);

   final String name;
   final int profile;
   final Set packages;
   final Set proprietaryPkgs;

   private Profile(String var3, int var4) {
      this.name = var3;
      this.profile = var4;
      this.packages = new HashSet();
      this.proprietaryPkgs = new HashSet();
   }

   public String profileName() {
      return this.name;
   }

   public static int getProfileCount() {
      return Profile.PackageToProfile.map.values().size();
   }

   public static Profile getProfile(String var0) {
      Profile var1 = (Profile)Profile.PackageToProfile.map.get(var0);
      return var1 != null && var1.packages.contains(var0) ? var1 : null;
   }

   public static void main(String[] var0) {
      int var2;
      int var3;
      if (var0.length == 0) {
         if (getProfileCount() == 0) {
            System.err.println("No profile is present in this JDK");
         }

         Profile[] var1 = values();
         var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            Profile var4 = var1[var3];
            String var5 = var4.name;
            TreeSet var6 = new TreeSet(var4.packages);
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               String var8 = (String)var7.next();
               if (Profile.PackageToProfile.map.get(var8) == var4) {
                  System.out.format("%2d: %-10s  %s%n", var4.profile, var5, var8);
                  var5 = "";
               } else {
                  System.err.format("Split package: %s in %s and %s %n", var8, ((Profile)Profile.PackageToProfile.map.get(var8)).name, var4.name);
               }
            }
         }
      }

      String[] var9 = var0;
      var2 = var0.length;

      for(var3 = 0; var3 < var2; ++var3) {
         String var10 = var9[var3];
         System.out.format("%s in %s%n", var10, getProfile(var10));
      }

   }

   static class PackageToProfile {
      static String[] JAVAX_CRYPTO_PKGS = new String[]{"javax.crypto", "javax.crypto.interfaces", "javax.crypto.spec"};
      static Map map = initProfiles();
      private static final String PROFILE_ANNOTATION = "Ljdk/Profile+Annotation;";
      private static final String PROPRIETARY_ANNOTATION = "Lsun/Proprietary+Annotation;";

      private static Map initProfiles() {
         try {
            String var0 = System.getProperty("jdeps.profiles");
            if (var0 != null) {
               initProfilesFromProperties(var0);
            } else {
               Path var1 = Paths.get(System.getProperty("java.home"));
               if (var1.endsWith("jre")) {
                  var1 = var1.getParent();
               }

               Path var2 = var1.resolve("lib").resolve("ct.sym");
               if (Files.exists(var2, new LinkOption[0])) {
                  JarFile var3 = new JarFile(var2.toFile());
                  Throwable var4 = null;

                  try {
                     ClassFileReader var5 = ClassFileReader.newInstance(var2, var3);
                     Iterator var6 = var5.getClassFiles().iterator();

                     while(var6.hasNext()) {
                        ClassFile var7 = (ClassFile)var6.next();
                        findProfile(var7);
                     }
                  } catch (Throwable var16) {
                     var4 = var16;
                     throw var16;
                  } finally {
                     if (var3 != null) {
                        if (var4 != null) {
                           try {
                              var3.close();
                           } catch (Throwable var15) {
                              var4.addSuppressed(var15);
                           }
                        } else {
                           var3.close();
                        }
                     }

                  }

                  Collections.addAll(Profile.COMPACT1.packages, JAVAX_CRYPTO_PKGS);
               }
            }
         } catch (ConstantPoolException | IOException var18) {
            throw new Error(var18);
         }

         HashMap var19 = new HashMap();
         Profile[] var20 = Profile.values();
         int var21 = var20.length;

         for(int var22 = 0; var22 < var21; ++var22) {
            Profile var23 = var20[var22];
            Iterator var24 = var23.packages.iterator();

            String var25;
            while(var24.hasNext()) {
               var25 = (String)var24.next();
               if (!var19.containsKey(var25)) {
                  var19.put(var25, var23);
               }
            }

            var24 = var23.proprietaryPkgs.iterator();

            while(var24.hasNext()) {
               var25 = (String)var24.next();
               if (!var19.containsKey(var25)) {
                  var19.put(var25, var23);
               }
            }
         }

         return var19;
      }

      private static Profile findProfile(ClassFile var0) throws ConstantPoolException {
         RuntimeAnnotations_attribute var1 = (RuntimeAnnotations_attribute)var0.attributes.get("RuntimeInvisibleAnnotations");
         int var2 = 0;
         boolean var3 = false;
         if (var1 != null) {
            for(int var4 = 0; var4 < var1.annotations.length; ++var4) {
               Annotation var5 = var1.annotations[var4];
               String var6 = var0.constant_pool.getUTF8Value(var5.type_index);
               if ("Ljdk/Profile+Annotation;".equals(var6)) {
                  byte var7 = 0;
                  if (var7 < var5.num_element_value_pairs) {
                     Annotation.element_value_pair var8 = var5.element_value_pairs[var7];
                     Annotation.Primitive_element_value var9 = (Annotation.Primitive_element_value)var8.value;
                     ConstantPool.CONSTANT_Integer_info var10 = (ConstantPool.CONSTANT_Integer_info)var0.constant_pool.get(var9.const_value_index);
                     var2 = var10.value;
                  }
               } else if ("Lsun/Proprietary+Annotation;".equals(var6)) {
                  var3 = true;
               }
            }
         }

         Profile var11 = null;
         switch (var2) {
            case 1:
               var11 = Profile.COMPACT1;
               break;
            case 2:
               var11 = Profile.COMPACT2;
               break;
            case 3:
               var11 = Profile.COMPACT3;
               break;
            case 4:
               var11 = Profile.FULL_JRE;
               break;
            default:
               return null;
         }

         String var12 = var0.getName();
         int var13 = var12.lastIndexOf(47);
         var12 = var13 > 0 ? var12.substring(0, var13).replace('/', '.') : "";
         if (var3) {
            var11.proprietaryPkgs.add(var12);
         } else {
            var11.packages.add(var12);
         }

         return var11;
      }

      private static void initProfilesFromProperties(String var0) throws IOException {
         Properties var1 = new Properties();
         FileReader var2 = new FileReader(var0);
         Throwable var3 = null;

         try {
            var1.load(var2);
         } catch (Throwable var19) {
            var3 = var19;
            throw var19;
         } finally {
            if (var2 != null) {
               if (var3 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var18) {
                     var3.addSuppressed(var18);
                  }
               } else {
                  var2.close();
               }
            }

         }

         Profile[] var21 = Profile.values();
         int var22 = var21.length;

         for(int var4 = 0; var4 < var22; ++var4) {
            Profile var5 = var21[var4];
            int var6 = var5.profile;
            String var7 = var1.getProperty("profile." + var6 + ".name");
            if (var7 == null) {
               throw new RuntimeException(var7 + " missing in " + var0);
            }

            String var8 = var1.getProperty("profile." + var6 + ".packages");
            String[] var9 = var8.split("\\s+");
            String[] var10 = var9;
            int var11 = var9.length;

            for(int var12 = 0; var12 < var11; ++var12) {
               String var13 = var10[var12];
               if (!var13.isEmpty()) {
                  var5.packages.add(var13);
               }
            }
         }

      }
   }
}
