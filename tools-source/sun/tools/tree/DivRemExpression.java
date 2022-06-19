package sun.tools.tree;

import sun.tools.java.Environment;

public abstract class DivRemExpression extends BinaryArithmeticExpression {
   public DivRemExpression(int var1, long var2, Expression var4, Expression var5) {
      super(var1, var2, var4, var5);
   }

   public Expression inline(Environment var1, Context var2) {
      if (this.type.inMask(62)) {
         this.right = this.right.inlineValue(var1, var2);
         if (this.right.isConstant() && !this.right.equals(0)) {
            this.left = this.left.inline(var1, var2);
            return this.left;
         } else {
            this.left = this.left.inlineValue(var1, var2);

            try {
               return this.eval().simplify();
            } catch (ArithmeticException var4) {
               var1.error(this.where, "arithmetic.exception");
               return this;
            }
         }
      } else {
         return super.inline(var1, var2);
      }
   }
}
