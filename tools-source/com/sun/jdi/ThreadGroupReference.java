package com.sun.jdi;

import java.util.List;
import jdk.Exported;

@Exported
public interface ThreadGroupReference extends ObjectReference {
   String name();

   ThreadGroupReference parent();

   void suspend();

   void resume();

   List threads();

   List threadGroups();
}
