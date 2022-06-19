package com.sun.xml.internal.xsom.visitor;

import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSWildcard;

public interface XSTermVisitor {
   void wildcard(XSWildcard var1);

   void modelGroupDecl(XSModelGroupDecl var1);

   void modelGroup(XSModelGroup var1);

   void elementDecl(XSElementDecl var1);
}
