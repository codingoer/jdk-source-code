package com.sun.tools.internal.xjc.outline;

import com.sun.codemodel.internal.JBlock;
import com.sun.codemodel.internal.JExpression;
import com.sun.codemodel.internal.JVar;
import com.sun.tools.internal.xjc.model.CPropertyInfo;

public interface FieldAccessor {
   void toRawValue(JBlock var1, JVar var2);

   void fromRawValue(JBlock var1, String var2, JExpression var3);

   void unsetValues(JBlock var1);

   JExpression hasSetValue();

   FieldOutline owner();

   CPropertyInfo getPropertyInfo();
}
