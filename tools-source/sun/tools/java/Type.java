package sun.tools.java;

import java.util.Hashtable;

public class Type implements Constants {
   private static final Hashtable typeHash = new Hashtable(231);
   protected int typeCode;
   protected String typeSig;
   public static final Type[] noArgs = new Type[0];
   public static final Type tError = new Type(13, "?");
   public static final Type tPackage = new Type(13, ".");
   public static final Type tNull = new Type(8, "*");
   public static final Type tVoid = new Type(11, "V");
   public static final Type tBoolean = new Type(0, "Z");
   public static final Type tByte = new Type(1, "B");
   public static final Type tChar = new Type(2, "C");
   public static final Type tShort = new Type(3, "S");
   public static final Type tInt = new Type(4, "I");
   public static final Type tFloat = new Type(6, "F");
   public static final Type tLong = new Type(5, "J");
   public static final Type tDouble = new Type(7, "D");
   public static final Type tObject;
   public static final Type tClassDesc;
   public static final Type tString;
   public static final Type tCloneable;
   public static final Type tSerializable;

   protected Type(int var1, String var2) {
      this.typeCode = var1;
      this.typeSig = var2;
      typeHash.put(var2, this);
   }

   public final String getTypeSignature() {
      return this.typeSig;
   }

   public final int getTypeCode() {
      return this.typeCode;
   }

   public final int getTypeMask() {
      return 1 << this.typeCode;
   }

   public final boolean isType(int var1) {
      return this.typeCode == var1;
   }

   public boolean isVoidArray() {
      if (!this.isType(9)) {
         return false;
      } else {
         Type var1;
         for(var1 = this; var1.isType(9); var1 = var1.getElementType()) {
         }

         return var1.isType(11);
      }
   }

   public final boolean inMask(int var1) {
      return (1 << this.typeCode & var1) != 0;
   }

   public static synchronized Type tArray(Type var0) {
      String var1 = new String("[" + var0.getTypeSignature());
      Object var2 = (Type)typeHash.get(var1);
      if (var2 == null) {
         var2 = new ArrayType(var1, var0);
      }

      return (Type)var2;
   }

   public Type getElementType() {
      throw new CompilerError("getElementType");
   }

   public int getArrayDimension() {
      return 0;
   }

   public static synchronized Type tClass(Identifier var0) {
      if (var0.isInner()) {
         Type var3 = tClass(mangleInnerType(var0));
         if (var3.getClassName() != var0) {
            changeClassName(var3.getClassName(), var0);
         }

         return var3;
      } else if (var0.typeObject != null) {
         return var0.typeObject;
      } else {
         String var1 = new String("L" + var0.toString().replace('.', '/') + ";");
         Object var2 = (Type)typeHash.get(var1);
         if (var2 == null) {
            var2 = new ClassType(var1, var0);
         }

         var0.typeObject = (Type)var2;
         return (Type)var2;
      }
   }

   public Identifier getClassName() {
      throw new CompilerError("getClassName:" + this);
   }

   public static Identifier mangleInnerType(Identifier var0) {
      if (!var0.isInner()) {
         return var0;
      } else {
         Identifier var1 = Identifier.lookup(var0.getFlatName().toString().replace('.', '$'));
         if (var1.isInner()) {
            throw new CompilerError("mangle " + var1);
         } else {
            return Identifier.lookup(var0.getQualifier(), var1);
         }
      }
   }

   static void changeClassName(Identifier var0, Identifier var1) {
      ((ClassType)tClass(var0)).className = var1;
   }

   public static synchronized Type tMethod(Type var0) {
      return tMethod(var0, noArgs);
   }

   public static synchronized Type tMethod(Type var0, Type[] var1) {
      StringBuffer var2 = new StringBuffer();
      var2.append("(");

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2.append(var1[var3].getTypeSignature());
      }

      var2.append(")");
      var2.append(var0.getTypeSignature());
      String var5 = var2.toString();
      Object var4 = (Type)typeHash.get(var5);
      if (var4 == null) {
         var4 = new MethodType(var5, var0, var1);
      }

      return (Type)var4;
   }

   public Type getReturnType() {
      throw new CompilerError("getReturnType");
   }

   public Type[] getArgumentTypes() {
      throw new CompilerError("getArgumentTypes");
   }

   public static synchronized Type tType(String var0) {
      Type var1 = (Type)typeHash.get(var0);
      if (var1 != null) {
         return var1;
      } else {
         switch (var0.charAt(0)) {
            case '(':
               Type[] var2 = new Type[8];
               int var3 = 0;

               int var4;
               int var5;
               Type[] var6;
               for(var4 = 1; var0.charAt(var4) != ')'; var4 = var5) {
                  for(var5 = var4; var0.charAt(var5) == '['; ++var5) {
                  }

                  if (var0.charAt(var5++) == 'L') {
                     while(var0.charAt(var5++) != ';') {
                     }
                  }

                  if (var3 == var2.length) {
                     var6 = new Type[var3 * 2];
                     System.arraycopy(var2, 0, var6, 0, var3);
                     var2 = var6;
                  }

                  var2[var3++] = tType(var0.substring(var4, var5));
               }

               var6 = new Type[var3];
               System.arraycopy(var2, 0, var6, 0, var3);
               return tMethod(tType(var0.substring(var4 + 1)), var6);
            case 'L':
               return tClass(Identifier.lookup(var0.substring(1, var0.length() - 1).replace('/', '.')));
            case '[':
               return tArray(tType(var0.substring(1)));
            default:
               throw new CompilerError("invalid TypeSignature:" + var0);
         }
      }
   }

   public boolean equalArguments(Type var1) {
      return false;
   }

   public int stackSize() {
      switch (this.typeCode) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 6:
         case 9:
         case 10:
            return 1;
         case 5:
         case 7:
            return 2;
         case 8:
         case 12:
         default:
            throw new CompilerError("stackSize " + this.toString());
         case 11:
         case 13:
            return 0;
      }
   }

   public int getTypeCodeOffset() {
      switch (this.typeCode) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
            return 0;
         case 5:
            return 1;
         case 6:
            return 2;
         case 7:
            return 3;
         case 8:
         case 9:
         case 10:
            return 4;
         default:
            throw new CompilerError("invalid typecode: " + this.typeCode);
      }
   }

   public String typeString(String var1, boolean var2, boolean var3) {
      String var4 = null;
      switch (this.typeCode) {
         case 0:
            var4 = "boolean";
            break;
         case 1:
            var4 = "byte";
            break;
         case 2:
            var4 = "char";
            break;
         case 3:
            var4 = "short";
            break;
         case 4:
            var4 = "int";
            break;
         case 5:
            var4 = "long";
            break;
         case 6:
            var4 = "float";
            break;
         case 7:
            var4 = "double";
            break;
         case 8:
            var4 = "null";
            break;
         case 9:
         case 10:
         case 12:
         default:
            var4 = "unknown";
            break;
         case 11:
            var4 = "void";
            break;
         case 13:
            var4 = "<error>";
            if (this == tPackage) {
               var4 = "<package>";
            }
      }

      return var1.length() > 0 ? var4 + " " + var1 : var4;
   }

   public String typeString(String var1) {
      return this.typeString(var1, false, true);
   }

   public String toString() {
      return this.typeString("", false, true);
   }

   static {
      tObject = tClass(idJavaLangObject);
      tClassDesc = tClass(idJavaLangClass);
      tString = tClass(idJavaLangString);
      tCloneable = tClass(idJavaLangCloneable);
      tSerializable = tClass(idJavaIoSerializable);
   }
}
