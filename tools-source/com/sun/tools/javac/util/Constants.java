package com.sun.tools.javac.util;

import com.sun.tools.javac.code.Type;

public class Constants {
   public static Object decode(Object var0, Type var1) {
      if (var0 instanceof Integer) {
         int var2 = (Integer)var0;
         switch (var1.getTag()) {
            case BOOLEAN:
               return var2 != 0;
            case CHAR:
               return (char)var2;
            case BYTE:
               return (byte)var2;
            case SHORT:
               return (short)var2;
         }
      }

      return var0;
   }

   public static String format(Object var0, Type var1) {
      var0 = decode(var0, var1);
      switch (var1.getTag()) {
         case CHAR:
            return formatChar((Character)var0);
         case BYTE:
            return formatByte((Byte)var0);
         case SHORT:
         default:
            if (var0 instanceof String) {
               return formatString((String)var0);
            }

            return var0 + "";
         case LONG:
            return formatLong((Long)var0);
         case FLOAT:
            return formatFloat((Float)var0);
         case DOUBLE:
            return formatDouble((Double)var0);
      }
   }

   public static String format(Object var0) {
      if (var0 instanceof Byte) {
         return formatByte((Byte)var0);
      } else if (var0 instanceof Short) {
         return formatShort((Short)var0);
      } else if (var0 instanceof Long) {
         return formatLong((Long)var0);
      } else if (var0 instanceof Float) {
         return formatFloat((Float)var0);
      } else if (var0 instanceof Double) {
         return formatDouble((Double)var0);
      } else if (var0 instanceof Character) {
         return formatChar((Character)var0);
      } else if (var0 instanceof String) {
         return formatString((String)var0);
      } else if (!(var0 instanceof Integer) && !(var0 instanceof Boolean)) {
         throw new IllegalArgumentException("Argument is not a primitive type or a string; it " + (var0 == null ? "is a null value." : "has class " + var0.getClass().getName()) + ".");
      } else {
         return var0.toString();
      }
   }

   private static String formatByte(byte var0) {
      return String.format("(byte)0x%02x", var0);
   }

   private static String formatShort(short var0) {
      return String.format("(short)%d", var0);
   }

   private static String formatLong(long var0) {
      return var0 + "L";
   }

   private static String formatFloat(float var0) {
      if (Float.isNaN(var0)) {
         return "0.0f/0.0f";
      } else if (Float.isInfinite(var0)) {
         return var0 < 0.0F ? "-1.0f/0.0f" : "1.0f/0.0f";
      } else {
         return var0 + "f";
      }
   }

   private static String formatDouble(double var0) {
      if (Double.isNaN(var0)) {
         return "0.0/0.0";
      } else if (Double.isInfinite(var0)) {
         return var0 < 0.0 ? "-1.0/0.0" : "1.0/0.0";
      } else {
         return var0 + "";
      }
   }

   private static String formatChar(char var0) {
      return '\'' + Convert.quote(var0) + '\'';
   }

   private static String formatString(String var0) {
      return '"' + Convert.quote(var0) + '"';
   }
}
