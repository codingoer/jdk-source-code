package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;
import com.sun.codemodel.internal.JType;

public interface XmlTypeWriter extends JAnnotationWriter {
   XmlTypeWriter name(String var1);

   XmlTypeWriter namespace(String var1);

   XmlTypeWriter propOrder(String var1);

   XmlTypeWriter factoryClass(Class var1);

   XmlTypeWriter factoryClass(JType var1);

   XmlTypeWriter factoryMethod(String var1);
}
