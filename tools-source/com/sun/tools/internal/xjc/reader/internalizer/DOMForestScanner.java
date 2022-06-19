package com.sun.tools.internal.xjc.reader.internalizer;

import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class DOMForestScanner {
   private final DOMForest forest;

   public DOMForestScanner(DOMForest _forest) {
      this.forest = _forest;
   }

   public void scan(Element e, ContentHandler contentHandler) throws SAXException {
      DOMScanner scanner = new DOMScanner();
      LocationResolver resolver = new LocationResolver(scanner);
      resolver.setContentHandler(contentHandler);
      scanner.setContentHandler(resolver);
      scanner.scan(e);
   }

   public void scan(Document d, ContentHandler contentHandler) throws SAXException {
      this.scan(d.getDocumentElement(), contentHandler);
   }

   private class LocationResolver extends XMLFilterImpl implements Locator {
      private final DOMScanner parent;
      private boolean inStart = false;

      LocationResolver(DOMScanner _parent) {
         this.parent = _parent;
      }

      public void setDocumentLocator(Locator locator) {
         super.setDocumentLocator(this);
      }

      public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
         this.inStart = false;
         super.endElement(namespaceURI, localName, qName);
      }

      public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
         this.inStart = true;
         super.startElement(namespaceURI, localName, qName, atts);
      }

      private Locator findLocator() {
         Node n = this.parent.getCurrentLocation();
         if (n instanceof Element) {
            Element e = (Element)n;
            return this.inStart ? DOMForestScanner.this.forest.locatorTable.getStartLocation(e) : DOMForestScanner.this.forest.locatorTable.getEndLocation(e);
         } else {
            return null;
         }
      }

      public int getColumnNumber() {
         Locator l = this.findLocator();
         return l != null ? l.getColumnNumber() : -1;
      }

      public int getLineNumber() {
         Locator l = this.findLocator();
         return l != null ? l.getLineNumber() : -1;
      }

      public String getPublicId() {
         Locator l = this.findLocator();
         return l != null ? l.getPublicId() : null;
      }

      public String getSystemId() {
         Locator l = this.findLocator();
         return l != null ? l.getSystemId() : null;
      }
   }
}
