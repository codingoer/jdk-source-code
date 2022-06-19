package com.sun.jdi.connect;

import jdk.Exported;

@Exported
public class VMStartException extends Exception {
   private static final long serialVersionUID = 6408644824640801020L;
   Process process;

   public VMStartException(Process var1) {
      this.process = var1;
   }

   public VMStartException(String var1, Process var2) {
      super(var1);
      this.process = var2;
   }

   public Process process() {
      return this.process;
   }
}
