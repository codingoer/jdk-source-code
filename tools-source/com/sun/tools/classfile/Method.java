package com.sun.tools.classfile;

import java.io.IOException;

public class Method {
   public final AccessFlags access_flags;
   public final int name_index;
   public final Descriptor descriptor;
   public final Attributes attributes;

   Method(ClassReader var1) throws IOException {
      this.access_flags = new AccessFlags(var1);
      this.name_index = var1.readUnsignedShort();
      this.descriptor = new Descriptor(var1);
      this.attributes = new Attributes(var1);
   }

   public Method(AccessFlags var1, int var2, Descriptor var3, Attributes var4) {
      this.access_flags = var1;
      this.name_index = var2;
      this.descriptor = var3;
      this.attributes = var4;
   }

   public int byteLength() {
      return 6 + this.attributes.byteLength();
   }

   public String getName(ConstantPool var1) throws ConstantPoolException {
      return var1.getUTF8Value(this.name_index);
   }
}
