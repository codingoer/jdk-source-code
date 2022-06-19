package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum EnumMemberMode {
   @XmlEnumValue("skipGeneration")
   SKIP,
   @XmlEnumValue("generateError")
   ERROR,
   @XmlEnumValue("generateName")
   GENERATE;

   public EnumMemberMode getModeWithEnum() {
      return this == SKIP ? ERROR : this;
   }
}
