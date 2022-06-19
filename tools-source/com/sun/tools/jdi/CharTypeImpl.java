package com.sun.tools.jdi;

import com.sun.jdi.CharType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.VirtualMachine;

public class CharTypeImpl extends PrimitiveTypeImpl implements CharType {
   CharTypeImpl(VirtualMachine var1) {
      super(var1);
   }

   public String signature() {
      return String.valueOf('C');
   }

   PrimitiveValue convert(PrimitiveValue var1) throws InvalidTypeException {
      return this.vm.mirrorOf(((PrimitiveValueImpl)var1).checkedCharValue());
   }
}
