package com.sun.xml.internal.xsom.parser;

import com.sun.xml.internal.xsom.impl.parser.Messages;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class JAXPParser implements XMLParser {
   private static final String ACCESS_EXTERNAL_SCHEMA = "http://javax.xml.XMLConstants/property/accessExternalSchema";
   private static final Logger LOGGER = Logger.getLogger(JAXPParser.class.getName());
   private final SAXParserFactory factory;

   public JAXPParser(SAXParserFactory factory) {
      factory.setNamespaceAware(true);
      this.factory = factory;
   }

   /** @deprecated */
   public JAXPParser() {
      this(SAXParserFactory.newInstance());
   }

   public void parse(InputSource source, ContentHandler handler, ErrorHandler errorHandler, EntityResolver entityResolver) throws SAXException, IOException {
      try {
         SAXParser saxParser = allowFileAccess(this.factory.newSAXParser(), false);
         XMLReader reader = new XMLReaderEx(saxParser.getXMLReader());
         reader.setContentHandler(handler);
         if (errorHandler != null) {
            reader.setErrorHandler(errorHandler);
         }

         if (entityResolver != null) {
            reader.setEntityResolver(entityResolver);
         }

         reader.parse(source);
      } catch (ParserConfigurationException var7) {
         SAXParseException spe = new SAXParseException(var7.getMessage(), (Locator)null, var7);
         errorHandler.fatalError(spe);
         throw spe;
      }
   }

   private static SAXParser allowFileAccess(SAXParser saxParser, boolean disableSecureProcessing) throws SAXException {
      if (disableSecureProcessing) {
         return saxParser;
      } else {
         try {
            saxParser.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", "file");
            LOGGER.log(Level.FINE, Messages.format("JAXPSupportedProperty", "http://javax.xml.XMLConstants/property/accessExternalSchema"));
         } catch (SAXException var3) {
            LOGGER.log(Level.CONFIG, Messages.format("JAXPUnsupportedProperty", "http://javax.xml.XMLConstants/property/accessExternalSchema"), var3);
         }

         return saxParser;
      }
   }

   private static class XMLReaderEx extends XMLFilterImpl {
      private Locator locator;

      XMLReaderEx(XMLReader parent) {
         this.setParent(parent);
      }

      public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
         try {
            InputSource is = null;
            if (this.getEntityResolver() != null) {
               is = this.getEntityResolver().resolveEntity(publicId, systemId);
            }

            if (is != null) {
               return is;
            } else {
               is = new InputSource((new URL(systemId)).openStream());
               is.setSystemId(systemId);
               is.setPublicId(publicId);
               return is;
            }
         } catch (IOException var5) {
            SAXParseException spe = new SAXParseException(Messages.format("EntityResolutionFailure", systemId, var5.toString()), this.locator, var5);
            if (this.getErrorHandler() != null) {
               this.getErrorHandler().fatalError(spe);
            }

            throw spe;
         }
      }

      public void setDocumentLocator(Locator locator) {
         super.setDocumentLocator(locator);
         this.locator = locator;
      }
   }
}
