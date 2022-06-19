package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class ReturnStatement extends Statement {
   Expression expr;

   public ReturnStatement(long var1, Expression var3) {
      super(100, var1);
      this.expr = var3;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);
      var3 = this.reach(var1, var3);
      if (this.expr != null) {
         var3 = this.expr.checkValue(var1, var2, var3, var4);
      }

      if (var2.field.isInitializer()) {
         var1.error(this.where, "return.inside.static.initializer");
         return DEAD_END;
      } else {
         if (var2.field.getType().getReturnType().isType(11)) {
            if (this.expr != null) {
               if (var2.field.isConstructor()) {
                  var1.error(this.where, "return.with.value.constr", var2.field);
               } else {
                  var1.error(this.where, "return.with.value", var2.field);
               }

               this.expr = null;
            }
         } else if (this.expr == null) {
            var1.error(this.where, "return.without.value", var2.field);
         } else {
            this.expr = this.convert(var1, var2, var2.field.getType().getReturnType(), this.expr);
         }

         CheckContext var5 = var2.getReturnContext();
         if (var5 != null) {
            var5.vsBreak = var5.vsBreak.join(var3);
         }

         CheckContext var6 = var2.getTryExitContext();
         if (var6 != null) {
            var6.vsTryExit = var6.vsTryExit.join(var3);
         }

         if (this.expr != null) {
            Node var7 = null;

            for(Context var8 = var2; var8 != null; var8 = var8.prev) {
               if (var8.node != null) {
                  if (var8.node.op == 47) {
                     break;
                  }

                  if (var8.node.op == 126) {
                     var7 = var8.node;
                     break;
                  }

                  if (var8.node.op == 103 && ((CheckContext)var8).vsContinue != null) {
                     var7 = var8.node;
                  }
               }
            }

            if (var7 != null) {
               if (var7.op == 103) {
                  ((FinallyStatement)var7).needReturnSlot = true;
               } else {
                  ((SynchronizedStatement)var7).needReturnSlot = true;
               }
            }
         }

         return DEAD_END;
      }
   }

   public Statement inline(Environment var1, Context var2) {
      if (this.expr != null) {
         this.expr = this.expr.inlineValue(var1, var2);
      }

      return this;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 1 + (this.expr != null ? this.expr.costInline(var1, var2, var3) : 0);
   }

   public Statement copyInline(Context var1, boolean var2) {
      Expression var3 = this.expr != null ? this.expr.copyInline(var1) : null;
      if (!var2 && var3 != null) {
         Statement[] var4 = new Statement[]{new ExpressionStatement(this.where, var3), new InlineReturnStatement(this.where, (Expression)null)};
         return new CompoundStatement(this.where, var4);
      } else {
         return new InlineReturnStatement(this.where, var3);
      }
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      if (this.expr == null) {
         this.codeFinally(var1, var2, var3, (Context)null, (Type)null);
         var3.add(this.where, 177);
      } else {
         this.expr.codeValue(var1, var2, var3);
         this.codeFinally(var1, var2, var3, (Context)null, this.expr.type);
         var3.add(this.where, 172 + this.expr.type.getTypeCodeOffset());
      }

   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("return");
      if (this.expr != null) {
         var1.print(" ");
         this.expr.print(var1);
      }

      var1.print(";");
   }
}
