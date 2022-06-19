package com.sun.xml.internal.xsom.visitor;

import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSWildcard;

public interface XSTermFunctionWithParam {
   Object wildcard(XSWildcard var1, Object var2);

   Object modelGroupDecl(XSModelGroupDecl var1, Object var2);

   Object modelGroup(XSModelGroup var1, Object var2);

   Object elementDecl(XSElementDecl var1, Object var2);
}
