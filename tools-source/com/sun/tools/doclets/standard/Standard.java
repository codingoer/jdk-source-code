package com.sun.tools.doclets.standard;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.formats.html.HtmlDoclet;

public class Standard {
   public static int optionLength(String var0) {
      return HtmlDoclet.optionLength(var0);
   }

   public static boolean start(RootDoc var0) {
      return HtmlDoclet.start(var0);
   }

   public static boolean validOptions(String[][] var0, DocErrorReporter var1) {
      return HtmlDoclet.validOptions(var0, var1);
   }

   public static LanguageVersion languageVersion() {
      return HtmlDoclet.languageVersion();
   }
}
