package com.sun.xml.internal.xsom.visitor;

import com.sun.xml.internal.xsom.XSWildcard;

public interface XSWildcardVisitor {
   void any(XSWildcard.Any var1);

   void other(XSWildcard.Other var1);

   void union(XSWildcard.Union var1);
}
