package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSUnionSimpleType;
import com.sun.xml.internal.xsom.XSVariety;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeFunction;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeVisitor;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.xml.sax.Locator;

public class UnionSimpleTypeImpl extends SimpleTypeImpl implements XSUnionSimpleType {
   private final Ref.SimpleType[] memberTypes;

   public UnionSimpleTypeImpl(SchemaDocumentImpl _parent, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, String _name, boolean _anonymous, Set finalSet, Ref.SimpleType[] _members) {
      super(_parent, _annon, _loc, _fa, _name, _anonymous, finalSet, _parent.getSchema().parent.anySimpleType);
      this.memberTypes = _members;
   }

   public XSSimpleType getMember(int idx) {
      return this.memberTypes[idx].getType();
   }

   public int getMemberSize() {
      return this.memberTypes.length;
   }

   public Iterator iterator() {
      return new Iterator() {
         int idx = 0;

         public boolean hasNext() {
            return this.idx < UnionSimpleTypeImpl.this.memberTypes.length;
         }

         public XSSimpleType next() {
            return UnionSimpleTypeImpl.this.memberTypes[this.idx++].getType();
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public void visit(XSSimpleTypeVisitor visitor) {
      visitor.unionSimpleType(this);
   }

   public Object apply(XSSimpleTypeFunction function) {
      return function.unionSimpleType(this);
   }

   public XSUnionSimpleType getBaseUnionType() {
      return this;
   }

   public XSFacet getFacet(String name) {
      return null;
   }

   public List getFacets(String name) {
      return Collections.EMPTY_LIST;
   }

   public XSVariety getVariety() {
      return XSVariety.UNION;
   }

   public XSSimpleType getPrimitiveType() {
      return null;
   }

   public boolean isUnion() {
      return true;
   }

   public XSUnionSimpleType asUnion() {
      return this;
   }
}
