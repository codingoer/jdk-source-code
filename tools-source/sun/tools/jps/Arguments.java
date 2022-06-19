package sun.tools.jps;

import java.io.PrintStream;
import java.net.URISyntaxException;
import sun.jvmstat.monitor.HostIdentifier;

public class Arguments {
   private static final boolean debug = Boolean.getBoolean("jps.debug");
   private static final boolean printStackTrace = Boolean.getBoolean("jps.printStackTrace");
   private boolean help;
   private boolean quiet;
   private boolean longPaths;
   private boolean vmArgs;
   private boolean vmFlags;
   private boolean mainArgs;
   private String hostname;
   private HostIdentifier hostId;

   public static void printUsage(PrintStream var0) {
      var0.println("usage: jps [-help]");
      var0.println("       jps [-q] [-mlvV] [<hostid>]");
      var0.println();
      var0.println("Definitions:");
      var0.println("    <hostid>:      <hostname>[:<port>]");
   }

   public Arguments(String[] var1) throws IllegalArgumentException {
      boolean var2 = false;
      if (var1.length != 1 || var1[0].compareTo("-?") != 0 && var1[0].compareTo("-help") != 0) {
         int var6;
         for(var6 = 0; var6 < var1.length && var1[var6].startsWith("-"); ++var6) {
            String var3 = var1[var6];
            if (var3.compareTo("-q") == 0) {
               this.quiet = true;
            } else {
               if (!var3.startsWith("-")) {
                  throw new IllegalArgumentException("illegal argument: " + var1[var6]);
               }

               for(int var4 = 1; var4 < var3.length(); ++var4) {
                  switch (var3.charAt(var4)) {
                     case 'V':
                        this.vmFlags = true;
                        break;
                     case 'l':
                        this.longPaths = true;
                        break;
                     case 'm':
                        this.mainArgs = true;
                        break;
                     case 'v':
                        this.vmArgs = true;
                        break;
                     default:
                        throw new IllegalArgumentException("illegal argument: " + var1[var6]);
                  }
               }
            }
         }

         switch (var1.length - var6) {
            case 0:
               this.hostname = null;
               break;
            case 1:
               this.hostname = var1[var1.length - 1];
               break;
            default:
               throw new IllegalArgumentException("invalid argument count");
         }

         try {
            this.hostId = new HostIdentifier(this.hostname);
         } catch (URISyntaxException var5) {
            IllegalArgumentException var7 = new IllegalArgumentException("Malformed Host Identifier: " + this.hostname);
            var7.initCause(var5);
            throw var7;
         }
      } else {
         this.help = true;
      }
   }

   public boolean isDebug() {
      return debug;
   }

   public boolean printStackTrace() {
      return printStackTrace;
   }

   public boolean isHelp() {
      return this.help;
   }

   public boolean isQuiet() {
      return this.quiet;
   }

   public boolean showLongPaths() {
      return this.longPaths;
   }

   public boolean showVmArgs() {
      return this.vmArgs;
   }

   public boolean showVmFlags() {
      return this.vmFlags;
   }

   public boolean showMainArgs() {
      return this.mainArgs;
   }

   public String hostname() {
      return this.hostname;
   }

   public HostIdentifier hostId() {
      return this.hostId;
   }
}
