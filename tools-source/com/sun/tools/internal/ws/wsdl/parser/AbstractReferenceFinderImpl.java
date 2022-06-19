package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.istack.internal.SAXParseException2;
import com.sun.tools.internal.ws.resources.WsdlMessages;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
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
         SAXParseException2 spe;
         try {
            assert this.locator != null;

            String lsi = this.locator.getSystemId();
            String ref;
            if (lsi.startsWith("jar:")) {
               int bangIdx = lsi.indexOf(33);
               if (bangIdx > 0) {
                  ref = (new URL(new URL(lsi), relativeRef)).toString();
               } else {
                  ref = relativeRef;
               }
            } else {
               ref = (new URI(lsi)).resolve(new URI(relativeRef)).toString();
            }

            this.parent.parse(ref, false);
         } catch (URISyntaxException var9) {
            spe = new SAXParseException2(WsdlMessages.ABSTRACT_REFERENCE_FINDER_IMPL_UNABLE_TO_PARSE(relativeRef, var9.getMessage()), this.locator, var9);
            this.fatalError(spe);
            throw spe;
         } catch (IOException var10) {
            spe = new SAXParseException2(WsdlMessages.ABSTRACT_REFERENCE_FINDER_IMPL_UNABLE_TO_PARSE(relativeRef, var10.getMessage()), this.locator, var10);
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
