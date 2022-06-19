package com.sun.tools.internal.ws.wsdl.parser;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensionHandler;
import com.sun.tools.internal.ws.resources.WsdlMessages;
import com.sun.tools.internal.ws.util.xml.XmlUtil;
import com.sun.tools.internal.ws.wscompile.ErrorReceiverFilter;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wsdl.document.Binding;
import com.sun.tools.internal.ws.wsdl.document.BindingFault;
import com.sun.tools.internal.ws.wsdl.document.BindingInput;
import com.sun.tools.internal.ws.wsdl.document.BindingOperation;
import com.sun.tools.internal.ws.wsdl.document.BindingOutput;
import com.sun.tools.internal.ws.wsdl.document.Definitions;
import com.sun.tools.internal.ws.wsdl.document.Documentation;
import com.sun.tools.internal.ws.wsdl.document.Fault;
import com.sun.tools.internal.ws.wsdl.document.Import;
import com.sun.tools.internal.ws.wsdl.document.Input;
import com.sun.tools.internal.ws.wsdl.document.Message;
import com.sun.tools.internal.ws.wsdl.document.MessagePart;
import com.sun.tools.internal.ws.wsdl.document.Operation;
import com.sun.tools.internal.ws.wsdl.document.OperationStyle;
import com.sun.tools.internal.ws.wsdl.document.Output;
import com.sun.tools.internal.ws.wsdl.document.Port;
import com.sun.tools.internal.ws.wsdl.document.PortType;
import com.sun.tools.internal.ws.wsdl.document.Service;
import com.sun.tools.internal.ws.wsdl.document.WSDLConstants;
import com.sun.tools.internal.ws.wsdl.document.WSDLDocument;
import com.sun.tools.internal.ws.wsdl.document.schema.SchemaConstants;
import com.sun.tools.internal.ws.wsdl.document.schema.SchemaKinds;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.ParserListener;
import com.sun.tools.internal.ws.wsdl.framework.TWSDLParserContextImpl;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class WSDLParser {
   private final ErrorReceiverFilter errReceiver;
   private WsimportOptions options;
   private final Map extensionHandlers;
   private MetadataFinder forest;
   private ArrayList listeners;

   public WSDLParser(WsimportOptions options, ErrorReceiverFilter errReceiver, MetadataFinder forest) {
      this.extensionHandlers = new HashMap();
      this.options = options;
      this.errReceiver = errReceiver;
      if (forest == null) {
         forest = new MetadataFinder(new WSDLInternalizationLogic(), options, errReceiver);
         forest.parseWSDL();
         if (forest.isMexMetadata) {
            errReceiver.reset();
         }
      }

      this.forest = forest;
      this.register(new SOAPExtensionHandler(this.extensionHandlers));
      this.register(new HTTPExtensionHandler(this.extensionHandlers));
      this.register(new MIMEExtensionHandler(this.extensionHandlers));
      this.register(new JAXWSBindingExtensionHandler(this.extensionHandlers));
      this.register(new SOAP12ExtensionHandler(this.extensionHandlers));
      this.register(new W3CAddressingExtensionHandler(this.extensionHandlers, errReceiver));
      this.register(new W3CAddressingMetadataExtensionHandler(this.extensionHandlers, errReceiver));
      this.register(new Policy12ExtensionHandler());
      this.register(new Policy15ExtensionHandler());
      Iterator var4 = ServiceFinder.find(TWSDLExtensionHandler.class).iterator();

      while(var4.hasNext()) {
         TWSDLExtensionHandler te = (TWSDLExtensionHandler)var4.next();
         this.register(te);
      }

   }

   WSDLParser(WsimportOptions options, ErrorReceiverFilter errReceiver) {
      this(options, errReceiver, (MetadataFinder)null);
   }

   private void register(TWSDLExtensionHandler h) {
      this.extensionHandlers.put(h.getNamespaceURI(), h);
   }

   public void addParserListener(ParserListener l) {
      if (this.listeners == null) {
         this.listeners = new ArrayList();
      }

      this.listeners.add(l);
   }

   public WSDLDocument parse() throws SAXException, IOException {
      InputSource[] var1 = this.options.getWSDLBindings();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         InputSource value = var1[var3];
         this.errReceiver.pollAbort();
         Document root = this.forest.parse(value, false);
         if (root != null) {
            Element binding = root.getDocumentElement();
            if (Internalizer.fixNull(binding.getNamespaceURI()).equals("http://java.sun.com/xml/ns/jaxws") && binding.getLocalName().equals("bindings")) {
               NodeList nl = binding.getElementsByTagNameNS("http://java.sun.com/xml/ns/javaee", "handler-chains");

               for(int i = 0; i < nl.getLength(); ++i) {
                  this.options.addHandlerChainConfiguration((Element)nl.item(i));
               }
            } else {
               this.errReceiver.error(this.forest.locatorTable.getStartLocation(binding), WsdlMessages.PARSER_NOT_A_BINDING_FILE(binding.getNamespaceURI(), binding.getLocalName()));
            }
         }
      }

      return this.buildWSDLDocument();
   }

   public MetadataFinder getDOMForest() {
      return this.forest;
   }

   private WSDLDocument buildWSDLDocument() {
      String location = this.forest.getRootWSDL();
      if (location == null) {
         return null;
      } else {
         Document root = this.forest.get(location);
         if (root == null) {
            return null;
         } else {
            WSDLDocument document = new WSDLDocument(this.forest, this.errReceiver);
            document.setSystemId(location);
            TWSDLParserContextImpl context = new TWSDLParserContextImpl(this.forest, document, this.listeners, this.errReceiver);
            Definitions definitions = this.parseDefinitions(context, root);
            document.setDefinitions(definitions);
            return document;
         }
      }
   }

   private Definitions parseDefinitions(TWSDLParserContextImpl context, Document root) {
      context.pushWSDLLocation();
      context.setWSDLLocation(context.getDocument().getSystemId());
      (new Internalizer(this.forest, this.options, this.errReceiver)).transform();
      Definitions definitions = this.parseDefinitionsNoImport(context, root);
      if (definitions == null) {
         Locator locator = this.forest.locatorTable.getStartLocation(root.getDocumentElement());
         this.errReceiver.error(locator, WsdlMessages.PARSING_NOT_AWSDL(locator.getSystemId()));
      }

      this.processImports(context);
      context.popWSDLLocation();
      return definitions;
   }

   private void processImports(TWSDLParserContextImpl context) {
      Iterator var2 = this.forest.getExternalReferences().iterator();

      while(var2.hasNext()) {
         String location = (String)var2.next();
         if (!context.getDocument().isImportedDocument(location)) {
            Document doc = this.forest.get(location);
            if (doc != null) {
               Definitions importedDefinitions = this.parseDefinitionsNoImport(context, doc);
               if (importedDefinitions != null) {
                  context.getDocument().addImportedEntity(importedDefinitions);
                  context.getDocument().addImportedDocument(location);
               }
            }
         }
      }

   }

   private Definitions parseDefinitionsNoImport(TWSDLParserContextImpl context, Document doc) {
      Element e = doc.getDocumentElement();
      if (e.getNamespaceURI() != null && e.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/") && e.getLocalName().equals("definitions")) {
         context.push();
         context.registerNamespaces(e);
         Definitions definitions = new Definitions(context.getDocument(), this.forest.locatorTable.getStartLocation(e));
         String name = XmlUtil.getAttributeOrNull(e, "name");
         definitions.setName(name);
         String targetNamespaceURI = XmlUtil.getAttributeOrNull(e, "targetNamespace");
         definitions.setTargetNamespaceURI(targetNamespaceURI);
         boolean gotDocumentation = false;
         boolean gotTypes = false;
         Iterator iter = XmlUtil.getAllChildren(e);

         while(iter.hasNext()) {
            Element e2 = Util.nextElement(iter);
            if (e2 == null) {
               break;
            }

            if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_DOCUMENTATION)) {
               if (gotDocumentation) {
                  this.errReceiver.error(this.forest.locatorTable.getStartLocation(e2), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
                  return null;
               }

               gotDocumentation = true;
               if (definitions.getDocumentation() == null) {
                  definitions.setDocumentation(this.getDocumentationFor(e2));
               }
            } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_TYPES)) {
               if (gotTypes && !this.options.isExtensionMode()) {
                  this.errReceiver.error(this.forest.locatorTable.getStartLocation(e2), WsdlMessages.PARSING_ONLY_ONE_TYPES_ALLOWED("definitions"));
                  return null;
               }

               gotTypes = true;
               if (!this.options.isExtensionMode()) {
                  this.validateSchemaImports(e2);
               }
            } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_MESSAGE)) {
               Message message = this.parseMessage(context, definitions, e2);
               definitions.add(message);
            } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_PORT_TYPE)) {
               PortType portType = this.parsePortType(context, definitions, e2);
               definitions.add(portType);
            } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_BINDING)) {
               Binding binding = this.parseBinding(context, definitions, e2);
               definitions.add(binding);
            } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_SERVICE)) {
               Service service = this.parseService(context, definitions, e2);
               definitions.add(service);
            } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_IMPORT)) {
               definitions.add(this.parseImport(context, definitions, e2));
            } else if (XmlUtil.matchesTagNS(e2, SchemaConstants.QNAME_IMPORT)) {
               this.errReceiver.warning(this.forest.locatorTable.getStartLocation(e2), WsdlMessages.WARNING_WSI_R_2003());
            } else {
               this.checkNotWsdlElement(e2);
               if (!this.handleExtension(context, definitions, e2)) {
                  this.checkNotWsdlRequired(e2);
               }
            }
         }

         context.pop();
         context.fireDoneParsingEntity(WSDLConstants.QNAME_DEFINITIONS, definitions);
         return definitions;
      } else {
         return null;
      }
   }

   private Message parseMessage(TWSDLParserContextImpl context, Definitions definitions, Element e) {
      context.push();
      context.registerNamespaces(e);
      Message message = new Message(definitions, this.forest.locatorTable.getStartLocation(e), this.errReceiver);
      String name = Util.getRequiredAttribute(e, "name");
      message.setName(name);
      boolean gotDocumentation = false;
      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_DOCUMENTATION)) {
            if (gotDocumentation) {
               Util.fail("parsing.onlyOneDocumentationAllowed", e.getLocalName());
            }

            gotDocumentation = true;
            message.setDocumentation(this.getDocumentationFor(e2));
         } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_PART)) {
            MessagePart part = this.parseMessagePart(context, e2);
            message.add(part);
         }
      }

      context.pop();
      context.fireDoneParsingEntity(WSDLConstants.QNAME_MESSAGE, message);
      return message;
   }

   private MessagePart parseMessagePart(TWSDLParserContextImpl context, Element e) {
      context.push();
      context.registerNamespaces(e);
      MessagePart part = new MessagePart(this.forest.locatorTable.getStartLocation(e));
      String partName = Util.getRequiredAttribute(e, "name");
      part.setName(partName);
      String elementAttr = XmlUtil.getAttributeOrNull(e, "element");
      String typeAttr = XmlUtil.getAttributeOrNull(e, "type");
      if (elementAttr != null) {
         if (typeAttr != null) {
            this.errReceiver.error(context.getLocation(e), WsdlMessages.PARSING_ONLY_ONE_OF_ELEMENT_OR_TYPE_REQUIRED(partName));
         }

         part.setDescriptor(context.translateQualifiedName(context.getLocation(e), elementAttr));
         part.setDescriptorKind(SchemaKinds.XSD_ELEMENT);
      } else if (typeAttr != null) {
         part.setDescriptor(context.translateQualifiedName(context.getLocation(e), typeAttr));
         part.setDescriptorKind(SchemaKinds.XSD_TYPE);
      } else {
         this.errReceiver.warning(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ELEMENT_OR_TYPE_REQUIRED(partName));
      }

      context.pop();
      context.fireDoneParsingEntity(WSDLConstants.QNAME_PART, part);
      return part;
   }

   private PortType parsePortType(TWSDLParserContextImpl context, Definitions definitions, Element e) {
      context.push();
      context.registerNamespaces(e);
      PortType portType = new PortType(definitions, this.forest.locatorTable.getStartLocation(e), this.errReceiver);
      String name = Util.getRequiredAttribute(e, "name");
      portType.setName(name);
      boolean gotDocumentation = false;
      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_DOCUMENTATION)) {
            if (gotDocumentation) {
               this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
            }

            gotDocumentation = true;
            if (portType.getDocumentation() == null) {
               portType.setDocumentation(this.getDocumentationFor(e2));
            }
         } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_OPERATION)) {
            Operation op = this.parsePortTypeOperation(context, e2);
            op.setParent(portType);
            portType.add(op);
         } else {
            this.checkNotWsdlElement(e2);
            if (!this.handleExtension(context, portType, e2)) {
               this.checkNotWsdlRequired(e2);
            }
         }
      }

      context.pop();
      context.fireDoneParsingEntity(WSDLConstants.QNAME_PORT_TYPE, portType);
      return portType;
   }

   private Operation parsePortTypeOperation(TWSDLParserContextImpl context, Element e) {
      context.push();
      context.registerNamespaces(e);
      Operation operation = new Operation(this.forest.locatorTable.getStartLocation(e));
      String name = Util.getRequiredAttribute(e, "name");
      operation.setName(name);
      String parameterOrderAttr = XmlUtil.getAttributeOrNull(e, "parameterOrder");
      operation.setParameterOrder(parameterOrderAttr);
      boolean gotDocumentation = false;
      boolean gotInput = false;
      boolean gotOutput = false;
      boolean gotFault = false;
      boolean inputBeforeOutput = false;
      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_DOCUMENTATION)) {
            if (gotDocumentation) {
               this.errReceiver.error(this.forest.locatorTable.getStartLocation(e2), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e2.getLocalName()));
            }

            gotDocumentation = true;
            if (operation.getDocumentation() == null) {
               operation.setDocumentation(this.getDocumentationFor(e2));
            }
         } else {
            String messageAttr;
            String nameAttr;
            Iterator iter2;
            Attr e3;
            Element e3;
            boolean gotDocumentation2;
            Iterator iter2;
            if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_INPUT)) {
               if (gotInput) {
                  this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_TOO_MANY_ELEMENTS("input", "operation", name));
               }

               context.push();
               context.registerNamespaces(e2);
               Input input = new Input(this.forest.locatorTable.getStartLocation(e2), this.errReceiver);
               input.setParent(operation);
               messageAttr = Util.getRequiredAttribute(e2, "message");
               input.setMessage(context.translateQualifiedName(context.getLocation(e2), messageAttr));
               nameAttr = XmlUtil.getAttributeOrNull(e2, "name");
               input.setName(nameAttr);
               operation.setInput(input);
               gotInput = true;
               if (gotOutput) {
                  inputBeforeOutput = false;
               }

               iter2 = XmlUtil.getAllAttributes(e2);

               while(iter2.hasNext()) {
                  e3 = (Attr)iter2.next();
                  if (!e3.getLocalName().equals("message") && !e3.getLocalName().equals("name")) {
                     this.checkNotWsdlAttribute(e3);
                     this.handleExtension(context, input, e3, e2);
                  }
               }

               gotDocumentation2 = false;
               iter2 = XmlUtil.getAllChildren(e2);

               while(iter2.hasNext()) {
                  e3 = Util.nextElement(iter2);
                  if (e3 == null) {
                     break;
                  }

                  if (XmlUtil.matchesTagNS(e3, WSDLConstants.QNAME_DOCUMENTATION)) {
                     if (gotDocumentation2) {
                        this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
                     }

                     gotDocumentation2 = true;
                     input.setDocumentation(this.getDocumentationFor(e3));
                  } else {
                     this.errReceiver.error(this.forest.locatorTable.getStartLocation(e3), WsdlMessages.PARSING_INVALID_ELEMENT(e3.getTagName(), e3.getNamespaceURI()));
                  }
               }

               context.pop();
            } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_OUTPUT)) {
               if (gotOutput) {
                  this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_TOO_MANY_ELEMENTS("input", "operation", name));
               }

               context.push();
               context.registerNamespaces(e2);
               Output output = new Output(this.forest.locatorTable.getStartLocation(e2), this.errReceiver);
               output.setParent(operation);
               messageAttr = Util.getRequiredAttribute(e2, "message");
               output.setMessage(context.translateQualifiedName(context.getLocation(e2), messageAttr));
               nameAttr = XmlUtil.getAttributeOrNull(e2, "name");
               output.setName(nameAttr);
               operation.setOutput(output);
               gotOutput = true;
               if (gotInput) {
                  inputBeforeOutput = true;
               }

               iter2 = XmlUtil.getAllAttributes(e2);

               while(iter2.hasNext()) {
                  e3 = (Attr)iter2.next();
                  if (!e3.getLocalName().equals("message") && !e3.getLocalName().equals("name")) {
                     this.checkNotWsdlAttribute(e3);
                     this.handleExtension(context, output, e3, e2);
                  }
               }

               gotDocumentation2 = false;
               iter2 = XmlUtil.getAllChildren(e2);

               while(iter2.hasNext()) {
                  e3 = Util.nextElement(iter2);
                  if (e3 == null) {
                     break;
                  }

                  if (XmlUtil.matchesTagNS(e3, WSDLConstants.QNAME_DOCUMENTATION)) {
                     if (gotDocumentation2) {
                        this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
                     }

                     gotDocumentation2 = true;
                     output.setDocumentation(this.getDocumentationFor(e3));
                  } else {
                     this.errReceiver.error(this.forest.locatorTable.getStartLocation(e3), WsdlMessages.PARSING_INVALID_ELEMENT(e3.getTagName(), e3.getNamespaceURI()));
                  }
               }

               context.pop();
            } else if (!XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_FAULT)) {
               this.checkNotWsdlElement(e2);
               if (!this.handleExtension(context, operation, e2)) {
                  this.checkNotWsdlRequired(e2);
               }
            } else {
               context.push();
               context.registerNamespaces(e2);
               Fault fault = new Fault(this.forest.locatorTable.getStartLocation(e2));
               fault.setParent(operation);
               messageAttr = Util.getRequiredAttribute(e2, "message");
               fault.setMessage(context.translateQualifiedName(context.getLocation(e2), messageAttr));
               nameAttr = XmlUtil.getAttributeOrNull(e2, "name");
               fault.setName(nameAttr);
               operation.addFault(fault);
               gotFault = true;
               iter2 = XmlUtil.getAllAttributes(e2);

               while(iter2.hasNext()) {
                  e3 = (Attr)iter2.next();
                  if (!e3.getLocalName().equals("message") && !e3.getLocalName().equals("name")) {
                     this.checkNotWsdlAttribute(e3);
                     this.handleExtension(context, fault, e3, e2);
                  }
               }

               gotDocumentation2 = false;
               iter2 = XmlUtil.getAllChildren(e2);

               while(iter2.hasNext()) {
                  e3 = Util.nextElement(iter2);
                  if (e3 == null) {
                     break;
                  }

                  if (XmlUtil.matchesTagNS(e3, WSDLConstants.QNAME_DOCUMENTATION)) {
                     if (gotDocumentation2) {
                        this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
                     }

                     gotDocumentation2 = true;
                     if (fault.getDocumentation() == null) {
                        fault.setDocumentation(this.getDocumentationFor(e3));
                     }
                  } else {
                     this.checkNotWsdlElement(e3);
                     if (!this.handleExtension(context, fault, e3)) {
                        this.checkNotWsdlRequired(e3);
                     }
                  }
               }

               context.pop();
            }
         }
      }

      if (gotInput && !gotOutput && !gotFault) {
         operation.setStyle(OperationStyle.ONE_WAY);
      } else if (gotInput && gotOutput && inputBeforeOutput) {
         operation.setStyle(OperationStyle.REQUEST_RESPONSE);
      } else if (gotInput && gotOutput && !inputBeforeOutput) {
         operation.setStyle(OperationStyle.SOLICIT_RESPONSE);
      } else if (gotOutput && !gotInput && !gotFault) {
         operation.setStyle(OperationStyle.NOTIFICATION);
      } else {
         this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_INVALID_OPERATION_STYLE(name));
      }

      context.pop();
      context.fireDoneParsingEntity(WSDLConstants.QNAME_OPERATION, operation);
      return operation;
   }

   private Binding parseBinding(TWSDLParserContextImpl context, Definitions definitions, Element e) {
      context.push();
      context.registerNamespaces(e);
      Binding binding = new Binding(definitions, this.forest.locatorTable.getStartLocation(e), this.errReceiver);
      String name = Util.getRequiredAttribute(e, "name");
      binding.setName(name);
      String typeAttr = Util.getRequiredAttribute(e, "type");
      binding.setPortType(context.translateQualifiedName(context.getLocation(e), typeAttr));
      boolean gotDocumentation = false;
      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_DOCUMENTATION)) {
            if (gotDocumentation) {
               this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
            }

            gotDocumentation = true;
            binding.setDocumentation(this.getDocumentationFor(e2));
         } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_OPERATION)) {
            BindingOperation op = this.parseBindingOperation(context, e2);
            binding.add(op);
         } else {
            this.checkNotWsdlElement(e2);
            if (!this.handleExtension(context, binding, e2)) {
               this.checkNotWsdlRequired(e2);
            }
         }
      }

      context.pop();
      context.fireDoneParsingEntity(WSDLConstants.QNAME_BINDING, binding);
      return binding;
   }

   private BindingOperation parseBindingOperation(TWSDLParserContextImpl context, Element e) {
      context.push();
      context.registerNamespaces(e);
      BindingOperation operation = new BindingOperation(this.forest.locatorTable.getStartLocation(e));
      String name = Util.getRequiredAttribute(e, "name");
      operation.setName(name);
      boolean gotDocumentation = false;
      boolean gotInput = false;
      boolean gotOutput = false;
      boolean gotFault = false;
      boolean inputBeforeOutput = false;
      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_DOCUMENTATION)) {
            if (gotDocumentation) {
               this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
            }

            gotDocumentation = true;
            operation.setDocumentation(this.getDocumentationFor(e2));
         } else {
            String nameAttr;
            boolean gotDocumentation2;
            Iterator iter2;
            Element e3;
            if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_INPUT)) {
               if (gotInput) {
                  this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_TOO_MANY_ELEMENTS("input", "operation", name));
               }

               context.push();
               context.registerNamespaces(e2);
               BindingInput input = new BindingInput(this.forest.locatorTable.getStartLocation(e2));
               nameAttr = XmlUtil.getAttributeOrNull(e2, "name");
               input.setName(nameAttr);
               operation.setInput(input);
               gotInput = true;
               if (gotOutput) {
                  inputBeforeOutput = false;
               }

               gotDocumentation2 = false;
               iter2 = XmlUtil.getAllChildren(e2);

               while(iter2.hasNext()) {
                  e3 = Util.nextElement(iter2);
                  if (e3 == null) {
                     break;
                  }

                  if (XmlUtil.matchesTagNS(e3, WSDLConstants.QNAME_DOCUMENTATION)) {
                     if (gotDocumentation2) {
                        this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
                     }

                     gotDocumentation2 = true;
                     input.setDocumentation(this.getDocumentationFor(e3));
                  } else {
                     this.checkNotWsdlElement(e3);
                     if (!this.handleExtension(context, input, e3)) {
                        this.checkNotWsdlRequired(e3);
                     }
                  }
               }

               context.pop();
            } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_OUTPUT)) {
               if (gotOutput) {
                  this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_TOO_MANY_ELEMENTS("input", "operation", name));
               }

               context.push();
               context.registerNamespaces(e2);
               BindingOutput output = new BindingOutput(this.forest.locatorTable.getStartLocation(e2));
               nameAttr = XmlUtil.getAttributeOrNull(e2, "name");
               output.setName(nameAttr);
               operation.setOutput(output);
               gotOutput = true;
               if (gotInput) {
                  inputBeforeOutput = true;
               }

               gotDocumentation2 = false;
               iter2 = XmlUtil.getAllChildren(e2);

               while(iter2.hasNext()) {
                  e3 = Util.nextElement(iter2);
                  if (e3 == null) {
                     break;
                  }

                  if (XmlUtil.matchesTagNS(e3, WSDLConstants.QNAME_DOCUMENTATION)) {
                     if (gotDocumentation2) {
                        this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
                     }

                     gotDocumentation2 = true;
                     output.setDocumentation(this.getDocumentationFor(e3));
                  } else {
                     this.checkNotWsdlElement(e3);
                     if (!this.handleExtension(context, output, e3)) {
                        this.checkNotWsdlRequired(e3);
                     }
                  }
               }

               context.pop();
            } else if (!XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_FAULT)) {
               this.checkNotWsdlElement(e2);
               if (!this.handleExtension(context, operation, e2)) {
                  this.checkNotWsdlRequired(e2);
               }
            } else {
               context.push();
               context.registerNamespaces(e2);
               BindingFault fault = new BindingFault(this.forest.locatorTable.getStartLocation(e2));
               nameAttr = Util.getRequiredAttribute(e2, "name");
               fault.setName(nameAttr);
               operation.addFault(fault);
               gotFault = true;
               gotDocumentation2 = false;
               iter2 = XmlUtil.getAllChildren(e2);

               while(iter2.hasNext()) {
                  e3 = Util.nextElement(iter2);
                  if (e3 == null) {
                     break;
                  }

                  if (XmlUtil.matchesTagNS(e3, WSDLConstants.QNAME_DOCUMENTATION)) {
                     if (gotDocumentation2) {
                        this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
                     }

                     gotDocumentation2 = true;
                     if (fault.getDocumentation() == null) {
                        fault.setDocumentation(this.getDocumentationFor(e3));
                     }
                  } else {
                     this.checkNotWsdlElement(e3);
                     if (!this.handleExtension(context, fault, e3)) {
                        this.checkNotWsdlRequired(e3);
                     }
                  }
               }

               context.pop();
            }
         }
      }

      if (gotInput && !gotOutput && !gotFault) {
         operation.setStyle(OperationStyle.ONE_WAY);
      } else if (gotInput && gotOutput && inputBeforeOutput) {
         operation.setStyle(OperationStyle.REQUEST_RESPONSE);
      } else if (gotInput && gotOutput && !inputBeforeOutput) {
         operation.setStyle(OperationStyle.SOLICIT_RESPONSE);
      } else if (gotOutput && !gotInput && !gotFault) {
         operation.setStyle(OperationStyle.NOTIFICATION);
      } else {
         this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_INVALID_OPERATION_STYLE(name));
      }

      context.pop();
      context.fireDoneParsingEntity(WSDLConstants.QNAME_OPERATION, operation);
      return operation;
   }

   private Import parseImport(TWSDLParserContextImpl context, Definitions definitions, Element e) {
      context.push();
      context.registerNamespaces(e);
      Import anImport = new Import(this.forest.locatorTable.getStartLocation(e));
      String namespace = Util.getRequiredAttribute(e, "namespace");
      anImport.setNamespace(namespace);
      String location = Util.getRequiredAttribute(e, "location");
      anImport.setLocation(location);
      boolean gotDocumentation = false;
      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_DOCUMENTATION)) {
            if (gotDocumentation) {
               this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
            }

            gotDocumentation = true;
            anImport.setDocumentation(this.getDocumentationFor(e2));
         } else {
            this.errReceiver.error(this.forest.locatorTable.getStartLocation(e2), WsdlMessages.PARSING_INVALID_ELEMENT(e2.getTagName(), e2.getNamespaceURI()));
         }
      }

      context.pop();
      context.fireDoneParsingEntity(WSDLConstants.QNAME_IMPORT, anImport);
      return anImport;
   }

   private Service parseService(TWSDLParserContextImpl context, Definitions definitions, Element e) {
      context.push();
      context.registerNamespaces(e);
      Service service = new Service(definitions, this.forest.locatorTable.getStartLocation(e), this.errReceiver);
      String name = Util.getRequiredAttribute(e, "name");
      service.setName(name);
      boolean gotDocumentation = false;
      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_DOCUMENTATION)) {
            if (gotDocumentation) {
               this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
            }

            gotDocumentation = true;
            if (service.getDocumentation() == null) {
               service.setDocumentation(this.getDocumentationFor(e2));
            }
         } else if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_PORT)) {
            Port port = this.parsePort(context, definitions, e2);
            service.add(port);
         } else {
            this.checkNotWsdlElement(e2);
            if (!this.handleExtension(context, service, e2)) {
               this.checkNotWsdlRequired(e2);
            }
         }
      }

      context.pop();
      context.fireDoneParsingEntity(WSDLConstants.QNAME_SERVICE, service);
      return service;
   }

   private Port parsePort(TWSDLParserContextImpl context, Definitions definitions, Element e) {
      context.push();
      context.registerNamespaces(e);
      Port port = new Port(definitions, this.forest.locatorTable.getStartLocation(e), this.errReceiver);
      String name = Util.getRequiredAttribute(e, "name");
      port.setName(name);
      String bindingAttr = Util.getRequiredAttribute(e, "binding");
      port.setBinding(context.translateQualifiedName(context.getLocation(e), bindingAttr));
      boolean gotDocumentation = false;
      Iterator iter = XmlUtil.getAllChildren(e);

      while(iter.hasNext()) {
         Element e2 = Util.nextElement(iter);
         if (e2 == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e2, WSDLConstants.QNAME_DOCUMENTATION)) {
            if (gotDocumentation) {
               this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(e.getLocalName()));
            }

            gotDocumentation = true;
            if (port.getDocumentation() == null) {
               port.setDocumentation(this.getDocumentationFor(e2));
            }
         } else {
            this.checkNotWsdlElement(e2);
            if (!this.handleExtension(context, port, e2)) {
               this.checkNotWsdlRequired(e2);
            }
         }
      }

      context.pop();
      context.fireDoneParsingEntity(WSDLConstants.QNAME_PORT, port);
      return port;
   }

   private void validateSchemaImports(Element typesElement) {
      Iterator iter = XmlUtil.getAllChildren(typesElement);

      while(iter.hasNext()) {
         Element e = Util.nextElement(iter);
         if (e == null) {
            break;
         }

         if (XmlUtil.matchesTagNS(e, SchemaConstants.QNAME_IMPORT)) {
            this.errReceiver.warning(this.forest.locatorTable.getStartLocation(e), WsdlMessages.WARNING_WSI_R_2003());
         } else {
            this.checkNotWsdlElement(e);
         }
      }

   }

   private boolean handleExtension(TWSDLParserContextImpl context, TWSDLExtensible entity, Element e) {
      TWSDLExtensionHandler h = (TWSDLExtensionHandler)this.extensionHandlers.get(e.getNamespaceURI());
      if (h == null) {
         context.fireIgnoringExtension(e, (Entity)entity);
         this.errReceiver.warning(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_UNKNOWN_EXTENSIBILITY_ELEMENT_OR_ATTRIBUTE(e.getLocalName(), e.getNamespaceURI()));
         return false;
      } else {
         return h.doHandleExtension(context, entity, e);
      }
   }

   private boolean handleExtension(TWSDLParserContextImpl context, TWSDLExtensible entity, Node n, Element e) {
      TWSDLExtensionHandler h = (TWSDLExtensionHandler)this.extensionHandlers.get(n.getNamespaceURI());
      if (h == null) {
         context.fireIgnoringExtension(e, (Entity)entity);
         this.errReceiver.warning(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_UNKNOWN_EXTENSIBILITY_ELEMENT_OR_ATTRIBUTE(n.getLocalName(), n.getNamespaceURI()));
         return false;
      } else {
         return h.doHandleExtension(context, entity, e);
      }
   }

   private void checkNotWsdlElement(Element e) {
      if (e.getNamespaceURI() != null && e.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/")) {
         this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_INVALID_WSDL_ELEMENT(e.getTagName()));
      }

   }

   private void checkNotWsdlAttribute(Attr a) {
      if ("http://schemas.xmlsoap.org/wsdl/".equals(a.getNamespaceURI())) {
         this.errReceiver.error(this.forest.locatorTable.getStartLocation(a.getOwnerElement()), WsdlMessages.PARSING_INVALID_WSDL_ELEMENT(a.getLocalName()));
      }

   }

   private void checkNotWsdlRequired(Element e) {
      String required = XmlUtil.getAttributeNSOrNull(e, "required", "http://schemas.xmlsoap.org/wsdl/");
      if (required != null && required.equals("true") && !this.options.isExtensionMode()) {
         this.errReceiver.error(this.forest.locatorTable.getStartLocation(e), WsdlMessages.PARSING_REQUIRED_EXTENSIBILITY_ELEMENT(e.getTagName(), e.getNamespaceURI()));
      }

   }

   private Documentation getDocumentationFor(Element e) {
      String s = XmlUtil.getTextForNode(e);
      return s == null ? null : new Documentation(s);
   }
}
