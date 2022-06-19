package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class UnsignedShiftRightExpression extends BinaryShiftExpression {
   public UnsignedShiftRightExpression(long var1, Expression var3, Expression var4) {
      super(28, var1, var3, var4);
   }

   Expression eval(int var1, int var2) {
      return new IntExpression(this.where, var1 >>> var2);
   }

   Expression eval(long var1, long var3) {
      return new LongExpression(this.where, var1 >>> (int)var3);
   }

   Expression simplify() {
      if (this.right.equals(0)) {
         return this.left;
      } else {
         return (Expression)(this.left.equals(0) ? (new CommaExpression(this.where, this.right, this.left)).simplify() : this);
      }
   }

   void codeOperation(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 124 + this.type.getTypeCodeOffset());
   }
}
