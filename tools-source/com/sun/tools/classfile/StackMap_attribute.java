package com.sun.tools.classfile;

import java.io.IOException;

public class StackMap_attribute extends Attribute {
   public final int number_of_entries;
   public final stack_map_frame[] entries;

   StackMap_attribute(ClassReader var1, int var2, int var3) throws IOException, StackMapTable_attribute.InvalidStackMap {
      super(var2, var3);
      this.number_of_entries = var1.readUnsignedShort();
      this.entries = new stack_map_frame[this.number_of_entries];

      for(int var4 = 0; var4 < this.number_of_entries; ++var4) {
         this.entries[var4] = new stack_map_frame(var1);
      }

   }

   public StackMap_attribute(ConstantPool var1, stack_map_frame[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("StackMap"), var2);
   }

   public StackMap_attribute(int var1, stack_map_frame[] var2) {
      super(var1, StackMapTable_attribute.length(var2));
      this.number_of_entries = var2.length;
      this.entries = var2;
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitStackMap(this, var2);
   }

   public static class stack_map_frame extends StackMapTable_attribute.full_frame {
      stack_map_frame(ClassReader var1) throws IOException, StackMapTable_attribute.InvalidStackMap {
         super(255, var1);
      }
   }
}
