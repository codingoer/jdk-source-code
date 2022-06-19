package com.sun.xml.internal.xsom.visitor;

import com.sun.xml.internal.xsom.XSWildcard;

public interface XSWildcardFunction {
   Object any(XSWildcard.Any var1);

   Object other(XSWildcard.Other var1);

   Object union(XSWildcard.Union var1);
}
