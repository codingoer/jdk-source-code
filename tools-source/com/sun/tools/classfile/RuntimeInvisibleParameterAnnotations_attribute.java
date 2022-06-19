package com.sun.tools.classfile;

import java.io.IOException;

public class RuntimeInvisibleParameterAnnotations_attribute extends RuntimeParameterAnnotations_attribute {
   RuntimeInvisibleParameterAnnotations_attribute(ClassReader var1, int var2, int var3) throws IOException, Annotation.InvalidAnnotation {
      super(var1, var2, var3);
   }

   public RuntimeInvisibleParameterAnnotations_attribute(ConstantPool var1, Annotation[][] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("RuntimeInvisibleParameterAnnotations"), var2);
   }

   public RuntimeInvisibleParameterAnnotations_attribute(int var1, Annotation[][] var2) {
      super(var1, var2);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitRuntimeInvisibleParameterAnnotations(this, var2);
   }
}
