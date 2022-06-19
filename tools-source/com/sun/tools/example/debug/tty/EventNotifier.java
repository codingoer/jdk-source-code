package com.sun.tools.example.debug.tty;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;

interface EventNotifier {
   void vmStartEvent(VMStartEvent var1);

   void vmDeathEvent(VMDeathEvent var1);

   void vmDisconnectEvent(VMDisconnectEvent var1);

   void threadStartEvent(ThreadStartEvent var1);

   void threadDeathEvent(ThreadDeathEvent var1);

   void classPrepareEvent(ClassPrepareEvent var1);

   void classUnloadEvent(ClassUnloadEvent var1);

   void breakpointEvent(BreakpointEvent var1);

   void fieldWatchEvent(WatchpointEvent var1);

   void stepEvent(StepEvent var1);

   void exceptionEvent(ExceptionEvent var1);

   void methodEntryEvent(MethodEntryEvent var1);

   boolean methodExitEvent(MethodExitEvent var1);

   void vmInterrupted();

   void receivedEvent(Event var1);
}
