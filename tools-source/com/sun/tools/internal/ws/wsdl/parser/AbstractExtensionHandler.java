package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensionHandler;
import com.sun.tools.internal.ws.api.wsdl.TWSDLParserContext;
import com.sun.tools.internal.ws.wsdl.document.mime.MIMEConstants;
import java.util.Collections;
import java.util.Map;
import org.w3c.dom.Element;

public abstract class AbstractExtensionHandler extends TWSDLExtensionHandler {
   private final Map extensionHandlers;
   private final Map unmodExtenHandlers;

   public AbstractExtensionHandler(Map extensionHandlerMap) {
      this.extensionHandlers = extensionHandlerMap;
      this.unmodExtenHandlers = Collections.unmodifiableMap(this.extensionHandlers);
   }

   public Map getExtensionHandlers() {
      return this.unmodExtenHandlers;
   }

   public boolean doHandleExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return parent.getWSDLElementName().equals(MIMEConstants.QNAME_PART) ? this.handleMIMEPartExtension(context, parent, e) : super.doHandleExtension(context, parent, e);
   }

   protected boolean handleMIMEPartExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }
}
