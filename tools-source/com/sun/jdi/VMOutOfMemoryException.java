package com.sun.jdi;

import jdk.Exported;

@Exported
public class VMOutOfMemoryException extends RuntimeException {
   private static final long serialVersionUID = 71504228548910686L;

   public VMOutOfMemoryException() {
   }

   public VMOutOfMemoryException(String var1) {
      super(var1);
   }
}
