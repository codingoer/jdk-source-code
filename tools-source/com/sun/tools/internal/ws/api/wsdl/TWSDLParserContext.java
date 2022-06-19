package com.sun.tools.internal.ws.api.wsdl;

import org.w3c.dom.Element;
import org.xml.sax.Locator;

/** @deprecated */
public interface TWSDLParserContext {
   void push();

   void pop();

   String getNamespaceURI(String var1);

   Iterable getPrefixes();

   String getDefaultNamespaceURI();

   void registerNamespaces(Element var1);

   Locator getLocation(Element var1);
}
