package com.sun.tools.javadoc;

import java.io.PrintWriter;

public class Main {
   private Main() {
   }

   public static void main(String... var0) {
      System.exit(execute(var0));
   }

   public static int execute(String... var0) {
      Start var1 = new Start();
      return var1.begin(var0);
   }

   public static int execute(ClassLoader var0, String... var1) {
      Start var2 = new Start(var0);
      return var2.begin(var1);
   }

   public static int execute(String var0, String... var1) {
      Start var2 = new Start(var0);
      return var2.begin(var1);
   }

   public static int execute(String var0, ClassLoader var1, String... var2) {
      Start var3 = new Start(var0, var1);
      return var3.begin(var2);
   }

   public static int execute(String var0, String var1, String... var2) {
      Start var3 = new Start(var0, var1);
      return var3.begin(var2);
   }

   public static int execute(String var0, String var1, ClassLoader var2, String... var3) {
      Start var4 = new Start(var0, var1, var2);
      return var4.begin(var3);
   }

   public static int execute(String var0, PrintWriter var1, PrintWriter var2, PrintWriter var3, String var4, String... var5) {
      Start var6 = new Start(var0, var1, var2, var3, var4);
      return var6.begin(var5);
   }

   public static int execute(String var0, PrintWriter var1, PrintWriter var2, PrintWriter var3, String var4, ClassLoader var5, String... var6) {
      Start var7 = new Start(var0, var1, var2, var3, var4, var5);
      return var7.begin(var6);
   }
}
