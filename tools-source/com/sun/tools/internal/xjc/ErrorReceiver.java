package com.sun.tools.internal.xjc;

import com.sun.istack.internal.SAXParseException2;
import com.sun.tools.internal.xjc.api.ErrorListener;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public abstract class ErrorReceiver implements ErrorHandler, ErrorListener {
   public final void error(Locator loc, String msg) {
      this.error((SAXParseException)(new SAXParseException2(msg, loc)));
   }

   public final void error(Locator loc, String msg, Exception e) {
      this.error((SAXParseException)(new SAXParseException2(msg, loc, e)));
   }

   public final void error(String msg, Exception e) {
      this.error((SAXParseException)(new SAXParseException2(msg, (Locator)null, e)));
   }

   public void error(Exception e) {
      this.error(e.getMessage(), e);
   }

   public final void warning(Locator loc, String msg) {
      this.warning(new SAXParseException(msg, loc));
   }

   public abstract void error(SAXParseException var1) throws AbortException;

   public abstract void fatalError(SAXParseException var1) throws AbortException;

   public abstract void warning(SAXParseException var1) throws AbortException;

   public void pollAbort() throws AbortException {
   }

   public abstract void info(SAXParseException var1);

   public final void debug(String msg) {
      this.info(new SAXParseException(msg, (Locator)null));
   }

   protected final String getLocationString(SAXParseException e) {
      if (e.getLineNumber() == -1 && e.getSystemId() == null) {
         return Messages.format("ConsoleErrorReporter.UnknownLocation");
      } else {
         int line = e.getLineNumber();
         return Messages.format("ConsoleErrorReporter.LineXOfY", line == -1 ? "?" : Integer.toString(line), this.getShortName(e.getSystemId()));
      }
   }

   private String getShortName(String url) {
      return url == null ? Messages.format("ConsoleErrorReporter.UnknownFile") : url;
   }
}
