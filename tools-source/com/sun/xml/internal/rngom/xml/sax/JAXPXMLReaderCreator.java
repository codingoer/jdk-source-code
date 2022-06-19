package com.sun.xml.internal.rngom.xml.sax;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class JAXPXMLReaderCreator implements XMLReaderCreator {
   private final SAXParserFactory spf;

   public JAXPXMLReaderCreator(SAXParserFactory spf) {
      this.spf = spf;
   }

   public JAXPXMLReaderCreator() {
      this.spf = SAXParserFactory.newInstance();

      try {
         this.spf.setNamespaceAware(true);
         this.spf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
      } catch (ParserConfigurationException var2) {
         Logger.getLogger(JAXPXMLReaderCreator.class.getName()).log(Level.SEVERE, (String)null, var2);
      } catch (SAXNotRecognizedException var3) {
         Logger.getLogger(JAXPXMLReaderCreator.class.getName()).log(Level.SEVERE, (String)null, var3);
      } catch (SAXNotSupportedException var4) {
         Logger.getLogger(JAXPXMLReaderCreator.class.getName()).log(Level.SEVERE, (String)null, var4);
      }

   }

   public XMLReader createXMLReader() throws SAXException {
      try {
         return this.spf.newSAXParser().getXMLReader();
      } catch (ParserConfigurationException var2) {
         throw new SAXException(var2);
      }
   }
}
