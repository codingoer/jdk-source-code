package com.sun.tools.internal.xjc.api.impl.s2j;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

final class DowngradingErrorHandler implements ErrorHandler {
   private final ErrorHandler core;

   public DowngradingErrorHandler(ErrorHandler core) {
      this.core = core;
   }

   public void warning(SAXParseException exception) throws SAXException {
      this.core.warning(exception);
   }

   public void error(SAXParseException exception) throws SAXException {
      this.core.warning(exception);
   }

   public void fatalError(SAXParseException exception) throws SAXException {
      this.core.warning(exception);
   }
}
