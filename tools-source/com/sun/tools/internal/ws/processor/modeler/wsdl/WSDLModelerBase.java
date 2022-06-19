package com.sun.tools.internal.ws.processor.modeler.wsdl;

import com.sun.tools.internal.ws.api.wsdl.TWSDLExtensible;
import com.sun.tools.internal.ws.api.wsdl.TWSDLExtension;
import com.sun.tools.internal.ws.processor.generator.Names;
import com.sun.tools.internal.ws.processor.model.Port;
import com.sun.tools.internal.ws.processor.modeler.Modeler;
import com.sun.tools.internal.ws.resources.ModelerMessages;
import com.sun.tools.internal.ws.wscompile.AbortException;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import com.sun.tools.internal.ws.wscompile.ErrorReceiverFilter;
import com.sun.tools.internal.ws.wscompile.WsimportOptions;
import com.sun.tools.internal.ws.wsdl.document.BindingFault;
import com.sun.tools.internal.ws.wsdl.document.BindingOperation;
import com.sun.tools.internal.ws.wsdl.document.Fault;
import com.sun.tools.internal.ws.wsdl.document.Kinds;
import com.sun.tools.internal.ws.wsdl.document.Message;
import com.sun.tools.internal.ws.wsdl.document.MessagePart;
import com.sun.tools.internal.ws.wsdl.document.Operation;
import com.sun.tools.internal.ws.wsdl.document.OperationStyle;
import com.sun.tools.internal.ws.wsdl.document.WSDLDocument;
import com.sun.tools.internal.ws.wsdl.document.jaxws.JAXWSBinding;
import com.sun.tools.internal.ws.wsdl.document.mime.MIMEContent;
import com.sun.tools.internal.ws.wsdl.document.mime.MIMEMultipartRelated;
import com.sun.tools.internal.ws.wsdl.document.mime.MIMEPart;
import com.sun.tools.internal.ws.wsdl.document.schema.SchemaKinds;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPBinding;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPBody;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPFault;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPHeader;
import com.sun.tools.internal.ws.wsdl.document.soap.SOAPOperation;
import com.sun.tools.internal.ws.wsdl.framework.Entity;
import com.sun.tools.internal.ws.wsdl.framework.GloballyKnown;
import com.sun.tools.internal.ws.wsdl.framework.NoSuchEntityException;
import com.sun.tools.internal.ws.wsdl.parser.MetadataFinder;
import com.sun.tools.internal.ws.wsdl.parser.WSDLParser;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public abstract class WSDLModelerBase implements Modeler {
   protected final ErrorReceiverFilter errReceiver;
   protected final WsimportOptions options;
   protected MetadataFinder forest;
   int numPasses = 0;
   protected static final String OPERATION_HAS_VOID_RETURN_TYPE = "com.sun.xml.internal.ws.processor.modeler.wsdl.operationHasVoidReturnType";
   protected static final String WSDL_PARAMETER_ORDER = "com.sun.xml.internal.ws.processor.modeler.wsdl.parameterOrder";
   public static final String WSDL_RESULT_PARAMETER = "com.sun.xml.internal.ws.processor.modeler.wsdl.resultParameter";
   public static final String MESSAGE_HAS_MIME_MULTIPART_RELATED_BINDING = "com.sun.xml.internal.ws.processor.modeler.wsdl.mimeMultipartRelatedBinding";
   protected ProcessSOAPOperationInfo info;
   private Set _conflictingClassNames;
   protected Map _javaExceptions;
   protected Map _faultTypeToStructureMap;
   protected Map _bindingNameToPortMap;
   private final Set reqResNames = new HashSet();
   protected WSDLParser parser;
   protected WSDLDocument document;
   protected static final LocatorImpl NULL_LOCATOR = new LocatorImpl();

   public WSDLModelerBase(WsimportOptions options, ErrorReceiver receiver, MetadataFinder forest) {
      this.options = options;
      this.errReceiver = new ErrorReceiverFilter(receiver);
      this.forest = forest;
   }

   protected void applyPortMethodCustomization(Port port, com.sun.tools.internal.ws.wsdl.document.Port wsdlPort) {
      if (!this.isProvider(wsdlPort)) {
         JAXWSBinding jaxwsBinding = (JAXWSBinding)getExtensionOfType(wsdlPort, JAXWSBinding.class);
         String portMethodName = jaxwsBinding != null ? (jaxwsBinding.getMethodName() != null ? jaxwsBinding.getMethodName().getName() : null) : null;
         if (portMethodName != null) {
            port.setPortGetter(portMethodName);
         } else {
            portMethodName = Names.getPortName(port);
            portMethodName = BindingHelper.mangleNameToClassName(portMethodName);
            port.setPortGetter("get" + portMethodName);
         }

      }
   }

   protected boolean isProvider(com.sun.tools.internal.ws.wsdl.document.Port wsdlPort) {
      JAXWSBinding portCustomization = (JAXWSBinding)getExtensionOfType(wsdlPort, JAXWSBinding.class);
      Boolean isProvider = portCustomization != null ? portCustomization.isProvider() : null;
      if (isProvider != null) {
         return isProvider;
      } else {
         JAXWSBinding jaxwsGlobalCustomization = (JAXWSBinding)getExtensionOfType(this.document.getDefinitions(), JAXWSBinding.class);
         isProvider = jaxwsGlobalCustomization != null ? jaxwsGlobalCustomization.isProvider() : null;
         return isProvider != null ? isProvider : false;
      }
   }

   protected SOAPBody getSOAPRequestBody() {
      SOAPBody requestBody = (SOAPBody)this.getAnyExtensionOfType(this.info.bindingOperation.getInput(), SOAPBody.class);
      if (requestBody == null) {
         this.error(this.info.bindingOperation.getInput(), ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_INPUT_MISSING_SOAP_BODY(this.info.bindingOperation.getName()));
      }

      return requestBody;
   }

   protected boolean isRequestMimeMultipart() {
      Iterator var1 = this.info.bindingOperation.getInput().extensions().iterator();

      TWSDLExtension extension;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         extension = (TWSDLExtension)var1.next();
      } while(!extension.getClass().equals(MIMEMultipartRelated.class));

      return true;
   }

   protected boolean isResponseMimeMultipart() {
      Iterator var1 = this.info.bindingOperation.getOutput().extensions().iterator();

      TWSDLExtension extension;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         extension = (TWSDLExtension)var1.next();
      } while(!extension.getClass().equals(MIMEMultipartRelated.class));

      return true;
   }

   protected SOAPBody getSOAPResponseBody() {
      SOAPBody responseBody = (SOAPBody)this.getAnyExtensionOfType(this.info.bindingOperation.getOutput(), SOAPBody.class);
      if (responseBody == null) {
         this.error(this.info.bindingOperation.getOutput(), ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_MISSING_SOAP_BODY(this.info.bindingOperation.getName()));
      }

      return responseBody;
   }

   protected Message getOutputMessage() {
      return this.info.portTypeOperation.getOutput() == null ? null : this.info.portTypeOperation.getOutput().resolveMessage(this.info.document);
   }

   protected Message getInputMessage() {
      return this.info.portTypeOperation.getInput().resolveMessage(this.info.document);
   }

   protected List getMessageParts(SOAPBody body, Message message, boolean isInput) {
      String bodyParts = body.getParts();
      ArrayList partsList = new ArrayList();
      List parts = new ArrayList();
      List mimeParts;
      if (isInput) {
         mimeParts = this.getMimeContentParts(message, this.info.bindingOperation.getInput());
      } else {
         mimeParts = this.getMimeContentParts(message, this.info.bindingOperation.getOutput());
      }

      Iterator var11;
      MessagePart mPart;
      if (bodyParts != null) {
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
      } else {
         for(var11 = message.getParts().iterator(); var11.hasNext(); partsList.add(mPart)) {
            mPart = (MessagePart)var11.next();
            if (!mimeParts.contains(mPart)) {
               mPart.setBindingExtensibilityElementKind(1);
            }
         }
      }

      var11 = message.getParts().iterator();

      while(var11.hasNext()) {
         mPart = (MessagePart)var11.next();
         if (mimeParts.contains(mPart)) {
            mPart.setBindingExtensibilityElementKind(5);
            parts.add(mPart);
         } else if (partsList.contains(mPart)) {
            mPart.setBindingExtensibilityElementKind(1);
            parts.add(mPart);
         }
      }

      return parts;
   }

   protected List getMimeContentParts(Message message, TWSDLExtensible ext) {
      ArrayList mimeContentParts = new ArrayList();
      Iterator var4 = this.getMimeParts(ext).iterator();

      while(var4.hasNext()) {
         MIMEPart mimePart = (MIMEPart)var4.next();
         MessagePart part = this.getMimeContentPart(message, mimePart);
         if (part != null) {
            mimeContentParts.add(part);
         }
      }

      return mimeContentParts;
   }

   protected boolean validateMimeParts(Iterable mimeParts) {
      boolean gotRootPart = false;
      List mimeContents = new ArrayList();
      Iterator var4 = mimeParts.iterator();

      while(var4.hasNext()) {
         MIMEPart mPart = (MIMEPart)var4.next();
         Iterator var6 = mPart.extensions().iterator();

         while(var6.hasNext()) {
            TWSDLExtension obj = (TWSDLExtension)var6.next();
            if (obj instanceof SOAPBody) {
               if (gotRootPart) {
                  this.warning(mPart, ModelerMessages.MIMEMODELER_INVALID_MIME_PART_MORE_THAN_ONE_SOAP_BODY(this.info.operation.getName().getLocalPart()));
                  return false;
               }

               gotRootPart = true;
            } else if (obj instanceof MIMEContent) {
               mimeContents.add((MIMEContent)obj);
            }
         }

         if (!this.validateMimeContentPartNames(mimeContents)) {
            return false;
         }

         if (mPart.getName() != null) {
            this.warning(mPart, ModelerMessages.MIMEMODELER_INVALID_MIME_PART_NAME_NOT_ALLOWED(this.info.portTypeOperation.getName()));
         }
      }

      return true;
   }

   private MessagePart getMimeContentPart(Message message, MIMEPart part) {
      Iterator var3 = this.getMimeContents(part).iterator();
      if (var3.hasNext()) {
         MIMEContent mimeContent = (MIMEContent)var3.next();
         String mimeContentPartName = mimeContent.getPart();
         MessagePart mPart = message.getPart(mimeContentPartName);
         if (null == mPart) {
            this.error(mimeContent, ModelerMessages.WSDLMODELER_ERROR_PARTS_NOT_FOUND(mimeContentPartName, message.getName()));
         }

         mPart.setBindingExtensibilityElementKind(5);
         return mPart;
      } else {
         return null;
      }
   }

   protected List getAlternateMimeTypes(List mimeContents) {
      List mimeTypes = new ArrayList();
      Iterator var3 = mimeContents.iterator();

      while(var3.hasNext()) {
         MIMEContent mimeContent = (MIMEContent)var3.next();
         String mimeType = this.getMimeContentType(mimeContent);
         if (!mimeTypes.contains(mimeType)) {
            mimeTypes.add(mimeType);
         }
      }

      return mimeTypes;
   }

   private boolean validateMimeContentPartNames(List mimeContents) {
      Iterator var2 = mimeContents.iterator();

      MIMEContent mimeContent;
      String mimeContnetPart;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         mimeContent = (MIMEContent)var2.next();
         mimeContnetPart = this.getMimeContentPartName(mimeContent);
      } while(mimeContnetPart != null);

      this.warning(mimeContent, ModelerMessages.MIMEMODELER_INVALID_MIME_CONTENT_MISSING_PART_ATTRIBUTE(this.info.operation.getName().getLocalPart()));
      return false;
   }

   protected Iterable getMimeParts(TWSDLExtensible ext) {
      MIMEMultipartRelated multiPartRelated = (MIMEMultipartRelated)this.getAnyExtensionOfType(ext, MIMEMultipartRelated.class);
      return (Iterable)(multiPartRelated == null ? Collections.emptyList() : multiPartRelated.getParts());
   }

   protected List getMimeContents(MIMEPart part) {
      List mimeContents = new ArrayList();
      Iterator var3 = part.extensions().iterator();

      while(var3.hasNext()) {
         TWSDLExtension mimeContent = (TWSDLExtension)var3.next();
         if (mimeContent instanceof MIMEContent) {
            mimeContents.add((MIMEContent)mimeContent);
         }
      }

      return mimeContents;
   }

   private String getMimeContentPartName(MIMEContent mimeContent) {
      return mimeContent.getPart();
   }

   private String getMimeContentType(MIMEContent mimeContent) {
      String mimeType = mimeContent.getType();
      if (mimeType == null) {
         this.error(mimeContent, ModelerMessages.MIMEMODELER_INVALID_MIME_CONTENT_MISSING_TYPE_ATTRIBUTE(this.info.operation.getName().getLocalPart()));
      }

      return mimeType;
   }

   protected boolean isStyleAndPartMatch(SOAPOperation soapOperation, MessagePart part) {
      if (soapOperation != null && soapOperation.getStyle() != null) {
         if (soapOperation.isDocument() && part.getDescriptorKind() != SchemaKinds.XSD_ELEMENT || soapOperation.isRPC() && part.getDescriptorKind() != SchemaKinds.XSD_TYPE) {
            return false;
         }
      } else if (this.info.soapBinding.isDocument() && part.getDescriptorKind() != SchemaKinds.XSD_ELEMENT || this.info.soapBinding.isRPC() && part.getDescriptorKind() != SchemaKinds.XSD_TYPE) {
         return false;
      }

      return true;
   }

   protected String getRequestNamespaceURI(SOAPBody body) {
      String namespaceURI = body.getNamespace();
      if (namespaceURI == null) {
         if (this.options.isExtensionMode()) {
            return this.info.modelPort.getName().getNamespaceURI();
         }

         this.error(body, ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_INPUT_SOAP_BODY_MISSING_NAMESPACE(this.info.bindingOperation.getName()));
      }

      return namespaceURI;
   }

   protected String getResponseNamespaceURI(SOAPBody body) {
      String namespaceURI = body.getNamespace();
      if (namespaceURI == null) {
         if (this.options.isExtensionMode()) {
            return this.info.modelPort.getName().getNamespaceURI();
         }

         this.error(body, ModelerMessages.WSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_SOAP_BODY_MISSING_NAMESPACE(this.info.bindingOperation.getName()));
      }

      return namespaceURI;
   }

   protected List getHeaderExtensions(TWSDLExtensible extensible) {
      List headerList = new ArrayList();
      Iterator var3 = extensible.extensions().iterator();

      while(true) {
         while(var3.hasNext()) {
            TWSDLExtension extension = (TWSDLExtension)var3.next();
            if (extension.getClass() == MIMEMultipartRelated.class) {
               Iterator var5 = ((MIMEMultipartRelated)extension).getParts().iterator();

               while(var5.hasNext()) {
                  MIMEPart part = (MIMEPart)var5.next();
                  boolean isRootPart = this.isRootPart(part);
                  Iterator var8 = part.extensions().iterator();

                  while(var8.hasNext()) {
                     TWSDLExtension obj = (TWSDLExtension)var8.next();
                     if (obj instanceof SOAPHeader) {
                        if (!isRootPart) {
                           this.warning((Entity)obj, ModelerMessages.MIMEMODELER_WARNING_IGNORINGINVALID_HEADER_PART_NOT_DECLARED_IN_ROOT_PART(this.info.bindingOperation.getName()));
                           return new ArrayList();
                        }

                        headerList.add((SOAPHeader)obj);
                     }
                  }
               }
            } else if (extension instanceof SOAPHeader) {
               headerList.add((SOAPHeader)extension);
            }
         }

         return headerList;
      }
   }

   private boolean isRootPart(MIMEPart part) {
      Iterator var2 = part.extensions().iterator();

      TWSDLExtension twsdlExtension;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         twsdlExtension = (TWSDLExtension)var2.next();
      } while(!(twsdlExtension instanceof SOAPBody));

      return true;
   }

   protected Set getDuplicateFaultNames() {
      Set faultNames = new HashSet();
      Set duplicateNames = new HashSet();
      Iterator var3 = this.info.bindingOperation.faults().iterator();

      while(var3.hasNext()) {
         BindingFault bindingFault = (BindingFault)var3.next();
         Fault portTypeFault = null;
         Iterator var6 = this.info.portTypeOperation.faults().iterator();

         while(var6.hasNext()) {
            Fault aFault = (Fault)var6.next();
            if (aFault.getName().equals(bindingFault.getName())) {
               if (portTypeFault != null) {
                  this.error(bindingFault, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_NOT_UNIQUE(bindingFault.getName(), this.info.bindingOperation.getName()));
               } else {
                  portTypeFault = aFault;
               }
            }
         }

         if (portTypeFault == null) {
            this.error(bindingFault, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_NOT_FOUND(bindingFault.getName(), this.info.bindingOperation.getName()));
         }

         SOAPFault soapFault = (SOAPFault)getExtensionOfType(bindingFault, SOAPFault.class);
         if (soapFault == null) {
            if (this.options.isExtensionMode()) {
               this.warning(bindingFault, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_OUTPUT_MISSING_SOAP_FAULT(bindingFault.getName(), this.info.bindingOperation.getName()));
            } else {
               this.error(bindingFault, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_OUTPUT_MISSING_SOAP_FAULT(bindingFault.getName(), this.info.bindingOperation.getName()));
            }
         }

         Message faultMessage = portTypeFault.resolveMessage(this.info.document);
         if (faultMessage.getParts().isEmpty()) {
            this.error(faultMessage, ModelerMessages.WSDLMODELER_INVALID_BINDING_FAULT_EMPTY_MESSAGE(bindingFault.getName(), faultMessage.getName()));
         }

         if (!this.options.isExtensionMode() && soapFault != null && soapFault.getNamespace() != null) {
            this.warning(soapFault, ModelerMessages.WSDLMODELER_WARNING_R_2716_R_2726("soapbind:fault", soapFault.getName()));
         }

         String faultNamespaceURI = soapFault != null && soapFault.getNamespace() != null ? soapFault.getNamespace() : portTypeFault.getMessage().getNamespaceURI();
         String faultName = faultMessage.getName();
         QName faultQName = new QName(faultNamespaceURI, faultName);
         if (faultNames.contains(faultQName)) {
            duplicateNames.add(faultQName);
         } else {
            faultNames.add(faultQName);
         }
      }

      return duplicateNames;
   }

   protected boolean validateBodyParts(BindingOperation operation) {
      boolean isRequestResponse = this.info.portTypeOperation.getStyle() == OperationStyle.REQUEST_RESPONSE;
      List inputParts = this.getMessageParts(this.getSOAPRequestBody(), this.getInputMessage(), true);
      if (!this.validateStyleAndPart(operation, inputParts)) {
         return false;
      } else {
         if (isRequestResponse) {
            List outputParts = this.getMessageParts(this.getSOAPResponseBody(), this.getOutputMessage(), false);
            if (!this.validateStyleAndPart(operation, outputParts)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean validateStyleAndPart(BindingOperation operation, List parts) {
      SOAPOperation soapOperation = (SOAPOperation)getExtensionOfType(operation, SOAPOperation.class);
      Iterator var4 = parts.iterator();

      MessagePart part;
      do {
         if (!var4.hasNext()) {
            return true;
         }

         part = (MessagePart)var4.next();
      } while(part.getBindingExtensibilityElementKind() != 1 || this.isStyleAndPartMatch(soapOperation, part));

      return false;
   }

   protected String getLiteralJavaMemberName(com.sun.tools.internal.ws.processor.model.Fault fault) {
      QName memberName = fault.getElementName();
      String javaMemberName = fault.getJavaMemberName();
      if (javaMemberName == null) {
         javaMemberName = memberName.getLocalPart();
      }

      return javaMemberName;
   }

   protected List getMimeContents(TWSDLExtensible ext, Message message, String name) {
      Iterator var4 = this.getMimeParts(ext).iterator();

      while(var4.hasNext()) {
         MIMEPart mimePart = (MIMEPart)var4.next();
         List mimeContents = this.getMimeContents(mimePart);
         Iterator var7 = mimeContents.iterator();

         while(var7.hasNext()) {
            MIMEContent mimeContent = (MIMEContent)var7.next();
            if (mimeContent.getPart().equals(name)) {
               return mimeContents;
            }
         }
      }

      return null;
   }

   protected String makePackageQualified(String s) {
      if (s.indexOf(".") != -1) {
         return s;
      } else {
         return this.options.defaultPackage != null && !this.options.defaultPackage.equals("") ? this.options.defaultPackage + "." + s : s;
      }
   }

   protected String getUniqueName(Operation operation, boolean hasOverloadedOperations) {
      return hasOverloadedOperations ? operation.getUniqueKey().replace(' ', '_') : operation.getName();
   }

   protected static QName getQNameOf(GloballyKnown entity) {
      return new QName(entity.getDefining().getTargetNamespaceURI(), entity.getName());
   }

   protected static TWSDLExtension getExtensionOfType(TWSDLExtensible extensible, Class type) {
      Iterator var2 = extensible.extensions().iterator();

      TWSDLExtension extension;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         extension = (TWSDLExtension)var2.next();
      } while(!extension.getClass().equals(type));

      return extension;
   }

   protected TWSDLExtension getAnyExtensionOfType(TWSDLExtensible extensible, Class type) {
      if (extensible == null) {
         return null;
      } else {
         Iterator var3 = extensible.extensions().iterator();

         while(true) {
            TWSDLExtension extension;
            do {
               do {
                  if (!var3.hasNext()) {
                     return null;
                  }

                  extension = (TWSDLExtension)var3.next();
                  if (extension.getClass().equals(type)) {
                     return extension;
                  }
               } while(!extension.getClass().equals(MIMEMultipartRelated.class));
            } while(!type.equals(SOAPBody.class) && !type.equals(MIMEContent.class) && !type.equals(MIMEPart.class));

            Iterator var5 = ((MIMEMultipartRelated)extension).getParts().iterator();

            while(var5.hasNext()) {
               MIMEPart part = (MIMEPart)var5.next();
               TWSDLExtension extn = getExtensionOfType(part, type);
               if (extn != null) {
                  return extn;
               }
            }
         }
      }
   }

   protected static Message findMessage(QName messageName, WSDLDocument document) {
      Message message = null;

      try {
         message = (Message)document.find(Kinds.MESSAGE, messageName);
      } catch (NoSuchEntityException var4) {
      }

      return message;
   }

   protected static boolean tokenListContains(String tokenList, String target) {
      if (tokenList == null) {
         return false;
      } else {
         StringTokenizer tokenizer = new StringTokenizer(tokenList, " ");

         String s;
         do {
            if (!tokenizer.hasMoreTokens()) {
               return false;
            }

            s = tokenizer.nextToken();
         } while(!target.equals(s));

         return true;
      }
   }

   protected String getUniqueClassName(String className) {
      int cnt = 2;

      String uniqueName;
      for(uniqueName = className; this.reqResNames.contains(uniqueName.toLowerCase(Locale.ENGLISH)); ++cnt) {
         uniqueName = className + cnt;
      }

      this.reqResNames.add(uniqueName.toLowerCase(Locale.ENGLISH));
      return uniqueName;
   }

   protected boolean isConflictingClassName(String name) {
      return this._conflictingClassNames == null ? false : this._conflictingClassNames.contains(name);
   }

   protected boolean isConflictingServiceClassName(String name) {
      return this.isConflictingClassName(name);
   }

   protected boolean isConflictingStubClassName(String name) {
      return this.isConflictingClassName(name);
   }

   protected boolean isConflictingTieClassName(String name) {
      return this.isConflictingClassName(name);
   }

   protected boolean isConflictingPortClassName(String name) {
      return this.isConflictingClassName(name);
   }

   protected boolean isConflictingExceptionClassName(String name) {
      return this.isConflictingClassName(name);
   }

   protected void warning(Entity entity, String message) {
      if (this.numPasses <= 1) {
         if (entity == null) {
            this.errReceiver.warning((Locator)null, message);
         } else {
            this.errReceiver.warning(entity.getLocator(), message);
         }

      }
   }

   protected void error(Entity entity, String message) {
      if (entity == null) {
         this.errReceiver.error((Locator)null, message);
      } else {
         this.errReceiver.error(entity.getLocator(), message);
      }

      throw new AbortException();
   }

   public static class ProcessSOAPOperationInfo {
      public Port modelPort;
      public com.sun.tools.internal.ws.wsdl.document.Port port;
      public Operation portTypeOperation;
      public BindingOperation bindingOperation;
      public SOAPBinding soapBinding;
      public WSDLDocument document;
      public boolean hasOverloadedOperations;
      public Map headers;
      public com.sun.tools.internal.ws.processor.model.Operation operation;

      public ProcessSOAPOperationInfo(Port modelPort, com.sun.tools.internal.ws.wsdl.document.Port port, Operation portTypeOperation, BindingOperation bindingOperation, SOAPBinding soapBinding, WSDLDocument document, boolean hasOverloadedOperations, Map headers) {
         this.modelPort = modelPort;
         this.port = port;
         this.portTypeOperation = portTypeOperation;
         this.bindingOperation = bindingOperation;
         this.soapBinding = soapBinding;
         this.document = document;
         this.hasOverloadedOperations = hasOverloadedOperations;
         this.headers = headers;
      }
   }
}
