package com.sun.tools.javac.util;

import com.sun.tools.javac.code.Lint;
import java.util.HashSet;
import java.util.Set;
import javax.tools.JavaFileObject;

public class MandatoryWarningHandler {
   private Log log;
   private boolean verbose;
   private String prefix;
   private Set sourcesWithReportedWarnings;
   private DeferredDiagnosticKind deferredDiagnosticKind;
   private JavaFileObject deferredDiagnosticSource;
   private Object deferredDiagnosticArg;
   private final boolean enforceMandatory;
   private final Lint.LintCategory lintCategory;

   public MandatoryWarningHandler(Log var1, boolean var2, boolean var3, String var4, Lint.LintCategory var5) {
      this.log = var1;
      this.verbose = var2;
      this.prefix = var4;
      this.enforceMandatory = var3;
      this.lintCategory = var5;
   }

   public void report(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      JavaFileObject var4 = this.log.currentSourceFile();
      if (this.verbose) {
         if (this.sourcesWithReportedWarnings == null) {
            this.sourcesWithReportedWarnings = new HashSet();
         }

         if (this.log.nwarnings < this.log.MaxWarnings) {
            this.logMandatoryWarning(var1, var2, var3);
            this.sourcesWithReportedWarnings.add(var4);
         } else if (this.deferredDiagnosticKind == null) {
            if (this.sourcesWithReportedWarnings.contains(var4)) {
               this.deferredDiagnosticKind = MandatoryWarningHandler.DeferredDiagnosticKind.ADDITIONAL_IN_FILE;
            } else {
               this.deferredDiagnosticKind = MandatoryWarningHandler.DeferredDiagnosticKind.IN_FILE;
            }

            this.deferredDiagnosticSource = var4;
            this.deferredDiagnosticArg = var4;
         } else if ((this.deferredDiagnosticKind == MandatoryWarningHandler.DeferredDiagnosticKind.IN_FILE || this.deferredDiagnosticKind == MandatoryWarningHandler.DeferredDiagnosticKind.ADDITIONAL_IN_FILE) && !equal(this.deferredDiagnosticSource, var4)) {
            this.deferredDiagnosticKind = MandatoryWarningHandler.DeferredDiagnosticKind.ADDITIONAL_IN_FILES;
            this.deferredDiagnosticArg = null;
         }
      } else if (this.deferredDiagnosticKind == null) {
         this.deferredDiagnosticKind = MandatoryWarningHandler.DeferredDiagnosticKind.IN_FILE;
         this.deferredDiagnosticSource = var4;
         this.deferredDiagnosticArg = var4;
      } else if (this.deferredDiagnosticKind == MandatoryWarningHandler.DeferredDiagnosticKind.IN_FILE && !equal(this.deferredDiagnosticSource, var4)) {
         this.deferredDiagnosticKind = MandatoryWarningHandler.DeferredDiagnosticKind.IN_FILES;
         this.deferredDiagnosticArg = null;
      }

   }

   public void reportDeferredDiagnostic() {
      if (this.deferredDiagnosticKind != null) {
         if (this.deferredDiagnosticArg == null) {
            this.logMandatoryNote(this.deferredDiagnosticSource, this.deferredDiagnosticKind.getKey(this.prefix));
         } else {
            this.logMandatoryNote(this.deferredDiagnosticSource, this.deferredDiagnosticKind.getKey(this.prefix), this.deferredDiagnosticArg);
         }

         if (!this.verbose) {
            this.logMandatoryNote(this.deferredDiagnosticSource, this.prefix + ".recompile");
         }
      }

   }

   private static boolean equal(Object var0, Object var1) {
      return var0 != null && var1 != null ? var0.equals(var1) : var0 == var1;
   }

   private void logMandatoryWarning(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      if (this.enforceMandatory) {
         this.log.mandatoryWarning(this.lintCategory, var1, var2, var3);
      } else {
         this.log.warning(this.lintCategory, var1, var2, var3);
      }

   }

   private void logMandatoryNote(JavaFileObject var1, String var2, Object... var3) {
      if (this.enforceMandatory) {
         this.log.mandatoryNote(var1, var2, var3);
      } else {
         this.log.note(var1, var2, var3);
      }

   }

   private static enum DeferredDiagnosticKind {
      IN_FILE(".filename"),
      ADDITIONAL_IN_FILE(".filename.additional"),
      IN_FILES(".plural"),
      ADDITIONAL_IN_FILES(".plural.additional");

      private final String value;

      private DeferredDiagnosticKind(String var3) {
         this.value = var3;
      }

      String getKey(String var1) {
         return var1 + this.value;
      }
   }
}
