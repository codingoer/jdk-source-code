package sun.tools.jstat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;

public class RowClosure implements Closure {
   private MonitoredVm vm;
   private StringBuilder row = new StringBuilder();

   public RowClosure(MonitoredVm var1) {
      this.vm = var1;
   }

   public void visit(Object var1, boolean var2) throws MonitorException {
      if (var1 instanceof ColumnFormat) {
         ColumnFormat var3 = (ColumnFormat)var1;
         String var4 = null;
         Expression var5 = var3.getExpression();
         ExpressionExecuter var6 = new ExpressionExecuter(this.vm);
         Object var7 = var6.evaluate(var5);
         if (var7 instanceof String) {
            var4 = (String)var7;
         } else if (var7 instanceof Number) {
            double var8 = ((Number)var7).doubleValue();
            double var10 = var3.getScale().scale(var8);
            DecimalFormat var12 = new DecimalFormat(var3.getFormat());
            DecimalFormatSymbols var13 = var12.getDecimalFormatSymbols();
            var13.setNaN("-");
            var12.setDecimalFormatSymbols(var13);
            var4 = var12.format(var10);
         }

         var3.setPreviousValue(var7);
         var4 = var3.getAlignment().align(var4, var3.getWidth());
         this.row.append(var4);
         if (var2) {
            this.row.append(" ");
         }

      }
   }

   public String getRow() {
      return this.row.toString();
   }
}
