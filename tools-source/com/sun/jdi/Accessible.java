package com.sun.jdi;

import jdk.Exported;

@Exported
public interface Accessible {
   int modifiers();

   boolean isPrivate();

   boolean isPackagePrivate();

   boolean isProtected();

   boolean isPublic();
}
