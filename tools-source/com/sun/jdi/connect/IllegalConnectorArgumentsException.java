package com.sun.jdi.connect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jdk.Exported;

@Exported
public class IllegalConnectorArgumentsException extends Exception {
   private static final long serialVersionUID = -3042212603611350941L;
   List names;

   public IllegalConnectorArgumentsException(String var1, String var2) {
      super(var1);
      this.names = new ArrayList(1);
      this.names.add(var2);
   }

   public IllegalConnectorArgumentsException(String var1, List var2) {
      super(var1);
      this.names = new ArrayList(var2);
   }

   public List argumentNames() {
      return Collections.unmodifiableList(this.names);
   }
}
