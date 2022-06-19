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

public interface XSVisitor extends XSTermVisitor, XSContentTypeVisitor {
   void annotation(XSAnnotation var1);

   void attGroupDecl(XSAttGroupDecl var1);

   void attributeDecl(XSAttributeDecl var1);

   void attributeUse(XSAttributeUse var1);

   void complexType(XSComplexType var1);

   void schema(XSSchema var1);

   void facet(XSFacet var1);

   void notation(XSNotation var1);

   void identityConstraint(XSIdentityConstraint var1);

   void xpath(XSXPath var1);
}
