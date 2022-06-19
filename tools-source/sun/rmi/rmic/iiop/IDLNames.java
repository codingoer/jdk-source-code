package sun.rmi.rmic.iiop;

import sun.tools.java.ClassNotFound;
import sun.tools.java.Identifier;

public class IDLNames implements Constants {
   public static final byte[] ASCII_HEX = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};
   private static final byte[] IDL_IDENTIFIER_CHARS = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1};

   public static String getMemberOrMethodName(NameContext var0, String var1, BatchEnvironment var2) {
      String var3 = (String)var2.namesCache.get(var1);
      if (var3 == null) {
         var3 = var0.get(var1);
         var3 = convertLeadingUnderscores(var3);
         var3 = convertIDLKeywords(var3);
         var3 = convertToISOLatin1(var3);
         var2.namesCache.put(var1, var3);
      }

      return var3;
   }

   public static String convertToISOLatin1(String var0) {
      String var1 = replace(var0, "x\\u", "U");
      var1 = replace(var1, "x\\U", "U");
      int var2 = var1.length();
      StringBuffer var3 = null;

      for(int var4 = 0; var4 < var2; ++var4) {
         char var5 = var1.charAt(var4);
         if (var5 <= 255 && IDL_IDENTIFIER_CHARS[var5] != 0) {
            if (var3 != null) {
               var3.append(var5);
            }
         } else {
            if (var3 == null) {
               var3 = new StringBuffer(var1.substring(0, var4));
            }

            var3.append("U");
            var3.append((char)ASCII_HEX[(var5 & '\uf000') >>> 12]);
            var3.append((char)ASCII_HEX[(var5 & 3840) >>> 8]);
            var3.append((char)ASCII_HEX[(var5 & 240) >>> 4]);
            var3.append((char)ASCII_HEX[var5 & 15]);
         }
      }

      if (var3 != null) {
         var1 = var3.toString();
      }

      return var1;
   }

   public static String convertIDLKeywords(String var0) {
      for(int var1 = 0; var1 < IDL_KEYWORDS.length; ++var1) {
         if (var0.equalsIgnoreCase(IDL_KEYWORDS[var1])) {
            return "_" + var0;
         }
      }

      return var0;
   }

   public static String convertLeadingUnderscores(String var0) {
      return var0.startsWith("_") ? "J" + var0 : var0;
   }

   public static String getClassOrInterfaceName(Identifier var0, BatchEnvironment var1) throws Exception {
      String var2 = var0.getName().toString();
      String var3 = null;
      if (var0.isQualified()) {
         var3 = var0.getQualifier().toString();
      }

      String var4 = (String)var1.namesCache.get(var2);
      if (var4 == null) {
         var4 = replace(var2, ". ", "__");
         var4 = convertToISOLatin1(var4);
         NameContext var5 = NameContext.forName(var3, false, var1);
         var5.assertPut(var4);
         var4 = getTypeOrModuleName(var4);
         var1.namesCache.put(var2, var4);
      }

      return var4;
   }

   public static String getExceptionName(String var0) {
      String var1;
      if (var0.endsWith("Exception")) {
         var1 = stripLeadingUnderscore(var0.substring(0, var0.lastIndexOf("Exception")) + "Ex");
      } else {
         var1 = var0 + "Ex";
      }

      return var1;
   }

   public static String[] getModuleNames(Identifier var0, boolean var1, BatchEnvironment var2) throws Exception {
      String[] var3 = null;
      if (var0.isQualified()) {
         Identifier var4 = var0.getQualifier();
         var2.modulesContext.assertPut(var4.toString());
         int var5 = 1;

         Identifier var6;
         for(var6 = var4; var6.isQualified(); ++var5) {
            var6 = var6.getQualifier();
         }

         var3 = new String[var5];
         int var7 = var5 - 1;
         var6 = var4;

         for(int var8 = 0; var8 < var5; ++var8) {
            String var9 = var6.getName().toString();
            String var10 = (String)var2.namesCache.get(var9);
            if (var10 == null) {
               var10 = convertToISOLatin1(var9);
               var10 = getTypeOrModuleName(var10);
               var2.namesCache.put(var9, var10);
            }

            var3[var7--] = var10;
            var6 = var6.getQualifier();
         }
      }

      if (var1) {
         if (var3 == null) {
            var3 = IDL_BOXEDIDL_MODULE;
         } else {
            String[] var11 = new String[var3.length + IDL_BOXEDIDL_MODULE.length];
            System.arraycopy(IDL_BOXEDIDL_MODULE, 0, var11, 0, IDL_BOXEDIDL_MODULE.length);
            System.arraycopy(var3, 0, var11, IDL_BOXEDIDL_MODULE.length, var3.length);
            var3 = var11;
         }
      }

      return var3;
   }

   public static String getArrayName(Type var0, int var1) {
      StringBuffer var2 = new StringBuffer(64);
      var2.append("seq");
      var2.append(Integer.toString(var1));
      var2.append("_");
      var2.append(replace(stripLeadingUnderscore(var0.getIDLName()), " ", "_"));
      return var2.toString();
   }

   public static String[] getArrayModuleNames(Type var0) {
      String[] var2 = var0.getIDLModuleNames();
      int var3 = var2.length;
      String[] var1;
      if (var3 == 0) {
         var1 = IDL_SEQUENCE_MODULE;
      } else {
         var1 = new String[var3 + IDL_SEQUENCE_MODULE.length];
         System.arraycopy(IDL_SEQUENCE_MODULE, 0, var1, 0, IDL_SEQUENCE_MODULE.length);
         System.arraycopy(var2, 0, var1, IDL_SEQUENCE_MODULE.length, var3);
      }

      return var1;
   }

   private static int getInitialAttributeKind(CompoundType.Method var0, BatchEnvironment var1) throws ClassNotFound {
      byte var2 = 0;
      if (!var0.isConstructor()) {
         boolean var3 = true;
         ValueType[] var4 = var0.getExceptions();
         if (var4.length > 0) {
            for(int var5 = 0; var5 < var4.length; ++var5) {
               if (var4[var5].isCheckedException() && !var4[var5].isRemoteExceptionOrSubclass()) {
                  var3 = false;
                  break;
               }
            }
         } else {
            var3 = var0.getEnclosing().isType(32768);
         }

         if (var3) {
            String var11 = var0.getName();
            int var6 = var11.length();
            int var7 = var0.getArguments().length;
            Type var8 = var0.getReturnType();
            boolean var9 = var8.isType(1);
            boolean var10 = var8.isType(2);
            if (var11.startsWith("get") && var6 > 3 && var7 == 0 && !var9) {
               var2 = 2;
            } else if (var11.startsWith("is") && var6 > 2 && var7 == 0 && var10) {
               var2 = 1;
            } else if (var11.startsWith("set") && var6 > 3 && var7 == 1 && var9) {
               var2 = 5;
            }
         }
      }

      return var2;
   }

   private static void setAttributeKinds(CompoundType.Method[] var0, int[] var1, String[] var2) {
      int var3 = var0.length;

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         switch (var1[var4]) {
            case 1:
               var2[var4] = var2[var4].substring(2);
               break;
            case 2:
               var2[var4] = var2[var4].substring(3);
            case 3:
            case 4:
            default:
               break;
            case 5:
               var2[var4] = var2[var4].substring(3);
         }
      }

      int var5;
      for(var4 = 0; var4 < var3; ++var4) {
         if (var1[var4] == 1) {
            for(var5 = 0; var5 < var3; ++var5) {
               if (var5 != var4 && (var1[var5] == 2 || var1[var5] == 5) && var2[var4].equals(var2[var5])) {
                  Type var6 = var0[var4].getReturnType();
                  Type var7;
                  if (var1[var5] == 2) {
                     var7 = var0[var5].getReturnType();
                  } else {
                     var7 = var0[var5].getArguments()[0];
                  }

                  if (!var6.equals(var7)) {
                     var1[var4] = 0;
                     var2[var4] = var0[var4].getName();
                     break;
                  }
               }
            }
         }
      }

      for(var4 = 0; var4 < var3; ++var4) {
         if (var1[var4] == 5) {
            var5 = -1;
            int var10 = -1;

            for(int var13 = 0; var13 < var3; ++var13) {
               if (var13 != var4 && var2[var4].equals(var2[var13])) {
                  Type var8 = var0[var13].getReturnType();
                  Type var9 = var0[var4].getArguments()[0];
                  if (var8.equals(var9)) {
                     if (var1[var13] == 1) {
                        var10 = var13;
                     } else if (var1[var13] == 2) {
                        var5 = var13;
                     }
                  }
               }
            }

            if (var5 > -1) {
               if (var10 > -1) {
                  var1[var10] = 3;
                  var0[var10].setAttributePairIndex(var4);
                  var0[var4].setAttributePairIndex(var10);
                  var1[var5] = 0;
                  var2[var5] = var0[var5].getName();
               } else {
                  var1[var5] = 4;
                  var0[var5].setAttributePairIndex(var4);
                  var0[var4].setAttributePairIndex(var5);
               }
            } else if (var10 > -1) {
               var1[var10] = 3;
               var0[var10].setAttributePairIndex(var4);
               var0[var4].setAttributePairIndex(var10);
            } else {
               var1[var4] = 0;
               var2[var4] = var0[var4].getName();
            }
         }
      }

      for(var4 = 0; var4 < var3; ++var4) {
         if (var1[var4] != 0) {
            String var11 = var2[var4];
            if (Character.isUpperCase(var11.charAt(0)) && (var11.length() == 1 || Character.isLowerCase(var11.charAt(1)))) {
               StringBuffer var12 = new StringBuffer(var11);
               var12.setCharAt(0, Character.toLowerCase(var11.charAt(0)));
               var2[var4] = var12.toString();
            }
         }

         var0[var4].setAttributeKind(var1[var4]);
      }

   }

   public static void setMethodNames(CompoundType var0, CompoundType.Method[] var1, BatchEnvironment var2) throws Exception {
      int var3 = var1.length;
      if (var3 != 0) {
         String[] var4 = new String[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = var1[var5].getName();
         }

         CompoundType var14 = var1[0].getEnclosing();
         int var7;
         if (var14.isType(4096) || var14.isType(8192) || var14.isType(32768)) {
            int[] var6 = new int[var3];

            for(var7 = 0; var7 < var3; ++var7) {
               var6[var7] = getInitialAttributeKind(var1[var7], var2);
            }

            setAttributeKinds(var1, var6, var4);
         }

         NameContext var15 = new NameContext(true);

         for(var7 = 0; var7 < var3; ++var7) {
            var15.put(var4[var7]);
         }

         boolean var16 = false;

         for(int var8 = 0; var8 < var3; ++var8) {
            if (!var1[var8].isConstructor()) {
               var4[var8] = getMemberOrMethodName(var15, var4[var8], var2);
            } else {
               var4[var8] = "create";
               var16 = true;
            }
         }

         boolean[] var17 = new boolean[var3];

         int var9;
         for(var9 = 0; var9 < var3; ++var9) {
            var17[var9] = !var1[var9].isAttribute() && !var1[var9].isConstructor() && doesMethodCollide(var4[var9], var1[var9], var1, var4, true);
         }

         convertOverloadedMethods(var1, var4, var17);

         for(var9 = 0; var9 < var3; ++var9) {
            var17[var9] = !var1[var9].isAttribute() && var1[var9].isConstructor() && doesConstructorCollide(var4[var9], var1[var9], var1, var4, true);
         }

         convertOverloadedMethods(var1, var4, var17);

         CompoundType.Method var10;
         for(var9 = 0; var9 < var3; ++var9) {
            var10 = var1[var9];
            if (var10.isAttribute() && doesMethodCollide(var4[var9], var10, var1, var4, true)) {
               var4[var9] = var4[var9] + "__";
            }
         }

         if (var16) {
            for(var9 = 0; var9 < var3; ++var9) {
               var10 = var1[var9];
               if (var10.isConstructor() && doesConstructorCollide(var4[var9], var10, var1, var4, false)) {
                  var4[var9] = var4[var9] + "__";
               }
            }
         }

         String var19 = var0.getIDLName();

         int var18;
         for(var18 = 0; var18 < var3; ++var18) {
            if (var4[var18].equalsIgnoreCase(var19) && !var1[var18].isAttribute()) {
               var4[var18] = var4[var18] + "_";
            }
         }

         for(var18 = 0; var18 < var3; ++var18) {
            if (doesMethodCollide(var4[var18], var1[var18], var1, var4, false)) {
               throw new Exception(var1[var18].toString());
            }
         }

         for(var18 = 0; var18 < var3; ++var18) {
            CompoundType.Method var11 = var1[var18];
            String var12 = var4[var18];
            if (var11.isAttribute()) {
               var12 = ATTRIBUTE_WIRE_PREFIX[var11.getAttributeKind()] + stripLeadingUnderscore(var12);
               String var13 = var4[var18];
               var11.setAttributeName(var13);
            }

            var11.setIDLName(var12);
         }

      }
   }

   private static String stripLeadingUnderscore(String var0) {
      return var0 != null && var0.length() > 1 && var0.charAt(0) == '_' ? var0.substring(1) : var0;
   }

   private static String stripTrailingUnderscore(String var0) {
      return var0 != null && var0.length() > 1 && var0.charAt(var0.length() - 1) == '_' ? var0.substring(0, var0.length() - 1) : var0;
   }

   private static void convertOverloadedMethods(CompoundType.Method[] var0, String[] var1, boolean[] var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var2[var3]) {
            CompoundType.Method var4 = var0[var3];
            Type[] var5 = var4.getArguments();

            for(int var6 = 0; var6 < var5.length; ++var6) {
               var1[var3] = var1[var3] + "__";
               String var7 = var5[var6].getQualifiedIDLName(false);
               var7 = replace(var7, "::_", "_");
               var7 = replace(var7, "::", "_");
               var7 = replace(var7, " ", "_");
               var1[var3] = var1[var3] + var7;
            }

            if (var5.length == 0) {
               var1[var3] = var1[var3] + "__";
            }

            var1[var3] = stripLeadingUnderscore(var1[var3]);
         }
      }

   }

   private static boolean doesMethodCollide(String var0, CompoundType.Method var1, CompoundType.Method[] var2, String[] var3, boolean var4) {
      for(int var5 = 0; var5 < var2.length; ++var5) {
         CompoundType.Method var6 = var2[var5];
         if (var1 != var6 && !var6.isConstructor() && (!var4 || !var6.isAttribute()) && var0.equals(var3[var5])) {
            int var7 = var1.getAttributeKind();
            int var8 = var6.getAttributeKind();
            if (var7 == 0 || var8 == 0 || (var7 != 5 || var8 == 5) && (var7 == 5 || var8 != 5) && (var7 != 3 || var8 != 2) && (var7 != 2 || var8 != 3)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean doesConstructorCollide(String var0, CompoundType.Method var1, CompoundType.Method[] var2, String[] var3, boolean var4) {
      for(int var5 = 0; var5 < var2.length; ++var5) {
         CompoundType.Method var6 = var2[var5];
         if (var1 != var6 && var6.isConstructor() == var4 && var0.equals(var3[var5])) {
            return true;
         }
      }

      return false;
   }

   public static void setMemberNames(CompoundType var0, CompoundType.Member[] var1, CompoundType.Method[] var2, BatchEnvironment var3) throws Exception {
      NameContext var4 = new NameContext(true);

      int var5;
      for(var5 = 0; var5 < var1.length; ++var5) {
         var4.put(var1[var5].getName());
      }

      String var7;
      for(var5 = 0; var5 < var1.length; ++var5) {
         CompoundType.Member var6 = var1[var5];
         var7 = getMemberOrMethodName(var4, var6.getName(), var3);
         var6.setIDLName(var7);
      }

      String var10 = var0.getIDLName();

      int var11;
      for(var11 = 0; var11 < var1.length; ++var11) {
         var7 = var1[var11].getIDLName();
         if (var7.equalsIgnoreCase(var10)) {
            var1[var11].setIDLName(var7 + "_");
         }
      }

      for(var11 = 0; var11 < var1.length; ++var11) {
         var7 = var1[var11].getIDLName();

         for(int var8 = 0; var8 < var1.length; ++var8) {
            if (var11 != var8 && var1[var8].getIDLName().equals(var7)) {
               throw new Exception(var7);
            }
         }
      }

      boolean var12;
      do {
         var12 = false;

         for(int var13 = 0; var13 < var1.length; ++var13) {
            String var14 = var1[var13].getIDLName();

            for(int var9 = 0; var9 < var2.length; ++var9) {
               if (var2[var9].getIDLName().equals(var14)) {
                  var1[var13].setIDLName(var14 + "_");
                  var12 = true;
                  break;
               }
            }
         }
      } while(var12);

   }

   public static String getTypeName(int var0, boolean var1) {
      String var2 = null;
      switch (var0) {
         case 1:
            var2 = "void";
            break;
         case 2:
            var2 = "boolean";
            break;
         case 4:
            var2 = "octet";
            break;
         case 8:
            var2 = "wchar";
            break;
         case 16:
            var2 = "short";
            break;
         case 32:
            var2 = "long";
            break;
         case 64:
            var2 = "long long";
            break;
         case 128:
            var2 = "float";
            break;
         case 256:
            var2 = "double";
            break;
         case 512:
            if (var1) {
               var2 = "wstring";
            } else {
               var2 = "WStringValue";
            }
            break;
         case 1024:
            var2 = "any";
            break;
         case 2048:
            var2 = "Object";
      }

      return var2;
   }

   public static String getQualifiedName(String[] var0, String var1) {
      String var2 = null;
      if (var0 != null && var0.length > 0) {
         for(int var3 = 0; var3 < var0.length; ++var3) {
            if (var3 == 0) {
               var2 = var0[0];
            } else {
               var2 = var2 + "::";
               var2 = var2 + var0[var3];
            }
         }

         var2 = var2 + "::";
         var2 = var2 + var1;
      } else {
         var2 = var1;
      }

      return var2;
   }

   public static String replace(String var0, String var1, String var2) {
      int var3 = var0.indexOf(var1, 0);
      if (var3 < 0) {
         return var0;
      } else {
         StringBuffer var4 = new StringBuffer(var0.length() + 16);
         int var5 = var1.length();

         int var6;
         for(var6 = 0; var3 >= 0; var3 = var0.indexOf(var1, var6)) {
            var4.append(var0.substring(var6, var3));
            var4.append(var2);
            var6 = var3 + var5;
         }

         if (var6 < var0.length()) {
            var4.append(var0.substring(var6));
         }

         return var4.toString();
      }
   }

   public static String getIDLRepositoryID(String var0) {
      return "IDL:" + replace(var0, "::", "/") + ":1.0";
   }

   private static String getTypeOrModuleName(String var0) {
      String var1 = convertLeadingUnderscores(var0);
      return convertIDLKeywords(var1);
   }
}
