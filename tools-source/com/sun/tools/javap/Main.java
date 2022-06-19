package com.sun.tools.javap;

import java.io.PrintWriter;
import java.io.Writer;

public class Main {
   public static void main(String[] var0) {
      JavapTask var1 = new JavapTask();
      int var2 = var1.run(var0);
      System.exit(var2);
   }

   public static int run(String[] var0, PrintWriter var1) {
      JavapTask var2 = new JavapTask();
      var2.setLog((Writer)var1);
      return var2.run(var0);
   }
}
