package com.sun.javadoc;

public abstract class Doclet {
   public static boolean start(RootDoc var0) {
      return true;
   }

   public static int optionLength(String var0) {
      return 0;
   }

   public static boolean validOptions(String[][] var0, DocErrorReporter var1) {
      return true;
   }

   public static LanguageVersion languageVersion() {
      return LanguageVersion.JAVA_1_1;
   }
}
