package com.sun.tools.javac.util;

import com.sun.tools.javac.api.DiagnosticFormatter;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import javax.tools.JavaFileObject.Kind;

public class BasicDiagnosticFormatter extends AbstractDiagnosticFormatter {
   public BasicDiagnosticFormatter(Options var1, JavacMessages var2) {
      super(var2, new BasicConfiguration(var1));
   }

   public BasicDiagnosticFormatter(JavacMessages var1) {
      super(var1, new BasicConfiguration());
   }

   public String formatDiagnostic(JCDiagnostic var1, Locale var2) {
      if (var2 == null) {
         var2 = this.messages.getCurrentLocale();
      }

      String var3 = this.selectFormat(var1);
      StringBuilder var4 = new StringBuilder();

      for(int var5 = 0; var5 < var3.length(); ++var5) {
         char var6 = var3.charAt(var5);
         boolean var7 = false;
         if (var6 == '%' && var5 < var3.length() - 1) {
            var7 = true;
            ++var5;
            var6 = var3.charAt(var5);
         }

         var4.append(var7 ? this.formatMeta(var6, var1, var2) : String.valueOf(var6));
      }

      if (this.depth == 0) {
         return this.addSourceLineIfNeeded(var1, var4.toString());
      } else {
         return var4.toString();
      }
   }

   public String formatMessage(JCDiagnostic var1, Locale var2) {
      int var3 = 0;
      StringBuilder var4 = new StringBuilder();
      Collection var5 = this.formatArguments(var1, var2);
      String var6 = this.localize(var2, var1.getCode(), var5.toArray());
      String[] var7 = var6.split("\n");
      if (this.getConfiguration().getVisible().contains(DiagnosticFormatter.Configuration.DiagnosticPart.SUMMARY)) {
         var3 += this.getConfiguration().getIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.SUMMARY);
         var4.append(this.indent(var7[0], var3));
      }

      if (var7.length > 1 && this.getConfiguration().getVisible().contains(DiagnosticFormatter.Configuration.DiagnosticPart.DETAILS)) {
         var3 += this.getConfiguration().getIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.DETAILS);

