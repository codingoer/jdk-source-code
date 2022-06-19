package com.sun.tools.jdi;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public class BooleanValueImpl extends PrimitiveValueImpl implements BooleanValue {
   private boolean value;

   BooleanValueImpl(VirtualMachine var1, boolean var2) {
      super(var1);
      this.value = var2;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof BooleanValue) {
         return this.value == ((BooleanValue)var1).value() && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.intValue();
   }

   public Type type() {
      return this.vm.theBooleanType();
   }

   public boolean value() {
      return this.value;
   }

   public boolean booleanValue() {
      return this.value;
   }

   public byte byteValue() {
      return (byte)(this.value ? 1 : 0);
   }

   public char charValue() {
      return (char)(this.value ? 1 : 0);
   }

   public short shortValue() {
      return (short)(this.value ? 1 : 0);
   }

   public int intValue() {
      return this.value ? 1 : 0;
   }

   public long longValue() {
      return (long)(this.value ? 1 : 0);
   }

   public float floatValue() {
      return (float)(this.value ? 1.0 : 0.0);
   }

   public double doubleValue() {
      return this.value ? 1.0 : 0.0;
   }

   public String toString() {
      return "" + this.value;
   }

   byte typeValueKey() {
      return 90;
   }
}
