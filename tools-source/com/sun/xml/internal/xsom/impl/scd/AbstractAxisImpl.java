package com.sun.xml.internal.xsom.impl.scd;

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
import java.util.Iterator;

abstract class AbstractAxisImpl implements Axis, XSFunction {
   protected final Iterator singleton(XSComponent t) {
      return Iterators.singleton(t);
   }

   protected final Iterator union(XSComponent... items) {
      return new Iterators.Array(items);
   }

   protected final Iterator union(Iterator first, Iterator second) {
      return new Iterators.Union(first, second);
   }

   public Iterator iterator(XSComponent contextNode) {
      return (Iterator)contextNode.apply(this);
   }

   public String getName() {
      return this.toString();
   }

   public Iterator iterator(Iterator contextNodes) {
      return new Iterators.Map(contextNodes) {
         protected Iterator apply(XSComponent u) {
            return AbstractAxisImpl.this.iterator(u);
         }
      };
   }

   public boolean isModelGroup() {
      return false;
   }

   public Iterator annotation(XSAnnotation ann) {
      return this.empty();
   }

   public Iterator attGroupDecl(XSAttGroupDecl decl) {
      return this.empty();
   }

   public Iterator attributeDecl(XSAttributeDecl decl) {
      return this.empty();
   }

   public Iterator attributeUse(XSAttributeUse use) {
      return this.empty();
   }

   public Iterator complexType(XSComplexType type) {
      XSParticle p = type.getContentType().asParticle();
      return p != null ? this.particle(p) : this.empty();
   }

   public Iterator schema(XSSchema schema) {
      return this.empty();
   }

   public Iterator facet(XSFacet facet) {
      return this.empty();
   }

   public Iterator notation(XSNotation notation) {
      return this.empty();
   }

   public Iterator identityConstraint(XSIdentityConstraint decl) {
      return this.empty();
   }

   public Iterator xpath(XSXPath xpath) {
      return this.empty();
   }

   public Iterator simpleType(XSSimpleType simpleType) {
      return this.empty();
   }

   public Iterator particle(XSParticle particle) {
      return this.empty();
   }

   public Iterator empty(XSContentType empty) {
      return this.empty();
   }

   public Iterator wildcard(XSWildcard wc) {
      return this.empty();
   }

   public Iterator modelGroupDecl(XSModelGroupDecl decl) {
      return this.empty();
   }

   public Iterator modelGroup(XSModelGroup group) {
      return new Iterators.Map(group.iterator()) {
         protected Iterator apply(XSParticle p) {
            return AbstractAxisImpl.this.particle(p);
         }
      };
   }

   public Iterator elementDecl(XSElementDecl decl) {
      return this.empty();
   }

   protected final Iterator empty() {
      return Iterators.empty();
   }
}
