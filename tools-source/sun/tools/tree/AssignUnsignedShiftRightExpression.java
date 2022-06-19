package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.Environment;

public class AssignUnsignedShiftRightExpression extends AssignOpExpression {
   public AssignUnsignedShiftRightExpression(long var1, Expression var3, Expression var4) {
      super(9, var1, var3, var4);
   }

   void codeOperation(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 124 + this.itype.getTypeCodeOffset());
   }
}
