package com.sun.tools.internal.xjc.api;

public enum SpecVersion {
   V2_0,
   V2_1,
   V2_2;

   public static final SpecVersion LATEST = V2_2;

   public boolean isLaterThan(SpecVersion t) {
      return this.ordinal() >= t.ordinal();
   }

   public static SpecVersion parse(String token) {
      if (token.equals("2.0")) {
         return V2_0;
      } else if (token.equals("2.1")) {
         return V2_1;
      } else {
         return token.equals("2.2") ? V2_2 : null;
      }
   }
}
