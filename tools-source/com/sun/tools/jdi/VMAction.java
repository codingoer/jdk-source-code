package com.sun.tools.jdi;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import java.util.EventObject;

class VMAction extends EventObject {
   private static final long serialVersionUID = -1701944679310296090L;
   static final int VM_SUSPENDED = 1;
   static final int VM_NOT_SUSPENDED = 2;
   int id;
   ThreadReference resumingThread;

   VMAction(VirtualMachine var1, int var2) {
      this(var1, (ThreadReference)null, var2);
   }

   VMAction(VirtualMachine var1, ThreadReference var2, int var3) {
      super(var1);
      this.id = var3;
      this.resumingThread = var2;
   }

   VirtualMachine vm() {
      return (VirtualMachine)this.getSource();
   }

   int id() {
      return this.id;
   }

   ThreadReference resumingThread() {
      return this.resumingThread;
   }
}
