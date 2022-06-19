package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class NaryExpression extends UnaryExpression {
   Expression[] args;

   NaryExpression(int var1, long var2, Type var4, Expression var5, Expression[] var6) {
      super(var1, var2, var4, var5);
      this.args = var6;
   }

   public Expression copyInline(Context var1) {
      NaryExpression var2 = (NaryExpression)this.clone();
      if (this.right != null) {
         var2.right = this.right.copyInline(var1);
      }

      var2.args = new Expression[this.args.length];

      for(int var3 = 0; var3 < this.args.length; ++var3) {
         if (this.args[var3] != null) {
            var2.args[var3] = this.args[var3].copyInline(var1);
         }
      }

      return var2;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      int var4 = 3;
      if (this.right != null) {
         var4 += this.right.costInline(var1, var2, var3);
      }

      for(int var5 = 0; var5 < this.args.length && var4 < var1; ++var5) {
         if (this.args[var5] != null) {
            var4 += this.args[var5].costInline(var1, var2, var3);
         }
      }

      return var4;
   }

   public void print(PrintStream var1) {
      var1.print("(" + opNames[this.op] + "#" + this.hashCode());
      if (this.right != null) {
         var1.print(" ");
         this.right.print(var1);
      }

      for(int var2 = 0; var2 < this.args.length; ++var2) {
         var1.print(" ");
         if (this.args[var2] != null) {
            this.args[var2].print(var1);
         } else {
            var1.print("<null>");
         }
      }

      var1.print(")");
   }
}
