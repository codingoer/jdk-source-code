package com.sun.tools.internal.ws.wsdl.document.soap;

import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class SOAPBinding extends ExtensionImpl {
   private String _transport;
   private SOAPStyle _style;

   public SOAPBinding(Locator locator) {
      super(locator);
      this._style = SOAPStyle.DOCUMENT;
   }

   public QName getElementName() {
      return SOAPConstants.QNAME_BINDING;
   }

   public String getTransport() {
      return this._transport;
   }

   public void setTransport(String s) {
      this._transport = s;
   }

   public SOAPStyle getStyle() {
      return this._style;
   }

   public void setStyle(SOAPStyle s) {
      this._style = s;
   }

   public boolean isDocument() {
      return this._style == SOAPStyle.DOCUMENT;
   }

   public boolean isRPC() {
      return this._style == SOAPStyle.RPC;
   }

   public void validateThis() {
   }
}
