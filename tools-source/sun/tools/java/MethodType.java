package sun.tools.java;

public final class MethodType extends Type {
   Type returnType;
   Type[] argTypes;

   MethodType(String var1, Type var2, Type[] var3) {
      super(12, var1);
      this.returnType = var2;
      this.argTypes = var3;
   }

   public Type getReturnType() {
      return this.returnType;
   }

   public Type[] getArgumentTypes() {
      return this.argTypes;
   }

   public boolean equalArguments(Type var1) {
      if (var1.typeCode != 12) {
         return false;
      } else {
         MethodType var2 = (MethodType)var1;
         if (this.argTypes.length != var2.argTypes.length) {
            return false;
         } else {
            for(int var3 = this.argTypes.length - 1; var3 >= 0; --var3) {
               if (this.argTypes[var3] != var2.argTypes[var3]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int stackSize() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.argTypes.length; ++var2) {
         var1 += this.argTypes[var2].stackSize();
      }

      return var1;
   }

   public String typeString(String var1, boolean var2, boolean var3) {
      StringBuffer var4 = new StringBuffer();
      var4.append(var1);
      var4.append('(');

      for(int var5 = 0; var5 < this.argTypes.length; ++var5) {
         if (var5 > 0) {
            var4.append(", ");
         }

         var4.append(this.argTypes[var5].typeString("", var2, var3));
      }

      var4.append(')');
      return var3 ? this.getReturnType().typeString(var4.toString(), var2, var3) : var4.toString();
   }
}
