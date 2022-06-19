package com.sun.tools.internal.ws.processor.modeler.wsdl;

import com.sun.codemodel.internal.JType;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.SAXParseException2;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.processor.generator.Names;
import com.sun.tools.internal.ws.processor.model.AbstractType;
import com.sun.tools.internal.ws.processor.model.AsyncOperation;
import com.sun.tools.internal.ws.processor.model.AsyncOperationType;
import com.sun.tools.internal.ws.processor.model.Block;
import com.sun.tools.internal.ws.processor.model.Model;
import com.sun.tools.internal.ws.processor.model.ModelException;
import com.sun.tools.internal.ws.processor.model.ModelObject;
import com.sun.tools.internal.ws.processor.model.Parameter;
import com.sun.tools.internal.ws.processor.model.Request;
import com.sun.tools.internal.ws.processor.model.Response;
import com.sun.tools.internal.ws.processor.model.java.JavaException;
import com.sun.tools.internal.ws.processor.model.java.JavaInterface;
import com.sun.tools.internal.ws.processor.model.java.JavaMethod;
import com.sun.tools.internal.ws.processor.model.java.JavaParameter;
import com.sun.tools.internal.ws.processor.model.java.JavaSimpleType;
import com.sun.tools.internal.ws.processor.model.java.JavaStructureMember;
import com.sun.tools.internal.ws.processor.model.java.JavaType;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBElementMember;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBMapping;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBProperty;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBStructuredType;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBType;
import com.sun.tools.internal.ws.processor.model.jaxb.JAXBTypeAndAnnotation;
import com.sun.tools.internal.ws.processor.model.jaxb.RpcLitStructure;
import com.sun.tools.internal.ws.processor.modeler.JavaSimpleTypeCreator;
import com.sun.tools.internal.ws.processor.util.ClassNameCollector;
import com.sun.tools.internal.ws.resources.ModelerMessages;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wsdl.document.Binding;
import com.sun.tools.internal.ws.wsdl.document.BindingFault;
import com.sun.tools.internal.ws.wsdl.document.BindingOperation;
import com.sun.tools.internal.ws.wsdl.document.Documentation;
import com.sun.tools.internal.ws.wsdl.document.Fault;
import com.sun.tools.internal.ws.wsdl.document.Kinds;
import com.sun.tools.internal.ws.wsdl.document.Message;
import com.sun.tools.internal.ws.wsdl.document.MessagePart;
import com.sun.tools.internal.ws.wsdl.document.Operation;
import com.sun.tools.internal.ws.wsdl.document.OperationStyle;
import com.sun.tools.internal.ws.wsdl.document.Port;
import com.sun.tools.internal.ws.wsdl.document.PortType;
import com.sun.tools.internal.ws.wsdl.document.Service;
import com.sun.tools.internal.ws.wsdl.document.WSDLConstants;
import com.sun.tools.internal.ws.wsdl.document.WSDLDocument;
import com.sun.tools.internal.ws.wsdl.document.jaxws.CustomName;
import com.sun.tools.internal.ws.wsdl.document.jaxws.JAXWSBinding;
import com.sun.tools.internal.ws.wsdl.document.schema.SchemaKinds;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAP12Binding;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPAddress;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPBinding;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPBody;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPFault;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPHeader;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPOperation;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPStyle;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPUse;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.NoSuchEntityException;
import com.sun.tools.internal.ws.wsdl.framework.ParseException;
import com.sun.tools.internal.ws.wsdl.framework.ParserListener;
import com.sun.tools.internal.ws.wsdl.framework.ValidationException;
import com.sun.tools.internal.ws.wsdl.parser.MetadataFinder;
import com.sun.tools.internal.ws.wsdl.parser.WSDLParser;
import com.sun.tools.internal.xjc.api.S2JJAXBModel;
import com.sun.tools.internal.xjc.api.TypeAndAnnotation;
import com.sun.tools.internal.xjc.api.XJC;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

public class WSDLModeler extends WSDLModelerBase {
   private final Map uniqueBodyBlocks = new HashMap();
   private final QName VOID_BODYBLOCK = new QName("");
   private final ClassNameCollector classNameCollector = new ClassNameCollector();
   private final String explicitDefaultPackage;
   private JAXBModelBuilder jaxbModelBuilder;

   public WSDLModeler(WsimportOptions options, ErrorReceiver receiver, MetadataFinder forest) {
      super(options, receiver, forest);
      this.explicitDefaultPackage = options.defaultPackage;
   }

   public Model buildModel() {
      try {
         this.parser = new WSDLParser(this.options, this.errReceiver, this.forest);
         this.parser.addParserListener(new ParserListener() {
            public void ignoringExtension(Entity entity, QName name, QName parent) {
               if (parent.equals(WSDLConstants.QNAME_TYPES) && name.getLocalPart().equals("schema") && !name.getNamespaceURI().equals("")) {
                  WSDLModeler.this.warning(entity, ModelerMessages.WSDLMODELER_WARNING_IGNORING_UNRECOGNIZED_SCHEMA_EXTENSION(name.getNamespaceURI()));
               }

            }

            public void doneParsingEntity(QName element, Entity entity) {
            }
         });
         this.document = this.parser.parse();
         if (this.document != null && this.document.getDefinitions() != null) {
            this.document.validateLocally();
            Model model = this.internalBuildModel(this.document);
            if (model != null && !this.errReceiver.hadError()) {
               this.classNameCollector.process(model);
               if (this.classNameCollector.getConflictingClassNames().isEmpty()) {
                  if (this.errReceiver.hadError()) {
                     return null;
                  }

                  return model;
               }

               model = this.internalBuildModel(this.document);
               this.classNameCollector.process(model);
               if (this.classNameCollector.getConflictingClassNames().isEmpty()) {
                  if (this.errReceiver.hadError()) {
                     return null;
                  }

                  return model;
               }

               StringBuilder conflictList = new StringBuilder();
               boolean first = true;

               for(Iterator iter = this.classNameCollector.getConflictingClassNames().iterator(); iter.hasNext(); conflictList.append((String)iter.next())) {
                  if (!first) {
                     conflictList.append(", ");
                  } else {
                     first = false;
                  }
               }

               this.error(this.document.getDefinitions(), ModelerMessages.WSDLMODELER_UNSOLVABLE_NAMING_CONFLICTS(conflictList.toString()));
               return null;
            }

            return null;
         }

         return null;
      } catch (ModelException var5) {
         this.reportError(this.document.getDefinitions(), var5.getMessage(), var5);
      } catch (ParseException var6) {
         this.errReceiver.error(var6);
      } catch (ValidationException var7) {
         this.errReceiver.error(var7.getMessage(), var7);
      } catch (SAXException var8) {
         this.errReceiver.error(var8);
      } catch (IOException var9) {
         this.errReceiver.error(var9);
      }

      return null;
   }

   private Model internalBuildModel(WSDLDocument document) {
      ++this.numPasses;
      this.buildJAXBModel(document);
      QName modelName = new QName(document.getDefinitions().getTargetNamespaceURI(), document.getDefinitions().getName() == null ? "model" : document.getDefinitions().getName());
      Model model = new Model(modelName, document.getDefinitions());
      model.setJAXBModel(this.getJAXBModelBuilder().getJAXBModel());
      model.setProperty("com.sun.xml.internal.ws.processor.model.ModelerName", "com.sun.xml.internal.ws.processor.modeler.wsdl.WSDLModeler");
      this._javaExceptions = new HashMap();
      this._bindingNameToPortMap = new HashMap();
      model.setTargetNamespaceURI(document.getDefinitions().getTargetNamespaceURI());
      setDocumentationIfPresent(model, document.getDefinitions().getDocumentation());
      boolean hasServices = document.getDefinitions().services().hasNext();
      if (hasServices) {
         Iterator iter = document.getDefinitions().services();

         while(iter.hasNext()) {
            this.processService((Service)iter.next(), model, document);
         }
      } else {
         this.warning(model.getEntity(), ModelerMessages.WSDLMODELER_WARNING_NO_SERVICE_DEFINITIONS_FOUND());
      }

      return model;
   }

   protected void processService(Service wsdlService, Model model, WSDLDocument document) {
      QName serviceQName = getQNameOf(wsdlService);
      String serviceInterface = this.getServiceInterfaceName(serviceQName, wsdlService);
      if (this.isConflictingServiceClassName(serviceInterface)) {
         serviceInterface = serviceInterface + "_Service";
      }

      com.sun.tools.internal.ws.processor.model.Service service = new com.sun.tools.internal.ws.processor.model.Service(serviceQName, new JavaInterface(serviceInterface, serviceInterface + "Impl"), wsdlService);
      setDocumentationIfPresent(service, wsdlService.getDocumentation());
      boolean hasPorts = false;

      boolean processed;
      for(Iterator iter = wsdlService.ports(); iter.hasNext(); hasPorts = hasPorts || processed) {
         processed = this.processPort((Port)iter.next(), service, document);
      }

      if (!hasPorts) {
         this.warning(wsdlService, ModelerMessages.WSDLMODELER_WARNING_NO_PORTS_IN_SERVICE(wsdlService.getName()));
      } else {
         model.addService(service);
      }

   }

   protected boolean processPort(Port wsdlPort, com.sun.tools.internal.ws.processor.model.Service service, WSDLDocument document) {
      try {
         this.uniqueBodyBlocks.clear();
         QName portQName = getQNameOf(wsdlPort);
         com.sun.tools.internal.ws.processor.model.Port port = new com.sun.tools.internal.ws.processor.model.Port(portQName, wsdlPort);
         setDocumentationIfPresent(port, wsdlPort.getDocumentation());
         SOAPAddress soapAddress = (SOAPAddress)getExtensionOfType(wsdlPort, SOAPAddress.class);
         if (soapAddress == null) {
            if (!this.options.isExtensionMode()) {
               this.warning(wsdlPort, ModelerMessages.WSDLMODELER_WARNING_IGNORING_NON_SOAP_PORT_NO_ADDRESS(wsdlPort.getName()));
               return false;
            }

            this.warning(wsdlPort, ModelerMessages.WSDLMODELER_WARNING_NO_SOAP_ADDRESS(wsdlPort.getName()));
         }

         if (soapAddress != null) {
            port.setAddress(soapAddress.getLocation());
         }

         Binding binding = wsdlPort.resolveBinding(document);
         QName bindingName = getQNameOf(binding);
         PortType portType = binding.resolvePortType(document);
         port.setProperty("com.sun.xml.internal.ws.processor.model.WSDLPortName", getQNameOf(wsdlPort));
         port.setProperty("com.sun.xml.internal.ws.processor.model.WSDLPortTypeName", getQNameOf(portType));
         port.setProperty("com.sun.xml.internal.ws.processor.model.WSDLBindingName", bindingName);
         boolean isProvider = this.isProvider(wsdlPort);
         if (this._bindingNameToPortMap.containsKey(bindingName) && !isProvider) {
            com.sun.tools.internal.ws.processor.model.Port existingPort = (com.sun.tools.internal.ws.processor.model.Port)this._bindingNameToPortMap.get(bindingName);
            port.setOperations(existingPort.getOperations());
            port.setJavaInterface(existingPort.getJavaInterface());
            port.setStyle(existingPort.getStyle());
            port.setWrapped(existingPort.isWrapped());
         } else {
            SOAPBinding soapBinding = (SOAPBinding)getExtensionOfType(binding, SOAPBinding.class);
            if (soapBinding == null) {
               soapBinding = (SOAPBinding)getExtensionOfType(binding, SOAP12Binding.class);
               if (soapBinding == null) {
                  if (!this.options.isExtensionMode()) {
                     this.warning(wsdlPort, ModelerMessages.WSDLMODELER_WARNING_IGNORING_NON_SOAP_PORT(wsdlPort.getName()));
                     return false;
                  }

                  this.warning(wsdlPort, ModelerMessages.WSDLMODELER_WARNING_NON_SOAP_PORT(wsdlPort.getName()));
               } else {
                  if (!this.options.isExtensionMode()) {
                     this.warning(wsdlPort, ModelerMessages.WSDLMODELER_WARNING_IGNORING_SOAP_BINDING_12(wsdlPort.getName()));
                     return false;
                  }

                  this.warning(wsdlPort, ModelerMessages.WSDLMODELER_WARNING_PORT_SOAP_BINDING_12(wsdlPort.getName()));
               }
            }

            if (soapBinding != null && (soapBinding.getTransport() == null || !soapBinding.getTransport().equals("http://schemas.xmlsoap.org/soap/http") && !soapBinding.getTransport().equals("http://www.w3.org/2003/05/soap/bindings/HTTP/")) && !this.options.isExtensionMode()) {
               this.warning(wsdlPort, ModelerMessages.WSDLMODELER_WARNING_IGNORING_SOAP_BINDING_NON_HTTP_TRANSPORT(wsdlPort.getName()));
               return false;
            }

            if (soapBinding != null && !this.validateWSDLBindingStyle(binding)) {
               if (this.options.isExtensionMode()) {
                  this.warning(wsdlPort, ModelerMessages.WSDLMODELER_WARNING_PORT_SOAP_BINDING_MIXED_STYLE(wsdlPort.getName()));
               } else {
                  this.error(wsdlPort, ModelerMessages.WSDLMODELER_WARNING_IGNORING_SOAP_BINDING_MIXED_STYLE(wsdlPort.getName()));
               }
            }

            if (soapBinding != null) {
               port.setStyle(soapBinding.getStyle());
            }

            boolean hasOverloadedOperations = false;
            Set operationNames = new HashSet();
            Iterator iter = portType.operations();

            Iterator itr;
            BindingOperation bindingOperation;
            while(iter.hasNext()) {
               Operation operation = (Operation)iter.next();
               if (operationNames.contains(operation.getName())) {
                  hasOverloadedOperations = true;
                  break;
               }

               operationNames.add(operation.getName());
               itr = binding.operations();

               while(iter.hasNext()) {
                  bindingOperation = (BindingOperation)itr.next();
                  if (operation.getName().equals(bindingOperation.getName())) {
                     break;
                  }

                  if (!itr.hasNext()) {
                     this.error(bindingOperation, ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_NOT_FOUND(operation.getName(), bindingOperation.getName()));
                  }
               }
            }

            Map headers = new HashMap();
            boolean hasOperations = false;
            itr = binding.operations();

            while(itr.hasNext()) {
               bindingOperation = (BindingOperation)itr.next();
               Operation portTypeOperation = null;
               Set operations = portType.getOperationsNamed(bindingOperation.getName());
               if (operations.isEmpty()) {
                  this.error(bindingOperation, ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_NOT_IN_PORT_TYPE(bindingOperation.getName(), binding.getName()));
               } else if (operations.size() == 1) {
                  portTypeOperation = (Operation)operations.iterator().next();
               } else {
                  boolean found = false;
                  String expectedInputName = bindingOperation.getInput().getName();
                  String expectedOutputName = bindingOperation.getOutput().getName();
                  Iterator iter2 = operations.iterator();

                  while(iter2.hasNext()) {
                     Operation candidateOperation = (Operation)iter2.next();
                     if (expectedInputName == null) {
                        this.error(bindingOperation, ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_MISSING_INPUT_NAME(bindingOperation.getName()));
                     }

                     if (expectedOutputName == null) {
                        this.error(bindingOperation, ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_MISSING_OUTPUT_NAME(bindingOperation.getName()));
                     }

                     if (expectedInputName.equals(candidateOperation.getInput().getName()) && expectedOutputName.equals(candidateOperation.getOutput().getName())) {
                        if (found) {
                           this.error(bindingOperation, ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_MULTIPLE_MATCHING_OPERATIONS(bindingOperation.getName(), bindingOperation.getName()));
                        }

                        found = true;
                        portTypeOperation = candidateOperation;
                     }
                  }

                  if (!found) {
                     this.error(bindingOperation, ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_NOT_FOUND(bindingOperation.getName(), binding.getName()));
                  }
               }

               if (!isProvider) {
                  this.info = new WSDLModelerBase.ProcessSOAPOperationInfo(port, wsdlPort, portTypeOperation, bindingOperation, soapBinding, document, hasOverloadedOperations, headers);
                  com.sun.tools.internal.ws.processor.model.Operation operation;
                  if (soapBinding != null) {
                     operation = this.processSOAPOperation();
                  } else {
                     operation = this.processNonSOAPOperation();
                  }

                  if (operation != null) {
                     port.addOperation(operation);
                     hasOperations = true;
                  }
               }
            }

            if (!isProvider && !hasOperations) {
               this.warning(wsdlPort, ModelerMessages.WSDLMODELER_WARNING_NO_OPERATIONS_IN_PORT(wsdlPort.getName()));
               return false;
            }

            this.createJavaInterfaceForPort(port, isProvider);
            PortType pt = binding.resolvePortType(document);
            String jd = pt.getDocumentation() != null ? pt.getDocumentation().getContent() : null;
            port.getJavaInterface().setJavaDoc(jd);
            this._bindingNameToPortMap.put(bindingName, port);
         }

         service.addPort(port);
         this.applyPortMethodCustomization(port, wsdlPort);
         this.applyWrapperStyleCustomization(port, binding.resolvePortType(document));
         return true;
      } catch (NoSuchEntityException var25) {
         this.warning(document.getDefinitions(), var25.getMessage());
         return false;
      }
   }

