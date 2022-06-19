package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.xml.internal.xsom.XSElementDecl;
import javax.xml.namespace.QName;

final class GElementImpl extends GElement {
   public final QName tagName;
   public final XSElementDecl decl;

   public GElementImpl(QName tagName, XSElementDecl decl) {
      this.tagName = tagName;
      this.decl = decl;
   }

   public String toString() {
      return this.tagName.toString();
   }

   String getPropertyNameSeed() {
      return this.tagName.getLocalPart();
   }
}
