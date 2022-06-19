package com.sun.tools.internal.xjc.outline;

import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JType;
import com.sun.tools.internal.xjc.model.CPropertyInfo;

public interface FieldOutline {
   ClassOutline parent();

   CPropertyInfo getPropertyInfo();

   JType getRawType();

   FieldAccessor create(JExpression var1);
}
