package com.sun.tools.classfile;

import java.io.IOException;

public class CharacterRangeTable_attribute extends Attribute {
   public static final int CRT_STATEMENT = 1;
   public static final int CRT_BLOCK = 2;
   public static final int CRT_ASSIGNMENT = 4;
   public static final int CRT_FLOW_CONTROLLER = 8;
   public static final int CRT_FLOW_TARGET = 16;
   public static final int CRT_INVOKE = 32;
   public static final int CRT_CREATE = 64;
   public static final int CRT_BRANCH_TRUE = 128;
   public static final int CRT_BRANCH_FALSE = 256;
   public final Entry[] character_range_table;

   CharacterRangeTable_attribute(ClassReader var1, int var2, int var3) throws IOException {
      super(var2, var3);
      int var4 = var1.readUnsignedShort();
      this.character_range_table = new Entry[var4];

      for(int var5 = 0; var5 < var4; ++var5) {
         this.character_range_table[var5] = new Entry(var1);
      }

   }

   public CharacterRangeTable_attribute(ConstantPool var1, Entry[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("CharacterRangeTable"), var2);
   }

   public CharacterRangeTable_attribute(int var1, Entry[] var2) {
      super(var1, 2 + var2.length * CharacterRangeTable_attribute.Entry.length());
      this.character_range_table = var2;
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitCharacterRangeTable(this, var2);
   }

   public static class Entry {
      public final int start_pc;
      public final int end_pc;
      public final int character_range_start;
      public final int character_range_end;
      public final int flags;

      Entry(ClassReader var1) throws IOException {
         this.start_pc = var1.readUnsignedShort();
         this.end_pc = var1.readUnsignedShort();
         this.character_range_start = var1.readInt();
         this.character_range_end = var1.readInt();
         this.flags = var1.readUnsignedShort();
      }

      public static int length() {
         return 14;
      }
   }
}
