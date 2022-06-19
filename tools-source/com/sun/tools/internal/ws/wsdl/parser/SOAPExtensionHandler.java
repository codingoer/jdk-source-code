package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLParserContext;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPAddress;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPBinding;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPBody;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPConstants;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPFault;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPHeader;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPHeaderFault;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPOperation;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPStyle;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPUse;
import com.sun.tools.internal.ws.wsdl.framework.TWSDLParserContextImpl;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

public class SOAPExtensionHandler extends AbstractExtensionHandler {
   public SOAPExtensionHandler(Map extensionHandlerMap) {
      super(extensionHandlerMap);
   }

   public String getNamespaceURI() {
      return "http://schemas.xmlsoap.org/wsdl/soap/";
   }

   public boolean handleDefinitionsExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
      return false;
   }

   public boolean handleTypesExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
      return false;
   }

   protected SOAPBinding getSOAPBinding(Locator location) {
      return new SOAPBinding(location);
   }

   public boolean handleBindingExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (XmlUtil.matchesTagNS(e, this.getBindingQName())) {
         context.push();
         context.registerNamespaces(e);
         SOAPBinding binding = this.getSOAPBinding(context.getLocation(e));
         String transport = Util.getRequiredAttribute(e, "transport");
         binding.setTransport(transport);
         String style = XmlUtil.getAttributeOrNull(e, "style");
         if (style != null) {
            if (style.equals("rpc")) {
               binding.setStyle(SOAPStyle.RPC);
            } else if (style.equals("document")) {
               binding.setStyle(SOAPStyle.DOCUMENT);
            } else {
               Util.fail("parsing.invalidAttributeValue", "style", style);
            }
         }

         parent.addExtension(binding);
         context.pop();
         return true;
      } else {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      }
   }

   public boolean handleOperationExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (XmlUtil.matchesTagNS(e, this.getOperationQName())) {
         context.push();
         context.registerNamespaces(e);
         SOAPOperation operation = new SOAPOperation(context.getLocation(e));
         String soapAction = XmlUtil.getAttributeOrNull(e, "soapAction");
         if (soapAction != null) {
            operation.setSOAPAction(soapAction);
         }

         String style = XmlUtil.getAttributeOrNull(e, "style");
         if (style != null) {
            if (style.equals("rpc")) {
               operation.setStyle(SOAPStyle.RPC);
            } else if (style.equals("document")) {
               operation.setStyle(SOAPStyle.DOCUMENT);
            } else {
               Util.fail("parsing.invalidAttributeValue", "style", style);
            }
         }

         parent.addExtension(operation);
         context.pop();
         return true;
      } else {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      }
   }

   public boolean handleInputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return this.handleInputOutputExtension(context, parent, e);
   }

   public boolean handleOutputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return this.handleInputOutputExtension(context, parent, e);
   }

   protected boolean handleMIMEPartExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return this.handleInputOutputExtension(context, parent, e);
   }

   protected boolean handleInputOutputExtension(TWSDLParserContext contextif, TWSDLExtensible parent, Element e) {
      TWSDLParserContextImpl context = (TWSDLParserContextImpl)contextif;
      if (XmlUtil.matchesTagNS(e, this.getBodyQName())) {
         context.push();
         context.registerNamespaces(e);
         SOAPBody body = new SOAPBody(context.getLocation(e));
         String use = XmlUtil.getAttributeOrNull(e, "use");
         if (use != null) {
            if (use.equals("literal")) {
               body.setUse(SOAPUse.LITERAL);
            } else if (use.equals("encoded")) {
               body.setUse(SOAPUse.ENCODED);
            } else {
               Util.fail("parsing.invalidAttributeValue", "use", use);
            }
         }

         String namespace = XmlUtil.getAttributeOrNull(e, "namespace");
         if (namespace != null) {
            body.setNamespace(namespace);
         }

         String encodingStyle = XmlUtil.getAttributeOrNull(e, "encodingStyle");
         if (encodingStyle != null) {
            body.setEncodingStyle(encodingStyle);
         }

         String parts = XmlUtil.getAttributeOrNull(e, "parts");
         if (parts != null) {
            body.setParts(parts);
         }

         parent.addExtension(body);
         context.pop();
         return true;
      } else if (XmlUtil.matchesTagNS(e, this.getHeaderQName())) {
         return this.handleHeaderElement(parent, e, context);
      } else {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      }
   }

   private boolean handleHeaderElement(TWSDLExtensible parent, Element e, TWSDLParserContextImpl context) {
      context.push();
      context.registerNamespaces(e);
      SOAPHeader header = new SOAPHeader(context.getLocation(e));
      String use = XmlUtil.getAttributeOrNull(e, "use");
      if (use != null) {
         if (use.equals("literal")) {
            header.setUse(SOAPUse.LITERAL);
         } else if (use.equals("encoded")) {
            header.setUse(SOAPUse.ENCODED);
         } else {
            Util.fail("parsing.invalidAttributeValue", "use", use);
         }
      }

      String namespace = XmlUtil.getAttributeOrNull(e, "namespace");
      if (namespace != null) {
         header.setNamespace(namespace);
      }

      String encodingStyle = XmlUtil.getAttributeOrNull(e, "encodingStyle");
      if (encodingStyle != null) {
         header.setEncodingStyle(encodingStyle);
      }

      String part = XmlUtil.getAttributeOrNull(e, "part");
      if (part != null) {
         header.setPart(part);
      }

      String messageAttr = XmlUtil.getAttributeOrNull(e, "message");
      if (messageAttr != null) {
         header.setMessage(context.translateQualifiedName(context.getLocation(e), messageAttr));
      }

      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, this.getHeaderfaultQName())) {
            this.handleHeaderFaultElement(e, context, header, use, e2);
         } else {
            Util.fail("parsing.invalidElement", e2.getTagName(), e2.getNamespaceURI());
         }
      }

      parent.addExtension(header);
      context.pop();
      context.fireDoneParsingEntity(this.getHeaderQName(), header);
      return true;
   }

   private void handleHeaderFaultElement(Element e, TWSDLParserContextImpl context, SOAPHeader header, String use, Element e2) {
      context.push();
      context.registerNamespaces(e);
      SOAPHeaderFault headerfault = new SOAPHeaderFault(context.getLocation(e));
      String use2 = XmlUtil.getAttributeOrNull(e2, "use");
      if (use2 != null) {
         if (use2.equals("literal")) {
            headerfault.setUse(SOAPUse.LITERAL);
         } else if (use.equals("encoded")) {
            headerfault.setUse(SOAPUse.ENCODED);
         } else {
            Util.fail("parsing.invalidAttributeValue", "use", use2);
         }
      }

      String namespace2 = XmlUtil.getAttributeOrNull(e2, "namespace");
      if (namespace2 != null) {
         headerfault.setNamespace(namespace2);
      }

      String encodingStyle2 = XmlUtil.getAttributeOrNull(e2, "encodingStyle");
      if (encodingStyle2 != null) {
         headerfault.setEncodingStyle(encodingStyle2);
      }

      String part2 = XmlUtil.getAttributeOrNull(e2, "part");
      if (part2 != null) {
         headerfault.setPart(part2);
      }

      String messageAttr2 = XmlUtil.getAttributeOrNull(e2, "message");
      if (messageAttr2 != null) {
         headerfault.setMessage(context.translateQualifiedName(context.getLocation(e2), messageAttr2));
      }

      header.add(headerfault);
      context.pop();
   }

   public boolean handleFaultExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (XmlUtil.matchesTagNS(e, this.getFaultQName())) {
         context.push();
         context.registerNamespaces(e);
         SOAPFault fault = new SOAPFault(context.getLocation(e));
         String name = XmlUtil.getAttributeOrNull(e, "name");
         if (name != null) {
            fault.setName(name);
         }

         String use = XmlUtil.getAttributeOrNull(e, "use");
         if (use != null) {
            if (use.equals("literal")) {
               fault.setUse(SOAPUse.LITERAL);
            } else if (use.equals("encoded")) {
               fault.setUse(SOAPUse.ENCODED);
            } else {
               Util.fail("parsing.invalidAttributeValue", "use", use);
            }
         }

         String namespace = XmlUtil.getAttributeOrNull(e, "namespace");
         if (namespace != null) {
            fault.setNamespace(namespace);
         }

         String encodingStyle = XmlUtil.getAttributeOrNull(e, "encodingStyle");
         if (encodingStyle != null) {
            fault.setEncodingStyle(encodingStyle);
         }

         parent.addExtension(fault);
         context.pop();
         return true;
      } else if (XmlUtil.matchesTagNS(e, this.getHeaderQName())) {
         return this.handleHeaderElement(parent, e, (TWSDLParserContextImpl)context);
      } else {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      }
   }

   public boolean handleServiceExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
      return false;
   }

   public boolean handlePortExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (XmlUtil.matchesTagNS(e, this.getAddressQName())) {
         context.push();
         context.registerNamespaces(e);
         SOAPAddress address = new SOAPAddress(context.getLocation(e));
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

   protected QName getBodyQName() {
      return SOAPConstants.QNAME_BODY;
   }

   protected QName getHeaderQName() {
      return SOAPConstants.QNAME_HEADER;
   }

   protected QName getHeaderfaultQName() {
      return SOAPConstants.QNAME_HEADERFAULT;
   }

   protected QName getOperationQName() {
      return SOAPConstants.QNAME_OPERATION;
   }

   protected QName getFaultQName() {
      return SOAPConstants.QNAME_FAULT;
   }

   protected QName getAddressQName() {
      return SOAPConstants.QNAME_ADDRESS;
   }

   protected QName getBindingQName() {
      return SOAPConstants.QNAME_BINDING;
   }
}
