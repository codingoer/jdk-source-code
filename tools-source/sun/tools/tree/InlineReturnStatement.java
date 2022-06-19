package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class InlineReturnStatement extends Statement {
   Expression expr;

   public InlineReturnStatement(long var1, Expression var3) {
      super(149, var1);
      this.expr = var3;
   }

   Context getDestination(Context var1) {
      while(var1 != null) {
         if (var1.node != null && (var1.node.op == 150 || var1.node.op == 151)) {
            return var1;
         }

         var1 = var1.prev;
      }

      return null;
   }

   public Statement inline(Environment var1, Context var2) {
      if (this.expr != null) {
         this.expr = this.expr.inlineValue(var1, var2);
      }

      return this;
   }

   public Statement copyInline(Context var1, boolean var2) {
      InlineReturnStatement var3 = (InlineReturnStatement)this.clone();
      if (this.expr != null) {
         var3.expr = this.expr.copyInline(var1);
      }

      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 1 + (this.expr != null ? this.expr.costInline(var1, var2, var3) : 0);
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      if (this.expr != null) {
         this.expr.codeValue(var1, var2, var3);
      }

      CodeContext var4 = (CodeContext)this.getDestination(var2);
      var3.add(this.where, 167, var4.breakLabel);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("inline-return");
      if (this.expr != null) {
         var1.print(" ");
         this.expr.print(var1);
      }

      var1.print(";");
   }
}
