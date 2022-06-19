package com.sun.xml.internal.xsom;

public interface XSAttributeDecl extends XSDeclaration {
   XSSimpleType getType();

   XmlString getDefaultValue();

   XmlString getFixedValue();
}
