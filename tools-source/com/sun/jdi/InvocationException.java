package com.sun.jdi;

import jdk.Exported;

@Exported
public class InvocationException extends Exception {
   private static final long serialVersionUID = 6066780907971918568L;
   ObjectReference exception;

   public InvocationException(ObjectReference var1) {
      super("Exception occurred in target VM");
      this.exception = var1;
   }

   public ObjectReference exception() {
      return this.exception;
   }
}
