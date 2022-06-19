package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLFilterImpl;

final class DomHandlerEx implements DomHandler {
   public ResultImpl createUnmarshaller(ValidationEventHandler errorHandler) {
      return new ResultImpl();
   }

   public DomAndLocation getElement(ResultImpl r) {
      return new DomAndLocation(((Document)r.s2d.getDOM()).getDocumentElement(), r.location);
   }

   public Source marshal(DomAndLocation domAndLocation, ValidationEventHandler errorHandler) {
      return new DOMSource(domAndLocation.element);
   }

   public static final class ResultImpl extends SAXResult {
      final SAX2DOMEx s2d;
      Locator location = null;

      ResultImpl() {
         try {
            DocumentBuilderFactory factory = XmlFactory.createDocumentBuilderFactory(false);
            this.s2d = new SAX2DOMEx(factory);
         } catch (ParserConfigurationException var2) {
            throw new AssertionError(var2);
         }

         XMLFilterImpl f = new XMLFilterImpl() {
            public void setDocumentLocator(Locator locator) {
               super.setDocumentLocator(locator);
               ResultImpl.this.location = new LocatorImpl(locator);
            }
         };
         f.setContentHandler(this.s2d);
         this.setHandler(f);
      }
   }

   public static final class DomAndLocation {
      public final Element element;
      public final Locator loc;

      public DomAndLocation(Element element, Locator loc) {
         this.element = element;
         this.loc = loc;
      }
   }
}
