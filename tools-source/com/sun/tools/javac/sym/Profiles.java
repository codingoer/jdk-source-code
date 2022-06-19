package com.sun.tools.javac.sym;

import com.sun.tools.javac.util.Assert;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public abstract class Profiles {
   public static void main(String[] var0) throws IOException {
      Profiles var1 = read(new File(var0[0]));
      if (var0.length >= 2) {
         TreeMap var2 = new TreeMap();

         for(int var3 = 1; var3 <= 4; ++var3) {
            var2.put(var3, new TreeSet());
         }

         File var12 = new File(var0[1]);
         Iterator var4 = Files.readAllLines(var12.toPath(), Charset.defaultCharset()).iterator();

         label114:
         while(true) {
            String var5;
            do {
               if (!var4.hasNext()) {
                  for(int var13 = 1; var13 <= 4; ++var13) {
                     BufferedWriter var14 = new BufferedWriter(new FileWriter(var13 + ".txt"));

                     try {
                        Iterator var15 = ((Set)var2.get(var13)).iterator();

                        while(var15.hasNext()) {
                           String var16 = (String)var15.next();
                           var14.write(var16);
                           var14.newLine();
                        }
                     } finally {
                        var14.close();
                     }
                  }
                  break label114;
               }

               var5 = (String)var4.next();
            } while(!var5.endsWith(".class"));

            String var6 = var5.substring(0, var5.length() - 6);
            int var7 = var1.getProfile(var6);

            for(int var8 = var7; var8 <= 4; ++var8) {
               ((Set)var2.get(var8)).add(var6);
            }
         }
      }

   }

   public static Profiles read(File var0) throws IOException {
      BufferedInputStream var1 = new BufferedInputStream(new FileInputStream(var0));

      SimpleProfiles var3;
      try {
         Properties var2 = new Properties();
         var2.load(var1);
         if (!var2.containsKey("java/lang/Object")) {
            MakefileProfiles var7 = new MakefileProfiles(var2);
            return var7;
         }

         var3 = new SimpleProfiles(var2);
      } finally {
         var1.close();
      }

      return var3;
   }

   public abstract int getProfileCount();

   public abstract int getProfile(String var1);

   public abstract Set getPackages(int var1);

   private static class SimpleProfiles extends Profiles {
      private final Map map;
      private final int profileCount;

      SimpleProfiles(Properties var1) {
         int var2 = 0;
         this.map = new HashMap();

         int var6;
         for(Iterator var3 = var1.entrySet().iterator(); var3.hasNext(); var2 = Math.max(var2, var6)) {
            Map.Entry var4 = (Map.Entry)var3.next();
            String var5 = (String)var4.getKey();
            var6 = Integer.valueOf((String)var4.getValue());
            this.map.put(var5, var6);
         }

         this.profileCount = var2;
      }

      public int getProfileCount() {
         return this.profileCount;
      }

      public int getProfile(String var1) {
         return (Integer)this.map.get(var1);
      }

      public Set getPackages(int var1) {
         TreeSet var2 = new TreeSet();
         Iterator var3 = this.map.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            String var5 = (String)var4.getKey();
            int var6 = (Integer)var4.getValue();
            int var7 = var5.lastIndexOf("/");
            if (var7 > 0 && var1 >= var6) {
               var2.add(var5);
            }
         }

         return var2;
      }
   }

   private static class MakefileProfiles extends Profiles {
      final Map packages = new TreeMap();
      final int maxProfile = 4;

      MakefileProfiles(Properties var1) {
         boolean var2 = false;

         for(int var3 = 1; var3 <= 4; ++var3) {
            String var4 = var3 < 4 ? "PROFILE_" + var3 : "FULL_JRE";
            String var5 = var1.getProperty(var4 + "_RTJAR_INCLUDE_PACKAGES");
            if (var5 == null) {
               break;
            }

            String[] var6 = var5.substring(1).trim().split("\\s+");
            int var7 = var6.length;

            int var8;
            for(var8 = 0; var8 < var7; ++var8) {
               String var9 = var6[var8];
               if (var9.endsWith("/")) {
                  var9 = var9.substring(0, var9.length() - 1);
               }

               if (!var2 && var9.equals("java/lang")) {
                  var2 = true;
               }

               this.includePackage(var3, var9);
            }

            String var12 = var1.getProperty(var4 + "_RTJAR_INCLUDE_TYPES");
            int var16;
            if (var12 != null) {
               String[] var13 = var12.replace("$$", "$").split("\\s+");
               var8 = var13.length;

               for(var16 = 0; var16 < var8; ++var16) {
                  String var10 = var13[var16];
                  if (var10.endsWith(".class")) {
                     this.includeType(var3, var10.substring(0, var10.length() - 6));
                  }
               }
            }

            String var14 = var1.getProperty(var4 + "_RTJAR_EXCLUDE_TYPES");
            if (var14 != null) {
               String[] var15 = var14.replace("$$", "$").split("\\s+");
               var16 = var15.length;

               for(int var17 = 0; var17 < var16; ++var17) {
                  String var11 = var15[var17];
                  if (var11.endsWith(".class")) {
                     this.excludeType(var3, var11.substring(0, var11.length() - 6));
                  }
               }
            }
         }

         if (var2) {
            this.includePackage(1, "javax/crypto");
         }

      }

      public int getProfileCount() {
         return 4;
      }

      public int getProfile(String var1) {
         int var2 = var1.lastIndexOf("/");
         String var3 = var1.substring(0, var2);
         String var4 = var1.substring(var2 + 1);
         Package var5 = this.getPackage(var3);
         return var5.getProfile(var4);
      }

      public Set getPackages(int var1) {
         TreeSet var2 = new TreeSet();
         Iterator var3 = this.packages.values().iterator();

         while(var3.hasNext()) {
            Package var4 = (Package)var3.next();
            var4.getPackages(var1, var2);
         }

         return var2;
      }

      private void includePackage(int var1, String var2) {
         Package var3 = this.getPackage(var2);
         Assert.check(var3.profile == 0);
         var3.profile = var1;
      }

      private void includeType(int var1, String var2) {
         int var3 = var2.lastIndexOf("/");
         String var4 = var2.substring(0, var3);
         String var5 = var2.substring(var3 + 1);
         Package var6 = this.getPackage(var4);
         Assert.check(!var6.includedTypes.containsKey(var5));
         var6.includedTypes.put(var5, var1);
      }

      private void excludeType(int var1, String var2) {
         int var3 = var2.lastIndexOf("/");
         String var4 = var2.substring(0, var3);
         String var5 = var2.substring(var3 + 1);
         Package var6 = this.getPackage(var4);
         Assert.check(!var6.excludedTypes.containsKey(var5));
         var6.excludedTypes.put(var5, var1);
      }

      private Package getPackage(String var1) {
         int var2 = var1.lastIndexOf("/");
         Package var3;
         Map var4;
         String var5;
         if (var2 == -1) {
            var3 = null;
            var4 = this.packages;
            var5 = var1;
         } else {
            var3 = this.getPackage(var1.substring(0, var2));
            var4 = var3.subpackages;
            var5 = var1.substring(var2 + 1);
         }

         Package var6 = (Package)var4.get(var5);
         if (var6 == null) {
            var4.put(var5, var6 = new Package(var3, var5));
         }

         return var6;
      }

      static class Package {
         final Package parent;
         final String name;
         Map subpackages = new TreeMap();
         int profile;
         Map includedTypes = new TreeMap();
         Map excludedTypes = new TreeMap();

         Package(Package var1, String var2) {
            this.parent = var1;
            this.name = var2;
         }

         int getProfile() {
            return this.parent == null ? this.profile : Math.max(this.parent.getProfile(), this.profile);
         }

         int getProfile(String var1) {
            Integer var2;
            if ((var2 = (Integer)this.includedTypes.get(var1)) != null) {
               return var2;
            } else if ((var2 = (Integer)this.includedTypes.get("*")) != null) {
               return var2;
            } else if ((var2 = (Integer)this.excludedTypes.get(var1)) != null) {
               return var2 + 1;
            } else {
               return (var2 = (Integer)this.excludedTypes.get("*")) != null ? var2 + 1 : this.getProfile();
            }
         }

         String getName() {
            return this.parent == null ? this.name : this.parent.getName() + "/" + this.name;
         }

         void getPackages(int var1, Set var2) {
            int var3 = this.getProfile();
            if (var3 != 0 && var1 >= var3) {
               var2.add(this.getName());
            }

            Iterator var4 = this.subpackages.values().iterator();

            while(var4.hasNext()) {
               Package var5 = (Package)var4.next();
               var5.getPackages(var1, var2);
            }

         }
      }
   }
}
