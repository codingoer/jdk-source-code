package sun.tools.jcmd;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.AttachOperationFailedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.tools.attach.HotSpotVirtualMachine;
import sun.tools.jstat.JStatLogger;

public class JCmd {
   public static void main(String[] var0) {
      Arguments var1 = null;

      try {
         var1 = new Arguments(var0);
      } catch (IllegalArgumentException var10) {
         System.err.println("Error parsing arguments: " + var10.getMessage() + "\n");
         Arguments.usage();
         System.exit(1);
      }

      if (var1.isShowUsage()) {
         Arguments.usage();
         System.exit(1);
      }

      if (var1.isListProcesses()) {
         List var2 = VirtualMachine.list();
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            VirtualMachineDescriptor var4 = (VirtualMachineDescriptor)var3.next();
            System.out.println(var4.id() + " " + var4.displayName());
         }

         System.exit(0);
      }

      ArrayList var12 = new ArrayList();
      VirtualMachineDescriptor var5;
      List var13;
      Iterator var15;
      if (var1.getPid() == 0) {
         var13 = VirtualMachine.list();
         var15 = var13.iterator();

         while(var15.hasNext()) {
            var5 = (VirtualMachineDescriptor)var15.next();
            if (!isJCmdProcess(var5)) {
               var12.add(var5.id());
            }
         }
      } else if (var1.getProcessSubstring() != null) {
         var13 = VirtualMachine.list();
         var15 = var13.iterator();

         label94:
         while(true) {
            do {
               if (!var15.hasNext()) {
                  if (var12.isEmpty()) {
                     System.err.println("Could not find any processes matching : '" + var1.getProcessSubstring() + "'");
                     System.exit(1);
                  }
                  break label94;
               }

               var5 = (VirtualMachineDescriptor)var15.next();
            } while(isJCmdProcess(var5));

            try {
               String var6 = getMainClass(var5);
               if (var6 != null && var6.indexOf(var1.getProcessSubstring()) != -1) {
                  var12.add(var5.id());
               }
            } catch (URISyntaxException | MonitorException var11) {
               if (var11.getMessage() != null) {
                  System.err.println(var11.getMessage());
               } else {
                  Throwable var7 = var11.getCause();
                  if (var7 != null && var7.getMessage() != null) {
                     System.err.println(var7.getMessage());
                  } else {
                     var11.printStackTrace();
                  }
               }
            }
         }
      } else if (var1.getPid() == -1) {
         System.err.println("Invalid pid specified");
         System.exit(1);
      } else {
         var12.add(var1.getPid() + "");
      }

      boolean var14 = true;
      var15 = var12.iterator();

      while(var15.hasNext()) {
         String var16 = (String)var15.next();
         System.out.println(var16 + ":");
         if (var1.isListCounters()) {
            listCounters(var16);
         } else {
            try {
               executeCommandForPid(var16, var1.getCommand());
            } catch (AttachOperationFailedException var8) {
               System.err.println(var8.getMessage());
               var14 = false;
            } catch (Exception var9) {
               var9.printStackTrace();
               var14 = false;
            }
         }
      }

      System.exit(var14 ? 0 : 1);
   }

   private static void executeCommandForPid(String var0, String var1) throws AttachNotSupportedException, IOException, UnsupportedEncodingException {
      VirtualMachine var2 = VirtualMachine.attach(var0);
      HotSpotVirtualMachine var3 = (HotSpotVirtualMachine)var2;
      String[] var4 = var1.split("\\n");
      String[] var5 = var4;
      int var6 = var4.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = var5[var7];
         if (var8.trim().equals("stop")) {
            break;
         }

         InputStream var9 = var3.executeJCmd(var8);
         Throwable var10 = null;

         try {
            byte[] var11 = new byte[256];
            boolean var13 = false;

            int var12;
            do {
               var12 = var9.read(var11);
               if (var12 > 0) {
                  String var14 = new String(var11, 0, var12, "UTF-8");
                  System.out.print(var14);
                  var13 = true;
               }
            } while(var12 > 0);

            if (!var13) {
               System.out.println("Command executed successfully");
            }
         } catch (Throwable var22) {
            var10 = var22;
            throw var22;
         } finally {
            if (var9 != null) {
               if (var10 != null) {
                  try {
                     var9.close();
                  } catch (Throwable var21) {
                     var10.addSuppressed(var21);
                  }
               } else {
                  var9.close();
               }
            }

         }
      }

      var2.detach();
   }

   private static void listCounters(String var0) {
      VmIdentifier var1 = null;

      try {
         var1 = new VmIdentifier(var0);
      } catch (URISyntaxException var6) {
         System.err.println("Malformed VM Identifier: " + var0);
         return;
      }

      try {
         MonitoredHost var2 = MonitoredHost.getMonitoredHost(var1);
         MonitoredVm var3 = var2.getMonitoredVm(var1, -1);
         JStatLogger var4 = new JStatLogger(var3);
         var4.printSnapShot("\\w*", new AscendingMonitorComparator(), false, true, System.out);
         var2.detach(var3);
      } catch (MonitorException var5) {
         var5.printStackTrace();
      }

   }

   private static boolean isJCmdProcess(VirtualMachineDescriptor var0) {
      try {
         String var1 = getMainClass(var0);
         return var1 != null && var1.equals(JCmd.class.getName());
      } catch (MonitorException | URISyntaxException var2) {
         return false;
      }
   }

   private static String getMainClass(VirtualMachineDescriptor var0) throws URISyntaxException, MonitorException {
      try {
         String var1 = null;
         VmIdentifier var2 = new VmIdentifier(var0.id());
         MonitoredHost var3 = MonitoredHost.getMonitoredHost(var2);
         MonitoredVm var4 = var3.getMonitoredVm(var2, -1);
         var1 = MonitoredVmUtil.mainClass(var4, true);
         var3.detach(var4);
         return var1;
      } catch (NullPointerException var5) {
         return null;
      }
   }

   static class AscendingMonitorComparator implements Comparator {
      public int compare(Monitor var1, Monitor var2) {
         String var3 = var1.getName();
         String var4 = var2.getName();
         return var3.compareTo(var4);
      }
   }
}
