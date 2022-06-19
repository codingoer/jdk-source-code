package com.sun.tools.internal.ws.resources;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;

public final class ConfigurationMessages {
   private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("com.sun.tools.internal.ws.resources.configuration");
   private static final Localizer localizer = new Localizer();

   public static Localizable localizableCONFIGURATION_NOT_BINDING_FILE(Object arg0) {
      return messageFactory.getMessage("configuration.notBindingFile", new Object[]{arg0});
   }

   public static String CONFIGURATION_NOT_BINDING_FILE(Object arg0) {
      return localizer.localize(localizableCONFIGURATION_NOT_BINDING_FILE(arg0));
   }
}
