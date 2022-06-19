package com.sun.xml.internal.xsom.impl.parser;

import com.sun.xml.internal.xsom.parser.XMLParser;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderAdapter;

/** @deprecated */
public class SAXParserFactoryAdaptor extends SAXParserFactory {
   private final XMLParser parser;

   public SAXParserFactoryAdaptor(XMLParser _parser) {
      this.parser = _parser;
   }

   public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
      return new SAXParserImpl();
   }

   public void setFeature(String name, boolean value) {
      throw new UnsupportedOperationException("XSOM parser does not support JAXP features.");
   }

   public boolean getFeature(String name) {
      return false;
   }

   private class XMLReaderImpl extends XMLFilterImpl {
      private XMLReaderImpl() {
      }

      public void parse(InputSource input) throws IOException, SAXException {
         SAXParserFactoryAdaptor.this.parser.parse(input, this, this, this);
      }

      public void parse(String systemId) throws IOException, SAXException {
         SAXParserFactoryAdaptor.this.parser.parse(new InputSource(systemId), this, this, this);
      }

      // $FF: synthetic method
      XMLReaderImpl(Object x1) {
         this();
      }
   }

   private class SAXParserImpl extends SAXParser {
      private final XMLReaderImpl reader;

      private SAXParserImpl() {
         this.reader = SAXParserFactoryAdaptor.this.new XMLReaderImpl();
      }

      /** @deprecated */
      public Parser getParser() throws SAXException {
         return new XMLReaderAdapter(this.reader);
      }

      public XMLReader getXMLReader() throws SAXException {
         return this.reader;
      }

      public boolean isNamespaceAware() {
         return true;
      }

      public boolean isValidating() {
         return false;
      }

      public void setProperty(String name, Object value) {
      }

      public Object getProperty(String name) {
         return null;
      }

      // $FF: synthetic method
      SAXParserImpl(Object x1) {
         this();
      }
   }
}
