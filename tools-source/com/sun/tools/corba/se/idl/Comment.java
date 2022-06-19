package com.sun.tools.corba.se.idl;

import java.io.PrintWriter;

public class Comment {
   static final int UNKNOWN = -1;
   static final int JAVA_DOC = 0;
   static final int C_BLOCK = 1;
   static final int CPP_LINE = 2;
   private static String _eol = System.getProperty("line.separator");
   private String _text = new String("");
   private int _style = -1;

   Comment() {
      this._text = new String("");
      this._style = -1;
   }

   Comment(String var1) {
      this._text = var1;
      this._style = this.style(this._text);
   }

   public void text(String var1) {
      this._text = var1;
      this._style = this.style(this._text);
   }

   public String text() {
      return this._text;
   }

   private int style(String var1) {
      if (var1 == null) {
         return -1;
      } else if (var1.startsWith("/**") && var1.endsWith("*/")) {
         return 0;
      } else if (var1.startsWith("/*") && var1.endsWith("*/")) {
         return 1;
      } else {
         return var1.startsWith("//") ? 2 : -1;
      }
   }

   public void write() {
      System.out.println(this._text);
   }

   public void generate(String var1, PrintWriter var2) {
      if (this._text != null && var2 != null) {
         if (var1 == null) {
            var1 = new String("");
         }

         switch (this._style) {
            case 0:
               this.print(var1, var2);
               break;
            case 1:
               this.print(var1, var2);
               break;
            case 2:
               this.print(var1, var2);
         }

      }
   }

   private void print(String var1, PrintWriter var2) {
      String var3 = this._text.trim() + _eol;
      String var4 = null;
      int var5 = 0;
      int var6 = var3.indexOf(_eol);
      int var7 = var3.length() - 1;
      var2.println();

      while(var5 < var7) {
         var4 = var3.substring(var5, var6);
         var2.println(var1 + var4);
         var5 = var6 + _eol.length();
         var6 = var5 + var3.substring(var5).indexOf(_eol);
      }

   }

   private void printJavaDoc(String var1, PrintWriter var2) {
      String var3 = this._text.substring(3, this._text.length() - 2).trim() + _eol;
      String var4 = null;
      int var5 = 0;
      int var6 = var3.indexOf(_eol);
      int var7 = var3.length() - 1;
      var2.println(_eol + var1 + "/**");

      while(var5 < var7) {
         var4 = var3.substring(var5, var6).trim();
         if (var4.startsWith("*")) {
            var2.println(var1 + " * " + var4.substring(1, var4.length()).trim());
         } else {
            var2.println(var1 + " * " + var4);
         }

         var5 = var6 + _eol.length();
         var6 = var5 + var3.substring(var5).indexOf(_eol);
      }

      var2.println(var1 + " */");
   }

   private void printCBlock(String var1, PrintWriter var2) {
      String var3 = this._text.substring(2, this._text.length() - 2).trim() + _eol;
      String var4 = null;
      int var5 = 0;
      int var6 = var3.indexOf(_eol);
      int var7 = var3.length() - 1;
      var2.println(var1 + "/*");

      while(var5 < var7) {
         var4 = var3.substring(var5, var6).trim();
         if (var4.startsWith("*")) {
            var2.println(var1 + " * " + var4.substring(1, var4.length()).trim());
         } else {
            var2.println(var1 + " * " + var4);
         }

         var5 = var6 + _eol.length();
         var6 = var5 + var3.substring(var5).indexOf(_eol);
      }

      var2.println(var1 + " */");
   }

   private void printCppLine(String var1, PrintWriter var2) {
      var2.println(var1 + "//");
      var2.println(var1 + "// " + this._text.substring(2).trim());
      var2.println(var1 + "//");
   }
}
