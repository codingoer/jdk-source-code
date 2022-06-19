package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.xml.internal.xsom.parser.XMLParser;
import java.io.IOException;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DOMForestParser implements XMLParser {
   private final DOMForest forest;
   private final DOMForestScanner scanner;
   private final XMLParser fallbackParser;

   public DOMForestParser(DOMForest forest, XMLParser fallbackParser) {
      this.forest = forest;
      this.scanner = new DOMForestScanner(forest);
      this.fallbackParser = fallbackParser;
   }

   public void parse(InputSource source, ContentHandler handler, EntityResolver entityResolver, ErrorHandler errHandler) throws SAXException, IOException {
   }

   public void parse(InputSource source, ContentHandler handler, ErrorHandler errorHandler, EntityResolver entityResolver) throws SAXException, IOException {
      String systemId = source.getSystemId();
      Document dom = this.forest.get(systemId);
      if (dom == null) {
         this.fallbackParser.parse(source, handler, errorHandler, entityResolver);
      } else {
         this.scanner.scan(dom, handler);
      }
   }
}
