package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement(
   name = "typesafeEnumMember"
)
public class BIEnumMember extends AbstractDeclarationImpl {
   @XmlAttribute
   public final String name = null;
   @XmlElement
   public final String javadoc = null;
   public static final QName NAME = new QName("http://java.sun.com/xml/ns/jaxb", "typesafeEnumMember");

   protected BIEnumMember() {
   }

   public QName getName() {
      return NAME;
   }
}
