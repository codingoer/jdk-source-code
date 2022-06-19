package com.sun.tools.jdi;

import com.sun.jdi.BooleanType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.VirtualMachine;

public class BooleanTypeImpl extends PrimitiveTypeImpl implements BooleanType {
   BooleanTypeImpl(VirtualMachine var1) {
      super(var1);
   }

   public String signature() {
      return String.valueOf('Z');
   }

   PrimitiveValue convert(PrimitiveValue var1) throws InvalidTypeException {
      return this.vm.mirrorOf(((PrimitiveValueImpl)var1).checkedBooleanValue());
   }
}
