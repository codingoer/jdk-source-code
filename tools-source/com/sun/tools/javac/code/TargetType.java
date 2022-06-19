package com.sun.tools.javac.code;

import com.sun.tools.javac.util.Assert;

public enum TargetType {
   CLASS_TYPE_PARAMETER(0),
   METHOD_TYPE_PARAMETER(1),
   CLASS_EXTENDS(16),
   CLASS_TYPE_PARAMETER_BOUND(17),
   METHOD_TYPE_PARAMETER_BOUND(18),
   FIELD(19),
   METHOD_RETURN(20),
   METHOD_RECEIVER(21),
   METHOD_FORMAL_PARAMETER(22),
   THROWS(23),
   LOCAL_VARIABLE(64, true),
   RESOURCE_VARIABLE(65, true),
   EXCEPTION_PARAMETER(66, true),
   INSTANCEOF(67, true),
   NEW(68, true),
   CONSTRUCTOR_REFERENCE(69, true),
   METHOD_REFERENCE(70, true),
   CAST(71, true),
   CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT(72, true),
   METHOD_INVOCATION_TYPE_ARGUMENT(73, true),
   CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT(74, true),
   METHOD_REFERENCE_TYPE_ARGUMENT(75, true),
   UNKNOWN(255);

   private static final int MAXIMUM_TARGET_TYPE_VALUE = 75;
   private final int targetTypeValue;
   private final boolean isLocal;
   private static final TargetType[] targets = new TargetType[76];

   private TargetType(int var3) {
      this(var3, false);
   }

   private TargetType(int var3, boolean var4) {
      if (var3 < 0 || var3 > 255) {
         Assert.error("Attribute type value needs to be an unsigned byte: " + String.format("0x%02X", var3));
      }

      this.targetTypeValue = var3;
      this.isLocal = var4;
   }

   public boolean isLocal() {
      return this.isLocal;
   }

   public int targetTypeValue() {
      return this.targetTypeValue;
   }

   public static boolean isValidTargetTypeValue(int var0) {
      if (var0 == UNKNOWN.targetTypeValue) {
         return true;
      } else {
         return var0 >= 0 && var0 < targets.length;
      }
   }

   public static TargetType fromTargetTypeValue(int var0) {
      if (var0 == UNKNOWN.targetTypeValue) {
         return UNKNOWN;
      } else {
         if (var0 < 0 || var0 >= targets.length) {
            Assert.error("Unknown TargetType: " + var0);
         }

         return targets[var0];
      }
   }

   static {
      TargetType[] var0 = values();
      TargetType[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         TargetType var4 = var1[var3];
         if (var4.targetTypeValue != UNKNOWN.targetTypeValue) {
            targets[var4.targetTypeValue] = var4;
         }
      }

      for(int var5 = 0; var5 <= 75; ++var5) {
         if (targets[var5] == null) {
            targets[var5] = UNKNOWN;
         }
      }

   }
}
