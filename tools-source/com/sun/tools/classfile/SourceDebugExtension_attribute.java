package com.sun.tools.classfile;

import java.io.IOException;
import java.nio.charset.Charset;

public class SourceDebugExtension_attribute extends Attribute {
   private static final Charset UTF8 = Charset.forName("UTF-8");
   public final byte[] debug_extension;

   SourceDebugExtension_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.debug_extension = new byte[this.attribute_length];
      var1.readFully(this.debug_extension);
   }

   public SourceDebugExtension_attribute(ConstantPool var1, byte[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("SourceDebugExtension"), var2);
   }

   public SourceDebugExtension_attribute(int var1, byte[] var2) {
      super(var1, var2.length);
      this.debug_extension = var2;
   }

   public String getValue() {
      return new String(this.debug_extension, UTF8);
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitSourceDebugExtension(this, var2);
   }
}
