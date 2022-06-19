package com.sun.jdi;

import jdk.Exported;

@Exported
public class InvalidStackFrameException extends RuntimeException {
   private static final long serialVersionUID = -1919378296505827922L;

   public InvalidStackFrameException() {
   }

   public InvalidStackFrameException(String var1) {
      super(var1);
   }
}
