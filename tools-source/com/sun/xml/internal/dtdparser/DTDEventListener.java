package com.sun.xml.internal.dtdparser;

import java.util.EventListener;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public interface DTDEventListener extends EventListener {
   short CONTENT_MODEL_EMPTY = 0;
   short CONTENT_MODEL_ANY = 1;
   short CONTENT_MODEL_MIXED = 2;
   short CONTENT_MODEL_CHILDREN = 3;
   short USE_NORMAL = 0;
   short USE_IMPLIED = 1;
   short USE_FIXED = 2;
   short USE_REQUIRED = 3;
   short CHOICE = 0;
   short SEQUENCE = 1;
   short OCCURENCE_ZERO_OR_MORE = 0;
   short OCCURENCE_ONE_OR_MORE = 1;
   short OCCURENCE_ZERO_OR_ONE = 2;
   short OCCURENCE_ONCE = 3;

   void setDocumentLocator(Locator var1);

   void processingInstruction(String var1, String var2) throws SAXException;

   void notationDecl(String var1, String var2, String var3) throws SAXException;

   void unparsedEntityDecl(String var1, String var2, String var3, String var4) throws SAXException;

   void internalGeneralEntityDecl(String var1, String var2) throws SAXException;

   void externalGeneralEntityDecl(String var1, String var2, String var3) throws SAXException;

   void internalParameterEntityDecl(String var1, String var2) throws SAXException;

   void externalParameterEntityDecl(String var1, String var2, String var3) throws SAXException;

   void startDTD(InputEntity var1) throws SAXException;

   void endDTD() throws SAXException;

   void comment(String var1) throws SAXException;

   void characters(char[] var1, int var2, int var3) throws SAXException;

   void ignorableWhitespace(char[] var1, int var2, int var3) throws SAXException;

   void startCDATA() throws SAXException;

   void endCDATA() throws SAXException;

   void fatalError(SAXParseException var1) throws SAXException;

   void error(SAXParseException var1) throws SAXException;

   void warning(SAXParseException var1) throws SAXException;

   void startContentModel(String var1, short var2) throws SAXException;

   void endContentModel(String var1, short var2) throws SAXException;

   void attributeDecl(String var1, String var2, String var3, String[] var4, short var5, String var6) throws SAXException;

   void childElement(String var1, short var2) throws SAXException;

   void mixedElement(String var1) throws SAXException;

   void startModelGroup() throws SAXException;

   void endModelGroup(short var1) throws SAXException;

   void connector(short var1) throws SAXException;
}
