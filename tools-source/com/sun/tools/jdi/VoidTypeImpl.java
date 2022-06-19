package com.sun.tools.jdi;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VoidType;

public class VoidTypeImpl extends TypeImpl implements VoidType {
   VoidTypeImpl(VirtualMachine var1) {
      super(var1);
   }

   public String signature() {
      return String.valueOf('V');
   }

   public String toString() {
      return this.name();
   }
}
