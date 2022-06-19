package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class LongExpression extends ConstantExpression {
   long value;

   public LongExpression(long var1, long var3) {
      super(66, var1, Type.tLong);
      this.value = var3;
   }

   public Object getValue() {
      return new Long(this.value);
   }

   public boolean equals(int var1) {
      return this.value == (long)var1;
   }

   public boolean equalsDefault() {
      return this.value == 0L;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 20, new Long(this.value));
   }

   public void print(PrintStream var1) {
      var1.print(this.value + "L");
   }
}
