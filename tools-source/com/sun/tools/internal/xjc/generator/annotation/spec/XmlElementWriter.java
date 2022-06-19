package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;
import com.sun.codemodel.internal.JType;

public interface XmlElementWriter extends JAnnotationWriter {
   XmlElementWriter name(String var1);

   XmlElementWriter type(Class var1);

   XmlElementWriter type(JType var1);

   XmlElementWriter namespace(String var1);

   XmlElementWriter defaultValue(String var1);

   XmlElementWriter required(boolean var1);

   XmlElementWriter nillable(boolean var1);
}
