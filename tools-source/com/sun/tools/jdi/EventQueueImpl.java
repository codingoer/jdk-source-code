package com.sun.tools.jdi;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import java.util.LinkedList;

public class EventQueueImpl extends MirrorImpl implements EventQueue {
   LinkedList eventSets = new LinkedList();
   TargetVM target;
   boolean closed = false;

   EventQueueImpl(VirtualMachine var1, TargetVM var2) {
      super(var1);
      this.target = var2;
      var2.addEventQueue(this);
   }

   public boolean equals(Object var1) {
      return this == var1;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   synchronized void enqueue(EventSet var1) {
      this.eventSets.add(var1);
      this.notifyAll();
   }

   synchronized int size() {
      return this.eventSets.size();
   }

   synchronized void close() {
      if (!this.closed) {
         this.closed = true;
         this.enqueue(new EventSetImpl(this.vm, (byte)100));
      }

   }

   public EventSet remove() throws InterruptedException {
      return this.remove(0L);
   }

   public EventSet remove(long var1) throws InterruptedException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Timeout cannot be negative");
      } else {
         EventSet var3;
         do {
            EventSetImpl var4 = this.removeUnfiltered(var1);
            if (var4 == null) {
               var3 = null;
               break;
            }

            var3 = var4.userFilter();
         } while(var3.isEmpty());

         if (var3 != null && var3.suspendPolicy() == 2) {
            this.vm.notifySuspend();
         }

         return var3;
      }
   }

   EventSet removeInternal() throws InterruptedException {
      EventSet var1;
      do {
         var1 = this.removeUnfiltered(0L).internalFilter();
      } while(var1 == null || var1.isEmpty());

      return var1;
   }

   private TimerThread startTimerThread(long var1) {
      TimerThread var3 = new TimerThread(var1);
      var3.setDaemon(true);
      var3.start();
      return var3;
   }

   private boolean shouldWait(TimerThread var1) {
      return !this.closed && this.eventSets.isEmpty() && (var1 == null || !var1.timedOut());
   }

   private EventSetImpl removeUnfiltered(long var1) throws InterruptedException {
      EventSetImpl var3 = null;
      this.vm.waitInitCompletion();
      synchronized(this) {
         if (!this.eventSets.isEmpty()) {
            var3 = (EventSetImpl)this.eventSets.removeFirst();
         } else {
            TimerThread var5 = null;

            try {
               if (var1 > 0L) {
                  var5 = this.startTimerThread(var1);
               }

               while(this.shouldWait(var5)) {
                  this.wait();
               }
            } finally {
               if (var5 != null && !var5.timedOut()) {
                  var5.interrupt();
               }

            }

            if (this.eventSets.isEmpty()) {
               if (this.closed) {
                  throw new VMDisconnectedException();
               }
            } else {
               var3 = (EventSetImpl)this.eventSets.removeFirst();
            }
         }
      }

      if (var3 != null) {
         this.target.notifyDequeueEventSet();
         var3.build();
      }

      return var3;
   }

   private class TimerThread extends Thread {
      private boolean timedOut = false;
      private long timeout;

      TimerThread(long var2) {
         super(EventQueueImpl.this.vm.threadGroupForJDI(), "JDI Event Queue Timer");
         this.timeout = var2;
      }

      boolean timedOut() {
         return this.timedOut;
      }

      public void run() {
         try {
            Thread.sleep(this.timeout);
            EventQueueImpl var1 = EventQueueImpl.this;
            synchronized(var1) {
               this.timedOut = true;
               var1.notifyAll();
            }
         } catch (InterruptedException var5) {
         }

      }
   }
}
