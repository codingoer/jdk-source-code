package com.sun.tools.internal.jxc;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
   UNEXPECTED_NGCC_TOKEN,
   BASEDIR_DOESNT_EXIST,
   USAGE,
   FULLVERSION,
   VERSION;

   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".MessageBundle");

   public String toString() {
      return this.format();
   }

   public String format(Object... args) {
      return MessageFormat.format(rb.getString(this.name()), args);
   }
}
