package com.sun.tools.classfile;

public class DefaultAttribute extends Attribute {
   public final byte[] info;
   public final String reason;

   DefaultAttribute(ClassReader var1, int var2, byte[] var3) {
      this((ClassReader)var1, var2, var3, (String)null);
   }

   DefaultAttribute(ClassReader var1, int var2, byte[] var3, String var4) {
      super(var2, var3.length);
      this.info = var3;
      this.reason = var4;
   }

   public DefaultAttribute(ConstantPool var1, int var2, byte[] var3) {
      this((ConstantPool)var1, var2, var3, (String)null);
   }

   public DefaultAttribute(ConstantPool var1, int var2, byte[] var3, String var4) {
      super(var2, var3.length);
      this.info = var3;
      this.reason = var4;
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitDefault(this, var2);
   }
}
