package sun.tools.jstat;

import sun.jvmstat.monitor.MonitorException;

public class SymbolResolutionClosure implements Closure {
   private static final boolean debug = Boolean.getBoolean("SymbolResolutionClosure.debug");
   private ExpressionEvaluator ee;

   public SymbolResolutionClosure(ExpressionEvaluator var1) {
      this.ee = var1;
   }

   public void visit(Object var1, boolean var2) throws MonitorException {
      if (var1 instanceof ColumnFormat) {
         ColumnFormat var3 = (ColumnFormat)var1;
         Expression var4 = var3.getExpression();
         String var5 = var4.toString();
         var4 = (Expression)this.ee.evaluate(var4);
         if (debug) {
            System.out.print("Expression: " + var5 + " resolved to " + var4.toString());
         }

         var3.setExpression(var4);
      }
   }
}
