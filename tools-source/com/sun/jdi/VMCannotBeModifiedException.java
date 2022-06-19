package com.sun.jdi;

import jdk.Exported;

@Exported
public class VMCannotBeModifiedException extends UnsupportedOperationException {
   private static final long serialVersionUID = -4063879815130164009L;

   public VMCannotBeModifiedException() {
   }

   public VMCannotBeModifiedException(String var1) {
      super(var1);
   }
}
