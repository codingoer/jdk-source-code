package com.sun.tools.javac.util;

import com.sun.tools.javac.api.DiagnosticFormatter;
import com.sun.tools.javac.api.Formattable;
import com.sun.tools.javac.file.BaseFileObject;
import com.sun.tools.javac.tree.JCTree;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;
import javax.tools.JavaFileObject.Kind;

public final class RawDiagnosticFormatter extends AbstractDiagnosticFormatter {
   public RawDiagnosticFormatter(Options var1) {
      super((JavacMessages)null, new AbstractDiagnosticFormatter.SimpleConfiguration(var1, EnumSet.of(DiagnosticFormatter.Configuration.DiagnosticPart.SUMMARY, DiagnosticFormatter.Configuration.DiagnosticPart.DETAILS, DiagnosticFormatter.Configuration.DiagnosticPart.SUBDIAGNOSTICS)));
   }

   public String formatDiagnostic(JCDiagnostic var1, Locale var2) {
      try {
         StringBuilder var3 = new StringBuilder();
         if (var1.getPosition() != -1L) {
            var3.append(this.formatSource(var1, false, (Locale)null));
            var3.append(':');
            var3.append(this.formatPosition(var1, DiagnosticFormatter.PositionKind.LINE, (Locale)null));
            var3.append(':');
            var3.append(this.formatPosition(var1, DiagnosticFormatter.PositionKind.COLUMN, (Locale)null));
            var3.append(':');
         } else if (var1.getSource() != null && var1.getSource().getKind() == Kind.CLASS) {
            var3.append(this.formatSource(var1, false, (Locale)null));
            var3.append(":-:-:");
         } else {
            var3.append('-');
         }

         var3.append(' ');
         var3.append(this.formatMessage((JCDiagnostic)var1, (Locale)null));
         if (this.displaySource(var1)) {
            var3.append("\n");
            var3.append(this.formatSourceLine(var1, 0));
         }

         return var3.toString();
      } catch (Exception var4) {
         return null;
      }
   }

   public String formatMessage(JCDiagnostic var1, Locale var2) {
      StringBuilder var3 = new StringBuilder();
      Collection var4 = this.formatArguments(var1, var2);
      var3.append(this.localize((Locale)null, var1.getCode(), var4.toArray()));
      if (var1.isMultiline() && this.getConfiguration().getVisible().contains(DiagnosticFormatter.Configuration.DiagnosticPart.SUBDIAGNOSTICS)) {
         List var5 = this.formatSubdiagnostics(var1, (Locale)null);
         if (var5.nonEmpty()) {
            String var6 = "";
            var3.append(",{");

            for(Iterator var7 = this.formatSubdiagnostics(var1, (Locale)null).iterator(); var7.hasNext(); var6 = ",") {
               String var8 = (String)var7.next();
               var3.append(var6);
               var3.append("(");
               var3.append(var8);
               var3.append(")");
            }

            var3.append('}');
         }
      }

      return var3.toString();
   }

   protected String formatArgument(JCDiagnostic var1, Object var2, Locale var3) {
      String var4;
      if (var2 instanceof Formattable) {
         var4 = var2.toString();
      } else if (var2 instanceof JCTree.JCExpression) {
         JCTree.JCExpression var5 = (JCTree.JCExpression)var2;
         var4 = "@" + var5.getStartPosition();
      } else if (var2 instanceof BaseFileObject) {
         var4 = ((BaseFileObject)var2).getShortName();
      } else {
         var4 = super.formatArgument(var1, var2, (Locale)null);
      }

      return var2 instanceof JCDiagnostic ? "(" + var4 + ")" : var4;
   }

   protected String localize(Locale var1, String var2, Object... var3) {
      StringBuilder var4 = new StringBuilder();
      var4.append(var2);
      String var5 = ": ";
      Object[] var6 = var3;
      int var7 = var3.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Object var9 = var6[var8];
         var4.append(var5);
         var4.append(var9);
         var5 = ", ";
      }

      return var4.toString();
   }

   public boolean isRaw() {
      return true;
   }
}
