package com.sun.xml.internal.xsom.impl.parser;

import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.impl.ElementDecl;
import com.sun.xml.internal.xsom.impl.SchemaImpl;
import com.sun.xml.internal.xsom.impl.SchemaSetImpl;
import com.sun.xml.internal.xsom.parser.AnnotationParserFactory;
import com.sun.xml.internal.xsom.parser.XMLParser;
import com.sun.xml.internal.xsom.parser.XSOMParser;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ParserContext {
   public final SchemaSetImpl schemaSet = new SchemaSetImpl();
   private final XSOMParser owner;
   final XMLParser parser;
   private final Vector patchers = new Vector();
   private final Vector errorCheckers = new Vector();
   public final Map parsedDocuments = new HashMap();
   private boolean hadError = false;
   final PatcherManager patcherManager = new PatcherManager() {
      public void addPatcher(Patch patch) {
         ParserContext.this.patchers.add(patch);
      }

      public void addErrorChecker(Patch patch) {
         ParserContext.this.errorCheckers.add(patch);
      }

      public void reportError(String msg, Locator src) throws SAXException {
         ParserContext.this.setErrorFlag();
         SAXParseException e = new SAXParseException(msg, src);
         if (ParserContext.this.errorHandler == null) {
            throw e;
         } else {
            ParserContext.this.errorHandler.error(e);
         }
      }
   };
   final ErrorHandler errorHandler = new ErrorHandler() {
      private ErrorHandler getErrorHandler() {
         return ParserContext.this.owner.getErrorHandler() == null ? ParserContext.this.noopHandler : ParserContext.this.owner.getErrorHandler();
      }

      public void warning(SAXParseException e) throws SAXException {
         this.getErrorHandler().warning(e);
      }

      public void error(SAXParseException e) throws SAXException {
         ParserContext.this.setErrorFlag();
         this.getErrorHandler().error(e);
      }

      public void fatalError(SAXParseException e) throws SAXException {
         ParserContext.this.setErrorFlag();
         this.getErrorHandler().fatalError(e);
      }
   };
   final ErrorHandler noopHandler = new ErrorHandler() {
      public void warning(SAXParseException e) {
      }

      public void error(SAXParseException e) {
      }

      public void fatalError(SAXParseException e) {
         ParserContext.this.setErrorFlag();
      }
   };

   public ParserContext(XSOMParser owner, XMLParser parser) {
      this.owner = owner;
      this.parser = parser;

      try {
         this.parse(new InputSource(ParserContext.class.getResource("datatypes.xsd").toExternalForm()));
         SchemaImpl xs = (SchemaImpl)this.schemaSet.getSchema("http://www.w3.org/2001/XMLSchema");
         xs.addSimpleType(this.schemaSet.anySimpleType, true);
         xs.addComplexType(this.schemaSet.anyType, true);
      } catch (SAXException var4) {
         if (var4.getException() != null) {
            var4.getException().printStackTrace();
         } else {
            var4.printStackTrace();
         }

         throw new InternalError();
      }
   }

   public EntityResolver getEntityResolver() {
      return this.owner.getEntityResolver();
   }

   public AnnotationParserFactory getAnnotationParserFactory() {
      return this.owner.getAnnotationParserFactory();
   }

   public void parse(InputSource source) throws SAXException {
      this.newNGCCRuntime().parseEntity(source, false, (String)null, (Locator)null);
   }

   public XSSchemaSet getResult() throws SAXException {
      Iterator itr = this.patchers.iterator();

      while(itr.hasNext()) {
         Patch patcher = (Patch)itr.next();
         patcher.run();
      }

      this.patchers.clear();
      itr = this.schemaSet.iterateElementDecls();

      while(itr.hasNext()) {
         ((ElementDecl)itr.next()).updateSubstitutabilityMap();
      }

      Iterator var4 = this.errorCheckers.iterator();

      while(var4.hasNext()) {
         Patch patcher = (Patch)var4.next();
         patcher.run();
      }

      this.errorCheckers.clear();
      return this.hadError ? null : this.schemaSet;
   }

   public NGCCRuntimeEx newNGCCRuntime() {
      return new NGCCRuntimeEx(this);
   }

   void setErrorFlag() {
      this.hadError = true;
   }
}
