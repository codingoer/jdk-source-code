package com.sun.jdi.connect.spi;

import java.io.IOException;
import jdk.Exported;

@Exported
public class ClosedConnectionException extends IOException {
   private static final long serialVersionUID = 3877032124297204774L;

   public ClosedConnectionException() {
   }

   public ClosedConnectionException(String var1) {
      super(var1);
   }
}
