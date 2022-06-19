package com.sun.jdi;

import jdk.Exported;

@Exported
public class ClassNotPreparedException extends RuntimeException {
   private static final long serialVersionUID = -6120698967144079642L;

   public ClassNotPreparedException() {
   }

   public ClassNotPreparedException(String var1) {
      super(var1);
   }
}
