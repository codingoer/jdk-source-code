package com.sun.tools.jdi;

import com.sun.jdi.CharValue;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class CharValueImpl extends PrimitiveValueImpl implements CharValue {
   private char value;

   CharValueImpl(VirtualMachine var1, char var2) {
      super(var1);
      this.value = var2;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof CharValue) {
         return this.value == ((CharValue)var1).value() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.intValue();
   }

   public int compareTo(CharValue var1) {
      char var2 = var1.value();
      return this.value() - var2;
   }

   public Type type() {
      return this.vm.theCharType();
   }

   public char value() {
      return this.value;
   }

   public boolean booleanValue() {
      return this.value != 0;
   }

   public byte byteValue() {
      return (byte)this.value;
   }

   public char charValue() {
      return this.value;
   }

   public short shortValue() {
      return (short)this.value;
   }

   public int intValue() {
      return this.value;
   }

   public long longValue() {
      return (long)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   public String toString() {
      return "" + this.value;
   }

   byte checkedByteValue() throws InvalidTypeException {
      if (this.value > 127) {
         throw new InvalidTypeException("Can't convert " + this.value + " to byte");
      } else {
         return super.checkedByteValue();
      }
   }

   short checkedShortValue() throws InvalidTypeException {
      if (this.value > 32767) {
         throw new InvalidTypeException("Can't convert " + this.value + " to short");
      } else {
         return super.checkedShortValue();
      }
   }

   byte typeValueKey() {
      return 67;
   }
}
