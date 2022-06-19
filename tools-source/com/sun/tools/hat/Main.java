package com.sun.tools.hat;

import com.sun.tools.hat.internal.model.ReachableExcludesImpl;
import com.sun.tools.hat.internal.model.Snapshot;
import com.sun.tools.hat.internal.parser.Reader;
import com.sun.tools.hat.internal.server.QueryListener;
import java.io.File;
import java.io.IOException;

public class Main {
   private static String VERSION_STRING = "jhat version 2.0";

   private static void usage(String var0) {
      if (var0 != null) {
         System.err.println("ERROR: " + var0);
      }

      System.err.println("Usage:  jhat [-stack <bool>] [-refs <bool>] [-port <port>] [-baseline <file>] [-debug <int>] [-version] [-h|-help] <file>");
      System.err.println();
      System.err.println("\t-J<flag>          Pass <flag> directly to the runtime system. For");
      System.err.println("\t\t\t  example, -J-mx512m to use a maximum heap size of 512MB");
      System.err.println("\t-stack false:     Turn off tracking object allocation call stack.");
      System.err.println("\t-refs false:      Turn off tracking of references to objects");
      System.err.println("\t-port <port>:     Set the port for the HTTP server.  Defaults to 7000");
      System.err.println("\t-exclude <file>:  Specify a file that lists data members that should");
      System.err.println("\t\t\t  be excluded from the reachableFrom query.");
      System.err.println("\t-baseline <file>: Specify a baseline object dump.  Objects in");
      System.err.println("\t\t\t  both heap dumps with the same ID and same class will");
      System.err.println("\t\t\t  be marked as not being \"new\".");
      System.err.println("\t-debug <int>:     Set debug level.");
      System.err.println("\t\t\t    0:  No debug output");
      System.err.println("\t\t\t    1:  Debug hprof file parsing");
      System.err.println("\t\t\t    2:  Debug hprof file parsing, no server");
      System.err.println("\t-version          Report version number");
      System.err.println("\t-h|-help          Print this help and exit");
      System.err.println("\t<file>            The file to read");
      System.err.println();
      System.err.println("For a dump file that contains multiple heap dumps,");
      System.err.println("you may specify which dump in the file");
      System.err.println("by appending \"#<number>\" to the file name, i.e. \"foo.hprof#3\".");
      System.err.println();
      System.err.println("All boolean options default to \"true\"");
      System.exit(1);
   }

   private static boolean booleanValue(String var0) {
      if ("true".equalsIgnoreCase(var0)) {
         return true;
      } else if ("false".equalsIgnoreCase(var0)) {
         return false;
      } else {
         usage("Boolean value must be true or false");
         return false;
      }
   }

   public static void main(String[] var0) {
      if (var0.length < 1) {
         usage("No arguments supplied");
      }

      boolean var1 = false;
      int var2 = 7000;
      boolean var3 = true;
      boolean var4 = true;
      String var5 = null;
      String var6 = null;
      int var7 = 0;
      int var8 = 0;

      while(true) {
         if (var8 > var0.length - 1) {
            usage("Option parsing error");
         }

         if ("-version".equals(var0[var8])) {
            System.out.print(VERSION_STRING);
            System.out.println(" (java version " + System.getProperty("java.version") + ")");
            System.exit(0);
         }

         if ("-h".equals(var0[var8]) || "-help".equals(var0[var8])) {
            usage((String)null);
         }

         if (var8 == var0.length - 1) {
            String var17 = var0[var0.length - 1];
            Snapshot var18 = null;
            File var19 = null;
            if (var6 != null) {
               var19 = new File(var6);
               if (!var19.exists()) {
                  System.out.println("Exclude file " + var19 + " does not exist.  Aborting.");
                  System.exit(1);
               }
            }

            System.out.println("Reading from " + var17 + "...");

            try {
               var18 = Reader.readFile(var17, var3, var7);
            } catch (IOException var15) {
               var15.printStackTrace();
               System.exit(1);
            } catch (RuntimeException var16) {
               var16.printStackTrace();
               System.exit(1);
            }

            System.out.println("Snapshot read, resolving...");
            var18.resolve(var4);
            System.out.println("Snapshot resolved.");
            if (var19 != null) {
               var18.setReachableExcludes(new ReachableExcludesImpl(var19));
            }

            if (var5 != null) {
               System.out.println("Reading baseline snapshot...");
               Snapshot var11 = null;

               try {
                  var11 = Reader.readFile(var5, false, var7);
               } catch (IOException var13) {
                  var13.printStackTrace();
                  System.exit(1);
               } catch (RuntimeException var14) {
                  var14.printStackTrace();
                  System.exit(1);
               }

               var11.resolve(false);
               System.out.println("Discovering new objects...");
               var18.markNewRelativeTo(var11);
               var11 = null;
            }

            if (var7 == 2) {
               System.out.println("No server, -debug 2 was used.");
               System.exit(0);
            }

            if (var1) {
               System.out.println("-parseonly is true, exiting..");
               System.exit(0);
            }

            QueryListener var20 = new QueryListener(var2);
            var20.setModel(var18);
            Thread var12 = new Thread(var20, "Query Listener");
            var12.setPriority(6);
            var12.start();
            System.out.println("Started HTTP server on port " + var2);
            System.out.println("Server is ready.");
            return;
         }

         String var9 = var0[var8];
         String var10 = var0[var8 + 1];
         if ("-stack".equals(var9)) {
            var3 = booleanValue(var10);
         } else if ("-refs".equals(var9)) {
            var4 = booleanValue(var10);
         } else if ("-port".equals(var9)) {
            var2 = Integer.parseInt(var10, 10);
         } else if ("-exclude".equals(var9)) {
            var6 = var10;
         } else if ("-baseline".equals(var9)) {
            var5 = var10;
         } else if ("-debug".equals(var9)) {
            var7 = Integer.parseInt(var10, 10);
         } else if ("-parseonly".equals(var9)) {
            var1 = booleanValue(var10);
         }

         var8 += 2;
      }
   }
}
