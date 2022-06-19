package com.sun.tools.internal.xjc.reader.xmlschema;

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
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.XSXPath;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

final class RefererFinder implements XSVisitor {
   private final Set visited = new HashSet();
   private final Map referers = new HashMap();

   public Set getReferer(XSComponent src) {
      Set r = (Set)this.referers.get(src);
      return r == null ? Collections.emptySet() : r;
   }

   public void schemaSet(XSSchemaSet xss) {
      if (this.visited.add(xss)) {
         Iterator var2 = xss.getSchemas().iterator();

         while(var2.hasNext()) {
            XSSchema xs = (XSSchema)var2.next();
            this.schema(xs);
         }

      }
   }

   public void schema(XSSchema xs) {
      if (this.visited.add(xs)) {
         Iterator var2 = xs.getComplexTypes().values().iterator();

         while(var2.hasNext()) {
            XSComplexType ct = (XSComplexType)var2.next();
            this.complexType(ct);
         }

         var2 = xs.getElementDecls().values().iterator();

         while(var2.hasNext()) {
            XSElementDecl e = (XSElementDecl)var2.next();
            this.elementDecl(e);
         }

      }
   }

   public void elementDecl(XSElementDecl e) {
      if (this.visited.add(e)) {
         this.refer(e, e.getType());
         e.getType().visit(this);
      }
   }

   public void complexType(XSComplexType ct) {
      if (this.visited.add(ct)) {
         this.refer(ct, ct.getBaseType());
         ct.getBaseType().visit(this);
         ct.getContentType().visit(this);
      }
   }

   public void modelGroupDecl(XSModelGroupDecl decl) {
      if (this.visited.add(decl)) {
         this.modelGroup(decl.getModelGroup());
      }
   }

   public void modelGroup(XSModelGroup group) {
      if (this.visited.add(group)) {
         XSParticle[] var2 = group.getChildren();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            XSParticle p = var2[var4];
            this.particle(p);
         }

      }
   }

   public void particle(XSParticle particle) {
      particle.getTerm().visit(this);
   }

   public void simpleType(XSSimpleType simpleType) {
   }

   public void annotation(XSAnnotation ann) {
   }

   public void attGroupDecl(XSAttGroupDecl decl) {
   }

   public void attributeDecl(XSAttributeDecl decl) {
   }

   public void attributeUse(XSAttributeUse use) {
   }

   public void facet(XSFacet facet) {
   }

   public void notation(XSNotation notation) {
   }

   public void identityConstraint(XSIdentityConstraint decl) {
   }

   public void xpath(XSXPath xp) {
   }

   public void wildcard(XSWildcard wc) {
   }

   public void empty(XSContentType empty) {
   }

   private void refer(XSComponent source, XSType target) {
      Set r = (Set)this.referers.get(target);
      if (r == null) {
         r = new HashSet();
         this.referers.put(target, r);
      }

      ((Set)r).add(source);
   }
}
