package sun.tools.jstat;

import sun.jvmstat.monitor.MonitorException;

public class HeaderClosure implements Closure {
   private static final char ALIGN_CHAR = '^';
   private StringBuilder header = new StringBuilder();

   public void visit(Object var1, boolean var2) throws MonitorException {
      if (var1 instanceof ColumnFormat) {
         ColumnFormat var3 = (ColumnFormat)var1;
         String var4 = var3.getHeader();
         if (var4.indexOf(94) >= 0) {
            int var5 = var4.length();
            if (var4.charAt(0) == '^' && var4.charAt(var5 - 1) == '^') {
               var3.setWidth(Math.max(var3.getWidth(), Math.max(var3.getFormat().length(), var5 - 2)));
               var4 = var4.substring(1, var5 - 1);
               var4 = Alignment.CENTER.align(var4, var3.getWidth());
            } else if (var4.charAt(0) == '^') {
               var3.setWidth(Math.max(var3.getWidth(), Math.max(var3.getFormat().length(), var5 - 1)));
               var4 = var4.substring(1, var5);
               var4 = Alignment.LEFT.align(var4, var3.getWidth());
            } else if (var4.charAt(var5 - 1) == '^') {
               var3.setWidth(Math.max(var3.getWidth(), Math.max(var3.getFormat().length(), var5 - 1)));
               var4 = var4.substring(0, var5 - 1);
               var4 = Alignment.RIGHT.align(var4, var3.getWidth());
            }
         }

         this.header.append(var4);
         if (var2) {
            this.header.append(" ");
         }

      }
   }

   public String getHeader() {
      return this.header.toString();
   }
}
