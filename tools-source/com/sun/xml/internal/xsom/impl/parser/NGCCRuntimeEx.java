package com.sun.xml.internal.xsom.impl.parser;

import com.sun.xml.internal.xsom.XSDeclaration;
import com.sun.xml.internal.xsom.XSSimpleType;
import com.sun.xml.internal.xsom.XmlString;
import com.sun.xml.internal.xsom.impl.ForeignAttributesImpl;
import com.sun.xml.internal.xsom.impl.SchemaImpl;
import com.sun.xml.internal.xsom.impl.UName;
import com.sun.xml.internal.xsom.impl.parser.state.NGCCRuntime;
import com.sun.xml.internal.xsom.impl.parser.state.Schema;
import com.sun.xml.internal.xsom.impl.util.Uri;
import com.sun.xml.internal.xsom.parser.AnnotationParser;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Stack;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

public class NGCCRuntimeEx extends NGCCRuntime implements PatcherManager {
   public final ParserContext parser;
   public SchemaImpl currentSchema;
   public int finalDefault;
   public int blockDefault;
   public boolean elementFormDefault;
   public boolean attributeFormDefault;
   public boolean chameleonMode;
   private String documentSystemId;
   private final Stack elementNames;
   private final NGCCRuntimeEx referer;
   public SchemaDocumentImpl document;
   private Context currentContext;
   public static final String XMLSchemaNSURI = "http://www.w3.org/2001/XMLSchema";

   NGCCRuntimeEx(ParserContext _parser) {
      this(_parser, false, (NGCCRuntimeEx)null);
   }

   private NGCCRuntimeEx(ParserContext _parser, boolean chameleonMode, NGCCRuntimeEx referer) {
      this.finalDefault = 0;
      this.blockDefault = 0;
      this.elementFormDefault = false;
      this.attributeFormDefault = false;
      this.chameleonMode = false;
      this.elementNames = new Stack();
      this.currentContext = null;
      this.parser = _parser;
      this.chameleonMode = chameleonMode;
      this.referer = referer;
      this.currentContext = new Context("", "", (Context)null);
      this.currentContext = new Context("xml", "http://www.w3.org/XML/1998/namespace", this.currentContext);
   }

   public void checkDoubleDefError(XSDeclaration c) throws SAXException {
      if (c != null && !ignorableDuplicateComponent(c)) {
         this.reportError(Messages.format("DoubleDefinition", c.getName()));
         this.reportError(Messages.format("DoubleDefinition.Original"), c.getLocator());
      }
   }

   public static boolean ignorableDuplicateComponent(XSDeclaration c) {
      if (c.getTargetNamespace().equals("http://www.w3.org/2001/XMLSchema")) {
         if (c instanceof XSSimpleType) {
            return true;
         }

         if (c.isGlobal() && c.getName().equals("anyType")) {
            return true;
         }
      }

      return false;
   }

   public void addPatcher(Patch patcher) {
      this.parser.patcherManager.addPatcher(patcher);
   }

   public void addErrorChecker(Patch patcher) {
      this.parser.patcherManager.addErrorChecker(patcher);
   }

   public void reportError(String msg, Locator loc) throws SAXException {
      this.parser.patcherManager.reportError(msg, loc);
   }

   public void reportError(String msg) throws SAXException {
      this.reportError(msg, this.getLocator());
   }

   private InputSource resolveRelativeURL(String namespaceURI, String relativeUri) throws SAXException {
      try {
         String baseUri = this.getLocator().getSystemId();
         if (baseUri == null) {
            baseUri = this.documentSystemId;
         }

         EntityResolver er = this.parser.getEntityResolver();
         String systemId = null;
         if (relativeUri != null) {
            systemId = Uri.resolve(baseUri, relativeUri);
         }

         if (er != null) {
            InputSource is = er.resolveEntity(namespaceURI, systemId);
            if (is == null) {
               try {
                  String normalizedSystemId = URI.create(systemId).normalize().toASCIIString();
                  is = er.resolveEntity(namespaceURI, normalizedSystemId);
               } catch (Exception var8) {
               }
            }

            if (is != null) {
               return is;
            }
         }

         return systemId != null ? new InputSource(systemId) : null;
      } catch (IOException var9) {
         SAXParseException se = new SAXParseException(var9.getMessage(), this.getLocator(), var9);
         this.parser.errorHandler.error(se);
         return null;
      }
   }

   public void includeSchema(String schemaLocation) throws SAXException {
      NGCCRuntimeEx runtime = new NGCCRuntimeEx(this.parser, this.chameleonMode, this);
      runtime.currentSchema = this.currentSchema;
      runtime.blockDefault = this.blockDefault;
      runtime.finalDefault = this.finalDefault;
      if (schemaLocation == null) {
         SAXParseException e = new SAXParseException(Messages.format("MissingSchemaLocation"), this.getLocator());
         this.parser.errorHandler.fatalError(e);
         throw e;
      } else {
         runtime.parseEntity(this.resolveRelativeURL((String)null, schemaLocation), true, this.currentSchema.getTargetNamespace(), this.getLocator());
      }
   }

   public void importSchema(String ns, String schemaLocation) throws SAXException {
      NGCCRuntimeEx newRuntime = new NGCCRuntimeEx(this.parser, false, this);
      InputSource source = this.resolveRelativeURL(ns, schemaLocation);
      if (source != null) {
         newRuntime.parseEntity(source, false, ns, this.getLocator());
      }

   }

