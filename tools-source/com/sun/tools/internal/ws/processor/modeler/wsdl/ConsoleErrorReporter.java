package com.sun.tools.internal.ws.processor.modeler.wsdl;

import com.sun.tools.internal.ws.resources.WscompileMessages;
import com.sun.tools.internal.ws.wscompile.ErrorReceiver;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import org.xml.sax.SAXParseException;

public class ConsoleErrorReporter extends ErrorReceiver {
   private boolean hasError;
   private PrintStream output;
   private boolean debug;

   public ConsoleErrorReporter(PrintStream stream) {
      this.output = stream;
   }

   public ConsoleErrorReporter(OutputStream outputStream) {
      this.output = new PrintStream(outputStream);
   }

   public boolean hasError() {
      return this.hasError;
   }

   public void error(SAXParseException e) {
      if (this.debug) {
         e.printStackTrace();
      }

      this.hasError = true;
      if (e.getSystemId() == null && e.getPublicId() == null && e.getCause() instanceof UnknownHostException) {
         this.print(WscompileMessages.WSIMPORT_ERROR_MESSAGE(e.toString()), e);
      } else {
         this.print(WscompileMessages.WSIMPORT_ERROR_MESSAGE(e.getMessage()), e);
      }

   }

   public void fatalError(SAXParseException e) {
      if (this.debug) {
         e.printStackTrace();
      }

      this.hasError = true;
      this.print(WscompileMessages.WSIMPORT_ERROR_MESSAGE(e.getMessage()), e);
   }

   public void warning(SAXParseException e) {
      this.print(WscompileMessages.WSIMPORT_WARNING_MESSAGE(e.getMessage()), e);
   }

   public void info(SAXParseException e) {
      this.print(WscompileMessages.WSIMPORT_INFO_MESSAGE(e.getMessage()), e);
   }

   public void debug(SAXParseException e) {
      this.print(WscompileMessages.WSIMPORT_DEBUG_MESSAGE(e.getMessage()), e);
   }

   private void print(String message, SAXParseException e) {
      this.output.println(message);
      this.output.println(this.getLocationString(e));
      this.output.println();
   }

   public void enableDebugging() {
      this.debug = true;
   }
}
