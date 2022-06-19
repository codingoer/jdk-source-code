package com.sun.tools.attach;

import java.io.IOException;
import jdk.Exported;

@Exported
public class AttachOperationFailedException extends IOException {
   private static final long serialVersionUID = 2140308168167478043L;

   public AttachOperationFailedException(String var1) {
      super(var1);
   }
}
