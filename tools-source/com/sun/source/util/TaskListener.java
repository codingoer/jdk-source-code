package com.sun.source.util;

import jdk.Exported;

@Exported
public interface TaskListener {
   void started(TaskEvent var1);

   void finished(TaskEvent var1);
}
