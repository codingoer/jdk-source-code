package com.sun.tools.classfile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Signature extends Descriptor {
   private String sig;
   private int sigp;
   private Type type;

   public Signature(int var1) {
      super(var1);
   }

   public Type getType(ConstantPool var1) throws ConstantPoolException {
      if (this.type == null) {
         this.type = this.parse(this.getValue(var1));
      }

      return this.type;
   }

   public int getParameterCount(ConstantPool var1) throws ConstantPoolException {
      Type.MethodType var2 = (Type.MethodType)this.getType(var1);
      return var2.paramTypes.size();
   }

   public String getParameterTypes(ConstantPool var1) throws ConstantPoolException {
      Type.MethodType var2 = (Type.MethodType)this.getType(var1);
      StringBuilder var3 = new StringBuilder();
      var3.append("(");
      String var4 = "";

      for(Iterator var5 = var2.paramTypes.iterator(); var5.hasNext(); var4 = ", ") {
         Type var6 = (Type)var5.next();
         var3.append(var4);
         var3.append(var6);
      }

      var3.append(")");
      return var3.toString();
   }

   public String getReturnType(ConstantPool var1) throws ConstantPoolException {
      Type.MethodType var2 = (Type.MethodType)this.getType(var1);
      return var2.returnType.toString();
   }

   public String getFieldType(ConstantPool var1) throws ConstantPoolException {
      return this.getType(var1).toString();
   }

   private Type parse(String var1) {
      this.sig = var1;
      this.sigp = 0;
      List var2 = null;
      if (var1.charAt(this.sigp) == '<') {
         var2 = this.parseTypeParamTypes();
      }

      ArrayList var5;
      if (var1.charAt(this.sigp) != '(') {
         Type var6 = this.parseTypeSignature();
         if (var2 == null && this.sigp == var1.length()) {
            return var6;
         } else {
            for(var5 = null; this.sigp < var1.length(); var5.add(this.parseTypeSignature())) {
               if (var5 == null) {
                  var5 = new ArrayList();
               }
            }

            return new Type.ClassSigType(var2, var6, var5);
         }
      } else {
         List var3 = this.parseTypeSignatures(')');
         Type var4 = this.parseTypeSignature();

         for(var5 = null; this.sigp < var1.length() && var1.charAt(this.sigp) == '^'; var5.add(this.parseTypeSignature())) {
            ++this.sigp;
            if (var5 == null) {
               var5 = new ArrayList();
            }
         }

         return new Type.MethodType(var2, var3, var4, var5);
      }
   }

   private Type parseTypeSignature() {
      switch (this.sig.charAt(this.sigp)) {
         case '*':
            ++this.sigp;
            return new Type.WildcardType();
         case '+':
            ++this.sigp;
            return new Type.WildcardType(Type.WildcardType.Kind.EXTENDS, this.parseTypeSignature());
         case ',':
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
         case 'U':
         case 'W':
         case 'X':
         case 'Y':
         default:
            throw new IllegalStateException(this.debugInfo());
         case '-':
            ++this.sigp;
            return new Type.WildcardType(Type.WildcardType.Kind.SUPER, this.parseTypeSignature());
         case 'B':
            ++this.sigp;
            return new Type.SimpleType("byte");
         case 'C':
            ++this.sigp;
            return new Type.SimpleType("char");
         case 'D':
            ++this.sigp;
            return new Type.SimpleType("double");
         case 'F':
            ++this.sigp;
            return new Type.SimpleType("float");
         case 'I':
            ++this.sigp;
            return new Type.SimpleType("int");
         case 'J':
            ++this.sigp;
            return new Type.SimpleType("long");
         case 'L':
            return this.parseClassTypeSignature();
         case 'S':
            ++this.sigp;
            return new Type.SimpleType("short");
         case 'T':
            return this.parseTypeVariableSignature();
         case 'V':
            ++this.sigp;
            return new Type.SimpleType("void");
         case 'Z':
            ++this.sigp;
            return new Type.SimpleType("boolean");
         case '[':
            ++this.sigp;
            return new Type.ArrayType(this.parseTypeSignature());
      }
   }

   private List parseTypeSignatures(char var1) {
      ++this.sigp;
      ArrayList var2 = new ArrayList();

      while(this.sig.charAt(this.sigp) != var1) {
         var2.add(this.parseTypeSignature());
      }

      ++this.sigp;
      return var2;
   }

   private Type parseClassTypeSignature() {
      assert this.sig.charAt(this.sigp) == 'L';

      ++this.sigp;
      return this.parseClassTypeSignatureRest();
   }

   private Type parseClassTypeSignatureRest() {
      StringBuilder var1 = new StringBuilder();
      List var2 = null;
      Type.ClassType var3 = null;

      char var4;
      do {
         switch (var4 = this.sig.charAt(this.sigp)) {
            case '.':
            case ';':
               ++this.sigp;
               var3 = new Type.ClassType(var3, var1.toString(), var2);
               var1.setLength(0);
               var2 = null;
               break;
            case '<':
               var2 = this.parseTypeSignatures('>');
               break;
            default:
               ++this.sigp;
               var1.append(var4);
         }
      } while(var4 != ';');

      return var3;
   }

   private List parseTypeParamTypes() {
      assert this.sig.charAt(this.sigp) == '<';

      ++this.sigp;
      ArrayList var1 = new ArrayList();

      while(this.sig.charAt(this.sigp) != '>') {
         var1.add(this.parseTypeParamType());
      }

      ++this.sigp;
      return var1;
   }

   private Type.TypeParamType parseTypeParamType() {
      int var1 = this.sig.indexOf(":", this.sigp);
      String var2 = this.sig.substring(this.sigp, var1);
      Type var3 = null;
      ArrayList var4 = null;
      this.sigp = var1 + 1;
      if (this.sig.charAt(this.sigp) != ':') {
         var3 = this.parseTypeSignature();
      }

      for(; this.sig.charAt(this.sigp) == ':'; var4.add(this.parseTypeSignature())) {
         ++this.sigp;
         if (var4 == null) {
            var4 = new ArrayList();
         }
      }

      return new Type.TypeParamType(var2, var3, var4);
   }

   private Type parseTypeVariableSignature() {
      ++this.sigp;
      int var1 = this.sig.indexOf(59, this.sigp);
      Type.SimpleType var2 = new Type.SimpleType(this.sig.substring(this.sigp, var1));
      this.sigp = var1 + 1;
      return var2;
   }

   private String debugInfo() {
      return this.sig.substring(0, this.sigp) + "!" + this.sig.charAt(this.sigp) + "!" + this.sig.substring(this.sigp + 1);
   }
}
