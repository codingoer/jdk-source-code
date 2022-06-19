package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
   ERR_CANNOT_BE_BOUND_TO_SIMPLETYPE,
   ERR_UNDEFINED_SIMPLE_TYPE,
   ERR_ILLEGAL_FIXEDATTR;

   String format(Object... args) {
      String text = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".MessageBundle").getString(this.name());
      return MessageFormat.format(text, args);
   }
}
