package com.sun.tools.jdi;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;

abstract class ValueImpl extends MirrorImpl implements Value {
   ValueImpl(VirtualMachine var1) {
      super(var1);
   }

   static ValueImpl prepareForAssignment(Value var0, ValueContainer var1) throws InvalidTypeException, ClassNotLoadedException {
      if (var0 == null) {
         if (var1.signature().length() == 1) {
            throw new InvalidTypeException("Can't set a primitive type to null");
         } else {
            return null;
         }
      } else {
         return ((ValueImpl)var0).prepareForAssignmentTo(var1);
      }
   }

   static byte typeValueKey(Value var0) {
      return var0 == null ? 76 : ((ValueImpl)var0).typeValueKey();
   }

   abstract ValueImpl prepareForAssignmentTo(ValueContainer var1) throws InvalidTypeException, ClassNotLoadedException;

   abstract byte typeValueKey();
}
