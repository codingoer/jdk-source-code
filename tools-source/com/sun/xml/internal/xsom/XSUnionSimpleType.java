package com.sun.xml.internal.xsom;

public interface XSUnionSimpleType extends XSSimpleType, Iterable {
   XSSimpleType getMember(int var1);

   int getMemberSize();
}
