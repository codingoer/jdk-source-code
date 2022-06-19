package sun.tools.jmap;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import sun.tools.attach.HotSpotVirtualMachine;

public class JMap {
   private static String HISTO_OPTION = "-histo";
   private static String LIVE_HISTO_OPTION = "-histo:live";
   private static String DUMP_OPTION_PREFIX = "-dump:";
   private static String SA_TOOL_OPTIONS = "-heap|-heap:format=b|-clstats|-finalizerinfo";
   private static String FORCE_SA_OPTION = "-F";
   private static String DEFAULT_OPTION = "-pmap";
   private static final String LIVE_OBJECTS_OPTION = "-live";
   private static final String ALL_OBJECTS_OPTION = "-all";

   public static void main(String[] var0) throws Exception {
      if (var0.length == 0) {
         usage(1);
      }

      boolean var1 = false;
      String var2 = null;

      int var3;
      for(var3 = 0; var3 < var0.length; ++var3) {
         String var4 = var0[var3];
         if (!var4.startsWith("-")) {
            break;
         }

         if (!var4.equals("-help") && !var4.equals("-h")) {
            if (var4.equals(FORCE_SA_OPTION)) {
               var1 = true;
            } else {
               if (var2 != null) {
                  usage(1);
               }

               var2 = var4;
            }
         } else {
            usage(0);
         }
      }

      if (var2 == null) {
         var2 = DEFAULT_OPTION;
      }

      if (var2.matches(SA_TOOL_OPTIONS)) {
         var1 = true;
      }

      int var7 = var0.length - var3;
      if (var7 == 0 || var7 > 2) {
         usage(1);
      }

      if (var3 != 0 && var7 == 1) {
         if (!var0[var3].matches("[0-9]+")) {
            var1 = true;
         }
      } else {
         var1 = true;
      }

      if (var1) {
         String[] var5 = new String[var7];

         for(int var6 = var3; var6 < var0.length; ++var6) {
            var5[var6 - var3] = var0[var6];
         }

         runTool(var2, var5);
      } else {
         String var8 = var0[1];
         if (var2.equals(HISTO_OPTION)) {
            histo(var8, false);
         } else if (var2.equals(LIVE_HISTO_OPTION)) {
            histo(var8, true);
         } else if (var2.startsWith(DUMP_OPTION_PREFIX)) {
            dump(var8, var2);
         } else {
            usage(1);
         }
      }

   }

   private static void runTool(String var0, String[] var1) throws Exception {
      String[][] var2 = new String[][]{{"-pmap", "sun.jvm.hotspot.tools.PMap"}, {"-heap", "sun.jvm.hotspot.tools.HeapSummary"}, {"-heap:format=b", "sun.jvm.hotspot.tools.HeapDumper"}, {"-histo", "sun.jvm.hotspot.tools.ObjectHistogram"}, {"-clstats", "sun.jvm.hotspot.tools.ClassLoaderStats"}, {"-finalizerinfo", "sun.jvm.hotspot.tools.FinalizerInfo"}};
      String var3 = null;
      if (var0.startsWith(DUMP_OPTION_PREFIX)) {
         String var4 = parseDumpOptions(var0);
         if (var4 == null) {
            usage(1);
         }

         var3 = "sun.jvm.hotspot.tools.HeapDumper";
         var1 = prepend(var4, var1);
         var1 = prepend("-f", var1);
      } else {
         for(int var8 = 0; var8 < var2.length; ++var8) {
            if (var0.equals(var2[var8][0])) {
               var3 = var2[var8][1];
               break;
            }
         }
      }

      if (var3 == null) {
         usage(1);
      }

      Class var9 = loadClass(var3);
      if (var9 == null) {
         usage(1);
      }

      Class[] var5 = new Class[]{String[].class};
      Method var6 = var9.getDeclaredMethod("main", var5);
      Object[] var7 = new Object[]{var1};
      var6.invoke((Object)null, var7);
   }

   private static Class loadClass(String var0) {
      try {
         return Class.forName(var0, true, ClassLoader.getSystemClassLoader());
      } catch (Exception var2) {
         return null;
      }
   }

   private static void histo(String var0, boolean var1) throws IOException {
      VirtualMachine var2 = attach(var0);
      InputStream var3 = ((HotSpotVirtualMachine)var2).heapHisto(var1 ? "-live" : "-all");
      drain(var2, var3);
   }

   private static void dump(String var0, String var1) throws IOException {
      String var2 = parseDumpOptions(var1);
      if (var2 == null) {
         usage(1);
      }

      var2 = (new File(var2)).getCanonicalPath();
      boolean var3 = isDumpLiveObjects(var1);
      VirtualMachine var4 = attach(var0);
      System.out.println("Dumping heap to " + var2 + " ...");
      InputStream var5 = ((HotSpotVirtualMachine)var4).dumpHeap(var2, var3 ? "-live" : "-all");
      drain(var4, var5);
   }

