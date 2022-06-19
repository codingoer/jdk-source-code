package com.sun.tools.classfile;

import java.io.IOException;

public class Annotation {
   public final int type_index;
   public final int num_element_value_pairs;
   public final element_value_pair[] element_value_pairs;

   Annotation(ClassReader var1) throws IOException, InvalidAnnotation {
      this.type_index = var1.readUnsignedShort();
      this.num_element_value_pairs = var1.readUnsignedShort();
      this.element_value_pairs = new element_value_pair[this.num_element_value_pairs];

      for(int var2 = 0; var2 < this.element_value_pairs.length; ++var2) {
         this.element_value_pairs[var2] = new element_value_pair(var1);
      }

   }

   public Annotation(ConstantPool var1, int var2, element_value_pair[] var3) {
      this.type_index = var2;
      this.num_element_value_pairs = var3.length;
      this.element_value_pairs = var3;
   }

   public int length() {
      int var1 = 4;
      element_value_pair[] var2 = this.element_value_pairs;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         element_value_pair var5 = var2[var4];
         var1 += var5.length();
      }

      return var1;
   }

   public static class element_value_pair {
      public final int element_name_index;
      public final element_value value;

      element_value_pair(ClassReader var1) throws IOException, InvalidAnnotation {
         this.element_name_index = var1.readUnsignedShort();
         this.value = Annotation.element_value.read(var1);
      }

      public int length() {
         return 2 + this.value.length();
      }
   }

   public static class Array_element_value extends element_value {
      public final int num_values;
      public final element_value[] values;

      Array_element_value(ClassReader var1, int var2) throws IOException, InvalidAnnotation {
         super(var2);
         this.num_values = var1.readUnsignedShort();
         this.values = new element_value[this.num_values];

         for(int var3 = 0; var3 < this.values.length; ++var3) {
            this.values[var3] = Annotation.element_value.read(var1);
         }

      }

      public int length() {
         int var1 = 2;

         for(int var2 = 0; var2 < this.values.length; ++var2) {
            var1 += this.values[var2].length();
         }

         return var1;
      }

      public Object accept(element_value.Visitor var1, Object var2) {
         return var1.visitArray(this, var2);
      }
   }

   public static class Annotation_element_value extends element_value {
      public final Annotation annotation_value;

      Annotation_element_value(ClassReader var1, int var2) throws IOException, InvalidAnnotation {
         super(var2);
         this.annotation_value = new Annotation(var1);
      }

      public int length() {
         return this.annotation_value.length();
      }

      public Object accept(element_value.Visitor var1, Object var2) {
         return var1.visitAnnotation(this, var2);
      }
   }

   public static class Class_element_value extends element_value {
      public final int class_info_index;

      Class_element_value(ClassReader var1, int var2) throws IOException {
         super(var2);
         this.class_info_index = var1.readUnsignedShort();
      }

      public int length() {
         return 2;
      }

      public Object accept(element_value.Visitor var1, Object var2) {
         return var1.visitClass(this, var2);
      }
   }

   public static class Enum_element_value extends element_value {
      public final int type_name_index;
      public final int const_name_index;

      Enum_element_value(ClassReader var1, int var2) throws IOException {
         super(var2);
         this.type_name_index = var1.readUnsignedShort();
         this.const_name_index = var1.readUnsignedShort();
      }

      public int length() {
         return 4;
      }

      public Object accept(element_value.Visitor var1, Object var2) {
         return var1.visitEnum(this, var2);
      }
   }

   public static class Primitive_element_value extends element_value {
      public final int const_value_index;

      Primitive_element_value(ClassReader var1, int var2) throws IOException {
         super(var2);
         this.const_value_index = var1.readUnsignedShort();
      }

      public int length() {
         return 2;
      }

      public Object accept(element_value.Visitor var1, Object var2) {
         return var1.visitPrimitive(this, var2);
      }
   }

   public abstract static class element_value {
      public final int tag;

      public static element_value read(ClassReader var0) throws IOException, InvalidAnnotation {
         int var1 = var0.readUnsignedByte();
         switch (var1) {
            case 64:
               return new Annotation_element_value(var0, var1);
            case 65:
            case 69:
            case 71:
            case 72:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 100:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            default:
               throw new InvalidAnnotation("unrecognized tag: " + var1);
            case 66:
            case 67:
            case 68:
            case 70:
            case 73:
            case 74:
            case 83:
            case 90:
            case 115:
               return new Primitive_element_value(var0, var1);
            case 91:
               return new Array_element_value(var0, var1);
            case 99:
               return new Class_element_value(var0, var1);
            case 101:
               return new Enum_element_value(var0, var1);
         }
      }

      protected element_value(int var1) {
         this.tag = var1;
      }

      public abstract int length();

      public abstract Object accept(Visitor var1, Object var2);

      public interface Visitor {
         Object visitPrimitive(Primitive_element_value var1, Object var2);

         Object visitEnum(Enum_element_value var1, Object var2);

         Object visitClass(Class_element_value var1, Object var2);

         Object visitAnnotation(Annotation_element_value var1, Object var2);

         Object visitArray(Array_element_value var1, Object var2);
      }
   }

   static class InvalidAnnotation extends AttributeException {
      private static final long serialVersionUID = -4620480740735772708L;

      InvalidAnnotation(String var1) {
         super(var1);
      }
   }
}
