package sun.tools.asm;

import java.io.DataOutputStream;
import java.io.IOException;
import sun.tools.java.Environment;

final class StringConstantData extends ConstantPoolData {
   String str;

   StringConstantData(ConstantPool var1, String var2) {
      this.str = var2;
   }

   void write(Environment var1, DataOutputStream var2, ConstantPool var3) throws IOException {
      var2.writeByte(1);
      var2.writeUTF(this.str);
   }

   int order() {
      return 4;
   }

   public String toString() {
      return "StringConstantData[" + this.str + "]=" + this.str.hashCode();
   }
}
