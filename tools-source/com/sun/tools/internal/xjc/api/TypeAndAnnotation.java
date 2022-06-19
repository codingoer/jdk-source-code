package com.sun.tools.internal.xjc.api;

import com.sun.codemodel.internal.JAnnotatable;
import com.sun.codemodel.internal.JType;

public interface TypeAndAnnotation {
   JType getTypeClass();

   void annotate(JAnnotatable var1);

   boolean equals(Object var1);
}
