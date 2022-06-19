package com.sun.xml.internal.xsom.impl.parser;

import org.xml.sax.SAXException;

public interface Patch {
   void run() throws SAXException;
}
