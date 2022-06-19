package com.sun.tools.internal.xjc.reader.internalizer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;

public class VersionChecker extends XMLFilterImpl {
   private String version = null;
   private boolean seenRoot = false;
   private boolean seenBindings = false;
   private Locator locator;
   private Locator rootTagStart;
   private static final Set VERSIONS = new HashSet(Arrays.asList("1.0", "2.0", "2.1"));

   public VersionChecker(XMLReader parent) {
      this.setParent(parent);
   }

   public VersionChecker(ContentHandler handler, ErrorHandler eh, EntityResolver er) {
      this.setContentHandler(handler);
      if (eh != null) {
         this.setErrorHandler(eh);
      }

      if (er != null) {
         this.setEntityResolver(er);
      }

   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      super.startElement(namespaceURI, localName, qName, atts);
      if (!this.seenRoot) {
         this.seenRoot = true;
         this.rootTagStart = new LocatorImpl(this.locator);
         this.version = atts.getValue("http://java.sun.com/xml/ns/jaxb", "version");
         if (namespaceURI.equals("http://java.sun.com/xml/ns/jaxb")) {
            String version2 = atts.getValue("", "version");
            if (this.version != null && version2 != null) {
               SAXParseException e = new SAXParseException(Messages.format("Internalizer.TwoVersionAttributes"), this.locator);
               this.getErrorHandler().error(e);
            }

            if (this.version == null) {
               this.version = version2;
            }
         }
      }

      if ("http://java.sun.com/xml/ns/jaxb".equals(namespaceURI)) {
         this.seenBindings = true;
      }

   }

   public void endDocument() throws SAXException {
      super.endDocument();
      SAXParseException e;
      if (this.seenBindings && this.version == null) {
         e = new SAXParseException(Messages.format("Internalizer.VersionNotPresent"), this.rootTagStart);
         this.getErrorHandler().error(e);
      }

      if (this.version != null && !VERSIONS.contains(this.version)) {
         e = new SAXParseException(Messages.format("Internalizer.IncorrectVersion"), this.rootTagStart);
         this.getErrorHandler().error(e);
      }

   }

   public void setDocumentLocator(Locator locator) {
      super.setDocumentLocator(locator);
      this.locator = locator;
   }
}
