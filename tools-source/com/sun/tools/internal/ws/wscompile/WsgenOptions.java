package com.sun.tools.internal.ws.wscompile;

import com.sun.tools.internal.ws.api.WsgenExtension;
import com.sun.tools.internal.ws.api.WsgenProtocol;
import com.sun.tools.internal.ws.resources.WscompileMessages;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jws.WebService;
import javax.xml.namespace.QName;

public class WsgenOptions extends Options {
   public QName serviceName;
   public QName portName;
   public File nonclassDestDir;
   public boolean genWsdl;
   public boolean inlineSchemas;
   public String protocol = "soap1.1";
   public Set protocols = new LinkedHashSet();
   public Map nonstdProtocols = new LinkedHashMap();
   public File wsgenReport;
   public boolean doNotOverWrite;
   public boolean protocolSet = false;
   public List externalMetadataFiles = new ArrayList();
   private static final String SERVICENAME_OPTION = "-servicename";
   private static final String PORTNAME_OPTION = "-portname";
   private static final String HTTP = "http";
   private static final String SOAP11 = "soap1.1";
   public static final String X_SOAP12 = "Xsoap1.2";
   List endpoints = new ArrayList();
   public Class endpoint;
   private boolean isImplClass;

   public WsgenOptions() {
      this.protocols.add("soap1.1");
      this.protocols.add("Xsoap1.2");
      this.nonstdProtocols.put("Xsoap1.2", "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/");
      ServiceFinder extn = ServiceFinder.find(WsgenExtension.class);
      Iterator var2 = extn.iterator();

      while(var2.hasNext()) {
         WsgenExtension ext = (WsgenExtension)var2.next();
         Class clazz = ext.getClass();
         WsgenProtocol pro = (WsgenProtocol)clazz.getAnnotation(WsgenProtocol.class);
         this.protocols.add(pro.token());
         this.nonstdProtocols.put(pro.token(), pro.lexical());
      }

   }

   protected int parseArguments(String[] args, int i) throws BadCommandLineException {
      int j = super.parseArguments(args, i);
      if (args[i].equals("-servicename")) {
         ++i;
         this.serviceName = QName.valueOf(this.requireArgument("-servicename", args, i));
         if (this.serviceName.getNamespaceURI() != null && this.serviceName.getNamespaceURI().length() != 0) {
            if (this.serviceName.getLocalPart() != null && this.serviceName.getLocalPart().length() != 0) {
               return 2;
            } else {
               throw new BadCommandLineException(WscompileMessages.WSGEN_SERVICENAME_MISSING_LOCALNAME(args[i]));
            }
         } else {
            throw new BadCommandLineException(WscompileMessages.WSGEN_SERVICENAME_MISSING_NAMESPACE(args[i]));
         }
      } else if (args[i].equals("-portname")) {
         ++i;
         this.portName = QName.valueOf(this.requireArgument("-portname", args, i));
         if (this.portName.getNamespaceURI() != null && this.portName.getNamespaceURI().length() != 0) {
            if (this.portName.getLocalPart() != null && this.portName.getLocalPart().length() != 0) {
               return 2;
            } else {
               throw new BadCommandLineException(WscompileMessages.WSGEN_PORTNAME_MISSING_LOCALNAME(args[i]));
            }
         } else {
            throw new BadCommandLineException(WscompileMessages.WSGEN_PORTNAME_MISSING_NAMESPACE(args[i]));
         }
      } else if (args[i].equals("-r")) {
         ++i;
         this.nonclassDestDir = new File(this.requireArgument("-r", args, i));
         if (!this.nonclassDestDir.exists()) {
            throw new BadCommandLineException(WscompileMessages.WSCOMPILE_NO_SUCH_DIRECTORY(this.nonclassDestDir.getPath()));
         } else {
            return 2;
         }
      } else if (args[i].startsWith("-wsdl")) {
         this.genWsdl = true;
         String value = args[i].substring(5);
         int index = value.indexOf(58);
         if (index == 0) {
            value = value.substring(1);
            index = value.indexOf(47);
            if (index == -1) {
               this.protocol = value;
            } else {
               this.protocol = value.substring(0, index);
            }

            this.protocolSet = true;
         }

         return 1;
      } else if (args[i].equals("-XwsgenReport")) {
         ++i;
         this.wsgenReport = new File(this.requireArgument("-XwsgenReport", args, i));
         return 2;
      } else if (args[i].equals("-Xdonotoverwrite")) {
         this.doNotOverWrite = true;
         return 1;
      } else if (args[i].equals("-inlineSchemas")) {
         this.inlineSchemas = true;
         return 1;
      } else if ("-x".equals(args[i])) {
         ++i;
         this.externalMetadataFiles.add(this.requireArgument("-x", args, i));
         return 1;
      } else {
         return j;
      }
   }

