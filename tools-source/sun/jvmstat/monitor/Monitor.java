package sun.jvmstat.monitor;

public interface Monitor {
   String getName();

   String getBaseName();

   Units getUnits();

   Variability getVariability();

   boolean isVector();

   int getVectorLength();

   boolean isSupported();

   Object getValue();
}
