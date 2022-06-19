package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class AssignExpression extends BinaryAssignExpression {
   private FieldUpdater updater = null;

   public AssignExpression(long var1, Expression var3, Expression var4) {
      super(1, var1, var3, var4);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      if (this.left instanceof IdentifierExpression) {
         var3 = this.right.checkValue(var1, var2, var3, var4);
         var3 = this.left.checkLHS(var1, var2, var3, var4);
      } else {
         var3 = this.left.checkLHS(var1, var2, var3, var4);
         var3 = this.right.checkValue(var1, var2, var3, var4);
      }

      this.type = this.left.type;
      this.right = this.convert(var1, var2, this.type, this.right);
      this.updater = this.left.getAssigner(var1, var2);
      return var3;
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.implementation != null) {
         return this.implementation.inlineValue(var1, var2);
      } else {
         this.left = this.left.inlineLHS(var1, var2);
         this.right = this.right.inlineValue(var1, var2);
         if (this.updater != null) {
            this.updater = this.updater.inline(var1, var2);
         }

         return this;
      }
   }

   public Expression copyInline(Context var1) {
      if (this.implementation != null) {
         return this.implementation.copyInline(var1);
      } else {
         AssignExpression var2 = (AssignExpression)this.clone();
         var2.left = this.left.copyInline(var1);
         var2.right = this.right.copyInline(var1);
         if (this.updater != null) {
            var2.updater = this.updater.copyInline(var1);
         }

         return var2;
      }
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return this.updater != null ? this.right.costInline(var1, var2, var3) + this.updater.costInline(var1, var2, var3, false) : this.right.costInline(var1, var2, var3) + this.left.costInline(var1, var2, var3) + 2;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      if (this.updater == null) {
         int var4 = this.left.codeLValue(var1, var2, var3);
         this.right.codeValue(var1, var2, var3);
         this.codeDup(var1, var2, var3, this.right.type.stackSize(), var4);
         this.left.codeStore(var1, var2, var3);
      } else {
         this.updater.startAssign(var1, var2, var3);
         this.right.codeValue(var1, var2, var3);
         this.updater.finishAssign(var1, var2, var3, true);
      }

   }

   public void code(Environment var1, Context var2, Assembler var3) {
      if (this.updater == null) {
         this.left.codeLValue(var1, var2, var3);
         this.right.codeValue(var1, var2, var3);
         this.left.codeStore(var1, var2, var3);
      } else {
         this.updater.startAssign(var1, var2, var3);
         this.right.codeValue(var1, var2, var3);
         this.updater.finishAssign(var1, var2, var3, false);
      }

   }
}
