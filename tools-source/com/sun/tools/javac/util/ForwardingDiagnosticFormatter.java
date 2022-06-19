package com.sun.tools.javac.util;

import com.sun.tools.javac.api.DiagnosticFormatter;
import java.util.Locale;
import java.util.Set;
import javax.tools.Diagnostic;

public class ForwardingDiagnosticFormatter implements DiagnosticFormatter {
   protected DiagnosticFormatter formatter;
   protected ForwardingConfiguration configuration;

   public ForwardingDiagnosticFormatter(DiagnosticFormatter var1) {
      this.formatter = var1;
      this.configuration = new ForwardingConfiguration(var1.getConfiguration());
   }

   public DiagnosticFormatter getDelegatedFormatter() {
      return this.formatter;
   }

   public DiagnosticFormatter.Configuration getConfiguration() {
      return this.configuration;
   }

   public boolean displaySource(Diagnostic var1) {
      return this.formatter.displaySource(var1);
   }

   public String format(Diagnostic var1, Locale var2) {
      return this.formatter.format(var1, var2);
   }

   public String formatKind(Diagnostic var1, Locale var2) {
      return this.formatter.formatKind(var1, var2);
   }

   public String formatMessage(Diagnostic var1, Locale var2) {
      return this.formatter.formatMessage(var1, var2);
   }

   public String formatPosition(Diagnostic var1, DiagnosticFormatter.PositionKind var2, Locale var3) {
      return this.formatter.formatPosition(var1, var2, var3);
   }

   public String formatSource(Diagnostic var1, boolean var2, Locale var3) {
      return this.formatter.formatSource(var1, var2, var3);
   }

   public static class ForwardingConfiguration implements DiagnosticFormatter.Configuration {
      protected DiagnosticFormatter.Configuration configuration;

      public ForwardingConfiguration(DiagnosticFormatter.Configuration var1) {
         this.configuration = var1;
      }

      public DiagnosticFormatter.Configuration getDelegatedConfiguration() {
         return this.configuration;
      }

      public int getMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit var1) {
         return this.configuration.getMultilineLimit(var1);
      }

      public Set getVisible() {
         return this.configuration.getVisible();
      }

      public void setMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit var1, int var2) {
         this.configuration.setMultilineLimit(var1, var2);
      }

      public void setVisible(Set var1) {
         this.configuration.setVisible(var1);
      }
   }
}
