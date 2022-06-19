package com.sun.tools.classfile;

import java.io.IOException;

public class MethodParameters_attribute extends Attribute {
   public final int method_parameter_table_length;
   public final Entry[] method_parameter_table;

   MethodParameters_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.method_parameter_table_length = var1.readUnsignedByte();
      this.method_parameter_table = new Entry[this.method_parameter_table_length];

      for(int var4 = 0; var4 < this.method_parameter_table_length; ++var4) {
         this.method_parameter_table[var4] = new Entry(var1);
      }

   }

   public MethodParameters_attribute(ConstantPool var1, Entry[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("MethodParameters"), var2);
   }

   public MethodParameters_attribute(int var1, Entry[] var2) {
      super(var1, 1 + var2.length * MethodParameters_attribute.Entry.length());
      this.method_parameter_table_length = var2.length;
      this.method_parameter_table = var2;
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitMethodParameters(this, var2);
   }

   public static class Entry {
      public final int name_index;
      public final int flags;

      Entry(ClassReader var1) throws IOException {
         this.name_index = var1.readUnsignedShort();
         this.flags = var1.readUnsignedShort();
      }

      public static int length() {
         return 6;
      }
   }
}
