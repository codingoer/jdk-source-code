package com.sun.xml.internal.xsom;

import com.sun.xml.internal.xsom.visitor.XSContentTypeFunction;
import com.sun.xml.internal.xsom.visitor.XSContentTypeVisitor;

public interface XSContentType extends XSComponent {
   XSSimpleType asSimpleType();

   XSParticle asParticle();

   XSContentType asEmpty();

   Object apply(XSContentTypeFunction var1);

   void visit(XSContentTypeVisitor var1);
}
