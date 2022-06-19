package sun.tools.asm;

import java.io.DataOutputStream;
import java.io.IOException;
import sun.tools.java.Environment;

final class NameAndTypeConstantData extends ConstantPoolData {
   String name;
   String type;

   NameAndTypeConstantData(ConstantPool var1, NameAndTypeData var2) {
      this.name = var2.field.getName().toString();
      this.type = var2.field.getType().getTypeSignature();
      var1.put(this.name);
      var1.put(this.type);
   }

   void write(Environment var1, DataOutputStream var2, ConstantPool var3) throws IOException {
      var2.writeByte(12);
      var2.writeShort(var3.index(this.name));
      var2.writeShort(var3.index(this.type));
   }

   int order() {
      return 3;
   }
}
