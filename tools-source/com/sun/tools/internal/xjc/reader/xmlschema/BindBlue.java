package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSWildcard;

final class BindBlue extends ColorBinder {
   public void complexType(XSComplexType ct) {
      throw new UnsupportedOperationException();
   }

   public void elementDecl(XSElementDecl e) {
      throw new UnsupportedOperationException();
   }

   public void wildcard(XSWildcard xsWildcard) {
      throw new UnsupportedOperationException();
   }

   public void attGroupDecl(XSAttGroupDecl xsAttGroupDecl) {
      throw new UnsupportedOperationException();
   }

   public void attributeDecl(XSAttributeDecl xsAttributeDecl) {
      throw new UnsupportedOperationException();
   }

   public void attributeUse(XSAttributeUse use) {
      throw new UnsupportedOperationException();
   }

   public void modelGroupDecl(XSModelGroupDecl xsModelGroupDecl) {
      throw new UnsupportedOperationException();
   }

   public void modelGroup(XSModelGroup xsModelGroup) {
      throw new UnsupportedOperationException();
   }

   public void particle(XSParticle xsParticle) {
      throw new UnsupportedOperationException();
   }

   public void empty(XSContentType xsContentType) {
      throw new UnsupportedOperationException();
   }

   public void simpleType(XSSimpleType type) {
      throw new IllegalStateException();
   }
}
