package com.sun.tools.jdi;

import com.sun.jdi.Location;
import java.util.List;

class JDWP {
   static class InvokeOptions {
      static final int INVOKE_SINGLE_THREADED = 1;
      static final int INVOKE_NONVIRTUAL = 2;
   }

   static class SuspendPolicy {
      static final int NONE = 0;
      static final int EVENT_THREAD = 1;
      static final int ALL = 2;
   }

   static class StepSize {
      static final int MIN = 0;
      static final int LINE = 1;
   }

   static class StepDepth {
      static final int INTO = 0;
      static final int OVER = 1;
      static final int OUT = 2;
   }

   static class Tag {
      static final int ARRAY = 91;
      static final int BYTE = 66;
      static final int CHAR = 67;
      static final int OBJECT = 76;
      static final int FLOAT = 70;
      static final int DOUBLE = 68;
      static final int INT = 73;
      static final int LONG = 74;
      static final int SHORT = 83;
      static final int VOID = 86;
      static final int BOOLEAN = 90;
      static final int STRING = 115;
      static final int THREAD = 116;
      static final int THREAD_GROUP = 103;
      static final int CLASS_LOADER = 108;
      static final int CLASS_OBJECT = 99;
   }

   static class TypeTag {
      static final int CLASS = 1;
      static final int INTERFACE = 2;
      static final int ARRAY = 3;
   }

   static class ClassStatus {
      static final int VERIFIED = 1;
      static final int PREPARED = 2;
      static final int INITIALIZED = 4;
      static final int ERROR = 8;
   }

   static class SuspendStatus {
      static final int SUSPEND_STATUS_SUSPENDED = 1;
   }

   static class ThreadStatus {
      static final int ZOMBIE = 0;
      static final int RUNNING = 1;
      static final int SLEEPING = 2;
      static final int MONITOR = 3;
      static final int WAIT = 4;
   }

   static class EventKind {
      static final int SINGLE_STEP = 1;
      static final int BREAKPOINT = 2;
      static final int FRAME_POP = 3;
      static final int EXCEPTION = 4;
      static final int USER_DEFINED = 5;
      static final int THREAD_START = 6;
      static final int THREAD_DEATH = 7;
      static final int THREAD_END = 7;
      static final int CLASS_PREPARE = 8;
      static final int CLASS_UNLOAD = 9;
      static final int CLASS_LOAD = 10;
      static final int FIELD_ACCESS = 20;
      static final int FIELD_MODIFICATION = 21;
      static final int EXCEPTION_CATCH = 30;
      static final int METHOD_ENTRY = 40;
      static final int METHOD_EXIT = 41;
      static final int METHOD_EXIT_WITH_RETURN_VALUE = 42;
      static final int MONITOR_CONTENDED_ENTER = 43;
      static final int MONITOR_CONTENDED_ENTERED = 44;
      static final int MONITOR_WAIT = 45;
      static final int MONITOR_WAITED = 46;
      static final int VM_START = 90;
      static final int VM_INIT = 90;
      static final int VM_DEATH = 99;
      static final int VM_DISCONNECTED = 100;
   }

   static class Error {
      static final int NONE = 0;
      static final int INVALID_THREAD = 10;
      static final int INVALID_THREAD_GROUP = 11;
      static final int INVALID_PRIORITY = 12;
      static final int THREAD_NOT_SUSPENDED = 13;
      static final int THREAD_SUSPENDED = 14;
      static final int THREAD_NOT_ALIVE = 15;
      static final int INVALID_OBJECT = 20;
      static final int INVALID_CLASS = 21;
      static final int CLASS_NOT_PREPARED = 22;
      static final int INVALID_METHODID = 23;
      static final int INVALID_LOCATION = 24;
      static final int INVALID_FIELDID = 25;
      static final int INVALID_FRAMEID = 30;
      static final int NO_MORE_FRAMES = 31;
      static final int OPAQUE_FRAME = 32;
      static final int NOT_CURRENT_FRAME = 33;
      static final int TYPE_MISMATCH = 34;
      static final int INVALID_SLOT = 35;
      static final int DUPLICATE = 40;
      static final int NOT_FOUND = 41;
      static final int INVALID_MONITOR = 50;
      static final int NOT_MONITOR_OWNER = 51;
      static final int INTERRUPT = 52;
      static final int INVALID_CLASS_FORMAT = 60;
      static final int CIRCULAR_CLASS_DEFINITION = 61;
      static final int FAILS_VERIFICATION = 62;
      static final int ADD_METHOD_NOT_IMPLEMENTED = 63;
      static final int SCHEMA_CHANGE_NOT_IMPLEMENTED = 64;
      static final int INVALID_TYPESTATE = 65;
      static final int HIERARCHY_CHANGE_NOT_IMPLEMENTED = 66;
      static final int DELETE_METHOD_NOT_IMPLEMENTED = 67;
      static final int UNSUPPORTED_VERSION = 68;
      static final int NAMES_DONT_MATCH = 69;
      static final int CLASS_MODIFIERS_CHANGE_NOT_IMPLEMENTED = 70;
      static final int METHOD_MODIFIERS_CHANGE_NOT_IMPLEMENTED = 71;
      static final int NOT_IMPLEMENTED = 99;
      static final int NULL_POINTER = 100;
      static final int ABSENT_INFORMATION = 101;
      static final int INVALID_EVENT_TYPE = 102;
      static final int ILLEGAL_ARGUMENT = 103;
      static final int OUT_OF_MEMORY = 110;
      static final int ACCESS_DENIED = 111;
      static final int VM_DEAD = 112;
      static final int INTERNAL = 113;
      static final int UNATTACHED_THREAD = 115;
      static final int INVALID_TAG = 500;
      static final int ALREADY_INVOKING = 502;
      static final int INVALID_INDEX = 503;
      static final int INVALID_LENGTH = 504;
      static final int INVALID_STRING = 506;
      static final int INVALID_CLASS_LOADER = 507;
      static final int INVALID_ARRAY = 508;
      static final int TRANSPORT_LOAD = 509;
      static final int TRANSPORT_INIT = 510;
      static final int NATIVE_METHOD = 511;
      static final int INVALID_COUNT = 512;
   }

   static class Event {
      static final int COMMAND_SET = 64;

      private Event() {
      }

      static class Composite {
         static final int COMMAND = 100;
         final byte suspendPolicy;
         final Events[] events;

         Composite(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.Event.Composite" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.suspendPolicy = var2.readByte();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "suspendPolicy(byte): " + this.suspendPolicy);
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "events(Events[]): ");
            }

            int var3 = var2.readInt();
            this.events = new Events[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "events[i](Events): ");
               }

