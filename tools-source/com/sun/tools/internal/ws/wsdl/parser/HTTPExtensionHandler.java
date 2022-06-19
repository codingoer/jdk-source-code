package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLParserContext;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.sun.tools.internal.ws.wsdl.document.http.HTTPAddress;
import com.sun.tools.internal.ws.wsdl.document.http.HTTPBinding;
import com.sun.tools.internal.ws.wsdl.document.http.HTTPConstants;
import com.sun.tools.internal.ws.wsdl.document.http.HTTPOperation;
import com.sun.tools.internal.ws.wsdl.document.http.HTTPUrlEncoded;
import com.sun.tools.internal.ws.wsdl.document.http.HTTPUrlReplacement;
import java.util.Map;
import org.w3c.dom.Element;

public class HTTPExtensionHandler extends AbstractExtensionHandler {
   public HTTPExtensionHandler(Map extensionHandlerMap) {
      super(extensionHandlerMap);
   }

   public String getNamespaceURI() {
      return "http://schemas.xmlsoap.org/wsdl/http/";
   }

   public boolean handleDefinitionsExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
      return false;
   }

   public boolean handleTypesExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
      return false;
   }

   public boolean handleBindingExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (XmlUtil.matchesTagNS(e, HTTPConstants.QNAME_BINDING)) {
         context.push();
         context.registerNamespaces(e);
         HTTPBinding binding = new HTTPBinding(context.getLocation(e));
         String verb = Util.getRequiredAttribute(e, "verb");
         binding.setVerb(verb);
         parent.addExtension(binding);
         context.pop();
         return true;
      } else {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      }
   }

   public boolean handleOperationExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (XmlUtil.matchesTagNS(e, HTTPConstants.QNAME_OPERATION)) {
         context.push();
         context.registerNamespaces(e);
         HTTPOperation operation = new HTTPOperation(context.getLocation(e));
         String location = Util.getRequiredAttribute(e, "location");
         operation.setLocation(location);
         parent.addExtension(operation);
         context.pop();
         return true;
      } else {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      }
   }

   public boolean handleInputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (XmlUtil.matchesTagNS(e, HTTPConstants.QNAME_URL_ENCODED)) {
         parent.addExtension(new HTTPUrlEncoded(context.getLocation(e)));
         return true;
      } else if (XmlUtil.matchesTagNS(e, HTTPConstants.QNAME_URL_REPLACEMENT)) {
         parent.addExtension(new HTTPUrlReplacement(context.getLocation(e)));
         return true;
      } else {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      }
   }

   public boolean handleOutputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
      return false;
   }

   public boolean handleFaultExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
      return false;
   }

   public boolean handleServiceExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
      return false;
   }

   public boolean handlePortExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (XmlUtil.matchesTagNS(e, HTTPConstants.QNAME_ADDRESS)) {
         context.push();
         context.registerNamespaces(e);
         HTTPAddress address = new HTTPAddress(context.getLocation(e));
         String location = Util.getRequiredAttribute(e, "location");
         address.setLocation(location);
         parent.addExtension(address);
         context.pop();
         return true;
      } else {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      }
   }

   public boolean handlePortTypeExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
      return false;
   }
}
