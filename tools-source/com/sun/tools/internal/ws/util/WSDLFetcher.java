package com.sun.tools.internal.ws.util;

import com.sun.istack.internal.NotNull;
import com.sun.tools.internal.ws.resources.WscompileMessages;
import com.sun.tools.internal.ws.wscompile.WsimportListener;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wsdl.parser.DOMForest;
import com.sun.tools.internal.ws.wsdl.parser.MetadataFinder;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import com.sun.xml.internal.ws.wsdl.writer.DocumentLocationResolver;
import com.sun.xml.internal.ws.wsdl.writer.WSDLPatcher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WSDLFetcher {
   private WsimportOptions options;
   private WsimportListener listener;
   private static String WSDL_PATH = "META-INF/wsdl";
   private static String WSDL_FILE_EXTENSION = ".wsdl";
   private static String SCHEMA_FILE_EXTENSION = ".xsd";

   public WSDLFetcher(WsimportOptions options, WsimportListener listener) {
      this.options = options;
      this.listener = listener;
   }

   public String fetchWsdls(MetadataFinder forest) throws IOException, XMLStreamException {
      String rootWsdl = null;

      String root;
      for(Iterator var3 = forest.getRootDocuments().iterator(); var3.hasNext(); rootWsdl = root) {
         root = (String)var3.next();
      }

      Set externalRefs = forest.getExternalReferences();
      Map documentMap = this.createDocumentMap(forest, this.getWSDLDownloadDir(), rootWsdl, externalRefs);
      String rootWsdlName = this.fetchFile(rootWsdl, forest, documentMap, this.getWSDLDownloadDir());
      Iterator var6 = forest.getExternalReferences().iterator();

      while(var6.hasNext()) {
         String reference = (String)var6.next();
         this.fetchFile(reference, forest, documentMap, this.getWSDLDownloadDir());
      }

      return WSDL_PATH + "/" + rootWsdlName;
   }

   private String fetchFile(String doc, DOMForest forest, Map documentMap, File destDir) throws IOException, XMLStreamException {
      DocumentLocationResolver docLocator = this.createDocResolver(doc, forest, documentMap);
      WSDLPatcher wsdlPatcher = new WSDLPatcher(new PortAddressResolver() {
         public String getAddressFor(@NotNull QName serviceName, @NotNull String portName) {
            return null;
         }
      }, docLocator);
      XMLStreamReader xsr = null;
      XMLStreamWriter xsw = null;
      OutputStream os = null;
      String resolvedRootWsdl = null;

      try {
         xsr = SourceReaderFactory.createSourceReader(new DOMSource(forest.get(doc)), false);
         XMLOutputFactory writerfactory = XMLOutputFactory.newInstance();
         resolvedRootWsdl = docLocator.getLocationFor((String)null, doc);
         File outFile = new File(destDir, resolvedRootWsdl);
         os = new FileOutputStream(outFile);
         if (this.options.verbose) {
            this.listener.message(WscompileMessages.WSIMPORT_DOCUMENT_DOWNLOAD(doc, outFile));
         }

         xsw = writerfactory.createXMLStreamWriter(os);
         IndentingXMLStreamWriter indentingWriter = new IndentingXMLStreamWriter(xsw);
         wsdlPatcher.bridge(xsr, indentingWriter);
         this.options.addGeneratedFile(outFile);
      } finally {
         try {
            if (xsr != null) {
               xsr.close();
            }

            if (xsw != null) {
               xsw.close();
            }
         } finally {
            if (os != null) {
               os.close();
            }

         }

      }

      return resolvedRootWsdl;
   }

   private Map createDocumentMap(MetadataFinder forest, File baseDir, String rootWsdl, Set externalReferences) {
      Map map = new HashMap();
      String rootWsdlFileName = rootWsdl;
      int slashIndex = rootWsdl.lastIndexOf("/");
      if (slashIndex >= 0) {
         rootWsdlFileName = rootWsdl.substring(slashIndex + 1);
      }

      String rootWsdlName;
      if (!rootWsdlFileName.endsWith(WSDL_FILE_EXTENSION)) {
         Document rootWsdlDoc = forest.get(rootWsdl);
         NodeList serviceNodes = rootWsdlDoc.getElementsByTagNameNS(WSDLConstants.QNAME_SERVICE.getNamespaceURI(), WSDLConstants.QNAME_SERVICE.getLocalPart());
         if (serviceNodes.getLength() == 0) {
            rootWsdlName = "Service";
         } else {
            Node serviceNode = serviceNodes.item(0);
            String serviceName = ((Element)serviceNode).getAttribute("name");
            rootWsdlName = serviceName;
         }

         rootWsdlFileName = rootWsdlName + WSDL_FILE_EXTENSION;
      } else {
         rootWsdlName = rootWsdlFileName.substring(0, rootWsdlFileName.length() - 5);
      }

      map.put(rootWsdl, this.sanitize(rootWsdlFileName));
      int i = 1;
      Iterator var18 = externalReferences.iterator();

      while(true) {
         while(var18.hasNext()) {
            String ref = (String)var18.next();
            Document refDoc = forest.get(ref);
            Element rootEl = refDoc.getDocumentElement();
            String fileName = null;
            int index = ref.lastIndexOf("/");
            if (index >= 0) {
               fileName = ref.substring(index + 1);
            }

            String fileExtn;
            if (rootEl.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart()) && rootEl.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/")) {
               fileExtn = WSDL_FILE_EXTENSION;
            } else if (rootEl.getLocalName().equals(WSDLConstants.QNAME_SCHEMA.getLocalPart()) && rootEl.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
               fileExtn = SCHEMA_FILE_EXTENSION;
            } else {
               fileExtn = ".xml";
            }

            if (fileName != null && (fileName.endsWith(WSDL_FILE_EXTENSION) || fileName.endsWith(SCHEMA_FILE_EXTENSION))) {
               map.put(ref, rootWsdlName + "_" + fileName);
            } else {
               map.put(ref, rootWsdlName + "_metadata" + i++ + fileExtn);
            }
         }

         return map;
      }
   }

   private DocumentLocationResolver createDocResolver(final String baseWsdl, final DOMForest forest, final Map documentMap) {
      return new DocumentLocationResolver() {
         public String getLocationFor(String namespaceURI, String systemId) {
            try {
               URL reference = new URL(new URL(baseWsdl), systemId);
               systemId = reference.toExternalForm();
            } catch (MalformedURLException var4) {
               throw new RuntimeException(var4);
            }

            if (documentMap.get(systemId) != null) {
               return (String)documentMap.get(systemId);
            } else {
               String parsedEntity = (String)forest.getReferencedEntityMap().get(systemId);
               return (String)documentMap.get(parsedEntity);
            }
         }
      };
   }

   private String sanitize(String fileName) {
      fileName = fileName.replace('?', '.');
      StringBuilder sb = new StringBuilder(fileName);

      for(int i = 0; i < sb.length(); ++i) {
         char c = sb.charAt(i);
         if (!Character.isLetterOrDigit(c) && c != '/' && c != '.' && c != '_' && c != ' ' && c != '-') {
            sb.setCharAt(i, '_');
         }
      }

      return sb.toString();
   }

   private File getWSDLDownloadDir() {
      File wsdlDir = new File(this.options.destDir, WSDL_PATH);
      boolean created = wsdlDir.mkdirs();
      if (this.options.verbose && !created) {
         this.listener.message(WscompileMessages.WSCOMPILE_NO_SUCH_DIRECTORY(wsdlDir));
      }

      return wsdlDir;
   }
}
