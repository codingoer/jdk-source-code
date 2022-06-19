package com.sun.tools.internal.ws.wsdl.document.jaxws;

import javax.xml.namespace.QName;

public interface JAXWSBindingsConstants {
   String NS_JAXWS_BINDINGS = "http://java.sun.com/xml/ns/jaxws";
   String NS_JAXB_BINDINGS = "http://java.sun.com/xml/ns/jaxb";
   String NS_XJC_BINDINGS = "http://java.sun.com/xml/ns/jaxb/xjc";
   QName JAXWS_BINDINGS = new QName("http://java.sun.com/xml/ns/jaxws", "bindings");
   String WSDL_LOCATION_ATTR = "wsdlLocation";
   String NODE_ATTR = "node";
   String VERSION_ATTR = "version";
   QName PACKAGE = new QName("http://java.sun.com/xml/ns/jaxws", "package");
   String NAME_ATTR = "name";
   QName JAVADOC = new QName("http://java.sun.com/xml/ns/jaxws", "javadoc");
   QName ENABLE_WRAPPER_STYLE = new QName("http://java.sun.com/xml/ns/jaxws", "enableWrapperStyle");
   QName ENABLE_ASYNC_MAPPING = new QName("http://java.sun.com/xml/ns/jaxws", "enableAsyncMapping");
   QName ENABLE_ADDITIONAL_SOAPHEADER_MAPPING = new QName("http://java.sun.com/xml/ns/jaxws", "enableAdditionalSOAPHeaderMapping");
   QName ENABLE_MIME_CONTENT = new QName("http://java.sun.com/xml/ns/jaxws", "enableMIMEContent");
   QName PROVIDER = new QName("http://java.sun.com/xml/ns/jaxws", "provider");
   QName CLASS = new QName("http://java.sun.com/xml/ns/jaxws", "class");
   QName METHOD = new QName("http://java.sun.com/xml/ns/jaxws", "method");
   QName PARAMETER = new QName("http://java.sun.com/xml/ns/jaxws", "parameter");
   String PART_ATTR = "part";
   String ELEMENT_ATTR = "childElementName";
   QName EXCEPTION = new QName("http://java.sun.com/xml/ns/jaxws", "exception");
   QName JAXB_BINDINGS = new QName("http://java.sun.com/xml/ns/jaxb", "bindings");
   String JAXB_BINDING_VERSION = "2.0";
   QName XSD_APPINFO = new QName("http://www.w3.org/2001/XMLSchema", "appinfo");
   QName XSD_ANNOTATION = new QName("http://www.w3.org/2001/XMLSchema", "annotation");
}
