package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

class WhitespaceStripper extends XMLFilterImpl {
   private int state = 0;
   private char[] buf = new char[1024];
   private int bufLen = 0;
   private static final int AFTER_START_ELEMENT = 1;
   private static final int AFTER_END_ELEMENT = 2;

   public WhitespaceStripper(XMLReader reader) {
      this.setParent(reader);
   }

   public WhitespaceStripper(ContentHandler handler, ErrorHandler eh, EntityResolver er) {
      this.setContentHandler(handler);
      if (eh != null) {
         this.setErrorHandler(eh);
      }

      if (er != null) {
         this.setEntityResolver(er);
      }

   }

   public void characters(char[] ch, int start, int length) throws SAXException {
      switch (this.state) {
         case 1:
            if (this.bufLen + length > this.buf.length) {
               char[] newBuf = new char[Math.max(this.bufLen + length, this.buf.length * 2)];
               System.arraycopy(this.buf, 0, newBuf, 0, this.bufLen);
               this.buf = newBuf;
            }

            System.arraycopy(ch, start, this.buf, this.bufLen, length);
            this.bufLen += length;
            break;
         case 2:
            int len = start + length;

            for(int i = start; i < len; ++i) {
               if (!WhiteSpaceProcessor.isWhiteSpace(ch[i])) {
                  super.characters(ch, start, length);
                  return;
               }
            }
      }

   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      this.processPendingText();
      super.startElement(uri, localName, qName, atts);
      this.state = 1;
      this.bufLen = 0;
   }

   public void endElement(String uri, String localName, String qName) throws SAXException {
      this.processPendingText();
      super.endElement(uri, localName, qName);
      this.state = 2;
   }

   private void processPendingText() throws SAXException {
      if (this.state == 1) {
         for(int i = this.bufLen - 1; i >= 0; --i) {
            if (!WhiteSpaceProcessor.isWhiteSpace(this.buf[i])) {
               super.characters(this.buf, 0, this.bufLen);
               return;
            }
         }
      }

   }

   public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
   }
}
