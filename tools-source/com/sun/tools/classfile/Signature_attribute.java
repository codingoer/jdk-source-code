package com.sun.tools.classfile;

import java.io.IOException;

public class Signature_attribute extends Attribute {
   public final int signature_index;

   Signature_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.signature_index = var1.readUnsignedShort();
   }

   public Signature_attribute(ConstantPool var1, int var2) throws ConstantPoolException {
      this(var1.getUTF8Index("Signature"), var2);
   }

   public Signature_attribute(int var1, int var2) {
      super(var1, 2);
      this.signature_index = var2;
   }

   public String getSignature(ConstantPool var1) throws ConstantPoolException {
      return var1.getUTF8Value(this.signature_index);
   }

   public Signature getParsedSignature() {
      return new Signature(this.signature_index);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitSignature(this, var2);
   }
}
