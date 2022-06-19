package com.sun.tools.internal.xjc.reader.internalizer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.XMLStreamReaderToContentHandler;
import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.Options;
import com.sun.tools.internal.xjc.util.ErrorReceiverFilter;
import com.sun.xml.internal.bind.marshaller.DataWriter;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import com.sun.xml.internal.xsom.parser.JAXPParser;
import com.sun.xml.internal.xsom.parser.XMLParser;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public final class DOMForest {
   private final Map core = new HashMap();
   private final Set rootDocuments = new HashSet();
   public final LocatorTable locatorTable = new LocatorTable();
   public final Set outerMostBindings = new HashSet();
   private EntityResolver entityResolver = null;
   private ErrorReceiver errorReceiver = null;
   protected final InternalizationLogic logic;
   private final SAXParserFactory parserFactory;
   private final DocumentBuilder documentBuilder;
   private final Options options;

   public DOMForest(SAXParserFactory parserFactory, DocumentBuilder documentBuilder, InternalizationLogic logic) {
      this.parserFactory = parserFactory;
      this.documentBuilder = documentBuilder;
      this.logic = logic;
      this.options = null;
   }

   public DOMForest(InternalizationLogic logic, Options opt) {
      if (opt == null) {
         throw new AssertionError("Options object null");
      } else {
         this.options = opt;

         try {
            DocumentBuilderFactory dbf = XmlFactory.createDocumentBuilderFactory(opt.disableXmlSecurity);
            this.documentBuilder = dbf.newDocumentBuilder();
            this.parserFactory = XmlFactory.createParserFactory(opt.disableXmlSecurity);
         } catch (ParserConfigurationException var4) {
            throw new AssertionError(var4);
         }

         this.logic = logic;
      }
   }

   public Document get(String systemId) {
      Document doc = (Document)this.core.get(systemId);
      if (doc == null && systemId.startsWith("file:/") && !systemId.startsWith("file://")) {
         doc = (Document)this.core.get("file://" + systemId.substring(5));
      }

      if (doc == null && systemId.startsWith("file:")) {
         String systemPath = this.getPath(systemId);
         Iterator var4 = this.core.keySet().iterator();

         while(var4.hasNext()) {
            String key = (String)var4.next();
            if (key.startsWith("file:") && this.getPath(key).equalsIgnoreCase(systemPath)) {
               doc = (Document)this.core.get(key);
               break;
            }
         }
      }

      return doc;
   }

   private String getPath(String key) {
      for(key = key.substring(5); key.length() > 0 && key.charAt(0) == '/'; key = key.substring(1)) {
      }

      return key;
   }

   public Set getRootDocuments() {
      return Collections.unmodifiableSet(this.rootDocuments);
   }

   public Document getOneDocument() {
      Iterator var1 = this.core.values().iterator();

      Document dom;
      do {
         if (!var1.hasNext()) {
            throw new AssertionError();
         }

         dom = (Document)var1.next();
      } while(dom.getDocumentElement().getNamespaceURI().equals("http://java.sun.com/xml/ns/jaxb"));

      return dom;
   }

   public boolean checkSchemaCorrectness(ErrorReceiver errorHandler) {
      try {
         boolean disableXmlSecurity = false;
         if (this.options != null) {
            disableXmlSecurity = this.options.disableXmlSecurity;
         }

         SchemaFactory sf = XmlFactory.createSchemaFactory("http://www.w3.org/2001/XMLSchema", disableXmlSecurity);
         ErrorReceiverFilter filter = new ErrorReceiverFilter(errorHandler);
         sf.setErrorHandler(filter);
         Set roots = this.getRootDocuments();
         Source[] sources = new Source[roots.size()];
         int i = 0;

         String root;
         for(Iterator var8 = roots.iterator(); var8.hasNext(); sources[i++] = new DOMSource(this.get(root), root)) {
            root = (String)var8.next();
         }

         sf.newSchema(sources);
         return !filter.hadError();
      } catch (SAXException var10) {
         return false;
      }
   }

   public String getSystemId(Document dom) {
      Iterator var2 = this.core.entrySet().iterator();

      Map.Entry e;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         e = (Map.Entry)var2.next();
      } while(e.getValue() != dom);

      return (String)e.getKey();
   }

   public Document parse(InputSource source, boolean root) throws SAXException {
      if (source.getSystemId() == null) {
         throw new IllegalArgumentException();
      } else {
         return this.parse(source.getSystemId(), source, root);
      }
   }

   public Document parse(String systemId, boolean root) throws SAXException, IOException {
      systemId = Options.normalizeSystemId(systemId);
      if (this.core.containsKey(systemId)) {
         return (Document)this.core.get(systemId);
      } else {
         InputSource is = null;
         if (this.entityResolver != null) {
            is = this.entityResolver.resolveEntity((String)null, systemId);
         }

         if (is == null) {
            is = new InputSource(systemId);
         }

         return this.parse(systemId, is, root);
      }
   }

   private ContentHandler getParserHandler(Document dom) {
      ContentHandler handler = new DOMBuilder(dom, this.locatorTable, this.outerMostBindings);
      ContentHandler handler = new WhitespaceStripper(handler, this.errorReceiver, this.entityResolver);
      ContentHandler handler = new VersionChecker(handler, this.errorReceiver, this.entityResolver);
      XMLFilterImpl f = this.logic.createExternalReferenceFinder(this);
      f.setContentHandler(handler);
      if (this.errorReceiver != null) {
         f.setErrorHandler(this.errorReceiver);
      }

      if (this.entityResolver != null) {
         f.setEntityResolver(this.entityResolver);
      }

      return f;
   }

   public Handler getParserHandler(String systemId, boolean root) {
      final Document dom = this.documentBuilder.newDocument();
      this.core.put(systemId, dom);
      if (root) {
         this.rootDocuments.add(systemId);
      }

      ContentHandler handler = this.getParserHandler(dom);
      HandlerImpl x = new HandlerImpl() {
         public Document getDocument() {
            return dom;
         }
      };
      x.setContentHandler(handler);
      return x;
   }

   public Document parse(String systemId, InputSource inputSource, boolean root) throws SAXException {
      Document dom = this.documentBuilder.newDocument();
      systemId = Options.normalizeSystemId(systemId);
      this.core.put(systemId, dom);
      if (root) {
         this.rootDocuments.add(systemId);
      }

      try {
         XMLReader reader = this.parserFactory.newSAXParser().getXMLReader();
         reader.setContentHandler(this.getParserHandler(dom));
         if (this.errorReceiver != null) {
            reader.setErrorHandler(this.errorReceiver);
         }

         if (this.entityResolver != null) {
            reader.setEntityResolver(this.entityResolver);
         }

         reader.parse(inputSource);
         return dom;
      } catch (ParserConfigurationException var6) {
         this.errorReceiver.error((String)var6.getMessage(), (Exception)var6);
         this.core.remove(systemId);
         this.rootDocuments.remove(systemId);
         return null;
      } catch (IOException var7) {
         this.errorReceiver.error((String)Messages.format("DOMFOREST_INPUTSOURCE_IOEXCEPTION", systemId, var7.toString()), (Exception)var7);
         this.core.remove(systemId);
         this.rootDocuments.remove(systemId);
         return null;
      }
   }

   public Document parse(String systemId, XMLStreamReader parser, boolean root) throws XMLStreamException {
      Document dom = this.documentBuilder.newDocument();
      systemId = Options.normalizeSystemId(systemId);
      if (root) {
         this.rootDocuments.add(systemId);
      }

      if (systemId == null) {
         throw new IllegalArgumentException("system id cannot be null");
      } else {
         this.core.put(systemId, dom);
         (new XMLStreamReaderToContentHandler(parser, this.getParserHandler(dom), false, false)).bridge();
         return dom;
      }
   }

   public SCDBasedBindingSet transform(boolean enableSCD) {
      return Internalizer.transform(this, enableSCD, this.options.disableXmlSecurity);
   }

   public void weakSchemaCorrectnessCheck(SchemaFactory sf) {
      List sources = new ArrayList();
      Iterator var3 = this.getRootDocuments().iterator();

      while(var3.hasNext()) {
         String systemId = (String)var3.next();
         Document dom = this.get(systemId);
         if (!dom.getDocumentElement().getNamespaceURI().equals("http://java.sun.com/xml/ns/jaxb")) {
            SAXSource ss = this.createSAXSource(systemId);

            try {
               ss.getXMLReader().setFeature("http://xml.org/sax/features/namespace-prefixes", true);
            } catch (SAXException var11) {
               throw new AssertionError(var11);
            }

            sources.add(ss);
         }
      }

      try {
         XmlFactory.allowExternalAccess(sf, "file,http", this.options.disableXmlSecurity).newSchema((Source[])sources.toArray(new SAXSource[0]));
      } catch (SAXException var9) {
      } catch (RuntimeException var10) {
         RuntimeException re = var10;

         try {
            sf.getErrorHandler().warning(new SAXParseException(Messages.format("ERR_GENERAL_SCHEMA_CORRECTNESS_ERROR", re.getMessage()), (String)null, (String)null, -1, -1, re));
         } catch (SAXException var8) {
         }
      }

   }

   @NotNull
   public SAXSource createSAXSource(String systemId) {
      ContentHandlerNamespacePrefixAdapter reader = new ContentHandlerNamespacePrefixAdapter(new XMLFilterImpl() {
         public void parse(InputSource input) throws SAXException, IOException {
            DOMForest.this.createParser().parse(input, this, this, this);
         }

         public void parse(String systemId) throws SAXException, IOException {
            this.parse(new InputSource(systemId));
         }
      });
      return new SAXSource(reader, new InputSource(systemId));
   }

   public XMLParser createParser() {
      return new DOMForestParser(this, new JAXPParser(XmlFactory.createParserFactory(this.options.disableXmlSecurity)));
   }

   public EntityResolver getEntityResolver() {
      return this.entityResolver;
   }

   public void setEntityResolver(EntityResolver entityResolver) {
      this.entityResolver = entityResolver;
   }

   public ErrorReceiver getErrorHandler() {
      return this.errorReceiver;
   }

   public void setErrorHandler(ErrorReceiver errorHandler) {
      this.errorReceiver = errorHandler;
   }

   public Document[] listDocuments() {
      return (Document[])this.core.values().toArray(new Document[this.core.size()]);
   }

   public String[] listSystemIDs() {
      return (String[])this.core.keySet().toArray(new String[this.core.keySet().size()]);
   }

   public void dump(OutputStream out) throws IOException {
      try {
         boolean disableXmlSecurity = false;
         if (this.options != null) {
            disableXmlSecurity = this.options.disableXmlSecurity;
         }

         TransformerFactory tf = XmlFactory.createTransformerFactory(disableXmlSecurity);
         Transformer it = tf.newTransformer();
         Iterator var5 = this.core.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry e = (Map.Entry)var5.next();
            out.write(("---<< " + (String)e.getKey() + '\n').getBytes());
            DataWriter dw = new DataWriter(new OutputStreamWriter(out), (String)null);
            dw.setIndentStep("  ");
            it.transform(new DOMSource((Node)e.getValue()), new SAXResult(dw));
            out.write("\n\n\n".getBytes());
         }
      } catch (TransformerException var8) {
         var8.printStackTrace();
      }

   }

   private abstract static class HandlerImpl extends XMLFilterImpl implements Handler {
      private HandlerImpl() {
      }

      // $FF: synthetic method
      HandlerImpl(Object x0) {
         this();
      }
   }

   public interface Handler extends ContentHandler {
      Document getDocument();
   }
}
