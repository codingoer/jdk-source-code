package com.sun.xml.internal.xsom.impl.util;

import com.sun.xml.internal.xsom.XSAnnotation;
import com.sun.xml.internal.xsom.XSAttGroupDecl;
import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XSComplexType;
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
import com.sun.xml.internal.xsom.XSType;
import com.sun.xml.internal.xsom.XSUnionSimpleType;
import com.sun.xml.internal.xsom.XSWildcard;
import com.sun.xml.internal.xsom.XSXPath;
import com.sun.xml.internal.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.internal.xsom.visitor.XSTermVisitor;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import com.sun.xml.internal.xsom.visitor.XSWildcardFunction;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Iterator;

public class SchemaWriter implements XSVisitor, XSSimpleTypeVisitor {
   private final Writer out;
   private int indent;
   private boolean hadError = false;
   private static final XSWildcardFunction WILDCARD_NS = new XSWildcardFunction() {
      public String any(XSWildcard.Any wc) {
         return "";
      }

      public String other(XSWildcard.Other wc) {
         return " namespace='##other'";
      }

      public String union(XSWildcard.Union wc) {
         StringBuilder buf = new StringBuilder(" namespace='");
         boolean first = true;

         String s;
         for(Iterator var4 = wc.getNamespaces().iterator(); var4.hasNext(); buf.append(s)) {
            s = (String)var4.next();
            if (first) {
               first = false;
            } else {
               buf.append(' ');
            }
         }

         return buf.append('\'').toString();
      }
   };

   public SchemaWriter(Writer _out) {
      this.out = _out;
   }

   private void println(String s) {
      try {
         for(int i = 0; i < this.indent; ++i) {
            this.out.write("  ");
         }

         this.out.write(s);
         this.out.write(10);
         this.out.flush();
      } catch (IOException var3) {
         this.hadError = true;
      }

   }

   private void println() {
      this.println("");
   }

   public boolean checkError() {
      try {
         this.out.flush();
      } catch (IOException var2) {
         this.hadError = true;
      }

      return this.hadError;
   }

   public void visit(XSSchemaSet s) {
      Iterator itr = s.iterateSchema();

      while(itr.hasNext()) {
         this.schema((XSSchema)itr.next());
         this.println();
      }

   }

   public void schema(XSSchema s) {
      if (!s.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
         this.println(MessageFormat.format("<schema targetNamespace=\"{0}\">", s.getTargetNamespace()));
         ++this.indent;
         Iterator itr = s.iterateAttGroupDecls();

         while(itr.hasNext()) {
            this.attGroupDecl((XSAttGroupDecl)itr.next());
         }

         itr = s.iterateAttributeDecls();

         while(itr.hasNext()) {
            this.attributeDecl((XSAttributeDecl)itr.next());
         }

         itr = s.iterateComplexTypes();

         while(itr.hasNext()) {
            this.complexType((XSComplexType)itr.next());
         }

         itr = s.iterateElementDecls();

         while(itr.hasNext()) {
            this.elementDecl((XSElementDecl)itr.next());
         }

         itr = s.iterateModelGroupDecls();

         while(itr.hasNext()) {
            this.modelGroupDecl((XSModelGroupDecl)itr.next());
         }

         itr = s.iterateSimpleTypes();

         while(itr.hasNext()) {
            this.simpleType((XSSimpleType)itr.next());
         }

         --this.indent;
         this.println("</schema>");
      }
   }

   public void attGroupDecl(XSAttGroupDecl decl) {
      this.println(MessageFormat.format("<attGroup name=\"{0}\">", decl.getName()));
      ++this.indent;
      Iterator itr = decl.iterateAttGroups();

      while(itr.hasNext()) {
         this.dumpRef((XSAttGroupDecl)itr.next());
      }

      itr = decl.iterateDeclaredAttributeUses();

      while(itr.hasNext()) {
         this.attributeUse((XSAttributeUse)itr.next());
      }

      --this.indent;
      this.println("</attGroup>");
   }

