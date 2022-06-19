package com.sun.tools.internal.ws.util.xml;

import com.sun.tools.internal.ws.util.WSDLParseException;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;

public class XmlUtil extends com.sun.xml.internal.ws.util.xml.XmlUtil {
   public static boolean matchesTagNS(Element e, String tag, String nsURI) {
      try {
         return e.getLocalName().equals(tag) && e.getNamespaceURI().equals(nsURI);
      } catch (NullPointerException var4) {
         throw new WSDLParseException("null.namespace.found", new Object[]{e.getLocalName()});
      }
   }

   public static boolean matchesTagNS(Element e, QName name) {
      try {
         return e.getLocalName().equals(name.getLocalPart()) && e.getNamespaceURI().equals(name.getNamespaceURI());
      } catch (NullPointerException var3) {
         throw new WSDLParseException("null.namespace.found", new Object[]{e.getLocalName()});
      }
   }
}
