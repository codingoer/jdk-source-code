package com.sun.tools.classfile;

import java.io.IOException;

public class Synthetic_attribute extends Attribute {
   Synthetic_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
   }

   public Synthetic_attribute(ConstantPool var1) throws ConstantPoolException {
      this(var1.getUTF8Index("Synthetic"));
   }

   public Synthetic_attribute(int var1) {
      super(var1, 0);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitSynthetic(this, var2);
   }
}
