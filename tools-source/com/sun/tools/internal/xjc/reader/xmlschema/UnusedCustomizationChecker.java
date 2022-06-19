package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.reader.Ring;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttContainer;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSListSimpleType;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSRestrictionSimpleType;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSUnionSimpleType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.XSXPath;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class UnusedCustomizationChecker extends BindingComponent implements XSVisitor, XSSimpleTypeVisitor {
   private final BGMBuilder builder = (BGMBuilder)Ring.get(BGMBuilder.class);
   private final SimpleTypeBuilder stb = (SimpleTypeBuilder)Ring.get(SimpleTypeBuilder.class);
   private final Set visitedComponents = new HashSet();

   void run() {
      Iterator var1 = ((XSSchemaSet)Ring.get(XSSchemaSet.class)).getSchemas().iterator();

      while(var1.hasNext()) {
         XSSchema s = (XSSchema)var1.next();
         this.schema(s);
         this.run(s.getAttGroupDecls());
         this.run(s.getAttributeDecls());
         this.run(s.getComplexTypes());
         this.run(s.getElementDecls());
         this.run(s.getModelGroupDecls());
         this.run(s.getNotations());
         this.run(s.getSimpleTypes());
      }

   }

   private void run(Map col) {
      Iterator var2 = col.values().iterator();

      while(var2.hasNext()) {
         XSComponent c = (XSComponent)var2.next();
         c.visit(this);
      }

   }

   private boolean check(XSComponent c) {
      if (!this.visitedComponents.add(c)) {
         return false;
      } else {
         BIDeclaration[] var2 = this.builder.getBindInfo(c).getDecls();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            BIDeclaration decl = var2[var4];
            this.check(decl, c);
         }

         this.checkExpectedContentTypes(c);
         return true;
      }
   }

   private void checkExpectedContentTypes(XSComponent c) {
      if (c.getForeignAttribute("http://www.w3.org/2005/05/xmlmime", "expectedContentTypes") != null) {
         if (!(c instanceof XSParticle)) {
            if (!this.stb.isAcknowledgedXmimeContentTypes(c)) {
               this.getErrorReporter().warning(c.getLocator(), "UnusedCustomizationChecker.WarnUnusedExpectedContentTypes");
            }

         }
      }
   }

   private void check(BIDeclaration decl, XSComponent c) {
      if (!decl.isAcknowledged()) {
         this.getErrorReporter().error(decl.getLocation(), "UnusedCustomizationChecker.UnacknolwedgedCustomization", decl.getName().getLocalPart());
         this.getErrorReporter().error(c.getLocator(), "UnusedCustomizationChecker.UnacknolwedgedCustomization.Relevant");
         decl.markAsAcknowledged();
      }

      Iterator var3 = decl.getChildren().iterator();

      while(var3.hasNext()) {
         BIDeclaration d = (BIDeclaration)var3.next();
         this.check(d, c);
      }

   }

   public void annotation(XSAnnotation ann) {
   }

   public void attGroupDecl(XSAttGroupDecl decl) {
      if (this.check(decl)) {
         this.attContainer(decl);
      }

   }

   public void attributeDecl(XSAttributeDecl decl) {
      if (this.check(decl)) {
         decl.getType().visit(this);
      }

   }

   public void attributeUse(XSAttributeUse use) {
      if (this.check(use)) {
         use.getDecl().visit(this);
      }

   }

   public void complexType(XSComplexType type) {
      if (this.check(type)) {
         type.getContentType().visit(this);
         this.attContainer(type);
      }

   }

   private void attContainer(XSAttContainer cont) {
      Iterator itr = cont.iterateAttGroups();

      while(itr.hasNext()) {
         ((XSAttGroupDecl)itr.next()).visit(this);
      }

      itr = cont.iterateDeclaredAttributeUses();

      while(itr.hasNext()) {
         ((XSAttributeUse)itr.next()).visit(this);
      }

      XSWildcard wc = cont.getAttributeWildcard();
      if (wc != null) {
         wc.visit(this);
      }

   }

   public void schema(XSSchema schema) {
      this.check(schema);
   }

   public void facet(XSFacet facet) {
      this.check(facet);
   }

   public void notation(XSNotation notation) {
      this.check(notation);
   }

   public void wildcard(XSWildcard wc) {
      this.check(wc);
   }

   public void modelGroupDecl(XSModelGroupDecl decl) {
      if (this.check(decl)) {
         decl.getModelGroup().visit(this);
      }

   }

   public void modelGroup(XSModelGroup group) {
      if (this.check(group)) {
         for(int i = 0; i < group.getSize(); ++i) {
            group.getChild(i).visit(this);
         }
      }

   }

   public void elementDecl(XSElementDecl decl) {
      if (this.check(decl)) {
         decl.getType().visit(this);
         Iterator var2 = decl.getIdentityConstraints().iterator();

         while(var2.hasNext()) {
            XSIdentityConstraint id = (XSIdentityConstraint)var2.next();
            id.visit(this);
         }
      }

   }

   public void simpleType(XSSimpleType simpleType) {
      if (this.check(simpleType)) {
         simpleType.visit(this);
      }

   }

   public void particle(XSParticle particle) {
      if (this.check(particle)) {
         particle.getTerm().visit(this);
      }

   }

   public void empty(XSContentType empty) {
      this.check(empty);
   }

   public void listSimpleType(XSListSimpleType type) {
      if (this.check(type)) {
         type.getItemType().visit(this);
      }

   }

   public void restrictionSimpleType(XSRestrictionSimpleType type) {
      if (this.check(type)) {
         type.getBaseType().visit(this);
      }

   }

   public void unionSimpleType(XSUnionSimpleType type) {
      if (this.check(type)) {
         for(int i = 0; i < type.getMemberSize(); ++i) {
            type.getMember(i).visit(this);
         }
      }

   }

   public void identityConstraint(XSIdentityConstraint id) {
      if (this.check(id)) {
         id.getSelector().visit(this);
         Iterator var2 = id.getFields().iterator();

         while(var2.hasNext()) {
            XSXPath xp = (XSXPath)var2.next();
            xp.visit(this);
         }
      }

   }

   public void xpath(XSXPath xp) {
      this.check(xp);
   }
}
