package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.CompilerError;
import sun.tools.java.Constants;
import sun.tools.java.Environment;
import sun.tools.java.MemberDefinition;

class FieldUpdater implements Constants {
   private long where;
   private MemberDefinition field;
   private Expression base;
   private MemberDefinition getter;
   private MemberDefinition setter;
   private int depth;

   public FieldUpdater(long var1, MemberDefinition var3, Expression var4, MemberDefinition var5, MemberDefinition var6) {
      this.where = var1;
      this.field = var3;
      this.base = var4;
      this.getter = var5;
      this.setter = var6;
   }

   public FieldUpdater inline(Environment var1, Context var2) {
      if (this.base != null) {
         if (this.field.isStatic()) {
            this.base = this.base.inline(var1, var2);
         } else {
            this.base = this.base.inlineValue(var1, var2);
         }
      }

      return this;
   }

   public FieldUpdater copyInline(Context var1) {
      return new FieldUpdater(this.where, this.field, this.base.copyInline(var1), this.getter, this.setter);
   }

   public int costInline(int var1, Environment var2, Context var3, boolean var4) {
      int var5 = var4 ? 7 : 3;
      if (!this.field.isStatic() && this.base != null) {
         var5 += this.base.costInline(var1, var2, var3);
      }

      return var5;
   }

   private void codeDup(Assembler var1, int var2, int var3) {
      switch (var2) {
         case 0:
            return;
         case 1:
            switch (var3) {
               case 0:
                  var1.add(this.where, 89);
                  return;
               case 1:
                  var1.add(this.where, 90);
                  return;
               case 2:
                  var1.add(this.where, 91);
                  return;
               default:
                  throw new CompilerError("can't dup: " + var2 + ", " + var3);
            }
         case 2:
            switch (var3) {
               case 0:
                  var1.add(this.where, 92);
                  return;
               case 1:
                  var1.add(this.where, 93);
                  return;
               case 2:
                  var1.add(this.where, 94);
                  return;
            }
      }

      throw new CompilerError("can't dup: " + var2 + ", " + var3);
   }

   public void startUpdate(Environment var1, Context var2, Assembler var3, boolean var4) {
      if (this.getter.isStatic() && this.setter.isStatic()) {
         if (!this.field.isStatic()) {
            this.base.codeValue(var1, var2, var3);
            this.depth = 1;
         } else {
            if (this.base != null) {
               this.base.code(var1, var2, var3);
            }

            this.depth = 0;
         }

         this.codeDup(var3, this.depth, 0);
         var3.add(this.where, 184, this.getter);
         if (var4) {
            this.codeDup(var3, this.field.getType().stackSize(), this.depth);
         }

      } else {
         throw new CompilerError("startUpdate isStatic");
      }
   }

   public void finishUpdate(Environment var1, Context var2, Assembler var3, boolean var4) {
      if (var4) {
         this.codeDup(var3, this.field.getType().stackSize(), this.depth);
      }

      var3.add(this.where, 184, this.setter);
   }

   public void startAssign(Environment var1, Context var2, Assembler var3) {
      if (!this.setter.isStatic()) {
         throw new CompilerError("startAssign isStatic");
      } else {
         if (!this.field.isStatic()) {
            this.base.codeValue(var1, var2, var3);
            this.depth = 1;
         } else {
            if (this.base != null) {
               this.base.code(var1, var2, var3);
            }

            this.depth = 0;
         }

      }
   }

   public void finishAssign(Environment var1, Context var2, Assembler var3, boolean var4) {
      if (var4) {
         this.codeDup(var3, this.field.getType().stackSize(), this.depth);
      }

      var3.add(this.where, 184, this.setter);
   }
}
