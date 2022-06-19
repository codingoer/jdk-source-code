package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.ct.ComplexTypeFieldBuilder;
import com.sun.xml.internal.xsom.XSAttContainer;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSWildcard;
import java.util.Iterator;

public final class BindGreen extends ColorBinder {
   private final ComplexTypeFieldBuilder ctBuilder = (ComplexTypeFieldBuilder)Ring.get(ComplexTypeFieldBuilder.class);

   public void attGroupDecl(XSAttGroupDecl ag) {
      this.attContainer(ag);
   }

   public void attContainer(XSAttContainer cont) {
      Iterator itr = cont.iterateDeclaredAttributeUses();

      while(itr.hasNext()) {
         this.builder.ying((XSAttributeUse)itr.next(), cont);
      }

      itr = cont.iterateAttGroups();

      while(itr.hasNext()) {
         this.builder.ying((XSAttGroupDecl)itr.next(), cont);
      }

      XSWildcard w = cont.getAttributeWildcard();
      if (w != null) {
         this.builder.ying(w, cont);
      }

   }

   public void complexType(XSComplexType ct) {
      this.ctBuilder.build(ct);
   }

   public void attributeDecl(XSAttributeDecl xsAttributeDecl) {
      throw new UnsupportedOperationException();
   }

   public void wildcard(XSWildcard xsWildcard) {
      throw new UnsupportedOperationException();
   }

   public void modelGroupDecl(XSModelGroupDecl xsModelGroupDecl) {
      throw new UnsupportedOperationException();
   }

   public void modelGroup(XSModelGroup xsModelGroup) {
      throw new UnsupportedOperationException();
   }

   public void elementDecl(XSElementDecl xsElementDecl) {
      throw new UnsupportedOperationException();
   }

   public void particle(XSParticle xsParticle) {
      throw new UnsupportedOperationException();
   }

   public void empty(XSContentType xsContentType) {
      throw new UnsupportedOperationException();
   }

   public void simpleType(XSSimpleType xsSimpleType) {
      throw new IllegalStateException();
   }

   public void attributeUse(XSAttributeUse use) {
      throw new IllegalStateException();
   }
}
