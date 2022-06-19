package com.sun.tools.javac.main;

import com.sun.tools.javac.util.Log;
import java.io.File;

public abstract class OptionHelper {
   public abstract String get(Option var1);

   public abstract void put(String var1, String var2);

   public abstract void remove(String var1);

   public abstract Log getLog();

   public abstract String getOwnName();

   abstract void error(String var1, Object... var2);

   abstract void addFile(File var1);

   abstract void addClassName(String var1);

   public static class GrumpyHelper extends OptionHelper {
      private final Log log;

      public GrumpyHelper(Log var1) {
         this.log = var1;
      }

      public Log getLog() {
         return this.log;
      }

      public String getOwnName() {
         throw new IllegalStateException();
      }

      public String get(Option var1) {
         throw new IllegalArgumentException();
      }

      public void put(String var1, String var2) {
         throw new IllegalArgumentException();
      }

      public void remove(String var1) {
         throw new IllegalArgumentException();
      }

      void error(String var1, Object... var2) {
         throw new IllegalArgumentException(this.log.localize(Log.PrefixKind.JAVAC, var1, var2));
      }

      public void addFile(File var1) {
         throw new IllegalArgumentException(var1.getPath());
      }

      public void addClassName(String var1) {
         throw new IllegalArgumentException(var1);
      }
   }
}
