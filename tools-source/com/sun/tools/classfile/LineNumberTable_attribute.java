package com.sun.tools.classfile;

import java.io.IOException;

public class LineNumberTable_attribute extends Attribute {
   public final int line_number_table_length;
   public final Entry[] line_number_table;

   LineNumberTable_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      this.line_number_table_length = var1.readUnsignedShort();
      this.line_number_table = new Entry[this.line_number_table_length];

      for(int var4 = 0; var4 < this.line_number_table_length; ++var4) {
         this.line_number_table[var4] = new Entry(var1);
      }

   }

   public LineNumberTable_attribute(ConstantPool var1, Entry[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("LineNumberTable"), var2);
   }

   public LineNumberTable_attribute(int var1, Entry[] var2) {
      super(var1, 2 + var2.length * LineNumberTable_attribute.Entry.length());
      this.line_number_table_length = var2.length;
      this.line_number_table = var2;
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitLineNumberTable(this, var2);
   }

   public static class Entry {
      public final int start_pc;
      public final int line_number;

      Entry(ClassReader var1) throws IOException {
         this.start_pc = var1.readUnsignedShort();
         this.line_number = var1.readUnsignedShort();
      }

      public static int length() {
         return 4;
      }
   }
}
