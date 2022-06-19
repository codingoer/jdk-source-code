package com.sun.jdi.event;

import com.sun.jdi.Mirror;
import java.util.Set;
import jdk.Exported;

@Exported
public interface EventSet extends Mirror, Set {
   int suspendPolicy();

   EventIterator eventIterator();

   void resume();
}
