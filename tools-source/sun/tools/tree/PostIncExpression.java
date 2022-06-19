package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class PostIncExpression extends IncDecExpression {
   public PostIncExpression(long var1, Expression var3) {
      super(44, var1, var3);
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.codeIncDec(var1, var2, var3, true, false, true);
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      this.codeIncDec(var1, var2, var3, true, false, false);
   }
}
