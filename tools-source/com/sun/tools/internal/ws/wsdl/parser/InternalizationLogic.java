package com.sun.tools.internal.ws.wsdl.parser;

import org.w3c.dom.Element;
import org.xml.sax.helpers.XMLFilterImpl;

public interface InternalizationLogic {
   XMLFilterImpl createExternalReferenceFinder(DOMForest var1);

   boolean checkIfValidTargetNode(DOMForest var1, Element var2, Element var3);

   Element refineSchemaTarget(Element var1);

   Element refineWSDLTarget(Element var1);
}
