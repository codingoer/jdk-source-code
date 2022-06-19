package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class PreDecExpression extends IncDecExpression {
   public PreDecExpression(long var1, Expression var3) {
      super(40, var1, var3);
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.codeIncDec(var1, var2, var3, false, true, true);
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      this.codeIncDec(var1, var2, var3, false, true, false);
   }
}
