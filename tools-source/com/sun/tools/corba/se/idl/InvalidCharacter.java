package com.sun.tools.corba.se.idl;

import java.io.IOException;

public class InvalidCharacter extends IOException {
   private String message = null;

   public InvalidCharacter(String var1, String var2, int var3, int var4, char var5) {
      String var6 = "^";
      if (var4 > 1) {
         byte[] var7 = new byte[var4 - 1];

         for(int var8 = 0; var8 < var4 - 1; ++var8) {
            var7[var8] = 32;
         }

         var6 = new String(var7) + var6;
      }

      String[] var9 = new String[]{var1, Integer.toString(var3), "" + var5, Integer.toString(var5), var2, var6};
      this.message = Util.getMessage("InvalidCharacter.1", var9);
   }

   public String getMessage() {
      return this.message;
   }
}
