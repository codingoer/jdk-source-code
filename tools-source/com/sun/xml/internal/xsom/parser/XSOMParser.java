package com.sun.xml.internal.xsom.parser;

import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.impl.parser.NGCCRuntimeEx;
import com.sun.xml.internal.xsom.impl.parser.ParserContext;
import com.sun.xml.internal.xsom.impl.parser.state.Schema;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class XSOMParser {
   private EntityResolver entityResolver;
   private ErrorHandler userErrorHandler;
   private AnnotationParserFactory apFactory;
   private final ParserContext context;

   /** @deprecated */
   public XSOMParser() {
      this((XMLParser)(new JAXPParser()));
   }

   public XSOMParser(SAXParserFactory factory) {
      this((XMLParser)(new JAXPParser(factory)));
   }

   public XSOMParser(XMLParser parser) {
      this.context = new ParserContext(this, parser);
   }

   public void parse(InputStream is) throws SAXException {
      this.parse(new InputSource(is));
   }

   public void parse(Reader reader) throws SAXException {
      this.parse(new InputSource(reader));
   }

   public void parse(File schema) throws SAXException, IOException {
      this.parse(schema.toURL());
   }

   public void parse(URL url) throws SAXException {
      this.parse(url.toExternalForm());
   }

   public void parse(String systemId) throws SAXException {
      this.parse(new InputSource(systemId));
   }

   public void parse(InputSource source) throws SAXException {
      this.context.parse(source);
   }

   public ContentHandler getParserHandler() {
      NGCCRuntimeEx runtime = this.context.newNGCCRuntime();
      Schema s = new Schema(runtime, false, (String)null);
      runtime.setRootHandler(s);
      return runtime;
   }

   public XSSchemaSet getResult() throws SAXException {
      return this.context.getResult();
   }

   public Set getDocuments() {
      return new HashSet(this.context.parsedDocuments.keySet());
   }

   public EntityResolver getEntityResolver() {
      return this.entityResolver;
   }

   public void setEntityResolver(EntityResolver resolver) {
      this.entityResolver = resolver;
   }

   public ErrorHandler getErrorHandler() {
      return this.userErrorHandler;
   }

   public void setErrorHandler(ErrorHandler errorHandler) {
      this.userErrorHandler = errorHandler;
   }

   public void setAnnotationParser(final Class annParser) {
      this.setAnnotationParser(new AnnotationParserFactory() {
         public AnnotationParser create() {
            try {
               return (AnnotationParser)annParser.newInstance();
            } catch (InstantiationException var2) {
               throw new InstantiationError(var2.getMessage());
            } catch (IllegalAccessException var3) {
               throw new IllegalAccessError(var3.getMessage());
            }
         }
      });
   }

   public void setAnnotationParser(AnnotationParserFactory factory) {
      this.apFactory = factory;
   }

   public AnnotationParserFactory getAnnotationParserFactory() {
      return this.apFactory;
   }
}
