package com.sun.xml.internal.xsom;

import com.sun.xml.internal.xsom.visitor.XSSimpleTypeFunction;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeVisitor;
import java.util.List;

public interface XSSimpleType extends XSType, XSContentType {
   XSSimpleType getSimpleBaseType();

   XSVariety getVariety();

   XSSimpleType getPrimitiveType();

   boolean isPrimitive();

   XSListSimpleType getBaseListType();

   XSUnionSimpleType getBaseUnionType();

   boolean isFinal(XSVariety var1);

   XSSimpleType getRedefinedBy();

   XSFacet getFacet(String var1);

   List getFacets(String var1);

   void visit(XSSimpleTypeVisitor var1);

   Object apply(XSSimpleTypeFunction var1);

   boolean isRestriction();

   boolean isList();

   boolean isUnion();

   XSRestrictionSimpleType asRestriction();

   XSListSimpleType asList();

   XSUnionSimpleType asUnion();
}
