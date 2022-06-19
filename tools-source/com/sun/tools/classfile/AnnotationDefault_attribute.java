package com.sun.tools.classfile;

import java.io.IOException;

public class AnnotationDefault_attribute extends Attribute {
   public final Annotation.element_value default_value;

   AnnotationDefault_attribute(ClassReader var1, int var2, int var3) throws IOException, Annotation.InvalidAnnotation {
      super(var2, var3);
      this.default_value = Annotation.element_value.read(var1);
   }

   public AnnotationDefault_attribute(ConstantPool var1, Annotation.element_value var2) throws ConstantPoolException {
      this(var1.getUTF8Index("AnnotationDefault"), var2);
   }

   public AnnotationDefault_attribute(int var1, Annotation.element_value var2) {
      super(var1, var2.length());
      this.default_value = var2;
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitAnnotationDefault(this, var2);
   }
}
