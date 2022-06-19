package com.sun.tools.classfile;

import java.io.IOException;

public class ConstantValue_attribute extends Attribute {
   public final int constantvalue_index;

   ConstantValue_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.constantvalue_index = var1.readUnsignedShort();
   }

   public ConstantValue_attribute(ConstantPool var1, int var2) throws ConstantPoolException {
      this(var1.getUTF8Index("ConstantValue"), var2);
   }

   public ConstantValue_attribute(int var1, int var2) {
      super(var1, 2);
      this.constantvalue_index = var2;
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitConstantValue(this, var2);
   }
}
