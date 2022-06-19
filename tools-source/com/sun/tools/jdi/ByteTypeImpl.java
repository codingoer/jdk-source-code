package com.sun.tools.jdi;

import com.sun.jdi.ByteType;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.VirtualMachine;

public class ByteTypeImpl extends PrimitiveTypeImpl implements ByteType {
   ByteTypeImpl(VirtualMachine var1) {
      super(var1);
   }

   public String signature() {
      return String.valueOf('B');
   }

   PrimitiveValue convert(PrimitiveValue var1) throws InvalidTypeException {
      return this.vm.mirrorOf(((PrimitiveValueImpl)var1).checkedByteValue());
   }
}
