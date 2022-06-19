package com.sun.tools.classfile;

import java.io.IOException;

public class RuntimeVisibleAnnotations_attribute extends RuntimeAnnotations_attribute {
   RuntimeVisibleAnnotations_attribute(ClassReader var1, int var2, int var3) throws IOException, Annotation.InvalidAnnotation {
      super(var1, var2, var3);
   }

   public RuntimeVisibleAnnotations_attribute(ConstantPool var1, Annotation[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("RuntimeVisibleAnnotations"), var2);
   }

   public RuntimeVisibleAnnotations_attribute(int var1, Annotation[] var2) {
      super(var1, var2);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitRuntimeVisibleAnnotations(this, var2);
   }
}
