package com.sun.tools.jdi;

import com.sun.jdi.Field;
import com.sun.jdi.InternalException;
import com.sun.jdi.Locatable;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.MonitorContendedEnterEvent;
import com.sun.jdi.event.MonitorContendedEnteredEvent;
import com.sun.jdi.event.MonitorWaitEvent;
import com.sun.jdi.event.MonitorWaitedEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;
import com.sun.jdi.request.EventRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;

public class EventSetImpl extends ArrayList implements EventSet {
   private static final long serialVersionUID = -4857338819787924570L;
   private VirtualMachineImpl vm;
   private Packet pkt;
   private byte suspendPolicy;
   private EventSetImpl internalEventSet;

   public String toString() {
      String var1 = "event set, policy:" + this.suspendPolicy + ", count:" + this.size() + " = {";
      boolean var2 = true;

      for(Iterator var3 = this.iterator(); var3.hasNext(); var2 = false) {
         Event var4 = (Event)var3.next();
         if (!var2) {
            var1 = var1 + ", ";
         }

         var1 = var1 + var4.toString();
      }

      var1 = var1 + "}";
      return var1;
   }

   EventSetImpl(VirtualMachine var1, Packet var2) {
      this.vm = (VirtualMachineImpl)var1;
      this.pkt = var2;
   }

   EventSetImpl(VirtualMachine var1, byte var2) {
      this(var1, (Packet)null);
      this.suspendPolicy = 0;
      switch (var2) {
         case 100:
            this.addEvent(new VMDisconnectEventImpl());
            return;
         default:
            throw new InternalException("Bad singleton event code");
      }
   }

   private void addEvent(EventImpl var1) {
      super.add(var1);
   }

   synchronized void build() {
      if (this.pkt != null) {
         PacketStream var1 = new PacketStream(this.vm, this.pkt);
         JDWP.Event.Composite var2 = new JDWP.Event.Composite(this.vm, var1);
         this.suspendPolicy = var2.suspendPolicy;
         if ((this.vm.traceFlags & 4) != 0) {
            switch (this.suspendPolicy) {
               case 0:
                  this.vm.printTrace("EventSet: SUSPEND_NONE");
                  break;
               case 1:
                  this.vm.printTrace("EventSet: SUSPEND_EVENT_THREAD");
                  break;
               case 2:
                  this.vm.printTrace("EventSet: SUSPEND_ALL");
            }
         }

         ThreadReference var3 = null;

         for(int var4 = 0; var4 < var2.events.length; ++var4) {
            EventImpl var5 = this.createEvent(var2.events[var4]);
            if ((this.vm.traceFlags & 4) != 0) {
               try {
                  this.vm.printTrace("Event: " + var5);
               } catch (VMDisconnectedException var7) {
               }
            }

            switch (var5.destination()) {
               case UNKNOWN_EVENT:
                  if (var5 instanceof ThreadedEventImpl && this.suspendPolicy == 1) {
                     var3 = ((ThreadedEventImpl)var5).thread();
                  }
                  break;
               case CLIENT_EVENT:
                  this.addEvent(var5);
                  break;
               case INTERNAL_EVENT:
                  if (this.internalEventSet == null) {
                     this.internalEventSet = new EventSetImpl(this.vm, (Packet)null);
                  }

                  this.internalEventSet.addEvent(var5);
                  break;
               default:
                  throw new InternalException("Invalid event destination");
            }
         }

         this.pkt = null;
         if (super.size() == 0) {
            if (this.suspendPolicy == 2) {
               this.vm.resume();
            } else if (this.suspendPolicy == 1 && var3 != null) {
               var3.resume();
            }

            this.suspendPolicy = 0;
         }

      }
   }

   EventSet userFilter() {
      return this;
   }

   EventSet internalFilter() {
      return this.internalEventSet;
   }