   public void dumpRef(XSAttGroupDecl decl) {
      this.println(MessageFormat.format("<attGroup ref=\"'{'{0}'}'{1}\"/>", decl.getTargetNamespace(), decl.getName()));
   }

   public void attributeUse(XSAttributeUse use) {
      XSAttributeDecl decl = use.getDecl();
      String additionalAtts = "";
      if (use.isRequired()) {
         additionalAtts = additionalAtts + " use=\"required\"";
      }

      if (use.getFixedValue() != null && use.getDecl().getFixedValue() == null) {
         additionalAtts = additionalAtts + " fixed=\"" + use.getFixedValue() + '"';
      }

      if (use.getDefaultValue() != null && use.getDecl().getDefaultValue() == null) {
         additionalAtts = additionalAtts + " default=\"" + use.getDefaultValue() + '"';
      }

      if (decl.isLocal()) {
         this.dump(decl, additionalAtts);
      } else {
         this.println(MessageFormat.format("<attribute ref=\"'{'{0}'}'{1}{2}\"/>", decl.getTargetNamespace(), decl.getName(), additionalAtts));
      }

   }

   public void attributeDecl(XSAttributeDecl decl) {
      this.dump(decl, "");
   }

   private void dump(XSAttributeDecl decl, String additionalAtts) {
      XSSimpleType type = decl.getType();
      this.println(MessageFormat.format("<attribute name=\"{0}\"{1}{2}{3}{4}{5}>", decl.getName(), additionalAtts, type.isLocal() ? "" : MessageFormat.format(" type=\"'{'{0}'}'{1}\"", type.getTargetNamespace(), type.getName()), decl.getFixedValue() == null ? "" : " fixed=\"" + decl.getFixedValue() + '"', decl.getDefaultValue() == null ? "" : " default=\"" + decl.getDefaultValue() + '"', type.isLocal() ? "" : " /"));
      if (type.isLocal()) {
         ++this.indent;
         this.simpleType(type);
         --this.indent;
         this.println("</attribute>");
      }

   }

   public void simpleType(XSSimpleType type) {
      this.println(MessageFormat.format("<simpleType{0}>", type.isLocal() ? "" : " name=\"" + type.getName() + '"'));
      ++this.indent;
      type.visit(this);
      --this.indent;
      this.println("</simpleType>");
   }

   public void listSimpleType(XSListSimpleType type) {
      XSSimpleType itemType = type.getItemType();
      if (itemType.isLocal()) {
         this.println("<list>");
         ++this.indent;
         this.simpleType(itemType);
         --this.indent;
         this.println("</list>");
      } else {
         this.println(MessageFormat.format("<list itemType=\"'{'{0}'}'{1}\" />", itemType.getTargetNamespace(), itemType.getName()));
      }

   }

   public void unionSimpleType(XSUnionSimpleType type) {
      int len = type.getMemberSize();
      StringBuffer ref = new StringBuffer();

      int i;
      XSSimpleType member;
      for(i = 0; i < len; ++i) {
         member = type.getMember(i);
         if (member.isGlobal()) {
            ref.append(MessageFormat.format(" '{'{0}'}'{1}", member.getTargetNamespace(), member.getName()));
         }
      }

      if (ref.length() == 0) {
         this.println("<union>");
      } else {
         this.println("<union memberTypes=\"" + ref + "\">");
      }

      ++this.indent;

      for(i = 0; i < len; ++i) {
         member = type.getMember(i);
         if (member.isLocal()) {
            this.simpleType(member);
         }
      }

      --this.indent;
      this.println("</union>");
   }

   public void restrictionSimpleType(XSRestrictionSimpleType type) {
      if (type.getBaseType() == null) {
         if (!type.getName().equals("anySimpleType")) {
            throw new InternalError();
         } else if (!"http://www.w3.org/2001/XMLSchema".equals(type.getTargetNamespace())) {
            throw new InternalError();
         }
      } else {
         XSSimpleType baseType = type.getSimpleBaseType();
         this.println(MessageFormat.format("<restriction{0}>", baseType.isLocal() ? "" : " base=\"{" + baseType.getTargetNamespace() + '}' + baseType.getName() + '"'));
         ++this.indent;
         if (baseType.isLocal()) {
            this.simpleType(baseType);
         }

         Iterator itr = type.iterateDeclaredFacets();

         while(itr.hasNext()) {
            this.facet((XSFacet)itr.next());
         }

         --this.indent;
         this.println("</restriction>");
      }
   }

