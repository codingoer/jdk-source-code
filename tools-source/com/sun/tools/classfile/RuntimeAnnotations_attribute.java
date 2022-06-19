package com.sun.tools.classfile;

import java.io.IOException;

public abstract class RuntimeAnnotations_attribute extends Attribute {
   public final Annotation[] annotations;

   protected RuntimeAnnotations_attribute(ClassReader var1, int var2, int var3) throws IOException, Annotation.InvalidAnnotation {
      super(var2, var3);
      int var4 = var1.readUnsignedShort();
      this.annotations = new Annotation[var4];

      for(int var5 = 0; var5 < this.annotations.length; ++var5) {
         this.annotations[var5] = new Annotation(var1);
      }

   }

   protected RuntimeAnnotations_attribute(int var1, Annotation[] var2) {
      super(var1, length(var2));
      this.annotations = var2;
   }

   private static int length(Annotation[] var0) {
      int var1 = 2;
      Annotation[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Annotation var5 = var2[var4];
         var1 += var5.length();
      }

      return var1;
   }
}
