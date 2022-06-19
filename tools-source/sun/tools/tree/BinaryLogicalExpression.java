package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public abstract class BinaryLogicalExpression extends BinaryExpression {
   public BinaryLogicalExpression(int var1, long var2, Expression var4, Expression var5) {
      super(var1, var2, Type.tBoolean, var4, var5);
   }

   public Vset checkValue(Environment var1, Context var2, Vset var3, Hashtable var4) {
      ConditionVars var5 = new ConditionVars();
      this.checkCondition(var1, var2, var3, var4, var5);
      return var5.vsTrue.join(var5.vsFalse);
   }

   public abstract void checkCondition(Environment var1, Context var2, Vset var3, Hashtable var4, ConditionVars var5);

   public Expression inline(Environment var1, Context var2) {
      this.left = this.left.inlineValue(var1, var2);
      this.right = this.right.inlineValue(var1, var2);
      return this;
   }
}
