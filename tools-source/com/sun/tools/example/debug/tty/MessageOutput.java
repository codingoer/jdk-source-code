package com.sun.tools.example.debug.tty;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageOutput {
   static ResourceBundle textResources;
   private static MessageFormat messageFormat;

   static void fatalError(String var0) {
      System.err.println();
      System.err.println(format("Fatal error"));
      System.err.println(format(var0));
      Env.shutdown();
   }

   static String format(String var0) {
      return textResources.getString(var0);
   }

   static String format(String var0, String var1) {
      return format(var0, new Object[]{var1});
   }

   static synchronized String format(String var0, Object[] var1) {
      if (messageFormat == null) {
         messageFormat = new MessageFormat(textResources.getString(var0));
      } else {
         messageFormat.applyPattern(textResources.getString(var0));
      }

      return messageFormat.format(var1);
   }

   static void printDirectln(String var0) {
      System.out.println(var0);
   }

   static void printDirect(String var0) {
      System.out.print(var0);
   }

   static void printDirect(char var0) {
      System.out.print(var0);
   }

   static void println() {
      System.out.println();
   }

   static void print(String var0) {
      System.out.print(format(var0));
   }

   static void println(String var0) {
      System.out.println(format(var0));
   }

   static void print(String var0, String var1) {
      System.out.print(format(var0, var1));
   }

   static void println(String var0, String var1) {
      System.out.println(format(var0, var1));
   }

   static void println(String var0, Object[] var1) {
      System.out.println(format(var0, var1));
   }

   static void lnprint(String var0) {
      System.out.println();
      System.out.print(textResources.getString(var0));
   }

   static void lnprint(String var0, String var1) {
      System.out.println();
      System.out.print(format(var0, var1));
   }

   static void lnprint(String var0, Object[] var1) {
      System.out.println();
      System.out.print(format(var0, var1));
   }

   static void printException(String var0, Exception var1) {
      if (var0 != null) {
         try {
            println(var0);
         } catch (MissingResourceException var3) {
            printDirectln(var0);
         }
      }

      System.out.flush();
      var1.printStackTrace();
   }

   static void printPrompt() {
      ThreadInfo var0 = ThreadInfo.getCurrentThreadInfo();
      if (var0 == null) {
         System.out.print(format("jdb prompt with no current thread"));
      } else {
         System.out.print(format("jdb prompt thread name and current stack frame", new Object[]{var0.getThread().name(), new Integer(var0.getCurrentFrameIndex() + 1)}));
      }

      System.out.flush();
   }
}
