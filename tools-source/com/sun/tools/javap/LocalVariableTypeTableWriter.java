package com.sun.tools.javap;

import com.sun.tools.classfile.Code_attribute;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.Descriptor;
import com.sun.tools.classfile.Instruction;
import com.sun.tools.classfile.LocalVariableTypeTable_attribute;
import com.sun.tools.classfile.Signature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class LocalVariableTypeTableWriter extends InstructionDetailWriter {
   private ClassWriter classWriter;
   private Code_attribute codeAttr;
   private Map pcMap;

   static LocalVariableTypeTableWriter instance(Context var0) {
      LocalVariableTypeTableWriter var1 = (LocalVariableTypeTableWriter)var0.get(LocalVariableTypeTableWriter.class);
      if (var1 == null) {
         var1 = new LocalVariableTypeTableWriter(var0);
      }

      return var1;
   }

   protected LocalVariableTypeTableWriter(Context var1) {
      super(var1);
      var1.put(LocalVariableTypeTableWriter.class, this);
      this.classWriter = ClassWriter.instance(var1);
   }

   public void reset(Code_attribute var1) {
      this.codeAttr = var1;
      this.pcMap = new HashMap();
      LocalVariableTypeTable_attribute var2 = (LocalVariableTypeTable_attribute)((LocalVariableTypeTable_attribute)var1.attributes.get("LocalVariableTypeTable"));
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.local_variable_table.length; ++var3) {
            LocalVariableTypeTable_attribute.Entry var4 = var2.local_variable_table[var3];
            this.put(var4.start_pc, var4);
            this.put(var4.start_pc + var4.length, var4);
         }

      }
   }

   public void writeDetails(Instruction var1) {
      int var2 = var1.getPC();
      this.writeLocalVariables(var2, LocalVariableTypeTableWriter.NoteKind.END);
      this.writeLocalVariables(var2, LocalVariableTypeTableWriter.NoteKind.START);
   }

   public void flush() {
      int var1 = this.codeAttr.code_length;
      this.writeLocalVariables(var1, LocalVariableTypeTableWriter.NoteKind.END);
   }

   public void writeLocalVariables(int var1, NoteKind var2) {
      ConstantPool var3 = this.classWriter.getClassFile().constant_pool;
      String var4 = this.space(2);
      List var5 = (List)this.pcMap.get(var1);
      if (var5 != null) {
         ListIterator var6 = var5.listIterator(var2 == LocalVariableTypeTableWriter.NoteKind.END ? var5.size() : 0);

         while(true) {
            if (var2 == LocalVariableTypeTableWriter.NoteKind.END) {
               if (!var6.hasPrevious()) {
                  break;
               }
            } else if (!var6.hasNext()) {
               break;
            }

            LocalVariableTypeTable_attribute.Entry var7 = var2 == LocalVariableTypeTableWriter.NoteKind.END ? (LocalVariableTypeTable_attribute.Entry)var6.previous() : (LocalVariableTypeTable_attribute.Entry)var6.next();
            if (var2.match(var7, var1)) {
               this.print(var4);
               this.print(var2.text);
               this.print(" generic local ");
               this.print(var7.index);
               this.print(" // ");
               Signature var8 = new Signature(var7.signature_index);

               try {
                  this.print(var8.getFieldType(var3).toString().replace("/", "."));
               } catch (Descriptor.InvalidDescriptor var11) {
                  this.print(this.report(var11));
               } catch (ConstantPoolException var12) {
                  this.print(this.report(var12));
               }

               this.print(" ");

               try {
                  this.print(var3.getUTF8Value(var7.name_index));
               } catch (ConstantPoolException var10) {
                  this.print(this.report(var10));
               }

               this.println();
            }
         }
      }

   }

   private void put(int var1, LocalVariableTypeTable_attribute.Entry var2) {
      Object var3 = (List)this.pcMap.get(var1);
      if (var3 == null) {
         var3 = new ArrayList();
         this.pcMap.put(var1, var3);
      }

      if (!((List)var3).contains(var2)) {
         ((List)var3).add(var2);
      }

   }

   public static enum NoteKind {
      START("start") {
         public boolean match(LocalVariableTypeTable_attribute.Entry var1, int var2) {
            return var2 == var1.start_pc;
         }
      },
      END("end") {
         public boolean match(LocalVariableTypeTable_attribute.Entry var1, int var2) {
            return var2 == var1.start_pc + var1.length;
         }
      };

      public final String text;

      private NoteKind(String var3) {
         this.text = var3;
      }

      public abstract boolean match(LocalVariableTypeTable_attribute.Entry var1, int var2);

      // $FF: synthetic method
      NoteKind(String var3, Object var4) {
         this(var3);
      }
   }
}
