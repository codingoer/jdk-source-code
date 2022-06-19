package com.sun.tools.internal.xjc.model;

import com.sun.xml.internal.bind.v2.model.core.NonElement;

public interface CNonElement extends NonElement, TypeUse, CTypeInfo {
   /** @deprecated */
   @Deprecated
   CNonElement getInfo();

   /** @deprecated */
   @Deprecated
   boolean isCollection();

   /** @deprecated */
   @Deprecated
   CAdapter getAdapterUse();
}
