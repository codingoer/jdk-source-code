package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;
import com.sun.codemodel.internal.JType;

public interface XmlSchemaTypeWriter extends JAnnotationWriter {
   XmlSchemaTypeWriter name(String var1);

   XmlSchemaTypeWriter type(Class var1);

   XmlSchemaTypeWriter type(JType var1);

   XmlSchemaTypeWriter namespace(String var1);
}
