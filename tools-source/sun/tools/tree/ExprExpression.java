package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.java.Environment;

public class ExprExpression extends UnaryExpression {
   public ExprExpression(long var1, Expression var3) {
      super(56, var1, var3.type, var3);
   }

   public void checkCondition(Environment var1, Context var2, Vset var3, Hashtable var4, ConditionVars var5) {
      this.right.checkCondition(var1, var2, var3, var4, var5);
      this.type = this.right.type;
   }

   public Vset checkAssignOp(Environment var1, Context var2, Vset var3, Hashtable var4, Expression var5) {
      var3 = this.right.checkAssignOp(var1, var2, var3, var4, var5);
      this.type = this.right.type;
      return var3;
   }

   public FieldUpdater getUpdater(Environment var1, Context var2) {
      return this.right.getUpdater(var1, var2);
   }

   public boolean isNull() {
      return this.right.isNull();
   }

   public boolean isNonNull() {
      return this.right.isNonNull();
   }

   public Object getValue() {
      return this.right.getValue();
   }

   protected StringBuffer inlineValueSB(Environment var1, Context var2, StringBuffer var3) {
      return this.right.inlineValueSB(var1, var2, var3);
   }

   void selectType(Environment var1, Context var2, int var3) {
      this.type = this.right.type;
   }

   Expression simplify() {
      return this.right;
   }
}
