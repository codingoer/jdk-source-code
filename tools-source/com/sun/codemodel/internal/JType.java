package com.sun.codemodel.internal;

public abstract class JType implements JGenerable, Comparable {
   public static JPrimitiveType parse(JCodeModel codeModel, String typeName) {
      if (typeName.equals("void")) {
         return codeModel.VOID;
      } else if (typeName.equals("boolean")) {
         return codeModel.BOOLEAN;
      } else if (typeName.equals("byte")) {
         return codeModel.BYTE;
      } else if (typeName.equals("short")) {
         return codeModel.SHORT;
      } else if (typeName.equals("char")) {
         return codeModel.CHAR;
      } else if (typeName.equals("int")) {
         return codeModel.INT;
      } else if (typeName.equals("float")) {
         return codeModel.FLOAT;
      } else if (typeName.equals("long")) {
         return codeModel.LONG;
      } else if (typeName.equals("double")) {
         return codeModel.DOUBLE;
      } else {
         throw new IllegalArgumentException("Not a primitive type: " + typeName);
      }
   }

   public abstract JCodeModel owner();

   public abstract String fullName();

   public String binaryName() {
      return this.fullName();
   }

   public abstract String name();

   public abstract JClass array();

   public boolean isArray() {
      return false;
   }

   public boolean isPrimitive() {
      return false;
   }

   public abstract JClass boxify();

   public abstract JType unboxify();

   public JType erasure() {
      return this;
   }

   public final boolean isReference() {
      return !this.isPrimitive();
   }

   public JType elementType() {
      throw new IllegalArgumentException("Not an array type");
   }

   public String toString() {
      return this.getClass().getName() + '(' + this.fullName() + ')';
   }

   public int compareTo(JType o) {
      String rhs = o.fullName();
      boolean p = this.fullName().startsWith("java");
      boolean q = rhs.startsWith("java");
      if (p && !q) {
         return -1;
      } else {
         return !p && q ? 1 : this.fullName().compareTo(rhs);
      }
   }
}
