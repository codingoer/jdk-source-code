package com.sun.tools.internal.xjc.reader.internalizer;

import com.sun.istack.internal.SAXParseException2;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.XMLFilterImpl;

public abstract class AbstractReferenceFinderImpl extends XMLFilterImpl {
   protected final DOMForest parent;
   private Locator locator;

   protected AbstractReferenceFinderImpl(DOMForest _parent) {
      this.parent = _parent;
   }

   protected abstract String findExternalResource(String var1, String var2, Attributes var3);

   public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      super.startElement(namespaceURI, localName, qName, atts);
      String relativeRef = this.findExternalResource(namespaceURI, localName, atts);
      if (relativeRef != null) {
         String ref;
         try {
            String lsi = this.locator.getSystemId();
            URI relRefURI = new URI(relativeRef);
            if (relRefURI.isAbsolute()) {
               ref = relativeRef;
            } else if (lsi.startsWith("jar:")) {
               int bangIdx = lsi.indexOf(33);
               if (bangIdx > 0) {
                  ref = lsi.substring(0, bangIdx + 1) + (new URI(lsi.substring(bangIdx + 1))).resolve(new URI(relativeRef)).toString();
               } else {
                  ref = relativeRef;
               }
            } else {
               ref = (new URI(lsi)).resolve(new URI(relativeRef)).toString();
            }

            if (this.parent != null) {
               this.parent.parse(ref, false);
            }

         } catch (URISyntaxException var10) {
            ref = var10.getMessage();
            if ((new File(relativeRef)).exists()) {
               ref = Messages.format("ERR_FILENAME_IS_NOT_URI") + ' ' + ref;
            }

            SAXParseException spe = new SAXParseException2(Messages.format("AbstractReferenceFinderImpl.UnableToParse", relativeRef, ref), this.locator, var10);
            this.fatalError(spe);
            throw spe;
         } catch (IOException var11) {
            SAXParseException spe = new SAXParseException2(Messages.format("AbstractReferenceFinderImpl.UnableToParse", relativeRef, var11.getMessage()), this.locator, var11);
            this.fatalError(spe);
            throw spe;
         }
      }
   }

   public void setDocumentLocator(Locator locator) {
      super.setDocumentLocator(locator);
      this.locator = locator;
   }
}
