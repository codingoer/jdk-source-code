package com.sun.tools.internal.ws.wsdl.framework;

import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public interface Elemental {
   QName getElementName();

   Locator getLocator();
}
