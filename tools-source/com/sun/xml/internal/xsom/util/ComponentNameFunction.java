package com.sun.xml.internal.xsom.util;

import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSElementDecl;
import com.sun.xml.internal.xsom.XSFacet;
import com.sun.xml.internal.xsom.XSIdentityConstraint;
import com.sun.xml.internal.xsom.XSModelGroup;
import com.sun.xml.internal.xsom.XSModelGroupDecl;
import com.sun.xml.internal.xsom.XSNotation;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSchema;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.XSXPath;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import java.util.Locale;

public class ComponentNameFunction implements XSFunction {
   private NameGetter nameGetter = new NameGetter((Locale)null);

   public String annotation(XSAnnotation ann) {
      return this.nameGetter.annotation(ann);
   }

   public String attGroupDecl(XSAttGroupDecl decl) {
      String name = decl.getName();
      if (name == null) {
         name = "";
      }

      return name + " " + this.nameGetter.attGroupDecl(decl);
   }

   public String attributeDecl(XSAttributeDecl decl) {
      String name = decl.getName();
      if (name == null) {
         name = "";
      }

      return name + " " + this.nameGetter.attributeDecl(decl);
   }

   public String attributeUse(XSAttributeUse use) {
      return this.nameGetter.attributeUse(use);
   }

   public String complexType(XSComplexType type) {
      String name = type.getName();
      if (name == null) {
         name = "anonymous";
      }

      return name + " " + this.nameGetter.complexType(type);
   }

   public String schema(XSSchema schema) {
      return this.nameGetter.schema(schema) + " \"" + schema.getTargetNamespace() + "\"";
   }

   public String facet(XSFacet facet) {
      String name = facet.getName();
      if (name == null) {
         name = "";
      }

      return name + " " + this.nameGetter.facet(facet);
   }

   public String notation(XSNotation notation) {
      String name = notation.getName();
      if (name == null) {
         name = "";
      }

      return name + " " + this.nameGetter.notation(notation);
   }

   public String simpleType(XSSimpleType simpleType) {
      String name = simpleType.getName();
      if (name == null) {
         name = "anonymous";
      }

      return name + " " + this.nameGetter.simpleType(simpleType);
   }

   public String particle(XSParticle particle) {
      return this.nameGetter.particle(particle);
   }

   public String empty(XSContentType empty) {
      return this.nameGetter.empty(empty);
   }

   public String wildcard(XSWildcard wc) {
      return this.nameGetter.wildcard(wc);
   }

   public String modelGroupDecl(XSModelGroupDecl decl) {
      String name = decl.getName();
      if (name == null) {
         name = "";
      }

      return name + " " + this.nameGetter.modelGroupDecl(decl);
   }

   public String modelGroup(XSModelGroup group) {
      return this.nameGetter.modelGroup(group);
   }

   public String elementDecl(XSElementDecl decl) {
      String name = decl.getName();
      if (name == null) {
         name = "";
      }

      return name + " " + this.nameGetter.elementDecl(decl);
   }

   public String identityConstraint(XSIdentityConstraint decl) {
      return decl.getName() + " " + this.nameGetter.identityConstraint(decl);
   }

   public String xpath(XSXPath xpath) {
      return this.nameGetter.xpath(xpath);
   }
}