   public boolean hasAlreadyBeenRead() {
      if (this.documentSystemId != null && this.documentSystemId.startsWith("file:///")) {
         this.documentSystemId = "file:/" + this.documentSystemId.substring(8);
      }

      assert this.document == null;

      this.document = new SchemaDocumentImpl(this.currentSchema, this.documentSystemId);
      SchemaDocumentImpl existing = (SchemaDocumentImpl)this.parser.parsedDocuments.get(this.document);
      if (existing == null) {
         this.parser.parsedDocuments.put(this.document, this.document);
      } else {
         this.document = existing;
      }

      assert this.document != null;

      if (this.referer != null) {
         assert this.referer.document != null : "referer " + this.referer.documentSystemId + " has docIdentity==null";

         this.referer.document.references.add(this.document);
         this.document.referers.add(this.referer.document);
      }

      return existing != null;
   }

   public void parseEntity(InputSource source, boolean includeMode, String expectedNamespace, Locator importLocation) throws SAXException {
      this.documentSystemId = source.getSystemId();

      try {
         Schema s = new Schema(this, includeMode, expectedNamespace);
         this.setRootHandler(s);

         try {
            this.parser.parser.parse(source, this, this.getErrorHandler(), this.parser.getEntityResolver());
         } catch (IOException var8) {
            SAXParseException se = new SAXParseException(var8.toString(), importLocation, var8);
            this.parser.errorHandler.warning(se);
         }

      } catch (SAXException var9) {
         this.parser.setErrorFlag();
         throw var9;
      }
   }

   public AnnotationParser createAnnotationParser() {
      return this.parser.getAnnotationParserFactory() == null ? DefaultAnnotationParser.theInstance : this.parser.getAnnotationParserFactory().create();
   }

   public String getAnnotationContextElementName() {
      return (String)this.elementNames.get(this.elementNames.size() - 2);
   }

   public Locator copyLocator() {
      return new LocatorImpl(this.getLocator());
   }

   public ErrorHandler getErrorHandler() {
      return this.parser.errorHandler;
   }

   public void onEnterElementConsumed(String uri, String localName, String qname, Attributes atts) throws SAXException {
      super.onEnterElementConsumed(uri, localName, qname, atts);
      this.elementNames.push(localName);
   }

   public void onLeaveElementConsumed(String uri, String localName, String qname) throws SAXException {
      super.onLeaveElementConsumed(uri, localName, qname);
      this.elementNames.pop();
   }

   public ValidationContext createValidationContext() {
      return this.currentContext;
   }

   public XmlString createXmlString(String value) {
      return value == null ? null : new XmlString(value, this.createValidationContext());
   }

   public void startPrefixMapping(String prefix, String uri) throws SAXException {
      super.startPrefixMapping(prefix, uri);
      this.currentContext = new Context(prefix, uri, this.currentContext);
   }

   public void endPrefixMapping(String prefix) throws SAXException {
      super.endPrefixMapping(prefix);
      this.currentContext = this.currentContext.previous;
   }

   public UName parseUName(String qname) throws SAXException {
      int idx = qname.indexOf(58);
      String prefix;
      if (idx < 0) {
         prefix = this.resolveNamespacePrefix("");
         if (prefix.equals("") && this.chameleonMode) {
            prefix = this.currentSchema.getTargetNamespace();
         }

         return new UName(prefix, qname, qname);
      } else {
         prefix = qname.substring(0, idx);
         String uri = this.currentContext.resolveNamespacePrefix(prefix);
         if (uri == null) {
            this.reportError(Messages.format("UndefinedPrefix", prefix));
            uri = "undefined";
         }

         return new UName(uri, qname.substring(idx + 1), qname);
      }
   }

   public boolean parseBoolean(String v) {
      if (v == null) {
         return false;
      } else {
         v = v.trim();
         return v.equals("true") || v.equals("1");
      }
   }

   protected void unexpectedX(String token) throws SAXException {
      SAXParseException e = new SAXParseException(MessageFormat.format("Unexpected {0} appears at line {1} column {2}", token, this.getLocator().getLineNumber(), this.getLocator().getColumnNumber()), this.getLocator());
      this.parser.errorHandler.fatalError(e);
      throw e;
   }

   public ForeignAttributesImpl parseForeignAttributes(ForeignAttributesImpl next) {
      ForeignAttributesImpl impl = new ForeignAttributesImpl(this.createValidationContext(), this.copyLocator(), next);
      Attributes atts = this.getCurrentAttributes();

      for(int i = 0; i < atts.getLength(); ++i) {
         if (atts.getURI(i).length() > 0) {
            impl.addAttribute(atts.getURI(i), atts.getLocalName(i), atts.getQName(i), atts.getType(i), atts.getValue(i));
         }
      }

      return impl;
   }

   private static class Context implements ValidationContext {
      private final String prefix;
      private final String uri;
      private final Context previous;

      Context(String _prefix, String _uri, Context _context) {
         this.previous = _context;
         this.prefix = _prefix;
         this.uri = _uri;
      }

      public String resolveNamespacePrefix(String p) {
         if (p.equals(this.prefix)) {
            return this.uri;
         } else {
            return this.previous == null ? null : this.previous.resolveNamespacePrefix(p);
         }
      }

      public String getBaseUri() {
         return null;
      }

      public boolean isNotation(String arg0) {
         return false;
      }

      public boolean isUnparsedEntity(String arg0) {
         return false;
      }
   }
}
