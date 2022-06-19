package com.sun.tools.classfile;

import java.io.IOException;

public class BootstrapMethods_attribute extends Attribute {
   public final BootstrapMethodSpecifier[] bootstrap_method_specifiers;

   BootstrapMethods_attribute(ClassReader var1, int var2, int var3) throws IOException, AttributeException {
      super(var2, var3);
      int var4 = var1.readUnsignedShort();
      this.bootstrap_method_specifiers = new BootstrapMethodSpecifier[var4];

      for(int var5 = 0; var5 < this.bootstrap_method_specifiers.length; ++var5) {
         this.bootstrap_method_specifiers[var5] = new BootstrapMethodSpecifier(var1);
      }

   }

   public BootstrapMethods_attribute(int var1, BootstrapMethodSpecifier[] var2) {
      super(var1, length(var2));
      this.bootstrap_method_specifiers = var2;
   }

   public static int length(BootstrapMethodSpecifier[] var0) {
      int var1 = 2;
      BootstrapMethodSpecifier[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         BootstrapMethodSpecifier var5 = var2[var4];
         var1 += var5.length();
      }

      return var1;
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitBootstrapMethods(this, var2);
   }

   public static class BootstrapMethodSpecifier {
      public int bootstrap_method_ref;
      public int[] bootstrap_arguments;

      public BootstrapMethodSpecifier(int var1, int[] var2) {
         this.bootstrap_method_ref = var1;
         this.bootstrap_arguments = var2;
      }

      BootstrapMethodSpecifier(ClassReader var1) throws IOException {
         this.bootstrap_method_ref = var1.readUnsignedShort();
         int var2 = var1.readUnsignedShort();
         this.bootstrap_arguments = new int[var2];

         for(int var3 = 0; var3 < this.bootstrap_arguments.length; ++var3) {
            this.bootstrap_arguments[var3] = var1.readUnsignedShort();
         }

      }

      int length() {
         return 4 + this.bootstrap_arguments.length * 2;
      }
   }
}