   public void facet(XSFacet facet) {
      this.println(MessageFormat.format("<{0} value=\"{1}\"/>", facet.getName(), facet.getValue()));
   }

   public void notation(XSNotation notation) {
      this.println(MessageFormat.format("<notation name='\"0}\" public =\"{1}\" system=\"{2}\" />", notation.getName(), notation.getPublicId(), notation.getSystemId()));
   }

   public void complexType(XSComplexType type) {
      this.println(MessageFormat.format("<complexType{0}>", type.isLocal() ? "" : " name=\"" + type.getName() + '"'));
      ++this.indent;
      if (type.getContentType().asSimpleType() != null) {
         this.println("<simpleContent>");
         ++this.indent;
         XSType baseType = type.getBaseType();
         if (type.getDerivationMethod() == 2) {
            this.println(MessageFormat.format("<restriction base=\"<{0}>{1}\">", baseType.getTargetNamespace(), baseType.getName()));
            ++this.indent;
            this.dumpComplexTypeAttribute(type);
            --this.indent;
            this.println("</restriction>");
         } else {
            this.println(MessageFormat.format("<extension base=\"<{0}>{1}\">", baseType.getTargetNamespace(), baseType.getName()));
            if (type.isGlobal() && type.getTargetNamespace().equals(baseType.getTargetNamespace()) && type.getName().equals(baseType.getName())) {
               ++this.indent;
               this.println("<redefine>");
               ++this.indent;
               baseType.visit(this);
               --this.indent;
               this.println("</redefine>");
               --this.indent;
            }

            ++this.indent;
            this.dumpComplexTypeAttribute(type);
            --this.indent;
            this.println("</extension>");
         }

         --this.indent;
         this.println("</simpleContent>");
      } else {
         this.println("<complexContent>");
         ++this.indent;
         XSComplexType baseType = type.getBaseType().asComplexType();
         if (type.getDerivationMethod() == 2) {
            this.println(MessageFormat.format("<restriction base=\"'{'{0}'}'{1}\">", baseType.getTargetNamespace(), baseType.getName()));
            ++this.indent;
            type.getContentType().visit(this);
            this.dumpComplexTypeAttribute(type);
            --this.indent;
            this.println("</restriction>");
         } else {
            this.println(MessageFormat.format("<extension base=\"'{'{0}'}'{1}\">", baseType.getTargetNamespace(), baseType.getName()));
            if (type.isGlobal() && type.getTargetNamespace().equals(baseType.getTargetNamespace()) && type.getName().equals(baseType.getName())) {
               ++this.indent;
               this.println("<redefine>");
               ++this.indent;
               baseType.visit(this);
               --this.indent;
               this.println("</redefine>");
               --this.indent;
            }

            ++this.indent;
            type.getExplicitContent().visit(this);
            this.dumpComplexTypeAttribute(type);
            --this.indent;
            this.println("</extension>");
         }

         --this.indent;
         this.println("</complexContent>");
      }

      --this.indent;
      this.println("</complexType>");
   }

   private void dumpComplexTypeAttribute(XSComplexType type) {
      Iterator itr = type.iterateAttGroups();

      while(itr.hasNext()) {
         this.dumpRef((XSAttGroupDecl)itr.next());
      }

      itr = type.iterateDeclaredAttributeUses();

      while(itr.hasNext()) {
         this.attributeUse((XSAttributeUse)itr.next());
      }

      XSWildcard awc = type.getAttributeWildcard();
      if (awc != null) {
         this.wildcard("anyAttribute", awc, "");
      }

   }

