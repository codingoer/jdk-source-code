package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.LocalVariable;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.IdentifierToken;
import sun.tools.java.Type;

public class CatchStatement extends Statement {
   int mod;
   Expression texpr;
   Identifier id;
   Statement body;
   LocalMember field;

   public CatchStatement(long var1, Expression var3, IdentifierToken var4, Statement var5) {
      super(102, var1);
      this.mod = var4.getModifiers();
      this.texpr = var3;
      this.id = var4.getName();
      this.body = var5;
   }

   /** @deprecated */
   @Deprecated
   public CatchStatement(long var1, Expression var3, Identifier var4, Statement var5) {
      super(102, var1);
      this.texpr = var3;
      this.id = var4;
      this.body = var5;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.reach(var1, var3);
      var2 = new Context(var2, this);
      Type var5 = this.texpr.toType(var1, var2);

      try {
         if (var2.getLocalField(this.id) != null) {
            var1.error(this.where, "local.redefined", this.id);
         }

         if (!var5.isType(13)) {
            if (!var5.isType(10)) {
               var1.error(this.where, "catch.not.throwable", var5);
            } else {
               ClassDefinition var6 = var1.getClassDefinition(var5);
               if (!var6.subClassOf(var1, var1.getClassDeclaration(idJavaLangThrowable))) {
                  var1.error(this.where, "catch.not.throwable", var6);
               }
            }
         }

         this.field = new LocalMember(this.where, var2.field.getClassDefinition(), this.mod, var5, this.id);
         var2.declare(var1, this.field);
         var3.addVar(this.field.number);
         return this.body.check(var1, var2, var3, var4);
      } catch (ClassNotFound var7) {
         var1.error(this.where, "class.not.found", var7.name, opNames[this.op]);
         return var3;
      }
   }

   public Statement inline(Environment var1, Context var2) {
      var2 = new Context(var2, this);
      if (this.field.isUsed()) {
         var2.declare(var1, this.field);
      }

      if (this.body != null) {
         this.body = this.body.inline(var1, var2);
      }

      return this;
   }

   public Statement copyInline(Context var1, boolean var2) {
      CatchStatement var3 = (CatchStatement)this.clone();
      if (this.body != null) {
         var3.body = this.body.copyInline(var1, var2);
      }

      if (this.field != null) {
         var3.field = this.field.copyInline(var1);
      }

      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      int var4 = 1;
      if (this.body != null) {
         var4 += this.body.costInline(var1, var2, var3);
      }

      return var4;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      CodeContext var4 = new CodeContext(var2, this);
      if (this.field.isUsed()) {
         var4.declare(var1, this.field);
         var3.add(this.where, 58, new LocalVariable(this.field, this.field.number));
      } else {
         var3.add(this.where, 87);
      }

      if (this.body != null) {
         this.body.code(var1, var4, var3);
      }

   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("catch (");
      this.texpr.print(var1);
      var1.print(" " + this.id + ") ");
      if (this.body != null) {
         this.body.print(var1, var2);
      } else {
         var1.print("<empty>");
      }

   }
}
