package com.sun.tools.classfile;

import java.io.IOException;

public class RuntimeVisibleParameterAnnotations_attribute extends RuntimeParameterAnnotations_attribute {
   RuntimeVisibleParameterAnnotations_attribute(ClassReader var1, int var2, int var3) throws IOException, Annotation.InvalidAnnotation {
      super(var1, var2, var3);
   }

   public RuntimeVisibleParameterAnnotations_attribute(ConstantPool var1, Annotation[][] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("RuntimeVisibleParameterAnnotations"), var2);
   }

   public RuntimeVisibleParameterAnnotations_attribute(int var1, Annotation[][] var2) {
      super(var1, var2);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitRuntimeVisibleParameterAnnotations(this, var2);
   }
}
