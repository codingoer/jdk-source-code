package com.sun.tools.internal.ws.wsdl.document.soap;

import com.sun.tools.internal.ws.wsdl.framework.ExtensionImpl;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class SOAPOperation extends ExtensionImpl {
   private String _soapAction;
   private SOAPStyle _style;

   public SOAPOperation(Locator locator) {
      super(locator);
   }

   public QName getElementName() {
      return SOAPConstants.QNAME_OPERATION;
   }

   public String getSOAPAction() {
      return this._soapAction;
   }

   public void setSOAPAction(String s) {
      this._soapAction = s;
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
