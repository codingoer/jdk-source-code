package sun.tools.tree;

import java.io.PrintStream;
import java.util.Hashtable;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class BooleanExpression extends ConstantExpression {
   boolean value;

   public BooleanExpression(long var1, boolean var3) {
      super(61, var1, Type.tBoolean);
      this.value = var3;
   }

   public Object getValue() {
      return new Integer(this.value ? 1 : 0);
   }

   public boolean equals(boolean var1) {
      return this.value == var1;
   }

   public boolean equalsDefault() {
      return !this.value;
   }

   public void checkCondition(Environment var1, Context var2, Vset var3, Hashtable var4, ConditionVars var5) {
      if (this.value) {
         var5.vsFalse = Vset.DEAD_END;
         var5.vsTrue = var3;
      } else {
         var5.vsFalse = var3;
         var5.vsTrue = Vset.DEAD_END;
      }

   }

   void codeBranch(Environment var1, Context var2, Assembler var3, Label var4, boolean var5) {
      if (this.value == var5) {
         var3.add(this.where, 167, var4);
      }

   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 18, new Integer(this.value ? 1 : 0));
   }

   public void print(PrintStream var1) {
      var1.print(this.value ? "true" : "false");
   }
}
