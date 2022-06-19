package com.sun.tools.internal.ws.processor.modeler.wsdl;

import com.sun.tools.internal.ws.processor.generator.Names;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.Options;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wsdl.document.Binding;
import com.sun.tools.internal.ws.wsdl.document.BindingOperation;
import com.sun.tools.internal.ws.wsdl.document.Message;
import com.sun.tools.internal.ws.wsdl.document.MessagePart;
import com.sun.tools.internal.ws.wsdl.document.Operation;
import com.sun.tools.internal.ws.wsdl.document.Port;
import com.sun.tools.internal.ws.wsdl.document.PortType;
import com.sun.tools.internal.ws.wsdl.document.Service;
import com.sun.tools.internal.ws.wsdl.document.WSDLDocument;
import com.sun.tools.internal.ws.wsdl.document.jaxws.JAXWSBinding;
import com.sun.tools.internal.ws.wsdl.document.schema.SchemaKinds;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAP12Binding;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPBinding;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.xml.sax.InputSource;

public class PseudoSchemaBuilder {
   private final StringWriter buf = new StringWriter();
   private final WSDLDocument wsdlDocument;
   private WSDLModeler wsdlModeler;
   private final List schemas = new ArrayList();
   private final HashMap bindingNameToPortMap = new HashMap();
   private static final String w3ceprSchemaBinding = "<bindings\n  xmlns=\"http://java.sun.com/xml/ns/jaxb\"\n  xmlns:wsa=\"http://www.w3.org/2005/08/addressing\"\n  xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\"\n  version=\"2.1\">\n  \n  <bindings scd=\"x-schema::wsa\" if-exists=\"true\">\n    <bindings scd=\"wsa:EndpointReference\">\n      <class ref=\"javax.xml.ws.wsaddressing.W3CEndpointReference\" xjc:recursive=\"true\"/>\n    </bindings>\n    <bindings scd=\"~wsa:EndpointReferenceType\">\n      <class ref=\"javax.xml.ws.wsaddressing.W3CEndpointReference\" xjc:recursive=\"true\"/>\n    </bindings>\n  </bindings>\n</bindings>";
   private static final String memberSubmissionEPR = "<bindings\n  xmlns=\"http://java.sun.com/xml/ns/jaxb\"\n  xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\"\n  version=\"2.1\">\n  \n  <bindings scd=\"x-schema::wsa\" if-exists=\"true\">\n    <bindings scd=\"wsa:EndpointReference\">\n      <class ref=\"com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference\"/>\n    </bindings>\n    <bindings scd=\"~wsa:EndpointReferenceType\">\n      <class ref=\"com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference\"/>\n    </bindings>\n  </bindings>\n</bindings>";
   private static final String sysId = "http://dummy.pseudo-schema#schema";
   private WsimportOptions options;
   boolean asyncRespBeanBinding = false;

   public static List build(WSDLModeler wsdlModeler, WsimportOptions options, ErrorReceiver errReceiver) {
      PseudoSchemaBuilder b = new PseudoSchemaBuilder(wsdlModeler.document);
      b.wsdlModeler = wsdlModeler;
      b.options = options;
      b.build();

      int i;
      InputSource is;
      for(i = 0; i < b.schemas.size(); ++i) {
         is = (InputSource)b.schemas.get(i);
         is.setSystemId("http://dummy.pseudo-schema#schema" + (i + 1));
      }

      if (!options.noAddressingBbinding && options.target.isLaterThan(Options.Target.V2_1)) {
         is = new InputSource(new ByteArrayInputStream("<bindings\n  xmlns=\"http://java.sun.com/xml/ns/jaxb\"\n  xmlns:wsa=\"http://www.w3.org/2005/08/addressing\"\n  xmlns:xjc=\"http://java.sun.com/xml/ns/jaxb/xjc\"\n  version=\"2.1\">\n  \n  <bindings scd=\"x-schema::wsa\" if-exists=\"true\">\n    <bindings scd=\"wsa:EndpointReference\">\n      <class ref=\"javax.xml.ws.wsaddressing.W3CEndpointReference\" xjc:recursive=\"true\"/>\n    </bindings>\n    <bindings scd=\"~wsa:EndpointReferenceType\">\n      <class ref=\"javax.xml.ws.wsaddressing.W3CEndpointReference\" xjc:recursive=\"true\"/>\n    </bindings>\n  </bindings>\n</bindings>".getBytes(StandardCharsets.UTF_8)));
         StringBuilder var10001 = (new StringBuilder()).append("http://dummy.pseudo-schema#schema");
         ++i;
         is.setSystemId(var10001.append(i + 1).toString());
         b.schemas.add(is);
      }

      return b.schemas;
   }

   private PseudoSchemaBuilder(WSDLDocument _wsdl) {
      this.wsdlDocument = _wsdl;
   }

   private void build() {
      Iterator itr = this.wsdlDocument.getDefinitions().services();

      while(itr.hasNext()) {
         this.build((Service)itr.next());
      }

   }

   private void build(Service service) {
      Iterator itr = service.ports();

      while(itr.hasNext()) {
         this.build((Port)itr.next());
      }

   }