   private com.sun.tools.internal.ws.processor.model.Operation processNonSOAPOperation() {
      com.sun.tools.internal.ws.processor.model.Operation operation = new com.sun.tools.internal.ws.processor.model.Operation(new QName((String)null, this.info.bindingOperation.getName()), this.info.bindingOperation);
      setDocumentationIfPresent(operation, this.info.portTypeOperation.getDocumentation());
      if (this.info.portTypeOperation.getStyle() != OperationStyle.REQUEST_RESPONSE && this.info.portTypeOperation.getStyle() != OperationStyle.ONE_WAY) {
         if (this.options.isExtensionMode()) {
            this.warning(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_WARNING_IGNORING_OPERATION_NOT_SUPPORTED_STYLE(this.info.portTypeOperation.getName()));
            return null;
         }

         this.error(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_INVALID_OPERATION_NOT_SUPPORTED_STYLE(this.info.portTypeOperation.getName(), this.info.port.resolveBinding(this.document).resolvePortType(this.document).getName()));
      }

      boolean isRequestResponse = this.info.portTypeOperation.getStyle() == OperationStyle.REQUEST_RESPONSE;
      Message inputMessage = this.getInputMessage();
      Request request = new Request(inputMessage, this.errReceiver);
      request.setErrorReceiver(this.errReceiver);
      this.info.operation = operation;
      this.info.operation.setWSDLPortTypeOperation(this.info.portTypeOperation);
      Message outputMessage = null;
      Response response;
      if (isRequestResponse) {
         outputMessage = this.getOutputMessage();
         response = new Response(outputMessage, this.errReceiver);
      } else {
         response = new Response((Message)null, this.errReceiver);
      }

      this.setNonSoapStyle(inputMessage, outputMessage);
      List parameterList = this.getParameterOrder();
      boolean unwrappable = this.isUnwrappable();
      this.info.operation.setWrapped(unwrappable);
      List params = this.getDoclitParameters(request, response, parameterList);
      if (!this.validateParameterName(params)) {
         return null;
      } else {
         List definitiveParameterList = new ArrayList();
         Iterator var11 = params.iterator();

         while(var11.hasNext()) {
            Parameter param = (Parameter)var11.next();
            if (param.isReturn()) {
               this.info.operation.setProperty("com.sun.xml.internal.ws.processor.modeler.wsdl.resultParameter", param);
               response.addParameter(param);
            } else {
               if (param.isIN()) {
                  request.addParameter(param);
               } else if (param.isOUT()) {
                  response.addParameter(param);
               } else if (param.isINOUT()) {
                  request.addParameter(param);
                  response.addParameter(param);
               }

               definitiveParameterList.add(param);
            }
         }

         this.info.operation.setRequest(request);
         if (isRequestResponse) {
            this.info.operation.setResponse(response);
         }

         Set duplicateNames = this.getDuplicateFaultNames();
         this.handleLiteralSOAPFault(response, duplicateNames);
         this.info.operation.setProperty("com.sun.xml.internal.ws.processor.modeler.wsdl.parameterOrder", definitiveParameterList);
         Binding binding = this.info.port.resolveBinding(this.document);
         PortType portType = binding.resolvePortType(this.document);
         if (this.isAsync(portType, this.info.portTypeOperation)) {
            this.warning(portType, "Can not generate Async methods for non-soap binding!");
         }

         return this.info.operation;
      }
   }

   private void setNonSoapStyle(Message inputMessage, Message outputMessage) {
      SOAPStyle style = SOAPStyle.DOCUMENT;
      Iterator var4 = inputMessage.getParts().iterator();

      MessagePart part;
      while(var4.hasNext()) {
         part = (MessagePart)var4.next();
         if (part.getDescriptorKind() == SchemaKinds.XSD_TYPE) {
            style = SOAPStyle.RPC;
         } else {
            style = SOAPStyle.DOCUMENT;
         }
      }

      if (outputMessage != null) {
         var4 = outputMessage.getParts().iterator();

         while(var4.hasNext()) {
            part = (MessagePart)var4.next();
            if (part.getDescriptorKind() == SchemaKinds.XSD_TYPE) {
               style = SOAPStyle.RPC;
            } else {
               style = SOAPStyle.DOCUMENT;
            }
         }
      }

      this.info.modelPort.setStyle(style);
   }

   protected com.sun.tools.internal.ws.processor.model.Operation processSOAPOperation() {
      com.sun.tools.internal.ws.processor.model.Operation operation = new com.sun.tools.internal.ws.processor.model.Operation(new QName((String)null, this.info.bindingOperation.getName()), this.info.bindingOperation);
      setDocumentationIfPresent(operation, this.info.portTypeOperation.getDocumentation());
      if (this.info.portTypeOperation.getStyle() != OperationStyle.REQUEST_RESPONSE && this.info.portTypeOperation.getStyle() != OperationStyle.ONE_WAY) {
         if (this.options.isExtensionMode()) {
            this.warning(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_WARNING_IGNORING_OPERATION_NOT_SUPPORTED_STYLE(this.info.portTypeOperation.getName()));
            return null;
         }

         this.error(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_INVALID_OPERATION_NOT_SUPPORTED_STYLE(this.info.portTypeOperation.getName(), this.info.port.resolveBinding(this.document).resolvePortType(this.document).getName()));
      }

      SOAPStyle soapStyle = this.info.soapBinding.getStyle();
      SOAPOperation soapOperation = (SOAPOperation)getExtensionOfType(this.info.bindingOperation, SOAPOperation.class);
      if (soapOperation != null) {
         if (soapOperation.getStyle() != null) {
            soapStyle = soapOperation.getStyle();
         }

         if (soapOperation.getSOAPAction() != null) {
            operation.setSOAPAction(soapOperation.getSOAPAction());
         }
      }

      operation.setStyle(soapStyle);
      String uniqueOperationName = this.getUniqueName(this.info.portTypeOperation, this.info.hasOverloadedOperations);
      if (this.info.hasOverloadedOperations) {
         operation.setUniqueName(uniqueOperationName);
      }

      this.info.operation = operation;
      SOAPBody soapRequestBody = this.getSOAPRequestBody();
      if (soapRequestBody == null) {
         this.error(this.info.bindingOperation, ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_INPUT_MISSING_SOAP_BODY(this.info.bindingOperation.getName()));
      }

      if (soapStyle == SOAPStyle.RPC) {
         if (soapRequestBody.isEncoded()) {
            if (this.options.isExtensionMode()) {
               this.warning(soapRequestBody, ModelerMessages.WSDLMODELER_20_RPCENC_NOT_SUPPORTED());
               this.processNonSOAPOperation();
            } else {
               this.error(soapRequestBody, ModelerMessages.WSDLMODELER_20_RPCENC_NOT_SUPPORTED());
            }
         }

         return this.processLiteralSOAPOperation(WSDLModeler.StyleAndUse.RPC_LITERAL);
      } else {
         return this.processLiteralSOAPOperation(WSDLModeler.StyleAndUse.DOC_LITERAL);
      }
   }

