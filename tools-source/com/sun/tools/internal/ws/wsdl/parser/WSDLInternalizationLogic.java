package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.xjc.util.DOMUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.XMLFilterImpl;

public class WSDLInternalizationLogic implements InternalizationLogic {
   public XMLFilterImpl createExternalReferenceFinder(DOMForest parent) {
      return new ReferenceFinder(parent);
   }

   public boolean checkIfValidTargetNode(DOMForest parent, Element bindings, Element target) {
      return false;
   }

   public Element refineSchemaTarget(Element target) {
      Element annotation = DOMUtils.getFirstChildElement(target, "http://www.w3.org/2001/XMLSchema", "annotation");
      if (annotation == null) {
         annotation = this.insertXMLSchemaElement(target, "annotation");
      }

      Element appinfo = DOMUtils.getFirstChildElement(annotation, "http://www.w3.org/2001/XMLSchema", "appinfo");
      if (appinfo == null) {
         appinfo = this.insertXMLSchemaElement(annotation, "appinfo");
      }

      return appinfo;
   }

   public Element refineWSDLTarget(Element target) {
      Element JAXWSBindings = DOMUtils.getFirstChildElement(target, "http://java.sun.com/xml/ns/jaxws", "bindings");
      if (JAXWSBindings == null) {
         JAXWSBindings = this.insertJAXWSBindingsElement(target, "bindings");
      }

      return JAXWSBindings;
   }

   private Element insertJAXWSBindingsElement(Element parent, String localName) {
      String qname = "JAXWS:" + localName;
      Element child = parent.getOwnerDocument().createElementNS("http://java.sun.com/xml/ns/jaxws", qname);
      NodeList children = parent.getChildNodes();
      if (children.getLength() == 0) {
         parent.appendChild(child);
      } else {
         parent.insertBefore(child, children.item(0));
      }

      return child;
   }

   private Element insertXMLSchemaElement(Element parent, String localName) {
      String qname = parent.getTagName();
      int idx = qname.indexOf(58);
      if (idx == -1) {
         qname = localName;
      } else {
         qname = qname.substring(0, idx + 1) + localName;
      }

      Element child = parent.getOwnerDocument().createElementNS("http://www.w3.org/2001/XMLSchema", qname);
      NodeList children = parent.getChildNodes();
      if (children.getLength() == 0) {
         parent.appendChild(child);
      } else {
         parent.insertBefore(child, children.item(0));
      }

      return child;
   }

   private static final class ReferenceFinder extends AbstractReferenceFinderImpl {
      ReferenceFinder(DOMForest parent) {
         super(parent);
      }

      protected String findExternalResource(String nsURI, String localName, Attributes atts) {
         if ("http://schemas.xmlsoap.org/wsdl/".equals(nsURI) && "import".equals(localName)) {
            return atts.getValue("location");
         } else {
            return this.parent.options.clientjar != null && "http://www.w3.org/2001/XMLSchema".equals(nsURI) && "import".equals(localName) ? atts.getValue("schemaLocation") : null;
         }
      }
   }
}
