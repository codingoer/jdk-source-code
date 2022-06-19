package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.java.Type;

public class ByteExpression extends IntegerExpression {
   public ByteExpression(long var1, byte var3) {
      super(62, var1, Type.tByte, var3);
   }

   public void print(PrintStream var1) {
      var1.print(this.value + "b");
   }
}
