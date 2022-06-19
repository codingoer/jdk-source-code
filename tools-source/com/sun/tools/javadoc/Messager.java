package com.sun.tools.javadoc;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.SourcePosition;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.DiagnosticSource;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.JavacMessages;
import com.sun.tools.javac.util.Log;
import java.io.PrintWriter;
import java.util.Locale;

public class Messager extends Log implements DocErrorReporter {
   public static final SourcePosition NOPOS = null;
   final String programName;
   private Locale locale;
   private final JavacMessages messages;
   private final JCDiagnostic.Factory javadocDiags;
   static final PrintWriter defaultErrWriter;
   static final PrintWriter defaultWarnWriter;
   static final PrintWriter defaultNoticeWriter;

   public static Messager instance0(Context var0) {
      Log var1 = (Log)var0.get(logKey);
      if (var1 != null && var1 instanceof Messager) {
         return (Messager)var1;
      } else {
         throw new InternalError("no messager instance!");
      }
   }

   public static void preRegister(Context var0, final String var1) {
      var0.put(logKey, new Context.Factory() {
         public Log make(Context var1x) {
            return new Messager(var1x, var1);
         }
      });
   }

   public static void preRegister(Context var0, final String var1, final PrintWriter var2, final PrintWriter var3, final PrintWriter var4) {
      var0.put(logKey, new Context.Factory() {
         public Log make(Context var1x) {
            return new Messager(var1x, var1, var2, var3, var4);
         }
      });
   }

   protected Messager(Context var1, String var2) {
      this(var1, var2, defaultErrWriter, defaultWarnWriter, defaultNoticeWriter);
   }

   protected Messager(Context var1, String var2, PrintWriter var3, PrintWriter var4, PrintWriter var5) {
      super(var1, var3, var4, var5);
      this.messages = JavacMessages.instance(var1);
      this.messages.add("com.sun.tools.javadoc.resources.javadoc");
      this.javadocDiags = new JCDiagnostic.Factory(this.messages, "javadoc");
      this.programName = var2;
   }

   public void setLocale(Locale var1) {
      this.locale = var1;
   }

   String getText(String var1, Object... var2) {
      return this.messages.getLocalizedString(this.locale, var1, var2);
   }

   public void printError(String var1) {
      this.printError((SourcePosition)null, var1);
   }

   public void printError(SourcePosition var1, String var2) {
      if (this.diagListener != null) {
         this.report(JCDiagnostic.DiagnosticType.ERROR, var1, var2);
      } else {
         if (this.nerrors < this.MaxErrors) {
            String var3 = var1 == null ? this.programName : var1.toString();
            this.errWriter.println(var3 + ": " + this.getText("javadoc.error") + " - " + var2);
            this.errWriter.flush();
            this.prompt();
            ++this.nerrors;
         }

      }
   }

   public void printWarning(String var1) {
      this.printWarning((SourcePosition)null, var1);
   }

   public void printWarning(SourcePosition var1, String var2) {
      if (this.diagListener != null) {
         this.report(JCDiagnostic.DiagnosticType.WARNING, var1, var2);
      } else {
         if (this.nwarnings < this.MaxWarnings) {
            String var3 = var1 == null ? this.programName : var1.toString();
            this.warnWriter.println(var3 + ": " + this.getText("javadoc.warning") + " - " + var2);
            this.warnWriter.flush();
            ++this.nwarnings;
         }

      }
   }

   public void printNotice(String var1) {
      this.printNotice((SourcePosition)null, var1);
   }

   public void printNotice(SourcePosition var1, String var2) {
      if (this.diagListener != null) {
         this.report(JCDiagnostic.DiagnosticType.NOTE, var1, var2);
      } else {
         if (var1 == null) {
            this.noticeWriter.println(var2);
         } else {
            this.noticeWriter.println(var1 + ": " + var2);
         }

         this.noticeWriter.flush();
      }
   }

   public void error(SourcePosition var1, String var2, Object... var3) {
      this.printError(var1, this.getText(var2, var3));
   }

   public void warning(SourcePosition var1, String var2, Object... var3) {
      this.printWarning(var1, this.getText(var2, var3));
   }

   public void notice(String var1, Object... var2) {
      this.printNotice(this.getText(var1, var2));
   }

   public int nerrors() {
      return this.nerrors;
   }

   public int nwarnings() {
      return this.nwarnings;
   }

   public void exitNotice() {
      if (this.nerrors > 0) {
         this.notice(this.nerrors > 1 ? "main.errors" : "main.error", "" + this.nerrors);
      }

      if (this.nwarnings > 0) {
         this.notice(this.nwarnings > 1 ? "main.warnings" : "main.warning", "" + this.nwarnings);
      }

   }

   public void exit() {
      throw new ExitJavadoc();
   }

   private void report(JCDiagnostic.DiagnosticType var1, SourcePosition var2, String var3) {
      switch (var1) {
         case ERROR:
         case WARNING:
            Object var4 = var2 == null ? this.programName : var2;
            this.report(this.javadocDiags.create(var1, (DiagnosticSource)null, (JCDiagnostic.DiagnosticPosition)null, "msg", var4, var3));
            break;
         case NOTE:
            String var5 = var2 == null ? "msg" : "pos.msg";
            this.report(this.javadocDiags.create(var1, (DiagnosticSource)null, (JCDiagnostic.DiagnosticPosition)null, var5, var2, var3));
            break;
         default:
            throw new IllegalArgumentException(var1.toString());
      }

   }

   static {
      defaultErrWriter = new PrintWriter(System.err);
      defaultWarnWriter = new PrintWriter(System.err);
      defaultNoticeWriter = new PrintWriter(System.out);
   }

   public class ExitJavadoc extends Error {
      private static final long serialVersionUID = 0L;
   }
}
