package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLParserContext;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class W3CAddressingExtensionHandler extends AbstractExtensionHandler {
   public W3CAddressingExtensionHandler(Map extensionHandlerMap) {
      this(extensionHandlerMap, (ErrorReceiver)null);
   }

   public W3CAddressingExtensionHandler(Map extensionHandlerMap, ErrorReceiver errReceiver) {
      super(extensionHandlerMap);
   }

   public String getNamespaceURI() {
      return AddressingVersion.W3C.wsdlNsUri;
   }

   protected QName getWSDLExtensionQName() {
      return AddressingVersion.W3C.wsdlExtensionTag;
   }

   public boolean handleBindingExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return XmlUtil.matchesTagNS(e, this.getWSDLExtensionQName());
   }

   public boolean handlePortExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return this.handleBindingExtension(context, parent, e);
   }
}
