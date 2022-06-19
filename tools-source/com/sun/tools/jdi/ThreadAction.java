package com.sun.tools.jdi;

import com.sun.jdi.ThreadReference;
import java.util.EventObject;

class ThreadAction extends EventObject {
   private static final long serialVersionUID = 5690763191100515283L;
   static final int THREAD_RESUMABLE = 2;
   int id;

   ThreadAction(ThreadReference var1, int var2) {
      super(var1);
      this.id = var2;
   }

   ThreadReference thread() {
      return (ThreadReference)this.getSource();
   }

   int id() {
      return this.id;
   }
}
