package com.sun.tools.classfile;

import java.io.IOException;

public class SourceID_attribute extends Attribute {
   public final int sourceID_index;

   SourceID_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.sourceID_index = var1.readUnsignedShort();
   }

   public SourceID_attribute(ConstantPool var1, int var2) throws ConstantPoolException {
      this(var1.getUTF8Index("SourceID"), var2);
   }

   public SourceID_attribute(int var1, int var2) {
      super(var1, 2);
      this.sourceID_index = var2;
   }

   String getSourceID(ConstantPool var1) throws ConstantPoolException {
      return var1.getUTF8Value(this.sourceID_index);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitSourceID(this, var2);
   }
}
