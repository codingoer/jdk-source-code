package com.sun.tools.internal.ws.wsdl.document.mime;

import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class MIMEXml extends ExtensionImpl {
   private String _part;

   public MIMEXml(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return MIMEConstants.QNAME_MIME_XML;
   }

   public String getPart() {
      return this._part;
   }

   public void setPart(String s) {
      this._part = s;
   }

   public void validateThis() {
   }
}
