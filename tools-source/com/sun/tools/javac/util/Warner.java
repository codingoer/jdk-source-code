package com.sun.tools.javac.util;

import com.sun.tools.javac.code.Lint;
import java.util.EnumSet;

public class Warner {
   private JCDiagnostic.DiagnosticPosition pos;
   protected boolean warned;
   private EnumSet nonSilentLintSet;
   private EnumSet silentLintSet;

   public JCDiagnostic.DiagnosticPosition pos() {
      return this.pos;
   }

   public void warn(Lint.LintCategory var1) {
      this.nonSilentLintSet.add(var1);
   }

   public void silentWarn(Lint.LintCategory var1) {
      this.silentLintSet.add(var1);
   }

   public Warner(JCDiagnostic.DiagnosticPosition var1) {
      this.pos = null;
      this.warned = false;
      this.nonSilentLintSet = EnumSet.noneOf(Lint.LintCategory.class);
      this.silentLintSet = EnumSet.noneOf(Lint.LintCategory.class);
      this.pos = var1;
   }

   public boolean hasSilentLint(Lint.LintCategory var1) {
      return this.silentLintSet.contains(var1);
   }

   public boolean hasNonSilentLint(Lint.LintCategory var1) {
      return this.nonSilentLintSet.contains(var1);
   }

   public boolean hasLint(Lint.LintCategory var1) {
      return this.hasSilentLint(var1) || this.hasNonSilentLint(var1);
   }

   public void clear() {
      this.nonSilentLintSet.clear();
      this.silentLintSet.clear();
      this.warned = false;
   }

   public Warner() {
      this((JCDiagnostic.DiagnosticPosition)null);
   }
}
