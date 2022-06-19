package sun.tools.asm;

import sun.tools.java.Type;

public final class ArrayData {
   Type type;
   int nargs;

   public ArrayData(Type var1, int var2) {
      this.type = var1;
      this.nargs = var2;
   }
}
