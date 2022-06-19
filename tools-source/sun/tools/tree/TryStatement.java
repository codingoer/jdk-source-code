package sun.tools.tree;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.CatchData;
import sun.tools.asm.TryData;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class TryStatement extends Statement {
   Statement body;
   Statement[] args;
   long arrayCloneWhere;

   public TryStatement(long var1, Statement var3, Statement[] var4) {
      super(101, var1);
      this.body = var3;
      this.args = var4;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);

      try {
         var3 = this.reach(var1, var3);
         Hashtable var5 = new Hashtable();
         CheckContext var6 = new CheckContext(var2, this);
         Vset var7 = this.body.check(var1, var6, var3.copy(), var5);
         Vset var8 = Vset.firstDAandSecondDU(var3, var7.copy().join(var6.vsTryExit));

         int var9;
         for(var9 = 0; var9 < this.args.length; ++var9) {
            var7 = var7.join(this.args[var9].check(var1, var6, var8.copy(), var4));
         }

         for(var9 = 1; var9 < this.args.length; ++var9) {
            CatchStatement var10 = (CatchStatement)this.args[var9];
            if (var10.field != null) {
               Type var11 = var10.field.getType();
               ClassDefinition var12 = var1.getClassDefinition(var11);

               for(int var13 = 0; var13 < var9; ++var13) {
                  CatchStatement var14 = (CatchStatement)this.args[var13];
                  if (var14.field != null) {
                     Type var15 = var14.field.getType();
                     ClassDeclaration var16 = var1.getClassDeclaration(var15);
                     if (var12.subClassOf(var1, var16)) {
                        var1.error(this.args[var9].where, "catch.not.reached");
                        break;
                     }
                  }
               }
            }
         }

         ClassDeclaration var19 = var1.getClassDeclaration(idJavaLangError);
         ClassDeclaration var20 = var1.getClassDeclaration(idJavaLangRuntimeException);

         for(int var21 = 0; var21 < this.args.length; ++var21) {
            CatchStatement var23 = (CatchStatement)this.args[var21];
            if (var23.field != null) {
               Type var25 = var23.field.getType();
               if (var25.isType(10)) {
                  ClassDefinition var27 = var1.getClassDefinition(var25);
                  if (!var27.subClassOf(var1, var19) && !var27.superClassOf(var1, var19) && !var27.subClassOf(var1, var20) && !var27.superClassOf(var1, var20)) {
                     boolean var29 = false;
                     Enumeration var31 = var5.keys();

                     while(var31.hasMoreElements()) {
                        ClassDeclaration var17 = (ClassDeclaration)var31.nextElement();
                        if (var27.superClassOf(var1, var17) || var27.subClassOf(var1, var17)) {
                           var29 = true;
                           break;
                        }
                     }

                     if (!var29 && this.arrayCloneWhere != 0L && var27.getName().toString().equals("java.lang.CloneNotSupportedException")) {
                        var1.error(this.arrayCloneWhere, "warn.array.clone.supported", var27.getName());
                     }

                     if (!var29) {
                        var1.error(var23.where, "catch.not.thrown", var27.getName());
                     }
                  }
               }
            }
         }

         Enumeration var22 = var5.keys();

         while(var22.hasMoreElements()) {
            ClassDeclaration var24 = (ClassDeclaration)var22.nextElement();
            ClassDefinition var26 = var24.getClassDefinition(var1);
            boolean var28 = true;

            for(int var30 = 0; var30 < this.args.length; ++var30) {
               CatchStatement var32 = (CatchStatement)this.args[var30];
               if (var32.field != null) {
                  Type var33 = var32.field.getType();
                  if (!var33.isType(13) && var26.subClassOf(var1, var1.getClassDeclaration(var33))) {
                     var28 = false;
                     break;
                  }
               }
            }

            if (var28) {
               var4.put(var24, var5.get(var24));
            }
         }

         return var2.removeAdditionalVars(var7.join(var6.vsBreak));
      } catch (ClassNotFound var18) {
         var1.error(this.where, "class.not.found", var18.name, opNames[this.op]);
         return var3;
      }
   }

   public Statement inline(Environment var1, Context var2) {
      if (this.body != null) {
         this.body = this.body.inline(var1, new Context(var2, this));
      }

      if (this.body == null) {
         return null;
      } else {
         for(int var3 = 0; var3 < this.args.length; ++var3) {
            if (this.args[var3] != null) {
               this.args[var3] = this.args[var3].inline(var1, new Context(var2, this));
            }
         }

         return (Statement)(this.args.length == 0 ? this.eliminate(var1, this.body) : this);
      }
   }

   public Statement copyInline(Context var1, boolean var2) {
      TryStatement var3 = (TryStatement)this.clone();
      if (this.body != null) {
         var3.body = this.body.copyInline(var1, var2);
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
      return var1;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      CodeContext var4 = new CodeContext(var2, this);
      TryData var5 = new TryData();

      int var6;
      for(var6 = 0; var6 < this.args.length; ++var6) {
         Type var7 = ((CatchStatement)this.args[var6]).field.getType();
         if (var7.isType(10)) {
            var5.add(var1.getClassDeclaration(var7));
         } else {
            var5.add(var7);
         }
      }

      var3.add(this.where, -3, var5);
      if (this.body != null) {
         this.body.code(var1, var4, var3);
      }

      var3.add(var5.getEndLabel());
      var3.add(this.where, 167, var4.breakLabel);

      for(var6 = 0; var6 < this.args.length; ++var6) {
         CatchData var8 = var5.getCatch(var6);
         var3.add(var8.getLabel());
         this.args[var6].code(var1, var4, var3);
         var3.add(this.where, 167, var4.breakLabel);
      }

      var3.add(var4.breakLabel);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("try ");
      if (this.body != null) {
         this.body.print(var1, var2);
      } else {
         var1.print("<empty>");
      }

      for(int var3 = 0; var3 < this.args.length; ++var3) {
         var1.print(" ");
         this.args[var3].print(var1, var2);
      }

   }
}
