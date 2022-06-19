package com.sun.tools.jdi;

import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.ShortValue;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class ShortValueImpl extends PrimitiveValueImpl implements ShortValue {
   private short value;

   ShortValueImpl(VirtualMachine var1, short var2) {
      super(var1);
      this.value = var2;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof ShortValue) {
         return this.value == ((ShortValue)var1).value() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.intValue();
   }

   public int compareTo(ShortValue var1) {
      short var2 = var1.value();
      return this.value() - var2;
   }

   public Type type() {
      return this.vm.theShortType();
   }

   public short value() {
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
      return this.value;
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
      if (this.value <= '\uffff' && this.value >= 0) {
         return super.checkedCharValue();
      } else {
         throw new InvalidTypeException("Can't convert " + this.value + " to char");
      }
   }

   public String toString() {
      return "" + this.value;
   }

   byte typeValueKey() {
      return 83;
   }
}
