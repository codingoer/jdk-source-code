package com.sun.tools.jdeps;

import java.io.PrintWriter;

public class Main {
   public static void main(String... var0) throws Exception {
      JdepsTask var1 = new JdepsTask();
      int var2 = var1.run(var0);
      System.exit(var2);
   }

   public static int run(String[] var0, PrintWriter var1) {
      JdepsTask var2 = new JdepsTask();
      var2.setLog(var1);
      return var2.run(var0);
   }
}
