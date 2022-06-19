package com.sun.tools.internal.xjc.api;

import java.util.List;
import javax.xml.namespace.QName;

public interface Mapping {
   QName getElement();

   TypeAndAnnotation getType();

   List getWrapperStyleDrilldown();
}
