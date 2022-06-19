package com.sun.jdi;

import com.sun.jdi.event.EventQueue;
import com.sun.jdi.request.EventRequestManager;
import java.util.List;
import java.util.Map;
import jdk.Exported;

@Exported
public interface VirtualMachine extends Mirror {
   int TRACE_NONE = 0;
   int TRACE_SENDS = 1;
   int TRACE_RECEIVES = 2;
   int TRACE_EVENTS = 4;
   int TRACE_REFTYPES = 8;
   int TRACE_OBJREFS = 16;
   int TRACE_ALL = 16777215;

   List classesByName(String var1);

   List allClasses();

   void redefineClasses(Map var1);

   List allThreads();

   void suspend();

   void resume();

   List topLevelThreadGroups();

   EventQueue eventQueue();

   EventRequestManager eventRequestManager();

   BooleanValue mirrorOf(boolean var1);

   ByteValue mirrorOf(byte var1);

   CharValue mirrorOf(char var1);

   ShortValue mirrorOf(short var1);

   IntegerValue mirrorOf(int var1);

   LongValue mirrorOf(long var1);

   FloatValue mirrorOf(float var1);

   DoubleValue mirrorOf(double var1);

   StringReference mirrorOf(String var1);

   VoidValue mirrorOfVoid();

   Process process();

   void dispose();

   void exit(int var1);

   boolean canWatchFieldModification();

   boolean canWatchFieldAccess();

   boolean canGetBytecodes();

   boolean canGetSyntheticAttribute();

   boolean canGetOwnedMonitorInfo();

   boolean canGetCurrentContendedMonitor();

   boolean canGetMonitorInfo();

   boolean canUseInstanceFilters();

   boolean canRedefineClasses();

   boolean canAddMethod();

   boolean canUnrestrictedlyRedefineClasses();

   boolean canPopFrames();

   boolean canGetSourceDebugExtension();

   boolean canRequestVMDeathEvent();

   boolean canGetMethodReturnValues();

   boolean canGetInstanceInfo();

   boolean canUseSourceNameFilters();

   boolean canForceEarlyReturn();

   boolean canBeModified();

   boolean canRequestMonitorEvents();

   boolean canGetMonitorFrameInfo();

   boolean canGetClassFileVersion();

   boolean canGetConstantPool();

   void setDefaultStratum(String var1);

   String getDefaultStratum();

   long[] instanceCounts(List var1);

   String description();

   String version();

   String name();

   void setDebugTraceMode(int var1);
}
