package com.sun.tools.internal.xjc.reader;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public enum Messages {
   DUPLICATE_PROPERTY,
   DUPLICATE_ELEMENT,
   ERR_UNDECLARED_PREFIX,
   ERR_UNEXPECTED_EXTENSION_BINDING_PREFIXES,
   ERR_UNSUPPORTED_EXTENSION,
   ERR_SUPPORTED_EXTENSION_IGNORED,
   ERR_RELEVANT_LOCATION,
   ERR_CLASS_NOT_FOUND,
   PROPERTY_CLASS_IS_RESERVED,
   ERR_VENDOR_EXTENSION_DISALLOWED_IN_STRICT_MODE,
   ERR_ILLEGAL_CUSTOMIZATION_TAGNAME,
   ERR_PLUGIN_NOT_ENABLED;

   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".MessageBundle");

   public String toString() {
      return this.format();
   }

   public String format(Object... args) {
      return MessageFormat.format(rb.getString(this.name()), args);
   }
}
