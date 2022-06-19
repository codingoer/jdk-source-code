package com.sun.tools.javah;

import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor8;

public class TypeSignature {
   Elements elems;
   private static final String SIG_VOID = "V";
   private static final String SIG_BOOLEAN = "Z";
   private static final String SIG_BYTE = "B";
   private static final String SIG_CHAR = "C";
   private static final String SIG_SHORT = "S";
   private static final String SIG_INT = "I";
   private static final String SIG_LONG = "J";
   private static final String SIG_FLOAT = "F";
   private static final String SIG_DOUBLE = "D";
   private static final String SIG_ARRAY = "[";
   private static final String SIG_CLASS = "L";

   public TypeSignature(Elements var1) {
      this.elems = var1;
   }

   public String getTypeSignature(String var1) throws SignatureException {
      return this.getParamJVMSignature(var1);
   }

   public String getTypeSignature(String var1, TypeMirror var2) throws SignatureException {
      String var3 = null;
      String var4 = null;
      ArrayList var5 = new ArrayList();
      String var6 = null;
      String var7 = null;
      String var8 = null;
      String var9 = null;
      int var10 = 0;
      int var11 = -1;
      int var12 = -1;
      StringTokenizer var13 = null;
      byte var14 = 0;
      if (var1 != null) {
         var11 = var1.indexOf("(");
         var12 = var1.indexOf(")");
      }

      if (var11 != -1 && var12 != -1 && var11 + 1 < var1.length() && var12 < var1.length()) {
         var3 = var1.substring(var11 + 1, var12);
      }

      if (var3 != null) {
         if (var3.indexOf(",") != -1) {
            var13 = new StringTokenizer(var3, ",");
            if (var13 != null) {
               while(var13.hasMoreTokens()) {
                  var5.add(var13.nextToken());
               }
            }
         } else {
            var5.add(var3);
         }
      }

      var4 = "(";

      while(!var5.isEmpty()) {
         var6 = ((String)var5.remove(var14)).trim();
         var7 = this.getParamJVMSignature(var6);
         if (var7 != null) {
            var4 = var4 + var7;
         }
      }

      var4 = var4 + ")";
      var9 = "";
      if (var2 != null) {
         var10 = this.dimensions(var2);
      }

      while(var10-- > 0) {
         var9 = var9 + "[";
      }

      if (var2 != null) {
         var8 = this.qualifiedTypeName(var2);
         var9 = var9 + this.getComponentType(var8);
      } else {
         System.out.println("Invalid return type.");
      }

      var4 = var4 + var9;
      return var4;
   }

   private String getParamJVMSignature(String var1) throws SignatureException {
      String var2 = "";
      String var3 = "";
      if (var1 != null) {
         if (var1.indexOf("[]") != -1) {
            int var4 = var1.indexOf("[]");
            var3 = var1.substring(0, var4);
            String var5 = var1.substring(var4);
            if (var5 != null) {
               while(var5.indexOf("[]") != -1) {
                  var2 = var2 + "[";
                  int var6 = var5.indexOf("]") + 1;
                  if (var6 < var5.length()) {
                     var5 = var5.substring(var6);
                  } else {
                     var5 = "";
                  }
               }
            }
         } else {
            var3 = var1;
         }

         var2 = var2 + this.getComponentType(var3);
      }

      return var2;
   }

   private String getComponentType(String var1) throws SignatureException {
      String var2 = "";
      if (var1 != null) {
         if (var1.equals("void")) {
            var2 = var2 + "V";
         } else if (var1.equals("boolean")) {
            var2 = var2 + "Z";
         } else if (var1.equals("byte")) {
            var2 = var2 + "B";
         } else if (var1.equals("char")) {
            var2 = var2 + "C";
         } else if (var1.equals("short")) {
            var2 = var2 + "S";
         } else if (var1.equals("int")) {
            var2 = var2 + "I";
         } else if (var1.equals("long")) {
            var2 = var2 + "J";
         } else if (var1.equals("float")) {
            var2 = var2 + "F";
         } else if (var1.equals("double")) {
            var2 = var2 + "D";
         } else if (!var1.equals("")) {
            TypeElement var3 = this.elems.getTypeElement(var1);
            if (var3 == null) {
               throw new SignatureException(var1);
            }

            String var4 = var3.getQualifiedName().toString();
            String var5 = var4.replace('.', '/');
            var2 = var2 + "L";
            var2 = var2 + var5;
            var2 = var2 + ";";
         }
      }

      return var2;
   }

   int dimensions(TypeMirror var1) {
      return var1.getKind() != TypeKind.ARRAY ? 0 : 1 + this.dimensions(((ArrayType)var1).getComponentType());
   }

   String qualifiedTypeName(TypeMirror var1) {
      SimpleTypeVisitor8 var2 = new SimpleTypeVisitor8() {
         public Name visitArray(ArrayType var1, Void var2) {
            return (Name)var1.getComponentType().accept(this, var2);
         }

         public Name visitDeclared(DeclaredType var1, Void var2) {
            return ((TypeElement)var1.asElement()).getQualifiedName();
         }

         public Name visitPrimitive(PrimitiveType var1, Void var2) {
            return TypeSignature.this.elems.getName(var1.toString());
         }

         public Name visitNoType(NoType var1, Void var2) {
            return var1.getKind() == TypeKind.VOID ? TypeSignature.this.elems.getName("void") : (Name)this.defaultAction(var1, var2);
         }

         public Name visitTypeVariable(TypeVariable var1, Void var2) {
            return (Name)var1.getUpperBound().accept(this, var2);
         }
      };
      return ((Name)var2.visit(var1)).toString();
   }

   static class SignatureException extends Exception {
      private static final long serialVersionUID = 1L;

      SignatureException(String var1) {
         super(var1);
      }
   }
}
