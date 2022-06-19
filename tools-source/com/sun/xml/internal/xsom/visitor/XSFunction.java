package com.sun.xml.internal.xsom.visitor;

import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSXPath;

public interface XSFunction extends XSContentTypeFunction, XSTermFunction {
   Object annotation(XSAnnotation var1);

   Object attGroupDecl(XSAttGroupDecl var1);

   Object attributeDecl(XSAttributeDecl var1);

   Object attributeUse(XSAttributeUse var1);

   Object complexType(XSComplexType var1);

   Object schema(XSSchema var1);

   Object facet(XSFacet var1);

   Object notation(XSNotation var1);

   Object identityConstraint(XSIdentityConstraint var1);

   Object xpath(XSXPath var1);
}
