package com.sun.tools.jdi;

import com.sun.jdi.DoubleType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.VirtualMachine;

public class DoubleTypeImpl extends PrimitiveTypeImpl implements DoubleType {
   DoubleTypeImpl(VirtualMachine var1) {
      super(var1);
   }

   public String signature() {
      return String.valueOf('D');
   }

   PrimitiveValue convert(PrimitiveValue var1) throws InvalidTypeException {
      return this.vm.mirrorOf(((PrimitiveValueImpl)var1).checkedDoubleValue());
   }
}
