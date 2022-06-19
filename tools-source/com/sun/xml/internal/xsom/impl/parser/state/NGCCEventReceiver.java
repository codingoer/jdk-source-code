package com.sun.xml.internal.xsom.impl.parser.state;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface NGCCEventReceiver {
   void enterElement(String var1, String var2, String var3, Attributes var4) throws SAXException;

   void leaveElement(String var1, String var2, String var3) throws SAXException;

   void text(String var1) throws SAXException;

   void enterAttribute(String var1, String var2, String var3) throws SAXException;

   void leaveAttribute(String var1, String var2, String var3) throws SAXException;
}
