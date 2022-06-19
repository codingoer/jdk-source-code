package sun.rmi.rmic.iiop;

import sun.tools.java.CompilerError;
import sun.tools.java.Identifier;

public class PrimitiveType extends Type {
   public static PrimitiveType forPrimitive(sun.tools.java.Type var0, ContextStack var1) {
      if (var1.anyErrors()) {
         return null;
      } else {
         Type var2 = getType(var0, var1);
         if (var2 != null) {
            return !(var2 instanceof PrimitiveType) ? null : (PrimitiveType)var2;
         } else {
            short var3;
            switch (var0.getTypeCode()) {
               case 0:
                  var3 = 2;
                  break;
               case 1:
                  var3 = 4;
                  break;
               case 2:
                  var3 = 8;
                  break;
               case 3:
                  var3 = 16;
                  break;
               case 4:
                  var3 = 32;
                  break;
               case 5:
                  var3 = 64;
                  break;
               case 6:
                  var3 = 128;
                  break;
               case 7:
                  var3 = 256;
                  break;
               case 8:
               case 9:
               case 10:
               default:
                  return null;
               case 11:
                  var3 = 1;
            }

            PrimitiveType var4 = new PrimitiveType(var1, var3);
            putType(var0, var4, var1);
            var1.push(var4);
            var1.pop(true);
            return var4;
         }
      }
   }

   public String getSignature() {
      switch (this.getTypeCode()) {
         case 1:
            return "V";
         case 2:
            return "Z";
         case 4:
            return "B";
         case 8:
            return "C";
         case 16:
            return "S";
         case 32:
            return "I";
         case 64:
            return "J";
         case 128:
            return "F";
         case 256:
            return "D";
         default:
            return null;
      }
   }

   public String getTypeDescription() {
      return "Primitive";
   }

   public String getQualifiedIDLName(boolean var1) {
      return super.getQualifiedIDLName(false);
   }

   protected Class loadClass() {
      switch (this.getTypeCode()) {
         case 1:
            return Null.class;
         case 2:
            return Boolean.TYPE;
         case 4:
            return Byte.TYPE;
         case 8:
            return Character.TYPE;
         case 16:
            return Short.TYPE;
         case 32:
            return Integer.TYPE;
         case 64:
            return Long.TYPE;
         case 128:
            return Float.TYPE;
         case 256:
            return Double.TYPE;
         default:
            throw new CompilerError("Not a primitive type");
      }
   }

   private PrimitiveType(ContextStack var1, int var2) {
      super(var1, var2 | 16777216);
      String var3 = IDLNames.getTypeName(var2, false);
      Identifier var4 = null;
      switch (var2) {
         case 1:
            var4 = idVoid;
            break;
         case 2:
            var4 = idBoolean;
            break;
         case 4:
            var4 = idByte;
            break;
         case 8:
            var4 = idChar;
            break;
         case 16:
            var4 = idShort;
            break;
         case 32:
            var4 = idInt;
            break;
         case 64:
            var4 = idLong;
            break;
         case 128:
            var4 = idFloat;
            break;
         case 256:
            var4 = idDouble;
            break;
         default:
            throw new CompilerError("Not a primitive type");
      }

      this.setNames(var4, (String[])null, var3);
      this.setRepositoryID();
   }
}
