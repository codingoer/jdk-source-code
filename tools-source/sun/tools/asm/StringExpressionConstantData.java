package sun.tools.asm;

import java.io.DataOutputStream;
import java.io.IOException;
import sun.tools.java.Environment;
import sun.tools.tree.StringExpression;

final class StringExpressionConstantData extends ConstantPoolData {
   StringExpression str;

   StringExpressionConstantData(ConstantPool var1, StringExpression var2) {
      this.str = var2;
      var1.put(var2.getValue());
   }

   void write(Environment var1, DataOutputStream var2, ConstantPool var3) throws IOException {
      var2.writeByte(8);
      var2.writeShort(var3.index(this.str.getValue()));
   }

   int order() {
      return 0;
   }

   public String toString() {
      return "StringExpressionConstantData[" + this.str.getValue() + "]=" + this.str.getValue().hashCode();
   }
}
