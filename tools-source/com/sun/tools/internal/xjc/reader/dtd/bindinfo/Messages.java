package com.sun.tools.internal.xjc.reader.dtd.bindinfo;

import java.text.MessageFormat;
import java.util.ResourceBundle;

class Messages {
   static final String ERR_UNDEFINED_FIELD = "BIConstructor.UndefinedField";

   static String format(String property, Object... args) {
      String text = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".MessageBundle").getString(property);
      return MessageFormat.format(text, args);
   }
}
