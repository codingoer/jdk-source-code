package com.sun.jdi;

import jdk.Exported;

@Exported
public class VMMismatchException extends RuntimeException {
   private static final long serialVersionUID = 289169358790459564L;

   public VMMismatchException() {
   }

   public VMMismatchException(String var1) {
      super(var1);
   }
}
