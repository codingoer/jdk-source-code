package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.wsdl.document.soap.SOAP12Binding;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAP12Constants;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPBinding;
import java.util.Map;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public class SOAP12ExtensionHandler extends SOAPExtensionHandler {
   public SOAP12ExtensionHandler(Map extensionHandlerMap) {
      super(extensionHandlerMap);
   }

   public String getNamespaceURI() {
      return "http://schemas.xmlsoap.org/wsdl/soap12/";
   }

   protected QName getAddressQName() {
      return SOAP12Constants.QNAME_ADDRESS;
   }

   protected QName getBindingQName() {
      return SOAP12Constants.QNAME_BINDING;
   }

   protected SOAPBinding getSOAPBinding(Locator location) {
      return new SOAP12Binding(location);
   }

   protected QName getBodyQName() {
      return SOAP12Constants.QNAME_BODY;
   }

   protected QName getFaultQName() {
      return SOAP12Constants.QNAME_FAULT;
   }

   protected QName getHeaderfaultQName() {
      return SOAP12Constants.QNAME_HEADERFAULT;
   }

   protected QName getHeaderQName() {
      return SOAP12Constants.QNAME_HEADER;
   }

   protected QName getOperationQName() {
      return SOAP12Constants.QNAME_OPERATION;
   }
}
