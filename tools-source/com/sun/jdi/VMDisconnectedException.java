package com.sun.jdi;

import jdk.Exported;

@Exported
public class VMDisconnectedException extends RuntimeException {
   private static final long serialVersionUID = 2892975269768351637L;

   public VMDisconnectedException() {
   }

   public VMDisconnectedException(String var1) {
      super(var1);
   }
}
