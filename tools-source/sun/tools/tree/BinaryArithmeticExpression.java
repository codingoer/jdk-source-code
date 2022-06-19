package sun.tools.tree;

import sun.tools.java.Environment;
import sun.tools.java.Type;

public class BinaryArithmeticExpression extends BinaryExpression {
   public BinaryArithmeticExpression(int var1, long var2, Expression var4, Expression var5) {
      super(var1, var2, var4.type, var4, var5);
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

      this.left = this.convert(var1, var2, this.type, this.left);
      this.right = this.convert(var1, var2, this.type, this.right);
   }
}
