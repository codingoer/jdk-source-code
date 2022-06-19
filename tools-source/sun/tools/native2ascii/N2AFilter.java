package sun.tools.native2ascii;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

class N2AFilter extends FilterWriter {
   public N2AFilter(Writer var1) {
      super(var1);
   }

   public void write(char var1) throws IOException {
      char[] var2 = new char[]{var1};
      this.write(var2, 0, 1);
   }

   public void write(char[] var1, int var2, int var3) throws IOException {
      String var4 = System.getProperty("line.separator");

      for(int var5 = 0; var5 < var3; ++var5) {
         if (var1[var5] <= 127) {
            this.out.write(var1[var5]);
         } else {
            this.out.write(92);
            this.out.write(117);
            String var6 = Integer.toHexString(var1[var5]);
            StringBuffer var7 = new StringBuffer(var6);
            var7.reverse();
            int var8 = 4 - var7.length();

            int var9;
            for(var9 = 0; var9 < var8; ++var9) {
               var7.append('0');
            }

            for(var9 = 0; var9 < 4; ++var9) {
               this.out.write(var7.charAt(3 - var9));
            }
         }
      }

   }
}
