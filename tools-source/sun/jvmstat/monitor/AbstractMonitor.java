package sun.jvmstat.monitor;

public abstract class AbstractMonitor implements Monitor {
   protected String name;
   protected Units units;
   protected Variability variability;
   protected int vectorLength;
   protected boolean supported;

   protected AbstractMonitor(String var1, Units var2, Variability var3, boolean var4, int var5) {
      this.name = var1;
      this.units = var2;
      this.variability = var3;
      this.vectorLength = var5;
      this.supported = var4;
   }

   protected AbstractMonitor(String var1, Units var2, Variability var3, boolean var4) {
      this(var1, var2, var3, var4, 0);
   }

   public String getName() {
      return this.name;
   }

   public String getBaseName() {
      int var1 = this.name.lastIndexOf(".") + 1;
      return this.name.substring(var1);
   }

   public Units getUnits() {
      return this.units;
   }

   public Variability getVariability() {
      return this.variability;
   }

   public boolean isVector() {
      return this.vectorLength > 0;
   }

   public int getVectorLength() {
      return this.vectorLength;
   }

   public boolean isSupported() {
      return this.supported;
   }

   public abstract Object getValue();
}
