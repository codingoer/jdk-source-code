package com.sun.tools.jdi;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.event.EventSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TargetVM implements Runnable {
   private Map waitingQueue = new HashMap(32, 0.75F);
   private volatile boolean shouldListen = true;
   private List eventQueues = Collections.synchronizedList(new ArrayList(2));
   private VirtualMachineImpl vm;
   private Connection connection;
   private Thread readerThread;
   private EventController eventController = null;
   private boolean eventsHeld = false;
   private static final int OVERLOADED_QUEUE = 2000;
   private static final int UNDERLOADED_QUEUE = 100;

   TargetVM(VirtualMachineImpl var1, Connection var2) {
      this.vm = var1;
      this.connection = var2;
      this.readerThread = new Thread(var1.threadGroupForJDI(), this, "JDI Target VM Interface");
      this.readerThread.setDaemon(true);
   }

   void start() {
      this.readerThread.start();
   }

   private void dumpPacket(Packet var1, boolean var2) {
      String var3 = var2 ? "Sending" : "Receiving";
      if (var2) {
         this.vm.printTrace(var3 + " Command. id=" + var1.id + ", length=" + var1.data.length + ", commandSet=" + var1.cmdSet + ", command=" + var1.cmd + ", flags=" + var1.flags);
      } else {
         String var4 = (var1.flags & 128) != 0 ? "Reply" : "Event";
         this.vm.printTrace(var3 + " " + var4 + ". id=" + var1.id + ", length=" + var1.data.length + ", errorCode=" + var1.errorCode + ", flags=" + var1.flags);
      }

      StringBuffer var8 = new StringBuffer(80);
      var8.append("0000: ");

      for(int var5 = 0; var5 < var1.data.length; ++var5) {
         int var6;
         if (var5 > 0 && var5 % 16 == 0) {
            this.vm.printTrace(var8.toString());
            var8.setLength(0);
            var8.append(String.valueOf(var5));
            var8.append(": ");
            var6 = var8.length();

            for(int var7 = 0; var7 < 6 - var6; ++var7) {
               var8.insert(0, '0');
            }
         }

         var6 = 255 & var1.data[var5];
         String var9 = Integer.toHexString(var6);
         if (var9.length() == 1) {
            var8.append('0');
         }

         var8.append(var9);
         var8.append(' ');
      }

      if (var8.length() > 6) {
         this.vm.printTrace(var8.toString());
      }

   }

   public void run() {
      if ((this.vm.traceFlags & 1) != 0) {
         this.vm.printTrace("Target VM interface thread running");
      }

      Packet var1 = null;

      while(this.shouldListen) {
         boolean var4 = false;

         try {
            byte[] var5 = this.connection.readPacket();
            if (var5.length == 0) {
               var4 = true;
            }

            var1 = Packet.fromByteArray(var5);
         } catch (IOException var14) {
            var4 = true;
         }

         if (var4) {
            this.shouldListen = false;

            try {
               this.connection.close();
            } catch (IOException var12) {
            }
            break;
         }

         if ((this.vm.traceFlags & VirtualMachineImpl.TRACE_RAW_RECEIVES) != 0) {
            this.dumpPacket(var1, false);
         }

         if ((var1.flags & 128) == 0) {
            this.handleVMCommand(var1);
         } else {
            this.vm.state().notifyCommandComplete(var1.id);
            String var3 = String.valueOf(var1.id);
            Packet var2;
            synchronized(this.waitingQueue) {
               var2 = (Packet)this.waitingQueue.get(var3);
               if (var2 != null) {
                  this.waitingQueue.remove(var3);
               }
            }

            if (var2 == null) {
               System.err.println("Recieved reply with no sender!");
            } else {
               var2.errorCode = var1.errorCode;
               var2.data = var1.data;
               var2.replied = true;
               synchronized(var2) {
                  var2.notify();
               }
            }
         }
      }

      this.vm.vmManager.disposeVirtualMachine(this.vm);
      if (this.eventController != null) {
         this.eventController.release();
      }

      Iterator var18;
      synchronized(this.eventQueues) {
         var18 = this.eventQueues.iterator();

         while(true) {
            if (!var18.hasNext()) {
               break;
            }

            ((EventQueueImpl)var18.next()).close();
         }
      }

      synchronized(this.waitingQueue) {
         var18 = this.waitingQueue.values().iterator();

         while(var18.hasNext()) {
            Packet var6 = (Packet)var18.next();
            synchronized(var6) {
               var6.notify();
            }
         }

         this.waitingQueue.clear();
      }

      if ((this.vm.traceFlags & 1) != 0) {
         this.vm.printTrace("Target VM interface thread exiting");
      }

   }

   protected void handleVMCommand(Packet var1) {
      switch (var1.cmdSet) {
         case 64:
            this.handleEventCmdSet(var1);
            return;
         default:
            System.err.println("Ignoring cmd " + var1.id + "/" + var1.cmdSet + "/" + var1.cmd + " from the VM");
      }
   }

   protected void handleEventCmdSet(Packet var1) {
      EventSetImpl var2 = new EventSetImpl(this.vm, var1);
      if (var2 != null) {
         this.queueEventSet(var2);
      }

   }

   private EventController eventController() {
      if (this.eventController == null) {
         this.eventController = new EventController();
      }

      return this.eventController;
   }

   private synchronized void controlEventFlow(int var1) {
      if (!this.eventsHeld && var1 > 2000) {
         this.eventController().hold();
         this.eventsHeld = true;
      } else if (this.eventsHeld && var1 < 100) {
         this.eventController().release();
         this.eventsHeld = false;
      }

   }

   void notifyDequeueEventSet() {
      int var1 = 0;
      synchronized(this.eventQueues) {
         Iterator var3 = this.eventQueues.iterator();

         while(true) {
            if (!var3.hasNext()) {
               break;
            }

            EventQueueImpl var4 = (EventQueueImpl)var3.next();
            var1 = Math.max(var1, var4.size());
         }
      }

      this.controlEventFlow(var1);
   }

   private void queueEventSet(EventSet var1) {
      int var2 = 0;
      synchronized(this.eventQueues) {
         Iterator var4 = this.eventQueues.iterator();

         while(true) {
            if (!var4.hasNext()) {
               break;
            }

            EventQueueImpl var5 = (EventQueueImpl)var4.next();
            var5.enqueue(var1);
            var2 = Math.max(var2, var5.size());
         }
      }

      this.controlEventFlow(var2);
   }

   void send(Packet var1) {
      String var2 = String.valueOf(var1.id);
      synchronized(this.waitingQueue) {
         this.waitingQueue.put(var2, var1);
      }

      if ((this.vm.traceFlags & VirtualMachineImpl.TRACE_RAW_SENDS) != 0) {
         this.dumpPacket(var1, true);
      }

      try {
         this.connection.writePacket(var1.toByteArray());
      } catch (IOException var5) {
         throw new VMDisconnectedException(var5.getMessage());
      }
   }

   void waitForReply(Packet var1) {
      synchronized(var1) {
         while(!var1.replied && this.shouldListen) {
            try {
               var1.wait();
            } catch (InterruptedException var5) {
            }
         }

         if (!var1.replied) {
            throw new VMDisconnectedException();
         }
      }
   }

   void addEventQueue(EventQueueImpl var1) {
      if ((this.vm.traceFlags & 4) != 0) {
         this.vm.printTrace("New event queue added");
      }

      this.eventQueues.add(var1);
   }

   void stopListening() {
      if ((this.vm.traceFlags & 4) != 0) {
         this.vm.printTrace("Target VM i/f closing event queues");
      }

      this.shouldListen = false;

      try {
         this.connection.close();
      } catch (IOException var2) {
      }

   }

   private class EventController extends Thread {
      int controlRequest = 0;

      EventController() {
         super(TargetVM.this.vm.threadGroupForJDI(), "JDI Event Control Thread");
         this.setDaemon(true);
         this.setPriority(7);
         super.start();
      }

      synchronized void hold() {
         ++this.controlRequest;
         this.notifyAll();
      }

      synchronized void release() {
         --this.controlRequest;
         this.notifyAll();
      }

      public void run() {
         while(true) {
            int var1;
            synchronized(this) {
               while(this.controlRequest == 0) {
                  try {
                     this.wait();
                  } catch (InterruptedException var6) {
                  }

                  if (!TargetVM.this.shouldListen) {
                     return;
                  }
               }

               var1 = this.controlRequest;
               this.controlRequest = 0;
            }

            try {
               if (var1 > 0) {
                  JDWP.VirtualMachine.HoldEvents.process(TargetVM.this.vm);
               } else {
                  JDWP.VirtualMachine.ReleaseEvents.process(TargetVM.this.vm);
               }
            } catch (JDWPException var5) {
               var5.toJDIException().printStackTrace(System.err);
            }
         }
      }
   }
}
