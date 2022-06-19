package com.sun.tools.internal.xjc.util;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public abstract class SubtreeCutter extends XMLFilterImpl {
   private int cutDepth = 0;
   private static final ContentHandler stub = new DefaultHandler();
   private ContentHandler next;

   public void startDocument() throws SAXException {
      this.cutDepth = 0;
      super.startDocument();
   }

   public boolean isCutting() {
      return this.cutDepth > 0;
   }

   public void startCutting() {
      super.setContentHandler(stub);
      this.cutDepth = 1;
   }

   public void setContentHandler(ContentHandler handler) {
      this.next = handler;
      if (this.getContentHandler() != stub) {
         super.setContentHandler(handler);
      }

   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      if (this.cutDepth > 0) {
         ++this.cutDepth;
      }

      super.startElement(uri, localName, qName, atts);
   }

   public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
      super.endElement(namespaceURI, localName, qName);
      if (this.cutDepth != 0) {
         --this.cutDepth;
         if (this.cutDepth == 1) {
            super.setContentHandler(this.next);
            this.cutDepth = 0;
         }
      }

   }
}
