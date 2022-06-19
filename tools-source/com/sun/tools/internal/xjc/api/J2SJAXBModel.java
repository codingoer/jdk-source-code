package com.sun.tools.internal.xjc.api;

import java.io.IOException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

public interface J2SJAXBModel extends JAXBModel {
   QName getXmlTypeName(Reference var1);

   void generateSchema(SchemaOutputResolver var1, ErrorListener var2) throws IOException;

   void generateEpisodeFile(Result var1);
}
