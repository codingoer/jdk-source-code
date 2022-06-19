package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;

public interface XmlElementWrapperWriter extends JAnnotationWriter {
   XmlElementWrapperWriter name(String var1);

   XmlElementWrapperWriter namespace(String var1);

   XmlElementWrapperWriter required(boolean var1);

   XmlElementWrapperWriter nillable(boolean var1);
}
