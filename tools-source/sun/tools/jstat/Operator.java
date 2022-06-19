package sun.tools.jstat;

import java.util.HashMap;
import java.util.Set;

public abstract class Operator {
   private static int nextOrdinal = 0;
   private static HashMap map = new HashMap();
   private final String name;
   private final int ordinal;
   public static final Operator PLUS = new Operator("+") {
      protected double eval(double var1, double var3) {
         return var1 + var3;
      }
   };
   public static final Operator MINUS = new Operator("-") {
      protected double eval(double var1, double var3) {
         return var1 - var3;
      }
   };
   public static final Operator DIVIDE = new Operator("/") {
      protected double eval(double var1, double var3) {
         return var3 == 0.0 ? Double.NaN : var1 / var3;
      }
   };
   public static final Operator MULTIPLY = new Operator("*") {
      protected double eval(double var1, double var3) {
         return var1 * var3;
      }
   };

   private Operator(String var1) {
      this.ordinal = nextOrdinal++;
      this.name = var1;
      map.put(var1, this);
   }

   protected abstract double eval(double var1, double var3);

   public String toString() {
      return this.name;
   }

   public static Operator toOperator(String var0) {
      return (Operator)map.get(var0);
   }

   protected static Set keySet() {
      return map.keySet();
   }

   // $FF: synthetic method
   Operator(String var1, Object var2) {
      this(var1);
   }
}
