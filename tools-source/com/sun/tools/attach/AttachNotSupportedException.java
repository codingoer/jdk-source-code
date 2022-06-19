package com.sun.tools.attach;

import jdk.Exported;

@Exported
public class AttachNotSupportedException extends Exception {
   static final long serialVersionUID = 3391824968260177264L;

   public AttachNotSupportedException() {
   }

   public AttachNotSupportedException(String var1) {
      super(var1);
   }
}
