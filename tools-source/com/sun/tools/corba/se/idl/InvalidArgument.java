package com.sun.tools.corba.se.idl;

public class InvalidArgument extends Exception {
   private String message = null;

   public InvalidArgument(String var1) {
      this.message = Util.getMessage("InvalidArgument.1", var1) + "\n\n" + Util.getMessage("usage");
   }

   public InvalidArgument() {
      this.message = Util.getMessage("InvalidArgument.2") + "\n\n" + Util.getMessage("usage");
   }

   public String getMessage() {
      return this.message;
   }
}
