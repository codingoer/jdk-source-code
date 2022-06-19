package com.sun.jdi.request;

import jdk.Exported;

@Exported
public class InvalidRequestStateException extends RuntimeException {
   private static final long serialVersionUID = -3774632428543322148L;

   public InvalidRequestStateException() {
   }

   public InvalidRequestStateException(String var1) {
      super(var1);
   }
}
