package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.model.CClassInfo;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.CReferencePropertyInfo;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSTerm;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.visitor.XSTermVisitor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class DefaultParticleBinder extends ParticleBinder {
   public void build(XSParticle p, Collection forcedProps) {
      Checker checker = this.checkCollision(p, forcedProps);
      if (checker.hasNameCollision()) {
         CReferencePropertyInfo prop = new CReferencePropertyInfo(this.getCurrentBean().getBaseClass() == null ? "Content" : "Rest", true, false, false, p, this.builder.getBindInfo(p).toCustomizationList(), p.getLocator(), false, false, false);
         RawTypeSetBuilder.build(p, false).addTo(prop);
         prop.javadoc = Messages.format("DefaultParticleBinder.FallbackJavadoc", checker.getCollisionInfo().toString());
         this.getCurrentBean().addProperty(prop);
      } else {
         (new Builder(checker.markedParticles)).particle(p);
      }

   }

   public boolean checkFallback(XSParticle p) {
      return this.checkCollision(p, Collections.emptyList()).hasNameCollision();
   }

   private Checker checkCollision(XSParticle p, Collection forcedProps) {
      Checker checker = new Checker(forcedProps);
      CClassInfo superClass = this.getCurrentBean().getBaseClass();
      if (superClass != null) {
         checker.readSuperClass(superClass);
      }

      checker.particle(p);
      return checker;
   }

   private final class Builder implements XSTermVisitor {
      private final Map markedParticles;
      private boolean insideOptionalParticle;

      Builder(Map markedParticles) {
         this.markedParticles = markedParticles;
      }

      private boolean marked(XSParticle p) {
         return this.markedParticles.containsKey(p);
      }

      private String getLabel(XSParticle p) {
         return (String)this.markedParticles.get(p);
      }

      public void particle(XSParticle p) {
         XSTerm t = p.getTerm();
         if (this.marked(p)) {
            BIProperty cust = BIProperty.getCustomization(p);
            CPropertyInfo prop = cust.createElementOrReferenceProperty(this.getLabel(p), false, p, RawTypeSetBuilder.build(p, this.insideOptionalParticle));
            DefaultParticleBinder.this.getCurrentBean().addProperty(prop);
         } else {
            assert !p.isRepeated();

            boolean oldIOP = this.insideOptionalParticle;
            this.insideOptionalParticle |= BigInteger.ZERO.equals(p.getMinOccurs());
            t.visit(this);
            this.insideOptionalParticle = oldIOP;
         }

      }

      public void elementDecl(XSElementDecl e) {
         assert false;

      }

      public void wildcard(XSWildcard wc) {
         assert false;

      }

      public void modelGroupDecl(XSModelGroupDecl decl) {
         this.modelGroup(decl.getModelGroup());
      }

      public void modelGroup(XSModelGroup mg) {
         boolean oldIOP = this.insideOptionalParticle;
         this.insideOptionalParticle |= mg.getCompositor() == XSModelGroup.CHOICE;
         XSParticle[] var3 = mg.getChildren();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            XSParticle p = var3[var5];
            this.particle(p);
         }

         this.insideOptionalParticle = oldIOP;
      }
   }

   private final class Checker implements XSTermVisitor {
      private CollisionInfo collisionInfo = null;
      private final NameCollisionChecker cchecker = new NameCollisionChecker();
      private final Collection forcedProps;
      private XSParticle outerParticle;
      public final Map markedParticles = new HashMap();
      private final Map labelCache = new Hashtable();

      Checker(Collection forcedProps) {
         this.forcedProps = forcedProps;
      }

      boolean hasNameCollision() {
         return this.collisionInfo != null;
      }

      CollisionInfo getCollisionInfo() {
         return this.collisionInfo;
      }

      public void particle(XSParticle p) {
         if (DefaultParticleBinder.this.getLocalPropCustomization(p) == null && DefaultParticleBinder.this.builder.getLocalDomCustomization(p) == null) {
            XSTerm t = p.getTerm();
            if (!p.isRepeated() || !t.isModelGroup() && !t.isModelGroupDecl()) {
               if (this.forcedProps.contains(p)) {
                  this.mark(p);
               } else {
                  this.outerParticle = p;
                  t.visit(this);
               }
            } else {
               this.mark(p);
            }
         } else {
            this.check(p);
            this.mark(p);
         }
      }

      public void elementDecl(XSElementDecl decl) {
         this.check(this.outerParticle);
         this.mark(this.outerParticle);
      }

      public void modelGroup(XSModelGroup mg) {
         if (mg.getCompositor() == XSModelGroup.Compositor.CHOICE && DefaultParticleBinder.this.builder.getGlobalBinding().isChoiceContentPropertyEnabled()) {
            this.mark(this.outerParticle);
         } else {
            XSParticle[] var2 = mg.getChildren();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               XSParticle child = var2[var4];
               this.particle(child);
            }

         }
      }

      public void modelGroupDecl(XSModelGroupDecl decl) {
         this.modelGroup(decl.getModelGroup());
      }

      public void wildcard(XSWildcard wc) {
         this.mark(this.outerParticle);
      }

      void readSuperClass(CClassInfo ci) {
         this.cchecker.readSuperClass(ci);
      }

      private void check(XSParticle p) {
         if (this.collisionInfo == null) {
            this.collisionInfo = this.cchecker.check(p);
         }

      }

      private void mark(XSParticle p) {
         this.markedParticles.put(p, this.computeLabel(p));
      }

      private String computeLabel(XSParticle p) {
         String label = (String)this.labelCache.get(p);
         if (label == null) {
            this.labelCache.put(p, label = DefaultParticleBinder.this.computeLabel(p));
         }

         return label;
      }

      private final class NameCollisionChecker {
         private final List particles;
         private final Map occupiedLabels;

         private NameCollisionChecker() {
            this.particles = new ArrayList();
            this.occupiedLabels = new HashMap();
         }

         CollisionInfo check(XSParticle p) {
            String label = Checker.this.computeLabel(p);
            if (this.occupiedLabels.containsKey(label)) {
               return new CollisionInfo(label, p.getLocator(), ((CPropertyInfo)this.occupiedLabels.get(label)).locator);
            } else {
               Iterator var3 = this.particles.iterator();

               XSParticle jp;
               do {
                  if (!var3.hasNext()) {
                     this.particles.add(p);
                     return null;
                  }

                  jp = (XSParticle)var3.next();
               } while(this.check(p, jp));

               return new CollisionInfo(label, p.getLocator(), jp.getLocator());
            }
         }

         private boolean check(XSParticle p1, XSParticle p2) {
            return !Checker.this.computeLabel(p1).equals(Checker.this.computeLabel(p2));
         }

         void readSuperClass(CClassInfo base) {
            while(base != null) {
               Iterator var2 = base.getProperties().iterator();

               while(var2.hasNext()) {
                  CPropertyInfo p = (CPropertyInfo)var2.next();
                  this.occupiedLabels.put(p.getName(true), p);
               }

               base = base.getBaseClass();
            }

         }

         // $FF: synthetic method
         NameCollisionChecker(Object x1) {
            this();
         }
      }
   }
}
