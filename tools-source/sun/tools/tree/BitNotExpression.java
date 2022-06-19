package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class BitNotExpression extends UnaryExpression {
   public BitNotExpression(long var1, Expression var3) {
      super(38, var1, var3.type, var3);
   }

   void selectType(Environment var1, Context var2, int var3) {
      if ((var3 & 32) != 0) {
         this.type = Type.tLong;
      } else {
         this.type = Type.tInt;
      }

      this.right = this.convert(var1, var2, this.type, this.right);
   }

   Expression eval(int var1) {
      return new IntExpression(this.where, ~var1);
   }

   Expression eval(long var1) {
      return new LongExpression(this.where, ~var1);
   }

   Expression simplify() {
      return (Expression)(this.right.op == 38 ? ((BitNotExpression)this.right).right : this);
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.right.codeValue(var1, var2, var3);
      if (this.type.isType(4)) {
         var3.add(this.where, 18, new Integer(-1));
         var3.add(this.where, 130);
      } else {
         var3.add(this.where, 20, new Long(-1L));
         var3.add(this.where, 131);
      }

   }
}
