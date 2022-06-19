package com.sun.tools.jdi;

import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.LongType;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.VirtualMachine;

public class LongTypeImpl extends PrimitiveTypeImpl implements LongType {
   LongTypeImpl(VirtualMachine var1) {
      super(var1);
   }

   public String signature() {
      return String.valueOf('J');
   }

   PrimitiveValue convert(PrimitiveValue var1) throws InvalidTypeException {
      return this.vm.mirrorOf(((PrimitiveValueImpl)var1).checkedLongValue());
   }
}
