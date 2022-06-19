package com.sun.tools.internal.ws.wsdl.document.http;

import javax.xml.namespace.QName;

public interface HTTPConstants {
   String NS_WSDL_HTTP = "http://schemas.xmlsoap.org/wsdl/http/";
   QName QNAME_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/http/", "address");
   QName QNAME_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/http/", "binding");
   QName QNAME_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/http/", "operation");
   QName QNAME_URL_ENCODED = new QName("http://schemas.xmlsoap.org/wsdl/http/", "urlEncoded");
   QName QNAME_URL_REPLACEMENT = new QName("http://schemas.xmlsoap.org/wsdl/http/", "urlReplacement");
}
