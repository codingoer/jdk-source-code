package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLParserContext;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.sun.tools.internal.ws.wsdl.document.WSDLConstants;
import com.sun.tools.internal.ws.wsdl.document.mime.MIMEConstants;
import com.sun.tools.internal.ws.wsdl.document.mime.MIMEContent;
import com.sun.tools.internal.ws.wsdl.document.mime.MIMEMultipartRelated;
import com.sun.tools.internal.ws.wsdl.document.mime.MIMEPart;
import com.sun.tools.internal.ws.wsdl.document.mime.MIMEXml;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Element;

public class MIMEExtensionHandler extends AbstractExtensionHandler {
   public MIMEExtensionHandler(Map extensionHandlerMap) {
      super(extensionHandlerMap);
   }

   public String getNamespaceURI() {
      return "http://schemas.xmlsoap.org/wsdl/mime/";
   }

   public boolean doHandleExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_OUTPUT)) {
         return this.handleInputOutputExtension(context, parent, e);
      } else if (parent.getWSDLElementName().equals(WSDLConstants.QNAME_INPUT)) {
         return this.handleInputOutputExtension(context, parent, e);
      } else {
         return parent.getWSDLElementName().equals(MIMEConstants.QNAME_PART) ? this.handleMIMEPartExtension(context, parent, e) : false;
      }
   }

   protected boolean handleInputOutputExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (!XmlUtil.matchesTagNS(e, MIMEConstants.QNAME_MULTIPART_RELATED)) {
         if (XmlUtil.matchesTagNS(e, MIMEConstants.QNAME_CONTENT)) {
            MIMEContent content = this.parseMIMEContent(context, e);
            parent.addExtension(content);
            return true;
         } else if (XmlUtil.matchesTagNS(e, MIMEConstants.QNAME_MIME_XML)) {
            MIMEXml mimeXml = this.parseMIMEXml(context, e);
            parent.addExtension(mimeXml);
            return true;
         } else {
            Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
            return false;
         }
      } else {
         context.push();
         context.registerNamespaces(e);
         MIMEMultipartRelated mpr = new MIMEMultipartRelated(context.getLocation(e));
         Iterator iter = XmlUtil.getAllChildren(e);

         while(iter.hasNext()) {
            Element e2 = Util.nextElement(iter);
            if (e2 == null) {
               break;
            }

            if (!XmlUtil.matchesTagNS(e2, MIMEConstants.QNAME_PART)) {
               Util.fail("parsing.invalidElement", e2.getTagName(), e2.getNamespaceURI());
            } else {
               context.push();
               context.registerNamespaces(e2);
               MIMEPart part = new MIMEPart(context.getLocation(e2));
               String name = XmlUtil.getAttributeOrNull(e2, "name");
               if (name != null) {
                  part.setName(name);
               }

               Iterator iter2 = XmlUtil.getAllChildren(e2);

               while(iter2.hasNext()) {
                  Element e3 = Util.nextElement(iter2);
                  if (e3 == null) {
                     break;
                  }

                  AbstractExtensionHandler h = (AbstractExtensionHandler)this.getExtensionHandlers().get(e3.getNamespaceURI());
                  boolean handled = false;
                  if (h != null) {
                     handled = h.doHandleExtension(context, part, e3);
                  }

                  if (!handled) {
                     String required = XmlUtil.getAttributeNSOrNull(e3, "required", "http://schemas.xmlsoap.org/wsdl/");
                     if (required != null && required.equals("true")) {
                        Util.fail("parsing.requiredExtensibilityElement", e3.getTagName(), e3.getNamespaceURI());
                     }
                  }
               }

               mpr.add(part);
               context.pop();
            }
         }

         parent.addExtension(mpr);
         context.pop();
         return true;
      }
   }

   protected boolean handleMIMEPartExtension(TWSDLParserContext context, TWSDLExtensible parent, Element e) {
      if (XmlUtil.matchesTagNS(e, MIMEConstants.QNAME_CONTENT)) {
         MIMEContent content = this.parseMIMEContent(context, e);
         parent.addExtension(content);
         return true;
      } else if (XmlUtil.matchesTagNS(e, MIMEConstants.QNAME_MIME_XML)) {
         MIMEXml mimeXml = this.parseMIMEXml(context, e);
         parent.addExtension(mimeXml);
         return true;
      } else {
         Util.fail("parsing.invalidExtensionElement", e.getTagName(), e.getNamespaceURI());
         return false;
      }
   }

   protected MIMEContent parseMIMEContent(TWSDLParserContext context, Element e) {
      context.push();
      context.registerNamespaces(e);
      MIMEContent content = new MIMEContent(context.getLocation(e));
      String part = XmlUtil.getAttributeOrNull(e, "part");
      if (part != null) {
         content.setPart(part);
      }

      String type = XmlUtil.getAttributeOrNull(e, "type");
      if (type != null) {
         content.setType(type);
      }

      context.pop();
      return content;
   }

   protected MIMEXml parseMIMEXml(TWSDLParserContext context, Element e) {
      context.push();
      context.registerNamespaces(e);
      MIMEXml mimeXml = new MIMEXml(context.getLocation(e));
      String part = XmlUtil.getAttributeOrNull(e, "part");
      if (part != null) {
         mimeXml.setPart(part);
      }

      context.pop();
      return mimeXml;
   }
}
