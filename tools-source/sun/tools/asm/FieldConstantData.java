package sun.tools.asm;

import java.io.DataOutputStream;
import java.io.IOException;
import sun.tools.java.Environment;
import sun.tools.java.MemberDefinition;

final class FieldConstantData extends ConstantPoolData {
   MemberDefinition field;
   NameAndTypeData nt;

   FieldConstantData(ConstantPool var1, MemberDefinition var2) {
      this.field = var2;
      this.nt = new NameAndTypeData(var2);
      var1.put(var2.getClassDeclaration());
      var1.put(this.nt);
   }

   void write(Environment var1, DataOutputStream var2, ConstantPool var3) throws IOException {
      if (this.field.isMethod()) {
         if (this.field.getClassDefinition().isInterface()) {
            var2.writeByte(11);
         } else {
            var2.writeByte(10);
         }
      } else {
         var2.writeByte(9);
      }

      var2.writeShort(var3.index(this.field.getClassDeclaration()));
      var2.writeShort(var3.index(this.nt));
   }

   int order() {
      return 2;
   }
}
