package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.xjc.reader.internalizer.LocatorTable;
import com.sun.xml.internal.bind.marshaller.DataWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class DOMForest {
   protected final Set rootDocuments = new HashSet();
   protected final Set externalReferences = new HashSet();
   protected final Map core = new HashMap();
   protected final ErrorReceiver errorReceiver;
   private final DocumentBuilder documentBuilder;
   private final SAXParserFactory parserFactory;
   protected final List inlinedSchemaElements = new ArrayList();
   public final LocatorTable locatorTable = new LocatorTable();
   protected final EntityResolver entityResolver;
   public final Set outerMostBindings = new HashSet();
   protected final InternalizationLogic logic;
   protected final WsimportOptions options;
   protected Map resolvedCache = new HashMap();

   public DOMForest(InternalizationLogic logic, @NotNull EntityResolver entityResolver, WsimportOptions options, ErrorReceiver errReceiver) {
      this.options = options;
      this.entityResolver = entityResolver;
      this.errorReceiver = errReceiver;
      this.logic = logic;
      boolean disableXmlSecurity = options == null ? false : options.disableXmlSecurity;
      DocumentBuilderFactory dbf = XmlUtil.newDocumentBuilderFactory(disableXmlSecurity);
      this.parserFactory = XmlUtil.newSAXParserFactory(disableXmlSecurity);

      try {
         this.documentBuilder = dbf.newDocumentBuilder();
      } catch (ParserConfigurationException var8) {
         throw new AssertionError(var8);
      }
   }

   public List getInlinedSchemaElement() {
      return this.inlinedSchemaElements;
   }

   @NotNull
   public Document parse(InputSource source, boolean root) throws SAXException, IOException {
      if (source.getSystemId() == null) {
         throw new IllegalArgumentException();
      } else {
         return this.parse(source.getSystemId(), source, root);
      }
   }

   public Document parse(String systemId, boolean root) throws SAXException, IOException {
      systemId = this.normalizeSystemId(systemId);
      InputSource is = null;
      is = this.entityResolver.resolveEntity((String)null, systemId);
      if (is == null) {
         is = new InputSource(systemId);
      } else {
         this.resolvedCache.put(systemId, is.getSystemId());
         systemId = is.getSystemId();
      }

      if (this.core.containsKey(systemId)) {
         return (Document)this.core.get(systemId);
      } else {
         if (!root) {
            this.addExternalReferences(systemId);
         }

         return this.parse(systemId, is, root);
      }
   }

   public Map getReferencedEntityMap() {
      return this.resolvedCache;
   }

   @NotNull
   private Document parse(String systemId, InputSource inputSource, boolean root) throws SAXException, IOException {
      Document dom = this.documentBuilder.newDocument();
      systemId = this.normalizeSystemId(systemId);
      this.core.put(systemId, dom);
      dom.setDocumentURI(systemId);
      if (root) {
         this.rootDocuments.add(systemId);
      }

      try {
         XMLReader reader = this.createReader(dom);
         InputStream is = null;
         if (inputSource.getByteStream() == null) {
            inputSource = this.entityResolver.resolveEntity((String)null, systemId);
         }

         reader.parse(inputSource);
         Element doc = dom.getDocumentElement();
         if (doc == null) {
            return null;
         }

         NodeList schemas = doc.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");

         for(int i = 0; i < schemas.getLength(); ++i) {
            this.inlinedSchemaElements.add((Element)schemas.item(i));
         }
      } catch (ParserConfigurationException var10) {
         this.errorReceiver.error((Exception)var10);
         throw new SAXException(var10.getMessage());
      }

      this.resolvedCache.put(systemId, dom.getDocumentURI());
      return dom;
   }

   public void addExternalReferences(String ref) {
      if (!this.externalReferences.contains(ref)) {
         this.externalReferences.add(ref);
      }

   }

   public Set getExternalReferences() {
      return this.externalReferences;
   }

   private XMLReader createReader(Document dom) throws SAXException, ParserConfigurationException {
      XMLReader reader = this.parserFactory.newSAXParser().getXMLReader();
      DOMBuilder dombuilder = new DOMBuilder(dom, this.locatorTable, this.outerMostBindings);

      try {
         reader.setProperty("http://xml.org/sax/properties/lexical-handler", dombuilder);
      } catch (SAXException var6) {
         this.errorReceiver.debug(var6.getMessage());
      }

      ContentHandler handler = new WhitespaceStripper(dombuilder, this.errorReceiver, this.entityResolver);
      ContentHandler handler = new VersionChecker(handler, this.errorReceiver, this.entityResolver);
      XMLFilterImpl f = this.logic.createExternalReferenceFinder(this);
      f.setContentHandler(handler);
      if (this.errorReceiver != null) {
         f.setErrorHandler(this.errorReceiver);
      }

      f.setEntityResolver(this.entityResolver);
      reader.setContentHandler(f);
      if (this.errorReceiver != null) {
         reader.setErrorHandler(this.errorReceiver);
      }

      reader.setEntityResolver(this.entityResolver);
      return reader;
   }

   private String normalizeSystemId(String systemId) {
      try {
         systemId = (new URI(systemId)).normalize().toString();
      } catch (URISyntaxException var3) {
      }

      return systemId;
   }

   boolean isExtensionMode() {
      return this.options.isExtensionMode();
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

   public String[] listSystemIDs() {
      return (String[])this.core.keySet().toArray(new String[this.core.keySet().size()]);
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

   public String getFirstRootDocument() {
      return this.rootDocuments.isEmpty() ? null : (String)this.rootDocuments.iterator().next();
   }

   public Set getRootDocuments() {
      return this.rootDocuments;
   }

   public void dump(OutputStream out) throws IOException {
      try {
         boolean secureProcessingEnabled = this.options == null || !this.options.disableXmlSecurity;
         TransformerFactory tf = XmlUtil.newTransformerFactory(secureProcessingEnabled);
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

   public interface Handler extends ContentHandler {
      Document getDocument();
   }
}
