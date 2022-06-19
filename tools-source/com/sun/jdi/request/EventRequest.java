package com.sun.jdi.request;

import com.sun.jdi.Mirror;
import jdk.Exported;

@Exported
public interface EventRequest extends Mirror {
   int SUSPEND_NONE = 0;
   int SUSPEND_EVENT_THREAD = 1;
   int SUSPEND_ALL = 2;

   boolean isEnabled();

   void setEnabled(boolean var1);

   void enable();

   void disable();

   void addCountFilter(int var1);

   void setSuspendPolicy(int var1);

   int suspendPolicy();

   void putProperty(Object var1, Object var2);

   Object getProperty(Object var1);
}
