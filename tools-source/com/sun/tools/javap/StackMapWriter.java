package com.sun.tools.javap;

import com.sun.tools.classfile.Code_attribute;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.Descriptor;
import com.sun.tools.classfile.Instruction;
import com.sun.tools.classfile.Method;
import com.sun.tools.classfile.StackMapTable_attribute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StackMapWriter extends InstructionDetailWriter {
   private Map map;
   private ClassWriter classWriter;
   private final StackMapTable_attribute.verification_type_info[] empty = new StackMapTable_attribute.verification_type_info[0];

   static StackMapWriter instance(Context var0) {
      StackMapWriter var1 = (StackMapWriter)var0.get(StackMapWriter.class);
      if (var1 == null) {
         var1 = new StackMapWriter(var0);
      }

      return var1;
   }

   protected StackMapWriter(Context var1) {
      super(var1);
      var1.put(StackMapWriter.class, this);
      this.classWriter = ClassWriter.instance(var1);
   }

   public void reset(Code_attribute var1) {
      this.setStackMap((StackMapTable_attribute)var1.attributes.get("StackMapTable"));
   }

   void setStackMap(StackMapTable_attribute var1) {
      if (var1 == null) {
         this.map = null;
      } else {
         Method var2 = this.classWriter.getMethod();
         Descriptor var3 = var2.descriptor;

         String[] var4;
         try {
            ConstantPool var5 = this.classWriter.getClassFile().constant_pool;
            String var6 = var3.getParameterTypes(var5);
            var4 = var6.substring(1, var6.length() - 1).split("[, ]+");
         } catch (ConstantPoolException var10) {
            return;
         } catch (Descriptor.InvalidDescriptor var11) {
            return;
         }

         boolean var12 = var2.access_flags.is(8);
         StackMapTable_attribute.verification_type_info[] var13 = new StackMapTable_attribute.verification_type_info[(var12 ? 0 : 1) + var4.length];
         if (!var12) {
            var13[0] = new CustomVerificationTypeInfo("this");
         }

         for(int var7 = 0; var7 < var4.length; ++var7) {
            var13[(var12 ? 0 : 1) + var7] = new CustomVerificationTypeInfo(var4[var7].replace(".", "/"));
         }

         this.map = new HashMap();
         StackMapBuilder var14 = new StackMapBuilder();
         int var8 = -1;
         this.map.put(var8, new StackMap(var13, this.empty));

         for(int var9 = 0; var9 < var1.entries.length; ++var9) {
            var8 = (Integer)var1.entries[var9].accept(var14, var8);
         }

      }
   }

   public void writeInitialDetails() {
      this.writeDetails(-1);
   }

   public void writeDetails(Instruction var1) {
      this.writeDetails(var1.getPC());
   }

   private void writeDetails(int var1) {
      if (this.map != null) {
         StackMap var2 = (StackMap)this.map.get(var1);
         if (var2 != null) {
            this.print("StackMap locals: ", var2.locals);
            this.print("StackMap stack: ", var2.stack);
         }

      }
   }

   void print(String var1, StackMapTable_attribute.verification_type_info[] var2) {
      this.print(var1);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         this.print(" ");
         this.print(var2[var3]);
      }

      this.println();
   }

   void print(StackMapTable_attribute.verification_type_info var1) {
      if (var1 == null) {
         this.print("ERROR");
      } else {
         switch (var1.tag) {
            case -1:
               this.print(((CustomVerificationTypeInfo)var1).text);
               break;
            case 0:
               this.print("top");
               break;
            case 1:
               this.print("int");
               break;
            case 2:
               this.print("float");
               break;
            case 3:
               this.print("double");
               break;
            case 4:
               this.print("long");
               break;
            case 5:
               this.print("null");
               break;
            case 6:
               this.print("uninit_this");
               break;
            case 7:
               try {
                  ConstantPool var2 = this.classWriter.getClassFile().constant_pool;
                  ConstantPool.CONSTANT_Class_info var3 = var2.getClassInfo(((StackMapTable_attribute.Object_variable_info)var1).cpool_index);
                  this.print(var2.getUTF8Value(var3.name_index));
               } catch (ConstantPoolException var4) {
                  this.print("??");
               }
               break;
            case 8:
               this.print(((StackMapTable_attribute.Uninitialized_variable_info)var1).offset);
         }

      }
   }

   static class CustomVerificationTypeInfo extends StackMapTable_attribute.verification_type_info {
      private String text;

      public CustomVerificationTypeInfo(String var1) {
         super(-1);
         this.text = var1;
      }
   }

   static class StackMap {
      private final StackMapTable_attribute.verification_type_info[] locals;
      private final StackMapTable_attribute.verification_type_info[] stack;

      StackMap(StackMapTable_attribute.verification_type_info[] var1, StackMapTable_attribute.verification_type_info[] var2) {
         this.locals = var1;
         this.stack = var2;
      }
   }

   class StackMapBuilder implements StackMapTable_attribute.stack_map_frame.Visitor {
      public Integer visit_same_frame(StackMapTable_attribute.same_frame var1, Integer var2) {
         int var3 = var2 + var1.getOffsetDelta() + 1;
         StackMap var4 = (StackMap)StackMapWriter.this.map.get(var2);

         assert var4 != null;

         StackMapWriter.this.map.put(var3, var4);
         return var3;
      }

      public Integer visit_same_locals_1_stack_item_frame(StackMapTable_attribute.same_locals_1_stack_item_frame var1, Integer var2) {
         int var3 = var2 + var1.getOffsetDelta() + 1;
         StackMap var4 = (StackMap)StackMapWriter.this.map.get(var2);

         assert var4 != null;

         StackMap var5 = new StackMap(var4.locals, var1.stack);
         StackMapWriter.this.map.put(var3, var5);
         return var3;
      }

      public Integer visit_same_locals_1_stack_item_frame_extended(StackMapTable_attribute.same_locals_1_stack_item_frame_extended var1, Integer var2) {
         int var3 = var2 + var1.getOffsetDelta() + 1;
         StackMap var4 = (StackMap)StackMapWriter.this.map.get(var2);

         assert var4 != null;

         StackMap var5 = new StackMap(var4.locals, var1.stack);
         StackMapWriter.this.map.put(var3, var5);
         return var3;
      }

      public Integer visit_chop_frame(StackMapTable_attribute.chop_frame var1, Integer var2) {
         int var3 = var2 + var1.getOffsetDelta() + 1;
         StackMap var4 = (StackMap)StackMapWriter.this.map.get(var2);

         assert var4 != null;

         int var5 = 251 - var1.frame_type;
         StackMapTable_attribute.verification_type_info[] var6 = (StackMapTable_attribute.verification_type_info[])Arrays.copyOf(var4.locals, var4.locals.length - var5);
         StackMap var7 = new StackMap(var6, StackMapWriter.this.empty);
         StackMapWriter.this.map.put(var3, var7);
         return var3;
      }

      public Integer visit_same_frame_extended(StackMapTable_attribute.same_frame_extended var1, Integer var2) {
         int var3 = var2 + var1.getOffsetDelta();
         StackMap var4 = (StackMap)StackMapWriter.this.map.get(var2);

         assert var4 != null;

         StackMapWriter.this.map.put(var3, var4);
         return var3;
      }

      public Integer visit_append_frame(StackMapTable_attribute.append_frame var1, Integer var2) {
         int var3 = var2 + var1.getOffsetDelta() + 1;
         StackMap var4 = (StackMap)StackMapWriter.this.map.get(var2);

         assert var4 != null;

         StackMapTable_attribute.verification_type_info[] var5 = new StackMapTable_attribute.verification_type_info[var4.locals.length + var1.locals.length];
         System.arraycopy(var4.locals, 0, var5, 0, var4.locals.length);
         System.arraycopy(var1.locals, 0, var5, var4.locals.length, var1.locals.length);
         StackMap var6 = new StackMap(var5, StackMapWriter.this.empty);
         StackMapWriter.this.map.put(var3, var6);
         return var3;
      }

      public Integer visit_full_frame(StackMapTable_attribute.full_frame var1, Integer var2) {
         int var3 = var2 + var1.getOffsetDelta() + 1;
         StackMap var4 = new StackMap(var1.locals, var1.stack);
         StackMapWriter.this.map.put(var3, var4);
         return var3;
      }
   }
}
