package com.sun.jdi;

import jdk.Exported;

@Exported
public class ObjectCollectedException extends RuntimeException {
   private static final long serialVersionUID = -1928428056197269588L;

   public ObjectCollectedException() {
   }

   public ObjectCollectedException(String var1) {
      super(var1);
   }
}
