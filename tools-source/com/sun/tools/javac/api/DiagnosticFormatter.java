package com.sun.tools.javac.api;

import java.util.Locale;
import java.util.Set;
import javax.tools.Diagnostic;

public interface DiagnosticFormatter {
   boolean displaySource(Diagnostic var1);

   String format(Diagnostic var1, Locale var2);

   String formatMessage(Diagnostic var1, Locale var2);

   String formatKind(Diagnostic var1, Locale var2);

   String formatSource(Diagnostic var1, boolean var2, Locale var3);

   String formatPosition(Diagnostic var1, PositionKind var2, Locale var3);

   Configuration getConfiguration();

   public interface Configuration {
      void setVisible(Set var1);

      Set getVisible();

      void setMultilineLimit(MultilineLimit var1, int var2);

      int getMultilineLimit(MultilineLimit var1);

      public static enum MultilineLimit {
         DEPTH,
         LENGTH;
      }

      public static enum DiagnosticPart {
         SUMMARY,
         DETAILS,
         SOURCE,
         SUBDIAGNOSTICS,
         JLS;
      }
   }

   public static enum PositionKind {
      START,
      END,
      LINE,
      COLUMN,
      OFFSET;
   }
}
