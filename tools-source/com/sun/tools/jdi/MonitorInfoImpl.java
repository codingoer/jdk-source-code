package com.sun.tools.jdi;

import com.sun.jdi.InternalException;
import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.MonitorInfo;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public class MonitorInfoImpl extends MirrorImpl implements MonitorInfo, ThreadListener {
   private boolean isValid = true;
   ObjectReference monitor;
   ThreadReference thread;
   int stack_depth;

   MonitorInfoImpl(VirtualMachine var1, ObjectReference var2, ThreadReferenceImpl var3, int var4) {
      super(var1);
      this.monitor = var2;
      this.thread = var3;
      this.stack_depth = var4;
      var3.addListener(this);
   }

   public boolean threadResumable(ThreadAction var1) {
      synchronized(this.vm.state()) {
         if (this.isValid) {
            this.isValid = false;
            return false;
         } else {
            throw new InternalException("Invalid stack frame thread listener");
         }
      }
   }

   private void validateMonitorInfo() {
      if (!this.isValid) {
         throw new InvalidStackFrameException("Thread has been resumed");
      }
   }

   public ObjectReference monitor() {
      this.validateMonitorInfo();
      return this.monitor;
   }

   public int stackDepth() {
      this.validateMonitorInfo();
      return this.stack_depth;
   }

   public ThreadReference thread() {
      this.validateMonitorInfo();
      return this.thread;
   }
}
