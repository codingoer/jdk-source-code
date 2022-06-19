package com.sun.tools.internal.xjc.reader;

import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.Plugin;
import com.sun.tools.internal.xjc.util.SubtreeCutter;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.NamespaceSupport;

public abstract class AbstractExtensionBindingChecker extends SubtreeCutter {
   protected final NamespaceSupport nsSupport = new NamespaceSupport();
   protected final Set enabledExtensions = new HashSet();
   private final Set recognizableExtensions = new HashSet();
   private Locator locator;
   protected final String schemaLanguage;
   protected final boolean allowExtensions;
   private final Options options;

   public AbstractExtensionBindingChecker(String schemaLanguage, Options options, ErrorHandler handler) {
      this.schemaLanguage = schemaLanguage;
      this.allowExtensions = options.compatibilityMode != 1;
      this.options = options;
      this.setErrorHandler(handler);
      Iterator var4 = options.getAllPlugins().iterator();

      while(var4.hasNext()) {
         Plugin plugin = (Plugin)var4.next();
         this.recognizableExtensions.addAll(plugin.getCustomizationURIs());
      }

      this.recognizableExtensions.add("http://java.sun.com/xml/ns/jaxb/xjc");
   }

   protected final void checkAndEnable(String uri) throws SAXException {
      if (!this.isRecognizableExtension(uri)) {
         String nearest = EditDistance.findNearest(uri, this.recognizableExtensions);
         this.error(Messages.ERR_UNSUPPORTED_EXTENSION.format(uri, nearest));
      } else if (!this.isSupportedExtension(uri)) {
         Plugin owner = null;
         Iterator var3 = this.options.getAllPlugins().iterator();

         while(var3.hasNext()) {
            Plugin p = (Plugin)var3.next();
            if (p.getCustomizationURIs().contains(uri)) {
               owner = p;
               break;
            }
         }

         if (owner != null) {
            this.error(Messages.ERR_PLUGIN_NOT_ENABLED.format(owner.getOptionName(), uri));
         } else {
            this.error(Messages.ERR_UNSUPPORTED_EXTENSION.format(uri));
         }
      }

      this.enabledExtensions.add(uri);
   }

   protected final void verifyTagName(String namespaceURI, String localName, String qName) throws SAXException {
      if (this.options.pluginURIs.contains(namespaceURI)) {
         boolean correct = false;
         Iterator var5 = this.options.activePlugins.iterator();

         while(var5.hasNext()) {
            Plugin p = (Plugin)var5.next();
            if (p.isCustomizationTagName(namespaceURI, localName)) {
               correct = true;
               break;
            }
         }

         if (!correct) {
            this.error(Messages.ERR_ILLEGAL_CUSTOMIZATION_TAGNAME.format(qName));
            this.startCutting();
         }
      }

   }

   protected final boolean isSupportedExtension(String namespaceUri) {
      return namespaceUri.equals("http://java.sun.com/xml/ns/jaxb/xjc") || this.options.pluginURIs.contains(namespaceUri);
   }

   protected final boolean isRecognizableExtension(String namespaceUri) {
      return this.recognizableExtensions.contains(namespaceUri);
   }

   public void setDocumentLocator(Locator locator) {
      super.setDocumentLocator(locator);
      this.locator = locator;
   }

   public void startDocument() throws SAXException {
      super.startDocument();
      this.nsSupport.reset();
      this.enabledExtensions.clear();
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      if (!"http://www.w3.org/XML/1998/namespace".equals(uri)) {
         super.startPrefixMapping(prefix, uri);
         this.nsSupport.pushContext();
         this.nsSupport.declarePrefix(prefix, uri);
      }
   }

   public void endPrefixMapping(String prefix) throws SAXException {
      if (!"xml".equals(prefix)) {
         super.endPrefixMapping(prefix);
         this.nsSupport.popContext();
      }
   }

   protected final SAXParseException error(String msg) throws SAXException {
      SAXParseException spe = new SAXParseException(msg, this.locator);
      this.getErrorHandler().error(spe);
      return spe;
   }

   protected final void warning(String msg) throws SAXException {
      SAXParseException spe = new SAXParseException(msg, this.locator);
      this.getErrorHandler().warning(spe);
   }
}
