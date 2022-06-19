package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class WhileStatement extends Statement {
   Expression cond;
   Statement body;

   public WhileStatement(long var1, Expression var3, Statement var4) {
      super(93, var1);
      this.cond = var3;
      this.body = var4;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);
      CheckContext var5 = new CheckContext(var2, this);
      Vset var6 = var3.copy();
      ConditionVars var7 = this.cond.checkCondition(var1, var5, this.reach(var1, var3), var4);
      this.cond = this.convert(var1, var5, Type.tBoolean, this.cond);
      var3 = this.body.check(var1, var5, var7.vsTrue, var4);
      var3 = var3.join(var5.vsContinue);
      var2.checkBackBranch(var1, this, var6, var3);
      var3 = var5.vsBreak.join(var7.vsFalse);
      return var2.removeAdditionalVars(var3);
   }

   public Statement inline(Environment var1, Context var2) {
      var2 = new Context(var2, this);
      this.cond = this.cond.inlineValue(var1, var2);
      if (this.body != null) {
         this.body = this.body.inline(var1, var2);
      }

      return this;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 1 + this.cond.costInline(var1, var2, var3) + (this.body != null ? this.body.costInline(var1, var2, var3) : 0);
   }

   public Statement copyInline(Context var1, boolean var2) {
      WhileStatement var3 = (WhileStatement)this.clone();
      var3.cond = this.cond.copyInline(var1);
      if (this.body != null) {
         var3.body = this.body.copyInline(var1, var2);
      }

      return var3;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      CodeContext var4 = new CodeContext(var2, this);
      var3.add(this.where, 167, var4.contLabel);
      Label var5 = new Label();
      var3.add(var5);
      if (this.body != null) {
         this.body.code(var1, var4, var3);
      }

      var3.add(var4.contLabel);
      this.cond.codeBranch(var1, var4, var3, var5, true);
      var3.add(var4.breakLabel);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("while ");
      this.cond.print(var1);
      if (this.body != null) {
         var1.print(" ");
         this.body.print(var1, var2);
      } else {
         var1.print(";");
      }

   }
}
