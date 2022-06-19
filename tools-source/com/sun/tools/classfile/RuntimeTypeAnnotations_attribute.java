package com.sun.tools.classfile;

import java.io.IOException;

public abstract class RuntimeTypeAnnotations_attribute extends Attribute {
   public final TypeAnnotation[] annotations;

   protected RuntimeTypeAnnotations_attribute(ClassReader var1, int var2, int var3) throws IOException, Annotation.InvalidAnnotation {
      super(var2, var3);
      int var4 = var1.readUnsignedShort();
      this.annotations = new TypeAnnotation[var4];

      for(int var5 = 0; var5 < this.annotations.length; ++var5) {
         this.annotations[var5] = new TypeAnnotation(var1);
      }

   }

   protected RuntimeTypeAnnotations_attribute(int var1, TypeAnnotation[] var2) {
      super(var1, length(var2));
      this.annotations = var2;
   }

   private static int length(TypeAnnotation[] var0) {
      int var1 = 2;
      TypeAnnotation[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         TypeAnnotation var5 = var2[var4];
         var1 += var5.length();
      }

      return var1;
   }
}
