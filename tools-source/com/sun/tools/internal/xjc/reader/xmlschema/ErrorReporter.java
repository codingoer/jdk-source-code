package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.reader.Ring;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public final class ErrorReporter extends BindingComponent {
   private final ErrorReceiver errorReceiver = (ErrorReceiver)Ring.get(ErrorReceiver.class);

   void error(Locator loc, String prop, Object... args) {
      this.errorReceiver.error(loc, Messages.format(prop, args));
   }

   void warning(Locator loc, String prop, Object... args) {
      this.errorReceiver.warning(new SAXParseException(Messages.format(prop, args), loc));
   }
}
