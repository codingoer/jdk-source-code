package com.sun.tools.internal.xjc.generator.bean.field;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public enum Messages {
   DEFAULT_GETTER_JAVADOC,
   DEFAULT_SETTER_JAVADOC;

   private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName().substring(0, Messages.class.getName().lastIndexOf(46)) + ".MessageBundle");

   public String toString() {
      return this.format();
   }

   public String format(Object... args) {
      return MessageFormat.format(rb.getString(this.name()), args);
   }
}
