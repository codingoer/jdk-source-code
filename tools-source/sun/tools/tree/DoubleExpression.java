package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class DoubleExpression extends ConstantExpression {
   double value;

   public DoubleExpression(long var1, double var3) {
      super(68, var1, Type.tDouble);
      this.value = var3;
   }

   public Object getValue() {
      return new Double(this.value);
   }

   public boolean equals(int var1) {
      return this.value == (double)var1;
   }

   public boolean equalsDefault() {
      return Double.doubleToLongBits(this.value) == 0L;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 20, new Double(this.value));
   }

   public void print(PrintStream var1) {
      var1.print(this.value + "D");
   }
}
