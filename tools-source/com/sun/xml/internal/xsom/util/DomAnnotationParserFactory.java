package com.sun.xml.internal.xsom.util;

import com.sun.xml.internal.xsom.parser.AnnotationContext;
import com.sun.xml.internal.xsom.parser.AnnotationParser;
import com.sun.xml.internal.xsom.parser.AnnotationParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

public class DomAnnotationParserFactory implements AnnotationParserFactory {
   private static final ContextClassloaderLocal stf = new ContextClassloaderLocal() {
      protected SAXTransformerFactory initialValue() throws Exception {
         return (SAXTransformerFactory)SAXTransformerFactory.newInstance();
      }
   };

   public AnnotationParser create() {
      return new AnnotationParserImpl();
   }

   public AnnotationParser create(boolean disableSecureProcessing) {
      return new AnnotationParserImpl(disableSecureProcessing);
   }

   private static class AnnotationParserImpl extends AnnotationParser {
      private final TransformerHandler transformer;
      private DOMResult result;

      AnnotationParserImpl() {
         this(false);
      }

      AnnotationParserImpl(boolean disableSecureProcessing) {
         try {
            SAXTransformerFactory factory = (SAXTransformerFactory)DomAnnotationParserFactory.stf.get();
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", disableSecureProcessing);
            this.transformer = factory.newTransformerHandler();
         } catch (TransformerConfigurationException var3) {
            throw new Error(var3);
         }
      }

      public ContentHandler getContentHandler(AnnotationContext context, String parentElementName, ErrorHandler errorHandler, EntityResolver entityResolver) {
         this.result = new DOMResult();
         this.transformer.setResult(this.result);
         return this.transformer;
      }

      public Object getResult(Object existing) {
         Document dom = (Document)this.result.getNode();
         Element e = dom.getDocumentElement();
         if (existing instanceof Element) {
            Element prev = (Element)existing;
            Node anchor = e.getFirstChild();

            while(prev.getFirstChild() != null) {
               Node move = prev.getFirstChild();
               e.insertBefore(e.getOwnerDocument().adoptNode(move), anchor);
            }
         }

         return e;
      }
   }
}
