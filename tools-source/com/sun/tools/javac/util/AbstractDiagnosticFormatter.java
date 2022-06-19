package com.sun.tools.javac.util;

import com.sun.tools.javac.api.DiagnosticFormatter;
import com.sun.tools.javac.api.Formattable;
import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Printer;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.file.BaseFileObject;
import com.sun.tools.javac.jvm.Profile;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.Pretty;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.tools.JavaFileObject;

public abstract class AbstractDiagnosticFormatter implements DiagnosticFormatter {
   protected JavacMessages messages;
   private SimpleConfiguration config;
   protected int depth = 0;
   private List allCaptured = List.nil();
   protected Printer printer = new Printer() {
      protected String localize(Locale var1, String var2, Object... var3) {
         return AbstractDiagnosticFormatter.this.localize(var1, var2, var3);
      }

      protected String capturedVarId(Type.CapturedType var1, Locale var2) {
         return "" + (AbstractDiagnosticFormatter.this.allCaptured.indexOf(var1) + 1);
      }

      public String visitCapturedType(Type.CapturedType var1, Locale var2) {
         if (!AbstractDiagnosticFormatter.this.allCaptured.contains(var1)) {
            AbstractDiagnosticFormatter.this.allCaptured = AbstractDiagnosticFormatter.this.allCaptured.append(var1);
         }

         return super.visitCapturedType(var1, var2);
      }
   };

   protected AbstractDiagnosticFormatter(JavacMessages var1, SimpleConfiguration var2) {
      this.messages = var1;
      this.config = var2;
   }

   public String formatKind(JCDiagnostic var1, Locale var2) {
      switch (var1.getType()) {
         case FRAGMENT:
            return "";
         case NOTE:
            return this.localize(var2, "compiler.note.note");
         case WARNING:
            return this.localize(var2, "compiler.warn.warning");
         case ERROR:
            return this.localize(var2, "compiler.err.error");
         default:
            throw new AssertionError("Unknown diagnostic type: " + var1.getType());
      }
   }

   public String format(JCDiagnostic var1, Locale var2) {
      this.allCaptured = List.nil();
      return this.formatDiagnostic(var1, var2);
   }

   protected abstract String formatDiagnostic(JCDiagnostic var1, Locale var2);

   public String formatPosition(JCDiagnostic var1, DiagnosticFormatter.PositionKind var2, Locale var3) {
      Assert.check(var1.getPosition() != -1L);
      return String.valueOf(this.getPosition(var1, var2));
   }

   private long getPosition(JCDiagnostic var1, DiagnosticFormatter.PositionKind var2) {
      switch (var2) {
         case START:
            return (long)var1.getIntStartPosition();
         case END:
            return (long)var1.getIntEndPosition();
         case LINE:
            return var1.getLineNumber();
         case COLUMN:
            return var1.getColumnNumber();
         case OFFSET:
            return (long)var1.getIntPosition();
         default:
            throw new AssertionError("Unknown diagnostic position: " + var2);
      }
   }

   public String formatSource(JCDiagnostic var1, boolean var2, Locale var3) {
      JavaFileObject var4 = var1.getSource();
      if (var4 == null) {
         throw new IllegalArgumentException();
      } else if (var2) {
         return var4.getName();
      } else {
         return var4 instanceof BaseFileObject ? ((BaseFileObject)var4).getShortName() : BaseFileObject.getSimpleName(var4);
      }
   }

   protected Collection formatArguments(JCDiagnostic var1, Locale var2) {
      ListBuffer var3 = new ListBuffer();
      Object[] var4 = var1.getArgs();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Object var7 = var4[var6];
         var3.append(this.formatArgument(var1, var7, var2));
      }

