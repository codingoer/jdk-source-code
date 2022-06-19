package com.sun.tools.internal.ws.wsdl.document.http;

import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class HTTPBinding extends ExtensionImpl {
   private String _verb;

   public HTTPBinding(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return HTTPConstants.QNAME_BINDING;
   }

   public String getVerb() {
      return this._verb;
   }

   public void setVerb(String s) {
      this._verb = s;
   }

   public void validateThis() {
      if (this._verb == null) {
         this.failValidation("validation.missingRequiredAttribute", "verb");
      }

   }
}
