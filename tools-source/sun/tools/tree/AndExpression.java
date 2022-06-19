package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class AndExpression extends BinaryLogicalExpression {
   public AndExpression(long var1, Expression var3, Expression var4) {
      super(15, var1, var3, var4);
   }

   public void checkCondition(Environment var1, Context var2, Vset var3, Hashtable var4, ConditionVars var5) {
      this.left.checkCondition(var1, var2, var3, var4, var5);
      this.left = this.convert(var1, var2, Type.tBoolean, this.left);
      Vset var6 = var5.vsTrue.copy();
      Vset var7 = var5.vsFalse.copy();
      this.right.checkCondition(var1, var2, var6, var4, var5);
      this.right = this.convert(var1, var2, Type.tBoolean, this.right);
      var5.vsFalse = var5.vsFalse.join(var7);
   }

   Expression eval(boolean var1, boolean var2) {
      return new BooleanExpression(this.where, var1 && var2);
   }

   Expression simplify() {
      if (this.left.equals(true)) {
         return this.right;
      } else if (this.right.equals(false)) {
         return (new CommaExpression(this.where, this.left, this.right)).simplify();
      } else if (this.right.equals(true)) {
         return this.left;
      } else {
         return (Expression)(this.left.equals(false) ? this.left : this);
      }
   }

   void codeBranch(Environment var1, Context var2, Assembler var3, Label var4, boolean var5) {
      if (var5) {
         Label var6 = new Label();
         this.left.codeBranch(var1, var2, var3, var6, false);
         this.right.codeBranch(var1, var2, var3, var4, true);
         var3.add(var6);
      } else {
         this.left.codeBranch(var1, var2, var3, var4, false);
         this.right.codeBranch(var1, var2, var3, var4, false);
      }

   }
}
