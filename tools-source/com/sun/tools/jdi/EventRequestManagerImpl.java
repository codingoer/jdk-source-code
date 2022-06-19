package com.sun.tools.jdi;

import com.sun.jdi.Location;
import com.sun.jdi.NativeMethodException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.ClassUnloadRequest;
import com.sun.jdi.request.DuplicateRequestException;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.InvalidRequestStateException;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ModificationWatchpointRequest;
import com.sun.jdi.request.MonitorContendedEnterRequest;
import com.sun.jdi.request.MonitorContendedEnteredRequest;
import com.sun.jdi.request.MonitorWaitRequest;
import com.sun.jdi.request.MonitorWaitedRequest;
import com.sun.jdi.request.StepRequest;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;
import com.sun.jdi.request.VMDeathRequest;
import com.sun.jdi.request.WatchpointRequest;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class EventRequestManagerImpl extends MirrorImpl implements EventRequestManager {
   List[] requestLists;
   private static int methodExitEventCmd = 0;

   static int JDWPtoJDISuspendPolicy(byte var0) {
      switch (var0) {
         case 0:
            return 0;
         case 1:
            return 1;
         case 2:
            return 2;
         default:
            throw new IllegalArgumentException("Illegal policy constant: " + var0);
      }
   }

   static byte JDItoJDWPSuspendPolicy(int var0) {
      switch (var0) {
         case 0:
            return 0;
         case 1:
            return 1;
         case 2:
            return 2;
         default:
            throw new IllegalArgumentException("Illegal policy constant: " + var0);
      }
   }

   public boolean equals(Object var1) {
      return this == var1;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   EventRequestManagerImpl(VirtualMachine var1) {
      super(var1);
      Field[] var2 = JDWP.EventKind.class.getDeclaredFields();
      int var3 = 0;

      int var4;
      for(var4 = 0; var4 < var2.length; ++var4) {
         int var5;
         try {
            var5 = var2[var4].getInt((Object)null);
         } catch (IllegalAccessException var7) {
            throw new RuntimeException("Got: " + var7);
         }

         if (var5 > var3) {
            var3 = var5;
         }
      }

      this.requestLists = new List[var3 + 1];

      for(var4 = 0; var4 <= var3; ++var4) {
         this.requestLists[var4] = new ArrayList();
      }

   }

   public ClassPrepareRequest createClassPrepareRequest() {
      return new ClassPrepareRequestImpl();
   }

   public ClassUnloadRequest createClassUnloadRequest() {
      return new ClassUnloadRequestImpl();
   }

   public ExceptionRequest createExceptionRequest(ReferenceType var1, boolean var2, boolean var3) {
      this.validateMirrorOrNull(var1);
      return new ExceptionRequestImpl(var1, var2, var3);
   }

   public StepRequest createStepRequest(ThreadReference var1, int var2, int var3) {
      this.validateMirror(var1);
      return new StepRequestImpl(var1, var2, var3);
   }

   public ThreadDeathRequest createThreadDeathRequest() {
      return new ThreadDeathRequestImpl();
   }

   public ThreadStartRequest createThreadStartRequest() {
      return new ThreadStartRequestImpl();
   }

   public MethodEntryRequest createMethodEntryRequest() {
      return new MethodEntryRequestImpl();
   }

   public MethodExitRequest createMethodExitRequest() {
      return new MethodExitRequestImpl();
   }

   public MonitorContendedEnterRequest createMonitorContendedEnterRequest() {
      if (!this.vm.canRequestMonitorEvents()) {
         throw new UnsupportedOperationException("target VM does not support requesting Monitor events");
      } else {
         return new MonitorContendedEnterRequestImpl();
      }
   }

   public MonitorContendedEnteredRequest createMonitorContendedEnteredRequest() {
      if (!this.vm.canRequestMonitorEvents()) {
         throw new UnsupportedOperationException("target VM does not support requesting Monitor events");
      } else {
         return new MonitorContendedEnteredRequestImpl();
      }
   }

   public MonitorWaitRequest createMonitorWaitRequest() {
      if (!this.vm.canRequestMonitorEvents()) {
         throw new UnsupportedOperationException("target VM does not support requesting Monitor events");
      } else {
         return new MonitorWaitRequestImpl();
      }
   }

   public MonitorWaitedRequest createMonitorWaitedRequest() {
      if (!this.vm.canRequestMonitorEvents()) {
         throw new UnsupportedOperationException("target VM does not support requesting Monitor events");
      } else {
         return new MonitorWaitedRequestImpl();
      }
   }

   public BreakpointRequest createBreakpointRequest(Location var1) {
      this.validateMirror(var1);
      if (var1.codeIndex() == -1L) {
         throw new NativeMethodException("Cannot set breakpoints on native methods");
      } else {
         return new BreakpointRequestImpl(var1);
      }
   }

   public AccessWatchpointRequest createAccessWatchpointRequest(com.sun.jdi.Field var1) {
      this.validateMirror(var1);
      if (!this.vm.canWatchFieldAccess()) {
         throw new UnsupportedOperationException("target VM does not support access watchpoints");
      } else {
         return new AccessWatchpointRequestImpl(var1);
      }
   }

   public ModificationWatchpointRequest createModificationWatchpointRequest(com.sun.jdi.Field var1) {
      this.validateMirror(var1);
      if (!this.vm.canWatchFieldModification()) {
         throw new UnsupportedOperationException("target VM does not support modification watchpoints");
      } else {
         return new ModificationWatchpointRequestImpl(var1);
      }
   }

   public VMDeathRequest createVMDeathRequest() {
      if (!this.vm.canRequestVMDeathEvent()) {
         throw new UnsupportedOperationException("target VM does not support requesting VM death events");
      } else {
         return new VMDeathRequestImpl();
      }
   }

   public void deleteEventRequest(EventRequest var1) {
      this.validateMirror(var1);
      ((EventRequestImpl)var1).delete();
   }

   public void deleteEventRequests(List var1) {
      this.validateMirrors(var1);
      Iterator var2 = (new ArrayList(var1)).iterator();

      while(var2.hasNext()) {
         ((EventRequestImpl)var2.next()).delete();
      }

   }

   public void deleteAllBreakpoints() {
      this.requestList(2).clear();

      try {
         JDWP.EventRequest.ClearAllBreakpoints.process(this.vm);
      } catch (JDWPException var2) {
         throw var2.toJDIException();
      }
   }

   public List stepRequests() {
      return this.unmodifiableRequestList(1);
   }

   public List classPrepareRequests() {
      return this.unmodifiableRequestList(8);
   }

   public List classUnloadRequests() {
      return this.unmodifiableRequestList(9);
   }

   public List threadStartRequests() {
      return this.unmodifiableRequestList(6);
   }

   public List threadDeathRequests() {
      return this.unmodifiableRequestList(7);
   }

   public List exceptionRequests() {
      return this.unmodifiableRequestList(4);
   }

   public List breakpointRequests() {
      return this.unmodifiableRequestList(2);
   }

   public List accessWatchpointRequests() {
      return this.unmodifiableRequestList(20);
   }

   public List modificationWatchpointRequests() {
      return this.unmodifiableRequestList(21);
   }

   public List methodEntryRequests() {
      return this.unmodifiableRequestList(40);
   }

   public List methodExitRequests() {
      return this.unmodifiableRequestList(methodExitEventCmd);
   }

   public List monitorContendedEnterRequests() {
      return this.unmodifiableRequestList(43);
   }

   public List monitorContendedEnteredRequests() {
      return this.unmodifiableRequestList(44);
   }

   public List monitorWaitRequests() {
      return this.unmodifiableRequestList(45);
   }

   public List monitorWaitedRequests() {
      return this.unmodifiableRequestList(46);
   }

   public List vmDeathRequests() {
      return this.unmodifiableRequestList(99);
   }

   List unmodifiableRequestList(int var1) {
      return Collections.unmodifiableList(this.requestList(var1));
   }

   EventRequest request(int var1, int var2) {
      List var3 = this.requestList(var1);

      for(int var4 = var3.size() - 1; var4 >= 0; --var4) {
         EventRequestImpl var5 = (EventRequestImpl)var3.get(var4);
         if (var5.id == var2) {
            return var5;
         }
      }

      return null;
   }

   List requestList(int var1) {
      return this.requestLists[var1];
   }

   class VMDeathRequestImpl extends EventRequestImpl implements VMDeathRequest {
      VMDeathRequestImpl() {
         super();
         this.requestList().add(this);
      }

      int eventCmd() {
         return 99;
      }

      public String toString() {
         return "VM death request " + this.state();
      }
   }

   class ModificationWatchpointRequestImpl extends WatchpointRequestImpl implements ModificationWatchpointRequest {
      ModificationWatchpointRequestImpl(com.sun.jdi.Field var2) {
         super(var2);
         this.requestList().add(this);
      }

      int eventCmd() {
         return 21;
      }

      public String toString() {
         return "modification watchpoint request " + this.field + this.state();
      }
   }

   class AccessWatchpointRequestImpl extends WatchpointRequestImpl implements AccessWatchpointRequest {
      AccessWatchpointRequestImpl(com.sun.jdi.Field var2) {
         super(var2);
         this.requestList().add(this);
      }

      int eventCmd() {
         return 20;
      }

      public String toString() {
         return "access watchpoint request " + this.field + this.state();
      }
   }

   abstract class WatchpointRequestImpl extends ClassVisibleEventRequestImpl implements WatchpointRequest {
      final com.sun.jdi.Field field;

      WatchpointRequestImpl(com.sun.jdi.Field var2) {
         super();
         this.field = var2;
         this.filters.add(0, JDWP.EventRequest.Set.Modifier.FieldOnly.create((ReferenceTypeImpl)var2.declaringType(), ((FieldImpl)var2).ref()));
      }

      public com.sun.jdi.Field field() {
         return this.field;
      }
   }

   class ThreadStartRequestImpl extends ThreadVisibleEventRequestImpl implements ThreadStartRequest {
      ThreadStartRequestImpl() {
         super();
         this.requestList().add(this);
      }

      int eventCmd() {
         return 6;
      }

      public String toString() {
         return "thread start request " + this.state();
      }
   }

   class ThreadDeathRequestImpl extends ThreadVisibleEventRequestImpl implements ThreadDeathRequest {
      ThreadDeathRequestImpl() {
         super();
         this.requestList().add(this);
      }

      int eventCmd() {
         return 7;
      }

      public String toString() {
         return "thread death request " + this.state();
      }
   }

   class StepRequestImpl extends ClassVisibleEventRequestImpl implements StepRequest {
      ThreadReferenceImpl thread;
      int size;
      int depth;

      StepRequestImpl(ThreadReference var2, int var3, int var4) {
         super();
         this.thread = (ThreadReferenceImpl)var2;
         this.size = var3;
         this.depth = var4;
         byte var5;
         switch (var3) {
            case -2:
               var5 = 1;
               break;
            case -1:
               var5 = 0;
               break;
            default:
               throw new IllegalArgumentException("Invalid step size");
         }

         byte var6;
         switch (var4) {
            case 1:
               var6 = 0;
               break;
            case 2:
               var6 = 1;
               break;
            case 3:
               var6 = 2;
               break;
            default:
               throw new IllegalArgumentException("Invalid step depth");
         }

         List var7 = EventRequestManagerImpl.this.stepRequests();
         Iterator var8 = var7.iterator();

         StepRequest var9;
         do {
            if (!var8.hasNext()) {
               this.filters.add(JDWP.EventRequest.Set.Modifier.Step.create(this.thread, var5, var6));
               this.requestList().add(this);
               return;
            }

            var9 = (StepRequest)var8.next();
         } while(var9 == this || !var9.isEnabled() || !var9.thread().equals(var2));

         throw new DuplicateRequestException("Only one step request allowed per thread");
      }

      public int depth() {
         return this.depth;
      }

      public int size() {
         return this.size;
      }

      public ThreadReference thread() {
         return this.thread;
      }

      int eventCmd() {
         return 1;
      }

      public String toString() {
         return "step request " + this.thread() + this.state();
      }
   }

   class MonitorWaitedRequestImpl extends ClassVisibleEventRequestImpl implements MonitorWaitedRequest {
      MonitorWaitedRequestImpl() {
         super();
         this.requestList().add(this);
      }

      int eventCmd() {
         return 46;
      }

      public String toString() {
         return "monitor waited request " + this.state();
      }
   }

   class MonitorWaitRequestImpl extends ClassVisibleEventRequestImpl implements MonitorWaitRequest {
      MonitorWaitRequestImpl() {
         super();
         this.requestList().add(this);
      }

      int eventCmd() {
         return 45;
      }

      public String toString() {
         return "monitor wait request " + this.state();
      }
   }

   class MonitorContendedEnteredRequestImpl extends ClassVisibleEventRequestImpl implements MonitorContendedEnteredRequest {
      MonitorContendedEnteredRequestImpl() {
         super();
         this.requestList().add(this);
      }

      int eventCmd() {
         return 44;
      }

      public String toString() {
         return "monitor contended entered request " + this.state();
      }
   }

   class MonitorContendedEnterRequestImpl extends ClassVisibleEventRequestImpl implements MonitorContendedEnterRequest {
      MonitorContendedEnterRequestImpl() {
         super();
         this.requestList().add(this);
      }

      int eventCmd() {
         return 43;
      }

      public String toString() {
         return "monitor contended enter request " + this.state();
      }
   }

   class MethodExitRequestImpl extends ClassVisibleEventRequestImpl implements MethodExitRequest {
      MethodExitRequestImpl() {
         super();
         if (EventRequestManagerImpl.methodExitEventCmd == 0) {
            if (this.vm.canGetMethodReturnValues()) {
               EventRequestManagerImpl.methodExitEventCmd = 42;
            } else {
               EventRequestManagerImpl.methodExitEventCmd = 41;
            }
         }

         this.requestList().add(this);
      }

      int eventCmd() {
         return EventRequestManagerImpl.methodExitEventCmd;
      }

      public String toString() {
         return "method exit request " + this.state();
      }
   }

   class MethodEntryRequestImpl extends ClassVisibleEventRequestImpl implements MethodEntryRequest {
      MethodEntryRequestImpl() {
         super();
         this.requestList().add(this);
      }

      int eventCmd() {
         return 40;
      }

      public String toString() {
         return "method entry request " + this.state();
      }
   }

   class ExceptionRequestImpl extends ClassVisibleEventRequestImpl implements ExceptionRequest {
      ReferenceType exception = null;
      boolean caught = true;
      boolean uncaught = true;

      ExceptionRequestImpl(ReferenceType var2, boolean var3, boolean var4) {
         super();
         this.exception = var2;
         this.caught = var3;
         this.uncaught = var4;
         Object var5;
         if (this.exception == null) {
            var5 = new ClassTypeImpl(this.vm, 0L);
         } else {
            var5 = (ReferenceTypeImpl)this.exception;
         }

         this.filters.add(JDWP.EventRequest.Set.Modifier.ExceptionOnly.create((ReferenceTypeImpl)var5, this.caught, this.uncaught));
         this.requestList().add(this);
      }

      public ReferenceType exception() {
         return this.exception;
      }

      public boolean notifyCaught() {
         return this.caught;
      }

      public boolean notifyUncaught() {
         return this.uncaught;
      }

      int eventCmd() {
         return 4;
      }

      public String toString() {
         return "exception request " + this.exception() + this.state();
      }
   }

   class ClassUnloadRequestImpl extends ClassVisibleEventRequestImpl implements ClassUnloadRequest {
      ClassUnloadRequestImpl() {
         super();
         this.requestList().add(this);
      }

      int eventCmd() {
         return 9;
      }

      public String toString() {
         return "class unload request " + this.state();
      }
   }

   class ClassPrepareRequestImpl extends ClassVisibleEventRequestImpl implements ClassPrepareRequest {
      ClassPrepareRequestImpl() {
         super();
         this.requestList().add(this);
      }

      int eventCmd() {
         return 8;
      }

      public synchronized void addSourceNameFilter(String var1) {
         if (!this.isEnabled() && !this.deleted) {
            if (!this.vm.canUseSourceNameFilters()) {
               throw new UnsupportedOperationException("target does not support source name filters");
            } else if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.filters.add(JDWP.EventRequest.Set.Modifier.SourceNameMatch.create(var1));
            }
         } else {
            throw this.invalidState();
         }
      }

      public String toString() {
         return "class prepare request " + this.state();
      }
   }

   class BreakpointRequestImpl extends ClassVisibleEventRequestImpl implements BreakpointRequest {
      private final Location location;

      BreakpointRequestImpl(Location var2) {
         super();
         this.location = var2;
         this.filters.add(0, JDWP.EventRequest.Set.Modifier.LocationOnly.create(var2));
         this.requestList().add(this);
      }

      public Location location() {
         return this.location;
      }

      int eventCmd() {
         return 2;
      }

      public String toString() {
         return "breakpoint request " + this.location() + this.state();
      }
   }

   abstract class ClassVisibleEventRequestImpl extends ThreadVisibleEventRequestImpl {
      ClassVisibleEventRequestImpl() {
         super();
      }

      public synchronized void addClassFilter(ReferenceType var1) {
         this.validateMirror(var1);
         if (!this.isEnabled() && !this.deleted) {
            this.filters.add(JDWP.EventRequest.Set.Modifier.ClassOnly.create((ReferenceTypeImpl)var1));
         } else {
            throw this.invalidState();
         }
      }

      public synchronized void addClassFilter(String var1) {
         if (!this.isEnabled() && !this.deleted) {
            if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.filters.add(JDWP.EventRequest.Set.Modifier.ClassMatch.create(var1));
            }
         } else {
            throw this.invalidState();
         }
      }

      public synchronized void addClassExclusionFilter(String var1) {
         if (!this.isEnabled() && !this.deleted) {
            if (var1 == null) {
               throw new NullPointerException();
            } else {
               this.filters.add(JDWP.EventRequest.Set.Modifier.ClassExclude.create(var1));
            }
         } else {
            throw this.invalidState();
         }
      }

      public synchronized void addInstanceFilter(ObjectReference var1) {
         this.validateMirror(var1);
         if (!this.isEnabled() && !this.deleted) {
            if (!this.vm.canUseInstanceFilters()) {
               throw new UnsupportedOperationException("target does not support instance filters");
            } else {
               this.filters.add(JDWP.EventRequest.Set.Modifier.InstanceOnly.create((ObjectReferenceImpl)var1));
            }
         } else {
            throw this.invalidState();
         }
      }
   }

   abstract class ThreadVisibleEventRequestImpl extends EventRequestImpl {
      ThreadVisibleEventRequestImpl() {
         super();
      }

      public synchronized void addThreadFilter(ThreadReference var1) {
         this.validateMirror(var1);
         if (!this.isEnabled() && !this.deleted) {
            this.filters.add(JDWP.EventRequest.Set.Modifier.ThreadOnly.create((ThreadReferenceImpl)var1));
         } else {
            throw this.invalidState();
         }
      }
   }

   abstract class EventRequestImpl extends MirrorImpl implements EventRequest {
      int id;
      List filters = new ArrayList();
      boolean isEnabled = false;
      boolean deleted = false;
      byte suspendPolicy = 2;
      private Map clientProperties = null;

      EventRequestImpl() {
         super(EventRequestManagerImpl.this.vm);
      }

      public boolean equals(Object var1) {
         return this == var1;
      }

      public int hashCode() {
         return System.identityHashCode(this);
      }

      abstract int eventCmd();

      InvalidRequestStateException invalidState() {
         return new InvalidRequestStateException(this.toString());
      }

      String state() {
         return this.deleted ? " (deleted)" : (this.isEnabled() ? " (enabled)" : " (disabled)");
      }

      List requestList() {
         return EventRequestManagerImpl.this.requestList(this.eventCmd());
      }

      void delete() {
         if (!this.deleted) {
            this.requestList().remove(this);
            this.disable();
            this.deleted = true;
         }

      }

      public boolean isEnabled() {
         return this.isEnabled;
      }

      public void enable() {
         this.setEnabled(true);
      }

      public void disable() {
         this.setEnabled(false);
      }

      public synchronized void setEnabled(boolean var1) {
         if (this.deleted) {
            throw this.invalidState();
         } else {
            if (var1 != this.isEnabled) {
               if (this.isEnabled) {
                  this.clear();
               } else {
                  this.set();
               }
            }

         }
      }

      public synchronized void addCountFilter(int var1) {
         if (!this.isEnabled() && !this.deleted) {
            if (var1 < 1) {
               throw new IllegalArgumentException("count is less than one");
            } else {
               this.filters.add(JDWP.EventRequest.Set.Modifier.Count.create(var1));
            }
         } else {
            throw this.invalidState();
         }
      }

      public void setSuspendPolicy(int var1) {
         if (!this.isEnabled() && !this.deleted) {
            this.suspendPolicy = EventRequestManagerImpl.JDItoJDWPSuspendPolicy(var1);
         } else {
            throw this.invalidState();
         }
      }

      public int suspendPolicy() {
         return EventRequestManagerImpl.JDWPtoJDISuspendPolicy(this.suspendPolicy);
      }

      synchronized void set() {
         JDWP.EventRequest.Set.Modifier[] var1 = (JDWP.EventRequest.Set.Modifier[])this.filters.toArray(new JDWP.EventRequest.Set.Modifier[this.filters.size()]);

         try {
            this.id = JDWP.EventRequest.Set.process(this.vm, (byte)this.eventCmd(), this.suspendPolicy, var1).requestID;
         } catch (JDWPException var3) {
            throw var3.toJDIException();
         }

         this.isEnabled = true;
      }

      synchronized void clear() {
         try {
            JDWP.EventRequest.Clear.process(this.vm, (byte)this.eventCmd(), this.id);
         } catch (JDWPException var2) {
            throw var2.toJDIException();
         }

         this.isEnabled = false;
      }

      private Map getProperties() {
         if (this.clientProperties == null) {
            this.clientProperties = new HashMap(2);
         }

         return this.clientProperties;
      }

      public final Object getProperty(Object var1) {
         return this.clientProperties == null ? null : this.getProperties().get(var1);
      }

      public final void putProperty(Object var1, Object var2) {
         if (var2 != null) {
            this.getProperties().put(var1, var2);
         } else {
            this.getProperties().remove(var1);
         }

      }
   }
}
