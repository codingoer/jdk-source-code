package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class FloatExpression extends ConstantExpression {
   float value;

   public FloatExpression(long var1, float var3) {
      super(67, var1, Type.tFloat);
      this.value = var3;
   }

   public Object getValue() {
      return new Float(this.value);
   }

   public boolean equals(int var1) {
      return this.value == (float)var1;
   }

   public boolean equalsDefault() {
      return Float.floatToIntBits(this.value) == 0;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 18, new Float(this.value));
   }

   public void print(PrintStream var1) {
      var1.print(this.value + "F");
   }
}
