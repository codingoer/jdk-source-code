package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSRestrictionSimpleType;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSVariety;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeFunction;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeVisitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.xml.sax.Locator;

public class RestrictionSimpleTypeImpl extends SimpleTypeImpl implements XSRestrictionSimpleType {
   private final List facets = new ArrayList();

   public RestrictionSimpleTypeImpl(SchemaDocumentImpl _parent, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, String _name, boolean _anonymous, Set finalSet, Ref.SimpleType _baseType) {
      super(_parent, _annon, _loc, _fa, _name, _anonymous, finalSet, _baseType);
   }

   public void addFacet(XSFacet facet) {
      this.facets.add(facet);
   }

   public Iterator iterateDeclaredFacets() {
      return this.facets.iterator();
   }

   public Collection getDeclaredFacets() {
      return this.facets;
   }

   public XSFacet getDeclaredFacet(String name) {
      int len = this.facets.size();

      for(int i = 0; i < len; ++i) {
         XSFacet f = (XSFacet)this.facets.get(i);
         if (f.getName().equals(name)) {
            return f;
         }
      }

      return null;
   }

   public List getDeclaredFacets(String name) {
      List r = new ArrayList();
      Iterator var3 = this.facets.iterator();

      while(var3.hasNext()) {
         XSFacet f = (XSFacet)var3.next();
         if (f.getName().equals(name)) {
            r.add(f);
         }
      }

      return r;
   }

   public XSFacet getFacet(String name) {
      XSFacet f = this.getDeclaredFacet(name);
      return f != null ? f : this.getSimpleBaseType().getFacet(name);
   }

   public List getFacets(String name) {
      List f = this.getDeclaredFacets(name);
      return !f.isEmpty() ? f : this.getSimpleBaseType().getFacets(name);
   }

   public XSVariety getVariety() {
      return this.getSimpleBaseType().getVariety();
   }

   public XSSimpleType getPrimitiveType() {
      return (XSSimpleType)(this.isPrimitive() ? this : this.getSimpleBaseType().getPrimitiveType());
   }

   public boolean isPrimitive() {
      return this.getSimpleBaseType() == this.getOwnerSchema().getRoot().anySimpleType;
   }

   public void visit(XSSimpleTypeVisitor visitor) {
      visitor.restrictionSimpleType(this);
   }

   public Object apply(XSSimpleTypeFunction function) {
      return function.restrictionSimpleType(this);
   }

   public boolean isRestriction() {
      return true;
   }

   public XSRestrictionSimpleType asRestriction() {
      return this;
   }
}
