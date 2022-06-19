package com.sun.tools.corba.se.idl;

public class InterfaceState {
   public static final int Private = 0;
   public static final int Protected = 1;
   public static final int Public = 2;
   public int modifier = 2;
   public TypedefEntry entry = null;

   public InterfaceState(int var1, TypedefEntry var2) {
      this.modifier = var1;
      this.entry = var2;
      if (this.modifier < 0 || this.modifier > 2) {
         this.modifier = 2;
      }

   }
}
