package sun.tools.java;

public final class ArrayType extends Type {
   Type elemType;

   ArrayType(String var1, Type var2) {
      super(9, var1);
      this.elemType = var2;
   }

   public Type getElementType() {
      return this.elemType;
   }

   public int getArrayDimension() {
      return this.elemType.getArrayDimension() + 1;
   }

   public String typeString(String var1, boolean var2, boolean var3) {
      return this.getElementType().typeString(var1, var2, var3) + "[]";
   }
}
