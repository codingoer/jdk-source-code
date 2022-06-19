package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class ExpressionStatement extends Statement {
   Expression expr;

   public ExpressionStatement(long var1, Expression var3) {
      super(106, var1);
      this.expr = var3;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);
      return this.expr.check(var1, var2, this.reach(var1, var3), var4);
   }

   public Statement inline(Environment var1, Context var2) {
      if (this.expr != null) {
         this.expr = this.expr.inline(var1, var2);
         return this.expr == null ? null : this;
      } else {
         return null;
      }
   }

   public Statement copyInline(Context var1, boolean var2) {
      ExpressionStatement var3 = (ExpressionStatement)this.clone();
      var3.expr = this.expr.copyInline(var1);
      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return this.expr.costInline(var1, var2, var3);
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      this.expr.code(var1, var2, var3);
   }

   public Expression firstConstructor() {
      return this.expr.firstConstructor();
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      if (this.expr != null) {
         this.expr.print(var1);
      } else {
         var1.print("<empty>");
      }

      var1.print(";");
   }
}
