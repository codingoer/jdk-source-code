package com.sun.tools.classfile;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class AccessFlags {
   public static final int ACC_PUBLIC = 1;
   public static final int ACC_PRIVATE = 2;
   public static final int ACC_PROTECTED = 4;
   public static final int ACC_STATIC = 8;
   public static final int ACC_FINAL = 16;
   public static final int ACC_SUPER = 32;
   public static final int ACC_SYNCHRONIZED = 32;
   public static final int ACC_VOLATILE = 64;
   public static final int ACC_BRIDGE = 64;
   public static final int ACC_TRANSIENT = 128;
   public static final int ACC_VARARGS = 128;
   public static final int ACC_NATIVE = 256;
   public static final int ACC_INTERFACE = 512;
   public static final int ACC_ABSTRACT = 1024;
   public static final int ACC_STRICT = 2048;
   public static final int ACC_SYNTHETIC = 4096;
   public static final int ACC_ANNOTATION = 8192;
   public static final int ACC_ENUM = 16384;
   public static final int ACC_MANDATED = 32768;
   private static final int[] classModifiers = new int[]{1, 16, 1024};
   private static final int[] classFlags = new int[]{1, 16, 32, 512, 1024, 4096, 8192, 16384};
   private static final int[] innerClassModifiers = new int[]{1, 2, 4, 8, 16, 1024};
   private static final int[] innerClassFlags = new int[]{1, 2, 4, 8, 16, 32, 512, 1024, 4096, 8192, 16384};
   private static final int[] fieldModifiers = new int[]{1, 2, 4, 8, 16, 64, 128};
   private static final int[] fieldFlags = new int[]{1, 2, 4, 8, 16, 64, 128, 4096, 16384};
   private static final int[] methodModifiers = new int[]{1, 2, 4, 8, 16, 32, 256, 1024, 2048};
   private static final int[] methodFlags = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 1024, 2048, 4096};
   public final int flags;

   AccessFlags(ClassReader var1) throws IOException {
      this(var1.readUnsignedShort());
   }

   public AccessFlags(int var1) {
      this.flags = var1;
   }

   public AccessFlags ignore(int var1) {
      return new AccessFlags(this.flags & ~var1);
   }

   public boolean is(int var1) {
      return (this.flags & var1) != 0;
   }

   public int byteLength() {
      return 2;
   }

   public Set getClassModifiers() {
      int var1 = (this.flags & 512) != 0 ? this.flags & -1025 : this.flags;
      return getModifiers(var1, classModifiers, AccessFlags.Kind.Class);
   }

   public Set getClassFlags() {
      return this.getFlags(classFlags, AccessFlags.Kind.Class);
   }

   public Set getInnerClassModifiers() {
      int var1 = (this.flags & 512) != 0 ? this.flags & -1025 : this.flags;
      return getModifiers(var1, innerClassModifiers, AccessFlags.Kind.InnerClass);
   }

   public Set getInnerClassFlags() {
      return this.getFlags(innerClassFlags, AccessFlags.Kind.InnerClass);
   }

   public Set getFieldModifiers() {
      return this.getModifiers(fieldModifiers, AccessFlags.Kind.Field);
   }

   public Set getFieldFlags() {
      return this.getFlags(fieldFlags, AccessFlags.Kind.Field);
   }

   public Set getMethodModifiers() {
      return this.getModifiers(methodModifiers, AccessFlags.Kind.Method);
   }

   public Set getMethodFlags() {
      return this.getFlags(methodFlags, AccessFlags.Kind.Method);
   }

   private Set getModifiers(int[] var1, Kind var2) {
      return getModifiers(this.flags, var1, var2);
   }

   private static Set getModifiers(int var0, int[] var1, Kind var2) {
      LinkedHashSet var3 = new LinkedHashSet();
      int[] var4 = var1;
      int var5 = var1.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var4[var6];
         if ((var0 & var7) != 0) {
            var3.add(flagToModifier(var7, var2));
         }
      }

      return var3;
   }

   private Set getFlags(int[] var1, Kind var2) {
      LinkedHashSet var3 = new LinkedHashSet();
      int var4 = this.flags;
      int[] var5 = var1;
      int var6 = var1.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = var5[var7];
         if ((var4 & var8) != 0) {
            var3.add(flagToName(var8, var2));
            var4 &= ~var8;
         }
      }

      while(var4 != 0) {
         int var9 = Integer.highestOneBit(var4);
         var3.add("0x" + Integer.toHexString(var9));
         var4 &= ~var9;
      }

      return var3;
   }

   private static String flagToModifier(int var0, Kind var1) {
      switch (var0) {
         case 1:
            return "public";
         case 2:
            return "private";
         case 4:
            return "protected";
         case 8:
            return "static";
         case 16:
            return "final";
         case 32:
            return "synchronized";
         case 64:
            return "volatile";
         case 128:
            return var1 == AccessFlags.Kind.Field ? "transient" : null;
         case 256:
            return "native";
         case 1024:
            return "abstract";
         case 2048:
            return "strictfp";
         case 32768:
            return "mandated";
         default:
            return null;
      }
   }

   private static String flagToName(int var0, Kind var1) {
      switch (var0) {
         case 1:
            return "ACC_PUBLIC";
         case 2:
            return "ACC_PRIVATE";
         case 4:
            return "ACC_PROTECTED";
         case 8:
            return "ACC_STATIC";
         case 16:
            return "ACC_FINAL";
         case 32:
            return var1 == AccessFlags.Kind.Class ? "ACC_SUPER" : "ACC_SYNCHRONIZED";
         case 64:
            return var1 == AccessFlags.Kind.Field ? "ACC_VOLATILE" : "ACC_BRIDGE";
         case 128:
            return var1 == AccessFlags.Kind.Field ? "ACC_TRANSIENT" : "ACC_VARARGS";
         case 256:
            return "ACC_NATIVE";
         case 512:
            return "ACC_INTERFACE";
         case 1024:
            return "ACC_ABSTRACT";
         case 2048:
            return "ACC_STRICT";
         case 4096:
            return "ACC_SYNTHETIC";
         case 8192:
            return "ACC_ANNOTATION";
         case 16384:
            return "ACC_ENUM";
         case 32768:
            return "ACC_MANDATED";
         default:
            return null;
      }
   }

   public static enum Kind {
      Class,
      InnerClass,
      Field,
      Method;
   }
}
