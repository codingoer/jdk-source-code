package com.sun.tools.classfile;

import java.io.IOException;

public class StackMapTable_attribute extends Attribute {
   public final int number_of_entries;
   public final stack_map_frame[] entries;

   StackMapTable_attribute(ClassReader var1, int var2, int var3) throws IOException, InvalidStackMap {
      super(var2, var3);
      this.number_of_entries = var1.readUnsignedShort();
      this.entries = new stack_map_frame[this.number_of_entries];

      for(int var4 = 0; var4 < this.number_of_entries; ++var4) {
         this.entries[var4] = StackMapTable_attribute.stack_map_frame.read(var1);
      }

   }

   public StackMapTable_attribute(ConstantPool var1, stack_map_frame[] var2) throws ConstantPoolException {
      this(var1.getUTF8Index("StackMapTable"), var2);
   }

   public StackMapTable_attribute(int var1, stack_map_frame[] var2) {
      super(var1, length(var2));
      this.number_of_entries = var2.length;
      this.entries = var2;
   }

   public Object accept(Attribute.Visitor var1, Object var2) {
      return var1.visitStackMapTable(this, var2);
   }

   static int length(stack_map_frame[] var0) {
      int var1 = 2;
      stack_map_frame[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         stack_map_frame var5 = var2[var4];
         var1 += var5.length();
      }

      return var1;
   }

   public static class Uninitialized_variable_info extends verification_type_info {
      public final int offset;

      Uninitialized_variable_info(ClassReader var1) throws IOException {
         super(8);
         this.offset = var1.readUnsignedShort();
      }

      public int length() {
         return super.length() + 2;
      }
   }

   public static class Object_variable_info extends verification_type_info {
      public final int cpool_index;

      Object_variable_info(ClassReader var1) throws IOException {
         super(7);
         this.cpool_index = var1.readUnsignedShort();
      }

      public int length() {
         return super.length() + 2;
      }
   }

   public static class verification_type_info {
      public static final int ITEM_Top = 0;
      public static final int ITEM_Integer = 1;
      public static final int ITEM_Float = 2;
      public static final int ITEM_Long = 4;
      public static final int ITEM_Double = 3;
      public static final int ITEM_Null = 5;
      public static final int ITEM_UninitializedThis = 6;
      public static final int ITEM_Object = 7;
      public static final int ITEM_Uninitialized = 8;
      public final int tag;

      static verification_type_info read(ClassReader var0) throws IOException, InvalidStackMap {
         int var1 = var0.readUnsignedByte();
         switch (var1) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
               return new verification_type_info(var1);
            case 7:
               return new Object_variable_info(var0);
            case 8:
               return new Uninitialized_variable_info(var0);
            default:
               throw new InvalidStackMap("unrecognized verification_type_info tag");
         }
      }

      protected verification_type_info(int var1) {
         this.tag = var1;
      }

