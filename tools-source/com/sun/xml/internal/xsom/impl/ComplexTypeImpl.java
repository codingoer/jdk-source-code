package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.impl.parser.DelayedRef;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xml.sax.Locator;

public class ComplexTypeImpl extends AttributesHolder implements XSComplexType, Ref.ComplexType {
   private int derivationMethod;
   private Ref.Type baseType;
   private short redefiningCount = 0;
   private ComplexTypeImpl redefinedBy = null;
   private XSElementDecl scope;
   private final boolean _abstract;
   private WildcardImpl localAttWildcard;
   private final int finalValue;
   private final int blockValue;
   private Ref.ContentType contentType;
   private XSContentType explicitContent;
   private final boolean mixed;

   public ComplexTypeImpl(SchemaDocumentImpl _parent, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl _fa, String _name, boolean _anonymous, boolean _abstract, int _derivationMethod, Ref.Type _base, int _final, int _block, boolean _mixed) {
      super(_parent, _annon, _loc, _fa, _name, _anonymous);
      if (_base == null) {
         throw new IllegalArgumentException();
      } else {
         this._abstract = _abstract;
         this.derivationMethod = _derivationMethod;
         this.baseType = _base;
         this.finalValue = _final;
         this.blockValue = _block;
         this.mixed = _mixed;
      }
   }

   public XSComplexType asComplexType() {
      return this;
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

   public XSSimpleType asSimpleType() {
      return null;
   }

   public final boolean isSimpleType() {
      return false;
   }

   public final boolean isComplexType() {
      return true;
   }

   public int getDerivationMethod() {
      return this.derivationMethod;
   }

   public XSType getBaseType() {
      return this.baseType.getType();
   }

   public void redefine(ComplexTypeImpl ct) {
      if (this.baseType instanceof DelayedRef) {
         ((DelayedRef)this.baseType).redefine(ct);
      } else {
         this.baseType = ct;
      }

      ct.redefinedBy = this;
      this.redefiningCount = (short)(ct.redefiningCount + 1);
   }

   public XSComplexType getRedefinedBy() {
      return this.redefinedBy;
   }

   public int getRedefinedCount() {
      int i = 0;

      for(ComplexTypeImpl ct = this.redefinedBy; ct != null; ct = ct.redefinedBy) {
         ++i;
      }

      return i;
   }

   public XSElementDecl getScope() {
      return this.scope;
   }

   public void setScope(XSElementDecl _scope) {
      this.scope = _scope;
   }

   public boolean isAbstract() {
      return this._abstract;
   }

   public void setWildcard(WildcardImpl wc) {
      this.localAttWildcard = wc;
   }

   public XSWildcard getAttributeWildcard() {
      WildcardImpl complete = this.localAttWildcard;
      Iterator itr = this.iterateAttGroups();

      WildcardImpl base;
      while(itr.hasNext()) {
         base = (WildcardImpl)((XSAttGroupDecl)itr.next()).getAttributeWildcard();
         if (base != null) {
            if (complete == null) {
               complete = base;
            } else {
               complete = complete.union(this.ownerDocument, base);
            }
         }
      }

      if (this.getDerivationMethod() == 2) {
         return complete;
      } else {
         base = null;
         XSType baseType = this.getBaseType();
         if (baseType.asComplexType() != null) {
            base = (WildcardImpl)baseType.asComplexType().getAttributeWildcard();
         }

         if (complete == null) {
            return base;
         } else if (base == null) {
            return complete;
         } else {
            return complete.union(this.ownerDocument, base);
         }
      }
   }

   public boolean isFinal(int derivationMethod) {
      return (this.finalValue & derivationMethod) != 0;
   }

   public boolean isSubstitutionProhibited(int method) {
      return (this.blockValue & method) != 0;
   }

   public void setContentType(Ref.ContentType v) {
      this.contentType = v;
   }

   public XSContentType getContentType() {
      return this.contentType.getContentType();
   }

   public void setExplicitContent(XSContentType v) {
      this.explicitContent = v;
   }

   public XSContentType getExplicitContent() {
      return this.explicitContent;
   }

   public boolean isMixed() {
      return this.mixed;
   }

   public XSAttributeUse getAttributeUse(String nsURI, String localName) {
      UName name = new UName(nsURI, localName);
      if (this.prohibitedAtts.contains(name)) {
         return null;
      } else {
         XSAttributeUse o = (XSAttributeUse)this.attributes.get(name);
         if (o == null) {
            for(Iterator itr = this.iterateAttGroups(); itr.hasNext() && o == null; o = ((XSAttGroupDecl)itr.next()).getAttributeUse(nsURI, localName)) {
            }
         }

         if (o == null) {
            XSType base = this.getBaseType();
            if (base.asComplexType() != null) {
               o = base.asComplexType().getAttributeUse(nsURI, localName);
            }
         }

         return o;
      }
   }

   public Iterator iterateAttributeUses() {
      XSComplexType baseType = this.getBaseType().asComplexType();
      return (Iterator)(baseType == null ? super.iterateAttributeUses() : new Iterators.Union(new Iterators.Filter(baseType.iterateAttributeUses()) {
         protected boolean matches(XSAttributeUse value) {
            XSAttributeDecl u = value.getDecl();
            UName n = new UName(u.getTargetNamespace(), u.getName());
            return !ComplexTypeImpl.this.prohibitedAtts.contains(n);
         }
      }, super.iterateAttributeUses()));
   }

   public Collection getAttributeUses() {
      XSComplexType baseType = this.getBaseType().asComplexType();
      if (baseType == null) {
         return super.getAttributeUses();
      } else {
         Map uses = new HashMap();
         Iterator var3 = baseType.getAttributeUses().iterator();

         XSAttributeUse a;
         while(var3.hasNext()) {
            a = (XSAttributeUse)var3.next();
            uses.put(new UName(a.getDecl()), a);
         }

         uses.keySet().removeAll(this.prohibitedAtts);
         var3 = super.getAttributeUses().iterator();

         while(var3.hasNext()) {
            a = (XSAttributeUse)var3.next();
            uses.put(new UName(a.getDecl()), a);
         }

         return uses.values();
      }
   }

   public XSType[] listSubstitutables() {
      return Util.listSubstitutables(this);
   }

   public void visit(XSVisitor visitor) {
      visitor.complexType(this);
   }

   public Object apply(XSFunction function) {
      return function.complexType(this);
   }

   public XSComplexType getType() {
      return this;
   }

   public List getSubtypes() {
      ArrayList subtypeList = new ArrayList();
      Iterator cTypes = this.getRoot().iterateComplexTypes();

      while(cTypes.hasNext()) {
         XSComplexType cType = (XSComplexType)cTypes.next();
         XSType base = cType.getBaseType();
         if (base != null && base.equals(this)) {
            subtypeList.add(cType);
         }
      }

      return subtypeList;
   }

   public List getElementDecls() {
      ArrayList declList = new ArrayList();
      XSSchemaSet schemaSet = this.getRoot();
      Iterator var3 = schemaSet.getSchemas().iterator();

      while(var3.hasNext()) {
         XSSchema sch = (XSSchema)var3.next();
         Iterator var5 = sch.getElementDecls().values().iterator();

         while(var5.hasNext()) {
            XSElementDecl decl = (XSElementDecl)var5.next();
            if (decl.getType().equals(this)) {
               declList.add(decl);
            }
         }
      }

      return declList;
   }
}
