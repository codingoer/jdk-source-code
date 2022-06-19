package com.sun.xml.internal.xsom.util;

import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
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

public class XSFunctionFilter implements XSFunction {
   protected XSFunction core;

   public XSFunctionFilter(XSFunction _core) {
      this.core = _core;
   }

   public XSFunctionFilter() {
   }

   public Object annotation(XSAnnotation ann) {
      return this.core.annotation(ann);
   }

   public Object attGroupDecl(XSAttGroupDecl decl) {
      return this.core.attGroupDecl(decl);
   }

   public Object attributeDecl(XSAttributeDecl decl) {
      return this.core.attributeDecl(decl);
   }

   public Object attributeUse(XSAttributeUse use) {
      return this.core.attributeUse(use);
   }

   public Object complexType(XSComplexType type) {
      return this.core.complexType(type);
   }

   public Object schema(XSSchema schema) {
      return this.core.schema(schema);
   }

   public Object facet(XSFacet facet) {
      return this.core.facet(facet);
   }

   public Object notation(XSNotation notation) {
      return this.core.notation(notation);
   }

   public Object simpleType(XSSimpleType simpleType) {
      return this.core.simpleType(simpleType);
   }

   public Object particle(XSParticle particle) {
      return this.core.particle(particle);
   }

   public Object empty(XSContentType empty) {
      return this.core.empty(empty);
   }

   public Object wildcard(XSWildcard wc) {
      return this.core.wildcard(wc);
   }

   public Object modelGroupDecl(XSModelGroupDecl decl) {
      return this.core.modelGroupDecl(decl);
   }

   public Object modelGroup(XSModelGroup group) {
      return this.core.modelGroup(group);
   }

   public Object elementDecl(XSElementDecl decl) {
      return this.core.elementDecl(decl);
   }

   public Object identityConstraint(XSIdentityConstraint decl) {
      return this.core.identityConstraint(decl);
   }

   public Object xpath(XSXPath xpath) {
      return this.core.xpath(xpath);
   }
}
