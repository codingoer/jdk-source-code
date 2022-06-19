package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLParserContext;
import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wsdl.document.Fault;
import com.sun.tools.internal.ws.wsdl.document.Input;
import com.sun.tools.internal.ws.wsdl.document.Output;
import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import java.util.Map;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

public class W3CAddressingMetadataExtensionHandler extends AbstractExtensionHandler {
   private ErrorReceiver errReceiver;

   public W3CAddressingMetadataExtensionHandler(Map extensionHandlerMap, ErrorReceiver errReceiver) {
      super(extensionHandlerMap);
      this.errReceiver = errReceiver;
   }

   public String getNamespaceURI() {
      return "http://www.w3.org/2007/05/addressing/metadata";
   }

   public boolean handleInputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      String actionValue = XmlUtil.getAttributeNSOrNull(e, W3CAddressingMetadataConstants.WSAM_ACTION_QNAME);
      if (actionValue != null && !actionValue.equals("")) {
         ((Input)parent).setAction(actionValue);
         return true;
      } else {
         return this.warnEmptyAction(parent, context.getLocation(e));
      }
   }

   public boolean handleOutputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      String actionValue = XmlUtil.getAttributeNSOrNull(e, W3CAddressingMetadataConstants.WSAM_ACTION_QNAME);
      if (actionValue != null && !actionValue.equals("")) {
         ((Output)parent).setAction(actionValue);
         return true;
      } else {
         return this.warnEmptyAction(parent, context.getLocation(e));
      }
   }

   public boolean handleFaultExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      String actionValue = XmlUtil.getAttributeNSOrNull(e, W3CAddressingMetadataConstants.WSAM_ACTION_QNAME);
      if (actionValue != null && !actionValue.equals("")) {
         ((Fault)parent).setAction(actionValue);
         return true;
      } else {
         this.errReceiver.warning(context.getLocation(e), WsdlMessages.WARNING_FAULT_EMPTY_ACTION(parent.getNameValue(), parent.getWSDLElementName().getLocalPart(), parent.getParent().getNameValue()));
         return false;
      }
   }

   private boolean warnEmptyAction(TWSDLExtensible parent, Locator pos) {
      this.errReceiver.warning(pos, WsdlMessages.WARNING_INPUT_OUTPUT_EMPTY_ACTION(parent.getWSDLElementName().getLocalPart(), parent.getParent().getNameValue()));
      return false;
   }
}