   EventImpl createEvent(JDWP.Event.Composite.Events var1) {
      JDWP.Event.Composite.Events.EventsCommon var2 = var1.aEventsCommon;
      switch (var1.eventKind) {
         case 1:
            return new StepEventImpl((JDWP.Event.Composite.Events.SingleStep)var2);
         case 2:
            return new BreakpointEventImpl((JDWP.Event.Composite.Events.Breakpoint)var2);
         case 4:
            return new ExceptionEventImpl((JDWP.Event.Composite.Events.Exception)var2);
         case 6:
            return new ThreadStartEventImpl((JDWP.Event.Composite.Events.ThreadStart)var2);
         case 7:
            return new ThreadDeathEventImpl((JDWP.Event.Composite.Events.ThreadDeath)var2);
         case 8:
            return new ClassPrepareEventImpl((JDWP.Event.Composite.Events.ClassPrepare)var2);
         case 9:
            return new ClassUnloadEventImpl((JDWP.Event.Composite.Events.ClassUnload)var2);
         case 20:
            return new AccessWatchpointEventImpl((JDWP.Event.Composite.Events.FieldAccess)var2);
         case 21:
            return new ModificationWatchpointEventImpl((JDWP.Event.Composite.Events.FieldModification)var2);
         case 40:
            return new MethodEntryEventImpl((JDWP.Event.Composite.Events.MethodEntry)var2);
         case 41:
            return new MethodExitEventImpl((JDWP.Event.Composite.Events.MethodExit)var2);
         case 42:
            return new MethodExitEventImpl((JDWP.Event.Composite.Events.MethodExitWithReturnValue)var2);
         case 43:
            return new MonitorContendedEnterEventImpl((JDWP.Event.Composite.Events.MonitorContendedEnter)var2);
         case 44:
            return new MonitorContendedEnteredEventImpl((JDWP.Event.Composite.Events.MonitorContendedEntered)var2);
         case 45:
            return new MonitorWaitEventImpl((JDWP.Event.Composite.Events.MonitorWait)var2);
         case 46:
            return new MonitorWaitedEventImpl((JDWP.Event.Composite.Events.MonitorWaited)var2);
         case 90:
            return new VMStartEventImpl((JDWP.Event.Composite.Events.VMStart)var2);
         case 99:
            return new VMDeathEventImpl((JDWP.Event.Composite.Events.VMDeath)var2);
         default:
            System.err.println("Ignoring event cmd " + var1.eventKind + " from the VM");
            return null;
      }
   }

   public VirtualMachine virtualMachine() {
      return this.vm;
   }

   public int suspendPolicy() {
      return EventRequestManagerImpl.JDWPtoJDISuspendPolicy(this.suspendPolicy);
   }

   private ThreadReference eventThread() {
      Iterator var1 = this.iterator();

      Event var2;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var2 = (Event)var1.next();
      } while(!(var2 instanceof ThreadedEventImpl));

