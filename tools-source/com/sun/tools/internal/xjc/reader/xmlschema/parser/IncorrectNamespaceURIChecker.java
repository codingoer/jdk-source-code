package com.sun.tools.internal.xjc.reader.xmlschema.parser;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLFilterImpl;

public class IncorrectNamespaceURIChecker extends XMLFilterImpl {
   private ErrorHandler errorHandler;
   private Locator locator = null;
   private boolean isJAXBPrefixUsed = false;
   private boolean isCustomizationUsed = false;

   public IncorrectNamespaceURIChecker(ErrorHandler handler) {
      this.errorHandler = handler;
   }

   public void endDocument() throws SAXException {
      if (this.isJAXBPrefixUsed && !this.isCustomizationUsed) {
         SAXParseException e = new SAXParseException(Messages.format("IncorrectNamespaceURIChecker.WarnIncorrectURI", "http://java.sun.com/xml/ns/jaxb"), this.locator);
         this.errorHandler.warning(e);
      }

      super.endDocument();
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      if (!"http://www.w3.org/XML/1998/namespace".equals(uri)) {
         if (prefix.equals("jaxb")) {
            this.isJAXBPrefixUsed = true;
         }

         if (uri.equals("http://java.sun.com/xml/ns/jaxb")) {
            this.isCustomizationUsed = true;
         }

         super.startPrefixMapping(prefix, uri);
      }
   }

   public void endPrefixMapping(String prefix) throws SAXException {
      if (!"xml".equals(prefix)) {
         super.endPrefixMapping(prefix);
      }
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      super.startElement(namespaceURI, localName, qName, atts);
      if (namespaceURI.equals("http://java.sun.com/xml/ns/jaxb")) {
         this.isCustomizationUsed = true;
      }

   }

   public void setDocumentLocator(Locator locator) {
      super.setDocumentLocator(locator);
      this.locator = locator;
   }
}
