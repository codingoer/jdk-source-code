package com.sun.tools.internal.xjc.model;

import com.sun.xml.internal.bind.v2.model.core.Element;

public interface CElement extends CTypeInfo, Element {
   void setAbstract();

   boolean isAbstract();
}
