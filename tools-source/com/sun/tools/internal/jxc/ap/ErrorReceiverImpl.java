package com.sun.tools.internal.jxc.ap;

import com.sun.tools.internal.xjc.ErrorReceiver;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;
import org.xml.sax.SAXParseException;

final class ErrorReceiverImpl extends ErrorReceiver {
   private final Messager messager;
   private final boolean debug;

   public ErrorReceiverImpl(Messager messager, boolean debug) {
      this.messager = messager;
      this.debug = debug;
   }

   public ErrorReceiverImpl(Messager messager) {
      this(messager, false);
   }

   public ErrorReceiverImpl(ProcessingEnvironment env) {
      this(env.getMessager());
   }

   public void error(SAXParseException exception) {
      this.messager.printMessage(Kind.ERROR, exception.getMessage());
      this.messager.printMessage(Kind.ERROR, this.getLocation(exception));
      this.printDetail(exception);
   }

   public void fatalError(SAXParseException exception) {
      this.messager.printMessage(Kind.ERROR, exception.getMessage());
      this.messager.printMessage(Kind.ERROR, this.getLocation(exception));
      this.printDetail(exception);
   }

   public void warning(SAXParseException exception) {
      this.messager.printMessage(Kind.WARNING, exception.getMessage());
      this.messager.printMessage(Kind.WARNING, this.getLocation(exception));
      this.printDetail(exception);
   }

   public void info(SAXParseException exception) {
      this.printDetail(exception);
   }

   private String getLocation(SAXParseException e) {
      return "";
   }

   private void printDetail(SAXParseException e) {
      if (this.debug) {
         e.printStackTrace(System.out);
      }

   }
}
