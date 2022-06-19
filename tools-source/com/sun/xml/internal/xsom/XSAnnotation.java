package com.sun.xml.internal.xsom;

import org.xml.sax.Locator;

public interface XSAnnotation {
   Object getAnnotation();

   Object setAnnotation(Object var1);

   Locator getLocator();
}
