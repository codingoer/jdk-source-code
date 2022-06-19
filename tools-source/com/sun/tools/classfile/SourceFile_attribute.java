package com.sun.tools.classfile;

import java.io.IOException;

public class SourceFile_attribute extends Attribute {
   public final int sourcefile_index;

   SourceFile_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.sourcefile_index = var1.readUnsignedShort();
   }

   public SourceFile_attribute(ConstantPool var1, int var2) throws ConstantPoolException {
      this(var1.getUTF8Index("SourceFile"), var2);
   }

   public SourceFile_attribute(int var1, int var2) {
      super(var1, 2);
      this.sourcefile_index = var2;
   }

   public String getSourceFile(ConstantPool var1) throws ConstantPoolException {
      return var1.getUTF8Value(this.sourcefile_index);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitSourceFile(this, var2);
   }
}
