package com.sun.tools.internal.xjc;

import java.io.OutputStream;
import java.io.PrintStream;
import org.xml.sax.SAXParseException;

public class ConsoleErrorReporter extends ErrorReceiver {
   private PrintStream output;
   private boolean hadError;

   public ConsoleErrorReporter(PrintStream out) {
      this.hadError = false;
      this.output = out;
   }

   public ConsoleErrorReporter(OutputStream out) {
      this(new PrintStream(out));
   }

   public ConsoleErrorReporter() {
      this(System.out);
   }

   public void warning(SAXParseException e) {
      this.print("Driver.WarningMessage", e);
   }

   public void error(SAXParseException e) {
      this.hadError = true;
      this.print("Driver.ErrorMessage", e);
   }

   public void fatalError(SAXParseException e) {
      this.hadError = true;
      this.print("Driver.ErrorMessage", e);
   }

   public void info(SAXParseException e) {
      this.print("Driver.InfoMessage", e);
   }

   public boolean hadError() {
      return this.hadError;
   }

   private void print(String resource, SAXParseException e) {
      this.output.println(Messages.format(resource, e.getMessage()));
      this.output.println(this.getLocationString(e));
      this.output.println();
   }
}
