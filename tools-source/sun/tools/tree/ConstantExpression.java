package sun.tools.tree;

import sun.tools.java.Type;

class ConstantExpression extends Expression {
   public ConstantExpression(int var1, long var2, Type var4) {
      super(var1, var2, var4);
   }

   public boolean isConstant() {
      return true;
   }
}
