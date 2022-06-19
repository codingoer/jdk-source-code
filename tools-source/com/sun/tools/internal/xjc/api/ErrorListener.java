package com.sun.tools.internal.xjc.api;

import org.xml.sax.SAXParseException;

public interface ErrorListener extends com.sun.xml.internal.bind.api.ErrorListener {
   void error(SAXParseException var1);

   void fatalError(SAXParseException var1);

   void warning(SAXParseException var1);

   void info(SAXParseException var1);
}
