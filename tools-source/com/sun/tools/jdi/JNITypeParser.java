package com.sun.tools.jdi;

import java.util.ArrayList;
import java.util.List;

public class JNITypeParser {
   static final char SIGNATURE_ENDCLASS = ';';
   static final char SIGNATURE_FUNC = '(';
   static final char SIGNATURE_ENDFUNC = ')';
   private String signature;
   private List typeNameList;
   private List signatureList;
   private int currentIndex;

   JNITypeParser(String var1) {
      this.signature = var1;
   }

   static String typeNameToSignature(String var0) {
      StringBuffer var1 = new StringBuffer();
      int var2 = var0.indexOf(91);

      for(int var3 = var2; var3 != -1; var3 = var0.indexOf(91, var3 + 1)) {
         var1.append('[');
      }

      if (var2 != -1) {
         var0 = var0.substring(0, var2);
      }

      if (var0.equals("boolean")) {
         var1.append('Z');
      } else if (var0.equals("byte")) {
         var1.append('B');
      } else if (var0.equals("char")) {
         var1.append('C');
      } else if (var0.equals("short")) {
         var1.append('S');
      } else if (var0.equals("int")) {
         var1.append('I');
      } else if (var0.equals("long")) {
         var1.append('J');
      } else if (var0.equals("float")) {
         var1.append('F');
      } else if (var0.equals("double")) {
         var1.append('D');
      } else {
         var1.append('L');
         var1.append(var0.replace('.', '/'));
         var1.append(';');
      }

      return var1.toString();
   }

   String typeName() {
      return (String)this.typeNameList().get(this.typeNameList().size() - 1);
   }

   List argumentTypeNames() {
      return this.typeNameList().subList(0, this.typeNameList().size() - 1);
   }

   String signature() {
      return (String)this.signatureList().get(this.signatureList().size() - 1);
   }

   List argumentSignatures() {
      return this.signatureList().subList(0, this.signatureList().size() - 1);
   }

   int dimensionCount() {
      int var1 = 0;

      for(String var2 = this.signature(); var2.charAt(var1) == '['; ++var1) {
      }

      return var1;
   }

   String componentSignature(int var1) {
      return this.signature().substring(var1);
   }

   private synchronized List signatureList() {
      if (this.signatureList == null) {
         this.signatureList = new ArrayList(10);
         this.currentIndex = 0;

         while(this.currentIndex < this.signature.length()) {
            String var1 = this.nextSignature();
            this.signatureList.add(var1);
         }

         if (this.signatureList.size() == 0) {
            throw new IllegalArgumentException("Invalid JNI signature '" + this.signature + "'");
         }
      }

      return this.signatureList;
   }

   private synchronized List typeNameList() {
      if (this.typeNameList == null) {
         this.typeNameList = new ArrayList(10);
         this.currentIndex = 0;

         while(this.currentIndex < this.signature.length()) {
            String var1 = this.nextTypeName();
            this.typeNameList.add(var1);
         }

         if (this.typeNameList.size() == 0) {
            throw new IllegalArgumentException("Invalid JNI signature '" + this.signature + "'");
         }
      }

      return this.typeNameList;
   }

   private String nextSignature() {
      char var1 = this.signature.charAt(this.currentIndex++);
      switch (var1) {
         case '(':
         case ')':
            return this.nextSignature();
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
            throw new IllegalArgumentException("Invalid JNI signature character '" + var1 + "'");
         case 'B':
         case 'C':
         case 'D':
         case 'F':
         case 'I':
         case 'J':
         case 'S':
         case 'V':
         case 'Z':
            return String.valueOf(var1);
         case 'L':
            int var2 = this.signature.indexOf(59, this.currentIndex);
            String var3 = this.signature.substring(this.currentIndex - 1, var2 + 1);
            this.currentIndex = var2 + 1;
            return var3;
         case '[':
            return var1 + this.nextSignature();
      }
   }

   private String nextTypeName() {
      char var1 = this.signature.charAt(this.currentIndex++);
      switch (var1) {
         case '(':
         case ')':
            return this.nextTypeName();
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
            throw new IllegalArgumentException("Invalid JNI signature character '" + var1 + "'");
         case 'B':
            return "byte";
         case 'C':
            return "char";
         case 'D':
            return "double";
         case 'F':
            return "float";
         case 'I':
            return "int";
         case 'J':
            return "long";
         case 'L':
            int var2 = this.signature.indexOf(59, this.currentIndex);
            String var3 = this.signature.substring(this.currentIndex, var2);
            var3 = var3.replace('/', '.');
            this.currentIndex = var2 + 1;
            return var3;
         case 'S':
            return "short";
         case 'V':
            return "void";
         case 'Z':
            return "boolean";
         case '[':
            return this.nextTypeName() + "[]";
      }
   }
}
