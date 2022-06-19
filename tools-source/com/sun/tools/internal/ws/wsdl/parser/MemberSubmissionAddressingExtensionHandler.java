package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLParserContext;
import com.sun.tools.internal.ws.resources.ModelerMessages;
import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wsdl.document.Fault;
import com.sun.tools.internal.ws.wsdl.document.Input;
import com.sun.tools.internal.ws.wsdl.document.Output;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import java.util.Map;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

public class MemberSubmissionAddressingExtensionHandler extends W3CAddressingExtensionHandler {
   private ErrorReceiver errReceiver;
   private boolean extensionModeOn;

   public MemberSubmissionAddressingExtensionHandler(Map extensionHandlerMap, ErrorReceiver env, boolean extensionModeOn) {
      super(extensionHandlerMap, env);
      this.errReceiver = env;
      this.extensionModeOn = extensionModeOn;
   }

   public String getNamespaceURI() {
      return AddressingVersion.MEMBER.wsdlNsUri;
   }

   protected QName getWSDLExtensionQName() {
      return AddressingVersion.MEMBER.wsdlExtensionTag;
   }

   public boolean handlePortExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      return false;
   }

   public boolean handleInputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (this.extensionModeOn) {
         this.warn(context.getLocation(e));
         String actionValue = XmlUtil.getAttributeNSOrNull(e, MemberSubmissionAddressingConstants.WSA_ACTION_QNAME);
         if (actionValue != null && !actionValue.equals("")) {
            ((Input)parent).setAction(actionValue);
            return true;
         } else {
            return this.warnEmptyAction(parent, context.getLocation(e));
         }
      } else {
         return this.fail(context.getLocation(e));
      }
   }

   private boolean fail(Locator location) {
      this.errReceiver.warning(location, ModelerMessages.WSDLMODELER_INVALID_IGNORING_MEMBER_SUBMISSION_ADDRESSING(AddressingVersion.MEMBER.nsUri, "http://www.w3.org/2007/05/addressing/metadata"));
      return false;
   }

   private void warn(Locator location) {
      this.errReceiver.warning(location, ModelerMessages.WSDLMODELER_WARNING_MEMBER_SUBMISSION_ADDRESSING_USED(AddressingVersion.MEMBER.nsUri, "http://www.w3.org/2007/05/addressing/metadata"));
   }

   public boolean handleOutputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (this.extensionModeOn) {
         this.warn(context.getLocation(e));
         String actionValue = XmlUtil.getAttributeNSOrNull(e, MemberSubmissionAddressingConstants.WSA_ACTION_QNAME);
         if (actionValue != null && !actionValue.equals("")) {
            ((Output)parent).setAction(actionValue);
            return true;
         } else {
            return this.warnEmptyAction(parent, context.getLocation(e));
         }
      } else {
         return this.fail(context.getLocation(e));
      }
   }

   public boolean handleFaultExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (this.extensionModeOn) {
         this.warn(context.getLocation(e));
         String actionValue = XmlUtil.getAttributeNSOrNull(e, MemberSubmissionAddressingConstants.WSA_ACTION_QNAME);
         if (actionValue != null && !actionValue.equals("")) {
            ((Fault)parent).setAction(actionValue);
            return true;
         } else {
            this.errReceiver.warning(context.getLocation(e), WsdlMessages.WARNING_FAULT_EMPTY_ACTION(parent.getNameValue(), parent.getWSDLElementName().getLocalPart(), parent.getParent().getNameValue()));
            return false;
         }
      } else {
         return this.fail(context.getLocation(e));
      }
   }

   private boolean warnEmptyAction(TWSDLExtensible parent, Locator pos) {
      this.errReceiver.warning(pos, WsdlMessages.WARNING_INPUT_OUTPUT_EMPTY_ACTION(parent.getWSDLElementName().getLocalPart(), parent.getParent().getNameValue()));
      return false;
   }
}
