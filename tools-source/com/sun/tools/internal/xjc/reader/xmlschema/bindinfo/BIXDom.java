package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(
   name = "dom",
   namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
)
public class BIXDom extends BIDom {
   @XmlAttribute
   String type = "w3c";
}
