package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement(
   name = "dom"
)
public class BIDom extends AbstractDeclarationImpl {
   @XmlAttribute
   String type;
   public static final QName NAME = new QName("http://java.sun.com/xml/ns/jaxb", "dom");

   public final QName getName() {
      return NAME;
   }
}
