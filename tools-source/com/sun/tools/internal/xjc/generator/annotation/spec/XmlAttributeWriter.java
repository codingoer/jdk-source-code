package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;

public interface XmlAttributeWriter extends JAnnotationWriter {
   XmlAttributeWriter name(String var1);

   XmlAttributeWriter namespace(String var1);

   XmlAttributeWriter required(boolean var1);
}
