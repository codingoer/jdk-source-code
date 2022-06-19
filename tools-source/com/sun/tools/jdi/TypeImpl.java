package com.sun.tools.jdi;

import com.sun.jdi.Type;
import com.sun.jdi.VirtualMachine;

public abstract class TypeImpl extends MirrorImpl implements Type {
   private String myName = null;

   TypeImpl(VirtualMachine var1) {
      super(var1);
   }

   public abstract String signature();

   public String name() {
      if (this.myName == null) {
         JNITypeParser var1 = new JNITypeParser(this.signature());
         this.myName = var1.typeName();
      }

      return this.myName;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Type) {
         Type var2 = (Type)var1;
         return this.signature().equals(var2.signature()) && super.equals(var1);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.signature().hashCode();
   }
}
