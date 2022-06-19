package com.sun.tools.classfile;

import java.io.IOException;

public class EnclosingMethod_attribute extends Attribute {
   public final int class_index;
   public final int method_index;

   EnclosingMethod_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.class_index = var1.readUnsignedShort();
      this.method_index = var1.readUnsignedShort();
   }

   public EnclosingMethod_attribute(ConstantPool var1, int var2, int var3) throws ConstantPoolException {
      this(var1.getUTF8Index("EnclosingMethod"), var2, var3);
   }

   public EnclosingMethod_attribute(int var1, int var2, int var3) {
      super(var1, 4);
      this.class_index = var2;
      this.method_index = var3;
   }

   public String getClassName(ConstantPool var1) throws ConstantPoolException {
      return var1.getClassInfo(this.class_index).getName();
   }

   public String getMethodName(ConstantPool var1) throws ConstantPoolException {
      return this.method_index == 0 ? "" : var1.getNameAndTypeInfo(this.method_index).getName();
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitEnclosingMethod(this, var2);
   }
}