   private static String parseDumpOptions(String var0) {
      assert var0.startsWith(DUMP_OPTION_PREFIX);

      String var1 = null;
      String[] var2 = var0.substring(DUMP_OPTION_PREFIX.length()).split(",");

      for(int var3 = 0; var3 < var2.length; ++var3) {
         String var4 = var2[var3];
         if (!var4.equals("format=b") && !var4.equals("live")) {
            if (!var4.startsWith("file=")) {
               return null;
            }

            var1 = var4.substring(5);
            if (var1.length() == 0) {
               return null;
            }
         }
      }

      return var1;
   }

   private static boolean isDumpLiveObjects(String var0) {
      String[] var1 = var0.substring(DUMP_OPTION_PREFIX.length()).split(",");
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (var5.equals("live")) {
            return true;
         }
      }

      return false;
   }

   private static VirtualMachine attach(String var0) {
      try {
         return VirtualMachine.attach(var0);
      } catch (Exception var3) {
         String var2 = var3.getMessage();
         if (var2 != null) {
            System.err.println(var0 + ": " + var2);
         } else {
            var3.printStackTrace();
         }

         if (var3 instanceof AttachNotSupportedException && haveSA()) {
            System.err.println("The -F option can be used when the target process is not responding");
         }

         System.exit(1);
         return null;
      }
   }

   private static void drain(VirtualMachine var0, InputStream var1) throws IOException {
      byte[] var2 = new byte[256];

      int var3;
      do {
         var3 = var1.read(var2);
         if (var3 > 0) {
            String var4 = new String(var2, 0, var3, "UTF-8");
            System.out.print(var4);
         }
      } while(var3 > 0);

      var1.close();
      var0.detach();
   }

   private static String[] prepend(String var0, String[] var1) {
      String[] var2 = new String[var1.length + 1];
      var2[0] = var0;
      System.arraycopy(var1, 0, var2, 1, var1.length);
      return var2;
   }

   private static boolean haveSA() {
      Class var0 = loadClass("sun.jvm.hotspot.tools.HeapSummary");
      return var0 != null;
   }

   private static void usage(int var0) {
      System.err.println("Usage:");
      if (haveSA()) {
         System.err.println("    jmap [option] <pid>");
         System.err.println("        (to connect to running process)");
         System.err.println("    jmap [option] <executable <core>");
         System.err.println("        (to connect to a core file)");
         System.err.println("    jmap [option] [server_id@]<remote server IP or hostname>");
         System.err.println("        (to connect to remote debug server)");
         System.err.println("");
         System.err.println("where <option> is one of:");
         System.err.println("    <none>               to print same info as Solaris pmap");
         System.err.println("    -heap                to print java heap summary");
         System.err.println("    -histo[:live]        to print histogram of java object heap; if the \"live\"");
         System.err.println("                         suboption is specified, only count live objects");
         System.err.println("    -clstats             to print class loader statistics");
         System.err.println("    -finalizerinfo       to print information on objects awaiting finalization");
         System.err.println("    -dump:<dump-options> to dump java heap in hprof binary format");
         System.err.println("                         dump-options:");
         System.err.println("                           live         dump only live objects; if not specified,");
         System.err.println("                                        all objects in the heap are dumped.");
         System.err.println("                           format=b     binary format");
         System.err.println("                           file=<file>  dump heap to <file>");
         System.err.println("                         Example: jmap -dump:live,format=b,file=heap.bin <pid>");
         System.err.println("    -F                   force. Use with -dump:<dump-options> <pid> or -histo");
         System.err.println("                         to force a heap dump or histogram when <pid> does not");
         System.err.println("                         respond. The \"live\" suboption is not supported");
         System.err.println("                         in this mode.");
         System.err.println("    -h | -help           to print this help message");
         System.err.println("    -J<flag>             to pass <flag> directly to the runtime system");
      } else {
         System.err.println("    jmap -histo <pid>");
         System.err.println("      (to connect to running process and print histogram of java object heap");
         System.err.println("    jmap -dump:<dump-options> <pid>");
         System.err.println("      (to connect to running process and dump java heap)");
         System.err.println("");
         System.err.println("    dump-options:");
         System.err.println("      format=b     binary default");
         System.err.println("      file=<file>  dump heap to <file>");
         System.err.println("");
         System.err.println("    Example:       jmap -dump:format=b,file=heap.bin <pid>");
      }

      System.exit(var0);
   }
}
