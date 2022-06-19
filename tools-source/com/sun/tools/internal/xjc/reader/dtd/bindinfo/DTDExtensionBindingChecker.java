package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.reader.AbstractExtensionBindingChecker;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

final class DTDExtensionBindingChecker extends AbstractExtensionBindingChecker {
   public DTDExtensionBindingChecker(String schemaLanguage, Options options, ErrorHandler handler) {
      super(schemaLanguage, options, handler);
   }

   private boolean needsToBePruned(String uri) {
      if (uri.equals(this.schemaLanguage)) {
         return false;
      } else if (uri.equals("http://java.sun.com/xml/ns/jaxb")) {
         return false;
      } else {
         return uri.equals("http://java.sun.com/xml/ns/jaxb/xjc") ? false : this.enabledExtensions.contains(uri);
      }
   }

   public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
      if (!this.isCutting() && !uri.equals("")) {
         this.checkAndEnable(uri);
         this.verifyTagName(uri, localName, qName);
         if (this.needsToBePruned(uri)) {
            this.startCutting();
         }
      }

      super.startElement(uri, localName, qName, atts);
   }
}
