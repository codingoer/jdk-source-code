package com.sun.tools.classfile;

import java.io.IOException;

public class RuntimeInvisibleAnnotations_attribute extends RuntimeAnnotations_attribute {
   RuntimeInvisibleAnnotations_attribute(ClassReader var1, int var2, int var3) throws IOException, AttributeException {
      super(var1, var2, var3);
   }

   public RuntimeInvisibleAnnotations_attribute(ConstantPool var1, Annotation[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("RuntimeInvisibleAnnotations"), var2);
   }

   public RuntimeInvisibleAnnotations_attribute(int var1, Annotation[] var2) {
      super(var1, var2);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitRuntimeInvisibleAnnotations(this, var2);
   }
}
