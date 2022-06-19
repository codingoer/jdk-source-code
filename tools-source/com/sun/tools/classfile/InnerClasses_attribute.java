package com.sun.tools.classfile;

import java.io.IOException;

public class InnerClasses_attribute extends Attribute {
   public final int number_of_classes;
   public final Info[] classes;

   InnerClasses_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.number_of_classes = var1.readUnsignedShort();
      this.classes = new Info[this.number_of_classes];

      for(int var4 = 0; var4 < this.number_of_classes; ++var4) {
         this.classes[var4] = new Info(var1);
      }

   }

   public InnerClasses_attribute(ConstantPool var1, Info[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("InnerClasses"), var2);
   }

   public InnerClasses_attribute(int var1, Info[] var2) {
      super(var1, 2 + InnerClasses_attribute.Info.length() * var2.length);
      this.number_of_classes = var2.length;
      this.classes = var2;
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitInnerClasses(this, var2);
   }

   public static class Info {
      public final int inner_class_info_index;
      public final int outer_class_info_index;
      public final int inner_name_index;
      public final AccessFlags inner_class_access_flags;

      Info(ClassReader var1) throws IOException {
         this.inner_class_info_index = var1.readUnsignedShort();
         this.outer_class_info_index = var1.readUnsignedShort();
         this.inner_name_index = var1.readUnsignedShort();
         this.inner_class_access_flags = new AccessFlags(var1.readUnsignedShort());
      }

      public ConstantPool.CONSTANT_Class_info getInnerClassInfo(ConstantPool var1) throws ConstantPoolException {
         return this.inner_class_info_index == 0 ? null : var1.getClassInfo(this.inner_class_info_index);
      }

      public ConstantPool.CONSTANT_Class_info getOuterClassInfo(ConstantPool var1) throws ConstantPoolException {
         return this.outer_class_info_index == 0 ? null : var1.getClassInfo(this.outer_class_info_index);
      }

      public String getInnerName(ConstantPool var1) throws ConstantPoolException {
         return this.inner_name_index == 0 ? null : var1.getUTF8Value(this.inner_name_index);
      }

      public static int length() {
         return 8;
      }
   }
}
