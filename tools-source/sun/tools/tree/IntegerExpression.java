package sun.tools.tree;

import sun.tools.asm.Assembler;
import sun.tools.java.Environment;
import sun.tools.java.Type;

public class IntegerExpression extends ConstantExpression {
   int value;

   IntegerExpression(int var1, long var2, Type var4, int var5) {
      super(var1, var2, var4);
      this.value = var5;
   }

   public boolean fitsType(Environment var1, Context var2, Type var3) {
      if (this.type.isType(2)) {
         return super.fitsType(var1, var2, var3);
      } else {
         switch (var3.getTypeCode()) {
            case 1:
               return this.value == (byte)this.value;
            case 2:
               return this.value == (char)this.value;
            case 3:
               return this.value == (short)this.value;
            default:
               return super.fitsType(var1, var2, var3);
         }
      }
   }

   public Object getValue() {
      return new Integer(this.value);
   }

   public boolean equals(int var1) {
      return this.value == var1;
   }

   public boolean equalsDefault() {
      return this.value == 0;
   }

   public void codeValue(Environment var1, Context var2, Assembler var3) {
      var3.add(this.where, 18, new Integer(this.value));
   }
}