      public int length() {
         return 1;
      }
   }

   public static class full_frame extends stack_map_frame {
      public final int offset_delta;
      public final int number_of_locals;
      public final verification_type_info[] locals;
      public final int number_of_stack_items;
      public final verification_type_info[] stack;

      full_frame(int var1, ClassReader var2) throws IOException, InvalidStackMap {
         super(var1);
         this.offset_delta = var2.readUnsignedShort();
         this.number_of_locals = var2.readUnsignedShort();
         this.locals = new verification_type_info[this.number_of_locals];

         int var3;
         for(var3 = 0; var3 < this.locals.length; ++var3) {
            this.locals[var3] = StackMapTable_attribute.verification_type_info.read(var2);
         }

         this.number_of_stack_items = var2.readUnsignedShort();
         this.stack = new verification_type_info[this.number_of_stack_items];

         for(var3 = 0; var3 < this.stack.length; ++var3) {
            this.stack[var3] = StackMapTable_attribute.verification_type_info.read(var2);
         }

      }

      public int length() {
         int var1 = super.length() + 2;
         verification_type_info[] var2 = this.locals;
         int var3 = var2.length;

         int var4;
         verification_type_info var5;
         for(var4 = 0; var4 < var3; ++var4) {
            var5 = var2[var4];
            var1 += var5.length();
         }

         var1 += 2;
         var2 = this.stack;
         var3 = var2.length;

         for(var4 = 0; var4 < var3; ++var4) {
            var5 = var2[var4];
            var1 += var5.length();
         }

         return var1;
      }

      public Object accept(stack_map_frame.Visitor var1, Object var2) {
         return var1.visit_full_frame(this, var2);
      }

      public int getOffsetDelta() {
         return this.offset_delta;
      }
   }

   public static class append_frame extends stack_map_frame {
      public final int offset_delta;
      public final verification_type_info[] locals;

      append_frame(int var1, ClassReader var2) throws IOException, InvalidStackMap {
         super(var1);
         this.offset_delta = var2.readUnsignedShort();
         this.locals = new verification_type_info[var1 - 251];

         for(int var3 = 0; var3 < this.locals.length; ++var3) {
            this.locals[var3] = StackMapTable_attribute.verification_type_info.read(var2);
         }

      }

      public int length() {
         int var1 = super.length() + 2;
         verification_type_info[] var2 = this.locals;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            verification_type_info var5 = var2[var4];
            var1 += var5.length();
         }

         return var1;
      }

      public Object accept(stack_map_frame.Visitor var1, Object var2) {
         return var1.visit_append_frame(this, var2);
      }

      public int getOffsetDelta() {
         return this.offset_delta;
      }
   }

   public static class same_frame_extended extends stack_map_frame {
      public final int offset_delta;

      same_frame_extended(int var1, ClassReader var2) throws IOException {
         super(var1);
         this.offset_delta = var2.readUnsignedShort();
      }

      public int length() {
         return super.length() + 2;
      }

      public Object accept(stack_map_frame.Visitor var1, Object var2) {
         return var1.visit_same_frame_extended(this, var2);
      }

      public int getOffsetDelta() {
         return this.offset_delta;
      }
   }

   public static class chop_frame extends stack_map_frame {
      public final int offset_delta;

      chop_frame(int var1, ClassReader var2) throws IOException {
         super(var1);
         this.offset_delta = var2.readUnsignedShort();
      }

      public int length() {
         return super.length() + 2;
      }

      public Object accept(stack_map_frame.Visitor var1, Object var2) {
         return var1.visit_chop_frame(this, var2);
      }

      public int getOffsetDelta() {
         return this.offset_delta;
      }
   }

   public static class same_locals_1_stack_item_frame_extended extends stack_map_frame {
      public final int offset_delta;
      public final verification_type_info[] stack;

      same_locals_1_stack_item_frame_extended(int var1, ClassReader var2) throws IOException, InvalidStackMap {
         super(var1);
         this.offset_delta = var2.readUnsignedShort();
         this.stack = new verification_type_info[1];
         this.stack[0] = StackMapTable_attribute.verification_type_info.read(var2);
      }

      public int length() {
         return super.length() + 2 + this.stack[0].length();
      }

      public Object accept(stack_map_frame.Visitor var1, Object var2) {
         return var1.visit_same_locals_1_stack_item_frame_extended(this, var2);
      }

      public int getOffsetDelta() {
         return this.offset_delta;
      }
   }

   public static class same_locals_1_stack_item_frame extends stack_map_frame {
      public final verification_type_info[] stack = new verification_type_info[1];

      same_locals_1_stack_item_frame(int var1, ClassReader var2) throws IOException, InvalidStackMap {
         super(var1);
         this.stack[0] = StackMapTable_attribute.verification_type_info.read(var2);
      }

      public int length() {
         return super.length() + this.stack[0].length();
      }

      public Object accept(stack_map_frame.Visitor var1, Object var2) {
         return var1.visit_same_locals_1_stack_item_frame(this, var2);
      }

      public int getOffsetDelta() {
         return this.frame_type - 64;
      }
   }

   public static class same_frame extends stack_map_frame {
      same_frame(int var1) {
         super(var1);
      }

      public Object accept(stack_map_frame.Visitor var1, Object var2) {
         return var1.visit_same_frame(this, var2);
      }

      public int getOffsetDelta() {
         return this.frame_type;
      }
   }

   public abstract static class stack_map_frame {
      public final int frame_type;

      static stack_map_frame read(ClassReader var0) throws IOException, InvalidStackMap {
         int var1 = var0.readUnsignedByte();
         if (var1 <= 63) {
            return new same_frame(var1);
         } else if (var1 <= 127) {
            return new same_locals_1_stack_item_frame(var1, var0);
         } else if (var1 <= 246) {
            throw new Error("unknown frame_type " + var1);
         } else if (var1 == 247) {
            return new same_locals_1_stack_item_frame_extended(var1, var0);
         } else if (var1 <= 250) {
            return new chop_frame(var1, var0);
         } else if (var1 == 251) {
            return new same_frame_extended(var1, var0);
         } else {
            return (stack_map_frame)(var1 <= 254 ? new append_frame(var1, var0) : new full_frame(var1, var0));
         }
      }

      protected stack_map_frame(int var1) {
         this.frame_type = var1;
      }

      public int length() {
         return 1;
      }

      public abstract int getOffsetDelta();

      public abstract Object accept(Visitor var1, Object var2);

      public interface Visitor {
         Object visit_same_frame(same_frame var1, Object var2);

         Object visit_same_locals_1_stack_item_frame(same_locals_1_stack_item_frame var1, Object var2);

         Object visit_same_locals_1_stack_item_frame_extended(same_locals_1_stack_item_frame_extended var1, Object var2);

         Object visit_chop_frame(chop_frame var1, Object var2);

         Object visit_same_frame_extended(same_frame_extended var1, Object var2);

         Object visit_append_frame(append_frame var1, Object var2);

         Object visit_full_frame(full_frame var1, Object var2);
      }
   }

   static class InvalidStackMap extends AttributeException {
      private static final long serialVersionUID = -5659038410855089780L;

      InvalidStackMap(String var1) {
         super(var1);
      }
   }
}
