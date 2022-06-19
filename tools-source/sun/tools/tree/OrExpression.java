package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class OrExpression extends BinaryLogicalExpression {
   public OrExpression(long var1, Expression var3, Expression var4) {
      super(14, var1, var3, var4);
   }

   public void checkCondition(Environment var1, Context var2, Vset var3, Hashtable var4, ConditionVars var5) {
      this.left.checkCondition(var1, var2, var3, var4, var5);
      this.left = this.convert(var1, var2, Type.tBoolean, this.left);
      Vset var6 = var5.vsTrue.copy();
      Vset var7 = var5.vsFalse.copy();
      this.right.checkCondition(var1, var2, var7, var4, var5);
      this.right = this.convert(var1, var2, Type.tBoolean, this.right);
      var5.vsTrue = var5.vsTrue.join(var6);
   }

   Expression eval(boolean var1, boolean var2) {
      return new BooleanExpression(this.where, var1 || var2);
   }

   Expression simplify() {
      if (this.right.equals(false)) {
         return this.left;
      } else if (this.left.equals(true)) {
         return this.left;
      } else if (this.left.equals(false)) {
         return this.right;
      } else {
         return (Expression)(this.right.equals(true) ? (new CommaExpression(this.where, this.left, this.right)).simplify() : this);
      }
   }

   void codeBranch(Environment var1, Context var2, Assembler var3, Label var4, boolean var5) {
      if (var5) {
         this.left.codeBranch(var1, var2, var3, var4, true);
         this.right.codeBranch(var1, var2, var3, var4, true);
      } else {
         Label var6 = new Label();
         this.left.codeBranch(var1, var2, var3, var6, true);
         this.right.codeBranch(var1, var2, var3, var4, false);
         var3.add(var6);
      }

   }
}
