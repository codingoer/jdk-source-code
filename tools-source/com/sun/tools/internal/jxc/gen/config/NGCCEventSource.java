package com.sun.tools.internal.jxc.gen.config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface NGCCEventSource {
   int replace(NGCCEventReceiver var1, NGCCEventReceiver var2);

   void sendEnterElement(int var1, String var2, String var3, String var4, Attributes var5) throws SAXException;

   void sendLeaveElement(int var1, String var2, String var3, String var4) throws SAXException;

   void sendEnterAttribute(int var1, String var2, String var3, String var4) throws SAXException;

   void sendLeaveAttribute(int var1, String var2, String var3, String var4) throws SAXException;

   void sendText(int var1, String var2) throws SAXException;
}
