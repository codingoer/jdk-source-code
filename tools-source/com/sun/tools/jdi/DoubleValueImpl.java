package com.sun.tools.jdi;

import com.sun.jdi.DoubleValue;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class DoubleValueImpl extends PrimitiveValueImpl implements DoubleValue {
   private double value;

   DoubleValueImpl(VirtualMachine var1, double var2) {
      super(var1);
      this.value = var2;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof DoubleValue) {
         return this.value == ((DoubleValue)var1).value() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int compareTo(DoubleValue var1) {
      double var2 = var1.value();
      if (this.value() < var2) {
         return -1;
      } else {
         return this.value() == var2 ? 0 : 1;
      }
   }

   public int hashCode() {
      return this.intValue();
   }

   public Type type() {
      return this.vm.theDoubleType();
   }

   public double value() {
      return this.value;
   }

   public boolean booleanValue() {
      return this.value != 0.0;
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
      return (long)this.value;
   }

   public float floatValue() {
      return (float)this.value;
   }

   public double doubleValue() {
      return this.value;
   }

   byte checkedByteValue() throws InvalidTypeException {
      if (!(this.value > 127.0) && !(this.value < -128.0)) {
         return super.checkedByteValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to byte");
      }
   }

   char checkedCharValue() throws InvalidTypeException {
      if (!(this.value > 65535.0) && !(this.value < 0.0)) {
         return super.checkedCharValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to char");
      }
   }

   short checkedShortValue() throws InvalidTypeException {
      if (!(this.value > 32767.0) && !(this.value < -32768.0)) {
         return super.checkedShortValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to short");
      }
   }

   int checkedIntValue() throws InvalidTypeException {
      if (!(this.value > 2.147483647E9) && !(this.value < -2.147483648E9)) {
         return super.checkedIntValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to int");
      }
   }

   long checkedLongValue() throws InvalidTypeException {
      long var1 = (long)this.value;
      if ((double)var1 != this.value) {
         throw new InvalidTypeException("Can't convert " + this.value + " to long");
      } else {
         return super.checkedLongValue();
      }
   }

   float checkedFloatValue() throws InvalidTypeException {
      float var1 = (float)this.value;
      if ((double)var1 != this.value) {
         throw new InvalidTypeException("Can't convert " + this.value + " to float");
      } else {
         return super.checkedFloatValue();
      }
   }

   public String toString() {
      return "" + this.value;
   }

   byte typeValueKey() {
      return 68;
   }
}
