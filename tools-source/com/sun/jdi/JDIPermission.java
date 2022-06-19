package com.sun.jdi;

import java.security.BasicPermission;
import jdk.Exported;

@Exported
public final class JDIPermission extends BasicPermission {
   private static final long serialVersionUID = -6988461416938786271L;

   public JDIPermission(String var1) {
      super(var1);
      if (!var1.equals("virtualMachineManager")) {
         throw new IllegalArgumentException("name: " + var1);
      }
   }

   public JDIPermission(String var1, String var2) throws IllegalArgumentException {
      super(var1);
      if (!var1.equals("virtualMachineManager")) {
         throw new IllegalArgumentException("name: " + var1);
      } else if (var2 != null && var2.length() > 0) {
         throw new IllegalArgumentException("actions: " + var2);
      }
   }
}
