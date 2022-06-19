package com.sun.xml.internal.xsom;

import java.math.BigInteger;

public interface XSParticle extends XSContentType {
   int UNBOUNDED = -1;

   BigInteger getMinOccurs();

   BigInteger getMaxOccurs();

   boolean isRepeated();

   XSTerm getTerm();
}
