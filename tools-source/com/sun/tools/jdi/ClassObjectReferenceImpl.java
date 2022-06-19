package com.sun.tools.jdi;

import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;

public class ClassObjectReferenceImpl extends ObjectReferenceImpl implements ClassObjectReference {
   private ReferenceType reflectedType;

   ClassObjectReferenceImpl(VirtualMachine var1, long var2) {
      super(var1, var2);
   }

   public ReferenceType reflectedType() {
      if (this.reflectedType == null) {
         try {
            JDWP.ClassObjectReference.ReflectedType var1 = JDWP.ClassObjectReference.ReflectedType.process(this.vm, this);
            this.reflectedType = this.vm.referenceType(var1.typeID, var1.refTypeTag);
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.reflectedType;
   }

   byte typeValueKey() {
      return 99;
   }

   public String toString() {
      return "instance of " + this.referenceType().name() + "(reflected class=" + this.reflectedType().name() + ", id=" + this.uniqueID() + ")";
   }
}
