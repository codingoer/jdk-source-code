package com.sun.tools.jdi;

import com.sun.jdi.IntegerType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.VirtualMachine;

public class IntegerTypeImpl extends PrimitiveTypeImpl implements IntegerType {
   IntegerTypeImpl(VirtualMachine var1) {
      super(var1);
   }

   public String signature() {
      return String.valueOf('I');
   }

   PrimitiveValue convert(PrimitiveValue var1) throws InvalidTypeException {
      return this.vm.mirrorOf(((PrimitiveValueImpl)var1).checkedIntValue());
   }
}
