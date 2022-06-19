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

public final class BindYellow extends ColorBinder {
   public void complexType(XSComplexType ct) {
   }

   public void wildcard(XSWildcard xsWildcard) {
      throw new UnsupportedOperationException();
   }

   public void elementDecl(XSElementDecl xsElementDecl) {
      throw new UnsupportedOperationException();
   }

   public void simpleType(XSSimpleType xsSimpleType) {
      throw new UnsupportedOperationException();
   }

   public void attributeDecl(XSAttributeDecl xsAttributeDecl) {
      throw new UnsupportedOperationException();
   }

   public void attGroupDecl(XSAttGroupDecl xsAttGroupDecl) {
      throw new IllegalStateException();
   }

   public void attributeUse(XSAttributeUse use) {
      throw new IllegalStateException();
   }

   public void modelGroupDecl(XSModelGroupDecl xsModelGroupDecl) {
      throw new IllegalStateException();
   }

   public void modelGroup(XSModelGroup xsModelGroup) {
      throw new IllegalStateException();
   }

   public void particle(XSParticle xsParticle) {
      throw new IllegalStateException();
   }

   public void empty(XSContentType xsContentType) {
      throw new IllegalStateException();
   }
}
