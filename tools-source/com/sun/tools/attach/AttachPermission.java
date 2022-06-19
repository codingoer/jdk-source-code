package com.sun.tools.attach;

import java.security.BasicPermission;
import jdk.Exported;

@Exported
public final class AttachPermission extends BasicPermission {
   static final long serialVersionUID = -4619447669752976181L;

   public AttachPermission(String var1) {
      super(var1);
      if (!var1.equals("attachVirtualMachine") && !var1.equals("createAttachProvider")) {
         throw new IllegalArgumentException("name: " + var1);
      }
   }

   public AttachPermission(String var1, String var2) {
      super(var1);
      if (!var1.equals("attachVirtualMachine") && !var1.equals("createAttachProvider")) {
         throw new IllegalArgumentException("name: " + var1);
      } else if (var2 != null && var2.length() > 0) {
         throw new IllegalArgumentException("actions: " + var2);
      }
   }
}
