package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.tools.internal.xjc.reader.xmlschema.WildcardNameClassBuilder;
import com.sun.xml.internal.rngom.nc.ChoiceNameClass;
import com.sun.xml.internal.rngom.nc.NameClass;
import com.sun.xml.internal.rngom.nc.SimpleNameClass;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSDeclaration;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.visitor.XSTermFunction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

abstract class AbstractExtendedComplexTypeBuilder extends CTBuilder {
   protected final Map characteristicNameClasses = new HashMap();
   protected final XSTermFunction contentModelNameClassBuilder = new XSTermFunction() {
      public NameClass wildcard(XSWildcard wc) {
         return WildcardNameClassBuilder.build(wc);
      }

      public NameClass modelGroupDecl(XSModelGroupDecl decl) {
         return this.modelGroup(decl.getModelGroup());
      }

      public NameClass modelGroup(XSModelGroup group) {
         NameClass nc = NameClass.NULL;

         for(int i = 0; i < group.getSize(); ++i) {
            nc = new ChoiceNameClass((NameClass)nc, (NameClass)group.getChild(i).getTerm().apply(this));
         }

         return (NameClass)nc;
      }

      public NameClass elementDecl(XSElementDecl decl) {
         return AbstractExtendedComplexTypeBuilder.this.getNameClass((XSDeclaration)decl);
      }
   };

   protected boolean checkCollision(NameClass anc, NameClass enc, XSComplexType type) {
      NameClass[] chnc = (NameClass[])this.characteristicNameClasses.get(type);
      if (chnc == null) {
         chnc = new NameClass[]{this.getNameClass(type.getContentType()), null};
         NameClass nc = NameClass.NULL;

         for(Iterator itr = type.iterateAttributeUses(); itr.hasNext(); anc = new ChoiceNameClass((NameClass)anc, this.getNameClass((XSDeclaration)((XSAttributeUse)itr.next()).getDecl()))) {
         }

         XSWildcard wc = type.getAttributeWildcard();
         if (wc != null) {
            nc = new ChoiceNameClass((NameClass)nc, WildcardNameClassBuilder.build(wc));
         }

         chnc[1] = (NameClass)nc;
         this.characteristicNameClasses.put(type, chnc);
      }

      return chnc[0].hasOverlapWith(enc) || chnc[1].hasOverlapWith((NameClass)anc);
   }

   protected XSComplexType getLastRestrictedType(XSComplexType t) {
      if (t.getBaseType() == this.schemas.getAnyType()) {
         return null;
      } else if (t.getDerivationMethod() == 2) {
         return t;
      } else {
         XSComplexType baseType = t.getBaseType().asComplexType();
         return baseType != null ? this.getLastRestrictedType(baseType) : null;
      }
   }

   protected boolean checkIfExtensionSafe(XSComplexType baseType, XSComplexType thisType) {
      XSComplexType lastType = this.getLastRestrictedType(baseType);
      if (lastType == null) {
         return true;
      } else {
         NameClass anc = NameClass.NULL;

         for(Iterator itr = thisType.iterateDeclaredAttributeUses(); itr.hasNext(); anc = new ChoiceNameClass((NameClass)anc, this.getNameClass((XSDeclaration)((XSAttributeUse)itr.next()).getDecl()))) {
         }

         for(NameClass enc = this.getNameClass(thisType.getExplicitContent()); lastType != lastType.getBaseType(); lastType = lastType.getBaseType().asComplexType()) {
            if (this.checkCollision((NameClass)anc, enc, lastType)) {
               return false;
            }

            if (lastType.getBaseType().isSimpleType()) {
               return true;
            }
         }

         return true;
      }
   }

   private NameClass getNameClass(XSContentType t) {
      if (t == null) {
         return NameClass.NULL;
      } else {
         XSParticle p = t.asParticle();
         return p == null ? NameClass.NULL : (NameClass)p.getTerm().apply(this.contentModelNameClassBuilder);
      }
   }

   private NameClass getNameClass(XSDeclaration decl) {
      return new SimpleNameClass(new QName(decl.getTargetNamespace(), decl.getName()));
   }
}