   protected com.sun.tools.internal.ws.processor.model.Operation processLiteralSOAPOperation(StyleAndUse styleAndUse) {
      if (!this.applyOperationNameCustomization()) {
         return null;
      } else {
         boolean isRequestResponse = this.info.portTypeOperation.getStyle() == OperationStyle.REQUEST_RESPONSE;
         Message inputMessage = this.getInputMessage();
         Request request = new Request(inputMessage, this.errReceiver);
         request.setErrorReceiver(this.errReceiver);
         this.info.operation.setUse(SOAPUse.LITERAL);
         this.info.operation.setWSDLPortTypeOperation(this.info.portTypeOperation);
         SOAPBody soapRequestBody = this.getSOAPRequestBody();
         if (WSDLModeler.StyleAndUse.DOC_LITERAL == styleAndUse && soapRequestBody.getNamespace() != null) {
            this.warning(soapRequestBody, ModelerMessages.WSDLMODELER_WARNING_R_2716("soapbind:body", this.info.bindingOperation.getName()));
         }

         SOAPBody soapResponseBody = null;
         Message outputMessage = null;
         Response response;
         if (isRequestResponse) {
            soapResponseBody = this.getSOAPResponseBody();
            if (this.isOperationDocumentLiteral(styleAndUse) && soapResponseBody.getNamespace() != null) {
               this.warning(soapResponseBody, ModelerMessages.WSDLMODELER_WARNING_R_2716("soapbind:body", this.info.bindingOperation.getName()));
            }

            outputMessage = this.getOutputMessage();
            response = new Response(outputMessage, this.errReceiver);
         } else {
            response = new Response((Message)null, this.errReceiver);
         }

         if (this.validateMimeParts(this.getMimeParts(this.info.bindingOperation.getInput())) && this.validateMimeParts(this.getMimeParts(this.info.bindingOperation.getOutput()))) {
            if (!this.validateBodyParts(this.info.bindingOperation)) {
               if (this.isOperationDocumentLiteral(styleAndUse)) {
                  if (this.options.isExtensionMode()) {
                     this.warning(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_TYPE_MESSAGE_PART(this.info.portTypeOperation.getName()));
                  } else {
                     this.error(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_INVALID_DOCLITOPERATION(this.info.portTypeOperation.getName()));
                  }
               } else if (this.isOperationRpcLiteral(styleAndUse)) {
                  if (this.options.isExtensionMode()) {
                     this.warning(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_ELEMENT_MESSAGE_PART(this.info.portTypeOperation.getName()));
                  } else {
                     this.error(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_INVALID_RPCLITOPERATION(this.info.portTypeOperation.getName()));
                  }
               }

               return null;
            } else {
               List parameterList = this.getParameterOrder();
               if (!this.setMessagePartsBinding(styleAndUse)) {
                  return null;
               } else {
                  List params = null;
                  boolean unwrappable = this.isUnwrappable();
                  this.info.operation.setWrapped(unwrappable);
                  QName body;
                  if (this.isOperationDocumentLiteral(styleAndUse)) {
                     params = this.getDoclitParameters(request, response, parameterList);
                  } else if (this.isOperationRpcLiteral(styleAndUse)) {
                     String operationName = this.info.bindingOperation.getName();
                     Block reqBlock = null;
                     if (inputMessage != null) {
                        body = new QName(this.getRequestNamespaceURI(soapRequestBody), operationName);
                        RpcLitStructure rpcStruct = new RpcLitStructure(body, this.getJAXBModelBuilder().getJAXBModel());
                        rpcStruct.setJavaType(new JavaSimpleType("com.sun.xml.internal.ws.encoding.jaxb.RpcLitPayload", (String)null));
                        reqBlock = new Block(body, rpcStruct, inputMessage);
                        request.addBodyBlock(reqBlock);
                     }

                     Block resBlock = null;
                     if (isRequestResponse && outputMessage != null) {
                        QName name = new QName(this.getResponseNamespaceURI(soapResponseBody), operationName + "Response");
                        RpcLitStructure rpcStruct = new RpcLitStructure(name, this.getJAXBModelBuilder().getJAXBModel());
                        rpcStruct.setJavaType(new JavaSimpleType("com.sun.xml.internal.ws.encoding.jaxb.RpcLitPayload", (String)null));
                        resBlock = new Block(name, rpcStruct, outputMessage);
                        response.addBodyBlock(resBlock);
                     }

                     params = this.getRpcLitParameters(request, response, reqBlock, resBlock, parameterList);
                  }

                  if (!this.validateParameterName(params)) {
                     return null;
                  } else {
                     List definitiveParameterList = new ArrayList();
                     Iterator bb = params.iterator();

                     while(bb.hasNext()) {
                        Parameter param = (Parameter)bb.next();
                        if (param.isReturn()) {
                           this.info.operation.setProperty("com.sun.xml.internal.ws.processor.modeler.wsdl.resultParameter", param);
                           response.addParameter(param);
                        } else {
                           if (param.isIN()) {
                              request.addParameter(param);
                           } else if (param.isOUT()) {
                              response.addParameter(param);
                           } else if (param.isINOUT()) {
                              request.addParameter(param);
                              response.addParameter(param);
                           }

                           definitiveParameterList.add(param);
                        }
                     }

                     this.info.operation.setRequest(request);
                     if (isRequestResponse) {
                        this.info.operation.setResponse(response);
                     }

                     bb = request.getBodyBlocks();
                     com.sun.tools.internal.ws.processor.model.Operation thatOp;
                     if (bb.hasNext()) {
                        body = ((Block)bb.next()).getName();
                        thatOp = (com.sun.tools.internal.ws.processor.model.Operation)this.uniqueBodyBlocks.get(body);
                     } else {
                        body = this.VOID_BODYBLOCK;
                        thatOp = (com.sun.tools.internal.ws.processor.model.Operation)this.uniqueBodyBlocks.get(this.VOID_BODYBLOCK);
                     }

                     if (thatOp != null) {
                        if (this.options.isExtensionMode()) {
                           this.warning(this.info.port, ModelerMessages.WSDLMODELER_NON_UNIQUE_BODY_WARNING(this.info.port.getName(), this.info.operation.getName(), thatOp.getName(), body));
                        } else {
                           this.error(this.info.port, ModelerMessages.WSDLMODELER_NON_UNIQUE_BODY_ERROR(this.info.port.getName(), this.info.operation.getName(), thatOp.getName(), body));
                        }
                     } else {
                        this.uniqueBodyBlocks.put(body, this.info.operation);
                     }

                     if (this.options.additionalHeaders) {
                        List additionalHeaders = new ArrayList();
                        if (inputMessage != null) {
                           Iterator var17 = this.getAdditionHeaderParts(this.info.bindingOperation, inputMessage, true).iterator();

                           while(var17.hasNext()) {
                              MessagePart part = (MessagePart)var17.next();
                              QName name = part.getDescriptor();
                              JAXBType jaxbType = this.getJAXBType(part);
                              Block block = new Block(name, jaxbType, part);
                              Parameter param = ModelerUtils.createParameter(part.getName(), jaxbType, block);
                              additionalHeaders.add(param);
                              request.addHeaderBlock(block);
                              request.addParameter(param);
                              definitiveParameterList.add(param);
                           }
                        }

                        if (isRequestResponse && outputMessage != null) {
                           List outParams = new ArrayList();
                           Iterator var34 = this.getAdditionHeaderParts(this.info.bindingOperation, outputMessage, false).iterator();

                           while(var34.hasNext()) {
                              MessagePart part = (MessagePart)var34.next();
                              QName name = part.getDescriptor();
                              JAXBType jaxbType = this.getJAXBType(part);
                              Block block = new Block(name, jaxbType, part);
                              Parameter param = ModelerUtils.createParameter(part.getName(), jaxbType, block);
                              param.setMode(Mode.OUT);
                              outParams.add(param);
                              response.addHeaderBlock(block);
                              response.addParameter(param);
                           }

                           var34 = outParams.iterator();

                           while(var34.hasNext()) {
                              Parameter outParam = (Parameter)var34.next();
                              Iterator var39 = additionalHeaders.iterator();

                              while(var39.hasNext()) {
                                 Parameter inParam = (Parameter)var39.next();
                                 if (inParam.getName().equals(outParam.getName()) && inParam.getBlock().getName().equals(outParam.getBlock().getName())) {
                                    inParam.setMode(Mode.INOUT);
                                    outParam.setMode(Mode.INOUT);
                                    break;
                                 }
                              }

                              if (outParam.isOUT()) {
                                 definitiveParameterList.add(outParam);
                              }
                           }
                        }
                     }

                     Set duplicateNames = this.getDuplicateFaultNames();
                     this.handleLiteralSOAPFault(response, duplicateNames);
                     this.info.operation.setProperty("com.sun.xml.internal.ws.processor.modeler.wsdl.parameterOrder", definitiveParameterList);
                     Binding binding = this.info.port.resolveBinding(this.document);
                     PortType portType = binding.resolvePortType(this.document);
                     if (this.isAsync(portType, this.info.portTypeOperation)) {
                        this.addAsyncOperations(this.info.operation, styleAndUse);
                     }

                     return this.info.operation;
                  }
               }
            }
         } else {
            return null;
         }
      }
   }

   private boolean validateParameterName(List params) {
      if (this.options.isExtensionMode()) {
         return true;
      } else {
         Message msg = this.getInputMessage();
         Iterator var3 = params.iterator();

         Parameter param;
         label94:
         do {
            do {
               do {
                  if (!var3.hasNext()) {
                     boolean isRequestResponse = this.info.portTypeOperation.getStyle() == OperationStyle.REQUEST_RESPONSE;
                     if (isRequestResponse) {
                        msg = this.getOutputMessage();
                        Iterator var7 = params.iterator();

                        while(true) {
                           while(true) {
                              Parameter param;
                              do {
                                 if (!var7.hasNext()) {
                                    return true;
                                 }

                                 param = (Parameter)var7.next();
                              } while(param.isIN());

                              if (param.getCustomName() != null) {
                                 if (Names.isJavaReservedWord(param.getCustomName())) {
                                    this.error(param.getEntity(), ModelerMessages.WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOM_NAME(this.info.operation.getName(), param.getCustomName()));
                                    return false;
                                 }

                                 return true;
                              }

                              if (param.isEmbedded() && !(param.getBlock().getType() instanceof RpcLitStructure)) {
                                 if (!param.isReturn() && !param.getName().equals("return") && Names.isJavaReservedWord(param.getName())) {
                                    this.error(param.getEntity(), ModelerMessages.WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_WRAPPER_STYLE(this.info.operation.getName(), param.getName(), param.getBlock().getName()));
                                    return false;
                                 }
                              } else if (!param.isReturn() && Names.isJavaReservedWord(param.getName())) {
                                 this.error(param.getEntity(), ModelerMessages.WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_NON_WRAPPER_STYLE(this.info.operation.getName(), msg.getName(), param.getName()));
                                 return false;
                              }
                           }
                        }
                     }

                     return true;
                  }

                  param = (Parameter)var3.next();
               } while(param.isOUT());

               if (param.getCustomName() != null) {
                  if (Names.isJavaReservedWord(param.getCustomName())) {
                     this.error(param.getEntity(), ModelerMessages.WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOM_NAME(this.info.operation.getName(), param.getCustomName()));
                     return false;
                  }

                  return true;
               }

               if (param.isEmbedded() && !(param.getBlock().getType() instanceof RpcLitStructure)) {
                  continue label94;
               }
            } while(!Names.isJavaReservedWord(param.getName()));

            this.error(param.getEntity(), ModelerMessages.WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_NON_WRAPPER_STYLE(this.info.operation.getName(), msg.getName(), param.getName()));
            return false;
         } while(!Names.isJavaReservedWord(param.getName()));

         this.error(param.getEntity(), ModelerMessages.WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_WRAPPER_STYLE(this.info.operation.getName(), param.getName(), param.getBlock().getName()));
         return false;
      }
   }

   private boolean enableMimeContent() {
      JAXWSBinding jaxwsCustomization = (JAXWSBinding)getExtensionOfType(this.info.bindingOperation, JAXWSBinding.class);
      Boolean mimeContentMapping = jaxwsCustomization != null ? jaxwsCustomization.isEnableMimeContentMapping() : null;
      if (mimeContentMapping != null) {
         return mimeContentMapping;
      } else {
         Binding binding = this.info.port.resolveBinding(this.info.document);
         jaxwsCustomization = (JAXWSBinding)getExtensionOfType(binding, JAXWSBinding.class);
         mimeContentMapping = jaxwsCustomization != null ? jaxwsCustomization.isEnableMimeContentMapping() : null;
         if (mimeContentMapping != null) {
            return mimeContentMapping;
         } else {
            jaxwsCustomization = (JAXWSBinding)getExtensionOfType(this.info.document.getDefinitions(), JAXWSBinding.class);
            mimeContentMapping = jaxwsCustomization != null ? jaxwsCustomization.isEnableMimeContentMapping() : null;
            return mimeContentMapping != null ? mimeContentMapping : false;
         }
      }
   }

   private boolean applyOperationNameCustomization() {
      JAXWSBinding jaxwsCustomization = (JAXWSBinding)getExtensionOfType(this.info.portTypeOperation, JAXWSBinding.class);
      String operationName = jaxwsCustomization != null ? (jaxwsCustomization.getMethodName() != null ? jaxwsCustomization.getMethodName().getName() : null) : null;
      if (operationName != null) {
         if (Names.isJavaReservedWord(operationName)) {
            if (this.options.isExtensionMode()) {
               this.warning(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOMIZED_OPERATION_NAME(this.info.operation.getName(), operationName));
            } else {
               this.error(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOMIZED_OPERATION_NAME(this.info.operation.getName(), operationName));
            }

            return false;
         }

         this.info.operation.setCustomizedName(operationName);
      }

      if (Names.isJavaReservedWord(this.info.operation.getJavaMethodName())) {
         if (this.options.isExtensionMode()) {
            this.warning(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_OPERATION_NAME(this.info.operation.getName()));
         } else {
            this.error(this.info.portTypeOperation, ModelerMessages.WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_OPERATION_NAME(this.info.operation.getName()));
         }

         return false;
      } else {
         return true;
      }
   }

   protected String getAsyncOperationName(com.sun.tools.internal.ws.processor.model.Operation operation) {
      String name = operation.getCustomizedName();
      if (name == null) {
         name = operation.getUniqueName();
      }

      return name;
   }

   private void addAsyncOperations(com.sun.tools.internal.ws.processor.model.Operation syncOperation, StyleAndUse styleAndUse) {
      com.sun.tools.internal.ws.processor.model.Operation operation = this.createAsyncOperation(syncOperation, styleAndUse, AsyncOperationType.POLLING);
      if (operation != null) {
         this.info.modelPort.addOperation(operation);
      }

      operation = this.createAsyncOperation(syncOperation, styleAndUse, AsyncOperationType.CALLBACK);
      if (operation != null) {
         this.info.modelPort.addOperation(operation);
      }

   }

