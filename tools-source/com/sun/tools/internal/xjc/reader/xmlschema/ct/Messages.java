package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
   ERR_NO_FURTHER_EXTENSION;

   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".MessageBundle");

   public String toString() {
      return this.format();
   }

   public String format(Object... args) {
      return MessageFormat.format(rb.getString(this.name()), args);
   }
}
