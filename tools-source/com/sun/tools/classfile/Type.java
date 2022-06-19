package com.sun.tools.classfile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class Type {
   protected Type() {
   }

   public boolean isObject() {
      return false;
   }

   public abstract Object accept(Visitor var1, Object var2);

   protected static void append(StringBuilder var0, String var1, List var2, String var3) {
      var0.append(var1);
      String var4 = "";

      for(Iterator var5 = var2.iterator(); var5.hasNext(); var4 = ", ") {
         Type var6 = (Type)var5.next();
         var0.append(var4);
         var0.append(var6);
      }

      var0.append(var3);
   }

   protected static void appendIfNotEmpty(StringBuilder var0, String var1, List var2, String var3) {
      if (var2 != null && var2.size() > 0) {
         append(var0, var1, var2, var3);
      }

   }

   public static class WildcardType extends Type {
      public final Kind kind;
      public final Type boundType;

      public WildcardType() {
         this(Type.WildcardType.Kind.UNBOUNDED, (Type)null);
      }

      public WildcardType(Kind var1, Type var2) {
         this.kind = var1;
         this.boundType = var2;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitWildcardType(this, var2);
      }

      public String toString() {
         switch (this.kind) {
            case UNBOUNDED:
               return "?";
            case EXTENDS:
               return "? extends " + this.boundType;
            case SUPER:
               return "? super " + this.boundType;
            default:
               throw new AssertionError();
         }
      }

      public static enum Kind {
         UNBOUNDED,
         EXTENDS,
         SUPER;
      }
   }

   public static class TypeParamType extends Type {
      public final String name;
      public final Type classBound;
      public final List interfaceBounds;

      public TypeParamType(String var1, Type var2, List var3) {
         this.name = var1;
         this.classBound = var2;
         this.interfaceBounds = var3;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitTypeParamType(this, var2);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append(this.name);
         String var2 = " extends ";
         if (this.classBound != null) {
            var1.append(var2);
            var1.append(this.classBound);
            var2 = " & ";
         }

         if (this.interfaceBounds != null) {
            for(Iterator var3 = this.interfaceBounds.iterator(); var3.hasNext(); var2 = " & ") {
               Type var4 = (Type)var3.next();
               var1.append(var2);
               var1.append(var4);
            }
         }

         return var1.toString();
      }
   }

   public static class ClassType extends Type {
      public final ClassType outerType;
      public final String name;
      public final List typeArgs;

      public ClassType(ClassType var1, String var2, List var3) {
         this.outerType = var1;
         this.name = var2;
         this.typeArgs = var3;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitClassType(this, var2);
      }

      public String getBinaryName() {
         return this.outerType == null ? this.name : this.outerType.getBinaryName() + "$" + this.name;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         if (this.outerType != null) {
            var1.append(this.outerType);
            var1.append(".");
         }

         var1.append(this.name);
         appendIfNotEmpty(var1, "<", this.typeArgs, ">");
         return var1.toString();
      }

      public boolean isObject() {
         return this.outerType == null && this.name.equals("java/lang/Object") && (this.typeArgs == null || this.typeArgs.isEmpty());
      }
   }

   public static class ClassSigType extends Type {
      public final List typeParamTypes;
      public final Type superclassType;
      public final List superinterfaceTypes;

      public ClassSigType(List var1, Type var2, List var3) {
         this.typeParamTypes = var1;
         this.superclassType = var2;
         this.superinterfaceTypes = var3;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitClassSigType(this, var2);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         appendIfNotEmpty(var1, "<", this.typeParamTypes, ">");
         if (this.superclassType != null) {
            var1.append(" extends ");
            var1.append(this.superclassType);
         }

         appendIfNotEmpty(var1, " implements ", this.superinterfaceTypes, "");
         return var1.toString();
      }
   }

   public static class MethodType extends Type {
      public final List typeParamTypes;
      public final List paramTypes;
      public final Type returnType;
      public final List throwsTypes;

      public MethodType(List var1, Type var2) {
         this((List)null, var1, var2, (List)null);
      }

      public MethodType(List var1, List var2, Type var3, List var4) {
         this.typeParamTypes = var1;
         this.paramTypes = var2;
         this.returnType = var3;
         this.throwsTypes = var4;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitMethodType(this, var2);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         appendIfNotEmpty(var1, "<", this.typeParamTypes, "> ");
         var1.append(this.returnType);
         append(var1, " (", this.paramTypes, ")");
         appendIfNotEmpty(var1, " throws ", this.throwsTypes, "");
         return var1.toString();
      }
   }

   public static class ArrayType extends Type {
      public final Type elemType;

      public ArrayType(Type var1) {
         this.elemType = var1;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitArrayType(this, var2);
      }

      public String toString() {
         return this.elemType + "[]";
      }
   }

   public static class SimpleType extends Type {
      private static final Set primitiveTypes = new HashSet(Arrays.asList("boolean", "byte", "char", "double", "float", "int", "long", "short", "void"));
      public final String name;

      public SimpleType(String var1) {
         this.name = var1;
      }

      public Object accept(Visitor var1, Object var2) {
         return var1.visitSimpleType(this, var2);
      }

      public boolean isPrimitiveType() {
         return primitiveTypes.contains(this.name);
      }

      public String toString() {
         return this.name;
      }
   }

   public interface Visitor {
      Object visitSimpleType(SimpleType var1, Object var2);

      Object visitArrayType(ArrayType var1, Object var2);

      Object visitMethodType(MethodType var1, Object var2);

      Object visitClassSigType(ClassSigType var1, Object var2);

      Object visitClassType(ClassType var1, Object var2);

      Object visitTypeParamType(TypeParamType var1, Object var2);

      Object visitWildcardType(WildcardType var1, Object var2);
   }
}
