package com.sun.tools.internal.jxc.ap;

public enum Const {
   CONFIG_FILE_OPTION("jaxb.config"),
   DEBUG_OPTION("jaxb.debug");

   private String value;

   private Const(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
