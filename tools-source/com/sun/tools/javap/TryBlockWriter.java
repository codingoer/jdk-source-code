package com.sun.tools.javap;

import com.sun.tools.classfile.Code_attribute;
import com.sun.tools.classfile.Instruction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class TryBlockWriter extends InstructionDetailWriter {
   private Map pcMap;
   private Map indexMap;
   private ConstantWriter constantWriter;

   static TryBlockWriter instance(Context var0) {
      TryBlockWriter var1 = (TryBlockWriter)var0.get(TryBlockWriter.class);
      if (var1 == null) {
         var1 = new TryBlockWriter(var0);
      }

      return var1;
   }

   protected TryBlockWriter(Context var1) {
      super(var1);
      var1.put(TryBlockWriter.class, this);
      this.constantWriter = ConstantWriter.instance(var1);
   }

   public void reset(Code_attribute var1) {
      this.indexMap = new HashMap();
      this.pcMap = new HashMap();

      for(int var2 = 0; var2 < var1.exception_table.length; ++var2) {
         Code_attribute.Exception_data var3 = var1.exception_table[var2];
         this.indexMap.put(var3, var2);
         this.put(var3.start_pc, var3);
         this.put(var3.end_pc, var3);
         this.put(var3.handler_pc, var3);
      }

   }

   public void writeDetails(Instruction var1) {
      this.writeTrys(var1, TryBlockWriter.NoteKind.END);
      this.writeTrys(var1, TryBlockWriter.NoteKind.START);
      this.writeTrys(var1, TryBlockWriter.NoteKind.HANDLER);
   }

   public void writeTrys(Instruction var1, NoteKind var2) {
      String var3 = this.space(2);
      int var4 = var1.getPC();
      List var5 = (List)this.pcMap.get(var4);
      if (var5 != null) {
         ListIterator var6 = var5.listIterator(var2 == TryBlockWriter.NoteKind.END ? var5.size() : 0);

         while(true) {
            if (var2 == TryBlockWriter.NoteKind.END) {
               if (!var6.hasPrevious()) {
                  break;
               }
            } else if (!var6.hasNext()) {
               break;
            }

            Code_attribute.Exception_data var7 = var2 == TryBlockWriter.NoteKind.END ? (Code_attribute.Exception_data)var6.previous() : (Code_attribute.Exception_data)var6.next();
            if (var2.match(var7, var4)) {
               this.print(var3);
               this.print(var2.text);
               this.print("[");
               this.print(this.indexMap.get(var7));
               this.print("] ");
               if (var7.catch_type == 0) {
                  this.print("finally");
               } else {
                  this.print("#" + var7.catch_type);
                  this.print(" // ");
                  this.constantWriter.write(var7.catch_type);
               }

               this.println();
            }
         }
      }

   }

   private void put(int var1, Code_attribute.Exception_data var2) {
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
      START("try") {
         public boolean match(Code_attribute.Exception_data var1, int var2) {
            return var2 == var1.start_pc;
         }
      },
      END("end try") {
         public boolean match(Code_attribute.Exception_data var1, int var2) {
            return var2 == var1.end_pc;
         }
      },
      HANDLER("catch") {
         public boolean match(Code_attribute.Exception_data var1, int var2) {
            return var2 == var1.handler_pc;
         }
      };

      public final String text;

      private NoteKind(String var3) {
         this.text = var3;
      }

      public abstract boolean match(Code_attribute.Exception_data var1, int var2);

      // $FF: synthetic method
      NoteKind(String var3, Object var4) {
         this(var3);
      }
   }
}
