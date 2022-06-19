package com.sun.tools.hat.internal.model;

public class JavaField {
   private String name;
   private String signature;

   public JavaField(String var1, String var2) {
      this.name = var1;
      this.signature = var2;
   }

   public boolean hasId() {
      char var1 = this.signature.charAt(0);
      return var1 == '[' || var1 == 'L';
   }

   public String getName() {
      return this.name;
   }

   public String getSignature() {
      return this.signature;
   }
}
