package com.sun.tools.classfile;

import java.io.IOException;

public class Descriptor {
   public final int index;
   private int count;

   public Descriptor(ClassReader var1) throws IOException {
      this(var1.readUnsignedShort());
   }

   public Descriptor(int var1) {
      this.index = var1;
   }

   public String getValue(ConstantPool var1) throws ConstantPoolException {
      return var1.getUTF8Value(this.index);
   }

   public int getParameterCount(ConstantPool var1) throws ConstantPoolException, InvalidDescriptor {
      String var2 = this.getValue(var1);
      int var3 = var2.indexOf(")");
      if (var3 == -1) {
         throw new InvalidDescriptor(var2);
      } else {
         this.parse(var2, 0, var3 + 1);
         return this.count;
      }
   }

   public String getParameterTypes(ConstantPool var1) throws ConstantPoolException, InvalidDescriptor {
      String var2 = this.getValue(var1);
      int var3 = var2.indexOf(")");
      if (var3 == -1) {
         throw new InvalidDescriptor(var2);
      } else {
         return this.parse(var2, 0, var3 + 1);
      }
   }

   public String getReturnType(ConstantPool var1) throws ConstantPoolException, InvalidDescriptor {
      String var2 = this.getValue(var1);
      int var3 = var2.indexOf(")");
      if (var3 == -1) {
         throw new InvalidDescriptor(var2);
      } else {
         return this.parse(var2, var3 + 1, var2.length());
      }
   }

   public String getFieldType(ConstantPool var1) throws ConstantPoolException, InvalidDescriptor {
      String var2 = this.getValue(var1);
      return this.parse(var2, 0, var2.length());
   }

   private String parse(String var1, int var2, int var3) throws InvalidDescriptor {
      int var4 = var2;
      StringBuilder var5 = new StringBuilder();
      int var6 = 0;
      this.count = 0;

      while(var4 < var3) {
         String var7;
         switch (var1.charAt(var4++)) {
            case '(':
               var5.append('(');
               continue;
            case ')':
               var5.append(')');
               continue;
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'E':
            case 'G':
            case 'H':
            case 'K':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'W':
            case 'X':
            case 'Y':
            default:
               throw new InvalidDescriptor(var1, var4 - 1);
            case 'B':
               var7 = "byte";
               break;
            case 'C':
               var7 = "char";
               break;
            case 'D':
               var7 = "double";
               break;
            case 'F':
               var7 = "float";
               break;
            case 'I':
               var7 = "int";
               break;
            case 'J':
               var7 = "long";
               break;
            case 'L':
               int var9 = var1.indexOf(59, var4);
               if (var9 == -1) {
                  throw new InvalidDescriptor(var1, var4 - 1);
               }

               var7 = var1.substring(var4, var9).replace('/', '.');
               var4 = var9 + 1;
               break;
            case 'S':
               var7 = "short";
               break;
            case 'V':
               var7 = "void";
               break;
            case 'Z':
               var7 = "boolean";
               break;
            case '[':
               ++var6;
               continue;
         }

         if (var5.length() > 1 && var5.charAt(0) == '(') {
            var5.append(", ");
         }

         var5.append(var7);

         while(var6 > 0) {
            var5.append("[]");
            --var6;
         }

         ++this.count;
      }

      return var5.toString();
   }

   public static class InvalidDescriptor extends DescriptorException {
      private static final long serialVersionUID = 1L;
      public final String desc;
      public final int index;

      InvalidDescriptor(String var1) {
         this.desc = var1;
         this.index = -1;
      }

      InvalidDescriptor(String var1, int var2) {
         this.desc = var1;
         this.index = var2;
      }

      public String getMessage() {
         return this.index == -1 ? "invalid descriptor \"" + this.desc + "\"" : "descriptor is invalid at offset " + this.index + " in \"" + this.desc + "\"";
      }
   }
}