      return ((ThreadedEventImpl)var2).thread();
   }

   public void resume() {
      switch (this.suspendPolicy()) {
         case 0:
            break;
         case 1:
            ThreadReference var1 = this.eventThread();
            if (var1 == null) {
               throw new InternalException("Inconsistent suspend policy");
            }

            var1.resume();
            break;
         case 2:
            this.vm.resume();
            break;
         default:
            throw new InternalException("Invalid suspend policy");
      }

   }

   public Iterator iterator() {
      return new Itr();
   }

   public EventIterator eventIterator() {
      return new Itr();
   }

   public Spliterator spliterator() {
      return Spliterators.spliterator(this, 1);
   }

   public boolean add(Event var1) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }

   public class Itr implements EventIterator {
      int cursor = 0;

      public boolean hasNext() {
         return this.cursor != EventSetImpl.this.size();
      }

      public Event next() {
         try {
            Event var1 = (Event)EventSetImpl.this.get(this.cursor);
            ++this.cursor;
            return var1;
         } catch (IndexOutOfBoundsException var2) {
            throw new NoSuchElementException();
         }
      }

      public Event nextEvent() {
         return this.next();
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   class ModificationWatchpointEventImpl extends WatchpointEventImpl implements ModificationWatchpointEvent {
      Value newValue;

      ModificationWatchpointEventImpl(JDWP.Event.Composite.Events.FieldModification var2) {
         super(var2, var2.requestID, var2.thread, var2.location, var2.refTypeTag, var2.typeID, var2.fieldID, var2.object);
         this.newValue = var2.valueToBe;
      }

      public Value valueToBe() {
         return this.newValue;
      }

      String eventName() {
         return "ModificationWatchpoint";
      }
   }

   class AccessWatchpointEventImpl extends WatchpointEventImpl implements AccessWatchpointEvent {
      AccessWatchpointEventImpl(JDWP.Event.Composite.Events.FieldAccess var2) {
         super(var2, var2.requestID, var2.thread, var2.location, var2.refTypeTag, var2.typeID, var2.fieldID, var2.object);
      }

      String eventName() {
         return "AccessWatchpoint";
      }
   }

   abstract class WatchpointEventImpl extends LocatableEventImpl implements WatchpointEvent {
      private final ReferenceTypeImpl refType;
      private final long fieldID;
      private final ObjectReference object;
      private Field field = null;

      WatchpointEventImpl(JDWP.Event.Composite.Events.EventsCommon var2, int var3, ThreadReference var4, Location var5, byte var6, long var7, long var9, ObjectReference var11) {
         super(var2, var3, var4, var5);
         this.refType = this.vm.referenceType(var7, var6);
         this.fieldID = var9;
         this.object = var11;
      }

      public Field field() {
         if (this.field == null) {
            this.field = this.refType.getFieldMirror(this.fieldID);
         }

         return this.field;
      }

      public ObjectReference object() {
         return this.object;
      }

      public Value valueCurrent() {
         return this.object == null ? this.refType.getValue(this.field()) : this.object.getValue(this.field());
      }
   }

   class VMDisconnectEventImpl extends EventImpl implements VMDisconnectEvent {
      VMDisconnectEventImpl() {
         super((byte)100);
      }

      String eventName() {
         return "VMDisconnectEvent";
      }
   }

   class VMDeathEventImpl extends EventImpl implements VMDeathEvent {
      VMDeathEventImpl(JDWP.Event.Composite.Events.VMDeath var2) {
         super(var2, var2.requestID);
      }

      String eventName() {
         return "VMDeathEvent";
      }
   }

   class VMStartEventImpl extends ThreadedEventImpl implements VMStartEvent {
      VMStartEventImpl(JDWP.Event.Composite.Events.VMStart var2) {
         super(var2, var2.requestID, var2.thread);
      }

      String eventName() {
         return "VMStartEvent";
      }
   }

   class ThreadStartEventImpl extends ThreadedEventImpl implements ThreadStartEvent {
      ThreadStartEventImpl(JDWP.Event.Composite.Events.ThreadStart var2) {
         super(var2, var2.requestID, var2.thread);
      }

      String eventName() {
         return "ThreadStartEvent";
      }
   }

   class ThreadDeathEventImpl extends ThreadedEventImpl implements ThreadDeathEvent {
      ThreadDeathEventImpl(JDWP.Event.Composite.Events.ThreadDeath var2) {
         super(var2, var2.requestID, var2.thread);
      }

      String eventName() {
         return "ThreadDeathEvent";
      }
   }

   class ExceptionEventImpl extends LocatableEventImpl implements ExceptionEvent {
      private ObjectReference exception;
      private Location catchLocation;

      ExceptionEventImpl(JDWP.Event.Composite.Events.Exception var2) {
         super(var2, var2.requestID, var2.thread, var2.location);
         this.exception = var2.exception;
         this.catchLocation = var2.catchLocation;
      }

      public ObjectReference exception() {
         return this.exception;
      }

      public Location catchLocation() {
         return this.catchLocation;
      }

      String eventName() {
         return "ExceptionEvent";
      }
   }

   class ClassUnloadEventImpl extends EventImpl implements ClassUnloadEvent {
      private String classSignature;

      ClassUnloadEventImpl(JDWP.Event.Composite.Events.ClassUnload var2) {
         super(var2, var2.requestID);
         this.classSignature = var2.signature;
      }

      public String className() {
         return this.classSignature.substring(1, this.classSignature.length() - 1).replace('/', '.');
      }

      public String classSignature() {
         return this.classSignature;
      }

      String eventName() {
         return "ClassUnloadEvent";
      }
   }

   class ClassPrepareEventImpl extends ThreadedEventImpl implements ClassPrepareEvent {
      private ReferenceType referenceType;

      ClassPrepareEventImpl(JDWP.Event.Composite.Events.ClassPrepare var2) {
         super(var2, var2.requestID, var2.thread);
         this.referenceType = this.vm.referenceType(var2.typeID, var2.refTypeTag, var2.signature);
         ((ReferenceTypeImpl)this.referenceType).setStatus(var2.status);
      }

      public ReferenceType referenceType() {
         return this.referenceType;
      }

      String eventName() {
         return "ClassPrepareEvent";
      }
   }

   class MonitorWaitedEventImpl extends LocatableEventImpl implements MonitorWaitedEvent {
      private ObjectReference monitor = null;
      private boolean timed_out;

      MonitorWaitedEventImpl(JDWP.Event.Composite.Events.MonitorWaited var2) {
         super(var2, var2.requestID, var2.thread, var2.location);
         this.monitor = var2.object;
         this.timed_out = var2.timed_out;
      }

      String eventName() {
         return "MonitorWaited";
      }

      public ObjectReference monitor() {
         return this.monitor;
      }

      public boolean timedout() {
         return this.timed_out;
      }
   }

   class MonitorWaitEventImpl extends LocatableEventImpl implements MonitorWaitEvent {
      private ObjectReference monitor = null;
      private long timeout;

      MonitorWaitEventImpl(JDWP.Event.Composite.Events.MonitorWait var2) {
         super(var2, var2.requestID, var2.thread, var2.location);
         this.monitor = var2.object;
         this.timeout = var2.timeout;
      }

      String eventName() {
         return "MonitorWait";
      }

      public ObjectReference monitor() {
         return this.monitor;
      }

      public long timeout() {
         return this.timeout;
      }
   }

   class MonitorContendedEnteredEventImpl extends LocatableEventImpl implements MonitorContendedEnteredEvent {
      private ObjectReference monitor = null;

      MonitorContendedEnteredEventImpl(JDWP.Event.Composite.Events.MonitorContendedEntered var2) {
         super(var2, var2.requestID, var2.thread, var2.location);
         this.monitor = var2.object;
      }

      String eventName() {
         return "MonitorContendedEntered";
      }

      public ObjectReference monitor() {
         return this.monitor;
      }
   }

   class MonitorContendedEnterEventImpl extends LocatableEventImpl implements MonitorContendedEnterEvent {
      private ObjectReference monitor = null;

      MonitorContendedEnterEventImpl(JDWP.Event.Composite.Events.MonitorContendedEnter var2) {
         super(var2, var2.requestID, var2.thread, var2.location);
         this.monitor = var2.object;
      }

      String eventName() {
         return "MonitorContendedEnter";
      }

      public ObjectReference monitor() {
         return this.monitor;
      }
   }

   class MethodExitEventImpl extends LocatableEventImpl implements MethodExitEvent {
      private Value returnVal = null;

      MethodExitEventImpl(JDWP.Event.Composite.Events.MethodExit var2) {
         super(var2, var2.requestID, var2.thread, var2.location);
      }

      MethodExitEventImpl(JDWP.Event.Composite.Events.MethodExitWithReturnValue var2) {
         super(var2, var2.requestID, var2.thread, var2.location);
         this.returnVal = var2.value;
      }

      String eventName() {
         return "MethodExitEvent";
      }

      public Value returnValue() {
         if (!this.vm.canGetMethodReturnValues()) {
            throw new UnsupportedOperationException("target does not support return values in MethodExit events");
         } else {
            return this.returnVal;
         }
      }
   }

   class MethodEntryEventImpl extends LocatableEventImpl implements MethodEntryEvent {
      MethodEntryEventImpl(JDWP.Event.Composite.Events.MethodEntry var2) {
         super(var2, var2.requestID, var2.thread, var2.location);
      }

      String eventName() {
         return "MethodEntryEvent";
      }
   }

   class StepEventImpl extends LocatableEventImpl implements StepEvent {
      StepEventImpl(JDWP.Event.Composite.Events.SingleStep var2) {
         super(var2, var2.requestID, var2.thread, var2.location);
      }

      String eventName() {
         return "StepEvent";
      }
   }

   class BreakpointEventImpl extends LocatableEventImpl implements BreakpointEvent {
      BreakpointEventImpl(JDWP.Event.Composite.Events.Breakpoint var2) {
         super(var2, var2.requestID, var2.thread, var2.location);
      }

      String eventName() {
         return "BreakpointEvent";
      }
   }

   abstract class LocatableEventImpl extends ThreadedEventImpl implements Locatable {
      private Location location;

      LocatableEventImpl(JDWP.Event.Composite.Events.EventsCommon var2, int var3, ThreadReference var4, Location var5) {
         super(var2, var3, var4);
         this.location = var5;
      }

      public Location location() {
         return this.location;
      }

      public Method method() {
         return this.location.method();
      }

      public String toString() {
         return this.eventName() + "@" + (this.location() == null ? " null" : this.location().toString()) + " in thread " + this.thread().name();
      }
   }

   abstract class ThreadedEventImpl extends EventImpl {
      private ThreadReference thread;

      ThreadedEventImpl(JDWP.Event.Composite.Events.EventsCommon var2, int var3, ThreadReference var4) {
         super(var2, var3);
         this.thread = var4;
      }

      public ThreadReference thread() {
         return this.thread;
      }

      public String toString() {
         return this.eventName() + " in thread " + this.thread.name();
      }
   }

   abstract class EventImpl extends MirrorImpl implements Event {
      private final byte eventCmd;
      private final int requestID;
      private final EventRequest request;

      protected EventImpl(JDWP.Event.Composite.Events.EventsCommon var2, int var3) {
         super(EventSetImpl.this.vm);
         this.eventCmd = var2.eventKind();
         this.requestID = var3;
         EventRequestManagerImpl var4 = EventSetImpl.this.vm.eventRequestManagerImpl();
         this.request = var4.request(this.eventCmd, var3);
      }

      public boolean equals(Object var1) {
         return this == var1;
      }

      public int hashCode() {
         return System.identityHashCode(this);
      }

      protected EventImpl(byte var2) {
         super(EventSetImpl.this.vm);
         this.eventCmd = var2;
         this.requestID = 0;
         this.request = null;
      }

      public EventRequest request() {
         return this.request;
      }

      int requestID() {
         return this.requestID;
      }

      EventDestination destination() {
         if (this.requestID == 0) {
            return EventDestination.CLIENT_EVENT;
         } else if (this.request == null) {
            EventRequestManagerImpl var1 = this.vm.getInternalEventRequestManager();
            return var1.request(this.eventCmd, this.requestID) != null ? EventDestination.INTERNAL_EVENT : EventDestination.UNKNOWN_EVENT;
         } else {
            return this.request.isEnabled() ? EventDestination.CLIENT_EVENT : EventDestination.UNKNOWN_EVENT;
         }
      }

      abstract String eventName();

      public String toString() {
         return this.eventName();
      }
   }
}
