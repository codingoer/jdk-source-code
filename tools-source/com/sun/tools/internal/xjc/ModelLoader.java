package com.sun.tools.internal.xjc;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.reader.ExtensionBindingChecker;
import com.sun.tools.internal.xjc.reader.dtd.TDTDReader;
import com.sun.tools.internal.xjc.reader.internalizer.DOMForest;
import com.sun.tools.internal.xjc.reader.internalizer.DOMForestScanner;
import com.sun.tools.internal.xjc.reader.internalizer.InternalizationLogic;
import com.sun.tools.internal.xjc.reader.internalizer.SCDBasedBindingSet;
import com.sun.tools.internal.xjc.reader.internalizer.VersionChecker;
import com.sun.tools.internal.xjc.reader.relaxng.RELAXNGCompiler;
import com.sun.tools.internal.xjc.reader.relaxng.RELAXNGInternalizationLogic;
import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.AnnotationParserFactoryImpl;
import com.sun.tools.internal.xjc.reader.xmlschema.parser.CustomizationContextChecker;
import com.sun.tools.internal.xjc.reader.xmlschema.parser.IncorrectNamespaceURIChecker;
import com.sun.tools.internal.xjc.reader.xmlschema.parser.SchemaConstraintChecker;
import com.sun.tools.internal.xjc.reader.xmlschema.parser.XMLSchemaInternalizationLogic;
import com.sun.tools.internal.xjc.util.ErrorReceiverFilter;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import com.sun.xml.internal.rngom.ast.builder.SchemaBuilder;
import com.sun.xml.internal.rngom.ast.util.CheckingSchemaBuilder;
import com.sun.xml.internal.rngom.digested.DPattern;
import com.sun.xml.internal.rngom.digested.DSchemaBuilderImpl;
import com.sun.xml.internal.rngom.parse.IllegalSchemaException;
import com.sun.xml.internal.rngom.parse.Parseable;
import com.sun.xml.internal.rngom.parse.compact.CompactParseable;
import com.sun.xml.internal.rngom.parse.xml.SAXParseable;
import com.sun.xml.internal.rngom.xml.sax.XMLReaderCreator;
import com.sun.xml.internal.xsom.XSSchemaSet;
import com.sun.xml.internal.xsom.parser.AnnotationParserFactory;
import com.sun.xml.internal.xsom.parser.JAXPParser;
import com.sun.xml.internal.xsom.parser.XMLParser;
import com.sun.xml.internal.xsom.parser.XSOMParser;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public final class ModelLoader {
   private final Options opt;
   private final ErrorReceiverFilter errorReceiver;
   private final JCodeModel codeModel;
   private SCDBasedBindingSet scdBasedBindingSet;

   public static Model load(Options opt, JCodeModel codeModel, ErrorReceiver er) {
      return (new ModelLoader(opt, codeModel, er)).load();
   }

   public ModelLoader(Options _opt, JCodeModel _codeModel, ErrorReceiver er) {
      this.opt = _opt;
      this.codeModel = _codeModel;
      this.errorReceiver = new ErrorReceiverFilter(er);
   }

   private Model load() {
      if (!this.sanityCheck()) {
         return null;
      } else {
         try {
            Model grammar;
            switch (this.opt.getSchemaLanguage()) {
               case DTD:
                  InputSource bindFile = null;
                  if (this.opt.getBindFiles().length > 0) {
                     bindFile = this.opt.getBindFiles()[0];
                  }

                  if (bindFile == null) {
                     bindFile = new InputSource(new StringReader("<?xml version='1.0'?><xml-java-binding-schema><options package='" + (this.opt.defaultPackage == null ? "generated" : this.opt.defaultPackage) + "'/></xml-java-binding-schema>"));
                  }

                  this.checkTooManySchemaErrors();
                  grammar = this.loadDTD(this.opt.getGrammars()[0], bindFile);
                  break;
               case RELAXNG:
                  this.checkTooManySchemaErrors();
                  grammar = this.loadRELAXNG();
                  break;
               case RELAXNG_COMPACT:
                  this.checkTooManySchemaErrors();
                  grammar = this.loadRELAXNGCompact();
                  break;
               case WSDL:
                  grammar = this.annotateXMLSchema(this.loadWSDL());
                  break;
               case XMLSCHEMA:
                  grammar = this.annotateXMLSchema(this.loadXMLSchema());
                  break;
               default:
                  throw new AssertionError();
            }

            if (this.errorReceiver.hadError()) {
               grammar = null;
            } else {
               grammar.setPackageLevelAnnotations(this.opt.packageLevelAnnotations);
            }

            return grammar;
         } catch (SAXException var3) {
            if (this.opt.verbose) {
               if (var3.getException() != null) {
                  var3.getException().printStackTrace();
               } else {
                  var3.printStackTrace();
               }
            }

            return null;
         } catch (AbortException var4) {
            return null;
         }
      }
   }

   private boolean sanityCheck() {
      if (this.opt.getSchemaLanguage() == Language.XMLSCHEMA) {
         Language guess = this.opt.guessSchemaLanguage();
         String[] msg = null;
         switch (guess) {
            case DTD:
               msg = new String[]{"DTD", "-dtd"};
               break;
            case RELAXNG:
               msg = new String[]{"RELAX NG", "-relaxng"};
               break;
            case RELAXNG_COMPACT:
               msg = new String[]{"RELAX NG compact syntax", "-relaxng-compact"};
               break;
            case WSDL:
               msg = new String[]{"WSDL", "-wsdl"};
         }

         if (msg != null) {
            this.errorReceiver.warning((Locator)null, Messages.format("Driver.ExperimentalLanguageWarning", msg[0], msg[1]));
         }
      }

      return true;
   }

   private void checkTooManySchemaErrors() {
      if (this.opt.getGrammars().length != 1) {
         this.errorReceiver.error((Locator)null, Messages.format("ModelLoader.TooManySchema"));
      }

   }

   private Model loadDTD(InputSource source, InputSource bindFile) {
      return TDTDReader.parse(source, bindFile, this.errorReceiver, this.opt);
   }

   public DOMForest buildDOMForest(InternalizationLogic logic) throws SAXException {
      DOMForest forest = new DOMForest(logic, this.opt);
      forest.setErrorHandler(this.errorReceiver);
      if (this.opt.entityResolver != null) {
         forest.setEntityResolver(this.opt.entityResolver);
      }

      InputSource[] var3 = this.opt.getGrammars();
      int var4 = var3.length;

      int var5;
      InputSource value;
      for(var5 = 0; var5 < var4; ++var5) {
         value = var3[var5];
         this.errorReceiver.pollAbort();
         forest.parse(value, true);
      }

      var3 = this.opt.getBindFiles();
      var4 = var3.length;

      for(var5 = 0; var5 < var4; ++var5) {
         value = var3[var5];
         this.errorReceiver.pollAbort();
         Document dom = forest.parse(value, true);
         if (dom != null) {
            Element root = dom.getDocumentElement();
            if (!this.fixNull(root.getNamespaceURI()).equals("http://java.sun.com/xml/ns/jaxb") || !root.getLocalName().equals("bindings")) {
               this.errorReceiver.error(new SAXParseException(Messages.format("Driver.NotABindingFile", root.getNamespaceURI(), root.getLocalName()), (String)null, value.getSystemId(), -1, -1));
            }
         }
      }

      this.scdBasedBindingSet = forest.transform(this.opt.isExtensionMode());
      return forest;
   }

   private String fixNull(String s) {
      return s == null ? "" : s;
   }

   public XSSchemaSet loadXMLSchema() throws SAXException {
      if (this.opt.strictCheck && !SchemaConstraintChecker.check(this.opt.getGrammars(), this.errorReceiver, this.opt.entityResolver, this.opt.disableXmlSecurity)) {
         return null;
      } else {
         if (this.opt.getBindFiles().length == 0) {
            try {
               return this.createXSOMSpeculative();
            } catch (SpeculationFailure var2) {
            }
         }

         DOMForest forest = this.buildDOMForest(new XMLSchemaInternalizationLogic());
         return this.createXSOM(forest, this.scdBasedBindingSet);
      }
   }

   private XSSchemaSet loadWSDL() throws SAXException {
      DOMForest forest = this.buildDOMForest(new XMLSchemaInternalizationLogic());
      DOMForestScanner scanner = new DOMForestScanner(forest);
      XSOMParser xsomParser = this.createXSOMParser(forest);
      InputSource[] var4 = this.opt.getGrammars();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         InputSource grammar = var4[var6];
         Document wsdlDom = forest.get(grammar.getSystemId());
         if (wsdlDom == null) {
            String systemId = Options.normalizeSystemId(grammar.getSystemId());
            if (forest.get(systemId) != null) {
               grammar.setSystemId(systemId);
               wsdlDom = forest.get(grammar.getSystemId());
            }
         }

         NodeList schemas = wsdlDom.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");

         for(int i = 0; i < schemas.getLength(); ++i) {
            scanner.scan((Element)schemas.item(i), xsomParser.getParserHandler());
         }
      }

      return xsomParser.getResult();
   }

   public Model annotateXMLSchema(XSSchemaSet xs) {
      return xs == null ? null : BGMBuilder.build(xs, this.codeModel, this.errorReceiver, this.opt);
   }

   public XSOMParser createXSOMParser(XMLParser parser) {
      XSOMParser reader = new XSOMParser(new XMLSchemaParser(parser));
      reader.setAnnotationParser((AnnotationParserFactory)(new AnnotationParserFactoryImpl(this.opt)));
      reader.setErrorHandler(this.errorReceiver);
      reader.setEntityResolver(this.opt.entityResolver);
      return reader;
   }

   public XSOMParser createXSOMParser(final DOMForest forest) {
      XSOMParser p = this.createXSOMParser(forest.createParser());
      p.setEntityResolver(new EntityResolver() {
         public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (systemId != null && forest.get(systemId) != null) {
               return new InputSource(systemId);
            } else {
               return ModelLoader.this.opt.entityResolver != null ? ModelLoader.this.opt.entityResolver.resolveEntity(publicId, systemId) : null;
            }
         }
      });
      return p;
   }

   private XSSchemaSet createXSOMSpeculative() throws SAXException, SpeculationFailure {
      XMLParser parser = new XMLParser() {
         private final JAXPParser base;

         {
            this.base = new JAXPParser(XmlFactory.createParserFactory(ModelLoader.this.opt.disableXmlSecurity));
         }

         public void parse(InputSource source, ContentHandler handler, ErrorHandler errorHandler, EntityResolver entityResolver) throws SAXException, IOException {
            handler = this.wrapBy(new SpeculationChecker(), handler);
            handler = this.wrapBy(new VersionChecker((ContentHandler)null, ModelLoader.this.errorReceiver, entityResolver), handler);
            this.base.parse(source, handler, errorHandler, entityResolver);
         }

         private ContentHandler wrapBy(XMLFilterImpl filter, ContentHandler handler) {
            filter.setContentHandler(handler);
            return filter;
         }
      };
      XSOMParser reader = this.createXSOMParser(parser);
      InputSource[] var3 = this.opt.getGrammars();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         InputSource value = var3[var5];
         reader.parse(value);
      }

      return reader.getResult();
   }

   public XSSchemaSet createXSOM(DOMForest forest, SCDBasedBindingSet scdBasedBindingSet) throws SAXException {
      XSOMParser reader = this.createXSOMParser(forest);
      Iterator var4 = forest.getRootDocuments().iterator();

      while(var4.hasNext()) {
         String systemId = (String)var4.next();
         this.errorReceiver.pollAbort();
         Document dom = forest.get(systemId);
         if (!dom.getDocumentElement().getNamespaceURI().equals("http://java.sun.com/xml/ns/jaxb")) {
            reader.parse(systemId);
         }
      }

      XSSchemaSet result = reader.getResult();
      if (result != null) {
         scdBasedBindingSet.apply(result, this.errorReceiver);
      }

      return result;
   }

   private Model loadRELAXNG() throws SAXException {
      final DOMForest forest = this.buildDOMForest(new RELAXNGInternalizationLogic());
      XMLReaderCreator xrc = new XMLReaderCreator() {
         public XMLReader createXMLReader() {
            XMLFilter buffer = new XMLFilterImpl() {
               public void parse(InputSource source) throws IOException, SAXException {
                  forest.createParser().parse(source, this, this, this);
               }
            };
            XMLFilter f = new ExtensionBindingChecker("http://relaxng.org/ns/structure/1.0", ModelLoader.this.opt, ModelLoader.this.errorReceiver);
            f.setParent(buffer);
            f.setEntityResolver(ModelLoader.this.opt.entityResolver);
            return f;
         }
      };
      Parseable p = new SAXParseable(this.opt.getGrammars()[0], this.errorReceiver, xrc);
      return this.loadRELAXNG(p);
   }

   private Model loadRELAXNGCompact() {
      if (this.opt.getBindFiles().length > 0) {
         this.errorReceiver.error(new SAXParseException(Messages.format("ModelLoader.BindingFileNotSupportedForRNC"), (Locator)null));
      }

      Parseable p = new CompactParseable(this.opt.getGrammars()[0], this.errorReceiver);
      return this.loadRELAXNG(p);
   }

   private Model loadRELAXNG(Parseable p) {
      SchemaBuilder sb = new CheckingSchemaBuilder(new DSchemaBuilderImpl(), this.errorReceiver);

      try {
         DPattern out = (DPattern)p.parse(sb);
         return RELAXNGCompiler.build(out, this.codeModel, this.opt);
      } catch (IllegalSchemaException var4) {
         this.errorReceiver.error(var4.getMessage(), var4);
         return null;
      }
   }

   private static final class SpeculationChecker extends XMLFilterImpl {
      private SpeculationChecker() {
      }

      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
         if (localName.equals("bindings") && uri.equals("http://java.sun.com/xml/ns/jaxb")) {
            throw new SpeculationFailure();
         } else {
            super.startElement(uri, localName, qName, attributes);
         }
      }

      // $FF: synthetic method
      SpeculationChecker(Object x0) {
         this();
      }
   }

   private static final class SpeculationFailure extends Error {
      private SpeculationFailure() {
      }

      // $FF: synthetic method
      SpeculationFailure(Object x0) {
         this();
      }
   }

   private class XMLSchemaParser implements XMLParser {
      private final XMLParser baseParser;

      private XMLSchemaParser(XMLParser baseParser) {
         this.baseParser = baseParser;
      }

      public void parse(InputSource source, ContentHandler handler, ErrorHandler errorHandler, EntityResolver entityResolver) throws SAXException, IOException {
         handler = this.wrapBy(new ExtensionBindingChecker("http://www.w3.org/2001/XMLSchema", ModelLoader.this.opt, ModelLoader.this.errorReceiver), handler);
         handler = this.wrapBy(new IncorrectNamespaceURIChecker(ModelLoader.this.errorReceiver), handler);
         handler = this.wrapBy(new CustomizationContextChecker(ModelLoader.this.errorReceiver), handler);
         this.baseParser.parse(source, handler, errorHandler, entityResolver);
      }

      private ContentHandler wrapBy(XMLFilterImpl filter, ContentHandler handler) {
         filter.setContentHandler(handler);
         return filter;
      }

      // $FF: synthetic method
      XMLSchemaParser(XMLParser x1, Object x2) {
         this(x1);
      }
   }
}
