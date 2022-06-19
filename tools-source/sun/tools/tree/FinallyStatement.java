package sun.tools.tree;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.CatchData;
import sun.tools.asm.Label;
import sun.tools.asm.TryData;
import sun.tools.java.ClassDefinition;
import sun.tools.java.Environment;
import sun.tools.java.Identifier;
import sun.tools.java.Type;

public class FinallyStatement extends Statement {
   Statement body;
   Statement finalbody;
   boolean finallyCanFinish;
   boolean needReturnSlot;
   Statement init;
   LocalMember tryTemp;

   public FinallyStatement(long var1, Statement var3, Statement var4) {
      super(103, var1);
      this.body = var3;
      this.finalbody = var4;
   }

   Vset check(Environment var1, Context var2, Vset var3, Hashtable var4) {
      var3 = this.reach(var1, var3);
      Hashtable var5 = new Hashtable();
      CheckContext var6 = new CheckContext(var2, this);
      Vset var7 = this.body.check(var1, var6, var3.copy(), var5).join(var6.vsBreak);
      CheckContext var8 = new CheckContext(var2, this);
      var8.vsContinue = null;
      Vset var9 = this.finalbody.check(var1, var8, var3, var4);
      this.finallyCanFinish = !var9.isDeadEnd();
      var9 = var9.join(var8.vsBreak);
      if (this.finallyCanFinish) {
         Enumeration var10 = var5.keys();

         while(var10.hasMoreElements()) {
            Object var11 = var10.nextElement();
            var4.put(var11, var5.get(var11));
         }
      }

      return var2.removeAdditionalVars(var7.addDAandJoinDU(var9));
   }

   public Statement inline(Environment var1, Context var2) {
      if (this.tryTemp != null) {
         var2 = new Context(var2, this);
         var2.declare(var1, this.tryTemp);
      }

      if (this.init != null) {
         this.init = this.init.inline(var1, var2);
      }

      if (this.body != null) {
         this.body = this.body.inline(var1, var2);
      }

      if (this.finalbody != null) {
         this.finalbody = this.finalbody.inline(var1, var2);
      }

      if (this.body == null) {
         return this.eliminate(var1, this.finalbody);
      } else {
         return (Statement)(this.finalbody == null ? this.eliminate(var1, this.body) : this);
      }
   }

   public Statement copyInline(Context var1, boolean var2) {
      FinallyStatement var3 = (FinallyStatement)this.clone();
      if (this.tryTemp != null) {
         var3.tryTemp = this.tryTemp.copyInline(var1);
      }

      if (this.init != null) {
         var3.init = this.init.copyInline(var1, var2);
      }

      if (this.body != null) {
         var3.body = this.body.copyInline(var1, var2);
      }

      if (this.finalbody != null) {
         var3.finalbody = this.finalbody.copyInline(var1, var2);
      }

      return var3;
   }

   public int costInline(int var1, Environment var2, Context var3) {
      int var4 = 4;
      if (this.init != null) {
         var4 += this.init.costInline(var1, var2, var3);
         if (var4 >= var1) {
            return var4;
         }
      }

      if (this.body != null) {
         var4 += this.body.costInline(var1, var2, var3);
         if (var4 >= var1) {
            return var4;
         }
      }

      if (this.finalbody != null) {
         var4 += this.finalbody.costInline(var1, var2, var3);
      }

      return var4;
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      var2 = new Context(var2);
      Integer var4 = null;
      Integer var5 = null;
      Label var6 = new Label();
      if (this.tryTemp != null) {
         var2.declare(var1, this.tryTemp);
      }

      if (this.init != null) {
         CodeContext var7 = new CodeContext(var2, this);
         this.init.code(var1, var7, var3);
      }

      if (this.finallyCanFinish) {
         ClassDefinition var9 = var2.field.getClassDefinition();
         if (this.needReturnSlot) {
            Type var10 = var2.field.getType().getReturnType();
            LocalMember var11 = new LocalMember(0L, var9, 0, var10, idFinallyReturnValue);
            var2.declare(var1, var11);
            Environment.debugOutput("Assigning return slot to " + var11.number);
         }

         LocalMember var12 = new LocalMember(this.where, var9, 0, Type.tObject, (Identifier)null);
         LocalMember var8 = new LocalMember(this.where, var9, 0, Type.tInt, (Identifier)null);
         var4 = new Integer(var2.declare(var1, var12));
         var5 = new Integer(var2.declare(var1, var8));
      }

      TryData var13 = new TryData();
      var13.add((Object)null);
      CodeContext var14 = new CodeContext(var2, this);
      var3.add(this.where, -3, var13);
      this.body.code(var1, var14, var3);
      var3.add(var14.breakLabel);
      var3.add(var13.getEndLabel());
      if (this.finallyCanFinish) {
         var3.add(this.where, 168, var14.contLabel);
         var3.add(this.where, 167, var6);
      } else {
         var3.add(this.where, 167, var14.contLabel);
      }

      CatchData var15 = var13.getCatch(0);
      var3.add(var15.getLabel());
      if (this.finallyCanFinish) {
         var3.add(this.where, 58, var4);
         var3.add(this.where, 168, var14.contLabel);
         var3.add(this.where, 25, var4);
         var3.add(this.where, 191);
      } else {
         var3.add(this.where, 87);
      }

      var3.add(var14.contLabel);
      var14.contLabel = null;
      var14.breakLabel = var6;
      if (this.finallyCanFinish) {
         var3.add(this.where, 58, var5);
         this.finalbody.code(var1, var14, var3);
         var3.add(this.where, 169, var5);
      } else {
         this.finalbody.code(var1, var14, var3);
      }

      var3.add(var6);
   }

   public void print(PrintStream var1, int var2) {
      super.print(var1, var2);
      var1.print("try ");
      if (this.body != null) {
         this.body.print(var1, var2);
      } else {
         var1.print("<empty>");
      }

      var1.print(" finally ");
      if (this.finalbody != null) {
         this.finalbody.print(var1, var2);
      } else {
         var1.print("<empty>");
      }

   }
}
