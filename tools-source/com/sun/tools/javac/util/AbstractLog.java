package com.sun.tools.javac.util;

import com.sun.tools.javac.code.Lint;
import java.util.HashMap;
import java.util.Map;
import javax.tools.JavaFileObject;

public abstract class AbstractLog {
   protected JCDiagnostic.Factory diags;
   protected DiagnosticSource source;
   protected Map sourceMap;

   AbstractLog(JCDiagnostic.Factory var1) {
      this.diags = var1;
      this.sourceMap = new HashMap();
   }

   public JavaFileObject useSource(JavaFileObject var1) {
      JavaFileObject var2 = this.source == null ? null : this.source.getFile();
      this.source = this.getSource(var1);
      return var2;
   }

   protected DiagnosticSource getSource(JavaFileObject var1) {
      if (var1 == null) {
         return DiagnosticSource.NO_SOURCE;
      } else {
         DiagnosticSource var2 = (DiagnosticSource)this.sourceMap.get(var1);
         if (var2 == null) {
            var2 = new DiagnosticSource(var1, this);
            this.sourceMap.put(var1, var2);
         }

         return var2;
      }
   }

   public DiagnosticSource currentSource() {
      return this.source;
   }

   public void error(String var1, Object... var2) {
      this.report(this.diags.error(this.source, (JCDiagnostic.DiagnosticPosition)null, var1, var2));
   }

   public void error(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      this.report(this.diags.error(this.source, var1, var2, var3));
   }

   public void error(JCDiagnostic.DiagnosticFlag var1, JCDiagnostic.DiagnosticPosition var2, String var3, Object... var4) {
      JCDiagnostic var5 = this.diags.error(this.source, var2, var3, var4);
      var5.setFlag(var1);
      this.report(var5);
   }

   public void error(int var1, String var2, Object... var3) {
      this.report(this.diags.error(this.source, this.wrap(var1), var2, var3));
   }

   public void error(JCDiagnostic.DiagnosticFlag var1, int var2, String var3, Object... var4) {
      JCDiagnostic var5 = this.diags.error(this.source, this.wrap(var2), var3, var4);
      var5.setFlag(var1);
      this.report(var5);
   }

   public void warning(String var1, Object... var2) {
      this.report(this.diags.warning(this.source, (JCDiagnostic.DiagnosticPosition)null, var1, var2));
   }

   public void warning(Lint.LintCategory var1, String var2, Object... var3) {
      this.report(this.diags.warning(var1, var2, var3));
   }

   public void warning(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      this.report(this.diags.warning(this.source, var1, var2, var3));
   }

   public void warning(Lint.LintCategory var1, JCDiagnostic.DiagnosticPosition var2, String var3, Object... var4) {
      this.report(this.diags.warning(var1, this.source, var2, var3, var4));
   }

   public void warning(int var1, String var2, Object... var3) {
      this.report(this.diags.warning(this.source, this.wrap(var1), var2, var3));
   }

   public void mandatoryWarning(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      this.report(this.diags.mandatoryWarning(this.source, var1, var2, var3));
   }

   public void mandatoryWarning(Lint.LintCategory var1, JCDiagnostic.DiagnosticPosition var2, String var3, Object... var4) {
      this.report(this.diags.mandatoryWarning(var1, this.source, var2, var3, var4));
   }

   public void note(String var1, Object... var2) {
      this.report(this.diags.note(this.source, (JCDiagnostic.DiagnosticPosition)null, var1, var2));
   }

   public void note(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      this.report(this.diags.note(this.source, var1, var2, var3));
   }

   public void note(int var1, String var2, Object... var3) {
      this.report(this.diags.note(this.source, this.wrap(var1), var2, var3));
   }

   public void note(JavaFileObject var1, String var2, Object... var3) {
      this.report(this.diags.note(this.getSource(var1), (JCDiagnostic.DiagnosticPosition)null, var2, var3));
   }

   public void mandatoryNote(JavaFileObject var1, String var2, Object... var3) {
      this.report(this.diags.mandatoryNote(this.getSource(var1), var2, var3));
   }

   protected abstract void report(JCDiagnostic var1);

   protected abstract void directError(String var1, Object... var2);

   private JCDiagnostic.DiagnosticPosition wrap(int var1) {
      return var1 == -1 ? null : new JCDiagnostic.SimpleDiagnosticPosition(var1);
   }
}
