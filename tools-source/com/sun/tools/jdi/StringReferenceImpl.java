package com.sun.tools.jdi;

import com.sun.jdi.StringReference;
import com.sun.jdi.VirtualMachine;

public class StringReferenceImpl extends ObjectReferenceImpl implements StringReference {
   private String value;

   StringReferenceImpl(VirtualMachine var1, long var2) {
      super(var1, var2);
   }

   public String value() {
      if (this.value == null) {
         try {
            this.value = JDWP.StringReference.Value.process(this.vm, this).stringValue;
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.value;
   }

   public String toString() {
      return "\"" + this.value() + "\"";
   }

   byte typeValueKey() {
      return 115;
   }
}
