package com.sun.tools.internal.xjc.api.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
   TOOLS_JAR_NOT_FOUND;

   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());

   public String toString() {
      return this.format();
   }

   public String format(Object... args) {
      return MessageFormat.format(rb.getString(this.name()), args);
   }
}
