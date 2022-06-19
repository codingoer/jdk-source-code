package com.sun.tools.example.debug.tty;

import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ModificationWatchpointRequest;

class ModificationWatchpointSpec extends WatchpointSpec {
   ModificationWatchpointSpec(ReferenceTypeSpec var1, String var2) throws MalformedMemberNameException {
      super(var1, var2);
   }

   EventRequest resolveEventRequest(ReferenceType var1) throws NoSuchFieldException {
      Field var2 = var1.fieldByName(this.fieldId);
      EventRequestManager var3 = var1.virtualMachine().eventRequestManager();
      ModificationWatchpointRequest var4 = var3.createModificationWatchpointRequest(var2);
      var4.setSuspendPolicy(this.suspendPolicy);
      var4.enable();
      return var4;
   }

   public String toString() {
      return MessageOutput.format("watch modification of", new Object[]{this.refSpec.toString(), this.fieldId});
   }
}
