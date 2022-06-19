package com.sun.tools.attach;

import jdk.Exported;

@Exported
public class AgentInitializationException extends Exception {
   static final long serialVersionUID = -1508756333332806353L;
   private int returnValue;

   public AgentInitializationException() {
      this.returnValue = 0;
   }

   public AgentInitializationException(String var1) {
      super(var1);
      this.returnValue = 0;
   }

   public AgentInitializationException(String var1, int var2) {
      super(var1);
      this.returnValue = var2;
   }

   public int returnValue() {
      return this.returnValue;
   }
}
