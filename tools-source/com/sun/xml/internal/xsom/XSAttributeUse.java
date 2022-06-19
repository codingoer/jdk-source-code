package com.sun.xml.internal.xsom;

public interface XSAttributeUse extends XSComponent {
   boolean isRequired();

   XSAttributeDecl getDecl();

   XmlString getDefaultValue();

   XmlString getFixedValue();
}