   private com.sun.tools.internal.ws.processor.model.Operation createAsyncOperation(com.sun.tools.internal.ws.processor.model.Operation syncOperation, StyleAndUse styleAndUse, AsyncOperationType asyncType) {
      boolean isRequestResponse = this.info.portTypeOperation.getStyle() == OperationStyle.REQUEST_RESPONSE;
      if (!isRequestResponse) {
         return null;
      } else {
         AsyncOperation operation = new AsyncOperation(this.info.operation, this.info.bindingOperation);
         if (asyncType.equals(AsyncOperationType.CALLBACK)) {
            operation.setUniqueName(this.info.operation.getUniqueName() + "_async_callback");
         } else if (asyncType.equals(AsyncOperationType.POLLING)) {
            operation.setUniqueName(this.info.operation.getUniqueName() + "_async_polling");
         }

         setDocumentationIfPresent(operation, this.info.portTypeOperation.getDocumentation());
         operation.setAsyncType(asyncType);
         operation.setSOAPAction(this.info.operation.getSOAPAction());
         boolean unwrappable = this.info.operation.isWrapped();
         operation.setWrapped(unwrappable);
         SOAPBody soapRequestBody = this.getSOAPRequestBody();
         Message inputMessage = this.getInputMessage();
         Request request = new Request(inputMessage, this.errReceiver);
         SOAPBody soapResponseBody = this.getSOAPResponseBody();
         Message outputMessage = this.getOutputMessage();
         Response response = new Response(outputMessage, this.errReceiver);
         List parameterList = this.getAsynParameterOrder();
         List inParameters = null;
         if (this.isOperationDocumentLiteral(styleAndUse)) {
            inParameters = this.getRequestParameters(request, parameterList);
            if (unwrappable) {
               List unwrappedParameterList = new ArrayList();
               if (inputMessage != null) {
                  Iterator parts = inputMessage.parts();
                  if (parts.hasNext()) {
                     MessagePart part = (MessagePart)parts.next();
                     JAXBType jaxbType = this.getJAXBType(part);
                     List memberList = jaxbType.getWrapperChildren();
                     Iterator props = memberList.iterator();

                     while(props.hasNext()) {
                        JAXBProperty prop = (JAXBProperty)props.next();
                        unwrappedParameterList.add(prop.getElementName().getLocalPart());
                     }
                  }
               }

               parameterList.clear();
               parameterList.addAll(unwrappedParameterList);
            }
         } else if (this.isOperationRpcLiteral(styleAndUse)) {
            String operationName = this.info.bindingOperation.getName();
            Block reqBlock = null;
            if (inputMessage != null) {
               QName name = new QName(this.getRequestNamespaceURI(soapRequestBody), operationName);
               RpcLitStructure rpcStruct = new RpcLitStructure(name, this.getJAXBModelBuilder().getJAXBModel());
               rpcStruct.setJavaType(new JavaSimpleType("com.sun.xml.internal.ws.encoding.jaxb.RpcLitPayload", (String)null));
               reqBlock = new Block(name, rpcStruct, inputMessage);
               request.addBodyBlock(reqBlock);
            }

            inParameters = this.createRpcLitRequestParameters(request, parameterList, reqBlock);
         }

         Iterator blocks = this.info.operation.getResponse().getBodyBlocks();

         while(blocks.hasNext()) {
            response.addBodyBlock((Block)blocks.next());
         }

         blocks = this.info.operation.getResponse().getHeaderBlocks();

         while(blocks.hasNext()) {
            response.addHeaderBlock((Block)blocks.next());
         }

         blocks = this.info.operation.getResponse().getAttachmentBlocks();

         while(blocks.hasNext()) {
            response.addAttachmentBlock((Block)blocks.next());
         }

         List outputParts = outputMessage.getParts();
         int numOfOutMsgParts = outputParts.size();
         if (numOfOutMsgParts == 1) {
            MessagePart part = (MessagePart)outputParts.get(0);
            if (this.isOperationDocumentLiteral(styleAndUse)) {
               JAXBType type = this.getJAXBType(part);
               operation.setResponseBean(type);
            } else if (this.isOperationRpcLiteral(styleAndUse)) {
               String operationName = this.info.bindingOperation.getName();
               Block resBlock = (Block)this.info.operation.getResponse().getBodyBlocksMap().get(new QName(this.getResponseNamespaceURI(soapResponseBody), operationName + "Response"));
               RpcLitStructure resBean = (RpcLitStructure)resBlock.getType();
               List members = resBean.getRpcLitMembers();
               operation.setResponseBean((AbstractType)members.get(0));
            }
         } else {
            String nspace = "";
            QName responseBeanName = new QName(nspace, this.getAsyncOperationName(this.info.operation) + "Response");
            JAXBType responseBeanType = this.getJAXBModelBuilder().getJAXBType(responseBeanName);
            if (responseBeanType == null) {
               this.error(this.info.operation.getEntity(), ModelerMessages.WSDLMODELER_RESPONSEBEAN_NOTFOUND(this.info.operation.getName()));
            }

            operation.setResponseBean(responseBeanType);
         }

         QName respBeanName = new QName(soapResponseBody.getNamespace(), this.getAsyncOperationName(this.info.operation) + "Response");
         Block block = new Block(respBeanName, operation.getResponseBeanType(), outputMessage);
         JavaType respJavaType = operation.getResponseBeanJavaType();
         JAXBType respType = new JAXBType(respBeanName, respJavaType);
         Parameter respParam = ModelerUtils.createParameter(this.info.operation.getName() + "Response", respType, block);
         respParam.setParameterIndex(-1);
         response.addParameter(respParam);
         operation.setProperty("com.sun.xml.internal.ws.processor.modeler.wsdl.resultParameter", respParam.getName());
         int parameterOrderPosition = 0;

         Parameter cbParam;
         for(Iterator var24 = parameterList.iterator(); var24.hasNext(); ++parameterOrderPosition) {
            String name = (String)var24.next();
            cbParam = ModelerUtils.getParameter(name, inParameters);
            if (cbParam == null) {
               if (this.options.isExtensionMode()) {
                  this.warning(this.info.operation.getEntity(), ModelerMessages.WSDLMODELER_WARNING_IGNORING_OPERATION_PART_NOT_FOUND(this.info.operation.getName().getLocalPart(), name));
               } else {
                  this.error(this.info.operation.getEntity(), ModelerMessages.WSDLMODELER_ERROR_PART_NOT_FOUND(this.info.operation.getName().getLocalPart(), name));
               }

               return null;
            }

            request.addParameter(cbParam);
            cbParam.setParameterIndex(parameterOrderPosition);
         }

         operation.setResponse(response);
         if (operation.getAsyncType().equals(AsyncOperationType.CALLBACK)) {
            JavaType cbJavaType = operation.getCallBackType();
            JAXBType callbackType = new JAXBType(respBeanName, cbJavaType);
            cbParam = ModelerUtils.createParameter("asyncHandler", callbackType, block);
            request.addParameter(cbParam);
         }

         operation.setRequest(request);
         return operation;
      }
   }

   protected boolean isAsync(PortType portType, Operation wsdlOperation) {
      JAXWSBinding jaxwsCustomization = (JAXWSBinding)getExtensionOfType(wsdlOperation, JAXWSBinding.class);
      Boolean isAsync = jaxwsCustomization != null ? jaxwsCustomization.isEnableAsyncMapping() : null;
      if (isAsync != null) {
         return isAsync;
      } else {
         jaxwsCustomization = (JAXWSBinding)getExtensionOfType(portType, JAXWSBinding.class);
         isAsync = jaxwsCustomization != null ? jaxwsCustomization.isEnableAsyncMapping() : null;
         if (isAsync != null) {
            return isAsync;
         } else {
            jaxwsCustomization = (JAXWSBinding)getExtensionOfType(this.document.getDefinitions(), JAXWSBinding.class);
            isAsync = jaxwsCustomization != null ? jaxwsCustomization.isEnableAsyncMapping() : null;
            return isAsync != null ? isAsync : false;
         }
      }
   }

   protected void handleLiteralSOAPHeaders(Request request, Response response, Iterator headerParts, Set duplicateNames, @NotNull List definitiveParameterList, boolean processRequest) {
      for(int parameterOrderPosition = definitiveParameterList.size(); headerParts.hasNext(); ++parameterOrderPosition) {
         MessagePart part = (MessagePart)headerParts.next();
         QName headerName = part.getDescriptor();
         JAXBType jaxbType = this.getJAXBType(part);
         Block headerBlock = new Block(headerName, jaxbType, part);
         Object ext;
         if (processRequest) {
            ext = this.info.bindingOperation.getInput();
         } else {
            ext = this.info.bindingOperation.getOutput();
         }

         Message headerMessage = this.getHeaderMessage(part, (TWSDLExtensible)ext);
         if (processRequest) {
            request.addHeaderBlock(headerBlock);
         } else {
            response.addHeaderBlock(headerBlock);
         }

         Parameter parameter = ModelerUtils.createParameter(part.getName(), jaxbType, headerBlock);
         parameter.setParameterIndex(parameterOrderPosition);
         this.setCustomizedParameterName(this.info.bindingOperation, headerMessage, part, parameter, false);
         if (processRequest) {
            request.addParameter(parameter);
            definitiveParameterList.add(parameter.getName());
         } else {
            Iterator var15 = definitiveParameterList.iterator();

            while(var15.hasNext()) {
               String inParamName = (String)var15.next();
               if (inParamName.equals(parameter.getName())) {
                  Parameter inParam = request.getParameterByName(inParamName);
                  parameter.setLinkedParameter(inParam);
                  inParam.setLinkedParameter(parameter);
                  parameter.setParameterIndex(inParam.getParameterIndex());
               }
            }

            if (!definitiveParameterList.contains(parameter.getName())) {
               definitiveParameterList.add(parameter.getName());
            }

            response.addParameter(parameter);
         }
      }

   }

   protected void handleLiteralSOAPFault(Response response, Set duplicateNames) {
      Iterator var3 = this.info.bindingOperation.faults().iterator();

      Iterator var6;
      while(var3.hasNext()) {
         BindingFault bindingFault = (BindingFault)var3.next();
         Fault portTypeFault = null;
         var6 = this.info.portTypeOperation.faults().iterator();

         while(var6.hasNext()) {
            Fault aFault = (Fault)var6.next();
            if (aFault.getName().equals(bindingFault.getName())) {
               if (portTypeFault != null) {
                  this.error(portTypeFault, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_NOT_UNIQUE(bindingFault.getName(), this.info.bindingOperation.getName()));
               }

               portTypeFault = aFault;
            }
         }

         if (portTypeFault == null) {
            this.error(bindingFault, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_NOT_FOUND(bindingFault.getName(), this.info.bindingOperation.getName()));
         }
      }

      var3 = this.info.portTypeOperation.faults().iterator();

      while(true) {
         while(var3.hasNext()) {
            Fault portTypeFault = (Fault)var3.next();
            BindingFault bindingFault = null;
            var6 = this.info.bindingOperation.faults().iterator();

            while(var6.hasNext()) {
               BindingFault bFault = (BindingFault)var6.next();
               if (bFault.getName().equals(portTypeFault.getName())) {
                  bindingFault = bFault;
               }
            }

            if (bindingFault == null) {
               this.warning(portTypeFault, ModelerMessages.WSDLMODELER_INVALID_PORT_TYPE_FAULT_NOT_FOUND(portTypeFault.getName(), this.info.portTypeOperation.getName()));
            }

            String faultName = this.getFaultClassName(portTypeFault);
            com.sun.tools.internal.ws.processor.model.Fault fault = new com.sun.tools.internal.ws.processor.model.Fault(faultName, portTypeFault);
            fault.setWsdlFaultName(portTypeFault.getName());
            setDocumentationIfPresent(fault, portTypeFault.getDocumentation());
            if (bindingFault != null) {
               SOAPFault soapFault = (SOAPFault)getExtensionOfType(bindingFault, SOAPFault.class);
               if (soapFault == null) {
                  if (this.options.isExtensionMode()) {
                     this.warning(bindingFault, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_OUTPUT_MISSING_SOAP_FAULT(bindingFault.getName(), this.info.bindingOperation.getName()));
                     soapFault = new SOAPFault(new LocatorImpl());
                  } else {
                     this.error(bindingFault, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_OUTPUT_MISSING_SOAP_FAULT(bindingFault.getName(), this.info.bindingOperation.getName()));
                  }
               }

               if (!soapFault.isLiteral()) {
                  if (this.options.isExtensionMode()) {
                     this.warning(soapFault, ModelerMessages.WSDLMODELER_WARNING_IGNORING_FAULT_NOT_LITERAL(bindingFault.getName(), this.info.bindingOperation.getName()));
                  } else {
                     this.error(soapFault, ModelerMessages.WSDLMODELER_INVALID_OPERATION_FAULT_NOT_LITERAL(bindingFault.getName(), this.info.bindingOperation.getName()));
                  }
                  continue;
               }

               if (soapFault.getName() == null) {
                  this.warning(bindingFault, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_NO_SOAP_FAULT_NAME(bindingFault.getName(), this.info.bindingOperation.getName()));
               } else if (!soapFault.getName().equals(bindingFault.getName())) {
                  this.warning(soapFault, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_WRONG_SOAP_FAULT_NAME(soapFault.getName(), bindingFault.getName(), this.info.bindingOperation.getName()));
               } else if (soapFault.getNamespace() != null) {
                  this.warning(soapFault, ModelerMessages.WSDLMODELER_WARNING_R_2716_R_2726("soapbind:fault", soapFault.getName()));
               }
            }

            Message faultMessage = portTypeFault.resolveMessage(this.info.document);
            Iterator iter2 = faultMessage.parts();
            if (!iter2.hasNext()) {
               this.error(faultMessage, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_EMPTY_MESSAGE(portTypeFault.getName(), faultMessage.getName()));
            }

            MessagePart faultPart = (MessagePart)iter2.next();
            QName faultQName = faultPart.getDescriptor();
            if (duplicateNames.contains(faultQName)) {
               this.warning(faultPart, ModelerMessages.WSDLMODELER_DUPLICATE_FAULT_SOAP_NAME(portTypeFault.getName(), this.info.portTypeOperation.getName(), faultPart.getName()));
            } else {
               if (iter2.hasNext()) {
                  this.error(faultMessage, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_MESSAGE_HAS_MORE_THAN_ONE_PART(portTypeFault.getName(), faultMessage.getName()));
               }

               if (faultPart.getDescriptorKind() != SchemaKinds.XSD_ELEMENT) {
                  if (this.options.isExtensionMode()) {
                     this.warning(faultPart, ModelerMessages.WSDLMODELER_INVALID_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(faultMessage.getName(), faultPart.getName()));
                  } else {
                     this.error(faultPart, ModelerMessages.WSDLMODELER_INVALID_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(faultMessage.getName(), faultPart.getName()));
                  }
               }

               JAXBType jaxbType = this.getJAXBType(faultPart);
               fault.setElementName(faultPart.getDescriptor());
               fault.setJavaMemberName(Names.getExceptionClassMemberName());
               Block faultBlock = new Block(faultQName, jaxbType, faultPart);
               fault.setBlock(faultBlock);
               if (!response.getFaultBlocksMap().containsKey(faultBlock.getName())) {
                  response.addFaultBlock(faultBlock);
               }

               this.info.operation.addFault(fault);
            }
         }

         return;
      }
   }

