package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;

public class NotEqualExpression extends BinaryEqualityExpression {
   public NotEqualExpression(long var1, Expression var3, Expression var4) {
      super(19, var1, var3, var4);
   }

   Expression eval(int var1, int var2) {
      return new BooleanExpression(this.where, var1 != var2);
   }

   Expression eval(long var1, long var3) {
      return new BooleanExpression(this.where, var1 != var3);
   }

   Expression eval(float var1, float var2) {
      return new BooleanExpression(this.where, var1 != var2);
   }

   Expression eval(double var1, double var3) {
      return new BooleanExpression(this.where, var1 != var3);
   }

   Expression eval(boolean var1, boolean var2) {
      return new BooleanExpression(this.where, var1 != var2);
   }

   Expression simplify() {
      return this.left.isConstant() && !this.right.isConstant() ? new NotEqualExpression(this.where, this.right, this.left) : this;
   }

   void codeBranch(Environment var1, Context var2, Assembler var3, Label var4, boolean var5) {
      this.left.codeValue(var1, var2, var3);
      switch (this.left.type.getTypeCode()) {
         case 0:
         case 4:
            if (!this.right.equals(0)) {
               this.right.codeValue(var1, var2, var3);
               var3.add(this.where, var5 ? 160 : 159, var4, var5);
               return;
            }
            break;
         case 1:
         case 2:
         case 3:
         default:
            throw new CompilerError("Unexpected Type");
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
         case 8:
         case 9:
         case 10:
            if (this.right.equals(0)) {
               var3.add(this.where, var5 ? 199 : 198, var4, var5);
            } else {
               this.right.codeValue(var1, var2, var3);
               var3.add(this.where, var5 ? 166 : 165, var4, var5);
            }

            return;
      }

      var3.add(this.where, var5 ? 154 : 153, var4, var5);
   }
}
