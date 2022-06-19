package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSTerm;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.XmlString;
import com.sun.xml.internal.xsom.impl.parser.PatcherManager;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSTermFunction;
import com.sun.xml.internal.xsom.visitor.XSTermFunctionWithParam;
import com.sun.xml.internal.xsom.visitor.XSTermVisitor;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.xml.sax.Locator;

public class ElementDecl extends DeclarationImpl implements XSElementDecl, Ref.Term {
   private XmlString defaultValue;
   private XmlString fixedValue;
   private boolean nillable;
   private boolean _abstract;
   private Ref.Type type;
   private Ref.Element substHead;
   private int substDisallowed;
   private int substExcluded;
   private final List idConstraints;
   private Boolean form;
   private Set substitutables = null;
   private Set substitutablesView = null;

   public ElementDecl(PatcherManager reader, SchemaDocumentImpl owner, AnnotationImpl _annon, Locator _loc, ForeignAttributesImpl fa, String _tns, String _name, boolean _anonymous, XmlString _defv, XmlString _fixedv, boolean _nillable, boolean _abstract, Boolean _form, Ref.Type _type, Ref.Element _substHead, int _substDisallowed, int _substExcluded, List idConstraints) {
      super(owner, _annon, _loc, fa, _tns, _name, _anonymous);
      this.defaultValue = _defv;
      this.fixedValue = _fixedv;
      this.nillable = _nillable;
      this._abstract = _abstract;
      this.form = _form;
      this.type = _type;
      this.substHead = _substHead;
      this.substDisallowed = _substDisallowed;
      this.substExcluded = _substExcluded;
      this.idConstraints = Collections.unmodifiableList(idConstraints);
      Iterator var19 = idConstraints.iterator();

      while(var19.hasNext()) {
         IdentityConstraintImpl idc = (IdentityConstraintImpl)var19.next();
         idc.setParent(this);
      }

      if (this.type == null) {
         throw new IllegalArgumentException();
      }
   }

   public XmlString getDefaultValue() {
      return this.defaultValue;
   }

   public XmlString getFixedValue() {
      return this.fixedValue;
   }

   public boolean isNillable() {
      return this.nillable;
   }

   public boolean isAbstract() {
      return this._abstract;
   }

   public XSType getType() {
      return this.type.getType();
   }

   public XSElementDecl getSubstAffiliation() {
      return this.substHead == null ? null : this.substHead.get();
   }

   public boolean isSubstitutionDisallowed(int method) {
      return (this.substDisallowed & method) != 0;
   }

   public boolean isSubstitutionExcluded(int method) {
      return (this.substExcluded & method) != 0;
   }

   public List getIdentityConstraints() {
      return this.idConstraints;
   }

   public Boolean getForm() {
      return this.form;
   }

   /** @deprecated */
   public XSElementDecl[] listSubstitutables() {
      Set s = this.getSubstitutables();
      return (XSElementDecl[])s.toArray(new XSElementDecl[s.size()]);
   }

   public Set getSubstitutables() {
      if (this.substitutables == null) {
         this.substitutables = this.substitutablesView = Collections.singleton(this);
      }

      return this.substitutablesView;
   }

   protected void addSubstitutable(ElementDecl decl) {
      if (this.substitutables == null) {
         this.substitutables = new HashSet();
         this.substitutables.add(this);
         this.substitutablesView = Collections.unmodifiableSet(this.substitutables);
      }

      this.substitutables.add(decl);
   }

   public void updateSubstitutabilityMap() {
      ElementDecl parent = this;
      XSType type = this.getType();
      boolean rused = false;
      boolean eused = false;

      while(true) {
         boolean rd;
         boolean ed;
         do {
            do {
               do {
                  do {
                     do {
                        if ((parent = (ElementDecl)parent.getSubstAffiliation()) == null) {
                           return;
                        }
                     } while(parent.isSubstitutionDisallowed(4));

                     rd = parent.isSubstitutionDisallowed(2);
                     ed = parent.isSubstitutionDisallowed(1);
                  } while(rd && rused);
               } while(ed && eused);

               XSType parentType = parent.getType();

               while(type != parentType) {
                  if (type.getDerivationMethod() == 2) {
                     rused = true;
                  } else {
                     eused = true;
                  }

                  type = type.getBaseType();
                  if (type == null) {
                     break;
                  }

                  if (type.isComplexType()) {
                     rd |= type.asComplexType().isSubstitutionProhibited(2);
                     ed |= type.asComplexType().isSubstitutionProhibited(1);
                  }

                  if (this.getRoot().getAnyType().equals(type)) {
                     break;
                  }
               }
            } while(rd && rused);
         } while(ed && eused);

         parent.addSubstitutable(this);
      }
   }

   public boolean canBeSubstitutedBy(XSElementDecl e) {
      return this.getSubstitutables().contains(e);
   }

   public boolean isWildcard() {
      return false;
   }

   public boolean isModelGroupDecl() {
      return false;
   }

   public boolean isModelGroup() {
      return false;
   }

   public boolean isElementDecl() {
      return true;
   }

   public XSWildcard asWildcard() {
      return null;
   }

   public XSModelGroupDecl asModelGroupDecl() {
      return null;
   }

   public XSModelGroup asModelGroup() {
      return null;
   }

   public XSElementDecl asElementDecl() {
      return this;
   }

   public void visit(XSVisitor visitor) {
      visitor.elementDecl(this);
   }

   public void visit(XSTermVisitor visitor) {
      visitor.elementDecl(this);
   }

   public Object apply(XSTermFunction function) {
      return function.elementDecl(this);
   }

   public Object apply(XSTermFunctionWithParam function, Object param) {
      return function.elementDecl(this, param);
   }

   public Object apply(XSFunction function) {
      return function.elementDecl(this);
   }

   public XSTerm getTerm() {
      return this;
   }
}
