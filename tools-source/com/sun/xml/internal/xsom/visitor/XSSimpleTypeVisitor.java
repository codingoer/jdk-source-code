package com.sun.xml.internal.xsom.visitor;

import com.sun.xml.internal.xsom.XSListSimpleType;
import com.sun.xml.internal.xsom.XSRestrictionSimpleType;
import com.sun.xml.internal.xsom.XSUnionSimpleType;

public interface XSSimpleTypeVisitor {
   void listSimpleType(XSListSimpleType var1);

   void unionSimpleType(XSUnionSimpleType var1);

   void restrictionSimpleType(XSRestrictionSimpleType var1);
}
