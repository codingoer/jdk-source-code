package sun.tools.asm;

import java.io.DataOutputStream;
import java.io.IOException;
import sun.tools.java.Environment;

final class NumberConstantData extends ConstantPoolData {
   Number num;

   NumberConstantData(ConstantPool var1, Number var2) {
      this.num = var2;
   }

   void write(Environment var1, DataOutputStream var2, ConstantPool var3) throws IOException {
      if (this.num instanceof Integer) {
         var2.writeByte(3);
         var2.writeInt(this.num.intValue());
      } else if (this.num instanceof Long) {
         var2.writeByte(5);
         var2.writeLong(this.num.longValue());
      } else if (this.num instanceof Float) {
         var2.writeByte(4);
         var2.writeFloat(this.num.floatValue());
      } else if (this.num instanceof Double) {
         var2.writeByte(6);
         var2.writeDouble(this.num.doubleValue());
      }

   }

   int order() {
      return this.width() == 1 ? 0 : 3;
   }

   int width() {
      return !(this.num instanceof Double) && !(this.num instanceof Long) ? 1 : 2;
   }
}
