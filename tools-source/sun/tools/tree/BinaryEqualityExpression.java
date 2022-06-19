package sun.tools.tree;

import sun.tools.java.ClassNotFound;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class BinaryEqualityExpression extends BinaryExpression {
   public BinaryEqualityExpression(int var1, long var2, Expression var4, Expression var5) {
      super(var1, var2, Type.tBoolean, var4, var5);
   }

   void selectType(Environment var1, Context var2, int var3) {
      if ((var3 & 8192) == 0) {
         if ((var3 & 1792) == 0) {
            Type var4;
            if ((var3 & 128) != 0) {
               var4 = Type.tDouble;
            } else if ((var3 & 64) != 0) {
               var4 = Type.tFloat;
            } else if ((var3 & 32) != 0) {
               var4 = Type.tLong;
            } else if ((var3 & 1) != 0) {
               var4 = Type.tBoolean;
            } else {
               var4 = Type.tInt;
            }

            this.left = this.convert(var1, var2, var4, this.left);
            this.right = this.convert(var1, var2, var4, this.right);
         } else {
            try {
               if (var1.explicitCast(this.left.type, this.right.type) || var1.explicitCast(this.right.type, this.left.type)) {
                  return;
               }

               var1.error(this.where, "incompatible.type", this.left.type, this.left.type, this.right.type);
            } catch (ClassNotFound var6) {
               var1.error(this.where, "class.not.found", var6.name, opNames[this.op]);
            }

         }
      }
   }
}
