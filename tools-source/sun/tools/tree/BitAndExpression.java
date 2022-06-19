package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class BitAndExpression extends BinaryBitExpression {
   public BitAndExpression(long var1, Expression var3, Expression var4) {
      super(18, var1, var3, var4);
   }

   Expression eval(boolean var1, boolean var2) {
      return new BooleanExpression(this.where, var1 & var2);
   }

   Expression eval(int var1, int var2) {
      return new IntExpression(this.where, var1 & var2);
   }

   Expression eval(long var1, long var3) {
      return new LongExpression(this.where, var1 & var3);
   }

   Expression simplify() {
      if (this.left.equals(true)) {
         return this.right;
      } else if (this.right.equals(true)) {
         return this.left;
      } else if (!this.left.equals(false) && !this.left.equals(0)) {
         return (Expression)(!this.right.equals(false) && !this.right.equals(0) ? this : (new CommaExpression(this.where, this.left, this.right)).simplify());
      } else {
         return (new CommaExpression(this.where, this.right, this.left)).simplify();
      }
   }

   void codeOperation(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 126 + this.type.getTypeCodeOffset());
   }
}