         for(int var8 = 1; var8 < var7.length; ++var8) {
            var4.append("\n" + this.indent(var7[var8], var3));
         }
      }

      if (var1.isMultiline() && this.getConfiguration().getVisible().contains(DiagnosticFormatter.Configuration.DiagnosticPart.SUBDIAGNOSTICS)) {
         var3 += this.getConfiguration().getIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.SUBDIAGNOSTICS);
         Iterator var10 = this.formatSubdiagnostics(var1, var2).iterator();

         while(var10.hasNext()) {
            String var9 = (String)var10.next();
            var4.append("\n" + this.indent(var9, var3));
         }
      }

      return var4.toString();
   }

   protected String addSourceLineIfNeeded(JCDiagnostic var1, String var2) {
      if (!this.displaySource(var1)) {
         return var2;
      } else {
         BasicConfiguration var3 = this.getConfiguration();
         int var4 = var3.getIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.SOURCE);
         String var5 = "\n" + this.formatSourceLine(var1, var4);
         boolean var6 = var2.indexOf("\n") == -1;
         return !var6 && this.getConfiguration().getSourcePosition() != BasicDiagnosticFormatter.BasicConfiguration.SourcePosition.BOTTOM ? var2.replaceFirst("\n", Matcher.quoteReplacement(var5) + "\n") : var2 + var5;
      }
   }

   protected String formatMeta(char var1, JCDiagnostic var2, Locale var3) {
      switch (var1) {
         case '%':
            return "%";
         case 'L':
            return this.formatLintCategory(var2, var3);
         case '_':
            return " ";
         case 'b':
            return this.formatSource(var2, false, var3);
         case 'c':
            return this.formatPosition(var2, DiagnosticFormatter.PositionKind.COLUMN, var3);
         case 'e':
            return this.formatPosition(var2, DiagnosticFormatter.PositionKind.END, var3);
         case 'f':
            return this.formatSource(var2, true, var3);
         case 'l':
            return this.formatPosition(var2, DiagnosticFormatter.PositionKind.LINE, var3);
         case 'm':
            return this.formatMessage(var2, var3);
         case 'o':
            return this.formatPosition(var2, DiagnosticFormatter.PositionKind.OFFSET, var3);
         case 'p':
            return this.formatKind(var2, var3);
         case 's':
            return this.formatPosition(var2, DiagnosticFormatter.PositionKind.START, var3);
         case 't':
            boolean var4;
            switch (var2.getType()) {
               case FRAGMENT:
                  var4 = false;
                  break;
               case ERROR:
                  var4 = var2.getIntPosition() == -1;
                  break;
               default:
                  var4 = true;
            }

            if (var4) {
               return this.formatKind(var2, var3);
            }

            return "";
         default:
            return String.valueOf(var1);
      }
   }

   private String selectFormat(JCDiagnostic var1) {
      DiagnosticSource var2 = var1.getDiagnosticSource();
      String var3 = this.getConfiguration().getFormat(BasicDiagnosticFormatter.BasicConfiguration.BasicFormatKind.DEFAULT_NO_POS_FORMAT);
      if (var2 != null && var2 != DiagnosticSource.NO_SOURCE) {
         if (var1.getIntPosition() != -1) {
            var3 = this.getConfiguration().getFormat(BasicDiagnosticFormatter.BasicConfiguration.BasicFormatKind.DEFAULT_POS_FORMAT);
         } else if (var2.getFile() != null && var2.getFile().getKind() == Kind.CLASS) {
            var3 = this.getConfiguration().getFormat(BasicDiagnosticFormatter.BasicConfiguration.BasicFormatKind.DEFAULT_CLASS_FORMAT);
         }
      }

      return var3;
   }

   public BasicConfiguration getConfiguration() {
      return (BasicConfiguration)super.getConfiguration();
   }

   public static class BasicConfiguration extends AbstractDiagnosticFormatter.SimpleConfiguration {
      protected Map indentationLevels;
      protected Map availableFormats;
      protected SourcePosition sourcePosition;

      public BasicConfiguration(Options var1) {
         super(var1, EnumSet.of(DiagnosticFormatter.Configuration.DiagnosticPart.SUMMARY, DiagnosticFormatter.Configuration.DiagnosticPart.DETAILS, DiagnosticFormatter.Configuration.DiagnosticPart.SUBDIAGNOSTICS, DiagnosticFormatter.Configuration.DiagnosticPart.SOURCE));
         this.initFormat();
         this.initIndentation();
         if (var1.isSet("oldDiags")) {
            this.initOldFormat();
         }

         String var2 = var1.get("diagsFormat");
         if (var2 != null) {
            if (var2.equals("OLD")) {
               this.initOldFormat();
            } else {
               this.initFormats(var2);
            }
         }

         String var3 = null;
         if ((var3 = var1.get("sourcePosition")) != null && var3.equals("bottom")) {
            this.setSourcePosition(BasicDiagnosticFormatter.BasicConfiguration.SourcePosition.BOTTOM);
         } else {
            this.setSourcePosition(BasicDiagnosticFormatter.BasicConfiguration.SourcePosition.AFTER_SUMMARY);
         }

         String var4 = var1.get("diagsIndentation");
         if (var4 != null) {
            String[] var5 = var4.split("\\|");

            try {
               switch (var5.length) {
                  case 5:
                     this.setIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.JLS, Integer.parseInt(var5[4]));
                  case 4:
                     this.setIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.SUBDIAGNOSTICS, Integer.parseInt(var5[3]));
                  case 3:
                     this.setIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.SOURCE, Integer.parseInt(var5[2]));
                  case 2:
                     this.setIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.DETAILS, Integer.parseInt(var5[1]));
                  default:
                     this.setIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.SUMMARY, Integer.parseInt(var5[0]));
               }
            } catch (NumberFormatException var7) {
               this.initIndentation();
            }
         }

      }

      public BasicConfiguration() {
         super(EnumSet.of(DiagnosticFormatter.Configuration.DiagnosticPart.SUMMARY, DiagnosticFormatter.Configuration.DiagnosticPart.DETAILS, DiagnosticFormatter.Configuration.DiagnosticPart.SUBDIAGNOSTICS, DiagnosticFormatter.Configuration.DiagnosticPart.SOURCE));
         this.initFormat();
         this.initIndentation();
      }

      private void initFormat() {
         this.initFormats("%f:%l:%_%p%L%m", "%p%L%m", "%f:%_%p%L%m");
      }

      private void initOldFormat() {
         this.initFormats("%f:%l:%_%t%L%m", "%p%L%m", "%f:%_%t%L%m");
      }

      private void initFormats(String var1, String var2, String var3) {
         this.availableFormats = new EnumMap(BasicFormatKind.class);
         this.setFormat(BasicDiagnosticFormatter.BasicConfiguration.BasicFormatKind.DEFAULT_POS_FORMAT, var1);
         this.setFormat(BasicDiagnosticFormatter.BasicConfiguration.BasicFormatKind.DEFAULT_NO_POS_FORMAT, var2);
         this.setFormat(BasicDiagnosticFormatter.BasicConfiguration.BasicFormatKind.DEFAULT_CLASS_FORMAT, var3);
      }

      private void initFormats(String var1) {
         String[] var2 = var1.split("\\|");
         switch (var2.length) {
            case 3:
               this.setFormat(BasicDiagnosticFormatter.BasicConfiguration.BasicFormatKind.DEFAULT_CLASS_FORMAT, var2[2]);
            case 2:
               this.setFormat(BasicDiagnosticFormatter.BasicConfiguration.BasicFormatKind.DEFAULT_NO_POS_FORMAT, var2[1]);
            default:
               this.setFormat(BasicDiagnosticFormatter.BasicConfiguration.BasicFormatKind.DEFAULT_POS_FORMAT, var2[0]);
         }
      }

      private void initIndentation() {
         this.indentationLevels = new HashMap();
         this.setIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.SUMMARY, 0);
         this.setIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.DETAILS, 2);
         this.setIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.SUBDIAGNOSTICS, 4);
         this.setIndentation(DiagnosticFormatter.Configuration.DiagnosticPart.SOURCE, 0);
      }

      public int getIndentation(DiagnosticFormatter.Configuration.DiagnosticPart var1) {
         return (Integer)this.indentationLevels.get(var1);
      }

      public void setIndentation(DiagnosticFormatter.Configuration.DiagnosticPart var1, int var2) {
         this.indentationLevels.put(var1, var2);
      }

      public void setSourcePosition(SourcePosition var1) {
         this.sourcePosition = var1;
      }

      public SourcePosition getSourcePosition() {
         return this.sourcePosition;
      }

      public void setFormat(BasicFormatKind var1, String var2) {
         this.availableFormats.put(var1, var2);
      }

      public String getFormat(BasicFormatKind var1) {
         return (String)this.availableFormats.get(var1);
      }

      public static enum BasicFormatKind {
         DEFAULT_POS_FORMAT,
         DEFAULT_NO_POS_FORMAT,
         DEFAULT_CLASS_FORMAT;
      }

      public static enum SourcePosition {
         BOTTOM,
         AFTER_SUMMARY;
      }
   }
}
