package sun.tools.jstack;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import java.io.InputStream;
import java.lang.reflect.Method;
import sun.tools.attach.HotSpotVirtualMachine;

public class JStack {
   public static void main(String[] var0) throws Exception {
      if (var0.length == 0) {
         usage(1);
      }

      boolean var1 = false;
      boolean var2 = false;
      boolean var3 = false;

      int var4;
      for(var4 = 0; var4 < var0.length; ++var4) {
         String var5 = var0[var4];
         if (!var5.startsWith("-")) {
            break;
         }

         if (!var5.equals("-help") && !var5.equals("-h")) {
            if (var5.equals("-F")) {
               var1 = true;
            } else if (var5.equals("-m")) {
               var2 = true;
            } else if (var5.equals("-l")) {
               var3 = true;
            } else {
               usage(1);
            }
         } else {
            usage(0);
         }
      }

      if (var2) {
         var1 = true;
      }

      int var8 = var0.length - var4;
      if (var8 == 0 || var8 > 2) {
         usage(1);
      }

      if (var8 == 2) {
         var1 = true;
      } else if (!var0[var4].matches("[0-9]+")) {
         var1 = true;
      }

      if (var1) {
         String[] var6 = new String[var8];

         for(int var7 = var4; var7 < var0.length; ++var7) {
            var6[var7 - var4] = var0[var7];
         }

         runJStackTool(var2, var3, var6);
      } else {
         String var9 = var0[var4];
         String[] var10;
         if (var3) {
            var10 = new String[]{"-l"};
         } else {
            var10 = new String[0];
         }

         runThreadDump(var9, var10);
      }

   }

   private static void runJStackTool(boolean var0, boolean var1, String[] var2) throws Exception {
      Class var3 = loadSAClass();
      if (var3 == null) {
         usage(1);
      }

      if (var0) {
         var2 = prepend("-m", var2);
      }

      if (var1) {
         var2 = prepend("-l", var2);
      }

      Class[] var4 = new Class[]{String[].class};
      Method var5 = var3.getDeclaredMethod("main", var4);
      Object[] var6 = new Object[]{var2};
      var5.invoke((Object)null, var6);
   }

   private static Class loadSAClass() {
      try {
         return Class.forName("sun.jvm.hotspot.tools.JStack", true, ClassLoader.getSystemClassLoader());
      } catch (Exception var1) {
         return null;
      }
   }

   private static void runThreadDump(String var0, String[] var1) throws Exception {
      VirtualMachine var2 = null;

      try {
         var2 = VirtualMachine.attach(var0);
      } catch (Exception var7) {
         String var4 = var7.getMessage();
         if (var4 != null) {
            System.err.println(var0 + ": " + var4);
         } else {
            var7.printStackTrace();
         }

         if (var7 instanceof AttachNotSupportedException && loadSAClass() != null) {
            System.err.println("The -F option can be used when the target process is not responding");
         }

         System.exit(1);
      }

      InputStream var3 = ((HotSpotVirtualMachine)var2).remoteDataDump((Object[])var1);
      byte[] var8 = new byte[256];

      int var5;
      do {
         var5 = var3.read(var8);
         if (var5 > 0) {
            String var6 = new String(var8, 0, var5, "UTF-8");
            System.out.print(var6);
         }
      } while(var5 > 0);

      var3.close();
      var2.detach();
   }

   private static String[] prepend(String var0, String[] var1) {
      String[] var2 = new String[var1.length + 1];
      var2[0] = var0;
      System.arraycopy(var1, 0, var2, 1, var1.length);
      return var2;
   }

   private static void usage(int var0) {
      System.err.println("Usage:");
      System.err.println("    jstack [-l] <pid>");
      System.err.println("        (to connect to running process)");
      if (loadSAClass() != null) {
         System.err.println("    jstack -F [-m] [-l] <pid>");
         System.err.println("        (to connect to a hung process)");
         System.err.println("    jstack [-m] [-l] <executable> <core>");
         System.err.println("        (to connect to a core file)");
         System.err.println("    jstack [-m] [-l] [server_id@]<remote server IP or hostname>");
         System.err.println("        (to connect to a remote debug server)");
      }

      System.err.println("");
      System.err.println("Options:");
      if (loadSAClass() != null) {
         System.err.println("    -F  to force a thread dump. Use when jstack <pid> does not respond (process is hung)");
         System.err.println("    -m  to print both java and native frames (mixed mode)");
      }

      System.err.println("    -l  long listing. Prints additional information about locks");
      System.err.println("    -h or -help to print this help message");
      System.exit(var0);
   }
}
