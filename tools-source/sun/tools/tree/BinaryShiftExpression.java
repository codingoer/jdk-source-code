package sun.tools.tree;

import sun.tools.java.Environment;
import sun.tools.java.Type;

public class BinaryShiftExpression extends BinaryExpression {
   public BinaryShiftExpression(int var1, long var2, Expression var4, Expression var5) {
      super(var1, var2, var4.type, var4, var5);
   }

   Expression eval() {
      return this.left.op == 66 && this.right.op == 65 ? this.eval(((LongExpression)this.left).value, (long)((IntExpression)this.right).value) : super.eval();
   }

   void selectType(Environment var1, Context var2, int var3) {
      if (this.left.type == Type.tLong) {
         this.type = Type.tLong;
      } else if (this.left.type.inMask(62)) {
         this.type = Type.tInt;
         this.left = this.convert(var1, var2, this.type, this.left);
      } else {
         this.type = Type.tError;
      }

      if (this.right.type.inMask(62)) {
         this.right = new ConvertExpression(this.where, Type.tInt, this.right);
      } else {
         this.right = this.convert(var1, var2, Type.tInt, this.right);
      }

   }
}
