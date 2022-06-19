package com.sun.tools.javac.jvm;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.util.Name;

public class ClassFile {
   public static final int JAVA_MAGIC = -889275714;
   public static final int CONSTANT_Utf8 = 1;
   public static final int CONSTANT_Unicode = 2;
   public static final int CONSTANT_Integer = 3;
   public static final int CONSTANT_Float = 4;
   public static final int CONSTANT_Long = 5;
   public static final int CONSTANT_Double = 6;
   public static final int CONSTANT_Class = 7;
   public static final int CONSTANT_String = 8;
   public static final int CONSTANT_Fieldref = 9;
   public static final int CONSTANT_Methodref = 10;
   public static final int CONSTANT_InterfaceMethodref = 11;
   public static final int CONSTANT_NameandType = 12;
   public static final int CONSTANT_MethodHandle = 15;
   public static final int CONSTANT_MethodType = 16;
   public static final int CONSTANT_InvokeDynamic = 18;
   public static final int REF_getField = 1;
   public static final int REF_getStatic = 2;
   public static final int REF_putField = 3;
   public static final int REF_putStatic = 4;
   public static final int REF_invokeVirtual = 5;
   public static final int REF_invokeStatic = 6;
   public static final int REF_invokeSpecial = 7;
   public static final int REF_newInvokeSpecial = 8;
   public static final int REF_invokeInterface = 9;
   public static final int MAX_PARAMETERS = 255;
   public static final int MAX_DIMENSIONS = 255;
   public static final int MAX_CODE = 65535;
   public static final int MAX_LOCALS = 65535;
   public static final int MAX_STACK = 65535;

   public static byte[] internalize(byte[] var0, int var1, int var2) {
      byte[] var3 = new byte[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         byte var5 = var0[var1 + var4];
         if (var5 == 47) {
            var3[var4] = 46;
         } else {
            var3[var4] = var5;
         }
      }

      return var3;
   }

   public static byte[] internalize(Name var0) {
      return internalize(var0.getByteArray(), var0.getByteOffset(), var0.getByteLength());
   }

   public static byte[] externalize(byte[] var0, int var1, int var2) {
      byte[] var3 = new byte[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         byte var5 = var0[var1 + var4];
         if (var5 == 46) {
            var3[var4] = 47;
         } else {
            var3[var4] = var5;
         }
      }

      return var3;
   }

   public static byte[] externalize(Name var0) {
      return externalize(var0.getByteArray(), var0.getByteOffset(), var0.getByteLength());
   }

   public static class NameAndType {
      Name name;
      Types.UniqueType uniqueType;
      Types types;

      NameAndType(Name var1, Type var2, Types var3) {
         this.name = var1;
         this.uniqueType = new Types.UniqueType(var2, var3);
         this.types = var3;
      }

      void setType(Type var1) {
         this.uniqueType = new Types.UniqueType(var1, this.types);
      }

      public boolean equals(Object var1) {
         return var1 instanceof NameAndType && this.name == ((NameAndType)var1).name && this.uniqueType.equals(((NameAndType)var1).uniqueType);
      }

      public int hashCode() {
         return this.name.hashCode() * this.uniqueType.hashCode();
      }
   }

   public static enum Version {
      V45_3(45, 3),
      V49(49, 0),
      V50(50, 0),
      V51(51, 0),
      V52(52, 0);

      public final int major;
      public final int minor;

      private Version(int var3, int var4) {
         this.major = var3;
         this.minor = var4;
      }
   }
}
