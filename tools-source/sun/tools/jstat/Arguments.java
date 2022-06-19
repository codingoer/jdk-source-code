package sun.tools.jstat;

import java.io.File;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import sun.jvmstat.monitor.VmIdentifier;

public class Arguments {
   private static final boolean debug = Boolean.getBoolean("jstat.debug");
   private static final boolean showUnsupported = Boolean.getBoolean("jstat.showUnsupported");
   private static final String JVMSTAT_USERDIR = ".jvmstat";
   private static final String OPTIONS_FILENAME = "jstat_options";
   private static final String UNSUPPORTED_OPTIONS_FILENAME = "jstat_unsupported_options";
   private static final String ALL_NAMES = "\\w*";
   private Comparator comparator;
   private int headerRate;
   private boolean help;
   private boolean list;
   private boolean options;
   private boolean constants;
   private boolean constantsOnly;
   private boolean strings;
   private boolean timestamp;
   private boolean snap;
   private boolean verbose;
   private String specialOption;
   private String names;
   private OptionFormat optionFormat;
   private int count = -1;
   private int interval = -1;
   private String vmIdString;
   private VmIdentifier vmId;

   public static void printUsage(PrintStream var0) {
      var0.println("Usage: jstat -help|-options");
      var0.println("       jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]");
      var0.println();
      var0.println("Definitions:");
      var0.println("  <option>      An option reported by the -options option");
      var0.println("  <vmid>        Virtual Machine Identifier. A vmid takes the following form:");
      var0.println("                     <lvmid>[@<hostname>[:<port>]]");
      var0.println("                Where <lvmid> is the local vm identifier for the target");
      var0.println("                Java virtual machine, typically a process id; <hostname> is");
      var0.println("                the name of the host running the target Java virtual machine;");
      var0.println("                and <port> is the port number for the rmiregistry on the");
      var0.println("                target host. See the jvmstat documentation for a more complete");
      var0.println("                description of the Virtual Machine Identifier.");
      var0.println("  <lines>       Number of samples between header lines.");
      var0.println("  <interval>    Sampling interval. The following forms are allowed:");
      var0.println("                    <n>[\"ms\"|\"s\"]");
      var0.println("                Where <n> is an integer and the suffix specifies the units as ");
      var0.println("                milliseconds(\"ms\") or seconds(\"s\"). The default units are \"ms\".");
      var0.println("  <count>       Number of samples to take before terminating.");
      var0.println("  -J<flag>      Pass <flag> directly to the runtime system.");
   }

   private static int toMillis(String var0) throws IllegalArgumentException {
      String[] var1 = new String[]{"ms", "s"};
      String var2 = null;
      String var3 = var0;

      int var4;
      for(var4 = 0; var4 < var1.length; ++var4) {
         int var5 = var0.indexOf(var1[var4]);
         if (var5 > 0) {
            var2 = var0.substring(var5);
            var3 = var0.substring(0, var5);
            break;
         }
      }

      try {
         var4 = Integer.parseInt(var3);
         if (var2 != null && var2.compareTo("ms") != 0) {
            if (var2.compareTo("s") == 0) {
               return var4 * 1000;
            } else {
               throw new IllegalArgumentException("Unknow time unit: " + var2);
            }
         } else {
            return var4;
         }
      } catch (NumberFormatException var6) {
         throw new IllegalArgumentException("Could not convert interval: " + var0);
      }
   }

