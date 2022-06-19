package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class LengthExpression extends UnaryExpression {
   public LengthExpression(long var1, Expression var3) {
      super(148, var1, Type.tInt, var3);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.right.checkValue(var1, var2, var3, var4);
      if (!this.right.type.isType(9)) {
         var1.error(this.where, "invalid.length", this.right.type);
      }

      return var3;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.right.codeValue(var1, var2, var3);
      var3.add(this.where, 190);
   }
}
