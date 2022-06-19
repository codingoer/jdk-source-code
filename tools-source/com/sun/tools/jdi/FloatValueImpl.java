package com.sun.tools.jdi;

import com.sun.jdi.FloatValue;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class FloatValueImpl extends PrimitiveValueImpl implements FloatValue {
   private float value;

   FloatValueImpl(VirtualMachine var1, float var2) {
      super(var1);
      this.value = var2;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof FloatValue) {
         return this.value == ((FloatValue)var1).value() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.intValue();
   }

   public int compareTo(FloatValue var1) {
      float var2 = var1.value();
      if (this.value() < var2) {
         return -1;
      } else {
         return this.value() == var2 ? 0 : 1;
      }
   }

   public Type type() {
      return this.vm.theFloatType();
   }

   public float value() {
      return this.value;
   }

   public boolean booleanValue() {
      return (double)this.value != 0.0;
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
      return this.value;
   }

   public double doubleValue() {
      return (double)this.value;
   }

   byte checkedByteValue() throws InvalidTypeException {
      if (!(this.value > 127.0F) && !(this.value < -128.0F)) {
         return super.checkedByteValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to byte");
      }
   }

   char checkedCharValue() throws InvalidTypeException {
      if (!(this.value > 65535.0F) && !(this.value < 0.0F)) {
         return super.checkedCharValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to char");
      }
   }

   short checkedShortValue() throws InvalidTypeException {
      if (!(this.value > 32767.0F) && !(this.value < -32768.0F)) {
         return super.checkedShortValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to short");
      }
   }

   int checkedIntValue() throws InvalidTypeException {
      int var1 = (int)this.value;
      if ((float)var1 != this.value) {
         throw new InvalidTypeException("Can't convert " + this.value + " to int");
      } else {
         return super.checkedIntValue();
      }
   }

   long checkedLongValue() throws InvalidTypeException {
      long var1 = (long)this.value;
      if ((float)var1 != this.value) {
         throw new InvalidTypeException("Can't convert " + this.value + " to long");
      } else {
         return super.checkedLongValue();
      }
   }

   public String toString() {
      return "" + this.value;
   }

   byte typeValueKey() {
      return 70;
   }
}
