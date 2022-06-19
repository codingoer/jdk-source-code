package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.outline.Aspect;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;

public interface CTypeInfo extends TypeInfo, CCustomizable {
   JType toType(Outline var1, Aspect var2);
}
