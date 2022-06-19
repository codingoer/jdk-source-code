package com.sun.tools.internal.xjc.api;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.tools.internal.xjc.Plugin;
import java.util.Collection;
import java.util.List;
import javax.xml.namespace.QName;

public interface S2JJAXBModel extends JAXBModel {
   Mapping get(QName var1);

   List getAllObjectFactories();

   Collection getMappings();

   TypeAndAnnotation getJavaType(QName var1);

   JCodeModel generateCode(Plugin[] var1, ErrorListener var2);
}
