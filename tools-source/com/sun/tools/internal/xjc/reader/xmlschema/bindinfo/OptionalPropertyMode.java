package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import javax.xml.bind.annotation.XmlEnumValue;

public enum OptionalPropertyMode {
   @XmlEnumValue("primitive")
   PRIMITIVE,
   @XmlEnumValue("wrapper")
   WRAPPER,
   @XmlEnumValue("isSet")
   ISSET;
}
