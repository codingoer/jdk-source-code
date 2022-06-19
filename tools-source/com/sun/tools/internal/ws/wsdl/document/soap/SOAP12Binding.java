package com.sun.tools.internal.ws.wsdl.document.soap;

import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class SOAP12Binding extends SOAPBinding {
   public SOAP12Binding(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return SOAP12Constants.QNAME_BINDING;
   }
}
