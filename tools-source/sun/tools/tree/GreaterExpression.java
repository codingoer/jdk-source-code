package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;

public class GreaterExpression extends BinaryCompareExpression {
   public GreaterExpression(long var1, Expression var3, Expression var4) {
      super(22, var1, var3, var4);
   }

   Expression eval(int var1, int var2) {
      return new BooleanExpression(this.where, var1 > var2);
   }

   Expression eval(long var1, long var3) {
      return new BooleanExpression(this.where, var1 > var3);
   }

   Expression eval(float var1, float var2) {
      return new BooleanExpression(this.where, var1 > var2);
   }

   Expression eval(double var1, double var3) {
      return new BooleanExpression(this.where, var1 > var3);
   }

   Expression simplify() {
      return (Expression)(this.left.isConstant() && !this.right.isConstant() ? new LessExpression(this.where, this.right, this.left) : this);
   }

   void codeBranch(Environment var1, Context var2, Assembler var3, Label var4, boolean var5) {
      this.left.codeValue(var1, var2, var3);
      switch (this.left.type.getTypeCode()) {
         case 4:
            if (!this.right.equals(0)) {
               this.right.codeValue(var1, var2, var3);
               var3.add(this.where, var5 ? 163 : 164, var4, var5);
               return;
            }
            break;
         case 5:
            this.right.codeValue(var1, var2, var3);
            var3.add(this.where, 148);
            break;
         case 6:
            this.right.codeValue(var1, var2, var3);
            var3.add(this.where, 149);
            break;
         case 7:
            this.right.codeValue(var1, var2, var3);
            var3.add(this.where, 151);
            break;
         default:
            throw new CompilerError("Unexpected Type");
      }

      var3.add(this.where, var5 ? 157 : 158, var4, var5);
   }
}
