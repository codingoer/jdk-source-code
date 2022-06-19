package com.sun.tools.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class WsdlMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.tools.internal.ws.resources.wsdl");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizablePARSING_ELEMENT_EXPECTED() {
      return messageFactory.getMessage("parsing.elementExpected", new Object[0]);
   }

   public static String PARSING_ELEMENT_EXPECTED() {
      return localizer.localize(localizablePARSING_ELEMENT_EXPECTED());
   }

   public static Localizable localizableENTITY_NOT_FOUND_BINDING(Object arg0, Object arg1) {
      return messageFactory.getMessage("entity.notFound.binding", new Object[]{arg0, arg1});
   }

   public static String ENTITY_NOT_FOUND_BINDING(Object arg0, Object arg1) {
      return localizer.localize(localizableENTITY_NOT_FOUND_BINDING(arg0, arg1));
   }

   public static Localizable localizablePARSING_UNABLE_TO_GET_METADATA(Object arg0, Object arg1) {
      return messageFactory.getMessage("parsing.unableToGetMetadata", new Object[]{arg0, arg1});
   }

   public static String PARSING_UNABLE_TO_GET_METADATA(Object arg0, Object arg1) {
      return localizer.localize(localizablePARSING_UNABLE_TO_GET_METADATA(arg0, arg1));
   }

   public static Localizable localizablePARSING_PARSE_FAILED() {
      return messageFactory.getMessage("Parsing.ParseFailed", new Object[0]);
   }

   public static String PARSING_PARSE_FAILED() {
      return localizer.localize(localizablePARSING_PARSE_FAILED());
   }

   public static Localizable localizablePARSING_INVALID_ATTRIBUTE_VALUE(Object arg0, Object arg1) {
      return messageFactory.getMessage("parsing.invalidAttributeValue", new Object[]{arg0, arg1});
   }

   public static String PARSING_INVALID_ATTRIBUTE_VALUE(Object arg0, Object arg1) {
      return localizer.localize(localizablePARSING_INVALID_ATTRIBUTE_VALUE(arg0, arg1));
   }

   public static Localizable localizableVALIDATION_INVALID_ATTRIBUTE_VALUE(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.invalidAttributeValue", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_INVALID_ATTRIBUTE_VALUE(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_INVALID_ATTRIBUTE_VALUE(arg0, arg1));
   }

   public static Localizable localizablePARSING_INVALID_TAG(Object arg0, Object arg1) {
      return messageFactory.getMessage("parsing.invalidTag", new Object[]{arg0, arg1});
   }

   public static String PARSING_INVALID_TAG(Object arg0, Object arg1) {
      return localizer.localize(localizablePARSING_INVALID_TAG(arg0, arg1));
   }

   public static Localizable localizableENTITY_NOT_FOUND_PORT_TYPE(Object arg0, Object arg1) {
      return messageFactory.getMessage("entity.notFound.portType", new Object[]{arg0, arg1});
   }

   public static String ENTITY_NOT_FOUND_PORT_TYPE(Object arg0, Object arg1) {
      return localizer.localize(localizableENTITY_NOT_FOUND_PORT_TYPE(arg0, arg1));
   }

   public static Localizable localizablePARSING_MISSING_REQUIRED_ATTRIBUTE(Object arg0, Object arg1) {
      return messageFactory.getMessage("parsing.missingRequiredAttribute", new Object[]{arg0, arg1});
   }

   public static String PARSING_MISSING_REQUIRED_ATTRIBUTE(Object arg0, Object arg1) {
      return localizer.localize(localizablePARSING_MISSING_REQUIRED_ATTRIBUTE(arg0, arg1));
   }

   public static Localizable localizablePARSING_INVALID_ELEMENT(Object arg0, Object arg1) {
      return messageFactory.getMessage("parsing.invalidElement", new Object[]{arg0, arg1});
   }

   public static String PARSING_INVALID_ELEMENT(Object arg0, Object arg1) {
      return localizer.localize(localizablePARSING_INVALID_ELEMENT(arg0, arg1));
   }

   public static Localizable localizableVALIDATION_INVALID_ELEMENT(Object arg0) {
      return messageFactory.getMessage("validation.invalidElement", new Object[]{arg0});
   }

   public static String VALIDATION_INVALID_ELEMENT(Object arg0) {
      return localizer.localize(localizableVALIDATION_INVALID_ELEMENT(arg0));
   }

   public static Localizable localizableINTERNALIZER_TWO_VERSION_ATTRIBUTES() {
      return messageFactory.getMessage("Internalizer.TwoVersionAttributes", new Object[0]);
   }

   public static String INTERNALIZER_TWO_VERSION_ATTRIBUTES() {
      return localizer.localize(localizableINTERNALIZER_TWO_VERSION_ATTRIBUTES());
   }

   public static Localizable localizableVALIDATION_DUPLICATE_PART_NAME(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.duplicatePartName", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_DUPLICATE_PART_NAME(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_DUPLICATE_PART_NAME(arg0, arg1));
   }

   public static Localizable localizablePARSING_INVALID_WSDL_ELEMENT(Object arg0) {
      return messageFactory.getMessage("parsing.invalidWsdlElement", new Object[]{arg0});
   }

   public static String PARSING_INVALID_WSDL_ELEMENT(Object arg0) {
      return localizer.localize(localizablePARSING_INVALID_WSDL_ELEMENT(arg0));
   }

   public static Localizable localizablePARSING_NON_WHITESPACE_TEXT_FOUND(Object arg0) {
      return messageFactory.getMessage("parsing.nonWhitespaceTextFound", new Object[]{arg0});
   }

   public static String PARSING_NON_WHITESPACE_TEXT_FOUND(Object arg0) {
      return localizer.localize(localizablePARSING_NON_WHITESPACE_TEXT_FOUND(arg0));
   }

   public static Localizable localizableINTERNALIZER_TARGET_NOT_FOUND(Object arg0) {
      return messageFactory.getMessage("internalizer.targetNotFound", new Object[]{arg0});
   }

   public static String INTERNALIZER_TARGET_NOT_FOUND(Object arg0) {
      return localizer.localize(localizableINTERNALIZER_TARGET_NOT_FOUND(arg0));
   }

   public static Localizable localizablePARSING_SAX_EXCEPTION_WITH_SYSTEM_ID(Object arg0) {
      return messageFactory.getMessage("parsing.saxExceptionWithSystemId", new Object[]{arg0});
   }

   public static String PARSING_SAX_EXCEPTION_WITH_SYSTEM_ID(Object arg0) {
      return localizer.localize(localizablePARSING_SAX_EXCEPTION_WITH_SYSTEM_ID(arg0));
   }

   public static Localizable localizablePARSING_REQUIRED_EXTENSIBILITY_ELEMENT(Object arg0, Object arg1) {
      return messageFactory.getMessage("parsing.requiredExtensibilityElement", new Object[]{arg0, arg1});
   }

   public static String PARSING_REQUIRED_EXTENSIBILITY_ELEMENT(Object arg0, Object arg1) {
      return localizer.localize(localizablePARSING_REQUIRED_EXTENSIBILITY_ELEMENT(arg0, arg1));
   }

   public static Localizable localizableENTITY_NOT_FOUND_BY_ID(Object arg0) {
      return messageFactory.getMessage("entity.notFoundByID", new Object[]{arg0});
   }

   public static String ENTITY_NOT_FOUND_BY_ID(Object arg0) {
      return localizer.localize(localizableENTITY_NOT_FOUND_BY_ID(arg0));
   }

   public static Localizable localizableVALIDATION_EXCLUSIVE_ATTRIBUTES(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.exclusiveAttributes", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_EXCLUSIVE_ATTRIBUTES(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_EXCLUSIVE_ATTRIBUTES(arg0, arg1));
   }

   public static Localizable localizableVALIDATION_MISSING_REQUIRED_SUB_ENTITY(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.missingRequiredSubEntity", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_MISSING_REQUIRED_SUB_ENTITY(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_MISSING_REQUIRED_SUB_ENTITY(arg0, arg1));
   }

   public static Localizable localizableINTERNALIZER_INCORRECT_VERSION() {
      return messageFactory.getMessage("Internalizer.IncorrectVersion", new Object[0]);
   }

   public static String INTERNALIZER_INCORRECT_VERSION() {
      return localizer.localize(localizableINTERNALIZER_INCORRECT_VERSION());
   }

   public static Localizable localizableLOCALIZED_ERROR(Object arg0) {
      return messageFactory.getMessage("localized.error", new Object[]{arg0});
   }

   public static String LOCALIZED_ERROR(Object arg0) {
      return localizer.localize(localizableLOCALIZED_ERROR(arg0));
   }

   public static Localizable localizableENTITY_DUPLICATE_WITH_TYPE(Object arg0, Object arg1) {
      return messageFactory.getMessage("entity.duplicateWithType", new Object[]{arg0, arg1});
   }

   public static String ENTITY_DUPLICATE_WITH_TYPE(Object arg0, Object arg1) {
      return localizer.localize(localizableENTITY_DUPLICATE_WITH_TYPE(arg0, arg1));
   }

   public static Localizable localizablePARSING_ONLY_ONE_OF_ELEMENT_OR_TYPE_REQUIRED(Object arg0) {
      return messageFactory.getMessage("parsing.onlyOneOfElementOrTypeRequired", new Object[]{arg0});
   }

   public static String PARSING_ONLY_ONE_OF_ELEMENT_OR_TYPE_REQUIRED(Object arg0) {
      return localizer.localize(localizablePARSING_ONLY_ONE_OF_ELEMENT_OR_TYPE_REQUIRED(arg0));
   }

   public static Localizable localizableVALIDATION_UNSUPPORTED_USE_ENCODED(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.unsupportedUse.encoded", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_UNSUPPORTED_USE_ENCODED(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_UNSUPPORTED_USE_ENCODED(arg0, arg1));
   }

   public static Localizable localizablePARSING_INCORRECT_ROOT_ELEMENT(Object arg0, Object arg1, Object arg2, Object arg3) {
      return messageFactory.getMessage("parsing.incorrectRootElement", new Object[]{arg0, arg1, arg2, arg3});
   }

   public static String PARSING_INCORRECT_ROOT_ELEMENT(Object arg0, Object arg1, Object arg2, Object arg3) {
      return localizer.localize(localizablePARSING_INCORRECT_ROOT_ELEMENT(arg0, arg1, arg2, arg3));
   }

   public static Localizable localizableTRY_WITH_MEX(Object arg0) {
      return messageFactory.getMessage("try.with.mex", new Object[]{arg0});
   }

   public static String TRY_WITH_MEX(Object arg0) {
      return localizer.localize(localizableTRY_WITH_MEX(arg0));
   }

   public static Localizable localizableVALIDATION_MISSING_REQUIRED_ATTRIBUTE(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.missingRequiredAttribute", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_MISSING_REQUIRED_ATTRIBUTE(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_MISSING_REQUIRED_ATTRIBUTE(arg0, arg1));
   }

   public static Localizable localizablePARSING_IO_EXCEPTION(Object arg0) {
      return messageFactory.getMessage("parsing.ioException", new Object[]{arg0});
   }

   public static String PARSING_IO_EXCEPTION(Object arg0) {
      return localizer.localize(localizablePARSING_IO_EXCEPTION(arg0));
   }

   public static Localizable localizableINTERNALIZER_X_PATH_EVAULATES_TO_TOO_MANY_TARGETS(Object arg0, Object arg1) {
      return messageFactory.getMessage("internalizer.XPathEvaulatesToTooManyTargets", new Object[]{arg0, arg1});
   }

   public static String INTERNALIZER_X_PATH_EVAULATES_TO_TOO_MANY_TARGETS(Object arg0, Object arg1) {
      return localizer.localize(localizableINTERNALIZER_X_PATH_EVAULATES_TO_TOO_MANY_TARGETS(arg0, arg1));
   }

   public static Localizable localizablePARSER_NOT_A_BINDING_FILE(Object arg0, Object arg1) {
      return messageFactory.getMessage("Parser.NotABindingFile", new Object[]{arg0, arg1});
   }

   public static String PARSER_NOT_A_BINDING_FILE(Object arg0, Object arg1) {
      return localizer.localize(localizablePARSER_NOT_A_BINDING_FILE(arg0, arg1));
   }

   public static Localizable localizablePARSING_UNKNOWN_NAMESPACE_PREFIX(Object arg0) {
      return messageFactory.getMessage("parsing.unknownNamespacePrefix", new Object[]{arg0});
   }

   public static String PARSING_UNKNOWN_NAMESPACE_PREFIX(Object arg0) {
      return localizer.localize(localizablePARSING_UNKNOWN_NAMESPACE_PREFIX(arg0));
   }

   public static Localizable localizablePARSING_FACTORY_CONFIG_EXCEPTION(Object arg0) {
      return messageFactory.getMessage("parsing.factoryConfigException", new Object[]{arg0});
   }

   public static String PARSING_FACTORY_CONFIG_EXCEPTION(Object arg0) {
      return localizer.localize(localizablePARSING_FACTORY_CONFIG_EXCEPTION(arg0));
   }

   public static Localizable localizableVALIDATION_MISSING_REQUIRED_PROPERTY(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.missingRequiredProperty", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_MISSING_REQUIRED_PROPERTY(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_MISSING_REQUIRED_PROPERTY(arg0, arg1));
   }

   public static Localizable localizablePARSING_INVALID_OPERATION_STYLE(Object arg0) {
      return messageFactory.getMessage("parsing.invalidOperationStyle", new Object[]{arg0});
   }

   public static String PARSING_INVALID_OPERATION_STYLE(Object arg0) {
      return localizer.localize(localizablePARSING_INVALID_OPERATION_STYLE(arg0));
   }

   public static Localizable localizableINTERNALIZER_X_PATH_EVALUATION_ERROR(Object arg0) {
      return messageFactory.getMessage("internalizer.XPathEvaluationError", new Object[]{arg0});
   }

   public static String INTERNALIZER_X_PATH_EVALUATION_ERROR(Object arg0) {
      return localizer.localize(localizableINTERNALIZER_X_PATH_EVALUATION_ERROR(arg0));
   }

   public static Localizable localizableVALIDATION_INVALID_SUB_ENTITY(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.invalidSubEntity", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_INVALID_SUB_ENTITY(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_INVALID_SUB_ENTITY(arg0, arg1));
   }

   public static Localizable localizableVALIDATION_SHOULD_NOT_HAPPEN(Object arg0) {
      return messageFactory.getMessage("validation.shouldNotHappen", new Object[]{arg0});
   }

   public static String VALIDATION_SHOULD_NOT_HAPPEN(Object arg0) {
      return localizer.localize(localizableVALIDATION_SHOULD_NOT_HAPPEN(arg0));
   }

   public static Localizable localizableABSTRACT_REFERENCE_FINDER_IMPL_UNABLE_TO_PARSE(Object arg0, Object arg1) {
      return messageFactory.getMessage("AbstractReferenceFinderImpl.UnableToParse", new Object[]{arg0, arg1});
   }

   public static String ABSTRACT_REFERENCE_FINDER_IMPL_UNABLE_TO_PARSE(Object arg0, Object arg1) {
      return localizer.localize(localizableABSTRACT_REFERENCE_FINDER_IMPL_UNABLE_TO_PARSE(arg0, arg1));
   }

   public static Localizable localizableWARNING_FAULT_EMPTY_ACTION(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("warning.faultEmptyAction", new Object[]{arg0, arg1, arg2});
   }

   public static String WARNING_FAULT_EMPTY_ACTION(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableWARNING_FAULT_EMPTY_ACTION(arg0, arg1, arg2));
   }

   public static Localizable localizablePARSING_INVALID_EXTENSION_ELEMENT(Object arg0, Object arg1) {
      return messageFactory.getMessage("parsing.invalidExtensionElement", new Object[]{arg0, arg1});
   }

   public static String PARSING_INVALID_EXTENSION_ELEMENT(Object arg0, Object arg1) {
      return localizer.localize(localizablePARSING_INVALID_EXTENSION_ELEMENT(arg0, arg1));
   }

   public static Localizable localizableINTERNALIZER_X_PATH_EVALUATES_TO_NON_ELEMENT(Object arg0) {
      return messageFactory.getMessage("internalizer.XPathEvaluatesToNonElement", new Object[]{arg0});
   }

   public static String INTERNALIZER_X_PATH_EVALUATES_TO_NON_ELEMENT(Object arg0) {
      return localizer.localize(localizableINTERNALIZER_X_PATH_EVALUATES_TO_NON_ELEMENT(arg0));
   }

   public static Localizable localizableINTERNALIZER_X_PATH_EVALUATES_TO_NO_TARGET(Object arg0) {
      return messageFactory.getMessage("internalizer.XPathEvaluatesToNoTarget", new Object[]{arg0});
   }

   public static String INTERNALIZER_X_PATH_EVALUATES_TO_NO_TARGET(Object arg0) {
      return localizer.localize(localizableINTERNALIZER_X_PATH_EVALUATES_TO_NO_TARGET(arg0));
   }

   public static Localizable localizablePARSING_SAX_EXCEPTION(Object arg0) {
      return messageFactory.getMessage("parsing.saxException", new Object[]{arg0});
   }

   public static String PARSING_SAX_EXCEPTION(Object arg0) {
      return localizer.localize(localizablePARSING_SAX_EXCEPTION(arg0));
   }

   public static Localizable localizableINVALID_CUSTOMIZATION_NAMESPACE(Object arg0) {
      return messageFactory.getMessage("invalid.customization.namespace", new Object[]{arg0});
   }

   public static String INVALID_CUSTOMIZATION_NAMESPACE(Object arg0) {
      return localizer.localize(localizableINVALID_CUSTOMIZATION_NAMESPACE(arg0));
   }

   public static Localizable localizableVALIDATION_INVALID_ATTRIBUTE(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.invalidAttribute", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_INVALID_ATTRIBUTE(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_INVALID_ATTRIBUTE(arg0, arg1));
   }

   public static Localizable localizablePARSING_PARSER_CONFIG_EXCEPTION(Object arg0) {
      return messageFactory.getMessage("parsing.parserConfigException", new Object[]{arg0});
   }

   public static String PARSING_PARSER_CONFIG_EXCEPTION(Object arg0) {
      return localizer.localize(localizablePARSING_PARSER_CONFIG_EXCEPTION(arg0));
   }

   public static Localizable localizablePARSING_ONLY_ONE_TYPES_ALLOWED(Object arg0) {
      return messageFactory.getMessage("parsing.onlyOneTypesAllowed", new Object[]{arg0});
   }

   public static String PARSING_ONLY_ONE_TYPES_ALLOWED(Object arg0) {
      return localizer.localize(localizablePARSING_ONLY_ONE_TYPES_ALLOWED(arg0));
   }

   public static Localizable localizablePARSING_INVALID_URI(Object arg0) {
      return messageFactory.getMessage("parsing.invalidURI", new Object[]{arg0});
   }

   public static String PARSING_INVALID_URI(Object arg0) {
      return localizer.localize(localizablePARSING_INVALID_URI(arg0));
   }

   public static Localizable localizableVALIDATION_INCORRECT_TARGET_NAMESPACE(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.incorrectTargetNamespace", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_INCORRECT_TARGET_NAMESPACE(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_INCORRECT_TARGET_NAMESPACE(arg0, arg1));
   }

   public static Localizable localizableENTITY_NOT_FOUND_BY_Q_NAME(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("entity.notFoundByQName", new Object[]{arg0, arg1, arg2});
   }

   public static String ENTITY_NOT_FOUND_BY_Q_NAME(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizableENTITY_NOT_FOUND_BY_Q_NAME(arg0, arg1, arg2));
   }

   public static Localizable localizableINVALID_WSDL(Object arg0, Object arg1, Object arg2, Object arg3) {
      return messageFactory.getMessage("invalid.wsdl", new Object[]{arg0, arg1, arg2, arg3});
   }

   public static String INVALID_WSDL(Object arg0, Object arg1, Object arg2, Object arg3) {
      return localizer.localize(localizableINVALID_WSDL(arg0, arg1, arg2, arg3));
   }

   public static Localizable localizablePARSING_UNKNOWN_IMPORTED_DOCUMENT_TYPE(Object arg0) {
      return messageFactory.getMessage("parsing.unknownImportedDocumentType", new Object[]{arg0});
   }

   public static String PARSING_UNKNOWN_IMPORTED_DOCUMENT_TYPE(Object arg0) {
      return localizer.localize(localizablePARSING_UNKNOWN_IMPORTED_DOCUMENT_TYPE(arg0));
   }

   public static Localizable localizablePARSING_IO_EXCEPTION_WITH_SYSTEM_ID(Object arg0) {
      return messageFactory.getMessage("parsing.ioExceptionWithSystemId", new Object[]{arg0});
   }

   public static String PARSING_IO_EXCEPTION_WITH_SYSTEM_ID(Object arg0) {
      return localizer.localize(localizablePARSING_IO_EXCEPTION_WITH_SYSTEM_ID(arg0));
   }

   public static Localizable localizableVALIDATION_AMBIGUOUS_NAME(Object arg0) {
      return messageFactory.getMessage("validation.ambiguousName", new Object[]{arg0});
   }

   public static String VALIDATION_AMBIGUOUS_NAME(Object arg0) {
      return localizer.localize(localizableVALIDATION_AMBIGUOUS_NAME(arg0));
   }

   public static Localizable localizablePARSING_WSDL_NOT_DEFAULT_NAMESPACE(Object arg0) {
      return messageFactory.getMessage("parsing.wsdlNotDefaultNamespace", new Object[]{arg0});
   }

   public static String PARSING_WSDL_NOT_DEFAULT_NAMESPACE(Object arg0) {
      return localizer.localize(localizablePARSING_WSDL_NOT_DEFAULT_NAMESPACE(arg0));
   }

   public static Localizable localizablePARSING_UNKNOWN_EXTENSIBILITY_ELEMENT_OR_ATTRIBUTE(Object arg0, Object arg1) {
      return messageFactory.getMessage("parsing.unknownExtensibilityElementOrAttribute", new Object[]{arg0, arg1});
   }

   public static String PARSING_UNKNOWN_EXTENSIBILITY_ELEMENT_OR_ATTRIBUTE(Object arg0, Object arg1) {
      return localizer.localize(localizablePARSING_UNKNOWN_EXTENSIBILITY_ELEMENT_OR_ATTRIBUTE(arg0, arg1));
   }

   public static Localizable localizableVALIDATION_DUPLICATED_ELEMENT(Object arg0) {
      return messageFactory.getMessage("validation.duplicatedElement", new Object[]{arg0});
   }

   public static String VALIDATION_DUPLICATED_ELEMENT(Object arg0) {
      return localizer.localize(localizableVALIDATION_DUPLICATED_ELEMENT(arg0));
   }

   public static Localizable localizableINTERNALIZER_TARGET_NOT_AN_ELEMENT() {
      return messageFactory.getMessage("internalizer.targetNotAnElement", new Object[0]);
   }

   public static String INTERNALIZER_TARGET_NOT_AN_ELEMENT() {
      return localizer.localize(localizableINTERNALIZER_TARGET_NOT_AN_ELEMENT());
   }

   public static Localizable localizableWARNING_INPUT_OUTPUT_EMPTY_ACTION(Object arg0, Object arg1) {
      return messageFactory.getMessage("warning.inputOutputEmptyAction", new Object[]{arg0, arg1});
   }

   public static String WARNING_INPUT_OUTPUT_EMPTY_ACTION(Object arg0, Object arg1) {
      return localizer.localize(localizableWARNING_INPUT_OUTPUT_EMPTY_ACTION(arg0, arg1));
   }

   public static Localizable localizablePARSING_INVALID_TAG_NS(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
      return messageFactory.getMessage("parsing.invalidTagNS", new Object[]{arg0, arg1, arg2, arg3, arg4});
   }

   public static String PARSING_INVALID_TAG_NS(Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) {
      return localizer.localize(localizablePARSING_INVALID_TAG_NS(arg0, arg1, arg2, arg3, arg4));
   }

   public static Localizable localizableINVALID_WSDL_WITH_DOOC(Object arg0, Object arg1) {
      return messageFactory.getMessage("invalid.wsdl.with.dooc", new Object[]{arg0, arg1});
   }

   public static String INVALID_WSDL_WITH_DOOC(Object arg0, Object arg1) {
      return localizer.localize(localizableINVALID_WSDL_WITH_DOOC(arg0, arg1));
   }

   public static Localizable localizablePARSING_NOT_AWSDL(Object arg0) {
      return messageFactory.getMessage("Parsing.NotAWSDL", new Object[]{arg0});
   }

   public static String PARSING_NOT_AWSDL(Object arg0) {
      return localizer.localize(localizablePARSING_NOT_AWSDL(arg0));
   }

   public static Localizable localizableENTITY_DUPLICATE(Object arg0) {
      return messageFactory.getMessage("entity.duplicate", new Object[]{arg0});
   }

   public static String ENTITY_DUPLICATE(Object arg0) {
      return localizer.localize(localizableENTITY_DUPLICATE(arg0));
   }

   public static Localizable localizableWARNING_WSI_R_2004() {
      return messageFactory.getMessage("warning.wsi.r2004", new Object[0]);
   }

   public static String WARNING_WSI_R_2004() {
      return localizer.localize(localizableWARNING_WSI_R_2004());
   }

   public static Localizable localizableWARNING_WSI_R_2003() {
      return messageFactory.getMessage("warning.wsi.r2003", new Object[0]);
   }

   public static String WARNING_WSI_R_2003() {
      return localizer.localize(localizableWARNING_WSI_R_2003());
   }

   public static Localizable localizableWARNING_WSI_R_2002(Object arg0, Object arg1) {
      return messageFactory.getMessage("warning.wsi.r2002", new Object[]{arg0, arg1});
   }

   public static String WARNING_WSI_R_2002(Object arg0, Object arg1) {
      return localizer.localize(localizableWARNING_WSI_R_2002(arg0, arg1));
   }

   public static Localizable localizablePARSING_ELEMENT_OR_TYPE_REQUIRED(Object arg0) {
      return messageFactory.getMessage("parsing.elementOrTypeRequired", new Object[]{arg0});
   }

   public static String PARSING_ELEMENT_OR_TYPE_REQUIRED(Object arg0) {
      return localizer.localize(localizablePARSING_ELEMENT_OR_TYPE_REQUIRED(arg0));
   }

   public static Localizable localizableWARNING_WSI_R_2001() {
      return messageFactory.getMessage("warning.wsi.r2001", new Object[0]);
   }

   public static String WARNING_WSI_R_2001() {
      return localizer.localize(localizableWARNING_WSI_R_2001());
   }

   public static Localizable localizableFILE_NOT_FOUND(Object arg0) {
      return messageFactory.getMessage("file.not.found", new Object[]{arg0});
   }

   public static String FILE_NOT_FOUND(Object arg0) {
      return localizer.localize(localizableFILE_NOT_FOUND(arg0));
   }

   public static Localizable localizableVALIDATION_INVALID_SIMPLE_TYPE_IN_ELEMENT(Object arg0, Object arg1) {
      return messageFactory.getMessage("validation.invalidSimpleTypeInElement", new Object[]{arg0, arg1});
   }

   public static String VALIDATION_INVALID_SIMPLE_TYPE_IN_ELEMENT(Object arg0, Object arg1) {
      return localizer.localize(localizableVALIDATION_INVALID_SIMPLE_TYPE_IN_ELEMENT(arg0, arg1));
   }

   public static Localizable localizablePARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(Object arg0) {
      return messageFactory.getMessage("parsing.onlyOneDocumentationAllowed", new Object[]{arg0});
   }

   public static String PARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(Object arg0) {
      return localizer.localize(localizablePARSING_ONLY_ONE_DOCUMENTATION_ALLOWED(arg0));
   }

   public static Localizable localizableINTERNALIZER_VERSION_NOT_PRESENT() {
      return messageFactory.getMessage("Internalizer.VersionNotPresent", new Object[0]);
   }

   public static String INTERNALIZER_VERSION_NOT_PRESENT() {
      return localizer.localize(localizableINTERNALIZER_VERSION_NOT_PRESENT());
   }

   public static Localizable localizableFAILED_NOSERVICE(Object arg0) {
      return messageFactory.getMessage("failed.noservice", new Object[]{arg0});
   }

   public static String FAILED_NOSERVICE(Object arg0) {
      return localizer.localize(localizableFAILED_NOSERVICE(arg0));
   }

   public static Localizable localizablePARSING_TOO_MANY_ELEMENTS(Object arg0, Object arg1, Object arg2) {
      return messageFactory.getMessage("parsing.tooManyElements", new Object[]{arg0, arg1, arg2});
   }

   public static String PARSING_TOO_MANY_ELEMENTS(Object arg0, Object arg1, Object arg2) {
      return localizer.localize(localizablePARSING_TOO_MANY_ELEMENTS(arg0, arg1, arg2));
   }

   public static Localizable localizableINTERNALIZER_INCORRECT_SCHEMA_REFERENCE(Object arg0, Object arg1) {
      return messageFactory.getMessage("Internalizer.IncorrectSchemaReference", new Object[]{arg0, arg1});
   }

   public static String INTERNALIZER_INCORRECT_SCHEMA_REFERENCE(Object arg0, Object arg1) {
      return localizer.localize(localizableINTERNALIZER_INCORRECT_SCHEMA_REFERENCE(arg0, arg1));
   }
}
