package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;

public interface XmlNsWriter extends JAnnotationWriter {
   XmlNsWriter prefix(String var1);

   XmlNsWriter namespaceURI(String var1);
}
