package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.ClassDefinition;
import sun.tools.java.CompilerError;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.Type;

public class Statement extends Node {
   public static final Vset DEAD_END;
   Identifier[] labels = null;
   public static final Statement empty;
   public static final int MAXINLINECOST;

   Statement(int var1, long var2) {
      super(var1, var2);
   }

   public static Statement insertStatement(Statement var0, Statement var1) {
      if (var1 == null) {
         var1 = var0;
      } else if (var1 instanceof CompoundStatement) {
         ((CompoundStatement)var1).insertStatement(var0);
      } else {
         Statement[] var2 = new Statement[]{var0, (Statement)var1};
         var1 = new CompoundStatement(var0.getWhere(), var2);
      }

      return (Statement)var1;
   }

   public void setLabel(Environment var1, Expression var2) {
      if (var2.op == 60) {
         if (this.labels == null) {
            this.labels = new Identifier[1];
         } else {
            Identifier[] var3 = new Identifier[this.labels.length + 1];
            System.arraycopy(this.labels, 0, var3, 1, this.labels.length);
            this.labels = var3;
         }

         this.labels[0] = ((IdentifierExpression)var2).id;
      } else {
         var1.error(var2.where, "invalid.label");
      }

   }

   public Vset checkMethod(Environment var1, Context var2, Vset var3, Hashtable var4) {
      CheckContext var5 = new CheckContext(var2, new Statement(47, 0L));
      var3 = this.check(var1, var5, var3, var4);
      if (!var5.field.getType().getReturnType().isType(11) && !var3.isDeadEnd()) {
         var1.error(var5.field.getWhere(), "return.required.at.end", var5.field);
      }

      var3 = var3.join(var5.vsBreak);
      return var3;
   }

   Vset checkDeclaration(Environment var1, Context var2, Vset var3, int var4, Type var5, Hashtable var6) {
      throw new CompilerError("checkDeclaration");
   }

   protected void checkLabel(Environment var1, Context var2) {
      if (this.labels != null) {
         label30:
         for(int var3 = 0; var3 < this.labels.length; ++var3) {
            for(int var4 = var3 + 1; var4 < this.labels.length; ++var4) {
               if (this.labels[var3] == this.labels[var4]) {
                  var1.error(this.where, "nested.duplicate.label", this.labels[var3]);
                  continue label30;
               }
            }

            CheckContext var5 = (CheckContext)var2.getLabelContext(this.labels[var3]);
            if (var5 != null && var5.frameNumber == var2.frameNumber) {
               var1.error(this.where, "nested.duplicate.label", this.labels[var3]);
            }
         }
      }

   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      throw new CompilerError("check");
   }

   Vset checkBlockStatement(Environment var1, Context var2, Vset var3, Hashtable var4) {
      return this.check(var1, var2, var3, var4);
   }

   Vset reach(Environment var1, Vset var2) {
      if (var2.isDeadEnd()) {
         var1.error(this.where, "stat.not.reached");
         var2 = var2.clearDeadEnd();
      }

      return var2;
   }

   public Statement inline(Environment var1, Context var2) {
      return this;
   }

   public Statement eliminate(Environment var1, Statement var2) {
      if (var2 != null && this.labels != null) {
         Statement[] var3 = new Statement[]{(Statement)var2};
         var2 = new CompoundStatement(this.where, var3);
         ((Statement)var2).labels = this.labels;
      }

      return (Statement)var2;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      throw new CompilerError("code");
   }

   void codeFinally(Environment var1, Context var2, Assembler var3, Context var4, Type var5) {
      Integer var6 = null;
      boolean var7 = false;
      boolean var8 = false;

      Context var9;
      FinallyStatement var10;
      for(var9 = var2; var9 != null && var9 != var4; var9 = var9.prev) {
         if (var9.node != null) {
            if (var9.node.op == 126) {
               var7 = true;
            } else if (var9.node.op == 103 && ((CodeContext)var9).contLabel != null) {
               var7 = true;
               var10 = (FinallyStatement)((FinallyStatement)var9.node);
               if (!var10.finallyCanFinish) {
                  var8 = true;
                  break;
               }
            }
         }
      }

      if (var7) {
         if (var5 != null) {
            ClassDefinition var12 = var2.field.getClassDefinition();
            if (!var8) {
               LocalMember var13 = var2.getLocalField(idFinallyReturnValue);
               var6 = new Integer(var13.number);
               var3.add(this.where, 54 + var5.getTypeCodeOffset(), var6);
            } else {
               switch (var2.field.getType().getReturnType().getTypeCode()) {
                  case 5:
                  case 7:
                     var3.add(this.where, 88);
                  case 11:
                     break;
                  default:
                     var3.add(this.where, 87);
               }
            }
         }

         for(var9 = var2; var9 != null && var9 != var4; var9 = var9.prev) {
            if (var9.node != null) {
               if (var9.node.op == 126) {
                  var3.add(this.where, 168, ((CodeContext)var9).contLabel);
               } else if (var9.node.op == 103 && ((CodeContext)var9).contLabel != null) {
                  var10 = (FinallyStatement)((FinallyStatement)var9.node);
                  Label var11 = ((CodeContext)var9).contLabel;
                  if (!var10.finallyCanFinish) {
                     var3.add(this.where, 167, var11);
                     break;
                  }

                  var3.add(this.where, 168, var11);
               }
            }
         }

         if (var6 != null) {
            var3.add(this.where, 21 + var5.getTypeCodeOffset(), var6);
         }

      }
   }

   public boolean hasLabel(Identifier var1) {
      Identifier[] var2 = this.labels;
      if (var2 != null) {
         int var3 = var2.length;

         while(true) {
            --var3;
            if (var3 < 0) {
               break;
            }

            if (var2[var3].equals(var1)) {
               return true;
            }
         }
      }

      return false;
   }

   public Expression firstConstructor() {
      return null;
   }

   public Statement copyInline(Context var1, boolean var2) {
      return (Statement)this.clone();
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return var1;
   }

   void printIndent(PrintStream var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         var1.print("    ");
      }

   }

   public void print(PrintStream var1, int var2) {
      if (this.labels != null) {
         int var3 = this.labels.length;

         while(true) {
            --var3;
            if (var3 < 0) {
               break;
            }

            var1.print(this.labels[var3] + ": ");
         }
      }

   }

   public void print(PrintStream var1) {
      this.print(var1, 0);
   }

   static {
      DEAD_END = Vset.DEAD_END;
      empty = new Statement(105, 0L);
      MAXINLINECOST = Integer.getInteger("javac.maxinlinecost", 30);
   }
}
