package com.sun.tools.jdi;

import com.sun.jdi.IntegerValue;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class IntegerValueImpl extends PrimitiveValueImpl implements IntegerValue {
   private int value;

   IntegerValueImpl(VirtualMachine var1, int var2) {
      super(var1);
      this.value = var2;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof IntegerValue) {
         return this.value == ((IntegerValue)var1).value() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.intValue();
   }

   public int compareTo(IntegerValue var1) {
      int var2 = var1.value();
      return this.value() < var2 ? -1 : (this.value() == var2 ? 0 : 1);
   }

   public Type type() {
      return this.vm.theIntegerType();
   }

   public int value() {
      return this.value;
   }

   public boolean booleanValue() {
      return this.value != 0;
   }

   public byte byteValue() {
      return (byte)this.value;
   }

   public char charValue() {
      return (char)this.value;
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

   byte checkedByteValue() throws InvalidTypeException {
      if (this.value <= 127 && this.value >= -128) {
         return super.checkedByteValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to byte");
      }
   }

   char checkedCharValue() throws InvalidTypeException {
      if (this.value <= 65535 && this.value >= 0) {
         return super.checkedCharValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to char");
      }
   }

   short checkedShortValue() throws InvalidTypeException {
      if (this.value <= 32767 && this.value >= -32768) {
         return super.checkedShortValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to short");
      }
   }

   public String toString() {
      return "" + this.value;
   }

   byte typeValueKey() {
      return 73;
   }
}