   private String getFaultClassName(Fault portTypeFault) {
      JAXWSBinding jaxwsBinding = (JAXWSBinding)getExtensionOfType(portTypeFault, JAXWSBinding.class);
      if (jaxwsBinding != null) {
         CustomName className = jaxwsBinding.getClassName();
         if (className != null) {
            return this.makePackageQualified(className.getName());
         }
      }

      return this.makePackageQualified(BindingHelper.mangleNameToClassName(portTypeFault.getMessage().getLocalPart()));
   }

   protected boolean setMessagePartsBinding(StyleAndUse styleAndUse) {
      SOAPBody inBody = this.getSOAPRequestBody();
      Message inMessage = this.getInputMessage();
      if (!this.setMessagePartsBinding(inBody, inMessage, styleAndUse, true)) {
         return false;
      } else {
         if (this.isRequestResponse()) {
            SOAPBody outBody = this.getSOAPResponseBody();
            Message outMessage = this.getOutputMessage();
            if (!this.setMessagePartsBinding(outBody, outMessage, styleAndUse, false)) {
               return false;
            }
         }

         return true;
      }
   }

   protected boolean setMessagePartsBinding(SOAPBody body, Message message, StyleAndUse styleAndUse, boolean isInput) {
      List bodyParts = this.getBodyParts(body, message);
      List mimeParts;
      List headerParts;
      if (isInput) {
         headerParts = this.getHeaderPartsFromMessage(message, isInput);
         mimeParts = this.getMimeContentParts(message, this.info.bindingOperation.getInput());
      } else {
         headerParts = this.getHeaderPartsFromMessage(message, isInput);
         mimeParts = this.getMimeContentParts(message, this.info.bindingOperation.getOutput());
      }

      Iterator iter;
      MessagePart mPart;
      if (bodyParts == null) {
         bodyParts = new ArrayList();

         for(iter = message.parts(); iter.hasNext(); ((List)bodyParts).add(mPart)) {
            mPart = (MessagePart)iter.next();
            if (mimeParts.contains(mPart) || headerParts.contains(mPart) || this.boundToFault(mPart.getName())) {
               if (this.options.isExtensionMode()) {
                  this.warning(mPart, ModelerMessages.WSDLMODELER_WARNING_BINDING_OPERATION_MULTIPLE_PART_BINDING(this.info.bindingOperation.getName(), mPart.getName()));
               } else {
                  this.error(mPart, ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_MULTIPLE_PART_BINDING(this.info.bindingOperation.getName(), mPart.getName()));
               }
            }
         }
      }

      iter = message.parts();

      while(iter.hasNext()) {
         mPart = (MessagePart)iter.next();
         if (mimeParts.contains(mPart)) {
            mPart.setBindingExtensibilityElementKind(5);
         } else if (headerParts.contains(mPart)) {
            mPart.setBindingExtensibilityElementKind(2);
         } else if (((List)bodyParts).contains(mPart)) {
            mPart.setBindingExtensibilityElementKind(1);
         } else {
            mPart.setBindingExtensibilityElementKind(-1);
         }
      }

      if (this.isOperationDocumentLiteral(styleAndUse) && ((List)bodyParts).size() > 1) {
         if (this.options.isExtensionMode()) {
            this.warning(message, ModelerMessages.WSDLMODELER_WARNING_OPERATION_MORE_THAN_ONE_PART_IN_MESSAGE(this.info.portTypeOperation.getName()));
         } else {
            this.error(message, ModelerMessages.WSDLMODELER_INVALID_OPERATION_MORE_THAN_ONE_PART_IN_MESSAGE(this.info.portTypeOperation.getName()));
         }

         return false;
      } else {
         return true;
      }
   }

   private boolean boundToFault(String partName) {
      Iterator var2 = this.info.bindingOperation.faults().iterator();

      BindingFault bindingFault;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         bindingFault = (BindingFault)var2.next();
      } while(!partName.equals(bindingFault.getName()));

