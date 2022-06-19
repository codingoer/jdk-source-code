package com.sun.xml.internal.xsom.impl.util;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class ResourceEntityResolver implements EntityResolver {
   private final Class base;

   public ResourceEntityResolver(Class _base) {
      this.base = _base;
   }

   public InputSource resolveEntity(String publicId, String systemId) {
      return new InputSource(this.base.getResourceAsStream(systemId));
   }
}
