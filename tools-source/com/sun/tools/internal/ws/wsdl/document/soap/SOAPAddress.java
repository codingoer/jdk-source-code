package com.sun.tools.internal.ws.wsdl.document.soap;

import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class SOAPAddress extends ExtensionImpl {
   private String _location;

   public SOAPAddress(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return SOAPConstants.QNAME_ADDRESS;
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
