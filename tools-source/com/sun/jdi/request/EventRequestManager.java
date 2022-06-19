package com.sun.jdi.request;

import com.sun.jdi.Field;
import com.sun.jdi.Location;
import com.sun.jdi.Mirror;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import java.util.List;
import jdk.Exported;

@Exported
public interface EventRequestManager extends Mirror {
   ClassPrepareRequest createClassPrepareRequest();

   ClassUnloadRequest createClassUnloadRequest();

   ThreadStartRequest createThreadStartRequest();

   ThreadDeathRequest createThreadDeathRequest();

   ExceptionRequest createExceptionRequest(ReferenceType var1, boolean var2, boolean var3);

   MethodEntryRequest createMethodEntryRequest();

   MethodExitRequest createMethodExitRequest();

   MonitorContendedEnterRequest createMonitorContendedEnterRequest();

   MonitorContendedEnteredRequest createMonitorContendedEnteredRequest();

   MonitorWaitRequest createMonitorWaitRequest();

   MonitorWaitedRequest createMonitorWaitedRequest();

   StepRequest createStepRequest(ThreadReference var1, int var2, int var3);

   BreakpointRequest createBreakpointRequest(Location var1);

   AccessWatchpointRequest createAccessWatchpointRequest(Field var1);

   ModificationWatchpointRequest createModificationWatchpointRequest(Field var1);

   VMDeathRequest createVMDeathRequest();

   void deleteEventRequest(EventRequest var1);

   void deleteEventRequests(List var1);

   void deleteAllBreakpoints();

   List stepRequests();

   List classPrepareRequests();

   List classUnloadRequests();

   List threadStartRequests();

   List threadDeathRequests();

   List exceptionRequests();

   List breakpointRequests();

   List accessWatchpointRequests();

   List modificationWatchpointRequests();

   List methodEntryRequests();

   List methodExitRequests();

   List monitorContendedEnterRequests();

   List monitorContendedEnteredRequests();

   List monitorWaitRequests();

   List monitorWaitedRequests();

   List vmDeathRequests();
}
