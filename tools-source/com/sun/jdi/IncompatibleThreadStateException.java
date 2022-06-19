package com.sun.jdi;

import jdk.Exported;

@Exported
public class IncompatibleThreadStateException extends Exception {
   private static final long serialVersionUID = 6199174323414551389L;

   public IncompatibleThreadStateException() {
   }

   public IncompatibleThreadStateException(String var1) {
      super(var1);
   }
}
