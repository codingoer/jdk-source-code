package com.sun.tools.jdi;

import com.sun.jdi.ByteValue;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class ByteValueImpl extends PrimitiveValueImpl implements ByteValue {
   private byte value;

   ByteValueImpl(VirtualMachine var1, byte var2) {
      super(var1);
      this.value = var2;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof ByteValue) {
         return this.value == ((ByteValue)var1).value() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.intValue();
   }

   public int compareTo(ByteValue var1) {
      byte var2 = var1.value();
      return this.value() - var2;
   }

   public Type type() {
      return this.vm.theByteType();
   }

   public byte value() {
      return this.value;
   }

   public boolean booleanValue() {
      return this.value != 0;
   }

   public byte byteValue() {
      return this.value;
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
      return 66;
   }
}
