package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;
import com.sun.codemodel.internal.JType;

public interface XmlJavaTypeAdapterWriter extends JAnnotationWriter {
   XmlJavaTypeAdapterWriter type(Class var1);

   XmlJavaTypeAdapterWriter type(JType var1);

   XmlJavaTypeAdapterWriter value(Class var1);

   XmlJavaTypeAdapterWriter value(JType var1);
}
