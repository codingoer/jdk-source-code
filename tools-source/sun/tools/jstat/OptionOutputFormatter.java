package sun.tools.jstat;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;

public class OptionOutputFormatter implements OutputFormatter {
   private OptionFormat format;
   private String header;
   private MonitoredVm vm;

   public OptionOutputFormatter(MonitoredVm var1, OptionFormat var2) throws MonitorException {
      this.vm = var1;
      this.format = var2;
      this.resolve();
   }

   private void resolve() throws MonitorException {
      ExpressionResolver var1 = new ExpressionResolver(this.vm);
      SymbolResolutionClosure var2 = new SymbolResolutionClosure(var1);
      this.format.apply(var2);
   }

   public String getHeader() throws MonitorException {
      if (this.header == null) {
         HeaderClosure var1 = new HeaderClosure();
         this.format.apply(var1);
         this.header = var1.getHeader();
      }

      return this.header;
   }

   public String getRow() throws MonitorException {
      RowClosure var1 = new RowClosure(this.vm);
      this.format.apply(var1);
      return var1.getRow();
   }
}
