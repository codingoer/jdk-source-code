package sun.tools.jstat;

import java.util.HashMap;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitoredVm;

public class ExpressionExecuter implements ExpressionEvaluator {
   private static final boolean debug = Boolean.getBoolean("ExpressionEvaluator.debug");
   private MonitoredVm vm;
   private HashMap map = new HashMap();

   ExpressionExecuter(MonitoredVm var1) {
      this.vm = var1;
   }

   public Object evaluate(Expression var1) {
      if (var1 == null) {
         return null;
      } else {
         if (debug) {
            System.out.println("Evaluating expression: " + var1);
         }

         if (var1 instanceof Literal) {
            return ((Literal)var1).getValue();
         } else if (var1 instanceof Identifier) {
            Identifier var9 = (Identifier)var1;
            if (this.map.containsKey(var9.getName())) {
               return this.map.get(var9.getName());
            } else {
               Monitor var10 = (Monitor)var9.getValue();
               Object var11 = var10.getValue();
               this.map.put(var9.getName(), var11);
               return var11;
            }
         } else {
            Expression var2 = var1.getLeft();
            Expression var3 = var1.getRight();
            Operator var4 = var1.getOperator();
            if (var4 == null) {
               return this.evaluate(var2);
            } else {
               Double var5 = new Double(((Number)this.evaluate(var2)).doubleValue());
               Double var6 = new Double(((Number)this.evaluate(var3)).doubleValue());
               double var7 = var4.eval(var5, var6);
               if (debug) {
                  System.out.println("Performed Operation: " + var5 + var4 + var6 + " = " + var7);
               }

               return new Double(var7);
            }
         }
      }
   }
}
