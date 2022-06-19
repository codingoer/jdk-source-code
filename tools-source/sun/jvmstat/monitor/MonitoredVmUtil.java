package sun.jvmstat.monitor;

public class MonitoredVmUtil {
   private static int IS_ATTACHABLE = 0;
   private static int IS_KERNEL_VM = 1;

   private MonitoredVmUtil() {
   }

   public static String vmVersion(MonitoredVm var0) throws MonitorException {
      StringMonitor var1 = (StringMonitor)var0.findByName("java.property.java.vm.version");
      return var1 == null ? "Unknown" : var1.stringValue();
   }

   public static String commandLine(MonitoredVm var0) throws MonitorException {
      StringMonitor var1 = (StringMonitor)var0.findByName("sun.rt.javaCommand");
      return var1 == null ? "Unknown" : var1.stringValue();
   }

   public static String mainArgs(MonitoredVm var0) throws MonitorException {
      String var1 = commandLine(var0);
      int var2 = var1.indexOf(32);
      if (var2 > 0) {
         return var1.substring(var2 + 1);
      } else {
         return var1.compareTo("Unknown") == 0 ? var1 : null;
      }
   }

   public static String mainClass(MonitoredVm var0, boolean var1) throws MonitorException {
      String var2 = commandLine(var0);
      String var3 = var2;
      int var4 = var2.indexOf(32);
      if (var4 > 0) {
         var3 = var2.substring(0, var4);
      }

      if (!var1) {
         int var5 = var3.lastIndexOf(47);
         if (var5 > 0) {
            return var3.substring(var5 + 1);
         }

         var5 = var3.lastIndexOf(92);
         if (var5 > 0) {
            return var3.substring(var5 + 1);
         }

         int var6 = var3.lastIndexOf(46);
         if (var6 > 0) {
            return var3.substring(var6 + 1);
         }
      }

      return var3;
   }

   public static String jvmArgs(MonitoredVm var0) throws MonitorException {
      StringMonitor var1 = (StringMonitor)var0.findByName("java.rt.vmArgs");
      return var1 == null ? "Unknown" : var1.stringValue();
   }

   public static String jvmFlags(MonitoredVm var0) throws MonitorException {
      StringMonitor var1 = (StringMonitor)var0.findByName("java.rt.vmFlags");
      return var1 == null ? "Unknown" : var1.stringValue();
   }

   public static boolean isAttachable(MonitoredVm var0) throws MonitorException {
      StringMonitor var1 = (StringMonitor)var0.findByName("sun.rt.jvmCapabilities");
      if (var1 == null) {
         return false;
      } else {
         return var1.stringValue().charAt(IS_ATTACHABLE) == '1';
      }
   }
}