   private void build(Port port) {
      if (!this.wsdlModeler.isProvider(port)) {
         Binding binding = port.resolveBinding(this.wsdlDocument);
         SOAPBinding soapBinding = (SOAPBinding)WSDLModelerBase.getExtensionOfType(binding, SOAPBinding.class);
         if (soapBinding == null) {
            soapBinding = (SOAPBinding)WSDLModelerBase.getExtensionOfType(binding, SOAP12Binding.class);
         }

         if (soapBinding != null) {
            PortType portType = binding.resolvePortType(this.wsdlDocument);
            QName bindingName = WSDLModelerBase.getQNameOf(binding);
            if (!this.bindingNameToPortMap.containsKey(bindingName)) {
               this.bindingNameToPortMap.put(bindingName, port);
               Iterator itr = binding.operations();

               while(itr.hasNext()) {
                  BindingOperation bindingOperation = (BindingOperation)itr.next();
                  Set boundedOps = portType.getOperationsNamed(bindingOperation.getName());
                  if (boundedOps.size() == 1) {
                     Operation operation = (Operation)boundedOps.iterator().next();
                     if (this.wsdlModeler.isAsync(portType, operation)) {
                        this.buildAsync(portType, operation, bindingOperation);
                     }
                  }
               }

            }
         }
      }
   }

   private void buildAsync(PortType portType, Operation operation, BindingOperation bindingOperation) {
      String operationName = this.getCustomizedOperationName(operation);
      if (operationName != null) {
         Message outputMessage = null;
         if (operation.getOutput() != null) {
            outputMessage = operation.getOutput().resolveMessage(this.wsdlDocument);
         }

         if (outputMessage != null) {
            List allParts = new ArrayList(outputMessage.getParts());
            if (this.options != null && this.options.additionalHeaders) {
               List addtionalHeaderParts = this.wsdlModeler.getAdditionHeaderParts(bindingOperation, outputMessage, false);
               allParts.addAll(addtionalHeaderParts);
            }

            if (allParts.size() > 1) {
               this.build(this.getOperationName(operationName), allParts);
            }
         }

      }
   }

   private String getCustomizedOperationName(Operation operation) {
      JAXWSBinding jaxwsCustomization = (JAXWSBinding)WSDLModelerBase.getExtensionOfType(operation, JAXWSBinding.class);
      String operationName = jaxwsCustomization != null ? (jaxwsCustomization.getMethodName() != null ? jaxwsCustomization.getMethodName().getName() : null) : null;
      if (operationName != null) {
         return Names.isJavaReservedWord(operationName) ? null : operationName;
      } else {
         return operation.getName();
      }
   }

   private void writeImports(QName elementName, List parts) {
      Set uris = new HashSet();
      Iterator var4 = parts.iterator();

      while(var4.hasNext()) {
         MessagePart p = (MessagePart)var4.next();
         String ns = p.getDescriptor().getNamespaceURI();
         if (!uris.contains(ns) && !ns.equals("http://www.w3.org/2001/XMLSchema") && !ns.equals(elementName.getNamespaceURI())) {
            this.print("<xs:import namespace=''{0}''/>", (Object)ns);
            uris.add(ns);
         }
      }

   }

   private void build(QName elementName, List allParts) {
      this.print("<xs:schema xmlns:xs=''http://www.w3.org/2001/XMLSchema''           xmlns:jaxb=''http://java.sun.com/xml/ns/jaxb''           xmlns:xjc=''http://java.sun.com/xml/ns/jaxb/xjc''           jaxb:extensionBindingPrefixes=''xjc''           jaxb:version=''1.0''           targetNamespace=''{0}''>", (Object)elementName.getNamespaceURI());
      this.writeImports(elementName, allParts);
      if (!this.asyncRespBeanBinding) {
         this.print("<xs:annotation><xs:appinfo>  <jaxb:schemaBindings>    <jaxb:package name=''{0}'' />  </jaxb:schemaBindings></xs:appinfo></xs:annotation>", (Object)this.wsdlModeler.getJavaPackage());
         this.asyncRespBeanBinding = true;
      }

      this.print("<xs:element name=''{0}''>", (Object)elementName.getLocalPart());
      this.print("<xs:complexType>");
      this.print("<xs:sequence>");
      Iterator var3 = allParts.iterator();

      while(var3.hasNext()) {
         MessagePart p = (MessagePart)var3.next();
         if (p.getDescriptorKind() == SchemaKinds.XSD_ELEMENT) {
            this.print("<xs:element ref=''types:{0}'' xmlns:types=''{1}''/>", p.getDescriptor().getLocalPart(), p.getDescriptor().getNamespaceURI());
         } else {
            this.print("<xs:element name=''{0}'' type=''{1}'' xmlns=''{2}'' />", p.getName(), p.getDescriptor().getLocalPart(), p.getDescriptor().getNamespaceURI());
         }
      }

      this.print("</xs:sequence>");
      this.print("</xs:complexType>");
      this.print("</xs:element>");
      this.print("</xs:schema>");
      if (this.buf.toString().length() > 0) {
         InputSource is = new InputSource(new StringReader(this.buf.toString()));
         this.schemas.add(is);
         this.buf.getBuffer().setLength(0);
      }

   }

   private QName getOperationName(String operationName) {
      if (operationName == null) {
         return null;
      } else {
         String namespaceURI = "";
         return new QName(namespaceURI, operationName + "Response");
      }
   }

   private void print(String msg) {
      this.print(msg, new Object[0]);
   }

   private void print(String msg, Object arg1) {
      this.print(msg, new Object[]{arg1});
   }

   private void print(String msg, Object arg1, Object arg2) {
      this.print(msg, new Object[]{arg1, arg2});
   }

   private void print(String msg, Object arg1, Object arg2, Object arg3) {
      this.print(msg, new Object[]{arg1, arg2, arg3});
   }

   private void print(String msg, Object[] args) {
      this.buf.write(MessageFormat.format(msg, args));
      this.buf.write(10);
   }
}