   protected void addFile(String arg) {
      this.endpoints.add(arg);
   }

   public void validate() throws BadCommandLineException {
      if (this.nonclassDestDir == null) {
         this.nonclassDestDir = this.destDir;
      }

      if (!this.protocols.contains(this.protocol)) {
         throw new BadCommandLineException(WscompileMessages.WSGEN_INVALID_PROTOCOL(this.protocol, this.protocols));
      } else if (this.endpoints.isEmpty()) {
         throw new BadCommandLineException(WscompileMessages.WSGEN_MISSING_FILE());
      } else if (this.protocol != null && (!this.protocol.equalsIgnoreCase("Xsoap1.2") || this.isExtensionMode())) {
         if (this.nonstdProtocols.containsKey(this.protocol) && !this.isExtensionMode()) {
            throw new BadCommandLineException(WscompileMessages.WSGEN_PROTOCOL_WITHOUT_EXTENSION(this.protocol));
         } else if (this.inlineSchemas && !this.genWsdl) {
            throw new BadCommandLineException(WscompileMessages.WSGEN_INLINE_SCHEMAS_ONLY_WITH_WSDL());
         } else {
            this.validateEndpointClass();
            this.validateArguments();
         }
      } else {
         throw new BadCommandLineException(WscompileMessages.WSGEN_SOAP_12_WITHOUT_EXTENSION());
      }
   }

   private void validateEndpointClass() throws BadCommandLineException {
      Class clazz = null;
      Iterator var2 = this.endpoints.iterator();

      while(var2.hasNext()) {
         String cls = (String)var2.next();
         clazz = this.getClass(cls);
         if (clazz != null && !clazz.isEnum() && !clazz.isInterface() && !clazz.isPrimitive()) {
            this.isImplClass = true;
            WebService webService = (WebService)clazz.getAnnotation(WebService.class);
            if (webService == null) {
               continue;
            }
            break;
         }
      }

      if (clazz == null) {
         throw new BadCommandLineException(WscompileMessages.WSGEN_CLASS_NOT_FOUND(this.endpoints.get(0)));
      } else if (!this.isImplClass) {
         throw new BadCommandLineException(WscompileMessages.WSGEN_CLASS_MUST_BE_IMPLEMENTATION_CLASS(clazz.getName()));
      } else {
         this.endpoint = clazz;
         this.validateBinding();
      }
   }

   private void validateBinding() throws BadCommandLineException {
      if (this.genWsdl) {
         BindingID binding = BindingID.parse(this.endpoint);
         if ((binding.equals(BindingID.SOAP12_HTTP) || binding.equals(BindingID.SOAP12_HTTP_MTOM)) && (!this.protocol.equals("Xsoap1.2") || !this.isExtensionMode())) {
            throw new BadCommandLineException(WscompileMessages.WSGEN_CANNOT_GEN_WSDL_FOR_SOAP_12_BINDING(binding.toString(), this.endpoint.getName()));
         }

         if (binding.equals(BindingID.XML_HTTP)) {
            throw new BadCommandLineException(WscompileMessages.WSGEN_CANNOT_GEN_WSDL_FOR_NON_SOAP_BINDING(binding.toString(), this.endpoint.getName()));
         }
      }

   }

   private void validateArguments() throws BadCommandLineException {
      if (!this.genWsdl) {
         if (this.serviceName != null) {
            throw new BadCommandLineException(WscompileMessages.WSGEN_WSDL_ARG_NO_GENWSDL("-servicename"));
         }

         if (this.portName != null) {
            throw new BadCommandLineException(WscompileMessages.WSGEN_WSDL_ARG_NO_GENWSDL("-portname"));
         }
      }

   }

   BindingID getBindingID(String protocol) {
      if (protocol.equals("soap1.1")) {
         return BindingID.SOAP11_HTTP;
      } else if (protocol.equals("Xsoap1.2")) {
         return BindingID.SOAP12_HTTP;
      } else {
         String lexical = (String)this.nonstdProtocols.get(protocol);
         return lexical != null ? BindingID.parse(lexical) : null;
      }
   }

   private Class getClass(String className) {
      try {
         return this.getClassLoader().loadClass(className);
      } catch (ClassNotFoundException var3) {
         return null;
      }
   }
}
