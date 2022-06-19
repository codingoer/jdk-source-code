package com.sun.tools.classfile;

import java.io.IOException;

public abstract class RuntimeParameterAnnotations_attribute extends Attribute {
   public final Annotation[][] parameter_annotations;

   RuntimeParameterAnnotations_attribute(ClassReader var1, int var2, int var3) throws IOException, Annotation.InvalidAnnotation {
      super(var2, var3);
      int var4 = var1.readUnsignedByte();
      this.parameter_annotations = new Annotation[var4][];

      for(int var5 = 0; var5 < this.parameter_annotations.length; ++var5) {
         int var6 = var1.readUnsignedShort();
         Annotation[] var7 = new Annotation[var6];

         for(int var8 = 0; var8 < var6; ++var8) {
            var7[var8] = new Annotation(var1);
         }

         this.parameter_annotations[var5] = var7;
      }

   }

   protected RuntimeParameterAnnotations_attribute(int var1, Annotation[][] var2) {
      super(var1, length(var2));
      this.parameter_annotations = var2;
   }

   private static int length(Annotation[][] var0) {
      int var1 = 1;
      Annotation[][] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Annotation[] var5 = var2[var4];
         var1 += 2;
         Annotation[] var6 = var5;
         int var7 = var5.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Annotation var9 = var6[var8];
            var1 += var9.length();
         }
      }

      return var1;
   }
}
