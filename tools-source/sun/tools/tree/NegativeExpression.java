package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class NegativeExpression extends UnaryExpression {
   public NegativeExpression(long var1, Expression var3) {
      super(36, var1, var3.type, var3);
   }

   void selectType(Environment var1, Context var2, int var3) {
      if ((var3 & 128) != 0) {
         this.type = Type.tDouble;
      } else if ((var3 & 64) != 0) {
         this.type = Type.tFloat;
      } else if ((var3 & 32) != 0) {
         this.type = Type.tLong;
      } else {
         this.type = Type.tInt;
      }

      this.right = this.convert(var1, var2, this.type, this.right);
   }

   Expression eval(int var1) {
      return new IntExpression(this.where, -var1);
   }

   Expression eval(long var1) {
      return new LongExpression(this.where, -var1);
   }

   Expression eval(float var1) {
      return new FloatExpression(this.where, -var1);
   }

   Expression eval(double var1) {
      return new DoubleExpression(this.where, -var1);
   }

   Expression simplify() {
      return (Expression)(this.right.op == 36 ? ((NegativeExpression)this.right).right : this);
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.right.codeValue(var1, var2, var3);
      var3.add(this.where, 116 + this.type.getTypeCodeOffset());
   }
}
