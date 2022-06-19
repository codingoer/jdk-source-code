package com.sun.tools.javac.jvm;

import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Options;
import java.util.EnumSet;
import java.util.Set;

public enum Profile {
   COMPACT1("compact1", 1, Target.JDK1_8, new Target[0]),
   COMPACT2("compact2", 2, Target.JDK1_8, new Target[0]),
   COMPACT3("compact3", 3, Target.JDK1_8, new Target[0]),
   DEFAULT {
      public boolean isValid(Target var1) {
         return true;
      }
   };

   private static final Context.Key profileKey = new Context.Key();
   public final String name;
   public final int value;
   final Set targets;

   public static Profile instance(Context var0) {
      Profile var1 = (Profile)var0.get(profileKey);
      if (var1 == null) {
         Options var2 = Options.instance(var0);
         String var3 = var2.get(Option.PROFILE);
         if (var3 != null) {
            var1 = lookup(var3);
         }

         if (var1 == null) {
            var1 = DEFAULT;
         }

         var0.put((Context.Key)profileKey, (Object)var1);
      }

      return var1;
   }

   private Profile() {
      this.name = null;
      this.value = Integer.MAX_VALUE;
      this.targets = null;
   }

   private Profile(String var3, int var4, Target var5, Target... var6) {
      this.name = var3;
      this.value = var4;
      this.targets = EnumSet.of(var5, var6);
   }

   public static Profile lookup(String var0) {
      Profile[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Profile var4 = var1[var3];
         if (var0.equals(var4.name)) {
            return var4;
         }
      }

      return null;
   }

   public static Profile lookup(int var0) {
      Profile[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Profile var4 = var1[var3];
         if (var0 == var4.value) {
            return var4;
         }
      }

      return null;
   }

   public boolean isValid(Target var1) {
      return this.targets.contains(var1);
   }

   // $FF: synthetic method
   Profile(Object var3) {
      this();
   }
}
