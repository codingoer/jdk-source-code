package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import org.w3c.dom.Element;
import org.xml.sax.Locator;

class DOMLocator {
   private static final String locationNamespace = "http://www.sun.com/xmlns/jaxb/dom-location";
   private static final String systemId = "systemid";
   private static final String column = "column";
   private static final String line = "line";

   public static void setLocationInfo(Element e, Locator loc) {
      e.setAttributeNS("http://www.sun.com/xmlns/jaxb/dom-location", "loc:systemid", loc.getSystemId());
      e.setAttributeNS("http://www.sun.com/xmlns/jaxb/dom-location", "loc:column", Integer.toString(loc.getLineNumber()));
      e.setAttributeNS("http://www.sun.com/xmlns/jaxb/dom-location", "loc:line", Integer.toString(loc.getColumnNumber()));
   }

   public static Locator getLocationInfo(final Element e) {
      return DOMUtil.getAttribute(e, "http://www.sun.com/xmlns/jaxb/dom-location", "systemid") == null ? null : new Locator() {
         public int getLineNumber() {
            return Integer.parseInt(DOMUtil.getAttribute(e, "http://www.sun.com/xmlns/jaxb/dom-location", "line"));
         }

         public int getColumnNumber() {
            return Integer.parseInt(DOMUtil.getAttribute(e, "http://www.sun.com/xmlns/jaxb/dom-location", "column"));
         }

         public String getSystemId() {
            return DOMUtil.getAttribute(e, "http://www.sun.com/xmlns/jaxb/dom-location", "systemid");
         }

         public String getPublicId() {
            return null;
         }
      };
   }
}
