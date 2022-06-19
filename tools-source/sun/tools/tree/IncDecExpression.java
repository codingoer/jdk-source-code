package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class IncDecExpression extends UnaryExpression {
   private FieldUpdater updater = null;

   public IncDecExpression(int var1, long var2, Expression var4) {
      super(var1, var2, var4.type, var4);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.right.checkAssignOp(var1, var2, var3, var4, this);
      if (this.right.type.inMask(254)) {
         this.type = this.right.type;
      } else {
         if (!this.right.type.isType(13)) {
            var1.error(this.where, "invalid.arg.type", this.right.type, opNames[this.op]);
         }

         this.type = Type.tError;
      }

      this.updater = this.right.getUpdater(var1, var2);
      return var3;
   }

   public Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      return this.checkValue(var1, var2, var3, var4);
   }

   public Expression inline(Environment var1, Context var2) {
      return this.inlineValue(var1, var2);
   }

   public Expression inlineValue(Environment var1, Context var2) {
      this.right = this.right.inlineValue(var1, var2);
      if (this.updater != null) {
         this.updater = this.updater.inline(var1, var2);
      }

      return this;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      if (this.updater == null) {
         return this.right.op == 60 && this.type.isType(4) && ((IdentifierExpression)this.right).field.isLocal() ? 3 : this.right.costInline(var1, var2, var3) + 4;
      } else {
         return this.updater.costInline(var1, var2, var3, true) + 1;
      }
   }

   private void codeIncDecOp(Assembler var1, boolean var2) {
      switch (this.type.getTypeCode()) {
         case 1:
            var1.add(this.where, 18, new Integer(1));
            var1.add(this.where, var2 ? 96 : 100);
            var1.add(this.where, 145);
            break;
         case 2:
            var1.add(this.where, 18, new Integer(1));
            var1.add(this.where, var2 ? 96 : 100);
            var1.add(this.where, 146);
            break;
         case 3:
            var1.add(this.where, 18, new Integer(1));
            var1.add(this.where, var2 ? 96 : 100);
            var1.add(this.where, 147);
            break;
         case 4:
            var1.add(this.where, 18, new Integer(1));
            var1.add(this.where, var2 ? 96 : 100);
            break;
         case 5:
            var1.add(this.where, 20, new Long(1L));
            var1.add(this.where, var2 ? 97 : 101);
            break;
         case 6:
            var1.add(this.where, 18, new Float(1.0F));
            var1.add(this.where, var2 ? 98 : 102);
            break;
         case 7:
            var1.add(this.where, 20, new Double(1.0));
            var1.add(this.where, var2 ? 99 : 103);
            break;
         default:
            throw new CompilerError("invalid type");
      }

   }

   void codeIncDec(Environment var1, Context var2, Assembler var3, boolean var4, boolean var5, boolean var6) {
      int var7;
      if (this.right.op == 60 && this.type.isType(4) && ((IdentifierExpression)this.right).field.isLocal() && this.updater == null) {
         if (var6 && !var5) {
            this.right.codeLoad(var1, var2, var3);
         }

         var7 = ((LocalMember)((IdentifierExpression)this.right).field).number;
         int[] var8 = new int[]{var7, var4 ? 1 : -1};
         var3.add(this.where, 132, var8);
         if (var6 && var5) {
            this.right.codeLoad(var1, var2, var3);
         }

      } else {
         if (this.updater == null) {
            var7 = this.right.codeLValue(var1, var2, var3);
            this.codeDup(var1, var2, var3, var7, 0);
            this.right.codeLoad(var1, var2, var3);
            if (var6 && !var5) {
               this.codeDup(var1, var2, var3, this.type.stackSize(), var7);
            }

            this.codeIncDecOp(var3, var4);
            if (var6 && var5) {
               this.codeDup(var1, var2, var3, this.type.stackSize(), var7);
            }

            this.right.codeStore(var1, var2, var3);
         } else {
            this.updater.startUpdate(var1, var2, var3, var6 && !var5);
            this.codeIncDecOp(var3, var4);
            this.updater.finishUpdate(var1, var2, var3, var6 && var5);
         }

      }
   }
}