   public Arguments(String[] var1) throws IllegalArgumentException {
      int var2 = 0;
      if (var1.length < 1) {
         throw new IllegalArgumentException("invalid argument count");
      } else if (var1[0].compareTo("-?") != 0 && var1[0].compareTo("-help") != 0) {
         if (var1[0].compareTo("-options") == 0) {
            this.options = true;
         } else {
            if (var1[0].compareTo("-list") == 0) {
               this.list = true;
               if (var1.length > 2) {
                  throw new IllegalArgumentException("invalid argument count");
               }

               ++var2;
            }

            for(; var2 < var1.length && var1[var2].startsWith("-"); ++var2) {
               String var3 = var1[var2];
               if (var3.compareTo("-a") == 0) {
                  this.comparator = new AscendingMonitorComparator();
               } else if (var3.compareTo("-d") == 0) {
                  this.comparator = new DescendingMonitorComparator();
               } else if (var3.compareTo("-t") == 0) {
                  this.timestamp = true;
               } else if (var3.compareTo("-v") == 0) {
                  this.verbose = true;
               } else if (var3.compareTo("-constants") != 0 && var3.compareTo("-c") != 0) {
                  if (var3.compareTo("-strings") != 0 && var3.compareTo("-s") != 0) {
                     String var4;
                     if (var3.startsWith("-h")) {
                        if (var3.compareTo("-h") != 0) {
                           var4 = var3.substring(2);
                        } else {
                           ++var2;
                           if (var2 >= var1.length) {
                              throw new IllegalArgumentException("-h requires an integer argument");
                           }

                           var4 = var1[var2];
                        }

                        try {
                           this.headerRate = Integer.parseInt(var4);
                        } catch (NumberFormatException var10) {
                           this.headerRate = -1;
                        }

                        if (this.headerRate < 0) {
                           throw new IllegalArgumentException("illegal -h argument: " + var4);
                        }
                     } else if (var3.startsWith("-name")) {
                        if (var3.startsWith("-name=")) {
                           this.names = var3.substring(7);
                        } else {
                           ++var2;
                           if (var2 >= var1.length) {
                              throw new IllegalArgumentException("option argument expected");
                           }

                           this.names = var1[var2];
                        }
                     } else {
                        var4 = null;
                        int var5 = var1[var2].indexOf(64);
                        if (var5 < 0) {
                           var4 = var1[var2];
                        } else {
                           var4 = var1[var2].substring(0, var5);
                        }

                        try {
                           int var6 = Integer.parseInt(var4);
                           break;
                        } catch (NumberFormatException var11) {
                           if (var2 == 0 && var1[var2].compareTo("-snap") == 0) {
                              this.snap = true;
                           } else {
                              if (var2 != 0) {
                                 throw new IllegalArgumentException("illegal argument: " + var1[var2]);
                              }

                              this.specialOption = var1[var2].substring(1);
                           }
                        }
                     }
                  } else {
                     this.strings = true;
                  }
               } else {
                  this.constants = true;
               }
            }

            if (this.specialOption == null && !this.list && !this.snap && this.names == null) {
               throw new IllegalArgumentException("-<option> required");
            } else {
               switch (var1.length - var2) {
                  case 0:
                     if (!this.list) {
                        throw new IllegalArgumentException("invalid argument count");
                     }
                     break;
                  case 1:
                     this.vmIdString = var1[var1.length - 1];
                     break;
                  case 2:
                     if (this.snap) {
                        throw new IllegalArgumentException("invalid argument count");
                     }

                     this.interval = toMillis(var1[var1.length - 1]);
                     this.vmIdString = var1[var1.length - 2];
                     break;
                  case 3:
                     if (this.snap) {
                        throw new IllegalArgumentException("invalid argument count");
                     }

                     try {
                        this.count = Integer.parseInt(var1[var1.length - 1]);
                     } catch (NumberFormatException var9) {
                        throw new IllegalArgumentException("illegal count value: " + var1[var1.length - 1]);
                     }

                     this.interval = toMillis(var1[var1.length - 2]);
                     this.vmIdString = var1[var1.length - 3];
                     break;
                  default:
                     throw new IllegalArgumentException("invalid argument count");
               }

               if (this.count == -1 && this.interval == -1) {
                  this.count = 1;
                  this.interval = 0;
               }

               if (this.comparator == null) {
                  this.comparator = new AscendingMonitorComparator();
               }

               this.names = this.names == null ? "\\w*" : this.names.replace(',', '|');

               try {
                  Pattern var12 = Pattern.compile(this.names);
               } catch (PatternSyntaxException var8) {
                  throw new IllegalArgumentException("Bad name pattern: " + var8.getMessage());
               }

               if (this.specialOption != null) {
                  OptionFinder var13 = new OptionFinder(this.optionsSources());
                  this.optionFormat = var13.getOptionFormat(this.specialOption, this.timestamp);
                  if (this.optionFormat == null) {
                     throw new IllegalArgumentException("Unknown option: -" + this.specialOption);
                  }
               }

               try {
                  this.vmId = new VmIdentifier(this.vmIdString);
               } catch (URISyntaxException var7) {
                  IllegalArgumentException var14 = new IllegalArgumentException("Malformed VM Identifier: " + this.vmIdString);
                  var14.initCause(var7);
                  throw var14;
               }
            }
         }
      } else {
         this.help = true;
      }
   }

   public Comparator comparator() {
      return this.comparator;
   }

   public boolean isHelp() {
      return this.help;
   }

   public boolean isList() {
      return this.list;
   }

   public boolean isSnap() {
      return this.snap;
   }

   public boolean isOptions() {
      return this.options;
   }

   public boolean isVerbose() {
      return this.verbose;
   }

   public boolean printConstants() {
      return this.constants;
   }

   public boolean isConstantsOnly() {
      return this.constantsOnly;
   }

   public boolean printStrings() {
      return this.strings;
   }

   public boolean showUnsupported() {
      return showUnsupported;
   }

   public int headerRate() {
      return this.headerRate;
   }

   public String counterNames() {
      return this.names;
   }

   public VmIdentifier vmId() {
      return this.vmId;
   }

   public String vmIdString() {
      return this.vmIdString;
   }

   public int sampleInterval() {
      return this.interval;
   }

   public int sampleCount() {
      return this.count;
   }

   public boolean isTimestamp() {
      return this.timestamp;
   }

   public boolean isSpecialOption() {
      return this.specialOption != null;
   }

   public String specialOption() {
      return this.specialOption;
   }

   public OptionFormat optionFormat() {
      return this.optionFormat;
   }

   public List optionsSources() {
      ArrayList var1 = new ArrayList();
      boolean var2 = false;
      String var3 = "jstat_options";

      try {
         String var4 = System.getProperty("user.home");
         String var5 = var4 + "/" + ".jvmstat";
         File var6 = new File(var5 + "/" + var3);
         var1.add(var6.toURI().toURL());
      } catch (Exception var7) {
         if (debug) {
            System.err.println(var7.getMessage());
            var7.printStackTrace();
         }

         throw new IllegalArgumentException("Internal Error: Bad URL: " + var7.getMessage());
      }

      URL var8 = this.getClass().getResource("resources/" + var3);

      assert var8 != null;

      var1.add(var8);
      if (showUnsupported) {
         var8 = this.getClass().getResource("resources/jstat_unsupported_options");

         assert var8 != null;

         var1.add(var8);
      }

      return var1;
   }
}
