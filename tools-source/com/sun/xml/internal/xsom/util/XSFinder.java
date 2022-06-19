package com.sun.xml.internal.xsom.util;

import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.XSXPath;
import com.sun.xml.internal.xsom.visitor.XSFunction;

public class XSFinder implements XSFunction {
   public final boolean find(XSComponent c) {
      return (Boolean)c.apply(this);
   }

   public Boolean annotation(XSAnnotation ann) {
      return Boolean.FALSE;
   }

   public Boolean attGroupDecl(XSAttGroupDecl decl) {
      return Boolean.FALSE;
   }

   public Boolean attributeDecl(XSAttributeDecl decl) {
      return Boolean.FALSE;
   }

   public Boolean attributeUse(XSAttributeUse use) {
      return Boolean.FALSE;
   }

   public Boolean complexType(XSComplexType type) {
      return Boolean.FALSE;
   }

   public Boolean schema(XSSchema schema) {
      return Boolean.FALSE;
   }

   public Boolean facet(XSFacet facet) {
      return Boolean.FALSE;
   }

   public Boolean notation(XSNotation notation) {
      return Boolean.FALSE;
   }

   public Boolean simpleType(XSSimpleType simpleType) {
      return Boolean.FALSE;
   }

   public Boolean particle(XSParticle particle) {
      return Boolean.FALSE;
   }

   public Boolean empty(XSContentType empty) {
      return Boolean.FALSE;
   }

   public Boolean wildcard(XSWildcard wc) {
      return Boolean.FALSE;
   }

   public Boolean modelGroupDecl(XSModelGroupDecl decl) {
      return Boolean.FALSE;
   }

   public Boolean modelGroup(XSModelGroup group) {
      return Boolean.FALSE;
   }

   public Boolean elementDecl(XSElementDecl decl) {
      return Boolean.FALSE;
   }

   public Boolean identityConstraint(XSIdentityConstraint decl) {
      return Boolean.FALSE;
   }

   public Boolean xpath(XSXPath xpath) {
      return Boolean.FALSE;
   }
}
