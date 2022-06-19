package sun.tools.asm;

import java.io.DataOutputStream;
import java.io.IOException;
import sun.tools.java.Environment;
import sun.tools.java.RuntimeConstants;

abstract class ConstantPoolData implements RuntimeConstants {
   int index;

   abstract void write(Environment var1, DataOutputStream var2, ConstantPool var3) throws IOException;

   int order() {
      return 0;
   }

   int width() {
      return 1;
   }
}
