package com.sun.tools.internal.xjc.util;

import java.io.IOException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ForkEntityResolver implements EntityResolver {
   private final EntityResolver lhs;
   private final EntityResolver rhs;

   public ForkEntityResolver(EntityResolver lhs, EntityResolver rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
   }

   public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
      InputSource is = this.lhs.resolveEntity(publicId, systemId);
      return is != null ? is : this.rhs.resolveEntity(publicId, systemId);
   }
}
