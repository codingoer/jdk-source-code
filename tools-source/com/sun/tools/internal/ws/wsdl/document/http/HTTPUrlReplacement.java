package com.sun.tools.internal.ws.wsdl.document.http;

import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class HTTPUrlReplacement extends ExtensionImpl {
   public HTTPUrlReplacement(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return HTTPConstants.QNAME_URL_REPLACEMENT;
   }

   public void validateThis() {
   }
}
