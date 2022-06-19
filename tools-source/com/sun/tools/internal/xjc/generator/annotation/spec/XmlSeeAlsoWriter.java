package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;
import com.sun.codemodel.internal.JType;

public interface XmlSeeAlsoWriter extends JAnnotationWriter {
   XmlSeeAlsoWriter value(Class var1);

   XmlSeeAlsoWriter value(JType var1);
}
