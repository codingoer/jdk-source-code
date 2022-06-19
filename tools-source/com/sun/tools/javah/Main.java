package com.sun.tools.javah;

import java.io.PrintWriter;

public class Main {
   public static void main(String[] var0) {
      JavahTask var1 = new JavahTask();
      int var2 = var1.run(var0);
      System.exit(var2);
   }

   public static int run(String[] var0, PrintWriter var1) {
      JavahTask var2 = new JavahTask();
      var2.setLog(var1);
      return var2.run(var0);
   }
}
