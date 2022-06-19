package com.sun.tools.javac.api;

import java.util.Locale;

public interface Formattable {
   String toString(Locale var1, Messages var2);

   String getKind();

   public static class LocalizedString implements Formattable {
      String key;

      public LocalizedString(String var1) {
         this.key = var1;
      }

      public String toString(Locale var1, Messages var2) {
         return var2.getLocalizedString(var1, this.key);
      }

      public String getKind() {
         return "LocalizedString";
      }

      public String toString() {
         return this.key;
      }
   }
}
