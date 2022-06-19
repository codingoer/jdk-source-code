package com.sun.tools.internal.xjc.util;

import com.sun.tools.internal.xjc.ErrorReceiver;
import com.sun.tools.internal.xjc.api.ErrorListener;
import org.xml.sax.SAXParseException;

public class ErrorReceiverFilter extends ErrorReceiver {
   private ErrorListener core;
   private boolean hadError = false;

   public ErrorReceiverFilter() {
   }

   public ErrorReceiverFilter(ErrorListener h) {
      this.setErrorReceiver(h);
   }

   public void setErrorReceiver(ErrorListener handler) {
      this.core = handler;
   }

   public final boolean hadError() {
      return this.hadError;
   }

   public void info(SAXParseException exception) {
      if (this.core != null) {
         this.core.info(exception);
      }

   }

   public void warning(SAXParseException exception) {
      if (this.core != null) {
         this.core.warning(exception);
      }

   }

   public void error(SAXParseException exception) {
      this.hadError = true;
      if (this.core != null) {
         this.core.error(exception);
      }

   }

   public void fatalError(SAXParseException exception) {
      this.hadError = true;
      if (this.core != null) {
         this.core.fatalError(exception);
      }

   }
}
