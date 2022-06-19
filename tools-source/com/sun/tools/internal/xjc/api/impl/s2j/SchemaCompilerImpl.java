package com.sun.tools.internal.xjc.api.impl.s2j;

import com.sun.codemodel.internal.JCodeModel;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.SAXParseException2;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.ModelLoader;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.api.ClassNameAllocator;
import com.sun.tools.internal.xjc.api.ErrorListener;
import com.sun.tools.internal.xjc.api.SchemaCompiler;
import com.sun.tools.internal.xjc.api.SpecVersion;
import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.tools.internal.xjc.reader.internalizer.DOMForest;
import com.sun.tools.internal.xjc.reader.internalizer.SCDBasedBindingSet;
import com.sun.tools.internal.xjc.reader.xmlschema.parser.LSInputSAXWrapper;
import com.sun.tools.internal.xjc.reader.xmlschema.parser.XMLSchemaInternalizationLogic;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import com.sun.xml.internal.xsom.XSSchemaSet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

public final class SchemaCompilerImpl extends ErrorReceiver implements SchemaCompiler {
   private ErrorListener errorListener;
   protected final Options opts = new Options();
   @NotNull
   protected DOMForest forest;
   private boolean hadError;
   private static boolean NO_CORRECTNESS_CHECK = false;

   public SchemaCompilerImpl() {
      this.opts.compatibilityMode = 2;
      this.resetSchema();
      if (System.getProperty("xjc-api.test") != null) {
         this.opts.debugMode = true;
         this.opts.verbose = true;
      }

   }

   @NotNull
   public Options getOptions() {
      return this.opts;
   }

   public ContentHandler getParserHandler(String systemId) {
      return this.forest.getParserHandler(systemId, true);
   }

   public void parseSchema(String systemId, Element element) {
      this.checkAbsoluteness(systemId);

      try {
         DOMScanner scanner = new DOMScanner();
         LocatorImpl loc = new LocatorImpl();
         loc.setSystemId(systemId);
         scanner.setLocator(loc);
         scanner.setContentHandler(this.getParserHandler(systemId));
         scanner.scan(element);
      } catch (SAXException var5) {
         this.fatalError(new SAXParseException2(var5.getMessage(), (String)null, systemId, -1, -1, var5));
      }

   }

   public void parseSchema(InputSource source) {
      this.checkAbsoluteness(source.getSystemId());

      try {
         this.forest.parse(source, true);
      } catch (SAXException var3) {
         var3.printStackTrace();
      }

   }

   public void setTargetVersion(SpecVersion version) {
      if (version == null) {
         version = SpecVersion.LATEST;
      }

      this.opts.target = version;
   }

   public void parseSchema(String systemId, XMLStreamReader reader) throws XMLStreamException {
      this.checkAbsoluteness(systemId);
      this.forest.parse(systemId, reader, true);
   }

   private void checkAbsoluteness(String systemId) {
      try {
         new URL(systemId);
      } catch (MalformedURLException var5) {
         try {
            new URI(systemId);
         } catch (URISyntaxException var4) {
            throw new IllegalArgumentException("system ID '" + systemId + "' isn't absolute", var4);
         }
      }

   }

   public void setEntityResolver(EntityResolver entityResolver) {
      this.forest.setEntityResolver(entityResolver);
      this.opts.entityResolver = entityResolver;
   }

   public void setDefaultPackageName(String packageName) {
      this.opts.defaultPackage2 = packageName;
   }

   public void forcePackageName(String packageName) {
      this.opts.defaultPackage = packageName;
   }

   public void setClassNameAllocator(ClassNameAllocator allocator) {
      this.opts.classNameAllocator = allocator;
   }

   public void resetSchema() {
      this.forest = new DOMForest(new XMLSchemaInternalizationLogic(), this.opts);
      this.forest.setErrorHandler(this);
      this.forest.setEntityResolver(this.opts.entityResolver);
   }

   public JAXBModelImpl bind() {
      InputSource[] var1 = this.opts.getBindFiles();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         InputSource is = var1[var3];
         this.parseSchema(is);
      }

      SCDBasedBindingSet scdBasedBindingSet = this.forest.transform(this.opts.isExtensionMode());
      if (!NO_CORRECTNESS_CHECK) {
         SchemaFactory sf = XmlFactory.createSchemaFactory("http://www.w3.org/2001/XMLSchema", this.opts.disableXmlSecurity);
         if (this.opts.entityResolver != null) {
            sf.setResourceResolver(new LSResourceResolver() {
               public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
                  try {
                     InputSource is = SchemaCompilerImpl.this.opts.entityResolver.resolveEntity(namespaceURI, systemId);
                     return is == null ? null : new LSInputSAXWrapper(is);
                  } catch (SAXException var7) {
                     return null;
                  } catch (IOException var8) {
                     return null;
                  }
               }
            });
         }

         sf.setErrorHandler(new DowngradingErrorHandler(this));
         this.forest.weakSchemaCorrectnessCheck(sf);
         if (this.hadError) {
            return null;
         }
      }

      JCodeModel codeModel = new JCodeModel();
      ModelLoader gl = new ModelLoader(this.opts, codeModel, this);

      try {
         XSSchemaSet result = gl.createXSOM(this.forest, scdBasedBindingSet);
         if (result == null) {
            return null;
         } else {
            Model model = gl.annotateXMLSchema(result);
            if (model == null) {
               return null;
            } else if (this.hadError) {
               return null;
            } else {
               model.setPackageLevelAnnotations(this.opts.packageLevelAnnotations);
               Outline context = model.generateCode(this.opts, this);
               if (context == null) {
                  return null;
               } else {
                  return this.hadError ? null : new JAXBModelImpl(context);
               }
            }
         }
      } catch (SAXException var7) {
         return null;
      }
   }

   public void setErrorListener(ErrorListener errorListener) {
      this.errorListener = errorListener;
   }

   public void info(SAXParseException exception) {
      if (this.errorListener != null) {
         this.errorListener.info(exception);
      }

   }

   public void warning(SAXParseException exception) {
      if (this.errorListener != null) {
         this.errorListener.warning(exception);
      }

   }

   public void error(SAXParseException exception) {
      this.hadError = true;
      if (this.errorListener != null) {
         this.errorListener.error(exception);
      }

   }

   public void fatalError(SAXParseException exception) {
      this.hadError = true;
      if (this.errorListener != null) {
         this.errorListener.fatalError(exception);
      }

   }

   static {
      try {
         NO_CORRECTNESS_CHECK = Boolean.getBoolean(SchemaCompilerImpl.class.getName() + ".noCorrectnessCheck");
      } catch (Throwable var1) {
      }

   }
}
