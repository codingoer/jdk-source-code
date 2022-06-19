package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.model.CElement;
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

abstract class ClassBinderFilter implements ClassBinder {
   private final ClassBinder core;

   protected ClassBinderFilter(ClassBinder core) {
      this.core = core;
   }

   public CElement annotation(XSAnnotation xsAnnotation) {
      return (CElement)this.core.annotation(xsAnnotation);
   }

   public CElement attGroupDecl(XSAttGroupDecl xsAttGroupDecl) {
      return (CElement)this.core.attGroupDecl(xsAttGroupDecl);
   }

   public CElement attributeDecl(XSAttributeDecl xsAttributeDecl) {
      return (CElement)this.core.attributeDecl(xsAttributeDecl);
   }

   public CElement attributeUse(XSAttributeUse xsAttributeUse) {
      return (CElement)this.core.attributeUse(xsAttributeUse);
   }

   public CElement complexType(XSComplexType xsComplexType) {
      return (CElement)this.core.complexType(xsComplexType);
   }

   public CElement schema(XSSchema xsSchema) {
      return (CElement)this.core.schema(xsSchema);
   }

   public CElement facet(XSFacet xsFacet) {
      return (CElement)this.core.facet(xsFacet);
   }

   public CElement notation(XSNotation xsNotation) {
      return (CElement)this.core.notation(xsNotation);
   }

   public CElement simpleType(XSSimpleType xsSimpleType) {
      return (CElement)this.core.simpleType(xsSimpleType);
   }

   public CElement particle(XSParticle xsParticle) {
      return (CElement)this.core.particle(xsParticle);
   }

   public CElement empty(XSContentType xsContentType) {
      return (CElement)this.core.empty(xsContentType);
   }

   public CElement wildcard(XSWildcard xsWildcard) {
      return (CElement)this.core.wildcard(xsWildcard);
   }

   public CElement modelGroupDecl(XSModelGroupDecl xsModelGroupDecl) {
      return (CElement)this.core.modelGroupDecl(xsModelGroupDecl);
   }

   public CElement modelGroup(XSModelGroup xsModelGroup) {
      return (CElement)this.core.modelGroup(xsModelGroup);
   }

   public CElement elementDecl(XSElementDecl xsElementDecl) {
      return (CElement)this.core.elementDecl(xsElementDecl);
   }

   public CElement identityConstraint(XSIdentityConstraint xsIdentityConstraint) {
      return (CElement)this.core.identityConstraint(xsIdentityConstraint);
   }

   public CElement xpath(XSXPath xsxPath) {
      return (CElement)this.core.xpath(xsxPath);
   }
}
