package com.sun.tools.jdi;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.InternalException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.VirtualMachine;

public abstract class PrimitiveValueImpl extends ValueImpl implements PrimitiveValue {
   PrimitiveValueImpl(VirtualMachine var1) {
      super(var1);
   }

   public abstract boolean booleanValue();

   public abstract byte byteValue();

   public abstract char charValue();

   public abstract short shortValue();

   public abstract int intValue();

   public abstract long longValue();

   public abstract float floatValue();

   public abstract double doubleValue();

   byte checkedByteValue() throws InvalidTypeException {
      return this.byteValue();
   }

   char checkedCharValue() throws InvalidTypeException {
      return this.charValue();
   }

   short checkedShortValue() throws InvalidTypeException {
      return this.shortValue();
   }

   int checkedIntValue() throws InvalidTypeException {
      return this.intValue();
   }

   long checkedLongValue() throws InvalidTypeException {
      return this.longValue();
   }

   float checkedFloatValue() throws InvalidTypeException {
      return this.floatValue();
   }

   final boolean checkedBooleanValue() throws InvalidTypeException {
      if (this instanceof BooleanValue) {
         return this.booleanValue();
      } else {
         throw new InvalidTypeException("Can't convert non-boolean value to boolean");
      }
   }

   final double checkedDoubleValue() throws InvalidTypeException {
      return this.doubleValue();
   }

   ValueImpl prepareForAssignmentTo(ValueContainer var1) throws InvalidTypeException {
      return this.convertForAssignmentTo(var1);
   }

   ValueImpl convertForAssignmentTo(ValueContainer var1) throws InvalidTypeException {
      if (var1.signature().length() > 1) {
         throw new InvalidTypeException("Can't assign primitive value to object");
      } else if (var1.signature().charAt(0) == 'Z' && this.type().signature().charAt(0) != 'Z') {
         throw new InvalidTypeException("Can't assign non-boolean value to a boolean");
      } else if (var1.signature().charAt(0) != 'Z' && this.type().signature().charAt(0) == 'Z') {
         throw new InvalidTypeException("Can't assign boolean value to an non-boolean");
      } else if ("void".equals(var1.typeName())) {
         throw new InvalidTypeException("Can't assign primitive value to a void");
      } else {
         try {
            PrimitiveTypeImpl var2 = (PrimitiveTypeImpl)var1.type();
            return (ValueImpl)((ValueImpl)var2.convert(this));
         } catch (ClassNotLoadedException var3) {
            throw new InternalException("Signature and type inconsistent for: " + var1.typeName());
         }
      }
   }
}
