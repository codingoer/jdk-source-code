package com.sun.tools.internal.ws.api.wsdl;

import javax.xml.namespace.QName;

/** @deprecated */
public interface TWSDLExtensible {
   String getNameValue();

   String getNamespaceURI();

   QName getWSDLElementName();

   void addExtension(TWSDLExtension var1);

   Iterable extensions();

   TWSDLExtensible getParent();
}
