package com.sun.tools.internal.xjc.model.nav;

import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;

public interface NType {
   JType toType(Outline var1, Aspect var2);

   boolean isBoxedType();

   String fullName();
}
