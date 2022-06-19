package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class IfStatement extends Statement {
   Expression cond;
   Statement ifTrue;
   Statement ifFalse;

   public IfStatement(long var1, Expression var3, Statement var4, Statement var5) {
      super(90, var1);
      this.cond = var3;
      this.ifTrue = var4;
      this.ifFalse = var5;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);
      CheckContext var5 = new CheckContext(var2, this);
      ConditionVars var6 = this.cond.checkCondition(var1, var5, this.reach(var1, var3), var4);
      this.cond = this.convert(var1, var5, Type.tBoolean, this.cond);
      Vset var7 = var6.vsTrue.clearDeadEnd();
      Vset var8 = var6.vsFalse.clearDeadEnd();
      var7 = this.ifTrue.check(var1, var5, var7, var4);
      if (this.ifFalse != null) {
         var8 = this.ifFalse.check(var1, var5, var8, var4);
      }

      var3 = var7.join(var8.join(var5.vsBreak));
      return var2.removeAdditionalVars(var3);
   }

   public Statement inline(Environment var1, Context var2) {
      var2 = new Context(var2, this);
      this.cond = this.cond.inlineValue(var1, var2);
      if (this.ifTrue != null) {
         this.ifTrue = this.ifTrue.inline(var1, var2);
      }

      if (this.ifFalse != null) {
         this.ifFalse = this.ifFalse.inline(var1, var2);
      }

      if (this.cond.equals(true)) {
         return this.eliminate(var1, this.ifTrue);
      } else if (this.cond.equals(false)) {
         return this.eliminate(var1, this.ifFalse);
      } else if (this.ifTrue == null && this.ifFalse == null) {
         return this.eliminate(var1, (new ExpressionStatement(this.where, this.cond)).inline(var1, var2));
      } else if (this.ifTrue == null) {
         this.cond = (new NotExpression(this.cond.where, this.cond)).inlineValue(var1, var2);
         return this.eliminate(var1, new IfStatement(this.where, this.cond, this.ifFalse, (Statement)null));
      } else {
         return this;
      }
   }

   public Statement copyInline(Context var1, boolean var2) {
      IfStatement var3 = (IfStatement)this.clone();
      var3.cond = this.cond.copyInline(var1);
      if (this.ifTrue != null) {
         var3.ifTrue = this.ifTrue.copyInline(var1, var2);
      }

      if (this.ifFalse != null) {
         var3.ifFalse = this.ifFalse.copyInline(var1, var2);
      }

      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      int var4 = 1 + this.cond.costInline(var1, var2, var3);
      if (this.ifTrue != null) {
         var4 += this.ifTrue.costInline(var1, var2, var3);
      }

      if (this.ifFalse != null) {
         var4 += this.ifFalse.costInline(var1, var2, var3);
      }

      return var4;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      CodeContext var4 = new CodeContext(var2, this);
      Label var5 = new Label();
      this.cond.codeBranch(var1, var4, var3, var5, false);
      this.ifTrue.code(var1, var4, var3);
      if (this.ifFalse != null) {
         Label var6 = new Label();
         var3.add(true, this.where, 167, var6);
         var3.add(var5);
         this.ifFalse.code(var1, var4, var3);
         var3.add(var6);
      } else {
         var3.add(var5);
      }

      var3.add(var4.breakLabel);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("if ");
      this.cond.print(var1);
      var1.print(" ");
      this.ifTrue.print(var1, var2);
      if (this.ifFalse != null) {
         var1.print(" else ");
         this.ifFalse.print(var1, var2);
      }

   }
}
