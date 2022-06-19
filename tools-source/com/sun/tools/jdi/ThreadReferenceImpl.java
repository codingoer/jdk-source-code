package com.sun.tools.jdi;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InternalException;
import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.Location;
import com.sun.jdi.NativeMethodException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ThreadReferenceImpl extends ObjectReferenceImpl implements ThreadReference, VMListener {
   static final int SUSPEND_STATUS_SUSPENDED = 1;
   static final int SUSPEND_STATUS_BREAK = 2;
   private int suspendedZombieCount = 0;
   private ThreadGroupReference threadGroup;
   private LocalCache localCache;
   private List listeners = new ArrayList();

   private void resetLocalCache() {
      this.localCache = new LocalCache();
   }

   protected ObjectReferenceImpl.Cache newCache() {
      return new Cache();
   }

   ThreadReferenceImpl(VirtualMachine var1, long var2) {
      super(var1, var2);
      this.resetLocalCache();
      this.vm.state().addListener(this);
   }

   protected String description() {
      return "ThreadReference " + this.uniqueID();
   }

   public boolean vmNotSuspended(VMAction var1) {
      if (var1.resumingThread() == null) {
         synchronized(this.vm.state()) {
            this.processThreadAction(new ThreadAction(this, 2));
         }
      }

      return super.vmNotSuspended(var1);
   }

   public String name() {
      String var1 = null;

      try {
         Cache var2 = (Cache)this.getCache();
         if (var2 != null) {
            var1 = var2.name;
         }

         if (var1 == null) {
            var1 = JDWP.ThreadReference.Name.process(this.vm, this).threadName;
            if (var2 != null) {
               var2.name = var1;
            }
         }

         return var1;
      } catch (JDWPException var3) {
         throw var3.toJDIException();
      }
   }

   PacketStream sendResumingCommand(CommandSender var1) {
      synchronized(this.vm.state()) {
         this.processThreadAction(new ThreadAction(this, 2));
         return var1.send();
      }
   }

   public void suspend() {
      try {
         JDWP.ThreadReference.Suspend.process(this.vm, this);
      } catch (JDWPException var2) {
         throw var2.toJDIException();
      }
   }

   public void resume() {
      if (this.suspendedZombieCount > 0) {
         --this.suspendedZombieCount;
      } else {
         PacketStream var1;
         synchronized(this.vm.state()) {
            this.processThreadAction(new ThreadAction(this, 2));
            var1 = JDWP.ThreadReference.Resume.enqueueCommand(this.vm, this);
         }

         try {
            JDWP.ThreadReference.Resume.waitForReply(this.vm, var1);
         } catch (JDWPException var4) {
            throw var4.toJDIException();
         }
      }
   }

   public int suspendCount() {
      if (this.suspendedZombieCount > 0) {
         return this.suspendedZombieCount;
      } else {
         try {
            return JDWP.ThreadReference.SuspendCount.process(this.vm, this).suspendCount;
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }
   }

   public void stop(ObjectReference var1) throws InvalidTypeException {
      this.validateMirrorOrNull(var1);
      List var2 = this.vm.classesByName("java.lang.Throwable");
      ClassTypeImpl var3 = (ClassTypeImpl)var2.get(0);
      if (var1 != null && var3.isAssignableFrom(var1)) {
         try {
            JDWP.ThreadReference.Stop.process(this.vm, this, (ObjectReferenceImpl)var1);
         } catch (JDWPException var5) {
            throw var5.toJDIException();
         }
      } else {
         throw new InvalidTypeException("Not an instance of Throwable");
      }
   }

   public void interrupt() {
      try {
         JDWP.ThreadReference.Interrupt.process(this.vm, this);
      } catch (JDWPException var2) {
         throw var2.toJDIException();
      }
   }

   private JDWP.ThreadReference.Status jdwpStatus() {
      LocalCache var1 = this.localCache;
      JDWP.ThreadReference.Status var2 = var1.status;

      try {
         if (var2 == null) {
            var2 = JDWP.ThreadReference.Status.process(this.vm, this);
            if ((var2.suspendStatus & 1) != 0) {
               var1.status = var2;
            }
         }

         return var2;
      } catch (JDWPException var4) {
         throw var4.toJDIException();
      }
   }

   public int status() {
      return this.jdwpStatus().threadStatus;
   }

   public boolean isSuspended() {
      return this.suspendedZombieCount > 0 || (this.jdwpStatus().suspendStatus & 1) != 0;
   }

   public boolean isAtBreakpoint() {
      try {
         StackFrame var1 = this.frame(0);
         Location var2 = var1.location();
         List var3 = this.vm.eventRequestManager().breakpointRequests();
         Iterator var4 = var3.iterator();

         BreakpointRequest var5;
         do {
            if (!var4.hasNext()) {
               return false;
            }

            var5 = (BreakpointRequest)var4.next();
         } while(!var2.equals(var5.location()));

         return true;
      } catch (IndexOutOfBoundsException var6) {
         return false;
      } catch (IncompatibleThreadStateException var7) {
         return false;
      }
   }

   public ThreadGroupReference threadGroup() {
      if (this.threadGroup == null) {
         try {
            this.threadGroup = JDWP.ThreadReference.ThreadGroup.process(this.vm, this).group;
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }
      }

      return this.threadGroup;
   }

   public int frameCount() throws IncompatibleThreadStateException {
      LocalCache var1 = this.localCache;

      try {
         if (var1.frameCount == -1) {
            var1.frameCount = JDWP.ThreadReference.FrameCount.process(this.vm, this).frameCount;
         }
      } catch (JDWPException var3) {
         switch (var3.errorCode()) {
            case 10:
            case 13:
               throw new IncompatibleThreadStateException();
            default:
               throw var3.toJDIException();
         }
      }

      return var1.frameCount;
   }

   public List frames() throws IncompatibleThreadStateException {
      return this.privateFrames(0, -1);
   }

   public StackFrame frame(int var1) throws IncompatibleThreadStateException {
      List var2 = this.privateFrames(var1, 1);
      return (StackFrame)var2.get(0);
   }

   private boolean isSubrange(LocalCache var1, int var2, int var3) {
      if (var2 < var1.framesStart) {
         return false;
      } else if (var3 == -1) {
         return var1.framesLength == -1;
      } else if (var1.framesLength == -1) {
         if (var2 + var3 > var1.framesStart + var1.frames.size()) {
            throw new IndexOutOfBoundsException();
         } else {
            return true;
         }
      } else {
         return var2 + var3 <= var1.framesStart + var1.framesLength;
      }
   }

   public List frames(int var1, int var2) throws IncompatibleThreadStateException {
      if (var2 < 0) {
         throw new IndexOutOfBoundsException("length must be greater than or equal to zero");
      } else {
         return this.privateFrames(var1, var2);
      }
   }

   private synchronized List privateFrames(int var1, int var2) throws IncompatibleThreadStateException {
      LocalCache var3 = this.localCache;

      try {
         int var5;
         if (var3.frames != null && this.isSubrange(var3, var1, var2)) {
            int var9 = var1 - var3.framesStart;
            if (var2 == -1) {
               var5 = var3.frames.size() - var9;
            } else {
               var5 = var9 + var2;
            }

            return Collections.unmodifiableList(var3.frames.subList(var9, var5));
         } else {
            JDWP.ThreadReference.Frames.Frame[] var4 = JDWP.ThreadReference.Frames.process(this.vm, this, var1, var2).frames;
            var5 = var4.length;
            var3.frames = new ArrayList(var5);

            for(int var6 = 0; var6 < var5; ++var6) {
               if (var4[var6].location == null) {
                  throw new InternalException("Invalid frame location");
               }

               StackFrameImpl var7 = new StackFrameImpl(this.vm, this, var4[var6].frameID, var4[var6].location);
               var3.frames.add(var7);
            }

            var3.framesStart = var1;
            var3.framesLength = var2;
            return Collections.unmodifiableList(var3.frames);
         }
      } catch (JDWPException var8) {
         switch (var8.errorCode()) {
            case 10:
            case 13:
               throw new IncompatibleThreadStateException();
            default:
               throw var8.toJDIException();
         }
      }
   }

   public List ownedMonitors() throws IncompatibleThreadStateException {
      LocalCache var1 = this.localCache;

      try {
         if (var1.ownedMonitors == null) {
            var1.ownedMonitors = Arrays.asList((ObjectReference[])JDWP.ThreadReference.OwnedMonitors.process(this.vm, this).owned);
            if ((this.vm.traceFlags & 16) != 0) {
               this.vm.printTrace(this.description() + " temporarily caching owned monitors (count = " + var1.ownedMonitors.size() + ")");
            }
         }
      } catch (JDWPException var3) {
         switch (var3.errorCode()) {
            case 10:
            case 13:
               throw new IncompatibleThreadStateException();
            default:
               throw var3.toJDIException();
         }
      }

      return var1.ownedMonitors;
   }

   public ObjectReference currentContendedMonitor() throws IncompatibleThreadStateException {
      LocalCache var1 = this.localCache;

      try {
         if (var1.contendedMonitor == null && !var1.triedCurrentContended) {
            var1.contendedMonitor = JDWP.ThreadReference.CurrentContendedMonitor.process(this.vm, this).monitor;
            var1.triedCurrentContended = true;
            if (var1.contendedMonitor != null && (this.vm.traceFlags & 16) != 0) {
               this.vm.printTrace(this.description() + " temporarily caching contended monitor (id = " + var1.contendedMonitor.uniqueID() + ")");
            }
         }
      } catch (JDWPException var3) {
         switch (var3.errorCode()) {
            case 10:
            case 13:
               throw new IncompatibleThreadStateException();
            default:
               throw var3.toJDIException();
         }
      }

      return var1.contendedMonitor;
   }

   public List ownedMonitorsAndFrames() throws IncompatibleThreadStateException {
      LocalCache var1 = this.localCache;

      try {
         if (var1.ownedMonitorsInfo == null) {
            JDWP.ThreadReference.OwnedMonitorsStackDepthInfo.monitor[] var2 = JDWP.ThreadReference.OwnedMonitorsStackDepthInfo.process(this.vm, this).owned;
            var1.ownedMonitorsInfo = new ArrayList(var2.length);

            for(int var3 = 0; var3 < var2.length; ++var3) {
               JDWP.ThreadReference.OwnedMonitorsStackDepthInfo.monitor var10000 = var2[var3];
               MonitorInfoImpl var5 = new MonitorInfoImpl(this.vm, var2[var3].monitor, this, var2[var3].stack_depth);
               var1.ownedMonitorsInfo.add(var5);
            }

            if ((this.vm.traceFlags & 16) != 0) {
               this.vm.printTrace(this.description() + " temporarily caching owned monitors (count = " + var1.ownedMonitorsInfo.size() + ")");
            }
         }
      } catch (JDWPException var6) {
         switch (var6.errorCode()) {
            case 10:
            case 13:
               throw new IncompatibleThreadStateException();
            default:
               throw var6.toJDIException();
         }
      }

      return var1.ownedMonitorsInfo;
   }

   public void popFrames(StackFrame var1) throws IncompatibleThreadStateException {
      if (!var1.thread().equals(this)) {
         throw new IllegalArgumentException("frame does not belong to this thread");
      } else if (!this.vm.canPopFrames()) {
         throw new UnsupportedOperationException("target does not support popping frames");
      } else {
         ((StackFrameImpl)var1).pop();
      }
   }

   public void forceEarlyReturn(Value var1) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException {
      if (!this.vm.canForceEarlyReturn()) {
         throw new UnsupportedOperationException("target does not support the forcing of a method to return early");
      } else {
         this.validateMirrorOrNull(var1);

         StackFrameImpl var2;
         try {
            var2 = (StackFrameImpl)this.frame(0);
         } catch (IndexOutOfBoundsException var6) {
            throw new InvalidStackFrameException("No more frames on the stack");
         }

         var2.validateStackFrame();
         MethodImpl var3 = (MethodImpl)var2.location().method();
         ValueImpl var4 = ValueImpl.prepareForAssignment(var1, var3.getReturnValueContainer());

         try {
            JDWP.ThreadReference.ForceEarlyReturn.process(this.vm, this, var4);
         } catch (JDWPException var7) {
            switch (var7.errorCode()) {
               case 13:
                  throw new IncompatibleThreadStateException("Thread not suspended");
               case 15:
                  throw new IncompatibleThreadStateException("Thread has not started or has finished");
               case 31:
                  throw new InvalidStackFrameException("No more frames on the stack");
               case 32:
                  throw new NativeMethodException();
               default:
                  throw var7.toJDIException();
            }
         }
      }
   }

   public String toString() {
      return "instance of " + this.referenceType().name() + "(name='" + this.name() + "', id=" + this.uniqueID() + ")";
   }

   byte typeValueKey() {
      return 116;
   }

   void addListener(ThreadListener var1) {
      synchronized(this.vm.state()) {
         this.listeners.add(new WeakReference(var1));
      }
   }

   void removeListener(ThreadListener var1) {
      synchronized(this.vm.state()) {
         Iterator var3 = this.listeners.iterator();

         while(var3.hasNext()) {
            WeakReference var4 = (WeakReference)var3.next();
            if (var1.equals(var4.get())) {
               var3.remove();
               break;
            }
         }

      }
   }

   private void processThreadAction(ThreadAction var1) {
      synchronized(this.vm.state()) {
         Iterator var3 = this.listeners.iterator();

         while(var3.hasNext()) {
            WeakReference var4 = (WeakReference)var3.next();
            ThreadListener var5 = (ThreadListener)var4.get();
            if (var5 != null) {
               switch (var1.id()) {
                  case 2:
                     if (!var5.threadResumable(var1)) {
                        var3.remove();
                     }
               }
            } else {
               var3.remove();
            }
         }

         this.resetLocalCache();
      }
   }

   private static class Cache extends ObjectReferenceImpl.Cache {
      String name;

      private Cache() {
         this.name = null;
      }

      // $FF: synthetic method
      Cache(Object var1) {
         this();
      }
   }

   private static class LocalCache {
      JDWP.ThreadReference.Status status;
      List frames;
      int framesStart;
      int framesLength;
      int frameCount;
      List ownedMonitors;
      List ownedMonitorsInfo;
      ObjectReference contendedMonitor;
      boolean triedCurrentContended;

      private LocalCache() {
         this.status = null;
         this.frames = null;
         this.framesStart = -1;
         this.framesLength = 0;
         this.frameCount = -1;
         this.ownedMonitors = null;
         this.ownedMonitorsInfo = null;
         this.contendedMonitor = null;
         this.triedCurrentContended = false;
      }

      // $FF: synthetic method
      LocalCache(Object var1) {
         this();
      }
   }
}
