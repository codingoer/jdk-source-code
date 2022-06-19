package com.sun.tools.internal.ws.wsdl.document;

import javax.xml.namespace.QName;

public interface WSDLConstants {
   String NS_XMLNS = "http://www.w3.org/2000/xmlns/";
   String NS_WSDL = "http://schemas.xmlsoap.org/wsdl/";
   QName QNAME_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/", "binding");
   QName QNAME_DEFINITIONS = new QName("http://schemas.xmlsoap.org/wsdl/", "definitions");
   QName QNAME_DOCUMENTATION = new QName("http://schemas.xmlsoap.org/wsdl/", "documentation");
   QName QNAME_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/", "fault");
   QName QNAME_IMPORT = new QName("http://schemas.xmlsoap.org/wsdl/", "import");
   QName QNAME_INPUT = new QName("http://schemas.xmlsoap.org/wsdl/", "input");
   QName QNAME_MESSAGE = new QName("http://schemas.xmlsoap.org/wsdl/", "message");
   QName QNAME_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/", "operation");
   QName QNAME_OUTPUT = new QName("http://schemas.xmlsoap.org/wsdl/", "output");
   QName QNAME_PART = new QName("http://schemas.xmlsoap.org/wsdl/", "part");
   QName QNAME_PORT = new QName("http://schemas.xmlsoap.org/wsdl/", "port");
   QName QNAME_PORT_TYPE = new QName("http://schemas.xmlsoap.org/wsdl/", "portType");
   QName QNAME_SERVICE = new QName("http://schemas.xmlsoap.org/wsdl/", "service");
   QName QNAME_TYPES = new QName("http://schemas.xmlsoap.org/wsdl/", "types");
   QName QNAME_ATTR_ARRAY_TYPE = new QName("http://schemas.xmlsoap.org/wsdl/", "arrayType");
}
