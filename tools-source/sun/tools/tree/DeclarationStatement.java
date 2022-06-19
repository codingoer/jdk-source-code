package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class DeclarationStatement extends Statement {
   int mod;
   Expression type;
   Statement[] args;

   public DeclarationStatement(long var1, int var3, Expression var4, Statement[] var5) {
      super(107, var1);
      this.mod = var3;
      this.type = var4;
      this.args = var5;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var1.error(this.where, "invalid.decl");
      return this.checkBlockStatement(var1, var2, var3, var4);
   }

   Vset checkBlockStatement(Environment var1, Context var2, Vset var3, Hashtable var4) {
      if (this.labels != null) {
         var1.error(this.where, "declaration.with.label", this.labels[0]);
      }

      var3 = this.reach(var1, var3);
      Type var5 = this.type.toType(var1, var2);

      for(int var6 = 0; var6 < this.args.length; ++var6) {
         var3 = this.args[var6].checkDeclaration(var1, var2, var3, this.mod, var5, var4);
      }

      return var3;
   }

   public Statement inline(Environment var1, Context var2) {
      int var3 = 0;

      for(int var4 = 0; var4 < this.args.length; ++var4) {
         if ((this.args[var4] = this.args[var4].inline(var1, var2)) != null) {
            ++var3;
         }
      }

      return var3 == 0 ? null : this;
   }

   public Statement copyInline(Context var1, boolean var2) {
      DeclarationStatement var3 = (DeclarationStatement)this.clone();
      if (this.type != null) {
         var3.type = this.type.copyInline(var1);
      }

      var3.args = new Statement[this.args.length];

      for(int var4 = 0; var4 < this.args.length; ++var4) {
         if (this.args[var4] != null) {
            var3.args[var4] = this.args[var4].copyInline(var1, var2);
         }
      }

      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      int var4 = 1;

      for(int var5 = 0; var5 < this.args.length; ++var5) {
         if (this.args[var5] != null) {
            var4 += this.args[var5].costInline(var1, var2, var3);
         }
      }

      return var4;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      for(int var4 = 0; var4 < this.args.length; ++var4) {
         if (this.args[var4] != null) {
            this.args[var4].code(var1, var2, var3);
         }
      }

   }

   public void print(PrintStream var1, int var2) {
      var1.print("declare ");
      super.print(var1, var2);
      this.type.print(var1);
      var1.print(" ");

      for(int var3 = 0; var3 < this.args.length; ++var3) {
         if (var3 > 0) {
            var1.print(", ");
         }

         if (this.args[var3] != null) {
            this.args[var3].print(var1);
         } else {
            var1.print("<empty>");
         }
      }

      var1.print(";");
   }
}
