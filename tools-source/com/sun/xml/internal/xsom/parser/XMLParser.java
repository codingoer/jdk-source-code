package com.sun.xml.internal.xsom.parser;

import java.io.IOException;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public interface XMLParser {
   void parse(InputSource var1, ContentHandler var2, ErrorHandler var3, EntityResolver var4) throws SAXException, IOException;
}
