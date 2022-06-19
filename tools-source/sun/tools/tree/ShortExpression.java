package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.java.Type;

public class ShortExpression extends IntegerExpression {
   public ShortExpression(long var1, short var3) {
      super(64, var1, Type.tShort, var3);
   }

   public void print(PrintStream var1) {
      var1.print(this.value + "s");
   }
}
