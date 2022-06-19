package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement(
   name = "substitutable",
   namespace = "http://java.sun.com/xml/ns/jaxb/xjc"
)
public final class BIXSubstitutable extends AbstractDeclarationImpl {
   public static final QName NAME = new QName("http://java.sun.com/xml/ns/jaxb/xjc", "substitutable");

   public final QName getName() {
      return NAME;
   }
}
