package sun.tools.native2ascii;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

class A2NFilter extends FilterReader {
   private char[] trailChars = null;

   public A2NFilter(Reader var1) {
      super(var1);
   }

   public int read(char[] var1, int var2, int var3) throws IOException {
      int var4 = 0;
      int var5 = 0;
      char[] var6 = new char[var3];
      boolean var7 = false;
      boolean var8 = false;
      int var9;
      if (this.trailChars != null) {
         for(var9 = 0; var9 < this.trailChars.length; ++var9) {
            var6[var9] = this.trailChars[var9];
         }

         var4 = this.trailChars.length;
         this.trailChars = null;
      }

      var9 = this.in.read(var6, var4, var3 - var4);
      if (var9 < 0) {
         var8 = true;
         if (var4 == 0) {
            return -1;
         }
      } else {
         var4 += var9;
      }

      int var10 = 0;

      while(var10 < var4) {
         char var11 = var6[var10++];
         if (var11 != '\\' || var8 && var4 <= 5) {
            var1[var5++] = var11;
         } else {
            int var12 = var4 - var10;
            int var13;
            if (var12 < 5) {
               this.trailChars = new char[1 + var12];
               this.trailChars[0] = var11;

               for(var13 = 0; var13 < var12; ++var13) {
                  this.trailChars[1 + var13] = var6[var10 + var13];
               }

               return var5;
            }

            var11 = var6[var10++];
            if (var11 != 'u') {
               var1[var5++] = '\\';
               var1[var5++] = var11;
            } else {
               var13 = 0;
               boolean var14 = true;

               try {
                  var13 = (char)Integer.parseInt(new String(var6, var10, 4), 16);
               } catch (NumberFormatException var16) {
                  var14 = false;
               }

               if (var14 && Main.canConvert((char)var13)) {
                  var1[var5++] = (char)var13;
                  var10 += 4;
               } else {
                  var1[var5++] = '\\';
                  var1[var5++] = 'u';
               }
            }
         }
      }

      return var5;
   }

   public int read() throws IOException {
      char[] var1 = new char[1];
      return this.read(var1, 0, 1) == -1 ? -1 : var1[0];
   }
}
