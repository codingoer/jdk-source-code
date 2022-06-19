package com.sun.jdi;

import jdk.Exported;

@Exported
public class AbsentInformationException extends Exception {
   private static final long serialVersionUID = 4988939309582416373L;

   public AbsentInformationException() {
   }

   public AbsentInformationException(String var1) {
      super(var1);
   }
}
