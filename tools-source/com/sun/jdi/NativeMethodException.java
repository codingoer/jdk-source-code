package com.sun.jdi;

import jdk.Exported;

@Exported
public class NativeMethodException extends RuntimeException {
   private static final long serialVersionUID = 3924951669039469992L;

   public NativeMethodException() {
   }

   public NativeMethodException(String var1) {
      super(var1);
   }
}
