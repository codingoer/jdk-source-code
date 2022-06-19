package com.sun.tools.doclets.internal.toolkit.builders;

import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LayoutParser extends DefaultHandler {
   private Map xmlElementsMap = new HashMap();
   private XMLNode currentNode;
   private final Configuration configuration;
   private String currentRoot;
   private boolean isParsing;

   private LayoutParser(Configuration var1) {
      this.configuration = var1;
   }

   public static LayoutParser getInstance(Configuration var0) {
      return new LayoutParser(var0);
   }

   public XMLNode parseXML(String var1) {
      if (this.xmlElementsMap.containsKey(var1)) {
         return (XMLNode)this.xmlElementsMap.get(var1);
      } else {
         try {
            this.currentRoot = var1;
            this.isParsing = false;
            SAXParserFactory var2 = SAXParserFactory.newInstance();
            SAXParser var3 = var2.newSAXParser();
            InputStream var4 = this.configuration.getBuilderXML();
            var3.parse(var4, this);
            return (XMLNode)this.xmlElementsMap.get(var1);
         } catch (Throwable var5) {
            var5.printStackTrace();
            throw new DocletAbortException(var5);
         }
      }
   }

   public void startElement(String var1, String var2, String var3, Attributes var4) throws SAXException {
      if (this.isParsing || var3.equals(this.currentRoot)) {
         this.isParsing = true;
         this.currentNode = new XMLNode(this.currentNode, var3);

         for(int var5 = 0; var5 < var4.getLength(); ++var5) {
            this.currentNode.attrs.put(var4.getLocalName(var5), var4.getValue(var5));
         }

         if (var3.equals(this.currentRoot)) {
            this.xmlElementsMap.put(var3, this.currentNode);
         }
      }

   }

   public void endElement(String var1, String var2, String var3) throws SAXException {
      if (this.isParsing) {
         this.currentNode = this.currentNode.parent;
         this.isParsing = !var3.equals(this.currentRoot);
      }
   }
}
