package sun.tools.tree;

import sun.tools.java.Environment;
import sun.tools.java.Type;

public class PositiveExpression extends UnaryExpression {
   public PositiveExpression(long var1, Expression var3) {
      super(35, var1, var3.type, var3);
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

   Expression simplify() {
      return this.right;
   }
}
