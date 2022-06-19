package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.asm.SwitchData;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class SwitchStatement extends Statement {
   Expression expr;
   Statement[] args;

   public SwitchStatement(long var1, Expression var3, Statement[] var4) {
      super(95, var1);
      this.expr = var3;
      this.args = var4;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);
      CheckContext var5 = new CheckContext(var2, this);
      var3 = this.expr.checkValue(var1, var5, this.reach(var1, var3), var4);
      Type var6 = this.expr.type;
      this.expr = this.convert(var1, var5, Type.tInt, this.expr);
      Hashtable var7 = new Hashtable();
      boolean var8 = false;
      Vset var9 = DEAD_END;

      for(int var10 = 0; var10 < this.args.length; ++var10) {
         Statement var11 = this.args[var10];
         if (var11.op == 96) {
            var9 = var11.check(var1, var5, var9.join(var3.copy()), var4);
            Expression var12 = ((CaseStatement)var11).expr;
            if (var12 != null) {
               if (var12 instanceof IntegerExpression) {
                  Integer var13 = (Integer)((Integer)((IntegerExpression)var12).getValue());
                  int var14 = var13;
                  if (var7.get(var12) != null) {
                     var1.error(var11.where, "duplicate.label", var13);
                  } else {
                     var7.put(var12, var11);
                     boolean var15;
                     switch (var6.getTypeCode()) {
                        case 1:
                           var15 = var14 != (byte)var14;
                           break;
                        case 2:
                           var15 = var14 != (char)var14;
                           break;
                        case 3:
                           var15 = var14 != (short)var14;
                           break;
                        default:
                           var15 = false;
                     }

                     if (var15) {
                        var1.error(var11.where, "switch.overflow", var13, var6);
                     }
                  }
               } else if (!var12.isConstant() || var12.getType() != Type.tInt) {
                  var1.error(var11.where, "const.expr.required");
               }
            } else {
               if (var8) {
                  var1.error(var11.where, "duplicate.default");
               }

               var8 = true;
            }
         } else {
            var9 = var11.checkBlockStatement(var1, var5, var9, var4);
         }
      }

      if (!var9.isDeadEnd()) {
         var5.vsBreak = var5.vsBreak.join(var9);
      }

      if (var8) {
         var3 = var5.vsBreak;
      }

      return var2.removeAdditionalVars(var3);
   }

   public Statement inline(Environment var1, Context var2) {
      var2 = new Context(var2, this);
      this.expr = this.expr.inlineValue(var1, var2);

      for(int var3 = 0; var3 < this.args.length; ++var3) {
         if (this.args[var3] != null) {
            this.args[var3] = this.args[var3].inline(var1, var2);
         }
      }

      return this;
   }

   public Statement copyInline(Context var1, boolean var2) {
      SwitchStatement var3 = (SwitchStatement)this.clone();
      var3.expr = this.expr.copyInline(var1);
      var3.args = new Statement[this.args.length];

      for(int var4 = 0; var4 < this.args.length; ++var4) {
         if (this.args[var4] != null) {
            var3.args[var4] = this.args[var4].copyInline(var1, var2);
         }
      }

      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      int var4 = this.expr.costInline(var1, var2, var3);

      for(int var5 = 0; var5 < this.args.length && var4 < var1; ++var5) {
         if (this.args[var5] != null) {
            var4 += this.args[var5].costInline(var1, var2, var3);
         }
      }

      return var4;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      CodeContext var4 = new CodeContext(var2, this);
      this.expr.codeValue(var1, var4, var3);
      SwitchData var5 = new SwitchData();
      boolean var6 = false;

      int var7;
      Statement var8;
      Expression var9;
      for(var7 = 0; var7 < this.args.length; ++var7) {
         var8 = this.args[var7];
         if (var8 != null && var8.op == 96) {
            var9 = ((CaseStatement)var8).expr;
            if (var9 != null) {
               var5.add(((IntegerExpression)var9).value, new Label());
            } else {
               var6 = true;
            }
         }
      }

      if (var1.coverage()) {
         var5.initTableCase();
      }

      var3.add(this.where, 170, var5);

      for(var7 = 0; var7 < this.args.length; ++var7) {
         var8 = this.args[var7];
         if (var8 != null) {
            if (var8.op == 96) {
               var9 = ((CaseStatement)var8).expr;
               if (var9 != null) {
                  var3.add(var5.get(((IntegerExpression)var9).value));
                  var5.addTableCase(((IntegerExpression)var9).value, var8.where);
               } else {
                  var3.add(var5.getDefaultLabel());
                  var5.addTableDefault(var8.where);
               }
            } else {
               var8.code(var1, var4, var3);
            }
         }
      }

      if (!var6) {
         var3.add(var5.getDefaultLabel());
      }

      var3.add(var4.breakLabel);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("switch (");
      this.expr.print(var1);
      var1.print(") {\n");

      for(int var3 = 0; var3 < this.args.length; ++var3) {
         if (this.args[var3] != null) {
            this.printIndent(var1, var2 + 1);
            this.args[var3].print(var1, var2 + 1);
            var1.print("\n");
         }
      }

      this.printIndent(var1, var2);
      var1.print("}");
   }
}
