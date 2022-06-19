package com.sun.tools.classfile;

import java.io.IOException;

public class RuntimeInvisibleTypeAnnotations_attribute extends RuntimeTypeAnnotations_attribute {
   RuntimeInvisibleTypeAnnotations_attribute(ClassReader var1, int var2, int var3) throws IOException, Annotation.InvalidAnnotation {
      super(var1, var2, var3);
   }

   public RuntimeInvisibleTypeAnnotations_attribute(ConstantPool var1, TypeAnnotation[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("RuntimeInvisibleTypeAnnotations"), var2);
   }

   public RuntimeInvisibleTypeAnnotations_attribute(int var1, TypeAnnotation[] var2) {
      super(var1, var2);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitRuntimeInvisibleTypeAnnotations(this, var2);
   }
}
