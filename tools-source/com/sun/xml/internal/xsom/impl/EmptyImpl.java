package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.internal.xsom.visitor.XSContentTypeFunction;
import com.sun.xml.internal.xsom.visitor.XSContentTypeVisitor;
import com.sun.xml.internal.xsom.visitor.XSFunction;
import com.sun.xml.internal.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

public class EmptyImpl extends ComponentImpl implements ContentTypeImpl {
   public EmptyImpl() {
      super((SchemaDocumentImpl)null, (AnnotationImpl)null, (Locator)null, (ForeignAttributesImpl)null);
   }

   public XSSimpleType asSimpleType() {
      return null;
   }

   public XSParticle asParticle() {
      return null;
   }

   public XSContentType asEmpty() {
      return this;
   }

   public Object apply(XSContentTypeFunction function) {
      return function.empty(this);
   }

   public Object apply(XSFunction function) {
      return function.empty(this);
   }

   public void visit(XSVisitor visitor) {
      visitor.empty(this);
   }

   public void visit(XSContentTypeVisitor visitor) {
      visitor.empty(this);
   }

   public XSContentType getContentType() {
      return this;
   }
}
