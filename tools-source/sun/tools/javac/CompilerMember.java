package sun.tools.javac;

import sun.tools.asm.Assembler;
import sun.tools.java.MemberDefinition;

/** @deprecated */
@Deprecated
final class CompilerMember implements Comparable {
   MemberDefinition field;
   Assembler asm;
   Object value;
   String name;
   String sig;
   String key;

   CompilerMember(MemberDefinition var1, Assembler var2) {
      this.field = var1;
      this.asm = var2;
      this.name = var1.getName().toString();
      this.sig = var1.getType().getTypeSignature();
   }

   public int compareTo(Object var1) {
      CompilerMember var2 = (CompilerMember)var1;
      return this.getKey().compareTo(var2.getKey());
   }

   String getKey() {
      if (this.key == null) {
         this.key = this.name + this.sig;
      }

      return this.key;
   }
}
