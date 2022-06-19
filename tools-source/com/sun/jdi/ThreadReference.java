package com.sun.jdi;

import java.util.List;
import jdk.Exported;

@Exported
public interface ThreadReference extends ObjectReference {
   int THREAD_STATUS_UNKNOWN = -1;
   int THREAD_STATUS_ZOMBIE = 0;
   int THREAD_STATUS_RUNNING = 1;
   int THREAD_STATUS_SLEEPING = 2;
   int THREAD_STATUS_MONITOR = 3;
   int THREAD_STATUS_WAIT = 4;
   int THREAD_STATUS_NOT_STARTED = 5;

   String name();

   void suspend();

   void resume();

   int suspendCount();

   void stop(ObjectReference var1) throws InvalidTypeException;

   void interrupt();

   int status();

   boolean isSuspended();

   boolean isAtBreakpoint();

   ThreadGroupReference threadGroup();

   int frameCount() throws IncompatibleThreadStateException;

   List frames() throws IncompatibleThreadStateException;

   StackFrame frame(int var1) throws IncompatibleThreadStateException;

   List frames(int var1, int var2) throws IncompatibleThreadStateException;

   List ownedMonitors() throws IncompatibleThreadStateException;

   List ownedMonitorsAndFrames() throws IncompatibleThreadStateException;

   ObjectReference currentContendedMonitor() throws IncompatibleThreadStateException;

   void popFrames(StackFrame var1) throws IncompatibleThreadStateException;

   void forceEarlyReturn(Value var1) throws InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException;
}
