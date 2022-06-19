package com.sun.tools.jdi;

import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.LongValue;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class LongValueImpl extends PrimitiveValueImpl implements LongValue {
   private long value;

   LongValueImpl(VirtualMachine var1, long var2) {
      super(var1);
      this.value = var2;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof LongValue) {
         return this.value == ((LongValue)var1).value() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.intValue();
   }

   public int compareTo(LongValue var1) {
      long var2 = var1.value();
      if (this.value() < var2) {
         return -1;
      } else {
         return this.value() == var2 ? 0 : 1;
      }
   }

   public Type type() {
      return this.vm.theLongType();
   }

   public long value() {
      return this.value;
   }

   public boolean booleanValue() {
      return this.value != 0L;
   }

   public byte byteValue() {
      return (byte)((int)this.value);
   }

   public char charValue() {
      return (char)((int)this.value);
   }

   public short shortValue() {
      return (short)((int)this.value);
   }

   public int intValue() {
      return (int)this.value;
   }

   public long longValue() {
      return this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   byte checkedByteValue() throws InvalidTypeException {
      if (this.value <= 127L && this.value >= -128L) {
         return super.checkedByteValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to byte");
      }
   }

   char checkedCharValue() throws InvalidTypeException {
      if (this.value <= 65535L && this.value >= 0L) {
         return super.checkedCharValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to char");
      }
   }

   short checkedShortValue() throws InvalidTypeException {
      if (this.value <= 32767L && this.value >= -32768L) {
         return super.checkedShortValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to short");
      }
   }

   int checkedIntValue() throws InvalidTypeException {
      if (this.value <= 2147483647L && this.value >= -2147483648L) {
         return super.checkedIntValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to int");
      }
   }

   public String toString() {
      return "" + this.value;
   }

   byte typeValueKey() {
      return 74;
   }
}
