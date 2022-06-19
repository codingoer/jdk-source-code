package sun.tools.tree;

import java.io.PrintStream;
import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class StringExpression extends ConstantExpression {
   String value;

   public StringExpression(long var1, String var3) {
      super(69, var1, Type.tString);
      this.value = var3;
   }

   public boolean equals(String var1) {
      return this.value.equals(var1);
   }

   public boolean isNonNull() {
      return true;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 18, this);
   }

   public Object getValue() {
      return this.value;
   }

   public int hashCode() {
      return this.value.hashCode() ^ 3213;
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof StringExpression ? this.value.equals(((StringExpression)var1).value) : false;
   }

   public void print(PrintStream var1) {
      var1.print("\"" + this.value + "\"");
   }
}
