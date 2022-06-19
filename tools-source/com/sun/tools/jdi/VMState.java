package com.sun.tools.jdi;

import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class VMState {
   private final VirtualMachineImpl vm;
   private final List listeners = new ArrayList();
   private boolean notifyingListeners = false;
   private final Set pendingResumeCommands = Collections.synchronizedSet(new HashSet());
   private Cache cache = null;
   private static final Cache markerCache = new Cache();

   private void disableCache() {
      synchronized(this) {
         this.cache = null;
      }
   }

   private void enableCache() {
      synchronized(this) {
         this.cache = markerCache;
      }
   }

   private Cache getCache() {
      synchronized(this) {
         if (this.cache == markerCache) {
            this.cache = new Cache();
         }

         return this.cache;
      }
   }

   VMState(VirtualMachineImpl var1) {
      this.vm = var1;
   }

   boolean isSuspended() {
      return this.cache != null;
   }

   void notifyCommandComplete(int var1) {
      this.pendingResumeCommands.remove(var1);
   }

   synchronized void freeze() {
      if (this.cache == null && this.pendingResumeCommands.isEmpty()) {
         this.processVMAction(new VMAction(this.vm, 1));
         this.enableCache();
      }

   }

   synchronized PacketStream thawCommand(CommandSender var1) {
      PacketStream var2 = var1.send();
      this.pendingResumeCommands.add(var2.id());
      this.thaw();
      return var2;
   }

   void thaw() {
      this.thaw((ThreadReference)null);
   }

   synchronized void thaw(ThreadReference var1) {
      if (this.cache != null) {
         if ((this.vm.traceFlags & 16) != 0) {
            this.vm.printTrace("Clearing VM suspended cache");
         }

         this.disableCache();
      }

      this.processVMAction(new VMAction(this.vm, var1, 2));
   }

   private synchronized void processVMAction(VMAction var1) {
      if (!this.notifyingListeners) {
         this.notifyingListeners = true;
         Iterator var2 = this.listeners.iterator();

         while(var2.hasNext()) {
            WeakReference var3 = (WeakReference)var2.next();
            VMListener var4 = (VMListener)var3.get();
            if (var4 != null) {
               boolean var5 = true;
               switch (var1.id()) {
                  case 1:
                     var5 = var4.vmSuspended(var1);
                     break;
                  case 2:
                     var5 = var4.vmNotSuspended(var1);
               }

               if (!var5) {
                  var2.remove();
               }
            } else {
               var2.remove();
            }
         }

         this.notifyingListeners = false;
      }

   }

   synchronized void addListener(VMListener var1) {
      this.listeners.add(new WeakReference(var1));
   }

   synchronized boolean hasListener(VMListener var1) {
      return this.listeners.contains(var1);
   }

   synchronized void removeListener(VMListener var1) {
      Iterator var2 = this.listeners.iterator();

      while(var2.hasNext()) {
         WeakReference var3 = (WeakReference)var2.next();
         if (var1.equals(var3.get())) {
            var2.remove();
            break;
         }
      }

   }

   List allThreads() {
      List var1 = null;

      try {
         Cache var2 = this.getCache();
         if (var2 != null) {
            var1 = var2.threads;
         }

         if (var1 == null) {
            var1 = Arrays.asList((ThreadReference[])JDWP.VirtualMachine.AllThreads.process(this.vm).threads);
            if (var2 != null) {
               var2.threads = var1;
               if ((this.vm.traceFlags & 16) != 0) {
                  this.vm.printTrace("Caching all threads (count = " + var1.size() + ") while VM suspended");
               }
            }
         }

         return var1;
      } catch (JDWPException var3) {
         throw var3.toJDIException();
      }
   }

   List topLevelThreadGroups() {
      List var1 = null;

      try {
         Cache var2 = this.getCache();
         if (var2 != null) {
            var1 = var2.groups;
         }

         if (var1 == null) {
            var1 = Arrays.asList((ThreadGroupReference[])JDWP.VirtualMachine.TopLevelThreadGroups.process(this.vm).groups);
            if (var2 != null) {
               var2.groups = var1;
               if ((this.vm.traceFlags & 16) != 0) {
                  this.vm.printTrace("Caching top level thread groups (count = " + var1.size() + ") while VM suspended");
               }
            }
         }

         return var1;
      } catch (JDWPException var3) {
         throw var3.toJDIException();
      }
   }

   private static class Cache {
      List groups;
      List threads;

      private Cache() {
         this.groups = null;
         this.threads = null;
      }

      // $FF: synthetic method
      Cache(Object var1) {
         this();
      }
   }
}
