package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.Type;

public class BreakStatement extends Statement {
   Identifier lbl;

   public BreakStatement(long var1, Identifier var3) {
      super(98, var1);
      this.lbl = var3;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.reach(var1, var3);
      this.checkLabel(var1, var2);
      CheckContext var5 = (CheckContext)(new CheckContext(var2, this)).getBreakContext(this.lbl);
      if (var5 != null) {
         if (var5.frameNumber != var2.frameNumber) {
            var1.error(this.where, "branch.to.uplevel", this.lbl);
         }

         var5.vsBreak = var5.vsBreak.join(var3);
      } else if (this.lbl != null) {
         var1.error(this.where, "label.not.found", this.lbl);
      } else {
         var1.error(this.where, "invalid.break");
      }

      CheckContext var6 = var2.getTryExitContext();
      if (var6 != null) {
         var6.vsTryExit = var6.vsTryExit.join(var3);
      }

      return DEAD_END;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 1;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      CodeContext var4 = new CodeContext(var2, this);
      CodeContext var5 = (CodeContext)var4.getBreakContext(this.lbl);
      this.codeFinally(var1, var2, var3, var5, (Type)null);
      var3.add(this.where, 167, var5.breakLabel);
      var3.add(var4.breakLabel);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("break");
      if (this.lbl != null) {
         var1.print(" " + this.lbl);
      }

      var1.print(";");
   }
}
