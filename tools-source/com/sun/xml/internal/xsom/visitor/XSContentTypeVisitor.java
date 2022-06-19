package com.sun.xml.internal.xsom.visitor;

import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSimpleType;

public interface XSContentTypeVisitor {
   void simpleType(XSSimpleType var1);

   void particle(XSParticle var1);

   void empty(XSContentType var1);
}
