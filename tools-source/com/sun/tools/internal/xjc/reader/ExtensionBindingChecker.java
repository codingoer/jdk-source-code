package com.sun.tools.internal.xjc.reader;

import com.sun.tools.internal.xjc.Options;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public final class ExtensionBindingChecker extends AbstractExtensionBindingChecker {
   private int count = 0;

   public ExtensionBindingChecker(String schemaLanguage, Options options, ErrorHandler handler) {
      super(schemaLanguage, options, handler);
   }

   private boolean needsToBePruned(String uri) {
      if (uri.equals(this.schemaLanguage)) {
         return false;
      } else if (uri.equals("http://java.sun.com/xml/ns/jaxb")) {
         return false;
      } else {
         return this.enabledExtensions.contains(uri) ? false : this.isRecognizableExtension(uri);
      }
   }

   public void startDocument() throws SAXException {
      super.startDocument();
      this.count = 0;
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      if (!this.isCutting()) {
         String v = atts.getValue("http://java.sun.com/xml/ns/jaxb", "extensionBindingPrefixes");
         if (v != null) {
            if (this.count != 0) {
               this.error(Messages.ERR_UNEXPECTED_EXTENSION_BINDING_PREFIXES.format());
            }

            if (!this.allowExtensions) {
               this.error(Messages.ERR_VENDOR_EXTENSION_DISALLOWED_IN_STRICT_MODE.format());
            }

            StringTokenizer tokens = new StringTokenizer(v);

            while(tokens.hasMoreTokens()) {
               String prefix = tokens.nextToken();
               String uri = this.nsSupport.getURI(prefix);
               if (uri == null) {
                  this.error(Messages.ERR_UNDECLARED_PREFIX.format(prefix));
               } else {
                  this.checkAndEnable(uri);
               }
            }
         }

         if (this.needsToBePruned(namespaceURI)) {
            if (this.isRecognizableExtension(namespaceURI)) {
               this.warning(Messages.ERR_SUPPORTED_EXTENSION_IGNORED.format(namespaceURI));
            }

            this.startCutting();
         } else {
            this.verifyTagName(namespaceURI, localName, qName);
         }
      }

      ++this.count;
      super.startElement(namespaceURI, localName, qName, atts);
   }
}
