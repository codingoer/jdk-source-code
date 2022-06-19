package com.sun.tools.extcheck;

import java.io.File;

public final class Main {
   public static final String INSUFFICIENT = "Insufficient number of arguments";
   public static final String MISSING = "Missing <jar file> argument";
   public static final String DOES_NOT_EXIST = "Jarfile does not exist: ";
   public static final String EXTRA = "Extra command line argument: ";

   public static void main(String[] var0) {
      try {
         realMain(var0);
      } catch (Exception var2) {
         System.err.println(var2.getMessage());
         System.exit(-1);
      }

   }

   public static void realMain(String[] var0) throws Exception {
      if (var0.length < 1) {
         usage("Insufficient number of arguments");
      }

      int var1 = 0;
      boolean var2 = false;
      if (var0[var1].equals("-verbose")) {
         var2 = true;
         ++var1;
         if (var1 >= var0.length) {
            usage("Missing <jar file> argument");
         }
      }

      String var3 = var0[var1];
      ++var1;
      File var4 = new File(var3);
      if (!var4.exists()) {
         usage("Jarfile does not exist: " + var3);
      }

      if (var1 < var0.length) {
         usage("Extra command line argument: " + var0[var1]);
      }

      ExtCheck var5 = ExtCheck.create(var4, var2);
      boolean var6 = var5.checkInstalledAgainstTarget();
      if (var6) {
         System.exit(0);
      } else {
         System.exit(1);
      }

   }

   private static void usage(String var0) throws Exception {
      throw new Exception(var0 + "\nUsage: extcheck [-verbose] <jar file>");
   }
}
