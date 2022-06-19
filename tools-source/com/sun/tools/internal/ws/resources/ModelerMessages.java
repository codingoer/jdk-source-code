package com.sun.tools.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class ModelerMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.tools.internal.ws.resources.modeler");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableMIMEMODELER_INVALID_MIME_CONTENT_INVALID_SCHEMA_TYPE(Object arg0, Object arg1) {
      return messageFactory.getMessage("mimemodeler.invalidMimeContent.invalidSchemaType", new Object[]{arg0, arg1});
   }

   public static String MIMEMODELER_INVALID_MIME_CONTENT_INVALID_SCHEMA_TYPE(Object arg0, Object arg1) {
      return localizer.localize(localizableMIMEMODELER_INVALID_MIME_CONTENT_INVALID_SCHEMA_TYPE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_PARAMETERORDER_PARAMETER(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.parameterorder.parameter", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_PARAMETERORDER_PARAMETER(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_PARAMETERORDER_PARAMETER(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_FAULT_NO_SOAP_FAULT_NAME(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingFault.noSoapFaultName", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_FAULT_NO_SOAP_FAULT_NAME(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_FAULT_NO_SOAP_FAULT_NAME(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_NONCONFORMING_WSDL_IMPORT() {
      return messageFactory.getMessage("wsdlmodeler.warning.nonconforming.wsdl.import", new Object[0]);
   }

   public static String WSDLMODELER_WARNING_NONCONFORMING_WSDL_IMPORT() {
      return localizer.localize(localizableWSDLMODELER_WARNING_NONCONFORMING_WSDL_IMPORT());
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_SOAP_BODY_MISSING_NAMESPACE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.outputSoapBody.missingNamespace", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_SOAP_BODY_MISSING_NAMESPACE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_SOAP_BODY_MISSING_NAMESPACE(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_OPERATION_FAULT_NOT_LITERAL(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.operation.fault.notLiteral", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_OPERATION_FAULT_NOT_LITERAL(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_OPERATION_FAULT_NOT_LITERAL(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_INPUT_MISSING_SOAP_BODY(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.inputMissingSoapBody", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_INPUT_MISSING_SOAP_BODY(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_INPUT_MISSING_SOAP_BODY(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_SOAP_BINDING_NON_HTTP_TRANSPORT(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringSOAPBinding.nonHTTPTransport", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_SOAP_BINDING_NON_HTTP_TRANSPORT(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_SOAP_BINDING_NON_HTTP_TRANSPORT(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_NOT_FOUND(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.notFound", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_NOT_FOUND(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_NOT_FOUND(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_UNSUPPORTED_BINDING_MIME() {
      return messageFactory.getMessage("wsdlmodeler.unsupportedBinding.mime", new Object[0]);
   }

   public static String WSDLMODELER_UNSUPPORTED_BINDING_MIME() {
      return localizer.localize(localizableWSDLMODELER_UNSUPPORTED_BINDING_MIME());
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_HEADER_FAULT_NO_ELEMENT_ATTRIBUTE(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringHeaderFault.noElementAttribute", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_WARNING_IGNORING_HEADER_FAULT_NO_ELEMENT_ATTRIBUTE(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_HEADER_FAULT_NO_ELEMENT_ATTRIBUTE(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_NON_WRAPPER_STYLE(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.invalid.operation.javaReservedWordNotAllowed.nonWrapperStyle", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_NON_WRAPPER_STYLE(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_NON_WRAPPER_STYLE(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_INVALID_HEADERFAULT_NOT_LITERAL(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.headerfault.notLiteral", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_HEADERFAULT_NOT_LITERAL(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_HEADERFAULT_NOT_LITERAL(arg0, arg1));
   }

   public static Localizable localizableMIMEMODELER_INVALID_MIME_CONTENT_DIFFERENT_PART() {
      return messageFactory.getMessage("mimemodeler.invalidMimeContent.differentPart", new Object[0]);
   }

   public static String MIMEMODELER_INVALID_MIME_CONTENT_DIFFERENT_PART() {
      return localizer.localize(localizableMIMEMODELER_INVALID_MIME_CONTENT_DIFFERENT_PART());
   }

   public static Localizable localizableWSDLMODELER_ERROR_PART_NOT_FOUND(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.error.partNotFound", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_ERROR_PART_NOT_FOUND(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_ERROR_PART_NOT_FOUND(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_HEADER_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.header.message.partMustHaveElementDescriptor", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_HEADER_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_HEADER_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_OPERATION_NAME(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.operation.javaReservedWordNotAllowed.operationName", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_OPERATION_NAME(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_OPERATION_NAME(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_FAULT_OUTPUT_MISSING_SOAP_FAULT(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingFault.outputMissingSoapFault", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_FAULT_OUTPUT_MISSING_SOAP_FAULT(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_FAULT_OUTPUT_MISSING_SOAP_FAULT(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_ELEMENT_MESSAGE_PART(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.cannotHandleElementMessagePart", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_ELEMENT_MESSAGE_PART(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_ELEMENT_MESSAGE_PART(arg0));
   }

   public static Localizable localizableWSDLMODLER_WARNING_OPERATION_USE() {
      return messageFactory.getMessage("wsdlmodler.warning.operation.use", new Object[0]);
   }

   public static String WSDLMODLER_WARNING_OPERATION_USE() {
      return localizer.localize(localizableWSDLMODLER_WARNING_OPERATION_USE());
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_NON_SOAP_PORT(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringNonSOAPPort", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_NON_SOAP_PORT(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_NON_SOAP_PORT(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_FAULT_MESSAGE_HAS_MORE_THAN_ONE_PART(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingFault.messageHasMoreThanOnePart", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_FAULT_MESSAGE_HAS_MORE_THAN_ONE_PART(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_FAULT_MESSAGE_HAS_MORE_THAN_ONE_PART(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_NO_SERVICE_DEFINITIONS_FOUND() {
      return messageFactory.getMessage("wsdlmodeler.warning.noServiceDefinitionsFound", new Object[0]);
   }

   public static String WSDLMODELER_WARNING_NO_SERVICE_DEFINITIONS_FOUND() {
      return localizer.localize(localizableWSDLMODELER_WARNING_NO_SERVICE_DEFINITIONS_FOUND());
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_FAULT_CANT_RESOLVE_MESSAGE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringFault.cant.resolve.message", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_FAULT_CANT_RESOLVE_MESSAGE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_FAULT_CANT_RESOLVE_MESSAGE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_JAXB_JAVATYPE_NOTFOUND(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.jaxb.javatype.notfound", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_JAXB_JAVATYPE_NOTFOUND(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_JAXB_JAVATYPE_NOTFOUND(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_PORT_SOAP_BINDING_MIXED_STYLE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.port.SOAPBinding.mixedStyle", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_PORT_SOAP_BINDING_MIXED_STYLE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_PORT_SOAP_BINDING_MIXED_STYLE(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_DOCLITOPERATION(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.doclitoperation", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_DOCLITOPERATION(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_DOCLITOPERATION(arg0));
   }

   public static Localizable localizableMODELER_NESTED_MODEL_ERROR(Object arg0) {
      return messageFactory.getMessage("modeler.nestedModelError", new Object[]{arg0});
   }

   public static String MODELER_NESTED_MODEL_ERROR(Object arg0) {
      return localizer.localize(localizableMODELER_NESTED_MODEL_ERROR(arg0));
   }

   public static Localizable localizableWSDLMODELER_DUPLICATE_FAULT_SOAP_NAME(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.duplicate.fault.soap.name", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_DUPLICATE_FAULT_SOAP_NAME(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_DUPLICATE_FAULT_SOAP_NAME(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_FAULT_WRONG_SOAP_FAULT_NAME(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingFault.wrongSoapFaultName", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_INVALID_BINDING_FAULT_WRONG_SOAP_FAULT_NAME(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_FAULT_WRONG_SOAP_FAULT_NAME(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_NOT_LITERAL(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.notLiteral", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_NOT_LITERAL(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_NOT_LITERAL(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_DOCUMENT_STYLE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.cannotHandleDocumentStyle", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_DOCUMENT_STYLE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_DOCUMENT_STYLE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_FAULT_NOT_LITERAL(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringFault.notLiteral", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_FAULT_NOT_LITERAL(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_FAULT_NOT_LITERAL(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_RPCLITOPERATION(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.rpclitoperation", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_RPCLITOPERATION(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_RPCLITOPERATION(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOM_NAME(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.operation.javaReservedWordNotAllowed.customName", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOM_NAME(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOM_NAME(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_MEMBER_SUBMISSION_ADDRESSING_USED(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.memberSubmissionAddressingUsed", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_MEMBER_SUBMISSION_ADDRESSING_USED(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_MEMBER_SUBMISSION_ADDRESSING_USED(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_BINDING_OPERATION_MULTIPLE_PART_BINDING(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.bindingOperation.multiplePartBinding", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_BINDING_OPERATION_MULTIPLE_PART_BINDING(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_BINDING_OPERATION_MULTIPLE_PART_BINDING(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_PORT_SOAP_BINDING_12(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.port.SOAPBinding12", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_PORT_SOAP_BINDING_12(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_PORT_SOAP_BINDING_12(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_PORT_TYPE_FAULT_NOT_FOUND(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.portTypeFault.notFound", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_PORT_TYPE_FAULT_NOT_FOUND(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_PORT_TYPE_FAULT_NOT_FOUND(arg0, arg1));
   }

   public static Localizable localizableMIMEMODELER_INVALID_MIME_PART_NAME_NOT_ALLOWED(Object arg0) {
      return messageFactory.getMessage("mimemodeler.invalidMimePart.nameNotAllowed", new Object[]{arg0});
   }

   public static String MIMEMODELER_INVALID_MIME_PART_NAME_NOT_ALLOWED(Object arg0) {
      return localizer.localize(localizableMIMEMODELER_INVALID_MIME_PART_NAME_NOT_ALLOWED(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_MIME_PART_NOT_FOUND(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringMimePart.notFound", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_MIME_PART_NOT_FOUND(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_MIME_PART_NOT_FOUND(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_OPERATION_MORE_THAN_ONE_PART_IN_MESSAGE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.operation.MoreThanOnePartInMessage", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_OPERATION_MORE_THAN_ONE_PART_IN_MESSAGE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_OPERATION_MORE_THAN_ONE_PART_IN_MESSAGE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_NON_WRAPPER_STYLE(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.javaReservedWordNotAllowed.nonWrapperStyle", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_NON_WRAPPER_STYLE(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_NON_WRAPPER_STYLE(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_INVALID_FAULT_CANT_RESOLVE_MESSAGE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.fault.cant.resolve.message", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_FAULT_CANT_RESOLVE_MESSAGE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_FAULT_CANT_RESOLVE_MESSAGE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_FAULT_EMPTY_MESSAGE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingFault.emptyMessage", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_FAULT_EMPTY_MESSAGE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_FAULT_EMPTY_MESSAGE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_RPCLIT_UNKOWNSCHEMATYPE(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.rpclit.unkownschematype", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_RPCLIT_UNKOWNSCHEMATYPE(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_RPCLIT_UNKOWNSCHEMATYPE(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_BODY_PARTS_ATTRIBUTE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.cannotHandleBodyPartsAttribute", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_BODY_PARTS_ATTRIBUTE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_BODY_PARTS_ATTRIBUTE(arg0));
   }

   public static Localizable localizableWSDLMODELER_NON_UNIQUE_BODY_ERROR(Object arg0, Object arg1, Object arg2, Object arg3) {
      return messageFactory.getMessage("wsdlmodeler.nonUnique.body.error", new Object[]{arg0, arg1, arg2, arg3});
   }

   public static String WSDLMODELER_NON_UNIQUE_BODY_ERROR(Object arg0, Object arg1, Object arg2, Object arg3) {
      return localizer.localize(localizableWSDLMODELER_NON_UNIQUE_BODY_ERROR(arg0, arg1, arg2, arg3));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_SOAP_BINDING_MIXED_STYLE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringSOAPBinding.mixedStyle", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_SOAP_BINDING_MIXED_STYLE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_SOAP_BINDING_MIXED_STYLE(arg0));
   }

   public static Localizable localizableMIMEMODELER_INVALID_MIME_CONTENT_MISSING_TYPE_ATTRIBUTE(Object arg0) {
      return messageFactory.getMessage("mimemodeler.invalidMimeContent.missingTypeAttribute", new Object[]{arg0});
   }

   public static String MIMEMODELER_INVALID_MIME_CONTENT_MISSING_TYPE_ATTRIBUTE(Object arg0) {
      return localizer.localize(localizableMIMEMODELER_INVALID_MIME_CONTENT_MISSING_TYPE_ATTRIBUTE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_EMPTY_INPUT_MESSAGE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.cannotHandleEmptyInputMessage", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_EMPTY_INPUT_MESSAGE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_EMPTY_INPUT_MESSAGE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_NO_PORTS_IN_SERVICE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.noPortsInService", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_NO_PORTS_IN_SERVICE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_NO_PORTS_IN_SERVICE(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_PARAMETER_ORDER_TOO_MANY_UNMENTIONED_PARTS(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.parameterOrder.tooManyUnmentionedParts", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_PARAMETER_ORDER_TOO_MANY_UNMENTIONED_PARTS(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_PARAMETER_ORDER_TOO_MANY_UNMENTIONED_PARTS(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_INPUT_SOAP_BODY_MISSING_NAMESPACE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.inputSoapBody.missingNamespace", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_INPUT_SOAP_BODY_MISSING_NAMESPACE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_INPUT_SOAP_BODY_MISSING_NAMESPACE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_HEADER(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringHeader", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_HEADER(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_HEADER(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_RESPONSEBEAN_NOTFOUND(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.responsebean.notfound", new Object[]{arg0});
   }

   public static String WSDLMODELER_RESPONSEBEAN_NOTFOUND(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_RESPONSEBEAN_NOTFOUND(arg0));
   }

   public static Localizable localizableWSDLMODELER_20_RPCENC_NOT_SUPPORTED() {
      return messageFactory.getMessage("wsdlmodeler20.rpcenc.not.supported", new Object[0]);
   }

   public static String WSDLMODELER_20_RPCENC_NOT_SUPPORTED() {
      return localizer.localize(localizableWSDLMODELER_20_RPCENC_NOT_SUPPORTED());
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_PART_NOT_FOUND(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.partNotFound", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_PART_NOT_FOUND(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_PART_NOT_FOUND(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.message.partMustHaveElementDescriptor", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_ERROR_PARTS_NOT_FOUND(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.error.partsNotFound", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_ERROR_PARTS_NOT_FOUND(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_ERROR_PARTS_NOT_FOUND(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_FAULT_NOT_ENCODED(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringFault.notEncoded", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_FAULT_NOT_ENCODED(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_FAULT_NOT_ENCODED(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_NOT_SUPPORTED_STYLE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.notSupportedStyle", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_NOT_SUPPORTED_STYLE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_NOT_SUPPORTED_STYLE(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_MULTIPLE_PART_BINDING(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.multiplePartBinding", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_MULTIPLE_PART_BINDING(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_MULTIPLE_PART_BINDING(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_MULTIPLE_MATCHING_OPERATIONS(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.multipleMatchingOperations", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_MULTIPLE_MATCHING_OPERATIONS(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_MULTIPLE_MATCHING_OPERATIONS(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_HEADER_CANT_RESOLVE_MESSAGE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringHeader.cant.resolve.message", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_HEADER_CANT_RESOLVE_MESSAGE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_HEADER_CANT_RESOLVE_MESSAGE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOMIZED_OPERATION_NAME(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.javaReservedWordNotAllowed.customizedOperationName", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOMIZED_OPERATION_NAME(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOMIZED_OPERATION_NAME(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_HEADER_NOT_LITERAL(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringHeader.notLiteral", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_HEADER_NOT_LITERAL(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_HEADER_NOT_LITERAL(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_INPUT_HEADER_MISSING_NAMESPACE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.inputHeader.missingNamespace", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_INPUT_HEADER_MISSING_NAMESPACE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_INPUT_HEADER_MISSING_NAMESPACE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_MISSING_INPUT_NAME(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.missingInputName", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_MISSING_INPUT_NAME(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_MISSING_INPUT_NAME(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_NON_SOAP_PORT_NO_ADDRESS(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringNonSOAPPort.noAddress", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_NON_SOAP_PORT_NO_ADDRESS(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_NON_SOAP_PORT_NO_ADDRESS(arg0));
   }

   public static Localizable localizableWSDLMODELER_RESULT_IS_IN_OUT_PARAMETER(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.resultIsInOutParameter", new Object[]{arg0});
   }

   public static String WSDLMODELER_RESULT_IS_IN_OUT_PARAMETER(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_RESULT_IS_IN_OUT_PARAMETER(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_HEADER_NOT_FOUND(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.header.notFound", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_HEADER_NOT_FOUND(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_HEADER_NOT_FOUND(arg0, arg1));
   }

   public static Localizable localizableMIMEMODELER_ELEMENT_PART_INVALID_ELEMENT_MIME_TYPE(Object arg0, Object arg1) {
      return messageFactory.getMessage("mimemodeler.elementPart.invalidElementMimeType", new Object[]{arg0, arg1});
   }

   public static String MIMEMODELER_ELEMENT_PART_INVALID_ELEMENT_MIME_TYPE(Object arg0, Object arg1) {
      return localizer.localize(localizableMIMEMODELER_ELEMENT_PART_INVALID_ELEMENT_MIME_TYPE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_HEADER_NOT_LITERAL(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.header.notLiteral", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_HEADER_NOT_LITERAL(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_HEADER_NOT_LITERAL(arg0, arg1));
   }

   public static Localizable localizableMIMEMODELER_INVALID_MIME_CONTENT_MESAGE_PART_ELEMENT_KIND(Object arg0) {
      return messageFactory.getMessage("mimemodeler.invalidMimeContent.mesagePartElementKind", new Object[]{arg0});
   }

   public static String MIMEMODELER_INVALID_MIME_CONTENT_MESAGE_PART_ELEMENT_KIND(Object arg0) {
      return localizer.localize(localizableMIMEMODELER_INVALID_MIME_CONTENT_MESAGE_PART_ELEMENT_KIND(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_NOT_ENCODED(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.notEncoded", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_NOT_ENCODED(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_NOT_ENCODED(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_NONCONFORMING_WSDL_TYPES() {
      return messageFactory.getMessage("wsdlmodeler.warning.nonconforming.wsdl.types", new Object[0]);
   }

   public static String WSDLMODELER_WARNING_NONCONFORMING_WSDL_TYPES() {
      return localizer.localize(localizableWSDLMODELER_WARNING_NONCONFORMING_WSDL_TYPES());
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_MORE_THAN_ONE_PART_IN_INPUT_MESSAGE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.cannotHandleMoreThanOnePartInInputMessage", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_MORE_THAN_ONE_PART_IN_INPUT_MESSAGE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_MORE_THAN_ONE_PART_IN_INPUT_MESSAGE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_EMPTY_OUTPUT_MESSAGE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.cannotHandleEmptyOutputMessage", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_EMPTY_OUTPUT_MESSAGE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_EMPTY_OUTPUT_MESSAGE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_R_2716_R_2726(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.r2716r2726", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_R_2716_R_2726(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_R_2716_R_2726(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_IGNORING_MEMBER_SUBMISSION_ADDRESSING(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.ignoringMemberSubmissionAddressing", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_IGNORING_MEMBER_SUBMISSION_ADDRESSING(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_IGNORING_MEMBER_SUBMISSION_ADDRESSING(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_NO_SOAP_ADDRESS(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.noSOAPAddress", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_NO_SOAP_ADDRESS(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_NO_SOAP_ADDRESS(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_FAULTS(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringFaults", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_FAULTS(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_FAULTS(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_FAULT_MISSING_NAME(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingFault.missingName", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_FAULT_MISSING_NAME(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_FAULT_MISSING_NAME(arg0, arg1));
   }

   public static Localizable localizableMIMEMODELER_WARNING_IGNORINGINVALID_HEADER_PART_NOT_DECLARED_IN_ROOT_PART(Object arg0) {
      return messageFactory.getMessage("mimemodeler.warning.IgnoringinvalidHeaderPart.notDeclaredInRootPart", new Object[]{arg0});
   }

   public static String MIMEMODELER_WARNING_IGNORINGINVALID_HEADER_PART_NOT_DECLARED_IN_ROOT_PART(Object arg0) {
      return localizer.localize(localizableMIMEMODELER_WARNING_IGNORINGINVALID_HEADER_PART_NOT_DECLARED_IN_ROOT_PART(arg0));
   }

   public static Localizable localizableMIMEMODELER_INVALID_MIME_CONTENT_ERROR_LOADING_JAVA_CLASS() {
      return messageFactory.getMessage("mimemodeler.invalidMimeContent.errorLoadingJavaClass", new Object[0]);
   }

   public static String MIMEMODELER_INVALID_MIME_CONTENT_ERROR_LOADING_JAVA_CLASS() {
      return localizer.localize(localizableMIMEMODELER_INVALID_MIME_CONTENT_ERROR_LOADING_JAVA_CLASS());
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_NOT_IN_PORT_TYPE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.notInPortType", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_NOT_IN_PORT_TYPE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_NOT_IN_PORT_TYPE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CONFLICT_STYLE_IN_WSI_MODE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.conflictStyleInWSIMode", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_CONFLICT_STYLE_IN_WSI_MODE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CONFLICT_STYLE_IN_WSI_MODE(arg0));
   }

   public static Localizable localizableMIMEMODELER_INVALID_MIME_CONTENT_MISSING_PART_ATTRIBUTE(Object arg0) {
      return messageFactory.getMessage("mimemodeler.invalidMimeContent.missingPartAttribute", new Object[]{arg0});
   }

   public static String MIMEMODELER_INVALID_MIME_CONTENT_MISSING_PART_ATTRIBUTE(Object arg0) {
      return localizer.localize(localizableMIMEMODELER_INVALID_MIME_CONTENT_MISSING_PART_ATTRIBUTE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_SEARCH_SCHEMA_UNRECOGNIZED_TYPES(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.searchSchema.unrecognizedTypes", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_SEARCH_SCHEMA_UNRECOGNIZED_TYPES(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_SEARCH_SCHEMA_UNRECOGNIZED_TYPES(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOMIZED_OPERATION_NAME(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.operation.javaReservedWordNotAllowed.customizedOperationName", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOMIZED_OPERATION_NAME(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOMIZED_OPERATION_NAME(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_HEADER_CANT_RESOLVE_MESSAGE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.header.cant.resolve.message", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_HEADER_CANT_RESOLVE_MESSAGE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_HEADER_CANT_RESOLVE_MESSAGE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_FAULT_MISSING_NAMESPACE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingFault.missingNamespace", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_FAULT_MISSING_NAMESPACE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_FAULT_MISSING_NAMESPACE(arg0, arg1));
   }

   public static Localizable localizableMIMEMODELER_INVALID_MIME_PART_MORE_THAN_ONE_SOAP_BODY(Object arg0) {
      return messageFactory.getMessage("mimemodeler.invalidMimePart.moreThanOneSOAPBody", new Object[]{arg0});
   }

   public static String MIMEMODELER_INVALID_MIME_PART_MORE_THAN_ONE_SOAP_BODY(Object arg0) {
      return localizer.localize(localizableMIMEMODELER_INVALID_MIME_PART_MORE_THAN_ONE_SOAP_BODY(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_HEADER_INCONSISTENT_DEFINITION(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringHeader.inconsistentDefinition", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_HEADER_INCONSISTENT_DEFINITION(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_HEADER_INCONSISTENT_DEFINITION(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_FAULT_NOT_FOUND(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingFault.notFound", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_FAULT_NOT_FOUND(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_FAULT_NOT_FOUND(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOM_NAME(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.javaReservedWordNotAllowed.customName", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOM_NAME(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_CUSTOM_NAME(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_UNRECOGNIZED_SCHEMA_EXTENSION(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringUnrecognizedSchemaExtension", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_UNRECOGNIZED_SCHEMA_EXTENSION(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_UNRECOGNIZED_SCHEMA_EXTENSION(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_HEADER_FAULT_NOT_FOUND(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringHeaderFault.notFound", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_WARNING_IGNORING_HEADER_FAULT_NOT_FOUND(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_HEADER_FAULT_NOT_FOUND(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_NON_UNIQUE_BODY_WARNING(Object arg0, Object arg1, Object arg2, Object arg3) {
      return messageFactory.getMessage("wsdlmodeler.nonUnique.body.warning", new Object[]{arg0, arg1, arg2, arg3});
   }

   public static String WSDLMODELER_NON_UNIQUE_BODY_WARNING(Object arg0, Object arg1, Object arg2, Object arg3) {
      return localizer.localize(localizableWSDLMODELER_NON_UNIQUE_BODY_WARNING(arg0, arg1, arg2, arg3));
   }

   public static Localizable localizableWSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_WRAPPER_STYLE(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.invalid.operation.javaReservedWordNotAllowed.wrapperStyle", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_WRAPPER_STYLE(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_INVALID_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_WRAPPER_STYLE(arg0, arg1, arg2));
   }

   public static Localizable localizableMIMEMODELER_INVALID_MIME_CONTENT_UNKNOWN_SCHEMA_TYPE(Object arg0, Object arg1) {
      return messageFactory.getMessage("mimemodeler.invalidMimeContent.unknownSchemaType", new Object[]{arg0, arg1});
   }

   public static String MIMEMODELER_INVALID_MIME_CONTENT_UNKNOWN_SCHEMA_TYPE(Object arg0, Object arg1) {
      return localizer.localize(localizableMIMEMODELER_INVALID_MIME_CONTENT_UNKNOWN_SCHEMA_TYPE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_R_2716(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.r2716", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_R_2716(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_R_2716(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_HEADER_NOT_FOUND(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringHeader.notFound", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_HEADER_NOT_FOUND(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_HEADER_NOT_FOUND(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_TYPE_MESSAGE_PART(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.cannotHandleTypeMessagePart", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_TYPE_MESSAGE_PART(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_TYPE_MESSAGE_PART(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_PARAMETER_ORDER_INVALID_PARAMETER_ORDER(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.parameterOrder.invalidParameterOrder", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_PARAMETER_ORDER_INVALID_PARAMETER_ORDER(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_PARAMETER_ORDER_INVALID_PARAMETER_ORDER(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_MISSING_OUTPUT_NAME(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.missingOutputName", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_MISSING_OUTPUT_NAME(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_MISSING_OUTPUT_NAME(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_OPERATION(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalidOperation", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_OPERATION(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_OPERATION(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_HEADER_MISSING_NAMESPACE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.outputHeader.missingNamespace", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_HEADER_MISSING_NAMESPACE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_HEADER_MISSING_NAMESPACE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_HEADER_PART_FROM_BODY(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringHeader.partFromBody", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_HEADER_PART_FROM_BODY(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_HEADER_PART_FROM_BODY(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_OPERATION_NOT_SUPPORTED_STYLE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.operation.notSupportedStyle", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_OPERATION_NOT_SUPPORTED_STYLE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_OPERATION_NOT_SUPPORTED_STYLE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_NOT_NC_NAME(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.notNCName", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_NOT_NC_NAME(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_NOT_NC_NAME(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_PARAMETER_DIFFERENT_TYPES(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.parameter.differentTypes", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_PARAMETER_DIFFERENT_TYPES(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_PARAMETER_DIFFERENT_TYPES(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_FAULT_DOCUMENT_OPERATION(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringFault.documentOperation", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_FAULT_DOCUMENT_OPERATION(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_FAULT_DOCUMENT_OPERATION(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_WARNING_NONCONFORMING_WSDL_USE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.nonconforming.wsdl.use", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_NONCONFORMING_WSDL_USE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_NONCONFORMING_WSDL_USE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_NON_SOAP_PORT(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.nonSOAPPort", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_NON_SOAP_PORT(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_NON_SOAP_PORT(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_HEADERFAULT_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.invalid.headerfault.message.partMustHaveElementDescriptor", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_INVALID_HEADERFAULT_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_INVALID_HEADERFAULT_MESSAGE_PART_MUST_HAVE_ELEMENT_DESCRIPTOR(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_INVALID_STATE_MODELING_OPERATION(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalidState.modelingOperation", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_STATE_MODELING_OPERATION(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_STATE_MODELING_OPERATION(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_OPERATION_NAME(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.javaReservedWordNotAllowed.operationName", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_OPERATION_NAME(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_OPERATION_NAME(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_HEADER_NOT_ENCODED(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringHeader.notEncoded", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_WARNING_IGNORING_HEADER_NOT_ENCODED(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_HEADER_NOT_ENCODED(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_DUPLICATE_FAULT_PART_NAME(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.duplicate.fault.part.name", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_DUPLICATE_FAULT_PART_NAME(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_DUPLICATE_FAULT_PART_NAME(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_INVALID_OPERATION_MORE_THAN_ONE_PART_IN_MESSAGE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.operation.MoreThanOnePartInMessage", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_OPERATION_MORE_THAN_ONE_PART_IN_MESSAGE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_OPERATION_MORE_THAN_ONE_PART_IN_MESSAGE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_SOAP_BINDING_12(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringSOAPBinding12", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_SOAP_BINDING_12(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_SOAP_BINDING_12(arg0));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_FAULT_NOT_UNIQUE(Object arg0, Object arg1) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingFault.notUnique", new Object[]{arg0, arg1});
   }

   public static String WSDLMODELER_INVALID_BINDING_FAULT_NOT_UNIQUE(Object arg0, Object arg1) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_FAULT_NOT_UNIQUE(arg0, arg1));
   }

   public static Localizable localizableWSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_MISSING_SOAP_BODY(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.invalid.bindingOperation.outputMissingSoapBody", new Object[]{arg0});
   }

   public static String WSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_MISSING_SOAP_BODY(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_INVALID_BINDING_OPERATION_OUTPUT_MISSING_SOAP_BODY(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_HEADER_FAULT_NOT_LITERAL(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringHeaderFault.notLiteral", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_WARNING_IGNORING_HEADER_FAULT_NOT_LITERAL(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_HEADER_FAULT_NOT_LITERAL(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_MORE_THAN_ONE_PART_IN_OUTPUT_MESSAGE(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.cannotHandleMoreThanOnePartInOutputMessage", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_MORE_THAN_ONE_PART_IN_OUTPUT_MESSAGE(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_CANNOT_HANDLE_MORE_THAN_ONE_PART_IN_OUTPUT_MESSAGE(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_NO_OPERATIONS_IN_PORT(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.warning.noOperationsInPort", new Object[]{arg0});
   }

   public static String WSDLMODELER_WARNING_NO_OPERATIONS_IN_PORT(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_WARNING_NO_OPERATIONS_IN_PORT(arg0));
   }

   public static Localizable localizableWSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_WRAPPER_STYLE(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("wsdlmodeler.warning.ignoringOperation.javaReservedWordNotAllowed.wrapperStyle", new Object[]{arg0, arg1, arg2});
   }

   public static String WSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_WRAPPER_STYLE(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWSDLMODELER_WARNING_IGNORING_OPERATION_JAVA_RESERVED_WORD_NOT_ALLOWED_WRAPPER_STYLE(arg0, arg1, arg2));
   }

   public static Localizable localizableWSDLMODELER_UNSOLVABLE_NAMING_CONFLICTS(Object arg0) {
      return messageFactory.getMessage("wsdlmodeler.unsolvableNamingConflicts", new Object[]{arg0});
   }

   public static String WSDLMODELER_UNSOLVABLE_NAMING_CONFLICTS(Object arg0) {
      return localizer.localize(localizableWSDLMODELER_UNSOLVABLE_NAMING_CONFLICTS(arg0));
   }
}
