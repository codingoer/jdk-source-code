package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class ThrowStatement extends Statement {
   Expression expr;

   public ThrowStatement(long var1, Expression var3) {
      super(104, var1);
      this.expr = var3;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);

      try {
         var3 = this.reach(var1, var3);
         this.expr.checkValue(var1, var2, var3, var4);
         if (this.expr.type.isType(10)) {
            ClassDeclaration var5 = var1.getClassDeclaration(this.expr.type);
            if (var4.get(var5) == null) {
               var4.put(var5, this);
            }

            ClassDefinition var6 = var5.getClassDefinition(var1);
            ClassDeclaration var7 = var1.getClassDeclaration(idJavaLangThrowable);
            if (!var6.subClassOf(var1, var7)) {
               var1.error(this.where, "throw.not.throwable", var6);
            }

            this.expr = this.convert(var1, var2, Type.tObject, this.expr);
         } else if (!this.expr.type.isType(13)) {
            var1.error(this.expr.where, "throw.not.throwable", this.expr.type);
         }
      } catch (ClassNotFound var8) {
         var1.error(this.where, "class.not.found", var8.name, opNames[this.op]);
      }

      CheckContext var9 = var2.getTryExitContext();
      if (var9 != null) {
         var9.vsTryExit = var9.vsTryExit.join(var3);
      }

      return DEAD_END;
   }

   public Statement inline(Environment var1, Context var2) {
      this.expr = this.expr.inlineValue(var1, var2);
      return this;
   }

   public Statement copyInline(Context var1, boolean var2) {
      ThrowStatement var3 = (ThrowStatement)this.clone();
      var3.expr = this.expr.copyInline(var1);
      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      return 1 + this.expr.costInline(var1, var2, var3);
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      this.expr.codeValue(var1, var2, var3);
      var3.add(this.where, 191);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("throw ");
      this.expr.print(var1);
      var1.print(":");
   }
}
