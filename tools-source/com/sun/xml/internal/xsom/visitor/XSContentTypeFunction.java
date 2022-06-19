package com.sun.xml.internal.xsom.visitor;

import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSSimpleType;

public interface XSContentTypeFunction {
   Object simpleType(XSSimpleType var1);

   Object particle(XSParticle var1);

   Object empty(XSContentType var1);
}
