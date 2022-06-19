package com.sun.tools.jdi;

import com.sun.jdi.ClassNotPreparedException;
import com.sun.jdi.InconsistentDebugInfoException;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VMOutOfMemoryException;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventSet;

public class InternalEventHandler implements Runnable {
   EventQueueImpl queue;
   VirtualMachineImpl vm;

   InternalEventHandler(VirtualMachineImpl var1, EventQueueImpl var2) {
      this.vm = var1;
      this.queue = var2;
      Thread var3 = new Thread(var1.threadGroupForJDI(), this, "JDI Internal Event Handler");
      var3.setDaemon(true);
      var3.start();
   }

   public void run() {
      if ((this.vm.traceFlags & 4) != 0) {
         this.vm.printTrace("Internal event handler running");
      }

      try {
         while(true) {
            while(true) {
               try {
                  EventSet var1 = this.queue.removeInternal();
                  EventIterator var2 = var1.eventIterator();

                  while(var2.hasNext()) {
                     Event var3 = var2.nextEvent();
                     if (var3 instanceof ClassUnloadEvent) {
                        ClassUnloadEvent var4 = (ClassUnloadEvent)var3;
                        this.vm.removeReferenceType(var4.classSignature());
                        if ((this.vm.traceFlags & 4) != 0) {
                           this.vm.printTrace("Handled Unload Event for " + var4.classSignature());
                        }
                     } else if (var3 instanceof ClassPrepareEvent) {
                        ClassPrepareEvent var11 = (ClassPrepareEvent)var3;
                        ((ReferenceTypeImpl)var11.referenceType()).markPrepared();
                        if ((this.vm.traceFlags & 4) != 0) {
                           this.vm.printTrace("Handled Prepare Event for " + var11.referenceType().name());
                        }
                     }
                  }
               } catch (VMOutOfMemoryException var5) {
                  var5.printStackTrace();
               } catch (InconsistentDebugInfoException var6) {
                  var6.printStackTrace();
               } catch (ObjectCollectedException var7) {
                  var7.printStackTrace();
               } catch (ClassNotPreparedException var8) {
                  var8.printStackTrace();
               }
            }
         }
      } catch (InterruptedException var9) {
      } catch (VMDisconnectedException var10) {
      }

      if ((this.vm.traceFlags & 4) != 0) {
         this.vm.printTrace("Internal event handler exiting");
      }

   }
}
