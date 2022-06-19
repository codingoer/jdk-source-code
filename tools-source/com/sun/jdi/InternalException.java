package com.sun.jdi;

import jdk.Exported;

@Exported
public class InternalException extends RuntimeException {
   private static final long serialVersionUID = -9171606393104480607L;
   private int errorCode;

   public InternalException() {
      this.errorCode = 0;
   }

   public InternalException(String var1) {
      super(var1);
      this.errorCode = 0;
   }

   public InternalException(int var1) {
      this.errorCode = var1;
   }

   public InternalException(String var1, int var2) {
      super(var1);
      this.errorCode = var2;
   }

   public int errorCode() {
      return this.errorCode;
   }
}
