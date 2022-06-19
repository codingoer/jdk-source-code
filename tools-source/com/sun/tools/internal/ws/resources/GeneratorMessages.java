package com.sun.tools.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class GeneratorMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.tools.internal.ws.resources.generator");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableGENERATOR_SERVICE_CLASS_ALREADY_EXIST(Object arg0, Object arg1) {
      return messageFactory.getMessage("generator.service.classAlreadyExist", new Object[]{arg0, arg1});
   }

   public static String GENERATOR_SERVICE_CLASS_ALREADY_EXIST(Object arg0, Object arg1) {
      return localizer.localize(localizableGENERATOR_SERVICE_CLASS_ALREADY_EXIST(arg0, arg1));
   }

   public static Localizable localizableGENERATOR_SEI_CLASS_ALREADY_EXIST(Object arg0, Object arg1) {
      return messageFactory.getMessage("generator.sei.classAlreadyExist", new Object[]{arg0, arg1});
   }

   public static String GENERATOR_SEI_CLASS_ALREADY_EXIST(Object arg0, Object arg1) {
      return localizer.localize(localizableGENERATOR_SEI_CLASS_ALREADY_EXIST(arg0, arg1));
   }

   public static Localizable localizableGENERATOR_NESTED_GENERATOR_ERROR(Object arg0) {
      return messageFactory.getMessage("generator.nestedGeneratorError", new Object[]{arg0});
   }

   public static String GENERATOR_NESTED_GENERATOR_ERROR(Object arg0) {
      return localizer.localize(localizableGENERATOR_NESTED_GENERATOR_ERROR(arg0));
   }

   public static Localizable localizableGENERATOR_INTERNAL_ERROR_SHOULD_NOT_HAPPEN(Object arg0) {
      return messageFactory.getMessage("generator.internal.error.should.not.happen", new Object[]{arg0});
   }

   public static String GENERATOR_INTERNAL_ERROR_SHOULD_NOT_HAPPEN(Object arg0) {
      return localizer.localize(localizableGENERATOR_INTERNAL_ERROR_SHOULD_NOT_HAPPEN(arg0));
   }

   public static Localizable localizableGENERATOR_INDENTINGWRITER_CHARSET_CANTENCODE(Object arg0) {
      return messageFactory.getMessage("generator.indentingwriter.charset.cantencode", new Object[]{arg0});
   }

   public static String GENERATOR_INDENTINGWRITER_CHARSET_CANTENCODE(Object arg0) {
      return localizer.localize(localizableGENERATOR_INDENTINGWRITER_CHARSET_CANTENCODE(arg0));
   }

   public static Localizable localizableGENERATOR_CANNOT_CREATE_DIR(Object arg0) {
      return messageFactory.getMessage("generator.cannot.create.dir", new Object[]{arg0});
   }

   public static String GENERATOR_CANNOT_CREATE_DIR(Object arg0) {
      return localizer.localize(localizableGENERATOR_CANNOT_CREATE_DIR(arg0));
   }
}
