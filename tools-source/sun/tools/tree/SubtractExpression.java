package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class SubtractExpression extends BinaryArithmeticExpression {
   public SubtractExpression(long var1, Expression var3, Expression var4) {
      super(30, var1, var3, var4);
   }

   Expression eval(int var1, int var2) {
      return new IntExpression(this.where, var1 - var2);
   }

   Expression eval(long var1, long var3) {
      return new LongExpression(this.where, var1 - var3);
   }

   Expression eval(float var1, float var2) {
      return new FloatExpression(this.where, var1 - var2);
   }

   Expression eval(double var1, double var3) {
      return new DoubleExpression(this.where, var1 - var3);
   }

   Expression simplify() {
      if (this.type.inMask(62)) {
         if (this.left.equals(0)) {
            return new NegativeExpression(this.where, this.right);
         }

         if (this.right.equals(0)) {
            return this.left;
         }
      }

      return this;
   }

   void codeOperation(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 100 + this.type.getTypeCodeOffset());
   }
}
