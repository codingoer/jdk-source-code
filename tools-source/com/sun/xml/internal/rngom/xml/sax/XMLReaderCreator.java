package com.sun.xml.internal.rngom.xml.sax;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public interface XMLReaderCreator {
   XMLReader createXMLReader() throws SAXException;
}
