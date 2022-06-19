package sun.tools.jstat;

import java.util.HashMap;
import java.util.Set;

public class Scale {
   private static int nextOrdinal = 0;
   private static HashMap map = new HashMap();
   private final String name;
   private final int ordinal;
   private final double factor;
   public static final Scale RAW = new Scale("raw", 1.0);
   public static final Scale PERCENT = new Scale("percent", 0.0);
   public static final Scale KILO = new Scale("K", 1024.0);
   public static final Scale MEGA = new Scale("M", 1048576.0);
   public static final Scale GIGA = new Scale("G", 1.073741824E9);
   public static final Scale TERA = new Scale("T", 0.0);
   public static final Scale PETA = new Scale("P", 0.0);
   public static final Scale PICO = new Scale("p", 1.0E-11);
   public static final Scale NANO = new Scale("n", 1.0E-8);
   public static final Scale MICRO = new Scale("u", 1.0E-5);
   public static final Scale MILLI = new Scale("m", 0.01);
   public static final Scale PSEC = new Scale("ps", 1.0E-11);
   public static final Scale NSEC = new Scale("ns", 1.0E-8);
   public static final Scale USEC = new Scale("us", 1.0E-5);
   public static final Scale MSEC = new Scale("ms", 0.01);
   public static final Scale SEC = new Scale("s", 1.0);
   public static final Scale SEC2 = new Scale("sec", 1.0);
   public static final Scale MINUTES = new Scale("min", 0.016666666666666666);
   public static final Scale HOUR = new Scale("h", 2.777777777777778E-4);
   public static final Scale HOUR2 = new Scale("hour", 2.777777777777778E-4);

   private Scale(String var1, double var2) {
      this.ordinal = nextOrdinal++;
      this.name = var1;
      this.factor = var2;

      assert !map.containsKey(var1);

      map.put(var1, this);
   }

   public double getFactor() {
      return this.factor;
   }

   public String toString() {
      return this.name;
   }

   public static Scale toScale(String var0) {
      return (Scale)map.get(var0);
   }

   protected static Set keySet() {
      return map.keySet();
   }

   protected double scale(double var1) {
      return var1 / this.factor;
   }
}
