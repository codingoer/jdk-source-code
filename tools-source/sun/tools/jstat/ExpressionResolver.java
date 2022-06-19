package sun.tools.jstat;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.Variability;

public class ExpressionResolver implements ExpressionEvaluator {
   private static boolean debug = Boolean.getBoolean("ExpressionResolver.debug");
   private MonitoredVm vm;

   ExpressionResolver(MonitoredVm var1) {
      this.vm = var1;
   }

   public Object evaluate(Expression var1) throws MonitorException {
      if (var1 == null) {
         return null;
      } else {
         if (debug) {
            System.out.println("Resolving Expression:" + var1);
         }

         if (var1 instanceof Identifier) {
            Identifier var12 = (Identifier)var1;
            if (var12.isResolved()) {
               return var12;
            } else {
               Monitor var13 = this.vm.findByName(var12.getName());
               if (var13 == null) {
                  System.err.println("Warning: Unresolved Symbol: " + var12.getName() + " substituted NaN");
                  return new Literal(new Double(Double.NaN));
               } else if (var13.getVariability() == Variability.CONSTANT) {
                  if (debug) {
                     System.out.println("Converting constant " + var12.getName() + " to literal with value " + var13.getValue());
                  }

                  return new Literal(var13.getValue());
               } else {
                  var12.setValue(var13);
                  return var12;
               }
            }
         } else if (var1 instanceof Literal) {
            return var1;
         } else {
            Expression var2 = null;
            Expression var3 = null;
            if (var1.getLeft() != null) {
               var2 = (Expression)this.evaluate(var1.getLeft());
            }

            if (var1.getRight() != null) {
               var3 = (Expression)this.evaluate(var1.getRight());
            }

            if (var2 != null && var3 != null && var2 instanceof Literal && var3 instanceof Literal) {
               Literal var4 = (Literal)var2;
               Literal var5 = (Literal)var3;
               boolean var6 = false;
               Double var7 = new Double(Double.NaN);
               if (var4.getValue() instanceof String) {
                  var6 = true;
                  var4.setValue(var7);
               }

               if (var5.getValue() instanceof String) {
                  var6 = true;
                  var5.setValue(var7);
               }

               if (debug && var6) {
                  System.out.println("Warning: String literal in numerical expression: substitutied NaN");
               }

               Number var8 = (Number)var4.getValue();
               Number var9 = (Number)var5.getValue();
               double var10 = var1.getOperator().eval(var8.doubleValue(), var9.doubleValue());
               if (debug) {
                  System.out.println("Converting expression " + var1 + " (left = " + var8.doubleValue() + ") (right = " + var9.doubleValue() + ") to literal value " + var10);
               }

               return new Literal(new Double(var10));
            } else if (var2 != null && var3 == null) {
               return var2;
            } else {
               var1.setLeft(var2);
               var1.setRight(var3);
               return var1;
            }
         }
      }
   }
}