               this.events[var4] = new Events(var1, var2);
            }

         }

         static class Events {
            final byte eventKind;
            EventsCommon aEventsCommon;

            Events(VirtualMachineImpl var1, PacketStream var2) {
               this.eventKind = var2.readByte();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "eventKind(byte): " + this.eventKind);
               }

               switch (this.eventKind) {
                  case 1:
                     this.aEventsCommon = new SingleStep(var1, var2);
                     break;
                  case 2:
                     this.aEventsCommon = new Breakpoint(var1, var2);
                     break;
                  case 4:
                     this.aEventsCommon = new Exception(var1, var2);
                     break;
                  case 6:
                     this.aEventsCommon = new ThreadStart(var1, var2);
                     break;
                  case 7:
                     this.aEventsCommon = new ThreadDeath(var1, var2);
                     break;
                  case 8:
                     this.aEventsCommon = new ClassPrepare(var1, var2);
                     break;
                  case 9:
                     this.aEventsCommon = new ClassUnload(var1, var2);
                     break;
                  case 20:
                     this.aEventsCommon = new FieldAccess(var1, var2);
                     break;
                  case 21:
                     this.aEventsCommon = new FieldModification(var1, var2);
                     break;
                  case 40:
                     this.aEventsCommon = new MethodEntry(var1, var2);
                     break;
                  case 41:
                     this.aEventsCommon = new MethodExit(var1, var2);
                     break;
                  case 42:
                     this.aEventsCommon = new MethodExitWithReturnValue(var1, var2);
                     break;
                  case 43:
                     this.aEventsCommon = new MonitorContendedEnter(var1, var2);
                     break;
                  case 44:
                     this.aEventsCommon = new MonitorContendedEntered(var1, var2);
                     break;
                  case 45:
                     this.aEventsCommon = new MonitorWait(var1, var2);
                     break;
                  case 46:
                     this.aEventsCommon = new MonitorWaited(var1, var2);
                     break;
                  case 90:
                     this.aEventsCommon = new VMStart(var1, var2);
                     break;
                  case 99:
                     this.aEventsCommon = new VMDeath(var1, var2);
               }

            }

            static class VMDeath extends EventsCommon {
               static final byte ALT_ID = 99;
               final int requestID;

               byte eventKind() {
                  return 99;
               }

               VMDeath(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

               }
            }

            static class FieldModification extends EventsCommon {
               static final byte ALT_ID = 21;
               final int requestID;
               final ThreadReferenceImpl thread;
               final Location location;
               final byte refTypeTag;
               final long typeID;
               final long fieldID;
               final ObjectReferenceImpl object;
               final ValueImpl valueToBe;

               byte eventKind() {
                  return 21;
               }

               FieldModification(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

                  this.refTypeTag = var2.readByte();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "refTypeTag(byte): " + this.refTypeTag);
                  }

                  this.typeID = var2.readClassRef();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "typeID(long): ref=" + this.typeID);
                  }

                  this.fieldID = var2.readFieldRef();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "fieldID(long): " + this.fieldID);
                  }

                  this.object = var2.readTaggedObjectReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "object(ObjectReferenceImpl): " + (this.object == null ? "NULL" : "ref=" + this.object.ref()));
                  }

                  this.valueToBe = var2.readValue();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "valueToBe(ValueImpl): " + this.valueToBe);
                  }

               }
            }

            static class FieldAccess extends EventsCommon {
               static final byte ALT_ID = 20;
               final int requestID;
               final ThreadReferenceImpl thread;
               final Location location;
               final byte refTypeTag;
               final long typeID;
               final long fieldID;
               final ObjectReferenceImpl object;

               byte eventKind() {
                  return 20;
               }

               FieldAccess(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

                  this.refTypeTag = var2.readByte();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "refTypeTag(byte): " + this.refTypeTag);
                  }

                  this.typeID = var2.readClassRef();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "typeID(long): ref=" + this.typeID);
                  }

                  this.fieldID = var2.readFieldRef();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "fieldID(long): " + this.fieldID);
                  }

                  this.object = var2.readTaggedObjectReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "object(ObjectReferenceImpl): " + (this.object == null ? "NULL" : "ref=" + this.object.ref()));
                  }

               }
            }

            static class ClassUnload extends EventsCommon {
               static final byte ALT_ID = 9;
               final int requestID;
               final String signature;

               byte eventKind() {
                  return 9;
               }

               ClassUnload(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.signature = var2.readString();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "signature(String): " + this.signature);
                  }

               }
            }

            static class ClassPrepare extends EventsCommon {
               static final byte ALT_ID = 8;
               final int requestID;
               final ThreadReferenceImpl thread;
               final byte refTypeTag;
               final long typeID;
               final String signature;
               final int status;

               byte eventKind() {
                  return 8;
               }

               ClassPrepare(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.refTypeTag = var2.readByte();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "refTypeTag(byte): " + this.refTypeTag);
                  }

                  this.typeID = var2.readClassRef();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "typeID(long): ref=" + this.typeID);
                  }

                  this.signature = var2.readString();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "signature(String): " + this.signature);
                  }

                  this.status = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "status(int): " + this.status);
                  }

               }
            }

            static class ThreadDeath extends EventsCommon {
               static final byte ALT_ID = 7;
               final int requestID;
               final ThreadReferenceImpl thread;

               byte eventKind() {
                  return 7;
               }

               ThreadDeath(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

               }
            }

            static class ThreadStart extends EventsCommon {
               static final byte ALT_ID = 6;
               final int requestID;
               final ThreadReferenceImpl thread;

               byte eventKind() {
                  return 6;
               }

               ThreadStart(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

               }
            }

            static class Exception extends EventsCommon {
               static final byte ALT_ID = 4;
               final int requestID;
               final ThreadReferenceImpl thread;
               final Location location;
               final ObjectReferenceImpl exception;
               final Location catchLocation;

               byte eventKind() {
                  return 4;
               }

               Exception(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

                  this.exception = var2.readTaggedObjectReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "exception(ObjectReferenceImpl): " + (this.exception == null ? "NULL" : "ref=" + this.exception.ref()));
                  }

                  this.catchLocation = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "catchLocation(Location): " + this.catchLocation);
                  }

               }
            }

            static class MonitorWaited extends EventsCommon {
               static final byte ALT_ID = 46;
               final int requestID;
               final ThreadReferenceImpl thread;
               final ObjectReferenceImpl object;
               final Location location;
               final boolean timed_out;

               byte eventKind() {
                  return 46;
               }

               MonitorWaited(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.object = var2.readTaggedObjectReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "object(ObjectReferenceImpl): " + (this.object == null ? "NULL" : "ref=" + this.object.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

                  this.timed_out = var2.readBoolean();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "timed_out(boolean): " + this.timed_out);
                  }

               }
            }

            static class MonitorWait extends EventsCommon {
               static final byte ALT_ID = 45;
               final int requestID;
               final ThreadReferenceImpl thread;
               final ObjectReferenceImpl object;
               final Location location;
               final long timeout;

               byte eventKind() {
                  return 45;
               }

               MonitorWait(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.object = var2.readTaggedObjectReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "object(ObjectReferenceImpl): " + (this.object == null ? "NULL" : "ref=" + this.object.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

                  this.timeout = var2.readLong();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "timeout(long): " + this.timeout);
                  }

               }
            }

            static class MonitorContendedEntered extends EventsCommon {
               static final byte ALT_ID = 44;
               final int requestID;
               final ThreadReferenceImpl thread;
               final ObjectReferenceImpl object;
               final Location location;

               byte eventKind() {
                  return 44;
               }

               MonitorContendedEntered(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.object = var2.readTaggedObjectReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "object(ObjectReferenceImpl): " + (this.object == null ? "NULL" : "ref=" + this.object.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

               }
            }

            static class MonitorContendedEnter extends EventsCommon {
               static final byte ALT_ID = 43;
               final int requestID;
               final ThreadReferenceImpl thread;
               final ObjectReferenceImpl object;
               final Location location;

               byte eventKind() {
                  return 43;
               }

               MonitorContendedEnter(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.object = var2.readTaggedObjectReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "object(ObjectReferenceImpl): " + (this.object == null ? "NULL" : "ref=" + this.object.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

               }
            }

            static class MethodExitWithReturnValue extends EventsCommon {
               static final byte ALT_ID = 42;
               final int requestID;
               final ThreadReferenceImpl thread;
               final Location location;
               final ValueImpl value;

               byte eventKind() {
                  return 42;
               }

               MethodExitWithReturnValue(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

                  this.value = var2.readValue();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "value(ValueImpl): " + this.value);
                  }

               }
            }

            static class MethodExit extends EventsCommon {
               static final byte ALT_ID = 41;
               final int requestID;
               final ThreadReferenceImpl thread;
               final Location location;

               byte eventKind() {
                  return 41;
               }

               MethodExit(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

               }
            }

            static class MethodEntry extends EventsCommon {
               static final byte ALT_ID = 40;
               final int requestID;
               final ThreadReferenceImpl thread;
               final Location location;

               byte eventKind() {
                  return 40;
               }

               MethodEntry(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

               }
            }

            static class Breakpoint extends EventsCommon {
               static final byte ALT_ID = 2;
               final int requestID;
               final ThreadReferenceImpl thread;
               final Location location;

               byte eventKind() {
                  return 2;
               }

               Breakpoint(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

               }
            }

            static class SingleStep extends EventsCommon {
               static final byte ALT_ID = 1;
               final int requestID;
               final ThreadReferenceImpl thread;
               final Location location;

               byte eventKind() {
                  return 1;
               }

               SingleStep(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  this.location = var2.readLocation();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "location(Location): " + this.location);
                  }

               }
            }

            static class VMStart extends EventsCommon {
               static final byte ALT_ID = 90;
               final int requestID;
               final ThreadReferenceImpl thread;

               byte eventKind() {
                  return 90;
               }

               VMStart(VirtualMachineImpl var1, PacketStream var2) {
                  this.requestID = var2.readInt();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "requestID(int): " + this.requestID);
                  }

                  this.thread = var2.readThreadReference();
                  if (var1.traceReceives) {
                     var1.printReceiveTrace(6, "thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

               }
            }

            abstract static class EventsCommon {
               abstract byte eventKind();
            }
         }
      }
   }

   static class ClassObjectReference {
      static final int COMMAND_SET = 17;

      private ClassObjectReference() {
      }

      static class ReflectedType {
         static final int COMMAND = 1;
         final byte refTypeTag;
         final long typeID;

         static ReflectedType process(VirtualMachineImpl var0, ClassObjectReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ClassObjectReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 17, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ClassObjectReference.ReflectedType" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 classObject(ClassObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static ReflectedType waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ReflectedType(var0, var1);
         }

         private ReflectedType(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ClassObjectReference.ReflectedType" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.refTypeTag = var2.readByte();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "refTypeTag(byte): " + this.refTypeTag);
            }

            this.typeID = var2.readClassRef();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "typeID(long): ref=" + this.typeID);
            }

         }
      }
   }

   static class StackFrame {
      static final int COMMAND_SET = 16;

      private StackFrame() {
      }

      static class PopFrames {
         static final int COMMAND = 4;

         static PopFrames process(VirtualMachineImpl var0, ThreadReferenceImpl var1, long var2) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1, long var2) {
            PacketStream var4 = new PacketStream(var0, 16, 4);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.StackFrame.PopFrames" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var4.writeObjectRef(var1.ref());
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 frame(long): " + var2);
            }

            var4.writeFrameRef(var2);
            var4.send();
            return var4;
         }

         static PopFrames waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new PopFrames(var0, var1);
         }

         private PopFrames(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.StackFrame.PopFrames" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class ThisObject {
         static final int COMMAND = 3;
         final ObjectReferenceImpl objectThis;

         static ThisObject process(VirtualMachineImpl var0, ThreadReferenceImpl var1, long var2) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1, long var2) {
            PacketStream var4 = new PacketStream(var0, 16, 3);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.StackFrame.ThisObject" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var4.writeObjectRef(var1.ref());
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 frame(long): " + var2);
            }

            var4.writeFrameRef(var2);
            var4.send();
            return var4;
         }

         static ThisObject waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ThisObject(var0, var1);
         }

         private ThisObject(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.StackFrame.ThisObject" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.objectThis = var2.readTaggedObjectReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "objectThis(ObjectReferenceImpl): " + (this.objectThis == null ? "NULL" : "ref=" + this.objectThis.ref()));
            }

         }
      }

      static class SetValues {
         static final int COMMAND = 2;

         static SetValues process(VirtualMachineImpl var0, ThreadReferenceImpl var1, long var2, SlotInfo[] var4) throws JDWPException {
            PacketStream var5 = enqueueCommand(var0, var1, var2, var4);
            return waitForReply(var0, var5);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1, long var2, SlotInfo[] var4) {
            PacketStream var5 = new PacketStream(var0, 16, 2);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var5.pkt.id + ") JDWP.StackFrame.SetValues" + (var5.pkt.flags != 0 ? ", FLAGS=" + var5.pkt.flags : ""));
            }

            if ((var5.vm.traceFlags & 1) != 0) {
               var5.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var5.writeObjectRef(var1.ref());
            if ((var5.vm.traceFlags & 1) != 0) {
               var5.vm.printTrace("Sending:                 frame(long): " + var2);
            }

            var5.writeFrameRef(var2);
            if ((var5.vm.traceFlags & 1) != 0) {
               var5.vm.printTrace("Sending:                 slotValues(SlotInfo[]): ");
            }

            var5.writeInt(var4.length);

            for(int var6 = 0; var6 < var4.length; ++var6) {
               if ((var5.vm.traceFlags & 1) != 0) {
                  var5.vm.printTrace("Sending:                     slotValues[i](SlotInfo): ");
               }

               var4[var6].write(var5);
            }

            var5.send();
            return var5;
         }

         static SetValues waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new SetValues(var0, var1);
         }

         private SetValues(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.StackFrame.SetValues" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }

         static class SlotInfo {
            final int slot;
            final ValueImpl slotValue;

            SlotInfo(int var1, ValueImpl var2) {
               this.slot = var1;
               this.slotValue = var2;
            }

            private void write(PacketStream var1) {
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     slot(int): " + this.slot);
               }

               var1.writeInt(this.slot);
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     slotValue(ValueImpl): " + this.slotValue);
               }

               var1.writeValue(this.slotValue);
            }
         }
      }

      static class GetValues {
         static final int COMMAND = 1;
         final ValueImpl[] values;

         static GetValues process(VirtualMachineImpl var0, ThreadReferenceImpl var1, long var2, SlotInfo[] var4) throws JDWPException {
            PacketStream var5 = enqueueCommand(var0, var1, var2, var4);
            return waitForReply(var0, var5);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1, long var2, SlotInfo[] var4) {
            PacketStream var5 = new PacketStream(var0, 16, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var5.pkt.id + ") JDWP.StackFrame.GetValues" + (var5.pkt.flags != 0 ? ", FLAGS=" + var5.pkt.flags : ""));
            }

            if ((var5.vm.traceFlags & 1) != 0) {
               var5.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var5.writeObjectRef(var1.ref());
            if ((var5.vm.traceFlags & 1) != 0) {
               var5.vm.printTrace("Sending:                 frame(long): " + var2);
            }

            var5.writeFrameRef(var2);
            if ((var5.vm.traceFlags & 1) != 0) {
               var5.vm.printTrace("Sending:                 slots(SlotInfo[]): ");
            }

            var5.writeInt(var4.length);

            for(int var6 = 0; var6 < var4.length; ++var6) {
               if ((var5.vm.traceFlags & 1) != 0) {
                  var5.vm.printTrace("Sending:                     slots[i](SlotInfo): ");
               }

               var4[var6].write(var5);
            }

            var5.send();
            return var5;
         }

         static GetValues waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new GetValues(var0, var1);
         }

         private GetValues(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.StackFrame.GetValues" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "values(ValueImpl[]): ");
            }

            int var3 = var2.readInt();
            this.values = new ValueImpl[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.values[var4] = var2.readValue();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "values[i](ValueImpl): " + this.values[var4]);
               }
            }

         }

         static class SlotInfo {
            final int slot;
            final byte sigbyte;

            SlotInfo(int var1, byte var2) {
               this.slot = var1;
               this.sigbyte = var2;
            }

            private void write(PacketStream var1) {
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     slot(int): " + this.slot);
               }

               var1.writeInt(this.slot);
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     sigbyte(byte): " + this.sigbyte);
               }

               var1.writeByte(this.sigbyte);
            }
         }
      }
   }

   static class EventRequest {
      static final int COMMAND_SET = 15;

      private EventRequest() {
      }

      static class ClearAllBreakpoints {
         static final int COMMAND = 3;

         static ClearAllBreakpoints process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 15, 3);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.EventRequest.ClearAllBreakpoints" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static ClearAllBreakpoints waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ClearAllBreakpoints(var0, var1);
         }

         private ClearAllBreakpoints(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.EventRequest.ClearAllBreakpoints" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class Clear {
         static final int COMMAND = 2;

         static Clear process(VirtualMachineImpl var0, byte var1, int var2) throws JDWPException {
            PacketStream var3 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var3);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, byte var1, int var2) {
            PacketStream var3 = new PacketStream(var0, 15, 2);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var3.pkt.id + ") JDWP.EventRequest.Clear" + (var3.pkt.flags != 0 ? ", FLAGS=" + var3.pkt.flags : ""));
            }

            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 eventKind(byte): " + var1);
            }

            var3.writeByte(var1);
            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 requestID(int): " + var2);
            }

            var3.writeInt(var2);
            var3.send();
            return var3;
         }

         static Clear waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Clear(var0, var1);
         }

         private Clear(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.EventRequest.Clear" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class Set {
         static final int COMMAND = 1;
         final int requestID;

         static Set process(VirtualMachineImpl var0, byte var1, byte var2, Modifier[] var3) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2, var3);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, byte var1, byte var2, Modifier[] var3) {
            PacketStream var4 = new PacketStream(var0, 15, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.EventRequest.Set" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 eventKind(byte): " + var1);
            }

            var4.writeByte(var1);
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 suspendPolicy(byte): " + var2);
            }

            var4.writeByte(var2);
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 modifiers(Modifier[]): ");
            }

            var4.writeInt(var3.length);

            for(int var5 = 0; var5 < var3.length; ++var5) {
               if ((var4.vm.traceFlags & 1) != 0) {
                  var4.vm.printTrace("Sending:                     modifiers[i](Modifier): ");
               }

               var3[var5].write(var4);
            }

            var4.send();
            return var4;
         }

         static Set waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Set(var0, var1);
         }

         private Set(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.EventRequest.Set" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.requestID = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "requestID(int): " + this.requestID);
            }

         }

         static class Modifier {
            final byte modKind;
            ModifierCommon aModifierCommon;

            Modifier(byte var1, ModifierCommon var2) {
               this.modKind = var1;
               this.aModifierCommon = var2;
            }

            private void write(PacketStream var1) {
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     modKind(byte): " + this.modKind);
               }

               var1.writeByte(this.modKind);
               this.aModifierCommon.write(var1);
            }

            static class SourceNameMatch extends ModifierCommon {
               static final byte ALT_ID = 12;
               final String sourceNamePattern;

               static Modifier create(String var0) {
                  return new Modifier((byte)12, new SourceNameMatch(var0));
               }

               SourceNameMatch(String var1) {
                  this.sourceNamePattern = var1;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         sourceNamePattern(String): " + this.sourceNamePattern);
                  }

                  var1.writeString(this.sourceNamePattern);
               }
            }

            static class InstanceOnly extends ModifierCommon {
               static final byte ALT_ID = 11;
               final ObjectReferenceImpl instance;

               static Modifier create(ObjectReferenceImpl var0) {
                  return new Modifier((byte)11, new InstanceOnly(var0));
               }

               InstanceOnly(ObjectReferenceImpl var1) {
                  this.instance = var1;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         instance(ObjectReferenceImpl): " + (this.instance == null ? "NULL" : "ref=" + this.instance.ref()));
                  }

                  var1.writeObjectRef(this.instance.ref());
               }
            }

            static class Step extends ModifierCommon {
               static final byte ALT_ID = 10;
               final ThreadReferenceImpl thread;
               final int size;
               final int depth;

               static Modifier create(ThreadReferenceImpl var0, int var1, int var2) {
                  return new Modifier((byte)10, new Step(var0, var1, var2));
               }

               Step(ThreadReferenceImpl var1, int var2, int var3) {
                  this.thread = var1;
                  this.size = var2;
                  this.depth = var3;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  var1.writeObjectRef(this.thread.ref());
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         size(int): " + this.size);
                  }

                  var1.writeInt(this.size);
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         depth(int): " + this.depth);
                  }

                  var1.writeInt(this.depth);
               }
            }

            static class FieldOnly extends ModifierCommon {
               static final byte ALT_ID = 9;
               final ReferenceTypeImpl declaring;
               final long fieldID;

               static Modifier create(ReferenceTypeImpl var0, long var1) {
                  return new Modifier((byte)9, new FieldOnly(var0, var1));
               }

               FieldOnly(ReferenceTypeImpl var1, long var2) {
                  this.declaring = var1;
                  this.fieldID = var2;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         declaring(ReferenceTypeImpl): " + (this.declaring == null ? "NULL" : "ref=" + this.declaring.ref()));
                  }

                  var1.writeClassRef(this.declaring.ref());
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         fieldID(long): " + this.fieldID);
                  }

                  var1.writeFieldRef(this.fieldID);
               }
            }

            static class ExceptionOnly extends ModifierCommon {
               static final byte ALT_ID = 8;
               final ReferenceTypeImpl exceptionOrNull;
               final boolean caught;
               final boolean uncaught;

               static Modifier create(ReferenceTypeImpl var0, boolean var1, boolean var2) {
                  return new Modifier((byte)8, new ExceptionOnly(var0, var1, var2));
               }

               ExceptionOnly(ReferenceTypeImpl var1, boolean var2, boolean var3) {
                  this.exceptionOrNull = var1;
                  this.caught = var2;
                  this.uncaught = var3;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         exceptionOrNull(ReferenceTypeImpl): " + (this.exceptionOrNull == null ? "NULL" : "ref=" + this.exceptionOrNull.ref()));
                  }

                  var1.writeClassRef(this.exceptionOrNull.ref());
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         caught(boolean): " + this.caught);
                  }

                  var1.writeBoolean(this.caught);
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         uncaught(boolean): " + this.uncaught);
                  }

                  var1.writeBoolean(this.uncaught);
               }
            }

            static class LocationOnly extends ModifierCommon {
               static final byte ALT_ID = 7;
               final Location loc;

               static Modifier create(Location var0) {
                  return new Modifier((byte)7, new LocationOnly(var0));
               }

               LocationOnly(Location var1) {
                  this.loc = var1;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         loc(Location): " + this.loc);
                  }

                  var1.writeLocation(this.loc);
               }
            }

            static class ClassExclude extends ModifierCommon {
               static final byte ALT_ID = 6;
               final String classPattern;

               static Modifier create(String var0) {
                  return new Modifier((byte)6, new ClassExclude(var0));
               }

               ClassExclude(String var1) {
                  this.classPattern = var1;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         classPattern(String): " + this.classPattern);
                  }

                  var1.writeString(this.classPattern);
               }
            }

            static class ClassMatch extends ModifierCommon {
               static final byte ALT_ID = 5;
               final String classPattern;

               static Modifier create(String var0) {
                  return new Modifier((byte)5, new ClassMatch(var0));
               }

               ClassMatch(String var1) {
                  this.classPattern = var1;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         classPattern(String): " + this.classPattern);
                  }

                  var1.writeString(this.classPattern);
               }
            }

            static class ClassOnly extends ModifierCommon {
               static final byte ALT_ID = 4;
               final ReferenceTypeImpl clazz;

               static Modifier create(ReferenceTypeImpl var0) {
                  return new Modifier((byte)4, new ClassOnly(var0));
               }

               ClassOnly(ReferenceTypeImpl var1) {
                  this.clazz = var1;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         clazz(ReferenceTypeImpl): " + (this.clazz == null ? "NULL" : "ref=" + this.clazz.ref()));
                  }

                  var1.writeClassRef(this.clazz.ref());
               }
            }

            static class ThreadOnly extends ModifierCommon {
               static final byte ALT_ID = 3;
               final ThreadReferenceImpl thread;

               static Modifier create(ThreadReferenceImpl var0) {
                  return new Modifier((byte)3, new ThreadOnly(var0));
               }

               ThreadOnly(ThreadReferenceImpl var1) {
                  this.thread = var1;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         thread(ThreadReferenceImpl): " + (this.thread == null ? "NULL" : "ref=" + this.thread.ref()));
                  }

                  var1.writeObjectRef(this.thread.ref());
               }
            }

            static class Conditional extends ModifierCommon {
               static final byte ALT_ID = 2;
               final int exprID;

               static Modifier create(int var0) {
                  return new Modifier((byte)2, new Conditional(var0));
               }

               Conditional(int var1) {
                  this.exprID = var1;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         exprID(int): " + this.exprID);
                  }

                  var1.writeInt(this.exprID);
               }
            }

            static class Count extends ModifierCommon {
               static final byte ALT_ID = 1;
               final int count;

               static Modifier create(int var0) {
                  return new Modifier((byte)1, new Count(var0));
               }

               Count(int var1) {
                  this.count = var1;
               }

               void write(PacketStream var1) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         count(int): " + this.count);
                  }

                  var1.writeInt(this.count);
               }
            }

            abstract static class ModifierCommon {
               abstract void write(PacketStream var1);
            }
         }
      }
   }

   static class ClassLoaderReference {
      static final int COMMAND_SET = 14;

      private ClassLoaderReference() {
      }

      static class VisibleClasses {
         static final int COMMAND = 1;
         final ClassInfo[] classes;

         static VisibleClasses process(VirtualMachineImpl var0, ClassLoaderReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ClassLoaderReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 14, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ClassLoaderReference.VisibleClasses" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 classLoaderObject(ClassLoaderReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static VisibleClasses waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new VisibleClasses(var0, var1);
         }

         private VisibleClasses(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ClassLoaderReference.VisibleClasses" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "classes(ClassInfo[]): ");
            }

            int var3 = var2.readInt();
            this.classes = new ClassInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "classes[i](ClassInfo): ");
               }

               this.classes[var4] = new ClassInfo(var1, var2);
            }

         }

         static class ClassInfo {
            final byte refTypeTag;
            final long typeID;

            private ClassInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.refTypeTag = var2.readByte();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "refTypeTag(byte): " + this.refTypeTag);
               }

               this.typeID = var2.readClassRef();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "typeID(long): ref=" + this.typeID);
               }

            }

            // $FF: synthetic method
            ClassInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }
   }

   static class ArrayReference {
      static final int COMMAND_SET = 13;

      private ArrayReference() {
      }

      static class SetValues {
         static final int COMMAND = 3;

         static SetValues process(VirtualMachineImpl var0, ArrayReferenceImpl var1, int var2, ValueImpl[] var3) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2, var3);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ArrayReferenceImpl var1, int var2, ValueImpl[] var3) {
            PacketStream var4 = new PacketStream(var0, 13, 3);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.ArrayReference.SetValues" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 arrayObject(ArrayReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var4.writeObjectRef(var1.ref());
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 firstIndex(int): " + var2);
            }

            var4.writeInt(var2);
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 values(ValueImpl[]): ");
            }

            var4.writeInt(var3.length);

            for(int var5 = 0; var5 < var3.length; ++var5) {
               if ((var4.vm.traceFlags & 1) != 0) {
                  var4.vm.printTrace("Sending:                     values[i](ValueImpl): " + var3[var5]);
               }

               var4.writeUntaggedValue(var3[var5]);
            }

            var4.send();
            return var4;
         }

         static SetValues waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new SetValues(var0, var1);
         }

         private SetValues(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ArrayReference.SetValues" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class GetValues {
         static final int COMMAND = 2;
         final List values;

         static GetValues process(VirtualMachineImpl var0, ArrayReferenceImpl var1, int var2, int var3) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2, var3);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ArrayReferenceImpl var1, int var2, int var3) {
            PacketStream var4 = new PacketStream(var0, 13, 2);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.ArrayReference.GetValues" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 arrayObject(ArrayReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var4.writeObjectRef(var1.ref());
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 firstIndex(int): " + var2);
            }

            var4.writeInt(var2);
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 length(int): " + var3);
            }

            var4.writeInt(var3);
            var4.send();
            return var4;
         }

         static GetValues waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new GetValues(var0, var1);
         }

         private GetValues(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ArrayReference.GetValues" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.values = var2.readArrayRegion();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "values(List<?>): " + this.values);
            }

         }
      }

      static class Length {
         static final int COMMAND = 1;
         final int arrayLength;

         static Length process(VirtualMachineImpl var0, ArrayReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ArrayReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 13, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ArrayReference.Length" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 arrayObject(ArrayReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static Length waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Length(var0, var1);
         }

         private Length(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ArrayReference.Length" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.arrayLength = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "arrayLength(int): " + this.arrayLength);
            }

         }
      }
   }

   static class ThreadGroupReference {
      static final int COMMAND_SET = 12;

      private ThreadGroupReference() {
      }

      static class Children {
         static final int COMMAND = 3;
         final ThreadReferenceImpl[] childThreads;
         final ThreadGroupReferenceImpl[] childGroups;

         static Children process(VirtualMachineImpl var0, ThreadGroupReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadGroupReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 12, 3);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadGroupReference.Children" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 group(ThreadGroupReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static Children waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Children(var0, var1);
         }

         private Children(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadGroupReference.Children" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "childThreads(ThreadReferenceImpl[]): ");
            }

            int var3 = var2.readInt();
            this.childThreads = new ThreadReferenceImpl[var3];

            int var4;
            for(var4 = 0; var4 < var3; ++var4) {
               this.childThreads[var4] = var2.readThreadReference();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "childThreads[i](ThreadReferenceImpl): " + (this.childThreads[var4] == null ? "NULL" : "ref=" + this.childThreads[var4].ref()));
               }
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "childGroups(ThreadGroupReferenceImpl[]): ");
            }

            var4 = var2.readInt();
            this.childGroups = new ThreadGroupReferenceImpl[var4];

            for(int var5 = 0; var5 < var4; ++var5) {
               this.childGroups[var5] = var2.readThreadGroupReference();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "childGroups[i](ThreadGroupReferenceImpl): " + (this.childGroups[var5] == null ? "NULL" : "ref=" + this.childGroups[var5].ref()));
               }
            }

         }
      }

      static class Parent {
         static final int COMMAND = 2;
         final ThreadGroupReferenceImpl parentGroup;

         static Parent process(VirtualMachineImpl var0, ThreadGroupReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadGroupReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 12, 2);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadGroupReference.Parent" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 group(ThreadGroupReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static Parent waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Parent(var0, var1);
         }

         private Parent(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadGroupReference.Parent" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.parentGroup = var2.readThreadGroupReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "parentGroup(ThreadGroupReferenceImpl): " + (this.parentGroup == null ? "NULL" : "ref=" + this.parentGroup.ref()));
            }

         }
      }

      static class Name {
         static final int COMMAND = 1;
         final String groupName;

         static Name process(VirtualMachineImpl var0, ThreadGroupReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadGroupReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 12, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadGroupReference.Name" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 group(ThreadGroupReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static Name waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Name(var0, var1);
         }

         private Name(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadGroupReference.Name" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.groupName = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "groupName(String): " + this.groupName);
            }

         }
      }
   }

   static class ThreadReference {
      static final int COMMAND_SET = 11;

      private ThreadReference() {
      }

      static class ForceEarlyReturn {
         static final int COMMAND = 14;

         static ForceEarlyReturn process(VirtualMachineImpl var0, ThreadReferenceImpl var1, ValueImpl var2) throws JDWPException {
            PacketStream var3 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var3);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1, ValueImpl var2) {
            PacketStream var3 = new PacketStream(var0, 11, 14);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var3.pkt.id + ") JDWP.ThreadReference.ForceEarlyReturn" + (var3.pkt.flags != 0 ? ", FLAGS=" + var3.pkt.flags : ""));
            }

            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var3.writeObjectRef(var1.ref());
            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 value(ValueImpl): " + var2);
            }

            var3.writeValue(var2);
            var3.send();
            return var3;
         }

         static ForceEarlyReturn waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ForceEarlyReturn(var0, var1);
         }

         private ForceEarlyReturn(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.ForceEarlyReturn" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class OwnedMonitorsStackDepthInfo {
         static final int COMMAND = 13;
         final monitor[] owned;

         static OwnedMonitorsStackDepthInfo process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 13);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.OwnedMonitorsStackDepthInfo" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static OwnedMonitorsStackDepthInfo waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new OwnedMonitorsStackDepthInfo(var0, var1);
         }

         private OwnedMonitorsStackDepthInfo(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.OwnedMonitorsStackDepthInfo" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "owned(monitor[]): ");
            }

            int var3 = var2.readInt();
            this.owned = new monitor[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "owned[i](monitor): ");
               }

               this.owned[var4] = new monitor(var1, var2);
            }

         }

         static class monitor {
            final ObjectReferenceImpl monitor;
            final int stack_depth;

            private monitor(VirtualMachineImpl var1, PacketStream var2) {
               this.monitor = var2.readTaggedObjectReference();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "monitor(ObjectReferenceImpl): " + (this.monitor == null ? "NULL" : "ref=" + this.monitor.ref()));
               }

               this.stack_depth = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "stack_depth(int): " + this.stack_depth);
               }

            }

            // $FF: synthetic method
            monitor(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class SuspendCount {
         static final int COMMAND = 12;
         final int suspendCount;

         static SuspendCount process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 12);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.SuspendCount" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static SuspendCount waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new SuspendCount(var0, var1);
         }

         private SuspendCount(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.SuspendCount" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.suspendCount = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "suspendCount(int): " + this.suspendCount);
            }

         }
      }

      static class Interrupt {
         static final int COMMAND = 11;

         static Interrupt process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 11);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Interrupt" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static Interrupt waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Interrupt(var0, var1);
         }

         private Interrupt(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Interrupt" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class Stop {
         static final int COMMAND = 10;

         static Stop process(VirtualMachineImpl var0, ThreadReferenceImpl var1, ObjectReferenceImpl var2) throws JDWPException {
            PacketStream var3 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var3);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1, ObjectReferenceImpl var2) {
            PacketStream var3 = new PacketStream(var0, 11, 10);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var3.pkt.id + ") JDWP.ThreadReference.Stop" + (var3.pkt.flags != 0 ? ", FLAGS=" + var3.pkt.flags : ""));
            }

            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var3.writeObjectRef(var1.ref());
            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 throwable(ObjectReferenceImpl): " + (var2 == null ? "NULL" : "ref=" + var2.ref()));
            }

            var3.writeObjectRef(var2.ref());
            var3.send();
            return var3;
         }

         static Stop waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Stop(var0, var1);
         }

         private Stop(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Stop" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class CurrentContendedMonitor {
         static final int COMMAND = 9;
         final ObjectReferenceImpl monitor;

         static CurrentContendedMonitor process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 9);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.CurrentContendedMonitor" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static CurrentContendedMonitor waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new CurrentContendedMonitor(var0, var1);
         }

         private CurrentContendedMonitor(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.CurrentContendedMonitor" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.monitor = var2.readTaggedObjectReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "monitor(ObjectReferenceImpl): " + (this.monitor == null ? "NULL" : "ref=" + this.monitor.ref()));
            }

         }
      }

      static class OwnedMonitors {
         static final int COMMAND = 8;
         final ObjectReferenceImpl[] owned;

         static OwnedMonitors process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 8);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.OwnedMonitors" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static OwnedMonitors waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new OwnedMonitors(var0, var1);
         }

         private OwnedMonitors(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.OwnedMonitors" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "owned(ObjectReferenceImpl[]): ");
            }

            int var3 = var2.readInt();
            this.owned = new ObjectReferenceImpl[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.owned[var4] = var2.readTaggedObjectReference();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "owned[i](ObjectReferenceImpl): " + (this.owned[var4] == null ? "NULL" : "ref=" + this.owned[var4].ref()));
               }
            }

         }
      }

      static class FrameCount {
         static final int COMMAND = 7;
         final int frameCount;

         static FrameCount process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 7);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.FrameCount" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static FrameCount waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new FrameCount(var0, var1);
         }

         private FrameCount(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.FrameCount" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.frameCount = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "frameCount(int): " + this.frameCount);
            }

         }
      }

      static class Frames {
         static final int COMMAND = 6;
         final Frame[] frames;

         static Frames process(VirtualMachineImpl var0, ThreadReferenceImpl var1, int var2, int var3) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2, var3);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1, int var2, int var3) {
            PacketStream var4 = new PacketStream(var0, 11, 6);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.ThreadReference.Frames" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var4.writeObjectRef(var1.ref());
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 startFrame(int): " + var2);
            }

            var4.writeInt(var2);
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 length(int): " + var3);
            }

            var4.writeInt(var3);
            var4.send();
            return var4;
         }

         static Frames waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Frames(var0, var1);
         }

         private Frames(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Frames" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "frames(Frame[]): ");
            }

            int var3 = var2.readInt();
            this.frames = new Frame[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "frames[i](Frame): ");
               }

               this.frames[var4] = new Frame(var1, var2);
            }

         }

         static class Frame {
            final long frameID;
            final Location location;

            private Frame(VirtualMachineImpl var1, PacketStream var2) {
               this.frameID = var2.readFrameRef();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "frameID(long): " + this.frameID);
               }

               this.location = var2.readLocation();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "location(Location): " + this.location);
               }

            }

            // $FF: synthetic method
            Frame(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class ThreadGroup {
         static final int COMMAND = 5;
         final ThreadGroupReferenceImpl group;

         static ThreadGroup process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 5);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.ThreadGroup" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static ThreadGroup waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ThreadGroup(var0, var1);
         }

         private ThreadGroup(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.ThreadGroup" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.group = var2.readThreadGroupReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "group(ThreadGroupReferenceImpl): " + (this.group == null ? "NULL" : "ref=" + this.group.ref()));
            }

         }
      }

      static class Status {
         static final int COMMAND = 4;
         final int threadStatus;
         final int suspendStatus;

         static Status process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 4);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Status" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static Status waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Status(var0, var1);
         }

         private Status(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Status" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.threadStatus = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "threadStatus(int): " + this.threadStatus);
            }

            this.suspendStatus = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "suspendStatus(int): " + this.suspendStatus);
            }

         }
      }

      static class Resume {
         static final int COMMAND = 3;

         static Resume process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 3);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Resume" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static Resume waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Resume(var0, var1);
         }

         private Resume(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Resume" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class Suspend {
         static final int COMMAND = 2;

         static Suspend process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 2);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Suspend" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static Suspend waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Suspend(var0, var1);
         }

         private Suspend(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Suspend" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class Name {
         static final int COMMAND = 1;
         final String threadName;

         static Name process(VirtualMachineImpl var0, ThreadReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ThreadReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 11, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Name" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static Name waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Name(var0, var1);
         }

         private Name(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ThreadReference.Name" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.threadName = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "threadName(String): " + this.threadName);
            }

         }
      }
   }

   static class StringReference {
      static final int COMMAND_SET = 10;

      private StringReference() {
      }

      static class Value {
         static final int COMMAND = 1;
         final String stringValue;

         static Value process(VirtualMachineImpl var0, ObjectReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ObjectReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 10, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.StringReference.Value" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 stringObject(ObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static Value waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Value(var0, var1);
         }

         private Value(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.StringReference.Value" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.stringValue = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "stringValue(String): " + this.stringValue);
            }

         }
      }
   }

   static class ObjectReference {
      static final int COMMAND_SET = 9;

      private ObjectReference() {
      }

      static class ReferringObjects {
         static final int COMMAND = 10;
         final ObjectReferenceImpl[] referringObjects;

         static ReferringObjects process(VirtualMachineImpl var0, ObjectReferenceImpl var1, int var2) throws JDWPException {
            PacketStream var3 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var3);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ObjectReferenceImpl var1, int var2) {
            PacketStream var3 = new PacketStream(var0, 9, 10);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var3.pkt.id + ") JDWP.ObjectReference.ReferringObjects" + (var3.pkt.flags != 0 ? ", FLAGS=" + var3.pkt.flags : ""));
            }

            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 object(ObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var3.writeObjectRef(var1.ref());
            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 maxReferrers(int): " + var2);
            }

            var3.writeInt(var2);
            var3.send();
            return var3;
         }

         static ReferringObjects waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ReferringObjects(var0, var1);
         }

         private ReferringObjects(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.ReferringObjects" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "referringObjects(ObjectReferenceImpl[]): ");
            }

            int var3 = var2.readInt();
            this.referringObjects = new ObjectReferenceImpl[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.referringObjects[var4] = var2.readTaggedObjectReference();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "referringObjects[i](ObjectReferenceImpl): " + (this.referringObjects[var4] == null ? "NULL" : "ref=" + this.referringObjects[var4].ref()));
               }
            }

         }
      }

      static class IsCollected {
         static final int COMMAND = 9;
         final boolean isCollected;

         static IsCollected process(VirtualMachineImpl var0, ObjectReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ObjectReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 9, 9);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.IsCollected" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 object(ObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static IsCollected waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new IsCollected(var0, var1);
         }

         private IsCollected(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.IsCollected" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.isCollected = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "isCollected(boolean): " + this.isCollected);
            }

         }
      }

      static class EnableCollection {
         static final int COMMAND = 8;

         static EnableCollection process(VirtualMachineImpl var0, ObjectReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ObjectReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 9, 8);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.EnableCollection" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 object(ObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static EnableCollection waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new EnableCollection(var0, var1);
         }

         private EnableCollection(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.EnableCollection" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class DisableCollection {
         static final int COMMAND = 7;

         static DisableCollection process(VirtualMachineImpl var0, ObjectReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ObjectReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 9, 7);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.DisableCollection" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 object(ObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static DisableCollection waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new DisableCollection(var0, var1);
         }

         private DisableCollection(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.DisableCollection" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class InvokeMethod {
         static final int COMMAND = 6;
         final ValueImpl returnValue;
         final ObjectReferenceImpl exception;

         static InvokeMethod process(VirtualMachineImpl var0, ObjectReferenceImpl var1, ThreadReferenceImpl var2, ClassTypeImpl var3, long var4, ValueImpl[] var6, int var7) throws JDWPException {
            PacketStream var8 = enqueueCommand(var0, var1, var2, var3, var4, var6, var7);
            return waitForReply(var0, var8);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ObjectReferenceImpl var1, ThreadReferenceImpl var2, ClassTypeImpl var3, long var4, ValueImpl[] var6, int var7) {
            PacketStream var8 = new PacketStream(var0, 9, 6);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var8.pkt.id + ") JDWP.ObjectReference.InvokeMethod" + (var8.pkt.flags != 0 ? ", FLAGS=" + var8.pkt.flags : ""));
            }

            if ((var8.vm.traceFlags & 1) != 0) {
               var8.vm.printTrace("Sending:                 object(ObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var8.writeObjectRef(var1.ref());
            if ((var8.vm.traceFlags & 1) != 0) {
               var8.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var2 == null ? "NULL" : "ref=" + var2.ref()));
            }

            var8.writeObjectRef(var2.ref());
            if ((var8.vm.traceFlags & 1) != 0) {
               var8.vm.printTrace("Sending:                 clazz(ClassTypeImpl): " + (var3 == null ? "NULL" : "ref=" + var3.ref()));
            }

            var8.writeClassRef(var3.ref());
            if ((var8.vm.traceFlags & 1) != 0) {
               var8.vm.printTrace("Sending:                 methodID(long): " + var4);
            }

            var8.writeMethodRef(var4);
            if ((var8.vm.traceFlags & 1) != 0) {
               var8.vm.printTrace("Sending:                 arguments(ValueImpl[]): ");
            }

            var8.writeInt(var6.length);

            for(int var9 = 0; var9 < var6.length; ++var9) {
               if ((var8.vm.traceFlags & 1) != 0) {
                  var8.vm.printTrace("Sending:                     arguments[i](ValueImpl): " + var6[var9]);
               }

               var8.writeValue(var6[var9]);
            }

            if ((var8.vm.traceFlags & 1) != 0) {
               var8.vm.printTrace("Sending:                 options(int): " + var7);
            }

            var8.writeInt(var7);
            var8.send();
            return var8;
         }

         static InvokeMethod waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new InvokeMethod(var0, var1);
         }

         private InvokeMethod(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.InvokeMethod" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.returnValue = var2.readValue();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "returnValue(ValueImpl): " + this.returnValue);
            }

            this.exception = var2.readTaggedObjectReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "exception(ObjectReferenceImpl): " + (this.exception == null ? "NULL" : "ref=" + this.exception.ref()));
            }

         }
      }

      static class MonitorInfo {
         static final int COMMAND = 5;
         final ThreadReferenceImpl owner;
         final int entryCount;
         final ThreadReferenceImpl[] waiters;

         static MonitorInfo process(VirtualMachineImpl var0, ObjectReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ObjectReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 9, 5);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.MonitorInfo" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 object(ObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static MonitorInfo waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new MonitorInfo(var0, var1);
         }

         private MonitorInfo(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.MonitorInfo" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.owner = var2.readThreadReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "owner(ThreadReferenceImpl): " + (this.owner == null ? "NULL" : "ref=" + this.owner.ref()));
            }

            this.entryCount = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "entryCount(int): " + this.entryCount);
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "waiters(ThreadReferenceImpl[]): ");
            }

            int var3 = var2.readInt();
            this.waiters = new ThreadReferenceImpl[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.waiters[var4] = var2.readThreadReference();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "waiters[i](ThreadReferenceImpl): " + (this.waiters[var4] == null ? "NULL" : "ref=" + this.waiters[var4].ref()));
               }
            }

         }
      }

      static class SetValues {
         static final int COMMAND = 3;

         static SetValues process(VirtualMachineImpl var0, ObjectReferenceImpl var1, FieldValue[] var2) throws JDWPException {
            PacketStream var3 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var3);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ObjectReferenceImpl var1, FieldValue[] var2) {
            PacketStream var3 = new PacketStream(var0, 9, 3);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var3.pkt.id + ") JDWP.ObjectReference.SetValues" + (var3.pkt.flags != 0 ? ", FLAGS=" + var3.pkt.flags : ""));
            }

            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 object(ObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var3.writeObjectRef(var1.ref());
            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 values(FieldValue[]): ");
            }

            var3.writeInt(var2.length);

            for(int var4 = 0; var4 < var2.length; ++var4) {
               if ((var3.vm.traceFlags & 1) != 0) {
                  var3.vm.printTrace("Sending:                     values[i](FieldValue): ");
               }

               var2[var4].write(var3);
            }

            var3.send();
            return var3;
         }

         static SetValues waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new SetValues(var0, var1);
         }

         private SetValues(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.SetValues" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }

         static class FieldValue {
            final long fieldID;
            final ValueImpl value;

            FieldValue(long var1, ValueImpl var3) {
               this.fieldID = var1;
               this.value = var3;
            }

            private void write(PacketStream var1) {
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     fieldID(long): " + this.fieldID);
               }

               var1.writeFieldRef(this.fieldID);
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     value(ValueImpl): " + this.value);
               }

               var1.writeUntaggedValue(this.value);
            }
         }
      }

      static class GetValues {
         static final int COMMAND = 2;
         final ValueImpl[] values;

         static GetValues process(VirtualMachineImpl var0, ObjectReferenceImpl var1, Field[] var2) throws JDWPException {
            PacketStream var3 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var3);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ObjectReferenceImpl var1, Field[] var2) {
            PacketStream var3 = new PacketStream(var0, 9, 2);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var3.pkt.id + ") JDWP.ObjectReference.GetValues" + (var3.pkt.flags != 0 ? ", FLAGS=" + var3.pkt.flags : ""));
            }

            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 object(ObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var3.writeObjectRef(var1.ref());
            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 fields(Field[]): ");
            }

            var3.writeInt(var2.length);

            for(int var4 = 0; var4 < var2.length; ++var4) {
               if ((var3.vm.traceFlags & 1) != 0) {
                  var3.vm.printTrace("Sending:                     fields[i](Field): ");
               }

               var2[var4].write(var3);
            }

            var3.send();
            return var3;
         }

         static GetValues waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new GetValues(var0, var1);
         }

         private GetValues(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.GetValues" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "values(ValueImpl[]): ");
            }

            int var3 = var2.readInt();
            this.values = new ValueImpl[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.values[var4] = var2.readValue();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "values[i](ValueImpl): " + this.values[var4]);
               }
            }

         }

         static class Field {
            final long fieldID;

            Field(long var1) {
               this.fieldID = var1;
            }

            private void write(PacketStream var1) {
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     fieldID(long): " + this.fieldID);
               }

               var1.writeFieldRef(this.fieldID);
            }
         }
      }

      static class ReferenceType {
         static final int COMMAND = 1;
         final byte refTypeTag;
         final long typeID;

         static ReferenceType process(VirtualMachineImpl var0, ObjectReferenceImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ObjectReferenceImpl var1) {
            PacketStream var2 = new PacketStream(var0, 9, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.ReferenceType" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 object(ObjectReferenceImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeObjectRef(var1.ref());
            var2.send();
            return var2;
         }

         static ReferenceType waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ReferenceType(var0, var1);
         }

         private ReferenceType(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ObjectReference.ReferenceType" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.refTypeTag = var2.readByte();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "refTypeTag(byte): " + this.refTypeTag);
            }

            this.typeID = var2.readClassRef();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "typeID(long): ref=" + this.typeID);
            }

         }
      }
   }

   static class Field {
      static final int COMMAND_SET = 8;

      private Field() {
      }
   }

   static class Method {
      static final int COMMAND_SET = 6;

      private Method() {
      }

      static class VariableTableWithGeneric {
         static final int COMMAND = 5;
         final int argCnt;
         final SlotInfo[] slots;

         static VariableTableWithGeneric process(VirtualMachineImpl var0, ReferenceTypeImpl var1, long var2) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1, long var2) {
            PacketStream var4 = new PacketStream(var0, 6, 5);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.Method.VariableTableWithGeneric" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var4.writeClassRef(var1.ref());
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 methodID(long): " + var2);
            }

            var4.writeMethodRef(var2);
            var4.send();
            return var4;
         }

         static VariableTableWithGeneric waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new VariableTableWithGeneric(var0, var1);
         }

         private VariableTableWithGeneric(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.Method.VariableTableWithGeneric" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.argCnt = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "argCnt(int): " + this.argCnt);
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "slots(SlotInfo[]): ");
            }

            int var3 = var2.readInt();
            this.slots = new SlotInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "slots[i](SlotInfo): ");
               }

               this.slots[var4] = new SlotInfo(var1, var2);
            }

         }

         static class SlotInfo {
            final long codeIndex;
            final String name;
            final String signature;
            final String genericSignature;
            final int length;
            final int slot;

            private SlotInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.codeIndex = var2.readLong();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "codeIndex(long): " + this.codeIndex);
               }

               this.name = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "name(String): " + this.name);
               }

               this.signature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "signature(String): " + this.signature);
               }

               this.genericSignature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "genericSignature(String): " + this.genericSignature);
               }

               this.length = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "length(int): " + this.length);
               }

               this.slot = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "slot(int): " + this.slot);
               }

            }

            // $FF: synthetic method
            SlotInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class IsObsolete {
         static final int COMMAND = 4;
         final boolean isObsolete;

         static IsObsolete process(VirtualMachineImpl var0, ReferenceTypeImpl var1, long var2) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1, long var2) {
            PacketStream var4 = new PacketStream(var0, 6, 4);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.Method.IsObsolete" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var4.writeClassRef(var1.ref());
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 methodID(long): " + var2);
            }

            var4.writeMethodRef(var2);
            var4.send();
            return var4;
         }

         static IsObsolete waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new IsObsolete(var0, var1);
         }

         private IsObsolete(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.Method.IsObsolete" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.isObsolete = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "isObsolete(boolean): " + this.isObsolete);
            }

         }
      }

      static class Bytecodes {
         static final int COMMAND = 3;
         final byte[] bytes;

         static Bytecodes process(VirtualMachineImpl var0, ReferenceTypeImpl var1, long var2) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1, long var2) {
            PacketStream var4 = new PacketStream(var0, 6, 3);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.Method.Bytecodes" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var4.writeClassRef(var1.ref());
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 methodID(long): " + var2);
            }

            var4.writeMethodRef(var2);
            var4.send();
            return var4;
         }

         static Bytecodes waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Bytecodes(var0, var1);
         }

         private Bytecodes(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.Method.Bytecodes" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "bytes(byte[]): ");
            }

            int var3 = var2.readInt();
            this.bytes = new byte[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.bytes[var4] = var2.readByte();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "bytes[i](byte): " + this.bytes[var4]);
               }
            }

         }
      }

      static class VariableTable {
         static final int COMMAND = 2;
         final int argCnt;
         final SlotInfo[] slots;

         static VariableTable process(VirtualMachineImpl var0, ReferenceTypeImpl var1, long var2) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1, long var2) {
            PacketStream var4 = new PacketStream(var0, 6, 2);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.Method.VariableTable" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var4.writeClassRef(var1.ref());
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 methodID(long): " + var2);
            }

            var4.writeMethodRef(var2);
            var4.send();
            return var4;
         }

         static VariableTable waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new VariableTable(var0, var1);
         }

         private VariableTable(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.Method.VariableTable" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.argCnt = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "argCnt(int): " + this.argCnt);
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "slots(SlotInfo[]): ");
            }

            int var3 = var2.readInt();
            this.slots = new SlotInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "slots[i](SlotInfo): ");
               }

               this.slots[var4] = new SlotInfo(var1, var2);
            }

         }

         static class SlotInfo {
            final long codeIndex;
            final String name;
            final String signature;
            final int length;
            final int slot;

            private SlotInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.codeIndex = var2.readLong();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "codeIndex(long): " + this.codeIndex);
               }

               this.name = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "name(String): " + this.name);
               }

               this.signature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "signature(String): " + this.signature);
               }

               this.length = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "length(int): " + this.length);
               }

               this.slot = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "slot(int): " + this.slot);
               }

            }

            // $FF: synthetic method
            SlotInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class LineTable {
         static final int COMMAND = 1;
         final long start;
         final long end;
         final LineInfo[] lines;

         static LineTable process(VirtualMachineImpl var0, ReferenceTypeImpl var1, long var2) throws JDWPException {
            PacketStream var4 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var4);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1, long var2) {
            PacketStream var4 = new PacketStream(var0, 6, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var4.pkt.id + ") JDWP.Method.LineTable" + (var4.pkt.flags != 0 ? ", FLAGS=" + var4.pkt.flags : ""));
            }

            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var4.writeClassRef(var1.ref());
            if ((var4.vm.traceFlags & 1) != 0) {
               var4.vm.printTrace("Sending:                 methodID(long): " + var2);
            }

            var4.writeMethodRef(var2);
            var4.send();
            return var4;
         }

         static LineTable waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new LineTable(var0, var1);
         }

         private LineTable(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.Method.LineTable" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.start = var2.readLong();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "start(long): " + this.start);
            }

            this.end = var2.readLong();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "end(long): " + this.end);
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "lines(LineInfo[]): ");
            }

            int var3 = var2.readInt();
            this.lines = new LineInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "lines[i](LineInfo): ");
               }

               this.lines[var4] = new LineInfo(var1, var2);
            }

         }

         static class LineInfo {
            final long lineCodeIndex;
            final int lineNumber;

            private LineInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.lineCodeIndex = var2.readLong();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "lineCodeIndex(long): " + this.lineCodeIndex);
               }

               this.lineNumber = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "lineNumber(int): " + this.lineNumber);
               }

            }

            // $FF: synthetic method
            LineInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }
   }

   static class InterfaceType {
      static final int COMMAND_SET = 5;

      private InterfaceType() {
      }

      static class InvokeMethod {
         static final int COMMAND = 1;
         final ValueImpl returnValue;
         final ObjectReferenceImpl exception;

         static InvokeMethod process(VirtualMachineImpl var0, InterfaceTypeImpl var1, ThreadReferenceImpl var2, long var3, ValueImpl[] var5, int var6) throws JDWPException {
            PacketStream var7 = enqueueCommand(var0, var1, var2, var3, var5, var6);
            return waitForReply(var0, var7);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, InterfaceTypeImpl var1, ThreadReferenceImpl var2, long var3, ValueImpl[] var5, int var6) {
            PacketStream var7 = new PacketStream(var0, 5, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var7.pkt.id + ") JDWP.InterfaceType.InvokeMethod" + (var7.pkt.flags != 0 ? ", FLAGS=" + var7.pkt.flags : ""));
            }

            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 clazz(InterfaceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var7.writeClassRef(var1.ref());
            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var2 == null ? "NULL" : "ref=" + var2.ref()));
            }

            var7.writeObjectRef(var2.ref());
            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 methodID(long): " + var3);
            }

            var7.writeMethodRef(var3);
            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 arguments(ValueImpl[]): ");
            }

            var7.writeInt(var5.length);

            for(int var8 = 0; var8 < var5.length; ++var8) {
               if ((var7.vm.traceFlags & 1) != 0) {
                  var7.vm.printTrace("Sending:                     arguments[i](ValueImpl): " + var5[var8]);
               }

               var7.writeValue(var5[var8]);
            }

            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 options(int): " + var6);
            }

            var7.writeInt(var6);
            var7.send();
            return var7;
         }

         static InvokeMethod waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new InvokeMethod(var0, var1);
         }

         private InvokeMethod(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.InterfaceType.InvokeMethod" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.returnValue = var2.readValue();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "returnValue(ValueImpl): " + this.returnValue);
            }

            this.exception = var2.readTaggedObjectReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "exception(ObjectReferenceImpl): " + (this.exception == null ? "NULL" : "ref=" + this.exception.ref()));
            }

         }
      }
   }

   static class ArrayType {
      static final int COMMAND_SET = 4;

      private ArrayType() {
      }

      static class NewInstance {
         static final int COMMAND = 1;
         final ObjectReferenceImpl newArray;

         static NewInstance process(VirtualMachineImpl var0, ArrayTypeImpl var1, int var2) throws JDWPException {
            PacketStream var3 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var3);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ArrayTypeImpl var1, int var2) {
            PacketStream var3 = new PacketStream(var0, 4, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var3.pkt.id + ") JDWP.ArrayType.NewInstance" + (var3.pkt.flags != 0 ? ", FLAGS=" + var3.pkt.flags : ""));
            }

            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 arrType(ArrayTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var3.writeClassRef(var1.ref());
            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 length(int): " + var2);
            }

            var3.writeInt(var2);
            var3.send();
            return var3;
         }

         static NewInstance waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new NewInstance(var0, var1);
         }

         private NewInstance(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ArrayType.NewInstance" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.newArray = var2.readTaggedObjectReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "newArray(ObjectReferenceImpl): " + (this.newArray == null ? "NULL" : "ref=" + this.newArray.ref()));
            }

         }
      }
   }

   static class ClassType {
      static final int COMMAND_SET = 3;

      private ClassType() {
      }

      static class NewInstance {
         static final int COMMAND = 4;
         final ObjectReferenceImpl newObject;
         final ObjectReferenceImpl exception;

         static NewInstance process(VirtualMachineImpl var0, ClassTypeImpl var1, ThreadReferenceImpl var2, long var3, ValueImpl[] var5, int var6) throws JDWPException {
            PacketStream var7 = enqueueCommand(var0, var1, var2, var3, var5, var6);
            return waitForReply(var0, var7);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ClassTypeImpl var1, ThreadReferenceImpl var2, long var3, ValueImpl[] var5, int var6) {
            PacketStream var7 = new PacketStream(var0, 3, 4);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var7.pkt.id + ") JDWP.ClassType.NewInstance" + (var7.pkt.flags != 0 ? ", FLAGS=" + var7.pkt.flags : ""));
            }

            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 clazz(ClassTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var7.writeClassRef(var1.ref());
            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var2 == null ? "NULL" : "ref=" + var2.ref()));
            }

            var7.writeObjectRef(var2.ref());
            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 methodID(long): " + var3);
            }

            var7.writeMethodRef(var3);
            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 arguments(ValueImpl[]): ");
            }

            var7.writeInt(var5.length);

            for(int var8 = 0; var8 < var5.length; ++var8) {
               if ((var7.vm.traceFlags & 1) != 0) {
                  var7.vm.printTrace("Sending:                     arguments[i](ValueImpl): " + var5[var8]);
               }

               var7.writeValue(var5[var8]);
            }

            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 options(int): " + var6);
            }

            var7.writeInt(var6);
            var7.send();
            return var7;
         }

         static NewInstance waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new NewInstance(var0, var1);
         }

         private NewInstance(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ClassType.NewInstance" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.newObject = var2.readTaggedObjectReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "newObject(ObjectReferenceImpl): " + (this.newObject == null ? "NULL" : "ref=" + this.newObject.ref()));
            }

            this.exception = var2.readTaggedObjectReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "exception(ObjectReferenceImpl): " + (this.exception == null ? "NULL" : "ref=" + this.exception.ref()));
            }

         }
      }

      static class InvokeMethod {
         static final int COMMAND = 3;
         final ValueImpl returnValue;
         final ObjectReferenceImpl exception;

         static InvokeMethod process(VirtualMachineImpl var0, ClassTypeImpl var1, ThreadReferenceImpl var2, long var3, ValueImpl[] var5, int var6) throws JDWPException {
            PacketStream var7 = enqueueCommand(var0, var1, var2, var3, var5, var6);
            return waitForReply(var0, var7);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ClassTypeImpl var1, ThreadReferenceImpl var2, long var3, ValueImpl[] var5, int var6) {
            PacketStream var7 = new PacketStream(var0, 3, 3);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var7.pkt.id + ") JDWP.ClassType.InvokeMethod" + (var7.pkt.flags != 0 ? ", FLAGS=" + var7.pkt.flags : ""));
            }

            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 clazz(ClassTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var7.writeClassRef(var1.ref());
            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 thread(ThreadReferenceImpl): " + (var2 == null ? "NULL" : "ref=" + var2.ref()));
            }

            var7.writeObjectRef(var2.ref());
            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 methodID(long): " + var3);
            }

            var7.writeMethodRef(var3);
            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 arguments(ValueImpl[]): ");
            }

            var7.writeInt(var5.length);

            for(int var8 = 0; var8 < var5.length; ++var8) {
               if ((var7.vm.traceFlags & 1) != 0) {
                  var7.vm.printTrace("Sending:                     arguments[i](ValueImpl): " + var5[var8]);
               }

               var7.writeValue(var5[var8]);
            }

            if ((var7.vm.traceFlags & 1) != 0) {
               var7.vm.printTrace("Sending:                 options(int): " + var6);
            }

            var7.writeInt(var6);
            var7.send();
            return var7;
         }

         static InvokeMethod waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new InvokeMethod(var0, var1);
         }

         private InvokeMethod(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ClassType.InvokeMethod" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.returnValue = var2.readValue();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "returnValue(ValueImpl): " + this.returnValue);
            }

            this.exception = var2.readTaggedObjectReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "exception(ObjectReferenceImpl): " + (this.exception == null ? "NULL" : "ref=" + this.exception.ref()));
            }

         }
      }

      static class SetValues {
         static final int COMMAND = 2;

         static SetValues process(VirtualMachineImpl var0, ClassTypeImpl var1, FieldValue[] var2) throws JDWPException {
            PacketStream var3 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var3);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ClassTypeImpl var1, FieldValue[] var2) {
            PacketStream var3 = new PacketStream(var0, 3, 2);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var3.pkt.id + ") JDWP.ClassType.SetValues" + (var3.pkt.flags != 0 ? ", FLAGS=" + var3.pkt.flags : ""));
            }

            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 clazz(ClassTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var3.writeClassRef(var1.ref());
            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 values(FieldValue[]): ");
            }

            var3.writeInt(var2.length);

            for(int var4 = 0; var4 < var2.length; ++var4) {
               if ((var3.vm.traceFlags & 1) != 0) {
                  var3.vm.printTrace("Sending:                     values[i](FieldValue): ");
               }

               var2[var4].write(var3);
            }

            var3.send();
            return var3;
         }

         static SetValues waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new SetValues(var0, var1);
         }

         private SetValues(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ClassType.SetValues" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }

         static class FieldValue {
            final long fieldID;
            final ValueImpl value;

            FieldValue(long var1, ValueImpl var3) {
               this.fieldID = var1;
               this.value = var3;
            }

            private void write(PacketStream var1) {
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     fieldID(long): " + this.fieldID);
               }

               var1.writeFieldRef(this.fieldID);
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     value(ValueImpl): " + this.value);
               }

               var1.writeUntaggedValue(this.value);
            }
         }
      }

      static class Superclass {
         static final int COMMAND = 1;
         final ClassTypeImpl superclass;

         static Superclass process(VirtualMachineImpl var0, ClassTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ClassTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 3, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ClassType.Superclass" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 clazz(ClassTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static Superclass waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Superclass(var0, var1);
         }

         private Superclass(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ClassType.Superclass" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.superclass = var1.classType(var2.readClassRef());
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "superclass(ClassTypeImpl): " + (this.superclass == null ? "NULL" : "ref=" + this.superclass.ref()));
            }

         }
      }
   }

   static class ReferenceType {
      static final int COMMAND_SET = 2;

      private ReferenceType() {
      }

      static class ConstantPool {
         static final int COMMAND = 18;
         final int count;
         final byte[] bytes;

         static ConstantPool process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 18);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.ConstantPool" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static ConstantPool waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ConstantPool(var0, var1);
         }

         private ConstantPool(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.ConstantPool" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.count = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "count(int): " + this.count);
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "bytes(byte[]): ");
            }

            int var3 = var2.readInt();
            this.bytes = new byte[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.bytes[var4] = var2.readByte();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "bytes[i](byte): " + this.bytes[var4]);
               }
            }

         }
      }

      static class ClassFileVersion {
         static final int COMMAND = 17;
         final int majorVersion;
         final int minorVersion;

         static ClassFileVersion process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 17);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.ClassFileVersion" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static ClassFileVersion waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ClassFileVersion(var0, var1);
         }

         private ClassFileVersion(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.ClassFileVersion" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.majorVersion = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "majorVersion(int): " + this.majorVersion);
            }

            this.minorVersion = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "minorVersion(int): " + this.minorVersion);
            }

         }
      }

      static class Instances {
         static final int COMMAND = 16;
         final ObjectReferenceImpl[] instances;

         static Instances process(VirtualMachineImpl var0, ReferenceTypeImpl var1, int var2) throws JDWPException {
            PacketStream var3 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var3);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1, int var2) {
            PacketStream var3 = new PacketStream(var0, 2, 16);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var3.pkt.id + ") JDWP.ReferenceType.Instances" + (var3.pkt.flags != 0 ? ", FLAGS=" + var3.pkt.flags : ""));
            }

            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var3.writeClassRef(var1.ref());
            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 maxInstances(int): " + var2);
            }

            var3.writeInt(var2);
            var3.send();
            return var3;
         }

         static Instances waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Instances(var0, var1);
         }

         private Instances(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Instances" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "instances(ObjectReferenceImpl[]): ");
            }

            int var3 = var2.readInt();
            this.instances = new ObjectReferenceImpl[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.instances[var4] = var2.readTaggedObjectReference();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "instances[i](ObjectReferenceImpl): " + (this.instances[var4] == null ? "NULL" : "ref=" + this.instances[var4].ref()));
               }
            }

         }
      }

      static class MethodsWithGeneric {
         static final int COMMAND = 15;
         final MethodInfo[] declared;

         static MethodsWithGeneric process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 15);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.MethodsWithGeneric" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static MethodsWithGeneric waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new MethodsWithGeneric(var0, var1);
         }

         private MethodsWithGeneric(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.MethodsWithGeneric" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "declared(MethodInfo[]): ");
            }

            int var3 = var2.readInt();
            this.declared = new MethodInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "declared[i](MethodInfo): ");
               }

               this.declared[var4] = new MethodInfo(var1, var2);
            }

         }

         static class MethodInfo {
            final long methodID;
            final String name;
            final String signature;
            final String genericSignature;
            final int modBits;

            private MethodInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.methodID = var2.readMethodRef();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "methodID(long): " + this.methodID);
               }

               this.name = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "name(String): " + this.name);
               }

               this.signature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "signature(String): " + this.signature);
               }

               this.genericSignature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "genericSignature(String): " + this.genericSignature);
               }

               this.modBits = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "modBits(int): " + this.modBits);
               }

            }

            // $FF: synthetic method
            MethodInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class FieldsWithGeneric {
         static final int COMMAND = 14;
         final FieldInfo[] declared;

         static FieldsWithGeneric process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 14);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.FieldsWithGeneric" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static FieldsWithGeneric waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new FieldsWithGeneric(var0, var1);
         }

         private FieldsWithGeneric(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.FieldsWithGeneric" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "declared(FieldInfo[]): ");
            }

            int var3 = var2.readInt();
            this.declared = new FieldInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "declared[i](FieldInfo): ");
               }

               this.declared[var4] = new FieldInfo(var1, var2);
            }

         }

         static class FieldInfo {
            final long fieldID;
            final String name;
            final String signature;
            final String genericSignature;
            final int modBits;

            private FieldInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.fieldID = var2.readFieldRef();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "fieldID(long): " + this.fieldID);
               }

               this.name = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "name(String): " + this.name);
               }

               this.signature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "signature(String): " + this.signature);
               }

               this.genericSignature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "genericSignature(String): " + this.genericSignature);
               }

               this.modBits = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "modBits(int): " + this.modBits);
               }

            }

            // $FF: synthetic method
            FieldInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class SignatureWithGeneric {
         static final int COMMAND = 13;
         final String signature;
         final String genericSignature;

         static SignatureWithGeneric process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 13);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.SignatureWithGeneric" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static SignatureWithGeneric waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new SignatureWithGeneric(var0, var1);
         }

         private SignatureWithGeneric(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.SignatureWithGeneric" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.signature = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "signature(String): " + this.signature);
            }

            this.genericSignature = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "genericSignature(String): " + this.genericSignature);
            }

         }
      }

      static class SourceDebugExtension {
         static final int COMMAND = 12;
         final String extension;

         static SourceDebugExtension process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 12);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.SourceDebugExtension" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static SourceDebugExtension waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new SourceDebugExtension(var0, var1);
         }

         private SourceDebugExtension(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.SourceDebugExtension" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.extension = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "extension(String): " + this.extension);
            }

         }
      }

      static class ClassObject {
         static final int COMMAND = 11;
         final ClassObjectReferenceImpl classObject;

         static ClassObject process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 11);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.ClassObject" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static ClassObject waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ClassObject(var0, var1);
         }

         private ClassObject(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.ClassObject" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.classObject = var2.readClassObjectReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "classObject(ClassObjectReferenceImpl): " + (this.classObject == null ? "NULL" : "ref=" + this.classObject.ref()));
            }

         }
      }

      static class Interfaces {
         static final int COMMAND = 10;
         final InterfaceTypeImpl[] interfaces;

         static Interfaces process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 10);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Interfaces" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static Interfaces waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Interfaces(var0, var1);
         }

         private Interfaces(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Interfaces" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "interfaces(InterfaceTypeImpl[]): ");
            }

            int var3 = var2.readInt();
            this.interfaces = new InterfaceTypeImpl[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.interfaces[var4] = var1.interfaceType(var2.readClassRef());
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "interfaces[i](InterfaceTypeImpl): " + (this.interfaces[var4] == null ? "NULL" : "ref=" + this.interfaces[var4].ref()));
               }
            }

         }
      }

      static class Status {
         static final int COMMAND = 9;
         final int status;

         static Status process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 9);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Status" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static Status waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Status(var0, var1);
         }

         private Status(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Status" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.status = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "status(int): " + this.status);
            }

         }
      }

      static class NestedTypes {
         static final int COMMAND = 8;
         final TypeInfo[] classes;

         static NestedTypes process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 8);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.NestedTypes" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static NestedTypes waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new NestedTypes(var0, var1);
         }

         private NestedTypes(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.NestedTypes" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "classes(TypeInfo[]): ");
            }

            int var3 = var2.readInt();
            this.classes = new TypeInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "classes[i](TypeInfo): ");
               }

               this.classes[var4] = new TypeInfo(var1, var2);
            }

         }

         static class TypeInfo {
            final byte refTypeTag;
            final long typeID;

            private TypeInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.refTypeTag = var2.readByte();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "refTypeTag(byte): " + this.refTypeTag);
               }

               this.typeID = var2.readClassRef();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "typeID(long): ref=" + this.typeID);
               }

            }

            // $FF: synthetic method
            TypeInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class SourceFile {
         static final int COMMAND = 7;
         final String sourceFile;

         static SourceFile process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 7);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.SourceFile" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static SourceFile waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new SourceFile(var0, var1);
         }

         private SourceFile(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.SourceFile" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.sourceFile = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "sourceFile(String): " + this.sourceFile);
            }

         }
      }

      static class GetValues {
         static final int COMMAND = 6;
         final ValueImpl[] values;

         static GetValues process(VirtualMachineImpl var0, ReferenceTypeImpl var1, Field[] var2) throws JDWPException {
            PacketStream var3 = enqueueCommand(var0, var1, var2);
            return waitForReply(var0, var3);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1, Field[] var2) {
            PacketStream var3 = new PacketStream(var0, 2, 6);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var3.pkt.id + ") JDWP.ReferenceType.GetValues" + (var3.pkt.flags != 0 ? ", FLAGS=" + var3.pkt.flags : ""));
            }

            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var3.writeClassRef(var1.ref());
            if ((var3.vm.traceFlags & 1) != 0) {
               var3.vm.printTrace("Sending:                 fields(Field[]): ");
            }

            var3.writeInt(var2.length);

            for(int var4 = 0; var4 < var2.length; ++var4) {
               if ((var3.vm.traceFlags & 1) != 0) {
                  var3.vm.printTrace("Sending:                     fields[i](Field): ");
               }

               var2[var4].write(var3);
            }

            var3.send();
            return var3;
         }

         static GetValues waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new GetValues(var0, var1);
         }

         private GetValues(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.GetValues" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "values(ValueImpl[]): ");
            }

            int var3 = var2.readInt();
            this.values = new ValueImpl[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.values[var4] = var2.readValue();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "values[i](ValueImpl): " + this.values[var4]);
               }
            }

         }

         static class Field {
            final long fieldID;

            Field(long var1) {
               this.fieldID = var1;
            }

            private void write(PacketStream var1) {
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     fieldID(long): " + this.fieldID);
               }

               var1.writeFieldRef(this.fieldID);
            }
         }
      }

      static class Methods {
         static final int COMMAND = 5;
         final MethodInfo[] declared;

         static Methods process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 5);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Methods" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static Methods waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Methods(var0, var1);
         }

         private Methods(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Methods" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "declared(MethodInfo[]): ");
            }

            int var3 = var2.readInt();
            this.declared = new MethodInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "declared[i](MethodInfo): ");
               }

               this.declared[var4] = new MethodInfo(var1, var2);
            }

         }

         static class MethodInfo {
            final long methodID;
            final String name;
            final String signature;
            final int modBits;

            private MethodInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.methodID = var2.readMethodRef();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "methodID(long): " + this.methodID);
               }

               this.name = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "name(String): " + this.name);
               }

               this.signature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "signature(String): " + this.signature);
               }

               this.modBits = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "modBits(int): " + this.modBits);
               }

            }

            // $FF: synthetic method
            MethodInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class Fields {
         static final int COMMAND = 4;
         final FieldInfo[] declared;

         static Fields process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 4);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Fields" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static Fields waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Fields(var0, var1);
         }

         private Fields(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Fields" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "declared(FieldInfo[]): ");
            }

            int var3 = var2.readInt();
            this.declared = new FieldInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "declared[i](FieldInfo): ");
               }

               this.declared[var4] = new FieldInfo(var1, var2);
            }

         }

         static class FieldInfo {
            final long fieldID;
            final String name;
            final String signature;
            final int modBits;

            private FieldInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.fieldID = var2.readFieldRef();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "fieldID(long): " + this.fieldID);
               }

               this.name = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "name(String): " + this.name);
               }

               this.signature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "signature(String): " + this.signature);
               }

               this.modBits = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "modBits(int): " + this.modBits);
               }

            }

            // $FF: synthetic method
            FieldInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class Modifiers {
         static final int COMMAND = 3;
         final int modBits;

         static Modifiers process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 3);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Modifiers" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static Modifiers waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Modifiers(var0, var1);
         }

         private Modifiers(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Modifiers" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.modBits = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "modBits(int): " + this.modBits);
            }

         }
      }

      static class ClassLoader {
         static final int COMMAND = 2;
         final ClassLoaderReferenceImpl classLoader;

         static ClassLoader process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 2);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.ClassLoader" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static ClassLoader waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ClassLoader(var0, var1);
         }

         private ClassLoader(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.ClassLoader" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.classLoader = var2.readClassLoaderReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "classLoader(ClassLoaderReferenceImpl): " + (this.classLoader == null ? "NULL" : "ref=" + this.classLoader.ref()));
            }

         }
      }

      static class Signature {
         static final int COMMAND = 1;
         final String signature;

         static Signature process(VirtualMachineImpl var0, ReferenceTypeImpl var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl var1) {
            PacketStream var2 = new PacketStream(var0, 2, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Signature" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refType(ReferenceTypeImpl): " + (var1 == null ? "NULL" : "ref=" + var1.ref()));
            }

            var2.writeClassRef(var1.ref());
            var2.send();
            return var2;
         }

         static Signature waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Signature(var0, var1);
         }

         private Signature(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.ReferenceType.Signature" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.signature = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "signature(String): " + this.signature);
            }

         }
      }
   }

   static class VirtualMachine {
      static final int COMMAND_SET = 1;

      private VirtualMachine() {
      }

      static class InstanceCounts {
         static final int COMMAND = 21;
         final long[] counts;

         static InstanceCounts process(VirtualMachineImpl var0, ReferenceTypeImpl[] var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ReferenceTypeImpl[] var1) {
            PacketStream var2 = new PacketStream(var0, 1, 21);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.InstanceCounts" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 refTypesCount(ReferenceTypeImpl[]): ");
            }

            var2.writeInt(var1.length);

            for(int var3 = 0; var3 < var1.length; ++var3) {
               if ((var2.vm.traceFlags & 1) != 0) {
                  var2.vm.printTrace("Sending:                     refTypesCount[i](ReferenceTypeImpl): " + (var1[var3] == null ? "NULL" : "ref=" + var1[var3].ref()));
               }

               var2.writeClassRef(var1[var3].ref());
            }

            var2.send();
            return var2;
         }

         static InstanceCounts waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new InstanceCounts(var0, var1);
         }

         private InstanceCounts(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.InstanceCounts" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "counts(long[]): ");
            }

            int var3 = var2.readInt();
            this.counts = new long[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.counts[var4] = var2.readLong();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "counts[i](long): " + this.counts[var4]);
               }
            }

         }
      }

      static class AllClassesWithGeneric {
         static final int COMMAND = 20;
         final ClassInfo[] classes;

         static AllClassesWithGeneric process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 20);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.AllClassesWithGeneric" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static AllClassesWithGeneric waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new AllClassesWithGeneric(var0, var1);
         }

         private AllClassesWithGeneric(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.AllClassesWithGeneric" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "classes(ClassInfo[]): ");
            }

            int var3 = var2.readInt();
            this.classes = new ClassInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "classes[i](ClassInfo): ");
               }

               this.classes[var4] = new ClassInfo(var1, var2);
            }

         }

         static class ClassInfo {
            final byte refTypeTag;
            final long typeID;
            final String signature;
            final String genericSignature;
            final int status;

            private ClassInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.refTypeTag = var2.readByte();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "refTypeTag(byte): " + this.refTypeTag);
               }

               this.typeID = var2.readClassRef();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "typeID(long): ref=" + this.typeID);
               }

               this.signature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "signature(String): " + this.signature);
               }

               this.genericSignature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "genericSignature(String): " + this.genericSignature);
               }

               this.status = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "status(int): " + this.status);
               }

            }

            // $FF: synthetic method
            ClassInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class SetDefaultStratum {
         static final int COMMAND = 19;

         static SetDefaultStratum process(VirtualMachineImpl var0, String var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, String var1) {
            PacketStream var2 = new PacketStream(var0, 1, 19);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.SetDefaultStratum" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 stratumID(String): " + var1);
            }

            var2.writeString(var1);
            var2.send();
            return var2;
         }

         static SetDefaultStratum waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new SetDefaultStratum(var0, var1);
         }

         private SetDefaultStratum(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.SetDefaultStratum" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class RedefineClasses {
         static final int COMMAND = 18;

         static RedefineClasses process(VirtualMachineImpl var0, ClassDef[] var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, ClassDef[] var1) {
            PacketStream var2 = new PacketStream(var0, 1, 18);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.RedefineClasses" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 classes(ClassDef[]): ");
            }

            var2.writeInt(var1.length);

            for(int var3 = 0; var3 < var1.length; ++var3) {
               if ((var2.vm.traceFlags & 1) != 0) {
                  var2.vm.printTrace("Sending:                     classes[i](ClassDef): ");
               }

               var1[var3].write(var2);
            }

            var2.send();
            return var2;
         }

         static RedefineClasses waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new RedefineClasses(var0, var1);
         }

         private RedefineClasses(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.RedefineClasses" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }

         static class ClassDef {
            final ReferenceTypeImpl refType;
            final byte[] classfile;

            ClassDef(ReferenceTypeImpl var1, byte[] var2) {
               this.refType = var1;
               this.classfile = var2;
            }

            private void write(PacketStream var1) {
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     refType(ReferenceTypeImpl): " + (this.refType == null ? "NULL" : "ref=" + this.refType.ref()));
               }

               var1.writeClassRef(this.refType.ref());
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     classfile(byte[]): ");
               }

               var1.writeInt(this.classfile.length);

               for(int var2 = 0; var2 < this.classfile.length; ++var2) {
                  if ((var1.vm.traceFlags & 1) != 0) {
                     var1.vm.printTrace("Sending:                         classfile[i](byte): " + this.classfile[var2]);
                  }

                  var1.writeByte(this.classfile[var2]);
               }

            }
         }
      }

      static class CapabilitiesNew {
         static final int COMMAND = 17;
         final boolean canWatchFieldModification;
         final boolean canWatchFieldAccess;
         final boolean canGetBytecodes;
         final boolean canGetSyntheticAttribute;
         final boolean canGetOwnedMonitorInfo;
         final boolean canGetCurrentContendedMonitor;
         final boolean canGetMonitorInfo;
         final boolean canRedefineClasses;
         final boolean canAddMethod;
         final boolean canUnrestrictedlyRedefineClasses;
         final boolean canPopFrames;
         final boolean canUseInstanceFilters;
         final boolean canGetSourceDebugExtension;
         final boolean canRequestVMDeathEvent;
         final boolean canSetDefaultStratum;
         final boolean canGetInstanceInfo;
         final boolean canRequestMonitorEvents;
         final boolean canGetMonitorFrameInfo;
         final boolean canUseSourceNameFilters;
         final boolean canGetConstantPool;
         final boolean canForceEarlyReturn;
         final boolean reserved22;
         final boolean reserved23;
         final boolean reserved24;
         final boolean reserved25;
         final boolean reserved26;
         final boolean reserved27;
         final boolean reserved28;
         final boolean reserved29;
         final boolean reserved30;
         final boolean reserved31;
         final boolean reserved32;

         static CapabilitiesNew process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 17);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.CapabilitiesNew" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static CapabilitiesNew waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new CapabilitiesNew(var0, var1);
         }

         private CapabilitiesNew(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.CapabilitiesNew" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.canWatchFieldModification = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canWatchFieldModification(boolean): " + this.canWatchFieldModification);
            }

            this.canWatchFieldAccess = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canWatchFieldAccess(boolean): " + this.canWatchFieldAccess);
            }

            this.canGetBytecodes = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetBytecodes(boolean): " + this.canGetBytecodes);
            }

            this.canGetSyntheticAttribute = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetSyntheticAttribute(boolean): " + this.canGetSyntheticAttribute);
            }

            this.canGetOwnedMonitorInfo = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetOwnedMonitorInfo(boolean): " + this.canGetOwnedMonitorInfo);
            }

            this.canGetCurrentContendedMonitor = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetCurrentContendedMonitor(boolean): " + this.canGetCurrentContendedMonitor);
            }

            this.canGetMonitorInfo = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetMonitorInfo(boolean): " + this.canGetMonitorInfo);
            }

            this.canRedefineClasses = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canRedefineClasses(boolean): " + this.canRedefineClasses);
            }

            this.canAddMethod = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canAddMethod(boolean): " + this.canAddMethod);
            }

            this.canUnrestrictedlyRedefineClasses = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canUnrestrictedlyRedefineClasses(boolean): " + this.canUnrestrictedlyRedefineClasses);
            }

            this.canPopFrames = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canPopFrames(boolean): " + this.canPopFrames);
            }

            this.canUseInstanceFilters = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canUseInstanceFilters(boolean): " + this.canUseInstanceFilters);
            }

            this.canGetSourceDebugExtension = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetSourceDebugExtension(boolean): " + this.canGetSourceDebugExtension);
            }

            this.canRequestVMDeathEvent = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canRequestVMDeathEvent(boolean): " + this.canRequestVMDeathEvent);
            }

            this.canSetDefaultStratum = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canSetDefaultStratum(boolean): " + this.canSetDefaultStratum);
            }

            this.canGetInstanceInfo = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetInstanceInfo(boolean): " + this.canGetInstanceInfo);
            }

            this.canRequestMonitorEvents = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canRequestMonitorEvents(boolean): " + this.canRequestMonitorEvents);
            }

            this.canGetMonitorFrameInfo = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetMonitorFrameInfo(boolean): " + this.canGetMonitorFrameInfo);
            }

            this.canUseSourceNameFilters = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canUseSourceNameFilters(boolean): " + this.canUseSourceNameFilters);
            }

            this.canGetConstantPool = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetConstantPool(boolean): " + this.canGetConstantPool);
            }

            this.canForceEarlyReturn = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canForceEarlyReturn(boolean): " + this.canForceEarlyReturn);
            }

            this.reserved22 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved22(boolean): " + this.reserved22);
            }

            this.reserved23 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved23(boolean): " + this.reserved23);
            }

            this.reserved24 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved24(boolean): " + this.reserved24);
            }

            this.reserved25 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved25(boolean): " + this.reserved25);
            }

            this.reserved26 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved26(boolean): " + this.reserved26);
            }

            this.reserved27 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved27(boolean): " + this.reserved27);
            }

            this.reserved28 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved28(boolean): " + this.reserved28);
            }

            this.reserved29 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved29(boolean): " + this.reserved29);
            }

            this.reserved30 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved30(boolean): " + this.reserved30);
            }

            this.reserved31 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved31(boolean): " + this.reserved31);
            }

            this.reserved32 = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "reserved32(boolean): " + this.reserved32);
            }

         }
      }

      static class ReleaseEvents {
         static final int COMMAND = 16;

         static ReleaseEvents process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 16);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.ReleaseEvents" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static ReleaseEvents waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ReleaseEvents(var0, var1);
         }

         private ReleaseEvents(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.ReleaseEvents" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class HoldEvents {
         static final int COMMAND = 15;

         static HoldEvents process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 15);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.HoldEvents" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static HoldEvents waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new HoldEvents(var0, var1);
         }

         private HoldEvents(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.HoldEvents" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class DisposeObjects {
         static final int COMMAND = 14;

         static DisposeObjects process(VirtualMachineImpl var0, Request[] var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, Request[] var1) {
            PacketStream var2 = new PacketStream(var0, 1, 14);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.DisposeObjects" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 requests(Request[]): ");
            }

            var2.writeInt(var1.length);

            for(int var3 = 0; var3 < var1.length; ++var3) {
               if ((var2.vm.traceFlags & 1) != 0) {
                  var2.vm.printTrace("Sending:                     requests[i](Request): ");
               }

               var1[var3].write(var2);
            }

            var2.send();
            return var2;
         }

         static DisposeObjects waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new DisposeObjects(var0, var1);
         }

         private DisposeObjects(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.DisposeObjects" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }

         static class Request {
            final ObjectReferenceImpl object;
            final int refCnt;

            Request(ObjectReferenceImpl var1, int var2) {
               this.object = var1;
               this.refCnt = var2;
            }

            private void write(PacketStream var1) {
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     object(ObjectReferenceImpl): " + (this.object == null ? "NULL" : "ref=" + this.object.ref()));
               }

               var1.writeObjectRef(this.object.ref());
               if ((var1.vm.traceFlags & 1) != 0) {
                  var1.vm.printTrace("Sending:                     refCnt(int): " + this.refCnt);
               }

               var1.writeInt(this.refCnt);
            }
         }
      }

      static class ClassPaths {
         static final int COMMAND = 13;
         final String baseDir;
         final String[] classpaths;
         final String[] bootclasspaths;

         static ClassPaths process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 13);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.ClassPaths" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static ClassPaths waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ClassPaths(var0, var1);
         }

         private ClassPaths(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.ClassPaths" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.baseDir = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "baseDir(String): " + this.baseDir);
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "classpaths(String[]): ");
            }

            int var3 = var2.readInt();
            this.classpaths = new String[var3];

            int var4;
            for(var4 = 0; var4 < var3; ++var4) {
               this.classpaths[var4] = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "classpaths[i](String): " + this.classpaths[var4]);
               }
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "bootclasspaths(String[]): ");
            }

            var4 = var2.readInt();
            this.bootclasspaths = new String[var4];

            for(int var5 = 0; var5 < var4; ++var5) {
               this.bootclasspaths[var5] = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "bootclasspaths[i](String): " + this.bootclasspaths[var5]);
               }
            }

         }
      }

      static class Capabilities {
         static final int COMMAND = 12;
         final boolean canWatchFieldModification;
         final boolean canWatchFieldAccess;
         final boolean canGetBytecodes;
         final boolean canGetSyntheticAttribute;
         final boolean canGetOwnedMonitorInfo;
         final boolean canGetCurrentContendedMonitor;
         final boolean canGetMonitorInfo;

         static Capabilities process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 12);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.Capabilities" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static Capabilities waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Capabilities(var0, var1);
         }

         private Capabilities(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.Capabilities" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.canWatchFieldModification = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canWatchFieldModification(boolean): " + this.canWatchFieldModification);
            }

            this.canWatchFieldAccess = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canWatchFieldAccess(boolean): " + this.canWatchFieldAccess);
            }

            this.canGetBytecodes = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetBytecodes(boolean): " + this.canGetBytecodes);
            }

            this.canGetSyntheticAttribute = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetSyntheticAttribute(boolean): " + this.canGetSyntheticAttribute);
            }

            this.canGetOwnedMonitorInfo = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetOwnedMonitorInfo(boolean): " + this.canGetOwnedMonitorInfo);
            }

            this.canGetCurrentContendedMonitor = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetCurrentContendedMonitor(boolean): " + this.canGetCurrentContendedMonitor);
            }

            this.canGetMonitorInfo = var2.readBoolean();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "canGetMonitorInfo(boolean): " + this.canGetMonitorInfo);
            }

         }
      }

      static class CreateString {
         static final int COMMAND = 11;
         final StringReferenceImpl stringObject;

         static CreateString process(VirtualMachineImpl var0, String var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, String var1) {
            PacketStream var2 = new PacketStream(var0, 1, 11);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.CreateString" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 utf(String): " + var1);
            }

            var2.writeString(var1);
            var2.send();
            return var2;
         }

         static CreateString waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new CreateString(var0, var1);
         }

         private CreateString(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.CreateString" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.stringObject = var2.readStringReference();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "stringObject(StringReferenceImpl): " + (this.stringObject == null ? "NULL" : "ref=" + this.stringObject.ref()));
            }

         }
      }

      static class Exit {
         static final int COMMAND = 10;

         static Exit process(VirtualMachineImpl var0, int var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, int var1) {
            PacketStream var2 = new PacketStream(var0, 1, 10);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.Exit" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 exitCode(int): " + var1);
            }

            var2.writeInt(var1);
            var2.send();
            return var2;
         }

         static Exit waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Exit(var0, var1);
         }

         private Exit(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.Exit" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class Resume {
         static final int COMMAND = 9;

         static Resume process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 9);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.Resume" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static Resume waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Resume(var0, var1);
         }

         private Resume(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.Resume" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class Suspend {
         static final int COMMAND = 8;

         static Suspend process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 8);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.Suspend" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static Suspend waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Suspend(var0, var1);
         }

         private Suspend(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.Suspend" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class IDSizes {
         static final int COMMAND = 7;
         final int fieldIDSize;
         final int methodIDSize;
         final int objectIDSize;
         final int referenceTypeIDSize;
         final int frameIDSize;

         static IDSizes process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 7);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.IDSizes" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static IDSizes waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new IDSizes(var0, var1);
         }

         private IDSizes(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.IDSizes" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.fieldIDSize = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "fieldIDSize(int): " + this.fieldIDSize);
            }

            this.methodIDSize = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "methodIDSize(int): " + this.methodIDSize);
            }

            this.objectIDSize = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "objectIDSize(int): " + this.objectIDSize);
            }

            this.referenceTypeIDSize = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "referenceTypeIDSize(int): " + this.referenceTypeIDSize);
            }

            this.frameIDSize = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "frameIDSize(int): " + this.frameIDSize);
            }

         }
      }

      static class Dispose {
         static final int COMMAND = 6;

         static Dispose process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 6);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.Dispose" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static Dispose waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Dispose(var0, var1);
         }

         private Dispose(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.Dispose" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

         }
      }

      static class TopLevelThreadGroups {
         static final int COMMAND = 5;
         final ThreadGroupReferenceImpl[] groups;

         static TopLevelThreadGroups process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 5);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.TopLevelThreadGroups" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static TopLevelThreadGroups waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new TopLevelThreadGroups(var0, var1);
         }

         private TopLevelThreadGroups(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.TopLevelThreadGroups" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "groups(ThreadGroupReferenceImpl[]): ");
            }

            int var3 = var2.readInt();
            this.groups = new ThreadGroupReferenceImpl[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.groups[var4] = var2.readThreadGroupReference();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "groups[i](ThreadGroupReferenceImpl): " + (this.groups[var4] == null ? "NULL" : "ref=" + this.groups[var4].ref()));
               }
            }

         }
      }

      static class AllThreads {
         static final int COMMAND = 4;
         final ThreadReferenceImpl[] threads;

         static AllThreads process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 4);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.AllThreads" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static AllThreads waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new AllThreads(var0, var1);
         }

         private AllThreads(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.AllThreads" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "threads(ThreadReferenceImpl[]): ");
            }

            int var3 = var2.readInt();
            this.threads = new ThreadReferenceImpl[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               this.threads[var4] = var2.readThreadReference();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "threads[i](ThreadReferenceImpl): " + (this.threads[var4] == null ? "NULL" : "ref=" + this.threads[var4].ref()));
               }
            }

         }
      }

      static class AllClasses {
         static final int COMMAND = 3;
         final ClassInfo[] classes;

         static AllClasses process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 3);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.AllClasses" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static AllClasses waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new AllClasses(var0, var1);
         }

         private AllClasses(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.AllClasses" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "classes(ClassInfo[]): ");
            }

            int var3 = var2.readInt();
            this.classes = new ClassInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "classes[i](ClassInfo): ");
               }

               this.classes[var4] = new ClassInfo(var1, var2);
            }

         }

         static class ClassInfo {
            final byte refTypeTag;
            final long typeID;
            final String signature;
            final int status;

            private ClassInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.refTypeTag = var2.readByte();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "refTypeTag(byte): " + this.refTypeTag);
               }

               this.typeID = var2.readClassRef();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "typeID(long): ref=" + this.typeID);
               }

               this.signature = var2.readString();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "signature(String): " + this.signature);
               }

               this.status = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "status(int): " + this.status);
               }

            }

            // $FF: synthetic method
            ClassInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class ClassesBySignature {
         static final int COMMAND = 2;
         final ClassInfo[] classes;

         static ClassesBySignature process(VirtualMachineImpl var0, String var1) throws JDWPException {
            PacketStream var2 = enqueueCommand(var0, var1);
            return waitForReply(var0, var2);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0, String var1) {
            PacketStream var2 = new PacketStream(var0, 1, 2);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.ClassesBySignature" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : ""));
            }

            if ((var2.vm.traceFlags & 1) != 0) {
               var2.vm.printTrace("Sending:                 signature(String): " + var1);
            }

            var2.writeString(var1);
            var2.send();
            return var2;
         }

         static ClassesBySignature waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new ClassesBySignature(var0, var1);
         }

         private ClassesBySignature(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.ClassesBySignature" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "classes(ClassInfo[]): ");
            }

            int var3 = var2.readInt();
            this.classes = new ClassInfo[var3];

            for(int var4 = 0; var4 < var3; ++var4) {
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "classes[i](ClassInfo): ");
               }

               this.classes[var4] = new ClassInfo(var1, var2);
            }

         }

         static class ClassInfo {
            final byte refTypeTag;
            final long typeID;
            final int status;

            private ClassInfo(VirtualMachineImpl var1, PacketStream var2) {
               this.refTypeTag = var2.readByte();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "refTypeTag(byte): " + this.refTypeTag);
               }

               this.typeID = var2.readClassRef();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "typeID(long): ref=" + this.typeID);
               }

               this.status = var2.readInt();
               if (var1.traceReceives) {
                  var1.printReceiveTrace(5, "status(int): " + this.status);
               }

            }

            // $FF: synthetic method
            ClassInfo(VirtualMachineImpl var1, PacketStream var2, Object var3) {
               this(var1, var2);
            }
         }
      }

      static class Version {
         static final int COMMAND = 1;
         final String description;
         final int jdwpMajor;
         final int jdwpMinor;
         final String vmVersion;
         final String vmName;

         static Version process(VirtualMachineImpl var0) throws JDWPException {
            PacketStream var1 = enqueueCommand(var0);
            return waitForReply(var0, var1);
         }

         static PacketStream enqueueCommand(VirtualMachineImpl var0) {
            PacketStream var1 = new PacketStream(var0, 1, 1);
            if ((var0.traceFlags & 1) != 0) {
               var0.printTrace("Sending Command(id=" + var1.pkt.id + ") JDWP.VirtualMachine.Version" + (var1.pkt.flags != 0 ? ", FLAGS=" + var1.pkt.flags : ""));
            }

            var1.send();
            return var1;
         }

         static Version waitForReply(VirtualMachineImpl var0, PacketStream var1) throws JDWPException {
            var1.waitForReply();
            return new Version(var0, var1);
         }

         private Version(VirtualMachineImpl var1, PacketStream var2) {
            if (var1.traceReceives) {
               var1.printTrace("Receiving Command(id=" + var2.pkt.id + ") JDWP.VirtualMachine.Version" + (var2.pkt.flags != 0 ? ", FLAGS=" + var2.pkt.flags : "") + (var2.pkt.errorCode != 0 ? ", ERROR CODE=" + var2.pkt.errorCode : ""));
            }

            this.description = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "description(String): " + this.description);
            }

            this.jdwpMajor = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "jdwpMajor(int): " + this.jdwpMajor);
            }

            this.jdwpMinor = var2.readInt();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "jdwpMinor(int): " + this.jdwpMinor);
            }

            this.vmVersion = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "vmVersion(String): " + this.vmVersion);
            }

            this.vmName = var2.readString();
            if (var1.traceReceives) {
               var1.printReceiveTrace(4, "vmName(String): " + this.vmName);
            }

         }
      }
   }
}
