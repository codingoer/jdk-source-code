package com.sun.jdi.event;

import java.util.Iterator;
import jdk.Exported;

@Exported
public interface EventIterator extends Iterator {
   Event nextEvent();
}
