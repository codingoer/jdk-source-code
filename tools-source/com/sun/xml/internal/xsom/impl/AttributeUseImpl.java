package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSAttributeDecl;
import com.sun.xml.internal.xsom.XSAttributeUse;
import com.sun.xml.internal.xsom.XmlString;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

public class AttributeUseImpl extends ComponentImpl implements XSAttributeUse {
   private final Ref.Attribute att;
   private final XmlString defaultValue;
   private final XmlString fixedValue;
   private final boolean required;

   public AttributeUseImpl(SchemaDocumentImpl owner, AnnotationImpl ann, Locator loc, ForeignAttributesImpl fa, Ref.Attribute _decl, XmlString def, XmlString fixed, boolean req) {
      super(owner, ann, loc, fa);
      this.att = _decl;
      this.defaultValue = def;
      this.fixedValue = fixed;
      this.required = req;
   }

   public XSAttributeDecl getDecl() {
      return this.att.getAttribute();
   }

   public XmlString getDefaultValue() {
      return this.defaultValue != null ? this.defaultValue : this.getDecl().getDefaultValue();
   }

   public XmlString getFixedValue() {
      return this.fixedValue != null ? this.fixedValue : this.getDecl().getFixedValue();
   }

   public boolean isRequired() {
      return this.required;
   }

   public Object apply(XSFunction f) {
      return f.attributeUse(this);
   }

   public void visit(XSVisitor v) {
      v.attributeUse(this);
   }
}
