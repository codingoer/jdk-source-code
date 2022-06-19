package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;
import com.sun.codemodel.internal.JType;

public interface XmlAnyElementWriter extends JAnnotationWriter {
   XmlAnyElementWriter value(Class var1);

   XmlAnyElementWriter value(JType var1);

   XmlAnyElementWriter lax(boolean var1);
}
