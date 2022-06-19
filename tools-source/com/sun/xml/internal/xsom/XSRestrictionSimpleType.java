package com.sun.xml.internal.xsom;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface XSRestrictionSimpleType extends XSSimpleType {
   Iterator iterateDeclaredFacets();

   Collection getDeclaredFacets();

   XSFacet getDeclaredFacet(String var1);

   List getDeclaredFacets(String var1);
}
