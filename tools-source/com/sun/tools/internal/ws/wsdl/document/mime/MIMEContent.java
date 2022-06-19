package com.sun.tools.internal.ws.wsdl.document.mime;

import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class MIMEContent extends ExtensionImpl {
   private String _part;
   private String _type;

   public MIMEContent(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return MIMEConstants.QNAME_CONTENT;
   }

   public String getPart() {
      return this._part;
   }

   public void setPart(String s) {
      this._part = s;
   }

   public String getType() {
      return this._type;
   }

   public void setType(String s) {
      this._type = s;
   }

   public void validateThis() {
   }
}
