package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;
import javax.xml.bind.annotation.XmlNsForm;

public interface XmlSchemaWriter extends JAnnotationWriter {
   XmlSchemaWriter location(String var1);

   XmlSchemaWriter namespace(String var1);

   XmlNsWriter xmlns();

   XmlSchemaWriter elementFormDefault(XmlNsForm var1);

   XmlSchemaWriter attributeFormDefault(XmlNsForm var1);
}
