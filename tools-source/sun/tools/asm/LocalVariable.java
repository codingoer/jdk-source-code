package sun.tools.asm;

import sun.tools.java.MemberDefinition;

public final class LocalVariable {
   MemberDefinition field;
   int slot;
   int from;
   int to;

   public LocalVariable(MemberDefinition var1, int var2) {
      if (var1 == null) {
         (new Exception()).printStackTrace();
      }

      this.field = var1;
      this.slot = var2;
      this.to = -1;
   }

   LocalVariable(MemberDefinition var1, int var2, int var3, int var4) {
      this.field = var1;
      this.slot = var2;
      this.from = var3;
      this.to = var4;
   }

   public String toString() {
      return this.field + "/" + this.slot;
   }
}
