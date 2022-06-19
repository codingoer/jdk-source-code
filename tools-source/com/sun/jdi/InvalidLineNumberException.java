package com.sun.jdi;

import jdk.Exported;

/** @deprecated */
@Exported
@Deprecated
public class InvalidLineNumberException extends RuntimeException {
   private static final long serialVersionUID = 4048709912372692875L;

   public InvalidLineNumberException() {
   }

   public InvalidLineNumberException(String var1) {
      super(var1);
   }
}
