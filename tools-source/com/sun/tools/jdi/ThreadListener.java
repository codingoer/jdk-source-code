package com.sun.tools.jdi;

import java.util.EventListener;

interface ThreadListener extends EventListener {
   boolean threadResumable(ThreadAction var1);
}
