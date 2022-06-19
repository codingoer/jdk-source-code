package com.sun.tools.jdi;

import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VoidValue;

public class VoidValueImpl extends ValueImpl implements VoidValue {
   VoidValueImpl(VirtualMachine var1) {
      super(var1);
   }

   public boolean equals(Object var1) {
      return var1 != null && var1 instanceof VoidValue && super.equals(var1);
   }

   public int hashCode() {
      return 47245;
   }

   public Type type() {
      return this.vm.theVoidType();
   }

   ValueImpl prepareForAssignmentTo(ValueContainer var1) throws InvalidTypeException {
      if ("void".equals(var1.typeName())) {
         return this;
      } else {
         throw new InvalidTypeException();
      }
   }

   public String toString() {
      return "<void value>";
   }

   byte typeValueKey() {
      return 86;
   }
}