      return true;
   }

   private List getBodyParts(SOAPBody body, Message message) {
      String bodyParts = body.getParts();
      if (bodyParts != null) {
         List partsList = new ArrayList();
         StringTokenizer in = new StringTokenizer(bodyParts.trim(), " ");

         while(in.hasMoreTokens()) {
            String part = in.nextToken();
            MessagePart mPart = message.getPart(part);
            if (null == mPart) {
               this.error(message, ModelerMessages.WSDLMODELER_ERROR_PARTS_NOT_FOUND(part, message.getName()));
            }

            mPart.setBindingExtensibilityElementKind(1);
            partsList.add(mPart);
         }

         return partsList;
      } else {
         return null;
      }
   }

   List getAdditionHeaderParts(BindingOperation bindingOperation, Message message, boolean isInput) {
      List headerParts = new ArrayList();
      List parts = message.getParts();
      List headers = this.getHeaderParts(bindingOperation, isInput);
      Iterator var7 = headers.iterator();

      while(var7.hasNext()) {
         MessagePart part = (MessagePart)var7.next();
         if (!parts.contains(part)) {
            headerParts.add(part);
         }
      }

      return headerParts;
   }

   private List getHeaderPartsFromMessage(Message message, boolean isInput) {
      List headerParts = new ArrayList();
      Iterator parts = message.parts();
      List headers = this.getHeaderParts(this.info.bindingOperation, isInput);

      while(parts.hasNext()) {
         MessagePart part = (MessagePart)parts.next();
         if (headers.contains(part)) {
            headerParts.add(part);
         }
      }

      return headerParts;
   }

   private Message getHeaderMessage(MessagePart part, TWSDLExtensible ext) {
      Iterator headers = this.getHeaderExtensions(ext).iterator();

      while(headers.hasNext()) {
         SOAPHeader header = (SOAPHeader)headers.next();
         if (header.isLiteral()) {
            Message headerMessage = findMessage(header.getMessage(), this.document);
            if (headerMessage != null) {
               MessagePart headerPart = headerMessage.getPart(header.getPart());
               if (headerPart == part) {
                  return headerMessage;
               }
            }
         }
      }

      return null;
   }

   private List getHeaderParts(BindingOperation bindingOperation, boolean isInput) {
      Object ext;
      if (isInput) {
         ext = bindingOperation.getInput();
      } else {
         ext = bindingOperation.getOutput();
      }

      List parts = new ArrayList();
      Iterator headers = this.getHeaderExtensions((TWSDLExtensible)ext).iterator();

      while(headers.hasNext()) {
         SOAPHeader header = (SOAPHeader)headers.next();
         if (!header.isLiteral()) {
            this.error(header, ModelerMessages.WSDLMODELER_INVALID_HEADER_NOT_LITERAL(header.getPart(), bindingOperation.getName()));
         }

         if (header.getNamespace() != null) {
            this.warning(header, ModelerMessages.WSDLMODELER_WARNING_R_2716_R_2726("soapbind:header", bindingOperation.getName()));
         }

         Message headerMessage = findMessage(header.getMessage(), this.document);
         if (headerMessage == null) {
            this.error(header, ModelerMessages.WSDLMODELER_INVALID_HEADER_CANT_RESOLVE_MESSAGE(header.getMessage(), bindingOperation.getName()));
         }

         MessagePart part = headerMessage.getPart(header.getPart());
         if (part == null) {
            this.error(header, ModelerMessages.WSDLMODELER_INVALID_HEADER_NOT_FOUND(header.getPart(), bindingOperation.getName()));
         }

         if (part.getDescriptorKind() != SchemaKinds.XSD_ELEMENT) {
            if (this.options.isExtensionMode()) {
               this.warning(part, ModelerMessages.WSDLMODELER_INVALID_HEADER_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(part.getName(), bindingOperation.getName()));
            } else {
               this.error(part, ModelerMessages.WSDLMODELER_INVALID_HEADER_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(part.getName(), bindingOperation.getName()));
            }
         }

         part.setBindingExtensibilityElementKind(2);
         parts.add(part);
      }

      return parts;
   }

   private boolean isOperationDocumentLiteral(StyleAndUse styleAndUse) {
      return WSDLModeler.StyleAndUse.DOC_LITERAL == styleAndUse;
   }

   private boolean isOperationRpcLiteral(StyleAndUse styleAndUse) {
      return WSDLModeler.StyleAndUse.RPC_LITERAL == styleAndUse;
   }

   private JAXBType getJAXBType(MessagePart part) {
      QName name = part.getDescriptor();
      JAXBType type;
      if (part.getDescriptorKind().equals(SchemaKinds.XSD_ELEMENT)) {
         type = this.getJAXBModelBuilder().getJAXBType(name);
         if (type == null) {
            this.error(part, ModelerMessages.WSDLMODELER_JAXB_JAVATYPE_NOTFOUND(name, part.getName()));
         }
      } else {
         S2JJAXBModel jaxbModel = this.getJAXBModelBuilder().getJAXBModel().getS2JJAXBModel();
         TypeAndAnnotation typeAnno = jaxbModel.getJavaType(name);
         if (typeAnno == null) {
            this.error(part, ModelerMessages.WSDLMODELER_JAXB_JAVATYPE_NOTFOUND(name, part.getName()));
         }

         JavaType javaType = new JavaSimpleType(new JAXBTypeAndAnnotation(typeAnno));
         type = new JAXBType(new QName("", part.getName()), javaType);
      }

      return type;
   }

   private List getDoclitParameters(Request req, Response res, List parameterList) {
      if (parameterList.isEmpty()) {
         return new ArrayList();
      } else {
         List params = new ArrayList();
         Message inMsg = this.getInputMessage();
         Message outMsg = this.getOutputMessage();
         boolean unwrappable = this.isUnwrappable();
         List outParams = null;
         int pIndex = 0;
         Iterator var10 = parameterList.iterator();

         while(true) {
            MessagePart part;
            Block block;
            JAXBStructuredType jaxbStructType;
            label192:
            do {
               while(true) {
                  String inType;
                  TypeAndAnnotation inTa;
                  while(var10.hasNext()) {
                     part = (MessagePart)var10.next();
                     QName reqBodyName = part.getDescriptor();
                     JAXBType jaxbType = this.getJAXBType(part);
                     block = new Block(reqBodyName, jaxbType, part);
                     if (unwrappable) {
                        jaxbStructType = ModelerUtils.createJAXBStructureType(jaxbType);
                        block = new Block(reqBodyName, jaxbStructType, part);
                        if (ModelerUtils.isBoundToSOAPBody(part)) {
                           if (part.isIN()) {
                              req.addBodyBlock(block);
                           } else if (part.isOUT()) {
                              res.addBodyBlock(block);
                           } else if (part.isINOUT()) {
                              req.addBodyBlock(block);
                              res.addBodyBlock(block);
                           }
                        } else if (ModelerUtils.isUnbound(part)) {
                           if (part.isIN()) {
                              req.addUnboundBlock(block);
                           } else if (part.isOUT()) {
                              res.addUnboundBlock(block);
                           } else if (part.isINOUT()) {
                              req.addUnboundBlock(block);
                              res.addUnboundBlock(block);
                           }
                        }

                        if (!part.isIN() && !part.isINOUT()) {
                           continue label192;
                        }

                        params = ModelerUtils.createUnwrappedParameters(jaxbStructType, block);
                        int index = 0;
                        WebParam.Mode mode = part.isINOUT() ? Mode.INOUT : Mode.IN;
                        Iterator var37 = ((List)params).iterator();

                        while(var37.hasNext()) {
                           Parameter param = (Parameter)var37.next();
                           param.setParameterIndex(index++);
                           param.setMode(mode);
                           this.setCustomizedParameterName(this.info.portTypeOperation, inMsg, part, param, unwrappable);
                        }
                     } else {
                        if (ModelerUtils.isBoundToSOAPBody(part)) {
                           if (part.isIN()) {
                              req.addBodyBlock(block);
                           } else if (part.isOUT()) {
                              res.addBodyBlock(block);
                           } else if (part.isINOUT()) {
                              req.addBodyBlock(block);
                              res.addBodyBlock(block);
                           }
                        } else if (ModelerUtils.isBoundToSOAPHeader(part)) {
                           if (part.isIN()) {
                              req.addHeaderBlock(block);
                           } else if (part.isOUT()) {
                              res.addHeaderBlock(block);
                           } else if (part.isINOUT()) {
                              req.addHeaderBlock(block);
                              res.addHeaderBlock(block);
                           }
                        } else if (ModelerUtils.isBoundToMimeContent(part)) {
                           List mimeContents;
                           if (part.isIN()) {
                              mimeContents = this.getMimeContents(this.info.bindingOperation.getInput(), this.getInputMessage(), part.getName());
                              jaxbType = this.getAttachmentType(mimeContents, part);
                              block = new Block(jaxbType.getName(), jaxbType, part);
                              req.addAttachmentBlock(block);
                           } else if (part.isOUT()) {
                              mimeContents = this.getMimeContents(this.info.bindingOperation.getOutput(), this.getOutputMessage(), part.getName());
                              jaxbType = this.getAttachmentType(mimeContents, part);
                              block = new Block(jaxbType.getName(), jaxbType, part);
                              res.addAttachmentBlock(block);
                           } else if (part.isINOUT()) {
                              mimeContents = this.getMimeContents(this.info.bindingOperation.getInput(), this.getInputMessage(), part.getName());
                              jaxbType = this.getAttachmentType(mimeContents, part);
                              block = new Block(jaxbType.getName(), jaxbType, part);
                              req.addAttachmentBlock(block);
                              res.addAttachmentBlock(block);
                              mimeContents = this.getMimeContents(this.info.bindingOperation.getOutput(), this.getOutputMessage(), part.getName());
                              JAXBType outJaxbType = this.getAttachmentType(mimeContents, part);
                              inType = jaxbType.getJavaType().getType().getName();
                              String outType = outJaxbType.getJavaType().getType().getName();
                              inTa = jaxbType.getJavaType().getType().getTypeAnn();
                              TypeAndAnnotation outTa = outJaxbType.getJavaType().getType().getTypeAnn();
                              if (inTa != null && outTa != null && inTa.equals(outTa) && !inType.equals(outType)) {
                                 String javaType = "javax.activation.DataHandler";
                                 JType jt = this.options.getCodeModel().ref(javaType);
                                 JAXBTypeAndAnnotation jaxbTa = jaxbType.getJavaType().getType();
                                 jaxbTa.setType(jt);
                              }
                           }
                        } else if (ModelerUtils.isUnbound(part)) {
                           if (part.isIN()) {
                              req.addUnboundBlock(block);
                           } else if (part.isOUT()) {
                              res.addUnboundBlock(block);
                           } else if (part.isINOUT()) {
                              req.addUnboundBlock(block);
                              res.addUnboundBlock(block);
                           }
                        }

                        Parameter param = ModelerUtils.createParameter(part.getName(), jaxbType, block);
                        param.setMode(part.getMode());
                        if (part.isReturn()) {
                           param.setParameterIndex(-1);
                        } else {
                           param.setParameterIndex(pIndex++);
                        }

                        if (part.isIN()) {
                           this.setCustomizedParameterName(this.info.bindingOperation, inMsg, part, param, false);
                        } else if (outMsg != null) {
                           this.setCustomizedParameterName(this.info.bindingOperation, outMsg, part, param, false);
                        }

                        ((List)params).add(param);
                     }
                  }

                  if (unwrappable && outParams != null) {
                     int index = ((List)params).size();
                     Iterator var25 = outParams.iterator();

                     while(true) {
                        while(var25.hasNext()) {
                           Parameter param = (Parameter)var25.next();
                           if (BindingHelper.mangleNameToVariableName(param.getName()).equals("return")) {
                              param.setParameterIndex(-1);
                           } else {
                              Parameter inParam = ModelerUtils.getParameter(param.getName(), (List)params);
                              if (inParam != null && inParam.isIN()) {
                                 QName inElementName = inParam.getType().getName();
                                 QName outElementName = param.getType().getName();
                                 String inJavaType = inParam.getTypeName();
                                 inType = param.getTypeName();
                                 TypeAndAnnotation inTa = inParam.getType().getJavaType().getType().getTypeAnn();
                                 inTa = param.getType().getJavaType().getType().getTypeAnn();
                                 QName inRawTypeName = ModelerUtils.getRawTypeName(inParam);
                                 QName outRawTypeName = ModelerUtils.getRawTypeName(param);
                                 if (inElementName.getLocalPart().equals(outElementName.getLocalPart()) && inJavaType.equals(inType) && (inTa == null || inTa == null || inTa.equals(inTa)) && (inRawTypeName == null || outRawTypeName == null || inRawTypeName.equals(outRawTypeName))) {
                                    inParam.setMode(Mode.INOUT);
                                    continue;
                                 }
                              }

                              if (outParams.size() == 1) {
                                 param.setParameterIndex(-1);
                              } else {
                                 param.setParameterIndex(index++);
                              }
                           }

                           ((List)params).add(param);
                        }

                        return (List)params;
                     }
                  }

                  return (List)params;
               }
            } while(!part.isOUT());

            outParams = ModelerUtils.createUnwrappedParameters(jaxbStructType, block);
            Iterator var33 = outParams.iterator();

            while(var33.hasNext()) {
               Parameter param = (Parameter)var33.next();
               param.setMode(Mode.OUT);
               this.setCustomizedParameterName(this.info.portTypeOperation, outMsg, part, param, unwrappable);
            }
         }
      }
   }

   private List getRpcLitParameters(Request req, Response res, Block reqBlock, Block resBlock, List paramList) {
      List params = new ArrayList();
      Message inMsg = this.getInputMessage();
      Message outMsg = this.getOutputMessage();
      S2JJAXBModel jaxbModel = ((RpcLitStructure)reqBlock.getType()).getJaxbModel().getS2JJAXBModel();
      List inParams = ModelerUtils.createRpcLitParameters(inMsg, reqBlock, jaxbModel, this.errReceiver);
      List outParams = null;
      if (outMsg != null) {
         outParams = ModelerUtils.createRpcLitParameters(outMsg, resBlock, jaxbModel, this.errReceiver);
      }

      int index = 0;
      Iterator var13 = paramList.iterator();

      while(var13.hasNext()) {
         MessagePart part = (MessagePart)var13.next();
         Parameter param = null;
         if (ModelerUtils.isBoundToSOAPBody(part)) {
            if (part.isIN()) {
               param = ModelerUtils.getParameter(part.getName(), inParams);
            } else if (outParams != null) {
               param = ModelerUtils.getParameter(part.getName(), outParams);
            }
         } else {
            JAXBType type;
            Block mimeBlock;
            QName name;
            if (ModelerUtils.isBoundToSOAPHeader(part)) {
               name = part.getDescriptor();
               type = this.getJAXBType(part);
               mimeBlock = new Block(name, type, part);
               param = ModelerUtils.createParameter(part.getName(), type, mimeBlock);
               if (part.isIN()) {
                  req.addHeaderBlock(mimeBlock);
               } else if (part.isOUT()) {
                  res.addHeaderBlock(mimeBlock);
               } else if (part.isINOUT()) {
                  req.addHeaderBlock(mimeBlock);
                  res.addHeaderBlock(mimeBlock);
               }
            } else if (!ModelerUtils.isBoundToMimeContent(part)) {
               if (ModelerUtils.isUnbound(part)) {
                  name = part.getDescriptor();
                  type = this.getJAXBType(part);
                  mimeBlock = new Block(name, type, part);
                  if (part.isIN()) {
                     req.addUnboundBlock(mimeBlock);
                  } else if (part.isOUT()) {
                     res.addUnboundBlock(mimeBlock);
                  } else if (part.isINOUT()) {
                     req.addUnboundBlock(mimeBlock);
                     res.addUnboundBlock(mimeBlock);
                  }

                  param = ModelerUtils.createParameter(part.getName(), type, mimeBlock);
               }
            } else {
               List mimeContents;
               if (!part.isIN() && !part.isINOUT()) {
                  mimeContents = this.getMimeContents(this.info.bindingOperation.getOutput(), this.getOutputMessage(), part.getName());
               } else {
                  mimeContents = this.getMimeContents(this.info.bindingOperation.getInput(), this.getInputMessage(), part.getName());
               }

               type = this.getAttachmentType(mimeContents, part);
               mimeBlock = new Block(type.getName(), type, part);
               param = ModelerUtils.createParameter(part.getName(), type, mimeBlock);
               if (part.isIN()) {
                  req.addAttachmentBlock(mimeBlock);
               } else if (part.isOUT()) {
                  res.addAttachmentBlock(mimeBlock);
               } else if (part.isINOUT()) {
                  mimeContents = this.getMimeContents(this.info.bindingOperation.getOutput(), this.getOutputMessage(), part.getName());
                  JAXBType outJaxbType = this.getAttachmentType(mimeContents, part);
                  String inType = type.getJavaType().getType().getName();
                  String outType = outJaxbType.getJavaType().getType().getName();
                  if (!inType.equals(outType)) {
                     String javaType = "javax.activation.DataHandler";
                     JType jt = this.options.getCodeModel().ref(javaType);
                     JAXBTypeAndAnnotation jaxbTa = type.getJavaType().getType();
                     jaxbTa.setType(jt);
                  }

                  req.addAttachmentBlock(mimeBlock);
                  res.addAttachmentBlock(mimeBlock);
               }
            }
         }

         if (param != null) {
            if (part.isReturn()) {
               param.setParameterIndex(-1);
            } else {
               param.setParameterIndex(index++);
            }

            param.setMode(part.getMode());
            params.add(param);
         }
      }

      var13 = params.iterator();

      while(var13.hasNext()) {
         Parameter param = (Parameter)var13.next();
         if (param.isIN()) {
            this.setCustomizedParameterName(this.info.portTypeOperation, inMsg, inMsg.getPart(param.getName()), param, false);
         } else if (outMsg != null) {
            this.setCustomizedParameterName(this.info.portTypeOperation, outMsg, outMsg.getPart(param.getName()), param, false);
         }
      }

      return params;
   }

   private List getRequestParameters(Request request, List parameterList) {
      Message inputMessage = this.getInputMessage();
      if (inputMessage != null && !inputMessage.parts().hasNext()) {
         return new ArrayList();
      } else {
         List inParameters = null;
         boolean unwrappable = this.isUnwrappable();
         boolean doneSOAPBody = false;
         Iterator var10 = parameterList.iterator();

         while(true) {
            while(true) {
               MessagePart part;
               do {
                  if (!var10.hasNext()) {
                     return (List)inParameters;
                  }

                  String inParamName = (String)var10.next();
                  part = inputMessage.getPart(inParamName);
               } while(part == null);

               QName reqBodyName = part.getDescriptor();
               JAXBType jaxbReqType = this.getJAXBType(part);
               Block reqBlock;
               if (unwrappable) {
                  JAXBStructuredType jaxbRequestType = ModelerUtils.createJAXBStructureType(jaxbReqType);
                  reqBlock = new Block(reqBodyName, jaxbRequestType, part);
                  if (ModelerUtils.isBoundToSOAPBody(part)) {
                     request.addBodyBlock(reqBlock);
                  } else if (ModelerUtils.isUnbound(part)) {
                     request.addUnboundBlock(reqBlock);
                  }

                  inParameters = ModelerUtils.createUnwrappedParameters(jaxbRequestType, reqBlock);
                  Iterator var14 = ((List)inParameters).iterator();

                  while(var14.hasNext()) {
                     Parameter param = (Parameter)var14.next();
                     this.setCustomizedParameterName(this.info.portTypeOperation, inputMessage, part, param, unwrappable);
                  }
               } else {
                  reqBlock = new Block(reqBodyName, jaxbReqType, part);
                  if (ModelerUtils.isBoundToSOAPBody(part) && !doneSOAPBody) {
                     doneSOAPBody = true;
                     request.addBodyBlock(reqBlock);
                  } else if (ModelerUtils.isBoundToSOAPHeader(part)) {
                     request.addHeaderBlock(reqBlock);
                  } else if (ModelerUtils.isBoundToMimeContent(part)) {
                     List mimeContents = this.getMimeContents(this.info.bindingOperation.getInput(), this.getInputMessage(), part.getName());
                     jaxbReqType = this.getAttachmentType(mimeContents, part);
                     reqBlock = new Block(jaxbReqType.getName(), jaxbReqType, part);
                     request.addAttachmentBlock(reqBlock);
                  } else if (ModelerUtils.isUnbound(part)) {
                     request.addUnboundBlock(reqBlock);
                  }

                  if (inParameters == null) {
                     inParameters = new ArrayList();
                  }

                  Parameter param = ModelerUtils.createParameter(part.getName(), jaxbReqType, reqBlock);
                  this.setCustomizedParameterName(this.info.portTypeOperation, inputMessage, part, param, false);
                  ((List)inParameters).add(param);
               }
            }
         }
      }
   }

   private void setCustomizedParameterName(TWSDLExtensible extension, Message msg, MessagePart part, Parameter param, boolean wrapperStyle) {
      JAXWSBinding jaxwsBinding = (JAXWSBinding)getExtensionOfType(extension, JAXWSBinding.class);
      if (jaxwsBinding != null) {
         String paramName = part.getName();
         QName elementName = part.getDescriptor();
         if (wrapperStyle) {
            elementName = param.getType().getName();
         }

         String customName = jaxwsBinding.getParameterName(msg.getName(), paramName, elementName, wrapperStyle);
         if (customName != null && !customName.equals("")) {
            param.setCustomName(customName);
         }

      }
   }

   protected boolean isConflictingPortClassName(String name) {
      return false;
   }

   protected boolean isUnwrappable() {
      if (!this.getWrapperStyleCustomization()) {
         return false;
      } else {
         Message inputMessage = this.getInputMessage();
         Message outputMessage = this.getOutputMessage();
         if ((inputMessage == null || inputMessage.numParts() == 1) && (outputMessage == null || outputMessage.numParts() == 1)) {
            MessagePart inputPart = inputMessage != null ? (MessagePart)inputMessage.parts().next() : null;
            MessagePart outputPart = outputMessage != null ? (MessagePart)outputMessage.parts().next() : null;
            String operationName = this.info.portTypeOperation.getName();
            if (inputPart != null && !inputPart.getDescriptor().getLocalPart().equals(operationName) || outputPart != null && outputPart.getDescriptorKind() != SchemaKinds.XSD_ELEMENT) {
               return false;
            } else if ((inputPart == null || inputPart.getBindingExtensibilityElementKind() == 1) && (outputPart == null || outputPart.getBindingExtensibilityElementKind() == 1)) {
               if (inputPart != null) {
                  boolean inputWrappable = false;
                  JAXBType inputType = this.getJAXBType(inputPart);
                  if (inputType != null) {
                     inputWrappable = inputType.isUnwrappable();
                  }

                  if (outputPart == null) {
                     return inputWrappable;
                  }

                  JAXBType outputType = this.getJAXBType(outputPart);
                  if (inputType != null && outputType != null) {
                     return inputType.isUnwrappable() && outputType.isUnwrappable();
                  }
               }

               return false;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private boolean getWrapperStyleCustomization() {
      Operation portTypeOperation = this.info.portTypeOperation;
      JAXWSBinding jaxwsBinding = (JAXWSBinding)getExtensionOfType(portTypeOperation, JAXWSBinding.class);
      if (jaxwsBinding != null) {
         Boolean isWrappable = jaxwsBinding.isEnableWrapperStyle();
         if (isWrappable != null) {
            return isWrappable;
         }
      }

      PortType portType = this.info.port.resolveBinding(this.document).resolvePortType(this.document);
      jaxwsBinding = (JAXWSBinding)getExtensionOfType(portType, JAXWSBinding.class);
      Boolean isWrappable;
      if (jaxwsBinding != null) {
         isWrappable = jaxwsBinding.isEnableWrapperStyle();
         if (isWrappable != null) {
            return isWrappable;
         }
      }

      jaxwsBinding = (JAXWSBinding)getExtensionOfType(this.document.getDefinitions(), JAXWSBinding.class);
      if (jaxwsBinding != null) {
         isWrappable = jaxwsBinding.isEnableWrapperStyle();
         if (isWrappable != null) {
            return isWrappable;
         }
      }

      return true;
   }

   protected boolean isSingleInOutPart(Set inputParameterNames, MessagePart outputPart) {
      SOAPOperation soapOperation = (SOAPOperation)getExtensionOfType(this.info.bindingOperation, SOAPOperation.class);
      if (soapOperation == null || !soapOperation.isDocument() && !this.info.soapBinding.isDocument()) {
         if (soapOperation != null && soapOperation.isRPC() || this.info.soapBinding.isRPC()) {
            Message inputMessage = this.getInputMessage();
            if (inputParameterNames.contains(outputPart.getName()) && inputMessage.getPart(outputPart.getName()).getDescriptor().equals(outputPart.getDescriptor())) {
               return true;
            }
         }
      } else {
         Iterator iter = this.getInputMessage().parts();

         while(iter.hasNext()) {
            MessagePart part = (MessagePart)iter.next();
            if (outputPart.getName().equals(part.getName()) && outputPart.getDescriptor().equals(part.getDescriptor())) {
               return true;
            }
         }
      }

      return false;
   }

   private List createRpcLitRequestParameters(Request request, List parameterList, Block block) {
      Message message = this.getInputMessage();
      S2JJAXBModel jaxbModel = ((RpcLitStructure)block.getType()).getJaxbModel().getS2JJAXBModel();
      List parameters = ModelerUtils.createRpcLitParameters(message, block, jaxbModel, this.errReceiver);
      Iterator var7 = parameterList.iterator();

      while(var7.hasNext()) {
         String paramName = (String)var7.next();
         MessagePart part = message.getPart(paramName);
         if (part != null) {
            QName name;
            JAXBType type;
            Block unboundBlock;
            Parameter param;
            if (ModelerUtils.isBoundToSOAPHeader(part)) {
               if (parameters == null) {
                  parameters = new ArrayList();
               }

               name = part.getDescriptor();
               type = this.getJAXBType(part);
               unboundBlock = new Block(name, type, part);
               request.addHeaderBlock(unboundBlock);
               param = ModelerUtils.createParameter(part.getName(), type, unboundBlock);
               if (param != null) {
                  ((List)parameters).add(param);
               }
            } else if (ModelerUtils.isBoundToMimeContent(part)) {
               if (parameters == null) {
                  parameters = new ArrayList();
               }

               List mimeContents = this.getMimeContents(this.info.bindingOperation.getInput(), this.getInputMessage(), paramName);
               type = this.getAttachmentType(mimeContents, part);
               unboundBlock = new Block(type.getName(), type, part);
               request.addAttachmentBlock(unboundBlock);
               param = ModelerUtils.createParameter(part.getName(), type, unboundBlock);
               if (param != null) {
                  ((List)parameters).add(param);
               }
            } else if (ModelerUtils.isUnbound(part)) {
               if (parameters == null) {
                  parameters = new ArrayList();
               }

               name = part.getDescriptor();
               type = this.getJAXBType(part);
               unboundBlock = new Block(name, type, part);
               request.addUnboundBlock(unboundBlock);
               param = ModelerUtils.createParameter(part.getName(), type, unboundBlock);
               if (param != null) {
                  ((List)parameters).add(param);
               }
            }
         }
      }

      var7 = ((List)parameters).iterator();

      while(var7.hasNext()) {
         Parameter param = (Parameter)var7.next();
         this.setCustomizedParameterName(this.info.portTypeOperation, message, message.getPart(param.getName()), param, false);
      }

      return (List)parameters;
   }

   private String getJavaTypeForMimeType(String mimeType) {
      if (!mimeType.equals("image/jpeg") && !mimeType.equals("image/gif")) {
         return !mimeType.equals("text/xml") && !mimeType.equals("application/xml") ? "javax.activation.DataHandler" : "javax.xml.transform.Source";
      } else {
         return "java.awt.Image";
      }
   }

   private JAXBType getAttachmentType(List mimeContents, MessagePart part) {
      if (!this.enableMimeContent()) {
         return this.getJAXBType(part);
      } else {
         List mimeTypes = this.getAlternateMimeTypes(mimeContents);
         String javaType;
         if (mimeTypes.size() > 1) {
            javaType = "javax.activation.DataHandler";
         } else {
            javaType = this.getJavaTypeForMimeType((String)mimeTypes.get(0));
         }

         S2JJAXBModel jaxbModel = this.getJAXBModelBuilder().getJAXBModel().getS2JJAXBModel();
         JType jt = this.options.getCodeModel().ref(javaType);
         QName desc = part.getDescriptor();
         TypeAndAnnotation typeAnno = null;
         if (part.getDescriptorKind() == SchemaKinds.XSD_TYPE) {
            typeAnno = jaxbModel.getJavaType(desc);
            desc = new QName("", part.getName());
         } else if (part.getDescriptorKind() == SchemaKinds.XSD_ELEMENT) {
            typeAnno = this.getJAXBModelBuilder().getElementTypeAndAnn(desc);
            if (typeAnno == null) {
               this.error(part, ModelerMessages.WSDLMODELER_JAXB_JAVATYPE_NOTFOUND(part.getDescriptor(), part.getName()));
            }

            Iterator var9 = mimeTypes.iterator();

            while(var9.hasNext()) {
               String mimeType = (String)var9.next();
               if (!mimeType.equals("text/xml") && !mimeType.equals("application/xml")) {
                  this.warning(part, ModelerMessages.MIMEMODELER_ELEMENT_PART_INVALID_ELEMENT_MIME_TYPE(part.getName(), mimeType));
               }
            }
         }

         if (typeAnno == null) {
            this.error(part, ModelerMessages.WSDLMODELER_JAXB_JAVATYPE_NOTFOUND(desc, part.getName()));
         }

         return new JAXBType(desc, new JavaSimpleType(new JAXBTypeAndAnnotation(typeAnno, jt)), (JAXBMapping)null, this.getJAXBModelBuilder().getJAXBModel());
      }
   }

   protected void buildJAXBModel(WSDLDocument wsdlDocument) {
      JAXBModelBuilder tempJaxbModelBuilder = new JAXBModelBuilder(this.options, this.classNameCollector, this.forest, this.errReceiver);
      if (this.explicitDefaultPackage != null) {
         tempJaxbModelBuilder.getJAXBSchemaCompiler().forcePackageName(this.options.defaultPackage);
      } else {
         this.options.defaultPackage = this.getJavaPackage();
      }

      List schemas = PseudoSchemaBuilder.build(this, this.options, this.errReceiver);
      Iterator var4 = schemas.iterator();

      while(var4.hasNext()) {
         InputSource schema = (InputSource)var4.next();
         tempJaxbModelBuilder.getJAXBSchemaCompiler().parseSchema(schema);
      }

      tempJaxbModelBuilder.bind();
      this.jaxbModelBuilder = tempJaxbModelBuilder;
   }

   protected String getJavaPackage() {
      String jaxwsPackage = null;
      JAXWSBinding jaxwsCustomization = (JAXWSBinding)getExtensionOfType(this.document.getDefinitions(), JAXWSBinding.class);
      if (jaxwsCustomization != null && jaxwsCustomization.getJaxwsPackage() != null) {
         jaxwsPackage = jaxwsCustomization.getJaxwsPackage().getName();
      }

      if (jaxwsPackage != null) {
         return jaxwsPackage;
      } else {
         String wsdlUri = this.document.getDefinitions().getTargetNamespaceURI();
         return XJC.getDefaultPackageName(wsdlUri);
      }
   }

   protected void createJavaInterfaceForProviderPort(com.sun.tools.internal.ws.processor.model.Port port) {
      String interfaceName = "javax.xml.ws.Provider";
      JavaInterface intf = new JavaInterface(interfaceName);
      port.setJavaInterface(intf);
   }

   protected void createJavaInterfaceForPort(com.sun.tools.internal.ws.processor.model.Port port, boolean isProvider) {
      if (isProvider) {
         this.createJavaInterfaceForProviderPort(port);
      } else {
         String interfaceName = this.getJavaNameOfSEI(port);
         if (this.isConflictingPortClassName(interfaceName)) {
            interfaceName = interfaceName + "_PortType";
         }

         JavaInterface intf = new JavaInterface(interfaceName);
         Iterator var5 = port.getOperations().iterator();

         while(var5.hasNext()) {
            com.sun.tools.internal.ws.processor.model.Operation operation = (com.sun.tools.internal.ws.processor.model.Operation)var5.next();
            this.createJavaMethodForOperation(port, operation, intf);
            Iterator var7 = operation.getJavaMethod().getParametersList().iterator();

            while(var7.hasNext()) {
               JavaParameter jParam = (JavaParameter)var7.next();
               Parameter param = jParam.getParameter();
               if (param.getCustomName() != null) {
                  jParam.setName(param.getCustomName());
               }
            }
         }

         port.setJavaInterface(intf);
      }
   }

   protected String getServiceInterfaceName(QName serviceQName, Service wsdlService) {
      String serviceName = wsdlService.getName();
      JAXWSBinding jaxwsCust = (JAXWSBinding)getExtensionOfType(wsdlService, JAXWSBinding.class);
      if (jaxwsCust != null && jaxwsCust.getClassName() != null) {
         CustomName name = jaxwsCust.getClassName();
         if (name != null && !name.getName().equals("")) {
            return this.makePackageQualified(name.getName());
         }
      }

      return this.makePackageQualified(BindingHelper.mangleNameToClassName(serviceName));
   }

   protected String getJavaNameOfSEI(com.sun.tools.internal.ws.processor.model.Port port) {
      QName portTypeName = (QName)port.getProperty("com.sun.xml.internal.ws.processor.model.WSDLPortTypeName");
      PortType pt = (PortType)this.document.find(Kinds.PORT_TYPE, portTypeName);
      port.portTypes.put(portTypeName, pt);
      JAXWSBinding jaxwsCust = (JAXWSBinding)getExtensionOfType(pt, JAXWSBinding.class);
      if (jaxwsCust != null && jaxwsCust.getClassName() != null) {
         CustomName name = jaxwsCust.getClassName();
         if (name != null && !name.getName().equals("")) {
            return this.makePackageQualified(name.getName());
         }
      }

      String interfaceName;
      if (portTypeName != null) {
         interfaceName = this.makePackageQualified(BindingHelper.mangleNameToClassName(portTypeName.getLocalPart()));
      } else {
         interfaceName = this.makePackageQualified(BindingHelper.mangleNameToClassName(port.getName().getLocalPart()));
      }

      return interfaceName;
   }

   private void createJavaMethodForAsyncOperation(com.sun.tools.internal.ws.processor.model.Port port, com.sun.tools.internal.ws.processor.model.Operation operation, JavaInterface intf) {
      String candidateName = this.getJavaNameForOperation(operation);
      JavaMethod method = new JavaMethod(candidateName, this.options, this.errReceiver);

      assert operation.getRequest() != null;

      Response response = operation.getResponse();
      Iterator iter = operation.getRequest().getParameters();

      Parameter parameter;
      JavaType returnType;
      while(iter.hasNext()) {
         parameter = (Parameter)iter.next();
         if (parameter.getJavaParameter() != null) {
            this.error(operation.getEntity(), ModelerMessages.WSDLMODELER_INVALID_OPERATION(operation.getName().getLocalPart()));
         }

         returnType = parameter.getType().getJavaType();
         JavaParameter javaParameter = new JavaParameter(BindingHelper.mangleNameToVariableName(parameter.getName()), returnType, parameter, parameter.getLinkedParameter() != null);
         if (javaParameter.isHolder()) {
            javaParameter.setHolderName(Holder.class.getName());
         }

         method.addParameter(javaParameter);
         parameter.setJavaParameter(javaParameter);
      }

      if (response != null) {
         String resultParameterName = (String)operation.getProperty("com.sun.xml.internal.ws.processor.modeler.wsdl.resultParameter");
         parameter = response.getParameterByName(resultParameterName);
         returnType = parameter.getType().getJavaType();
         method.setReturnType(returnType);
      }

      operation.setJavaMethod(method);
      intf.addMethod(method);
   }

   protected void createJavaMethodForOperation(com.sun.tools.internal.ws.processor.model.Port port, com.sun.tools.internal.ws.processor.model.Operation operation, JavaInterface intf) {
      if (operation instanceof AsyncOperation) {
         this.createJavaMethodForAsyncOperation(port, operation, intf);
      } else {
         String candidateName = this.getJavaNameForOperation(operation);
         JavaMethod method = new JavaMethod(candidateName, this.options, this.errReceiver);
         Parameter returnParam = (Parameter)operation.getProperty("com.sun.xml.internal.ws.processor.modeler.wsdl.resultParameter");
         if (returnParam != null) {
            JavaType parameterType = returnParam.getType().getJavaType();
            method.setReturnType(parameterType);
         } else {
            method.setReturnType(JavaSimpleTypeCreator.VOID_JAVATYPE);
         }

         List parameterOrder = (List)operation.getProperty("com.sun.xml.internal.ws.processor.modeler.wsdl.parameterOrder");
         Iterator var8 = parameterOrder.iterator();

         while(var8.hasNext()) {
            Parameter param = (Parameter)var8.next();
            JavaType parameterType = param.getType().getJavaType();
            String name = param.getCustomName() != null ? param.getCustomName() : param.getName();
            name = BindingHelper.mangleNameToVariableName(name);
            if (Names.isJavaReservedWord(name)) {
               name = "_" + name;
            }

            JavaParameter javaParameter = new JavaParameter(name, parameterType, param, param.isINOUT() || param.isOUT());
            if (javaParameter.isHolder()) {
               javaParameter.setHolderName(Holder.class.getName());
            }

            method.addParameter(javaParameter);
            param.setJavaParameter(javaParameter);
         }

         operation.setJavaMethod(method);
         intf.addMethod(method);
         String opName = BindingHelper.mangleNameToVariableName(operation.getName().getLocalPart());
         Iterator iter = operation.getFaults();

         com.sun.tools.internal.ws.processor.model.Fault fault;
         while(iter != null && iter.hasNext()) {
            fault = (com.sun.tools.internal.ws.processor.model.Fault)iter.next();
            this.createJavaExceptionFromLiteralType(fault, port, opName);
         }

         Iterator iter = operation.getFaults();

         while(iter.hasNext()) {
            fault = (com.sun.tools.internal.ws.processor.model.Fault)iter.next();
            JavaException javaException = fault.getJavaException();
            method.addException(javaException.getName());
         }

      }
   }

   protected boolean createJavaExceptionFromLiteralType(com.sun.tools.internal.ws.processor.model.Fault fault, com.sun.tools.internal.ws.processor.model.Port port, String operationName) {
      JAXBType faultType = (JAXBType)fault.getBlock().getType();
      String exceptionName = fault.getName();
      JAXBStructuredType jaxbStruct = new JAXBStructuredType(new QName(fault.getBlock().getName().getNamespaceURI(), fault.getName()));
      QName memberName = fault.getElementName();
      JAXBElementMember jaxbMember = new JAXBElementMember(memberName, faultType);
      String javaMemberName = this.getLiteralJavaMemberName(fault);
      JavaStructureMember javaMember = new JavaStructureMember(javaMemberName, faultType.getJavaType(), jaxbMember);
      jaxbMember.setJavaStructureMember(javaMember);
      javaMember.setReadMethod(Names.getJavaMemberReadMethod(javaMember));
      javaMember.setInherited(false);
      jaxbMember.setJavaStructureMember(javaMember);
      jaxbStruct.add(jaxbMember);
      if (this.isConflictingExceptionClassName(exceptionName)) {
         exceptionName = exceptionName + "_Exception";
      }

      JavaException existingJavaException = (JavaException)this._javaExceptions.get(exceptionName);
      if (existingJavaException != null && existingJavaException.getName().equals(exceptionName) && (((JAXBType)existingJavaException.getOwner()).getName().equals(jaxbStruct.getName()) || ModelerUtils.isEquivalentLiteralStructures(jaxbStruct, (JAXBStructuredType)existingJavaException.getOwner()))) {
         if (faultType instanceof JAXBStructuredType) {
            fault.getBlock().setType((JAXBType)existingJavaException.getOwner());
         }

         fault.setJavaException(existingJavaException);
         return false;
      } else {
         JavaException javaException = new JavaException(exceptionName, false, jaxbStruct);
         javaException.add(javaMember);
         jaxbStruct.setJavaType(javaException);
         this._javaExceptions.put(javaException.getName(), javaException);
         fault.setJavaException(javaException);
         return true;
      }
   }

   protected boolean isRequestResponse() {
      return this.info.portTypeOperation.getStyle() == OperationStyle.REQUEST_RESPONSE;
   }

   protected List getAsynParameterOrder() {
      List parameterList = new ArrayList();
      Message inputMessage = this.getInputMessage();
      List inputParts = inputMessage.getParts();
      Iterator var4 = inputParts.iterator();

      while(var4.hasNext()) {
         MessagePart part = (MessagePart)var4.next();
         parameterList.add(part.getName());
      }

      return parameterList;
   }

   protected List getParameterOrder() {
      List params = new ArrayList();
      String parameterOrder = this.info.portTypeOperation.getParameterOrder();
      boolean parameterOrderPresent = false;
      Object parameterList;
      if (parameterOrder != null && !parameterOrder.trim().equals("")) {
         parameterList = XmlUtil.parseTokenList(parameterOrder);
         parameterOrderPresent = true;
      } else {
         parameterList = new ArrayList();
      }

      Message inputMessage = this.getInputMessage();
      Message outputMessage = this.getOutputMessage();
      List outputParts = null;
      List inputParts = inputMessage.getParts();
      Iterator var9 = inputParts.iterator();

      MessagePart part;
      while(var9.hasNext()) {
         part = (MessagePart)var9.next();
         part.setMode(Mode.IN);
         part.setReturn(false);
      }

      if (this.isRequestResponse()) {
         outputParts = outputMessage.getParts();
         var9 = outputParts.iterator();

         while(var9.hasNext()) {
            part = (MessagePart)var9.next();
            part.setMode(Mode.OUT);
            part.setReturn(false);
         }
      }

      Iterator paramOrders;
      if (parameterOrderPresent) {
         boolean validParameterOrder = true;
         paramOrders = ((List)parameterList).iterator();

         Iterator var13;
         MessagePart part;
         while(paramOrders.hasNext()) {
            String param = (String)paramOrders.next();
            boolean partFound = false;
            var13 = inputParts.iterator();

            while(var13.hasNext()) {
               part = (MessagePart)var13.next();
               if (param.equals(part.getName())) {
                  partFound = true;
                  break;
               }
            }

            if (!partFound) {
               var13 = outputParts.iterator();

               while(var13.hasNext()) {
                  part = (MessagePart)var13.next();
                  if (param.equals(part.getName())) {
                     partFound = true;
                     break;
                  }
               }
            }

            if (!partFound) {
               this.warning(this.info.operation.getEntity(), ModelerMessages.WSDLMODELER_INVALID_PARAMETERORDER_PARAMETER(param, this.info.operation.getName().getLocalPart()));
               validParameterOrder = false;
            }
         }

         List inputUnlistedParts = new ArrayList();
         List outputUnlistedParts = new ArrayList();
         if (validParameterOrder) {
            var13 = ((List)parameterList).iterator();

            MessagePart inPart;
            while(var13.hasNext()) {
               String param = (String)var13.next();
               inPart = inputMessage.getPart(param);
               if (inPart != null) {
                  params.add(inPart);
               } else if (this.isRequestResponse()) {
                  MessagePart outPart = outputMessage.getPart(param);
                  if (outPart != null) {
                     params.add(outPart);
                  }
               }
            }

            var13 = inputParts.iterator();

            while(var13.hasNext()) {
               part = (MessagePart)var13.next();
               if (!((List)parameterList).contains(part.getName())) {
                  inputUnlistedParts.add(part);
               }
            }

            if (this.isRequestResponse()) {
               var13 = outputParts.iterator();

               label126:
               while(true) {
                  while(true) {
                     while(var13.hasNext()) {
                        part = (MessagePart)var13.next();
                        if (!((List)parameterList).contains(part.getName())) {
                           inPart = inputMessage.getPart(part.getName());
                           if (inPart != null && inPart.getDescriptor().equals(part.getDescriptor())) {
                              inPart.setMode(Mode.INOUT);
                           } else {
                              outputUnlistedParts.add(part);
                           }
                        } else {
                           inPart = inputMessage.getPart(part.getName());
                           if (inPart != null && inPart.getDescriptor().equals(part.getDescriptor())) {
                              inPart.setMode(Mode.INOUT);
                           } else if (!params.contains(part)) {
                              params.add(part);
                           }
                        }
                     }

                     if (outputUnlistedParts.size() == 1) {
                        MessagePart resultPart = (MessagePart)outputUnlistedParts.get(0);
                        resultPart.setReturn(true);
                        params.add(resultPart);
                        outputUnlistedParts.clear();
                     }
                     break label126;
                  }
               }
            }

            var13 = inputUnlistedParts.iterator();

            while(var13.hasNext()) {
               part = (MessagePart)var13.next();
               params.add(part);
            }

            var13 = outputUnlistedParts.iterator();

            while(var13.hasNext()) {
               part = (MessagePart)var13.next();
               params.add(part);
            }

            return params;
         }

         this.warning(this.info.operation.getEntity(), ModelerMessages.WSDLMODELER_INVALID_PARAMETER_ORDER_INVALID_PARAMETER_ORDER(this.info.operation.getName().getLocalPart()));
         ((List)parameterList).clear();
      }

      List outParts = new ArrayList();
      paramOrders = inputParts.iterator();

      MessagePart part;
      while(paramOrders.hasNext()) {
         part = (MessagePart)paramOrders.next();
         params.add(part);
      }

      if (this.isRequestResponse()) {
         paramOrders = outputParts.iterator();

         while(true) {
            while(paramOrders.hasNext()) {
               part = (MessagePart)paramOrders.next();
               MessagePart inPart = inputMessage.getPart(part.getName());
               if (inPart != null && part.getDescriptorKind() == inPart.getDescriptorKind() && part.getDescriptor().equals(inPart.getDescriptor())) {
                  inPart.setMode(Mode.INOUT);
               } else {
                  outParts.add(part);
               }
            }

            for(paramOrders = outParts.iterator(); paramOrders.hasNext(); params.add(part)) {
               part = (MessagePart)paramOrders.next();
               if (outParts.size() == 1) {
                  part.setReturn(true);
               }
            }
            break;
         }
      }

      return params;
   }

   protected String getClassName(com.sun.tools.internal.ws.processor.model.Port port, String suffix) {
      String prefix = BindingHelper.mangleNameToClassName(port.getName().getLocalPart());
      return this.options.defaultPackage + "." + prefix + suffix;
   }

   protected boolean isConflictingServiceClassName(String name) {
      return this.conflictsWithSEIClass(name) || this.conflictsWithJAXBClass(name) || this.conflictsWithExceptionClass(name);
   }

   private boolean conflictsWithSEIClass(String name) {
      Set seiNames = this.classNameCollector.getSeiClassNames();
      return seiNames != null && seiNames.contains(name);
   }

   private boolean conflictsWithJAXBClass(String name) {
      Set jaxbNames = this.classNameCollector.getJaxbGeneratedClassNames();
      return jaxbNames != null && jaxbNames.contains(name);
   }

   private boolean conflictsWithExceptionClass(String name) {
      Set exceptionNames = this.classNameCollector.getExceptionClassNames();
      return exceptionNames != null && exceptionNames.contains(name);
   }

   protected boolean isConflictingExceptionClassName(String name) {
      return this.conflictsWithSEIClass(name) || this.conflictsWithJAXBClass(name);
   }

   protected JAXBModelBuilder getJAXBModelBuilder() {
      return this.jaxbModelBuilder;
   }

   protected boolean validateWSDLBindingStyle(Binding binding) {
      SOAPBinding soapBinding = (SOAPBinding)getExtensionOfType(binding, SOAPBinding.class);
      if (soapBinding == null) {
         soapBinding = (SOAPBinding)getExtensionOfType(binding, SOAP12Binding.class);
      }

      if (soapBinding == null) {
         return false;
      } else {
         if (soapBinding.getStyle() == null) {
            soapBinding.setStyle(SOAPStyle.DOCUMENT);
         }

         SOAPStyle opStyle = soapBinding.getStyle();
         Iterator iter = binding.operations();

         while(iter.hasNext()) {
            BindingOperation bindingOperation = (BindingOperation)iter.next();
            SOAPOperation soapOperation = (SOAPOperation)getExtensionOfType(bindingOperation, SOAPOperation.class);
            if (soapOperation != null) {
               SOAPStyle currOpStyle = soapOperation.getStyle() != null ? soapOperation.getStyle() : soapBinding.getStyle();
               if (!currOpStyle.equals(opStyle)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private void applyWrapperStyleCustomization(com.sun.tools.internal.ws.processor.model.Port port, PortType portType) {
      JAXWSBinding jaxwsBinding = (JAXWSBinding)getExtensionOfType(portType, JAXWSBinding.class);
      Boolean wrapperStyle = jaxwsBinding != null ? jaxwsBinding.isEnableWrapperStyle() : null;
      if (wrapperStyle != null) {
         port.setWrapped(wrapperStyle);
      }

   }

   protected static void setDocumentationIfPresent(ModelObject obj, Documentation documentation) {
      if (documentation != null && documentation.getContent() != null) {
         obj.setJavaDoc(documentation.getContent());
      }

   }

   protected String getJavaNameForOperation(com.sun.tools.internal.ws.processor.model.Operation operation) {
      String name = operation.getJavaMethodName();
      if (Names.isJavaReservedWord(name)) {
         name = "_" + name;
      }

      return name;
   }

   private void reportError(Entity entity, String formattedMsg, Exception nestedException) {
      Locator locator = entity == null ? null : entity.getLocator();
      SAXParseException e = new SAXParseException2(formattedMsg, locator, nestedException);
      this.errReceiver.error(e);
   }

   protected static enum StyleAndUse {
      RPC_LITERAL,
      DOC_LITERAL;
   }
}
