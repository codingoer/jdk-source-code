package sun.tools.attach;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.AttachPermission;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.spi.AttachProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

public abstract class HotSpotAttachProvider extends AttachProvider {
   private static final String JVM_VERSION = "java.property.java.vm.version";

   public void checkAttachPermission() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new AttachPermission("attachVirtualMachine"));
      }

   }

   public List listVirtualMachines() {
      ArrayList var1 = new ArrayList();

      MonitoredHost var2;
      Set var3;
      try {
         var2 = MonitoredHost.getMonitoredHost(new HostIdentifier((String)null));
         var3 = var2.activeVms();
      } catch (Throwable var17) {
         Throwable var4 = var17;
         if (var17 instanceof ExceptionInInitializerError) {
            var4 = var17.getCause();
         }

         if (var4 instanceof ThreadDeath) {
            throw (ThreadDeath)var4;
         }

         if (var4 instanceof SecurityException) {
            return var1;
         }

         throw new InternalError(var4);
      }

      Iterator var20 = var3.iterator();

      while(var20.hasNext()) {
         Integer var5 = (Integer)var20.next();
         String var6 = var5.toString();
         String var7 = var6;
         boolean var8 = false;
         MonitoredVm var9 = null;

         try {
            var9 = var2.getMonitoredVm(new VmIdentifier(var6));

            try {
               var8 = MonitoredVmUtil.isAttachable(var9);
               var7 = MonitoredVmUtil.commandLine(var9);
            } catch (Exception var16) {
            }

            if (var8) {
               var1.add(new HotSpotVirtualMachineDescriptor(this, var6, var7));
            }
         } catch (Throwable var18) {
            if (var18 instanceof ThreadDeath) {
               throw (ThreadDeath)var18;
            }
         } finally {
            if (var9 != null) {
               var9.detach();
            }

         }
      }

      return var1;
   }

   void testAttachable(String var1) throws AttachNotSupportedException {
      MonitoredVm var2 = null;

      try {
         VmIdentifier var3 = new VmIdentifier(var1);
         MonitoredHost var10 = MonitoredHost.getMonitoredHost(var3);
         var2 = var10.getMonitoredVm(var3);
         if (!MonitoredVmUtil.isAttachable(var2)) {
            throw new AttachNotSupportedException("The VM does not support the attach mechanism");
         }

         return;
      } catch (Throwable var8) {
         if (var8 instanceof ThreadDeath) {
            ThreadDeath var4 = (ThreadDeath)var8;
            throw var4;
         }
      } finally {
         if (var2 != null) {
            var2.detach();
         }

      }

   }

   static class HotSpotVirtualMachineDescriptor extends VirtualMachineDescriptor {
      HotSpotVirtualMachineDescriptor(AttachProvider var1, String var2, String var3) {
         super(var1, var2, var3);
      }

      public boolean isAttachable() {
         return true;
      }
   }
}
