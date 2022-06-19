package com.sun.jdi;

import jdk.Exported;

/** @deprecated */
@Exported
@Deprecated
public class InvalidCodeIndexException extends RuntimeException {
   private static final long serialVersionUID = 7416010225133747805L;

   public InvalidCodeIndexException() {
   }

   public InvalidCodeIndexException(String var1) {
      super(var1);
   }
}
