package com.sun.tools.classfile;

import java.io.IOException;

public class CompilationID_attribute extends Attribute {
   public final int compilationID_index;

   CompilationID_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.compilationID_index = var1.readUnsignedShort();
   }

   public CompilationID_attribute(ConstantPool var1, int var2) throws ConstantPoolException {
      this(var1.getUTF8Index("CompilationID"), var2);
   }

   public CompilationID_attribute(int var1, int var2) {
      super(var1, 2);
      this.compilationID_index = var2;
   }

   String getCompilationID(ConstantPool var1) throws ConstantPoolException {
      return var1.getUTF8Value(this.compilationID_index);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitCompilationID(this, var2);
   }
}
