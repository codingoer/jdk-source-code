package com.sun.tools.internal.ws.wsdl.framework;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import org.xml.sax.Locator;

public abstract class ExtensionImpl extends Entity implements TWSDLExtension {
   private TWSDLExtensible _parent;

   public ExtensionImpl(Locator locator) {
      super(locator);
   }

   public TWSDLExtensible getParent() {
      return this._parent;
   }

   public void setParent(TWSDLExtensible parent) {
      this._parent = parent;
   }

   public void accept(ExtensionVisitor visitor) throws Exception {
      visitor.preVisit(this);
      visitor.postVisit(this);
   }
}
