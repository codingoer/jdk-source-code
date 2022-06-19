package com.sun.tools.classfile;

import java.io.IOException;

public class RuntimeVisibleTypeAnnotations_attribute extends RuntimeTypeAnnotations_attribute {
   RuntimeVisibleTypeAnnotations_attribute(ClassReader var1, int var2, int var3) throws IOException, Annotation.InvalidAnnotation {
      super(var1, var2, var3);
   }

   public RuntimeVisibleTypeAnnotations_attribute(ConstantPool var1, TypeAnnotation[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("RuntimeVisibleTypeAnnotations"), var2);
   }

   public RuntimeVisibleTypeAnnotations_attribute(int var1, TypeAnnotation[] var2) {
      super(var1, var2);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitRuntimeVisibleTypeAnnotations(this, var2);
   }
}
