package com.sun.jdi;

import jdk.Exported;

@Exported
public class InconsistentDebugInfoException extends RuntimeException {
   private static final long serialVersionUID = 7964236415376861808L;

   public InconsistentDebugInfoException() {
   }

   public InconsistentDebugInfoException(String var1) {
      super(var1);
   }
}
