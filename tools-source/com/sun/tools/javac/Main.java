package com.sun.tools.javac;

import java.io.PrintWriter;
import jdk.Exported;

@Exported
public class Main {
   public static void main(String[] var0) throws Exception {
      System.exit(compile(var0));
   }

   public static int compile(String[] var0) {
      com.sun.tools.javac.main.Main var1 = new com.sun.tools.javac.main.Main("javac");
      return var1.compile(var0).exitCode;
   }

   public static int compile(String[] var0, PrintWriter var1) {
      com.sun.tools.javac.main.Main var2 = new com.sun.tools.javac.main.Main("javac", var1);
      return var2.compile(var0).exitCode;
   }
}
