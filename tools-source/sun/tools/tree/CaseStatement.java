package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class CaseStatement extends Statement {
   Expression expr;

   public CaseStatement(long var1, Expression var3) {
      super(96, var1);
      this.expr = var3;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      if (this.expr != null) {
         this.expr.checkValue(var1, var2, var3, var4);
         this.expr = this.convert(var1, var2, Type.tInt, this.expr);
         this.expr = this.expr.inlineValue(var1, var2);
      }

      return var3.clearDeadEnd();
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 6;
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      if (this.expr == null) {
         var1.print("default");
      } else {
         var1.print("case ");
         this.expr.print(var1);
      }

      var1.print(":");
   }
}
