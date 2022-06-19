package com.sun.tools.internal.ws.api.wsdl;

import com.sun.tools.internal.ws.wsdl.document.WSDLConstants;
import org.w3c.dom.Element;

/** @deprecated */
public abstract class TWSDLExtensionHandler {
   public String getNamespaceURI() {
      return null;
   }

   public boolean doHandleExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_DEFINITIONS)) {
         return this.handleDefinitionsExtension(context, parent, e);
      } else if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_TYPES)) {
         return this.handleTypesExtension(context, parent, e);
      } else if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_PORT_TYPE)) {
         return this.handlePortTypeExtension(context, parent, e);
      } else if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_BINDING)) {
         return this.handleBindingExtension(context, parent, e);
      } else if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_OPERATION)) {
         return this.handleOperationExtension(context, parent, e);
      } else if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_INPUT)) {
         return this.handleInputExtension(context, parent, e);
      } else if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_OUTPUT)) {
         return this.handleOutputExtension(context, parent, e);
      } else if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_FAULT)) {
         return this.handleFaultExtension(context, parent, e);
      } else if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_SERVICE)) {
         return this.handleServiceExtension(context, parent, e);
      } else {
         return parent.getWSDLElementName().equals(WSDLConstants.QNAME_PORT) ? this.handlePortExtension(context, parent, e) : false;
      }
   }

   public boolean handlePortTypeExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }

   public boolean handleDefinitionsExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }

   public boolean handleTypesExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }

   public boolean handleBindingExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }

   public boolean handleOperationExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }

   public boolean handleInputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }

   public boolean handleOutputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }

   public boolean handleFaultExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }

   public boolean handleServiceExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }

   public boolean handlePortExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }
}
