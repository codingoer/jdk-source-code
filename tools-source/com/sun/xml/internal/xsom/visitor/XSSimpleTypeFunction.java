package com.sun.xml.internal.xsom.visitor;

import com.sun.xml.internal.xsom.XSListSimpleType;
import com.sun.xml.internal.xsom.XSRestrictionSimpleType;
import com.sun.xml.internal.xsom.XSUnionSimpleType;

public interface XSSimpleTypeFunction {
   Object listSimpleType(XSListSimpleType var1);

   Object unionSimpleType(XSUnionSimpleType var1);

   Object restrictionSimpleType(XSRestrictionSimpleType var1);
}
