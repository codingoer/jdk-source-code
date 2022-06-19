package sun.tools.asm;

import java.io.DataOutputStream;
import java.io.IOException;
import sun.tools.java.Environment;
import sun.tools.java.MemberDefinition;

final class LocalVariableTable {
   LocalVariable[] locals = new LocalVariable[8];
   int len;

   void define(MemberDefinition var1, int var2, int var3, int var4) {
      if (var3 < var4) {
         for(int var5 = 0; var5 < this.len; ++var5) {
            if (this.locals[var5].field == var1 && this.locals[var5].slot == var2 && var3 <= this.locals[var5].to && var4 >= this.locals[var5].from) {
               this.locals[var5].from = Math.min(this.locals[var5].from, var3);
               this.locals[var5].to = Math.max(this.locals[var5].to, var4);
               return;
            }
         }

         if (this.len == this.locals.length) {
            LocalVariable[] var6 = new LocalVariable[this.len * 2];
            System.arraycopy(this.locals, 0, var6, 0, this.len);
            this.locals = var6;
         }

         this.locals[this.len++] = new LocalVariable(var1, var2, var3, var4);
      }
   }

   private void trim_ranges() {
      for(int var1 = 0; var1 < this.len; ++var1) {
         for(int var2 = var1 + 1; var2 < this.len; ++var2) {
            if (this.locals[var1].field.getName() == this.locals[var2].field.getName() && this.locals[var1].from <= this.locals[var2].to && this.locals[var1].to >= this.locals[var2].from) {
               if (this.locals[var1].slot < this.locals[var2].slot) {
                  if (this.locals[var1].from < this.locals[var2].from) {
                     this.locals[var1].to = Math.min(this.locals[var1].to, this.locals[var2].from);
                  }
               } else if (this.locals[var1].slot > this.locals[var2].slot && this.locals[var1].from > this.locals[var2].from) {
                  this.locals[var2].to = Math.min(this.locals[var2].to, this.locals[var1].from);
               }
            }
         }
      }

   }

   void write(Environment var1, DataOutputStream var2, ConstantPool var3) throws IOException {
      this.trim_ranges();
      var2.writeShort(this.len);

      for(int var4 = 0; var4 < this.len; ++var4) {
         var2.writeShort(this.locals[var4].from);
         var2.writeShort(this.locals[var4].to - this.locals[var4].from);
         var2.writeShort(var3.index(this.locals[var4].field.getName().toString()));
         var2.writeShort(var3.index(this.locals[var4].field.getType().getTypeSignature()));
         var2.writeShort(this.locals[var4].slot);
      }

   }
}