   public void elementDecl(XSElementDecl decl) {
      this.elementDecl(decl, "");
   }

   private void elementDecl(XSElementDecl decl, String extraAtts) {
      XSType type = decl.getType();
      if (decl.getForm() != null) {
         extraAtts = extraAtts + " form=\"" + (decl.getForm() ? "qualified" : "unqualified") + "\"";
      }

      this.println(MessageFormat.format("<element name=\"{0}\"{1}{2}{3}>", decl.getName(), type.isLocal() ? "" : " type=\"{" + type.getTargetNamespace() + '}' + type.getName() + '"', extraAtts, type.isLocal() ? "" : "/"));
      if (type.isLocal()) {
         ++this.indent;
         if (type.isLocal()) {
            type.visit(this);
         }

         --this.indent;
         this.println("</element>");
      }

   }

   public void modelGroupDecl(XSModelGroupDecl decl) {
      this.println(MessageFormat.format("<group name=\"{0}\">", decl.getName()));
      ++this.indent;
      this.modelGroup(decl.getModelGroup());
      --this.indent;
      this.println("</group>");
   }

   public void modelGroup(XSModelGroup group) {
      this.modelGroup(group, "");
   }

   private void modelGroup(XSModelGroup group, String extraAtts) {
      this.println(MessageFormat.format("<{0}{1}>", group.getCompositor(), extraAtts));
      ++this.indent;
      int len = group.getSize();

      for(int i = 0; i < len; ++i) {
         this.particle(group.getChild(i));
      }

      --this.indent;
      this.println(MessageFormat.format("</{0}>", group.getCompositor()));
   }

   public void particle(XSParticle part) {
      StringBuilder buf = new StringBuilder();
      BigInteger i = part.getMaxOccurs();
      if (i.equals(BigInteger.valueOf(-1L))) {
         buf.append(" maxOccurs=\"unbounded\"");
      } else if (!i.equals(BigInteger.ONE)) {
         buf.append(" maxOccurs=\"").append(i).append('"');
      }

      i = part.getMinOccurs();
      if (!i.equals(BigInteger.ONE)) {
         buf.append(" minOccurs=\"").append(i).append('"');
      }

      final String extraAtts = buf.toString();
      part.getTerm().visit(new XSTermVisitor() {
         public void elementDecl(XSElementDecl decl) {
            if (decl.isLocal()) {
               SchemaWriter.this.elementDecl(decl, extraAtts);
            } else {
               SchemaWriter.this.println(MessageFormat.format("<element ref=\"'{'{0}'}'{1}\"{2}/>", decl.getTargetNamespace(), decl.getName(), extraAtts));
            }

         }

         public void modelGroupDecl(XSModelGroupDecl decl) {
            SchemaWriter.this.println(MessageFormat.format("<group ref=\"'{'{0}'}'{1}\"{2}/>", decl.getTargetNamespace(), decl.getName(), extraAtts));
         }

         public void modelGroup(XSModelGroup group) {
            SchemaWriter.this.modelGroup(group, extraAtts);
         }

         public void wildcard(XSWildcard wc) {
            SchemaWriter.this.wildcard("any", wc, extraAtts);
         }
      });
   }

   public void wildcard(XSWildcard wc) {
      this.wildcard("any", wc, "");
   }

   private void wildcard(String tagName, XSWildcard wc, String extraAtts) {
      String proessContents;
      switch (wc.getMode()) {
         case 1:
            proessContents = " processContents='lax'";
            break;
         case 2:
            proessContents = "";
            break;
         case 3:
            proessContents = " processContents='skip'";
            break;
         default:
            throw new AssertionError();
      }

      this.println(MessageFormat.format("<{0}{1}{2}{3}/>", tagName, proessContents, wc.apply(WILDCARD_NS), extraAtts));
   }

   public void annotation(XSAnnotation ann) {
   }

   public void identityConstraint(XSIdentityConstraint decl) {
   }

   public void xpath(XSXPath xp) {
   }

   public void empty(XSContentType t) {
   }
}
