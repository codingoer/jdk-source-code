package com.sun.tools.internal.jxc.ap;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
   NON_EXISTENT_FILE,
   UNRECOGNIZED_PARAMETER,
   OPERAND_MISSING;

   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".MessageBundle");

   public String toString() {
      return this.format();
   }

   public String format(Object... args) {
      return MessageFormat.format(rb.getString(this.name()), args);
   }
}
