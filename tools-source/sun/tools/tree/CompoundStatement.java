package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class CompoundStatement extends Statement {
   Statement[] args;

   public CompoundStatement(long var1, Statement[] var3) {
      super(105, var1);
      this.args = var3;

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (var3[var4] == null) {
            var3[var4] = new CompoundStatement(var1, new Statement[0]);
         }
      }

   }

   public void insertStatement(Statement var1) {
      Statement[] var2 = new Statement[1 + this.args.length];
      var2[0] = var1;

      for(int var3 = 0; var3 < this.args.length; ++var3) {
         var2[var3 + 1] = this.args[var3];
      }

      this.args = var2;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);
      if (this.args.length > 0) {
         var3 = this.reach(var1, var3);
         CheckContext var5 = new CheckContext(var2, this);
         Environment var6 = Context.newEnvironment(var1, var5);

         for(int var7 = 0; var7 < this.args.length; ++var7) {
            var3 = this.args[var7].checkBlockStatement(var6, var5, var3, var4);
         }

         var3 = var3.join(var5.vsBreak);
      }

      return var2.removeAdditionalVars(var3);
   }

   public Statement inline(Environment var1, Context var2) {
      var2 = new Context(var2, this);
      boolean var3 = false;
      int var4 = 0;

      int var5;
      for(var5 = 0; var5 < this.args.length; ++var5) {
         Statement var6 = this.args[var5];
         if (var6 != null) {
            if ((var6 = var6.inline(var1, var2)) != null) {
               if (var6.op == 105 && var6.labels == null) {
                  var4 += ((CompoundStatement)var6).args.length;
               } else {
                  ++var4;
               }

               var3 = true;
            }

            this.args[var5] = var6;
         }
      }

      switch (var4) {
         case 0:
            return null;
         case 1:
            var5 = this.args.length;

            while(var5-- > 0) {
               if (this.args[var5] != null) {
                  return this.eliminate(var1, this.args[var5]);
               }
            }
         default:
            if (var3 || var4 != this.args.length) {
               Statement[] var10 = new Statement[var4];
               int var11 = this.args.length;

               while(true) {
                  while(true) {
                     Statement var7;
                     do {
                        if (var11-- <= 0) {
                           this.args = var10;
                           return this;
                        }

                        var7 = this.args[var11];
                     } while(var7 == null);

                     if (var7.op == 105 && var7.labels == null) {
                        Statement[] var8 = ((CompoundStatement)var7).args;

                        for(int var9 = var8.length; var9-- > 0; var10[var4] = var8[var9]) {
                           --var4;
                        }
                     } else {
                        --var4;
                        var10[var4] = var7;
                     }
                  }
               }
            } else {
               return this;
            }
      }
   }

   public Statement copyInline(Context var1, boolean var2) {
      CompoundStatement var3 = (CompoundStatement)this.clone();
      var3.args = new Statement[this.args.length];

      for(int var4 = 0; var4 < this.args.length; ++var4) {
         var3.args[var4] = this.args[var4].copyInline(var1, var2);
      }

      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      int var4 = 0;

      for(int var5 = 0; var5 < this.args.length && var4 < var1; ++var5) {
         var4 += this.args[var5].costInline(var1, var2, var3);
      }

      return var4;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      CodeContext var4 = new CodeContext(var2, this);

      for(int var5 = 0; var5 < this.args.length; ++var5) {
         this.args[var5].code(var1, var4, var3);
      }

      var3.add(var4.breakLabel);
   }

   public Expression firstConstructor() {
      return this.args.length > 0 ? this.args[0].firstConstructor() : null;
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("{\n");

      for(int var3 = 0; var3 < this.args.length; ++var3) {
         this.printIndent(var1, var2 + 1);
         if (this.args[var3] != null) {
            this.args[var3].print(var1, var2 + 1);
         } else {
            var1.print("<empty>");
         }

         var1.print("\n");
      }

      this.printIndent(var1, var2);
      var1.print("}");
   }
}
