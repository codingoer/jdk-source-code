package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.wsdl.document.jaxws.JAXWSBindingsConstants;
import com.sun.tools.internal.xjc.reader.internalizer.LocatorTable;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import java.util.Set;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

class DOMBuilder extends SAX2DOMEx implements LexicalHandler {
   private final LocatorTable locatorTable;
   private final Set outerMostBindings;
   private Locator locator;

   public DOMBuilder(Document dom, LocatorTable ltable, Set outerMostBindings) {
      super(dom);
      this.locatorTable = ltable;
      this.outerMostBindings = outerMostBindings;
   }

   public void setDocumentLocator(Locator locator) {
      this.locator = locator;
      super.setDocumentLocator(locator);
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
      super.startElement(namespaceURI, localName, qName, atts);
      Element e = this.getCurrentElement();
      this.locatorTable.storeStartLocation(e, this.locator);
      if (JAXWSBindingsConstants.JAXWS_BINDINGS.getNamespaceURI().equals(e.getNamespaceURI()) && "bindings".equals(e.getLocalName())) {
         Node p = e.getParentNode();
         if (p instanceof Document) {
            this.outerMostBindings.add(e);
         }
      }

   }

   public void endElement(String namespaceURI, String localName, String qName) {
      this.locatorTable.storeEndLocation(this.getCurrentElement(), this.locator);
      super.endElement(namespaceURI, localName, qName);
   }

   public void startDTD(String name, String publicId, String systemId) throws SAXException {
   }

   public void endDTD() throws SAXException {
   }

   public void startEntity(String name) throws SAXException {
   }

   public void endEntity(String name) throws SAXException {
   }

   public void startCDATA() throws SAXException {
   }

   public void endCDATA() throws SAXException {
   }

   public void comment(char[] ch, int start, int length) throws SAXException {
      Node parent = (Node)this.nodeStack.peek();
      Comment comment = this.document.createComment(new String(ch, start, length));
      parent.appendChild(comment);
   }
}
