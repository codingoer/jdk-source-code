package sun.tools.asm;

import sun.tools.java.MemberDefinition;

final class NameAndTypeData {
   MemberDefinition field;

   NameAndTypeData(MemberDefinition var1) {
      this.field = var1;
   }

   public int hashCode() {
      return this.field.getName().hashCode() * this.field.getType().hashCode();
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof NameAndTypeData) {
         NameAndTypeData var2 = (NameAndTypeData)var1;
         return this.field.getName().equals(var2.field.getName()) && this.field.getType().equals(var2.field.getType());
      } else {
         return false;
      }
   }

   public String toString() {
      return "%%" + this.field.toString() + "%%";
   }
}
