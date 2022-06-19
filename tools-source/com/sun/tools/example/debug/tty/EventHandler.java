package com.sun.tools.example.debug.tty;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;

public class EventHandler implements Runnable {
   EventNotifier notifier;
   Thread thread;
   volatile boolean connected = true;
   boolean completed = false;
   String shutdownMessageKey;
   boolean stopOnVMStart;
   private boolean vmDied = false;

   EventHandler(EventNotifier var1, boolean var2) {
      this.notifier = var1;
      this.stopOnVMStart = var2;
      this.thread = new Thread(this, "event-handler");
      this.thread.start();
   }

   synchronized void shutdown() {
      this.connected = false;
      this.thread.interrupt();

      while(!this.completed) {
         try {
            this.wait();
         } catch (InterruptedException var2) {
         }
      }

   }

   public void run() {
      EventQueue var1 = Env.vm().eventQueue();

      while(this.connected) {
         try {
            EventSet var2 = var1.remove();
            boolean var3 = false;

            for(EventIterator var4 = var2.eventIterator(); var4.hasNext(); var3 |= !this.handleEvent(var4.nextEvent())) {
            }

            if (var3) {
               var2.resume();
            } else if (var2.suspendPolicy() == 2) {
               this.setCurrentThread(var2);
               this.notifier.vmInterrupted();
            }
         } catch (InterruptedException var7) {
         } catch (VMDisconnectedException var8) {
            this.handleDisconnectedException();
            break;
         }
      }

      synchronized(this) {
         this.completed = true;
         this.notifyAll();
      }
   }

   private boolean handleEvent(Event var1) {
      this.notifier.receivedEvent(var1);
      if (var1 instanceof ExceptionEvent) {
         return this.exceptionEvent(var1);
      } else if (var1 instanceof BreakpointEvent) {
         return this.breakpointEvent(var1);
      } else if (var1 instanceof WatchpointEvent) {
         return this.fieldWatchEvent(var1);
      } else if (var1 instanceof StepEvent) {
         return this.stepEvent(var1);
      } else if (var1 instanceof MethodEntryEvent) {
         return this.methodEntryEvent(var1);
      } else if (var1 instanceof MethodExitEvent) {
         return this.methodExitEvent(var1);
      } else if (var1 instanceof ClassPrepareEvent) {
         return this.classPrepareEvent(var1);
      } else if (var1 instanceof ClassUnloadEvent) {
         return this.classUnloadEvent(var1);
      } else if (var1 instanceof ThreadStartEvent) {
         return this.threadStartEvent(var1);
      } else if (var1 instanceof ThreadDeathEvent) {
         return this.threadDeathEvent(var1);
      } else {
         return var1 instanceof VMStartEvent ? this.vmStartEvent(var1) : this.handleExitEvent(var1);
      }
   }

   private boolean handleExitEvent(Event var1) {
      if (var1 instanceof VMDeathEvent) {
         this.vmDied = true;
         return this.vmDeathEvent(var1);
      } else if (var1 instanceof VMDisconnectEvent) {
         this.connected = false;
         if (!this.vmDied) {
            this.vmDisconnectEvent(var1);
         }

         ((TTY)this.notifier).setShuttingDown(true);
         Env.shutdown(this.shutdownMessageKey);
         return false;
      } else {
         throw new InternalError(MessageOutput.format("Unexpected event type", new Object[]{var1.getClass()}));
      }
   }

   synchronized void handleDisconnectedException() {
      EventQueue var1 = Env.vm().eventQueue();

      while(this.connected) {
         try {
            EventSet var2 = var1.remove();
            EventIterator var3 = var2.eventIterator();

            while(var3.hasNext()) {
               this.handleExitEvent((Event)var3.next());
            }
         } catch (InterruptedException var4) {
         } catch (InternalError var5) {
         }
      }

   }

   private ThreadReference eventThread(Event var1) {
      if (var1 instanceof ClassPrepareEvent) {
         return ((ClassPrepareEvent)var1).thread();
      } else if (var1 instanceof LocatableEvent) {
         return ((LocatableEvent)var1).thread();
      } else if (var1 instanceof ThreadStartEvent) {
         return ((ThreadStartEvent)var1).thread();
      } else if (var1 instanceof ThreadDeathEvent) {
         return ((ThreadDeathEvent)var1).thread();
      } else {
         return var1 instanceof VMStartEvent ? ((VMStartEvent)var1).thread() : null;
      }
   }

   private void setCurrentThread(EventSet var1) {
      ThreadReference var2;
      if (var1.size() > 0) {
         Event var3 = (Event)var1.iterator().next();
         var2 = this.eventThread(var3);
      } else {
         var2 = null;
      }

      this.setCurrentThread(var2);
   }

   private void setCurrentThread(ThreadReference var1) {
      ThreadInfo.invalidateAll();
      ThreadInfo.setCurrentThread(var1);
   }

   private boolean vmStartEvent(Event var1) {
      VMStartEvent var2 = (VMStartEvent)var1;
      this.notifier.vmStartEvent(var2);
      return this.stopOnVMStart;
   }

   private boolean breakpointEvent(Event var1) {
      BreakpointEvent var2 = (BreakpointEvent)var1;
      this.notifier.breakpointEvent(var2);
      return true;
   }

   private boolean methodEntryEvent(Event var1) {
      MethodEntryEvent var2 = (MethodEntryEvent)var1;
      this.notifier.methodEntryEvent(var2);
      return true;
   }

   private boolean methodExitEvent(Event var1) {
      MethodExitEvent var2 = (MethodExitEvent)var1;
      return this.notifier.methodExitEvent(var2);
   }

   private boolean fieldWatchEvent(Event var1) {
      WatchpointEvent var2 = (WatchpointEvent)var1;
      this.notifier.fieldWatchEvent(var2);
      return true;
   }

   private boolean stepEvent(Event var1) {
      StepEvent var2 = (StepEvent)var1;
      this.notifier.stepEvent(var2);
      return true;
   }

   private boolean classPrepareEvent(Event var1) {
      ClassPrepareEvent var2 = (ClassPrepareEvent)var1;
      this.notifier.classPrepareEvent(var2);
      if (!Env.specList.resolve(var2)) {
         MessageOutput.lnprint("Stopping due to deferred breakpoint errors.");
         return true;
      } else {
         return false;
      }
   }

   private boolean classUnloadEvent(Event var1) {
      ClassUnloadEvent var2 = (ClassUnloadEvent)var1;
      this.notifier.classUnloadEvent(var2);
      return false;
   }

   private boolean exceptionEvent(Event var1) {
      ExceptionEvent var2 = (ExceptionEvent)var1;
      this.notifier.exceptionEvent(var2);
      return true;
   }

   private boolean threadDeathEvent(Event var1) {
      ThreadDeathEvent var2 = (ThreadDeathEvent)var1;
      ThreadInfo.removeThread(var2.thread());
      return false;
   }

   private boolean threadStartEvent(Event var1) {
      ThreadStartEvent var2 = (ThreadStartEvent)var1;
      ThreadInfo.addThread(var2.thread());
      this.notifier.threadStartEvent(var2);
      return false;
   }

   public boolean vmDeathEvent(Event var1) {
      this.shutdownMessageKey = "The application exited";
      this.notifier.vmDeathEvent((VMDeathEvent)var1);
      return false;
   }

   public boolean vmDisconnectEvent(Event var1) {
      this.shutdownMessageKey = "The application has been disconnected";
      this.notifier.vmDisconnectEvent((VMDisconnectEvent)var1);
      return false;
   }
}
