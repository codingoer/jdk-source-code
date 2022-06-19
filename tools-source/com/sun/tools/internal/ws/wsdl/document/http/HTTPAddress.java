package com.sun.tools.internal.ws.wsdl.document.http;

import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class HTTPAddress extends ExtensionImpl {
   private String _location;

   public HTTPAddress(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return HTTPConstants.QNAME_ADDRESS;
   }

   public String getLocation() {
      return this._location;
   }

   public void setLocation(String s) {
      this._location = s;
   }

   public void validateThis() {
      if (this._location == null) {
         this.failValidation("validation.missingRequiredAttribute", "location");
      }

   }
}
