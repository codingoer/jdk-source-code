package com.sun.jdi;

import jdk.Exported;

@Exported
public class InvalidTypeException extends Exception {
   private static final long serialVersionUID = 2256667231949650806L;

   public InvalidTypeException() {
   }

   public InvalidTypeException(String var1) {
      super(var1);
   }
}
