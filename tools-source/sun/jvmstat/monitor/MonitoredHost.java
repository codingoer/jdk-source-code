package sun.jvmstat.monitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import sun.jvmstat.monitor.event.HostListener;

public abstract class MonitoredHost {
   private static Map monitoredHosts = new HashMap();
   private static final String IMPL_OVERRIDE_PROP_NAME = "sun.jvmstat.monitor.MonitoredHost";
   private static final String IMPL_PKG_PROP_NAME = "sun.jvmstat.monitor.package";
   private static final String IMPL_PACKAGE = System.getProperty("sun.jvmstat.monitor.package", "sun.jvmstat.perfdata");
   private static final String LOCAL_PROTOCOL_PROP_NAME = "sun.jvmstat.monitor.local";
   private static final String LOCAL_PROTOCOL = System.getProperty("sun.jvmstat.monitor.local", "local");
   private static final String REMOTE_PROTOCOL_PROP_NAME = "sun.jvmstat.monitor.remote";
   private static final String REMOTE_PROTOCOL = System.getProperty("sun.jvmstat.monitor.remote", "rmi");
   private static final String MONITORED_HOST_CLASS = "MonitoredHostProvider";
   protected HostIdentifier hostId;
   protected int interval;
   protected Exception lastException;

   public static MonitoredHost getMonitoredHost(String var0) throws MonitorException, URISyntaxException {
      HostIdentifier var1 = new HostIdentifier(var0);
      return getMonitoredHost(var1);
   }

   public static MonitoredHost getMonitoredHost(VmIdentifier var0) throws MonitorException {
      HostIdentifier var1 = new HostIdentifier(var0);
      return getMonitoredHost(var1);
   }

   public static MonitoredHost getMonitoredHost(HostIdentifier var0) throws MonitorException {
      String var1 = System.getProperty("sun.jvmstat.monitor.MonitoredHost");
      MonitoredHost var2 = null;
      synchronized(monitoredHosts) {
         var2 = (MonitoredHost)monitoredHosts.get(var0);
         if (var2 != null) {
            if (!var2.isErrored()) {
               return var2;
            }

            monitoredHosts.remove(var0);
         }
      }

      var0 = resolveHostId(var0);
      if (var1 == null) {
         var1 = IMPL_PACKAGE + ".monitor.protocol." + var0.getScheme() + "." + "MonitoredHostProvider";
      }

      try {
         Class var3 = Class.forName(var1);
         Constructor var14 = var3.getConstructor(var0.getClass());
         var2 = (MonitoredHost)var14.newInstance(var0);
         synchronized(monitoredHosts) {
            monitoredHosts.put(var2.hostId, var2);
         }

         return var2;
      } catch (ClassNotFoundException var8) {
         throw new IllegalArgumentException("Could not find " + var1 + ": " + var8.getMessage(), var8);
      } catch (NoSuchMethodException var9) {
         throw new IllegalArgumentException("Expected constructor missing in " + var1 + ": " + var9.getMessage(), var9);
      } catch (IllegalAccessException var10) {
         throw new IllegalArgumentException("Unexpected constructor access in " + var1 + ": " + var10.getMessage(), var10);
      } catch (InstantiationException var11) {
         throw new IllegalArgumentException(var1 + "is abstract: " + var11.getMessage(), var11);
      } catch (InvocationTargetException var12) {
         Throwable var4 = var12.getCause();
         if (var4 instanceof MonitorException) {
            throw (MonitorException)var4;
         } else {
            throw new RuntimeException("Unexpected exception", var12);
         }
      }
   }

   protected static HostIdentifier resolveHostId(HostIdentifier var0) throws MonitorException {
      String var1 = var0.getHost();
      String var2 = var0.getScheme();
      StringBuffer var3 = new StringBuffer();

      assert var1 != null;

      if (var2 == null) {
         if (var1.compareTo("localhost") == 0) {
            var2 = LOCAL_PROTOCOL;
         } else {
            var2 = REMOTE_PROTOCOL;
         }
      }

      var3.append(var2).append(":").append(var0.getSchemeSpecificPart());
      String var4 = var0.getFragment();
      if (var4 != null) {
         var3.append("#").append(var4);
      }

      try {
         return new HostIdentifier(var3.toString());
      } catch (URISyntaxException var6) {
         assert false;

         throw new IllegalArgumentException("Malformed URI created: " + var3.toString());
      }
   }

   public HostIdentifier getHostIdentifier() {
      return this.hostId;
   }

   public void setInterval(int var1) {
      this.interval = var1;
   }

   public int getInterval() {
      return this.interval;
   }

   public void setLastException(Exception var1) {
      this.lastException = var1;
   }

   public Exception getLastException() {
      return this.lastException;
   }

   public void clearLastException() {
      this.lastException = null;
   }

   public boolean isErrored() {
      return this.lastException != null;
   }

   public abstract MonitoredVm getMonitoredVm(VmIdentifier var1) throws MonitorException;

   public abstract MonitoredVm getMonitoredVm(VmIdentifier var1, int var2) throws MonitorException;

   public abstract void detach(MonitoredVm var1) throws MonitorException;

   public abstract void addHostListener(HostListener var1) throws MonitorException;

   public abstract void removeHostListener(HostListener var1) throws MonitorException;

   public abstract Set activeVms() throws MonitorException;
}
