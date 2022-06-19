package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.Type;

public class ContinueStatement extends Statement {
   Identifier lbl;

   public ContinueStatement(long var1, Identifier var3) {
      super(99, var1);
      this.lbl = var3;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);
      this.reach(var1, var3);
      CheckContext var5 = (CheckContext)(new CheckContext(var2, this)).getContinueContext(this.lbl);
      if (var5 != null) {
         switch (var5.node.op) {
            case 92:
            case 93:
            case 94:
               if (var5.frameNumber != var2.frameNumber) {
                  var1.error(this.where, "branch.to.uplevel", this.lbl);
               }

               var5.vsContinue = var5.vsContinue.join(var3);
               break;
            default:
               var1.error(this.where, "invalid.continue");
         }
      } else if (this.lbl != null) {
         var1.error(this.where, "label.not.found", this.lbl);
      } else {
         var1.error(this.where, "invalid.continue");
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
      CodeContext var4 = (CodeContext)var2.getContinueContext(this.lbl);
      this.codeFinally(var1, var2, var3, var4, (Type)null);
      var3.add(this.where, 167, var4.contLabel);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("continue");
      if (this.lbl != null) {
         var1.print(" " + this.lbl);
      }

      var1.print(";");
   }
}
