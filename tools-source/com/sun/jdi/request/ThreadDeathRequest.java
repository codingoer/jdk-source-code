package com.sun.jdi.request;

import com.sun.jdi.ThreadReference;
import jdk.Exported;

@Exported
public interface ThreadDeathRequest extends EventRequest {
   void addThreadFilter(ThreadReference var1);
}
