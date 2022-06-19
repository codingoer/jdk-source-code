package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.CatchData;
import sun.tools.asm.Label;
import sun.tools.asm.TryData;
import sun.tools.java.ClassDefinition;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.Type;

public class SynchronizedStatement extends Statement {
   Expression expr;
   Statement body;
   boolean needReturnSlot;

   public SynchronizedStatement(long var1, Expression var3, Statement var4) {
      super(126, var1);
      this.expr = var3;
      this.body = var4;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      this.checkLabel(var1, var2);
      CheckContext var5 = new CheckContext(var2, this);
      var3 = this.reach(var1, var3);
      var3 = this.expr.checkValue(var1, var5, var3, var4);
      if (this.expr.type.equals(Type.tNull)) {
         var1.error(this.expr.where, "synchronized.null");
      }

      this.expr = this.convert(var1, var5, Type.tClass(idJavaLangObject), this.expr);
      var3 = this.body.check(var1, var5, var3, var4);
      return var2.removeAdditionalVars(var3.join(var5.vsBreak));
   }

   public Statement inline(Environment var1, Context var2) {
      if (this.body != null) {
         this.body = this.body.inline(var1, var2);
      }

      this.expr = this.expr.inlineValue(var1, var2);
      return this;
   }

   public Statement copyInline(Context var1, boolean var2) {
      SynchronizedStatement var3 = (SynchronizedStatement)this.clone();
      var3.expr = this.expr.copyInline(var1);
      if (this.body != null) {
         var3.body = this.body.copyInline(var1, var2);
      }

      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      int var4 = 1;
      if (this.expr != null) {
         var4 += this.expr.costInline(var1, var2, var3);
         if (var4 >= var1) {
            return var4;
         }
      }

      if (this.body != null) {
         var4 += this.body.costInline(var1, var2, var3);
      }

      return var4;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      ClassDefinition var4 = var2.field.getClassDefinition();
      this.expr.codeValue(var1, var2, var3);
      var2 = new Context(var2);
      LocalMember var6;
      if (this.needReturnSlot) {
         Type var5 = var2.field.getType().getReturnType();
         var6 = new LocalMember(0L, var4, 0, var5, idFinallyReturnValue);
         var2.declare(var1, var6);
         Environment.debugOutput("Assigning return slot to " + var6.number);
      }

      LocalMember var13 = new LocalMember(this.where, var4, 0, Type.tObject, (Identifier)null);
      var6 = new LocalMember(this.where, var4, 0, Type.tInt, (Identifier)null);
      Integer var7 = new Integer(var2.declare(var1, var13));
      Integer var8 = new Integer(var2.declare(var1, var6));
      Label var9 = new Label();
      TryData var10 = new TryData();
      var10.add((Object)null);
      var3.add(this.where, 58, var7);
      var3.add(this.where, 25, var7);
      var3.add(this.where, 194);
      CodeContext var11 = new CodeContext(var2, this);
      var3.add(this.where, -3, var10);
      if (this.body != null) {
         this.body.code(var1, var11, var3);
      } else {
         var3.add(this.where, 0);
      }

      var3.add(var11.breakLabel);
      var3.add(var10.getEndLabel());
      var3.add(this.where, 25, var7);
      var3.add(this.where, 195);
      var3.add(this.where, 167, var9);
      CatchData var12 = var10.getCatch(0);
      var3.add(var12.getLabel());
      var3.add(this.where, 25, var7);
      var3.add(this.where, 195);
      var3.add(this.where, 191);
      var3.add(var11.contLabel);
      var3.add(this.where, 58, var8);
      var3.add(this.where, 25, var7);
      var3.add(this.where, 195);
      var3.add(this.where, 169, var8);
      var3.add(var9);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("synchronized ");
      this.expr.print(var1);
      var1.print(" ");
      if (this.body != null) {
         this.body.print(var1, var2);
      } else {
         var1.print("{}");
      }

   }
}
