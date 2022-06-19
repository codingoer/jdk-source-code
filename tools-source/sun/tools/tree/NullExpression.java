package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class NullExpression extends ConstantExpression {
   public NullExpression(long var1) {
      super(84, var1, Type.tNull);
   }

   public boolean equals(int var1) {
      return var1 == 0;
   }

   public boolean isNull() {
      return true;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 1);
   }

   public void print(PrintStream var1) {
      var1.print("null");
   }
}
