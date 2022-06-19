package com.sun.jdi.request;

import jdk.Exported;

@Exported
public class DuplicateRequestException extends RuntimeException {
   private static final long serialVersionUID = -3719784920313411060L;

   public DuplicateRequestException() {
   }

   public DuplicateRequestException(String var1) {
      super(var1);
   }
}
