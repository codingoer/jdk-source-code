package com.sun.tools.jdi;

import com.sun.jdi.FloatType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.VirtualMachine;

public class FloatTypeImpl extends PrimitiveTypeImpl implements FloatType {
   FloatTypeImpl(VirtualMachine var1) {
      super(var1);
   }

   public String signature() {
      return String.valueOf('F');
   }

   PrimitiveValue convert(PrimitiveValue var1) throws InvalidTypeException {
      return this.vm.mirrorOf(((PrimitiveValueImpl)var1).checkedFloatValue());
   }
}
