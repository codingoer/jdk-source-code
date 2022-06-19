package sun.tools.tree;

import java.util.Hashtable;
import sun.tools.java.Environment;

public class BinaryAssignExpression extends BinaryExpression {
   Expression implementation;

   BinaryAssignExpression(int var1, long var2, Expression var4, Expression var5) {
      super(var1, var2, var4.type, var4, var5);
   }

   public Expression getImplementation() {
      return (Expression)(this.implementation != null ? this.implementation : this);
   }

   public Expression order() {
      if (this.precedence() >= this.left.precedence()) {
         UnaryExpression var1 = (UnaryExpression)this.left;
         this.left = var1.right;
         var1.right = this.order();
         return var1;
      } else {
         return this;
      }
   }

   public Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      return this.checkValue(var1, var2, var3, var4);
   }

   public Expression inline(Environment var1, Context var2) {
      return this.implementation != null ? this.implementation.inline(var1, var2) : this.inlineValue(var1, var2);
   }

   public Expression inlineValue(Environment var1, Context var2) {
      if (this.implementation != null) {
         return this.implementation.inlineValue(var1, var2);
      } else {
         this.left = this.left.inlineLHS(var1, var2);
         this.right = this.right.inlineValue(var1, var2);
         return this;
      }
   }

   public Expression copyInline(Context var1) {
      return this.implementation != null ? this.implementation.copyInline(var1) : super.copyInline(var1);
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return this.implementation != null ? this.implementation.costInline(var1, var2, var3) : super.costInline(var1, var2, var3);
   }
}
