package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;
import com.sun.codemodel.internal.JType;

public interface XmlEnumWriter extends JAnnotationWriter {
   XmlEnumWriter value(Class var1);

   XmlEnumWriter value(JType var1);
}
