package com.sun.tools.jdi;

import com.sun.jdi.Mirror;
import com.sun.jdi.VMMismatchException;
import com.sun.jdi.VirtualMachine;
import java.util.Collection;
import java.util.Iterator;

abstract class MirrorImpl implements Mirror {
   protected VirtualMachineImpl vm;

   MirrorImpl(VirtualMachine var1) {
      this.vm = (VirtualMachineImpl)var1;
   }

   public VirtualMachine virtualMachine() {
      return this.vm;
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Mirror) {
         Mirror var2 = (Mirror)var1;
         return this.vm.equals(var2.virtualMachine());
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.vm.hashCode();
   }

   void validateMirror(Mirror var1) {
      if (!this.vm.equals(var1.virtualMachine())) {
         throw new VMMismatchException(var1.toString());
      }
   }

   void validateMirrorOrNull(Mirror var1) {
      if (var1 != null && !this.vm.equals(var1.virtualMachine())) {
         throw new VMMismatchException(var1.toString());
      }
   }

   void validateMirrors(Collection var1) {
      Iterator var2 = var1.iterator();

      MirrorImpl var3;
      do {
         if (!var2.hasNext()) {
            return;
         }

         var3 = (MirrorImpl)var2.next();
      } while(this.vm.equals(var3.vm));

      throw new VMMismatchException(var3.toString());
   }

   void validateMirrorsOrNulls(Collection var1) {
      Iterator var2 = var1.iterator();

      MirrorImpl var3;
      do {
         if (!var2.hasNext()) {
            return;
         }

         var3 = (MirrorImpl)var2.next();
      } while(var3 == null || this.vm.equals(var3.vm));

      throw new VMMismatchException(var3.toString());
   }
}
