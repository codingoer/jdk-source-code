package com.sun.tools.jdi;

import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ShortType;
import com.sun.jdi.VirtualMachine;

public class ShortTypeImpl extends PrimitiveTypeImpl implements ShortType {
   ShortTypeImpl(VirtualMachine var1) {
      super(var1);
   }

   public String signature() {
      return String.valueOf('S');
   }

   PrimitiveValue convert(PrimitiveValue var1) throws InvalidTypeException {
      return this.vm.mirrorOf(((PrimitiveValueImpl)var1).checkedShortValue());
   }
}
