package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;
import com.sun.codemodel.internal.JType;

public interface XmlElementRefWriter extends JAnnotationWriter {
   XmlElementRefWriter name(String var1);

   XmlElementRefWriter type(Class var1);

   XmlElementRefWriter type(JType var1);

   XmlElementRefWriter namespace(String var1);

   XmlElementRefWriter required(boolean var1);
}
