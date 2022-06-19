package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.tools.internal.ws.resources.WscompileMessages;
import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.wscompile.AbortException;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wsdl.framework.ParseException;
import com.sun.xml.internal.ws.api.wsdl.parser.MetaDataResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.MetadataResolverFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.ServiceDescriptor;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class MetadataFinder extends DOMForest {
   public boolean isMexMetadata;
   private String rootWSDL;
   private final Set rootWsdls = new HashSet();

   public MetadataFinder(InternalizationLogic logic, WsimportOptions options, ErrorReceiver errReceiver) {
      super(logic, new WSEntityResolver(options, errReceiver), options, errReceiver);
   }

   public void parseWSDL() {
      InputSource[] var1 = this.options.getWSDLs();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         InputSource value = var1[var3];
         String systemID = value.getSystemId();
         this.errorReceiver.pollAbort();

         Element doc;
         try {
            if (this.options.entityResolver != null) {
               value = this.options.entityResolver.resolveEntity((String)null, systemID);
            }

            if (value == null) {
               value = new InputSource(systemID);
            }

            Document dom = this.parse(value, true);
            doc = dom.getDocumentElement();
            if (doc == null) {
               continue;
            }

            if (doc.getNamespaceURI() == null || !doc.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") || !doc.getLocalName().equals("definitions")) {
               throw new SAXParseException(WsdlMessages.INVALID_WSDL(systemID, WSDLConstants.QNAME_DEFINITIONS, doc.getNodeName(), this.locatorTable.getStartLocation(doc).getLineNumber()), this.locatorTable.getStartLocation(doc));
            }
         } catch (FileNotFoundException var10) {
            this.errorReceiver.error((String)WsdlMessages.FILE_NOT_FOUND(systemID), (Exception)var10);
            return;
         } catch (IOException var11) {
            doc = this.getFromMetadataResolver(systemID, var11);
         } catch (SAXParseException var12) {
            doc = this.getFromMetadataResolver(systemID, var12);
         } catch (SAXException var13) {
            doc = this.getFromMetadataResolver(systemID, var13);
         }

         if (doc != null) {
            NodeList schemas = doc.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "schema");

            for(int i = 0; i < schemas.getLength(); ++i) {
               if (!this.inlinedSchemaElements.contains(schemas.item(i))) {
                  this.inlinedSchemaElements.add((Element)schemas.item(i));
               }
            }
         }
      }

      this.identifyRootWsdls();
   }

   @Nullable
   public String getRootWSDL() {
      return this.rootWSDL;
   }

   @NotNull
   public Set getRootWSDLs() {
      return this.rootWsdls;
   }

   private void identifyRootWsdls() {
      Iterator var1 = this.rootDocuments.iterator();

      while(var1.hasNext()) {
         String location = (String)var1.next();
         Document doc = this.get(location);
         if (doc != null) {
            Element definition = doc.getDocumentElement();
            if (definition != null && definition.getLocalName() != null && definition.getNamespaceURI() != null && definition.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && definition.getLocalName().equals("definitions")) {
               this.rootWsdls.add(location);
               NodeList nl = definition.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "service");
               if (nl.getLength() > 0) {
                  this.rootWSDL = location;
               }
            }
         }
      }

      if (this.rootWSDL == null) {
         StringBuilder strbuf = new StringBuilder();
         Iterator var7 = this.rootWsdls.iterator();

         while(var7.hasNext()) {
            String str = (String)var7.next();
            strbuf.append(str);
            strbuf.append('\n');
         }

         this.errorReceiver.error((Locator)null, (String)WsdlMessages.FAILED_NOSERVICE(strbuf.toString()));
      }

   }

   @Nullable
   private Element getFromMetadataResolver(String systemId, Exception ex) {
      ServiceDescriptor serviceDescriptor = null;
      Iterator var5 = ServiceFinder.find(MetadataResolverFactory.class).iterator();

      while(var5.hasNext()) {
         MetadataResolverFactory resolverFactory = (MetadataResolverFactory)var5.next();
         MetaDataResolver resolver = resolverFactory.metadataResolver(this.options.entityResolver);

         try {
            serviceDescriptor = resolver.resolve(new URI(systemId));
            if (serviceDescriptor != null) {
               break;
            }
         } catch (URISyntaxException var8) {
            throw new ParseException(var8);
         }
      }

      if (serviceDescriptor != null) {
         this.errorReceiver.warning(new SAXParseException(WsdlMessages.TRY_WITH_MEX(ex.getMessage()), (Locator)null, ex));
         return this.parseMetadata(systemId, serviceDescriptor);
      } else {
         this.errorReceiver.error((Locator)null, WsdlMessages.PARSING_UNABLE_TO_GET_METADATA(ex.getMessage(), WscompileMessages.WSIMPORT_NO_WSDL(systemId)), ex);
         return null;
      }
   }

   private Element parseMetadata(@NotNull String systemId, @NotNull ServiceDescriptor serviceDescriptor) {
      List mexWsdls = serviceDescriptor.getWSDLs();
      List mexSchemas = serviceDescriptor.getSchemas();
      Document root = null;
      Iterator var6 = mexWsdls.iterator();

      while(true) {
         Source src;
         Node n;
         do {
            if (!var6.hasNext()) {
               var6 = mexSchemas.iterator();

               while(var6.hasNext()) {
                  src = (Source)var6.next();
                  if (src instanceof DOMSource) {
                     n = ((DOMSource)src).getNode();
                     Element e = n.getNodeType() == 1 ? (Element)n : DOMUtil.getFirstElementChild(n);
                     this.inlinedSchemaElements.add(e);
                  }
               }

               return root.getDocumentElement();
            }

            src = (Source)var6.next();
         } while(!(src instanceof DOMSource));

         n = ((DOMSource)src).getNode();
         Document doc;
         if (n.getNodeType() == 1 && n.getOwnerDocument() == null) {
            doc = DOMUtil.createDom();
            doc.importNode(n, true);
         } else {
            doc = n.getOwnerDocument();
         }

         NodeList nl;
         if (root == null) {
            nl = doc.getDocumentElement().getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "service");
            if (nl.getLength() > 0) {
               root = doc;
               this.rootWSDL = src.getSystemId();
            }
         }

         nl = doc.getDocumentElement().getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", "import");

         for(int i = 0; i < nl.getLength(); ++i) {
            Element imp = (Element)nl.item(i);
            String loc = imp.getAttribute("location");
            if (loc != null && !this.externalReferences.contains(loc)) {
               this.externalReferences.add(loc);
            }
         }

         if (this.core.keySet().contains(systemId)) {
            this.core.remove(systemId);
         }

         this.core.put(src.getSystemId(), doc);
         this.resolvedCache.put(systemId, doc.getDocumentURI());
         this.isMexMetadata = true;
      }
   }

   private static class HttpClientVerifier implements HostnameVerifier {
      private HttpClientVerifier() {
      }

      public boolean verify(String s, SSLSession sslSession) {
         return true;
      }

      // $FF: synthetic method
      HttpClientVerifier(Object x0) {
         this();
      }
   }

   public static class WSEntityResolver implements EntityResolver {
      WsimportOptions options;
      ErrorReceiver errorReceiver;
      private URLConnection c = null;
      private boolean doReset = false;

      public WSEntityResolver(WsimportOptions options, ErrorReceiver errReceiver) {
         this.options = options;
         this.errorReceiver = errReceiver;
      }

      public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
         InputSource inputSource = null;
         if (this.options.entityResolver != null) {
            inputSource = this.options.entityResolver.resolveEntity((String)null, systemId);
         }

         if (inputSource == null) {
            inputSource = new InputSource(systemId);
            InputStream is = null;
            int redirects = 0;
            URL url = JAXWSUtils.getFileOrURL(inputSource.getSystemId());
            URLConnection conn = url.openConnection();

            boolean redirect;
            do {
               if (conn instanceof HttpsURLConnection && this.options.disableSSLHostnameVerification) {
                  ((HttpsURLConnection)conn).setHostnameVerifier(new HttpClientVerifier());
               }

               redirect = false;
               if (conn instanceof HttpURLConnection) {
                  ((HttpURLConnection)conn).setInstanceFollowRedirects(false);
               }

               if (conn instanceof JarURLConnection && conn.getUseCaches()) {
                  this.doReset = true;
                  conn.setDefaultUseCaches(false);
                  this.c = conn;
               }

               try {
                  is = conn.getInputStream();
               } catch (IOException var13) {
                  if (conn instanceof HttpURLConnection) {
                     HttpURLConnection httpConn = (HttpURLConnection)conn;
                     int code = httpConn.getResponseCode();
                     if (code == 401) {
                        this.errorReceiver.error(new SAXParseException(WscompileMessages.WSIMPORT_AUTH_INFO_NEEDED(var13.getMessage(), systemId, WsimportOptions.defaultAuthfile), (Locator)null, var13));
                        throw new AbortException();
                     }
                  }

                  throw var13;
               }

               if (conn instanceof HttpURLConnection) {
                  HttpURLConnection httpConn = (HttpURLConnection)conn;
                  int code = httpConn.getResponseCode();
                  if (code == 302 || code == 303) {
                     List seeOther = (List)httpConn.getHeaderFields().get("Location");
                     if (seeOther != null && seeOther.size() > 0) {
                        URL newurl = new URL(url, (String)seeOther.get(0));
                        if (!newurl.equals(url)) {
                           this.errorReceiver.info(new SAXParseException(WscompileMessages.WSIMPORT_HTTP_REDIRECT(code, seeOther.get(0)), (Locator)null));
                           url = newurl;
                           httpConn.disconnect();
                           if (redirects >= 5) {
                              this.errorReceiver.error(new SAXParseException(WscompileMessages.WSIMPORT_MAX_REDIRECT_ATTEMPT(), (Locator)null));
                              throw new AbortException();
                           }

                           conn = newurl.openConnection();
                           inputSource.setSystemId(newurl.toExternalForm());
                           ++redirects;
                           redirect = true;
                        }
                     }
                  }
               }
            } while(redirect);

            inputSource.setByteStream(is);
         }

         return inputSource;
      }

      protected void finalize() throws Throwable {
         if (this.doReset) {
            this.c.setDefaultUseCaches(true);
         }

      }
   }
}
