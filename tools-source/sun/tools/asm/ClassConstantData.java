package sun.tools.asm;

import java.io.DataOutputStream;
import java.io.IOException;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.Environment;
import sun.tools.java.Type;

final class ClassConstantData extends ConstantPoolData {
   String name;

   ClassConstantData(ConstantPool var1, ClassDeclaration var2) {
      String var3 = var2.getType().getTypeSignature();
      this.name = var3.substring(1, var3.length() - 1);
      var1.put(this.name);
   }

   ClassConstantData(ConstantPool var1, Type var2) {
      this.name = var2.getTypeSignature();
      var1.put(this.name);
   }

   void write(Environment var1, DataOutputStream var2, ConstantPool var3) throws IOException {
      var2.writeByte(7);
      var2.writeShort(var3.index(this.name));
   }

   int order() {
      return 1;
   }

   public String toString() {
      return "ClassConstantData[" + this.name + "]";
   }
}