      return var3.toList();
   }

   protected String formatArgument(JCDiagnostic var1, Object var2, Locale var3) {
      if (var2 instanceof JCDiagnostic) {
         String var4 = null;
         ++this.depth;

         try {
            var4 = this.formatMessage((JCDiagnostic)var2, var3);
         } finally {
            --this.depth;
         }

         return var4;
      } else if (var2 instanceof JCTree.JCExpression) {
         return this.expr2String((JCTree.JCExpression)var2);
      } else if (var2 instanceof Iterable) {
         return this.formatIterable(var1, (Iterable)var2, var3);
      } else if (var2 instanceof Type) {
         return this.printer.visit((Type)var2, var3);
      } else if (var2 instanceof Symbol) {
         return this.printer.visit((Symbol)var2, var3);
      } else if (var2 instanceof JavaFileObject) {
         return ((JavaFileObject)var2).getName();
      } else if (var2 instanceof Profile) {
         return ((Profile)var2).name;
      } else {
         return var2 instanceof Formattable ? ((Formattable)var2).toString(var3, this.messages) : String.valueOf(var2);
      }
   }

   private String expr2String(JCTree.JCExpression var1) {
      switch (var1.getTag()) {
         case PARENS:
            return this.expr2String(((JCTree.JCParens)var1).expr);
         case LAMBDA:
         case REFERENCE:
         case CONDEXPR:
            return Pretty.toSimpleString(var1);
         default:
            Assert.error("unexpected tree kind " + var1.getKind());
            return null;
      }
   }

   protected String formatIterable(JCDiagnostic var1, Iterable var2, Locale var3) {
      StringBuilder var4 = new StringBuilder();
      String var5 = "";

      for(Iterator var6 = var2.iterator(); var6.hasNext(); var5 = ",") {
         Object var7 = var6.next();
         var4.append(var5);
         var4.append(this.formatArgument(var1, var7, var3));
      }

      return var4.toString();
   }

   protected List formatSubdiagnostics(JCDiagnostic var1, Locale var2) {
      List var3 = List.nil();
      int var4 = this.config.getMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit.DEPTH);
      if (var4 == -1 || this.depth < var4) {
         ++this.depth;

         try {
            int var5 = this.config.getMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit.LENGTH);
            int var6 = 0;

            for(Iterator var7 = var1.getSubdiagnostics().iterator(); var7.hasNext(); ++var6) {
               JCDiagnostic var8 = (JCDiagnostic)var7.next();
               if (var5 != -1 && var6 >= var5) {
                  break;
               }

               var3 = var3.append(this.formatSubdiagnostic(var1, var8, var2));
            }
         } finally {
            --this.depth;
         }
      }

      return var3;
   }

   protected String formatSubdiagnostic(JCDiagnostic var1, JCDiagnostic var2, Locale var3) {
      return this.formatMessage(var2, var3);
   }

   protected String formatSourceLine(JCDiagnostic var1, int var2) {
      StringBuilder var3 = new StringBuilder();
      DiagnosticSource var4 = var1.getDiagnosticSource();
      int var5 = var1.getIntPosition();
      if (var1.getIntPosition() == -1) {
         throw new AssertionError();
      } else {
         String var6 = var4 == null ? null : var4.getLine(var5);
         if (var6 == null) {
            return "";
         } else {
            var3.append(this.indent(var6, var2));
            int var7 = var4.getColumnNumber(var5, false);
            if (this.config.isCaretEnabled()) {
               var3.append("\n");

               for(int var8 = 0; var8 < var7 - 1; ++var8) {
                  var3.append(var6.charAt(var8) == '\t' ? "\t" : " ");
               }

               var3.append(this.indent("^", var2));
            }

            return var3.toString();
         }
      }
   }

   protected String formatLintCategory(JCDiagnostic var1, Locale var2) {
      Lint.LintCategory var3 = var1.getLintCategory();
      return var3 == null ? "" : this.localize(var2, "compiler.warn.lintOption", var3.option);
   }

   protected String localize(Locale var1, String var2, Object... var3) {
      return this.messages.getLocalizedString(var1, var2, var3);
   }

   public boolean displaySource(JCDiagnostic var1) {
      return this.config.getVisible().contains(DiagnosticFormatter.Configuration.DiagnosticPart.SOURCE) && var1.getType() != JCDiagnostic.DiagnosticType.FRAGMENT && var1.getIntPosition() != -1;
   }

   public boolean isRaw() {
      return false;
   }

   protected String indentString(int var1) {
      String var2 = "                        ";
      if (var1 <= var2.length()) {
         return var2.substring(0, var1);
      } else {
         StringBuilder var3 = new StringBuilder();

         for(int var4 = 0; var4 < var1; ++var4) {
            var3.append(" ");
         }

         return var3.toString();
      }
   }

   protected String indent(String var1, int var2) {
      String var3 = this.indentString(var2);
      StringBuilder var4 = new StringBuilder();
      String var5 = "";
      String[] var6 = var1.split("\n");
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String var9 = var6[var8];
         var4.append(var5);
         var4.append(var3 + var9);
         var5 = "\n";
      }

      return var4.toString();
   }

   public SimpleConfiguration getConfiguration() {
      return this.config;
   }

   public Printer getPrinter() {
      return this.printer;
   }

   public void setPrinter(Printer var1) {
      this.printer = var1;
   }

   public static class SimpleConfiguration implements DiagnosticFormatter.Configuration {
      protected Map multilineLimits;
      protected EnumSet visibleParts;
      protected boolean caretEnabled;

      public SimpleConfiguration(Set var1) {
         this.multilineLimits = new HashMap();
         this.setVisible(var1);
         this.setMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit.DEPTH, -1);
         this.setMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit.LENGTH, -1);
         this.setCaretEnabled(true);
      }

      public SimpleConfiguration(Options var1, Set var2) {
         this(var2);
         String var3 = null;
         if ((var3 = var1.get("showSource")) != null) {
            if (var3.equals("true")) {
               this.setVisiblePart(DiagnosticFormatter.Configuration.DiagnosticPart.SOURCE, true);
            } else if (var3.equals("false")) {
               this.setVisiblePart(DiagnosticFormatter.Configuration.DiagnosticPart.SOURCE, false);
            }
         }

         String var4 = var1.get("diags");
         java.util.List var5;
         if (var4 != null) {
            var5 = Arrays.asList(var4.split(","));
            if (var5.contains("short")) {
               this.setVisiblePart(DiagnosticFormatter.Configuration.DiagnosticPart.DETAILS, false);
               this.setVisiblePart(DiagnosticFormatter.Configuration.DiagnosticPart.SUBDIAGNOSTICS, false);
            }

            if (var5.contains("source")) {
               this.setVisiblePart(DiagnosticFormatter.Configuration.DiagnosticPart.SOURCE, true);
            }

            if (var5.contains("-source")) {
               this.setVisiblePart(DiagnosticFormatter.Configuration.DiagnosticPart.SOURCE, false);
            }
         }

         var5 = null;
         String var6;
         String var10;
         if ((var10 = var1.get("multilinePolicy")) != null) {
            if (var10.equals("disabled")) {
               this.setVisiblePart(DiagnosticFormatter.Configuration.DiagnosticPart.SUBDIAGNOSTICS, false);
            } else if (var10.startsWith("limit:")) {
               var6 = var10.substring("limit:".length());
               String[] var7 = var6.split(":");

               try {
                  switch (var7.length) {
                     case 2:
                        if (!var7[1].equals("*")) {
                           this.setMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit.DEPTH, Integer.parseInt(var7[1]));
                        }
                     case 1:
                        if (!var7[0].equals("*")) {
                           this.setMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit.LENGTH, Integer.parseInt(var7[0]));
                        }
                  }
               } catch (NumberFormatException var9) {
                  this.setMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit.DEPTH, -1);
                  this.setMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit.LENGTH, -1);
               }
            }
         }

         var6 = null;
         if ((var6 = var1.get("showCaret")) != null && var6.equals("false")) {
            this.setCaretEnabled(false);
         } else {
            this.setCaretEnabled(true);
         }

      }

      public int getMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit var1) {
         return (Integer)this.multilineLimits.get(var1);
      }

      public EnumSet getVisible() {
         return EnumSet.copyOf(this.visibleParts);
      }

      public void setMultilineLimit(DiagnosticFormatter.Configuration.MultilineLimit var1, int var2) {
         this.multilineLimits.put(var1, var2 < -1 ? -1 : var2);
      }

      public void setVisible(Set var1) {
         this.visibleParts = EnumSet.copyOf(var1);
      }

      public void setVisiblePart(DiagnosticFormatter.Configuration.DiagnosticPart var1, boolean var2) {
         if (var2) {
            this.visibleParts.add(var1);
         } else {
            this.visibleParts.remove(var1);
         }

      }

      public void setCaretEnabled(boolean var1) {
         this.caretEnabled = var1;
      }

      public boolean isCaretEnabled() {
         return this.caretEnabled;
      }
   }
}
