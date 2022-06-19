package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class ForStatement extends Statement {
   Statement init;
   Expression cond;
   Expression inc;
   Statement body;

   public ForStatement(long var1, Statement var3, Expression var4, Expression var5, Statement var6) {
      super(92, var1);
      this.init = var3;
      this.cond = var4;
      this.inc = var5;
      this.body = var6;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);
      var3 = this.reach(var1, var3);
      Context var5 = new Context(var2, this);
      if (this.init != null) {
         var3 = this.init.checkBlockStatement(var1, var5, var3, var4);
      }

      CheckContext var6 = new CheckContext(var5, this);
      Vset var7 = var3.copy();
      ConditionVars var8;
      if (this.cond != null) {
         var8 = this.cond.checkCondition(var1, var6, var3, var4);
         this.cond = this.convert(var1, var6, Type.tBoolean, this.cond);
      } else {
         var8 = new ConditionVars();
         var8.vsFalse = Vset.DEAD_END;
         var8.vsTrue = var3;
      }

      var3 = this.body.check(var1, var6, var8.vsTrue, var4);
      var3 = var3.join(var6.vsContinue);
      if (this.inc != null) {
         var3 = this.inc.check(var1, var6, var3, var4);
      }

      var5.checkBackBranch(var1, this, var7, var3);
      var3 = var6.vsBreak.join(var8.vsFalse);
      return var2.removeAdditionalVars(var3);
   }

   public Statement inline(Environment var1, Context var2) {
      var2 = new Context(var2, this);
      if (this.init != null) {
         Statement[] var3 = new Statement[]{this.init, this};
         this.init = null;
         return (new CompoundStatement(this.where, var3)).inline(var1, var2);
      } else {
         if (this.cond != null) {
            this.cond = this.cond.inlineValue(var1, var2);
         }

         if (this.body != null) {
            this.body = this.body.inline(var1, var2);
         }

         if (this.inc != null) {
            this.inc = this.inc.inline(var1, var2);
         }

         return this;
      }
   }

   public Statement copyInline(Context var1, boolean var2) {
      ForStatement var3 = (ForStatement)this.clone();
      if (this.init != null) {
         var3.init = this.init.copyInline(var1, var2);
      }

      if (this.cond != null) {
         var3.cond = this.cond.copyInline(var1);
      }

      if (this.body != null) {
         var3.body = this.body.copyInline(var1, var2);
      }

      if (this.inc != null) {
         var3.inc = this.inc.copyInline(var1);
      }

      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      int var4 = 2;
      if (this.init != null) {
         var4 += this.init.costInline(var1, var2, var3);
      }

      if (this.cond != null) {
         var4 += this.cond.costInline(var1, var2, var3);
      }

      if (this.body != null) {
         var4 += this.body.costInline(var1, var2, var3);
      }

      if (this.inc != null) {
         var4 += this.inc.costInline(var1, var2, var3);
      }

      return var4;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      CodeContext var4 = new CodeContext(var2, this);
      if (this.init != null) {
         this.init.code(var1, var4, var3);
      }

      Label var5 = new Label();
      Label var6 = new Label();
      var3.add(this.where, 167, var6);
      var3.add(var5);
      if (this.body != null) {
         this.body.code(var1, var4, var3);
      }

      var3.add(var4.contLabel);
      if (this.inc != null) {
         this.inc.code(var1, var4, var3);
      }

      var3.add(var6);
      if (this.cond != null) {
         this.cond.codeBranch(var1, var4, var3, var5, true);
      } else {
         var3.add(this.where, 167, var5);
      }

      var3.add(var4.breakLabel);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("for (");
      if (this.init != null) {
         this.init.print(var1, var2);
         var1.print(" ");
      } else {
         var1.print("; ");
      }

      if (this.cond != null) {
         this.cond.print(var1);
         var1.print(" ");
      }

      var1.print("; ");
      if (this.inc != null) {
         this.inc.print(var1);
      }

      var1.print(") ");
      if (this.body != null) {
         this.body.print(var1, var2);
      } else {
         var1.print(";");
      }

   }
}
