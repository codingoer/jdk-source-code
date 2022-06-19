package com.sun.tools.javah;

import java.util.Iterator;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class Mangle {
   private Elements elems;
   private Types types;

   Mangle(Elements var1, Types var2) {
      this.elems = var1;
      this.types = var2;
   }

   public final String mangle(CharSequence var1, int var2) {
      StringBuilder var3 = new StringBuilder(100);
      int var4 = var1.length();

      for(int var5 = 0; var5 < var4; ++var5) {
         char var6 = var1.charAt(var5);
         if (isalnum(var6)) {
            var3.append(var6);
         } else if (var6 == '.' && var2 == 1) {
            var3.append('_');
         } else if (var6 == '$' && var2 == 1) {
            var3.append('_');
            var3.append('_');
         } else if (var6 == '_' && var2 == 2) {
            var3.append('_');
         } else if (var6 == '_' && var2 == 1) {
            var3.append('_');
         } else if (var2 == 4) {
            String var7 = null;
            if (var6 == '_') {
               var7 = "_1";
            } else if (var6 == '.') {
               var7 = "_";
            } else if (var6 == ';') {
               var7 = "_2";
            } else if (var6 == '[') {
               var7 = "_3";
            }

            if (var7 != null) {
               var3.append(var7);
            } else {
               var3.append(this.mangleChar(var6));
            }
         } else if (var2 == 5) {
            if (isprint(var6)) {
               var3.append(var6);
            } else {
               var3.append(this.mangleChar(var6));
            }
         } else {
            var3.append(this.mangleChar(var6));
         }
      }

      return var3.toString();
   }

   public String mangleMethod(ExecutableElement var1, TypeElement var2, int var3) throws TypeSignature.SignatureException {
      StringBuilder var4 = new StringBuilder(100);
      var4.append("Java_");
      if (var3 == 6) {
         var4.append(this.mangle(var2.getQualifiedName(), 1));
         var4.append('_');
         var4.append(this.mangle(var1.getSimpleName(), 3));
         var4.append("_stub");
         return var4.toString();
      } else {
         var4.append(this.mangle(this.getInnerQualifiedName(var2), 4));
         var4.append('_');
         var4.append(this.mangle(var1.getSimpleName(), 4));
         if (var3 == 8) {
            var4.append("__");
            String var5 = this.signature(var1);
            TypeSignature var6 = new TypeSignature(this.elems);
            String var7 = var6.getTypeSignature(var5, var1.getReturnType());
            var7 = var7.substring(1);
            var7 = var7.substring(0, var7.lastIndexOf(41));
            var7 = var7.replace('/', '.');
            var4.append(this.mangle(var7, 4));
         }

         return var4.toString();
      }
   }

   private String getInnerQualifiedName(TypeElement var1) {
      return this.elems.getBinaryName(var1).toString();
   }

   public final String mangleChar(char var1) {
      String var2 = Integer.toHexString(var1);
      int var3 = 5 - var2.length();
      char[] var4 = new char[6];
      var4[0] = '_';

      int var5;
      for(var5 = 1; var5 <= var3; ++var5) {
         var4[var5] = '0';
      }

      var5 = var3 + 1;

      for(int var6 = 0; var5 < 6; ++var6) {
         var4[var5] = var2.charAt(var6);
         ++var5;
      }

      return new String(var4);
   }

   private String signature(ExecutableElement var1) {
      StringBuilder var2 = new StringBuilder();
      String var3 = "(";

      for(Iterator var4 = var1.getParameters().iterator(); var4.hasNext(); var3 = ",") {
         VariableElement var5 = (VariableElement)var4.next();
         var2.append(var3);
         var2.append(this.types.erasure(var5.asType()).toString());
      }

      var2.append(")");
      return var2.toString();
   }

   private static final boolean isalnum(char var0) {
      return var0 <= 127 && (var0 >= 'A' && var0 <= 'Z' || var0 >= 'a' && var0 <= 'z' || var0 >= '0' && var0 <= '9');
   }

   private static final boolean isprint(char var0) {
      return var0 >= ' ' && var0 <= '~';
   }

   public static class Type {
      public static final int CLASS = 1;
      public static final int FIELDSTUB = 2;
      public static final int FIELD = 3;
      public static final int JNI = 4;
      public static final int SIGNATURE = 5;
      public static final int METHOD_JDK_1 = 6;
      public static final int METHOD_JNI_SHORT = 7;
      public static final int METHOD_JNI_LONG = 8;
   }
}
