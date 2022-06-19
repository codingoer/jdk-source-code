package sun.tools.tree;

import sun.tools.java.Environment;
import sun.tools.java.Type;

public class BinaryCompareExpression extends BinaryExpression {
   public BinaryCompareExpression(int var1, long var2, Expression var4, Expression var5) {
      super(var1, var2, Type.tBoolean, var4, var5);
   }

   void selectType(Environment var1, Context var2, int var3) {
      Type var4 = Type.tInt;
      if ((var3 & 128) != 0) {
         var4 = Type.tDouble;
      } else if ((var3 & 64) != 0) {
         var4 = Type.tFloat;
      } else if ((var3 & 32) != 0) {
         var4 = Type.tLong;
      }

      this.left = this.convert(var1, var2, var4, this.left);
      this.right = this.convert(var1, var2, var4, this.right);
   }
}
