package com.sun.tools.internal.xjc.generator.annotation.spec;

import com.sun.codemodel.internal.JAnnotationWriter;
import com.sun.codemodel.internal.JType;

public interface XmlElementDeclWriter extends JAnnotationWriter {
   XmlElementDeclWriter name(String var1);

   XmlElementDeclWriter scope(Class var1);

   XmlElementDeclWriter scope(JType var1);

   XmlElementDeclWriter namespace(String var1);

   XmlElementDeclWriter defaultValue(String var1);

   XmlElementDeclWriter substitutionHeadNamespace(String var1);

   XmlElementDeclWriter substitutionHeadName(String var1);
}
