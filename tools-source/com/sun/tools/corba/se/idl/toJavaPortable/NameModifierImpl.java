package com.sun.tools.corba.se.idl.toJavaPortable;

public class NameModifierImpl implements NameModifier {
   private String prefix;
   private String suffix;

   public NameModifierImpl() {
      this.prefix = null;
      this.suffix = null;
   }

   public NameModifierImpl(String var1, String var2) {
      this.prefix = var1;
      this.suffix = var2;
   }

   public NameModifierImpl(String var1) {
      int var2 = var1.indexOf(37);
      int var3 = var1.lastIndexOf(37);
      if (var2 != var3) {
         throw new IllegalArgumentException(Util.getMessage("NameModifier.TooManyPercent"));
      } else if (var2 == -1) {
         throw new IllegalArgumentException(Util.getMessage("NameModifier.NoPercent"));
      } else {
         for(int var4 = 0; var4 < var1.length(); ++var4) {
            char var5 = var1.charAt(var4);
            if (this.invalidChar(var5, var4 == 0)) {
               char[] var6 = new char[]{var5};
               throw new IllegalArgumentException(Util.getMessage("NameModifier.InvalidChar", new String(var6)));
            }
         }

         this.prefix = var1.substring(0, var2);
         this.suffix = var1.substring(var2 + 1);
      }
   }

   private boolean invalidChar(char var1, boolean var2) {
      if ('A' <= var1 && var1 <= 'Z') {
         return false;
      } else if ('a' <= var1 && var1 <= 'z') {
         return false;
      } else if ('0' <= var1 && var1 <= '9') {
         return var2;
      } else if (var1 == '%') {
         return false;
      } else if (var1 == '$') {
         return false;
      } else {
         return var1 != '_';
      }
   }

   public String makeName(String var1) {
      StringBuffer var2 = new StringBuffer();
      if (this.prefix != null) {
         var2.append(this.prefix);
      }

      var2.append(var1);
      if (this.suffix != null) {
         var2.append(this.suffix);
      }

      return var2.toString();
   }
}
