package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSListSimpleType;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSRestrictionSimpleType;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.XSUnionSimpleType;
import com.sun.xml.internal.xsom.XSVariety;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.visitor.XSContentTypeFunction;
import com.sun.xml.internal.xsom.visitor.XSContentTypeVisitor;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.util.Set;
import org.xml.sax.Locator;

public abstract class SimpleTypeImpl extends DeclarationImpl implements XSSimpleType, ContentTypeImpl, Ref.SimpleType {
   private Ref.SimpleType baseType;
   private short redefiningCount = 0;
   private SimpleTypeImpl redefinedBy = null;
   private final Set finalSet;

   SimpleTypeImpl(SchemaDocumentImpl _parent, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, String _name, boolean _anonymous, Set finalSet, Ref.SimpleType _baseType) {
      super(_parent, _annon, _loc, _fa, _parent.getTargetNamespace(), _name, _anonymous);
      this.baseType = _baseType;
      this.finalSet = finalSet;
   }

   public XSType[] listSubstitutables() {
      return Util.listSubstitutables(this);
   }

   public void redefine(SimpleTypeImpl st) {
      this.baseType = st;
      st.redefinedBy = this;
      this.redefiningCount = (short)(st.redefiningCount + 1);
   }

   public XSSimpleType getRedefinedBy() {
      return this.redefinedBy;
   }

   public int getRedefinedCount() {
      int i = 0;

      for(SimpleTypeImpl st = this.redefinedBy; st != null; st = st.redefinedBy) {
         ++i;
      }

      return i;
   }

   public XSType getBaseType() {
      return this.baseType.getType();
   }

   public XSSimpleType getSimpleBaseType() {
      return this.baseType.getType();
   }

   public boolean isPrimitive() {
      return false;
   }

   public XSListSimpleType getBaseListType() {
      return this.getSimpleBaseType().getBaseListType();
   }

   public XSUnionSimpleType getBaseUnionType() {
      return this.getSimpleBaseType().getBaseUnionType();
   }

   public boolean isFinal(XSVariety v) {
      return this.finalSet.contains(v);
   }

   public final int getDerivationMethod() {
      return 2;
   }

   public final XSSimpleType asSimpleType() {
      return this;
   }

   public final XSComplexType asComplexType() {
      return null;
   }

   public boolean isDerivedFrom(XSType t) {
      XSType s;
      for(XSType x = this; t != x; x = s) {
         s = ((XSType)x).getBaseType();
         if (s == x) {
            return false;
         }
      }

      return true;
   }

   public final boolean isSimpleType() {
      return true;
   }

   public final boolean isComplexType() {
      return false;
   }

   public final XSParticle asParticle() {
      return null;
   }

   public final XSContentType asEmpty() {
      return null;
   }

   public boolean isRestriction() {
      return false;
   }

   public boolean isList() {
      return false;
   }

   public boolean isUnion() {
      return false;
   }

   public XSRestrictionSimpleType asRestriction() {
      return null;
   }

   public XSListSimpleType asList() {
      return null;
   }

   public XSUnionSimpleType asUnion() {
      return null;
   }

   public final void visit(XSVisitor visitor) {
      visitor.simpleType(this);
   }

   public final void visit(XSContentTypeVisitor visitor) {
      visitor.simpleType(this);
   }

   public final Object apply(XSFunction function) {
      return function.simpleType(this);
   }

   public final Object apply(XSContentTypeFunction function) {
      return function.simpleType(this);
   }

   public XSContentType getContentType() {
      return this;
   }

   public XSSimpleType getType() {
      return this;
   }
}
