package com.sun.xml.internal.xsom.impl.parser;

import com.sun.xml.internal.xsom.parser.AnnotationContext;
import com.sun.xml.internal.xsom.parser.AnnotationParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;

class DefaultAnnotationParser extends AnnotationParser {
   public static final AnnotationParser theInstance = new DefaultAnnotationParser();

   private DefaultAnnotationParser() {
   }

   public ContentHandler getContentHandler(AnnotationContext contest, String elementName, ErrorHandler errorHandler, EntityResolver entityResolver) {
      return new DefaultHandler();
   }

   public Object getResult(Object existing) {
      return null;
   }
}
