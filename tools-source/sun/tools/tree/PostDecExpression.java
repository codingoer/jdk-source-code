package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class PostDecExpression extends IncDecExpression {
   public PostDecExpression(long var1, Expression var3) {
      super(45, var1, var3);
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      this.codeIncDec(var1, var2, var3, false, false, true);
   }

   public void code(Environment var1, Context var2, Assembler var3) {
      this.codeIncDec(var1, var2, var3, false, false, false);
   }
}
