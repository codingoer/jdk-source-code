package com.sun.tools.javap;

import com.sun.tools.classfile.Code_attribute;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.Descriptor;
import com.sun.tools.classfile.Instruction;
import com.sun.tools.classfile.LocalVariableTable_attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class LocalVariableTableWriter extends InstructionDetailWriter {
   private ClassWriter classWriter;
   private Code_attribute codeAttr;
   private Map pcMap;

   static LocalVariableTableWriter instance(Context var0) {
      LocalVariableTableWriter var1 = (LocalVariableTableWriter)var0.get(LocalVariableTableWriter.class);
      if (var1 == null) {
         var1 = new LocalVariableTableWriter(var0);
      }

      return var1;
   }

   protected LocalVariableTableWriter(Context var1) {
      super(var1);
      var1.put(LocalVariableTableWriter.class, this);
      this.classWriter = ClassWriter.instance(var1);
   }

   public void reset(Code_attribute var1) {
      this.codeAttr = var1;
      this.pcMap = new HashMap();
      LocalVariableTable_attribute var2 = (LocalVariableTable_attribute)((LocalVariableTable_attribute)var1.attributes.get("LocalVariableTable"));
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.local_variable_table.length; ++var3) {
            LocalVariableTable_attribute.Entry var4 = var2.local_variable_table[var3];
            this.put(var4.start_pc, var4);
            this.put(var4.start_pc + var4.length, var4);
         }

      }
   }

   public void writeDetails(Instruction var1) {
      int var2 = var1.getPC();
      this.writeLocalVariables(var2, LocalVariableTableWriter.NoteKind.END);
      this.writeLocalVariables(var2, LocalVariableTableWriter.NoteKind.START);
   }

   public void flush() {
      int var1 = this.codeAttr.code_length;
      this.writeLocalVariables(var1, LocalVariableTableWriter.NoteKind.END);
   }

   public void writeLocalVariables(int var1, NoteKind var2) {
      ConstantPool var3 = this.classWriter.getClassFile().constant_pool;
      String var4 = this.space(2);
      List var5 = (List)this.pcMap.get(var1);
      if (var5 != null) {
         ListIterator var6 = var5.listIterator(var2 == LocalVariableTableWriter.NoteKind.END ? var5.size() : 0);

         while(true) {
            if (var2 == LocalVariableTableWriter.NoteKind.END) {
               if (!var6.hasPrevious()) {
                  break;
               }
            } else if (!var6.hasNext()) {
               break;
            }

            LocalVariableTable_attribute.Entry var7 = var2 == LocalVariableTableWriter.NoteKind.END ? (LocalVariableTable_attribute.Entry)var6.previous() : (LocalVariableTable_attribute.Entry)var6.next();
            if (var2.match(var7, var1)) {
               this.print(var4);
               this.print(var2.text);
               this.print(" local ");
               this.print(var7.index);
               this.print(" // ");
               Descriptor var8 = new Descriptor(var7.descriptor_index);

               try {
                  this.print(var8.getFieldType(var3));
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

   private void put(int var1, LocalVariableTable_attribute.Entry var2) {
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
         public boolean match(LocalVariableTable_attribute.Entry var1, int var2) {
            return var2 == var1.start_pc;
         }
      },
      END("end") {
         public boolean match(LocalVariableTable_attribute.Entry var1, int var2) {
            return var2 == var1.start_pc + var1.length;
         }
      };

      public final String text;

      private NoteKind(String var3) {
         this.text = var3;
      }

      public abstract boolean match(LocalVariableTable_attribute.Entry var1, int var2);

      // $FF: synthetic method
      NoteKind(String var3, Object var4) {
         this(var3);
      }
   }
}
