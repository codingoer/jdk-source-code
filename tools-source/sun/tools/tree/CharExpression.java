package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.java.Type;

public class CharExpression extends IntegerExpression {
   public CharExpression(long var1, char var3) {
      super(63, var1, Type.tChar, var3);
   }

   public void print(PrintStream var1) {
      var1.print(this.value + "c");
   }
}
