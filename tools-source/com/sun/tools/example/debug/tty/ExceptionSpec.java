package com.sun.tools.example.debug.tty;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;

class ExceptionSpec extends EventRequestSpec {
   private boolean notifyCaught;
   private boolean notifyUncaught;

   private ExceptionSpec(ReferenceTypeSpec var1) {
      this(var1, true, true);
   }

   ExceptionSpec(ReferenceTypeSpec var1, boolean var2, boolean var3) {
      super(var1);
      this.notifyCaught = var2;
      this.notifyUncaught = var3;
   }

   EventRequest resolveEventRequest(ReferenceType var1) {
      EventRequestManager var2 = var1.virtualMachine().eventRequestManager();
      ExceptionRequest var3 = var2.createExceptionRequest(var1, this.notifyCaught, this.notifyUncaught);
      var3.enable();
      return var3;
   }

   public boolean notifyCaught() {
      return this.notifyCaught;
   }

   public boolean notifyUncaught() {
      return this.notifyUncaught;
   }

   public int hashCode() {
      int var1 = 17;
      var1 = 37 * var1 + (this.notifyCaught() ? 0 : 1);
      var1 = 37 * var1 + (this.notifyUncaught() ? 0 : 1);
      var1 = 37 * var1 + this.refSpec.hashCode();
      return var1;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof ExceptionSpec) {
         ExceptionSpec var2 = (ExceptionSpec)var1;
         if (this.refSpec.equals(var2.refSpec) && this.notifyCaught() == var2.notifyCaught() && this.notifyUncaught() == var2.notifyUncaught()) {
            return true;
         }
      }

      return false;
   }

   public String toString() {
      String var1;
      if (this.notifyCaught && !this.notifyUncaught) {
         var1 = MessageOutput.format("exceptionSpec caught", this.refSpec.toString());
      } else if (this.notifyUncaught && !this.notifyCaught) {
         var1 = MessageOutput.format("exceptionSpec uncaught", this.refSpec.toString());
      } else {
         var1 = MessageOutput.format("exceptionSpec all", this.refSpec.toString());
      }

      return var1;
   }
}
