package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.java.Type;

public class IntExpression extends IntegerExpression {
   public IntExpression(long var1, int var3) {
      super(65, var1, Type.tInt, var3);
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof IntExpression) {
         return this.value == ((IntExpression)var1).value;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.value;
   }

   public void print(PrintStream var1) {
      var1.print(this.value);
   }
}
