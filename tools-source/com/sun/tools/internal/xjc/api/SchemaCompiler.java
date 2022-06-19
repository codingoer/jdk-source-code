package com.sun.tools.internal.xjc.api;

import com.sun.istack.internal.NotNull;
import com.sun.tools.internal.xjc.Options;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public interface SchemaCompiler {
   ContentHandler getParserHandler(String var1);

   void parseSchema(InputSource var1);

   void setTargetVersion(SpecVersion var1);

   void parseSchema(String var1, Element var2);

   void parseSchema(String var1, XMLStreamReader var2) throws XMLStreamException;

   void setErrorListener(ErrorListener var1);

   void setEntityResolver(EntityResolver var1);

   void setDefaultPackageName(String var1);

   void forcePackageName(String var1);

   void setClassNameAllocator(ClassNameAllocator var1);

   void resetSchema();

   S2JJAXBModel bind();

   /** @deprecated */
   @NotNull
   Options getOptions();
}
