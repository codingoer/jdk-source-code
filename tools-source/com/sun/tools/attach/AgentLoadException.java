package com.sun.tools.attach;

import jdk.Exported;

@Exported
public class AgentLoadException extends Exception {
   static final long serialVersionUID = 688047862952114238L;

   public AgentLoadException() {
   }

   public AgentLoadException(String var1) {
      super(var1);
   }
}
