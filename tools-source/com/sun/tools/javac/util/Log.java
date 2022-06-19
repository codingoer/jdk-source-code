package com.sun.tools.javac.util;

import com.sun.tools.javac.api.DiagnosticFormatter;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.tree.EndPosTable;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class Log extends AbstractLog {
   public static final Context.Key logKey = new Context.Key();
   public static final Context.Key outKey = new Context.Key();
   protected PrintWriter errWriter;
   protected PrintWriter warnWriter;
   protected PrintWriter noticeWriter;
   protected int MaxErrors;
   protected int MaxWarnings;
   public boolean promptOnError;
   public boolean emitWarnings;
   public boolean suppressNotes;
   public boolean dumpOnError;
   public boolean multipleErrors;
   protected DiagnosticListener diagListener;
   private DiagnosticFormatter diagFormatter;
   public Set expectDiagKeys;
   public boolean compressedOutput;
   private JavacMessages messages;
   private DiagnosticHandler diagnosticHandler;
   public int nerrors;
   public int nwarnings;
   private Set recorded;
   private static boolean useRawMessages = false;

   protected Log(Context var1, PrintWriter var2, PrintWriter var3, PrintWriter var4) {
      super(JCDiagnostic.Factory.instance(var1));
      this.nerrors = 0;
      this.nwarnings = 0;
      this.recorded = new HashSet();
      var1.put((Context.Key)logKey, (Object)this);
      this.errWriter = var2;
      this.warnWriter = var3;
      this.noticeWriter = var4;
      DiagnosticListener var5 = (DiagnosticListener)var1.get(DiagnosticListener.class);
      this.diagListener = var5;
      this.diagnosticHandler = new DefaultDiagnosticHandler();
      this.messages = JavacMessages.instance(var1);
      this.messages.add("com.sun.tools.javac.resources.javac");
      final Options var6 = Options.instance(var1);
      this.initOptions(var6);
      var6.addListener(new Runnable() {
         public void run() {
            Log.this.initOptions(var6);
         }
      });
   }

   private void initOptions(Options var1) {
      this.dumpOnError = var1.isSet(Option.DOE);
      this.promptOnError = var1.isSet(Option.PROMPT);
      this.emitWarnings = var1.isUnset(Option.XLINT_CUSTOM, "none");
      this.suppressNotes = var1.isSet("suppressNotes");
      this.MaxErrors = this.getIntOption(var1, Option.XMAXERRS, this.getDefaultMaxErrors());
      this.MaxWarnings = this.getIntOption(var1, Option.XMAXWARNS, this.getDefaultMaxWarnings());
      boolean var2 = var1.isSet("rawDiagnostics");
      this.diagFormatter = (DiagnosticFormatter)(var2 ? new RawDiagnosticFormatter(var1) : new BasicDiagnosticFormatter(var1, this.messages));
      String var3 = var1.get("expectKeys");
      if (var3 != null) {
         this.expectDiagKeys = new HashSet(Arrays.asList(var3.split(", *")));
      }

   }

   private int getIntOption(Options var1, Option var2, int var3) {
      String var4 = var1.get(var2);

      try {
         if (var4 != null) {
            int var5 = Integer.parseInt(var4);
            return var5 <= 0 ? Integer.MAX_VALUE : var5;
         }
      } catch (NumberFormatException var6) {
      }

      return var3;
   }

   protected int getDefaultMaxErrors() {
      return 100;
   }

   protected int getDefaultMaxWarnings() {
      return 100;
   }

   static PrintWriter defaultWriter(Context var0) {
      PrintWriter var1 = (PrintWriter)var0.get(outKey);
      if (var1 == null) {
         var0.put((Context.Key)outKey, (Object)(var1 = new PrintWriter(System.err)));
      }

      return var1;
   }

   protected Log(Context var1) {
      this(var1, defaultWriter(var1));
   }

   protected Log(Context var1, PrintWriter var2) {
      this(var1, var2, var2, var2);
   }

   public static Log instance(Context var0) {
      Log var1 = (Log)var0.get(logKey);
      if (var1 == null) {
         var1 = new Log(var0);
      }

      return var1;
   }

   public boolean hasDiagnosticListener() {
      return this.diagListener != null;
   }

   public void setEndPosTable(JavaFileObject var1, EndPosTable var2) {
      var1.getClass();
      this.getSource(var1).setEndPosTable(var2);
   }

   public JavaFileObject currentSourceFile() {
      return this.source == null ? null : this.source.getFile();
   }

   public DiagnosticFormatter getDiagnosticFormatter() {
      return this.diagFormatter;
   }

   public void setDiagnosticFormatter(DiagnosticFormatter var1) {
      this.diagFormatter = var1;
   }

   public PrintWriter getWriter(WriterKind var1) {
      switch (var1) {
         case NOTICE:
            return this.noticeWriter;
         case WARNING:
            return this.warnWriter;
         case ERROR:
            return this.errWriter;
         default:
            throw new IllegalArgumentException();
      }
   }

   public void setWriter(WriterKind var1, PrintWriter var2) {
      var2.getClass();
      switch (var1) {
         case NOTICE:
            this.noticeWriter = var2;
            break;
         case WARNING:
            this.warnWriter = var2;
            break;
         case ERROR:
            this.errWriter = var2;
            break;
         default:
            throw new IllegalArgumentException();
      }

   }

   public void setWriters(PrintWriter var1) {
      var1.getClass();
      this.noticeWriter = this.warnWriter = this.errWriter = var1;
   }

   public void initRound(Log var1) {
      this.noticeWriter = var1.noticeWriter;
      this.warnWriter = var1.warnWriter;
      this.errWriter = var1.errWriter;
      this.sourceMap = var1.sourceMap;
      this.recorded = var1.recorded;
      this.nerrors = var1.nerrors;
      this.nwarnings = var1.nwarnings;
   }

   public void popDiagnosticHandler(DiagnosticHandler var1) {
      Assert.check(this.diagnosticHandler == var1);
      this.diagnosticHandler = var1.prev;
   }

   public void flush() {
      this.errWriter.flush();
      this.warnWriter.flush();
      this.noticeWriter.flush();
   }

   public void flush(WriterKind var1) {
      this.getWriter(var1).flush();
   }

   protected boolean shouldReport(JavaFileObject var1, int var2) {
      if (!this.multipleErrors && var1 != null) {
         Pair var3 = new Pair(var1, var2);
         boolean var4 = !this.recorded.contains(var3);
         if (var4) {
            this.recorded.add(var3);
         }

         return var4;
      } else {
         return true;
      }
   }

   public void prompt() {
      if (this.promptOnError) {
         System.err.println(this.localize("resume.abort"));

         try {
            while(true) {
               switch (System.in.read()) {
                  case 65:
                  case 97:
                     System.exit(-1);
                     return;
                  case 82:
                  case 114:
                     return;
                  case 88:
                  case 120:
                     throw new AssertionError("user abort");
               }
            }
         } catch (IOException var2) {
         }
      }

   }

   private void printErrLine(int var1, PrintWriter var2) {
      String var3 = this.source == null ? null : this.source.getLine(var1);
      if (var3 != null) {
         int var4 = this.source.getColumnNumber(var1, false);
         printRawLines(var2, var3);

         for(int var5 = 0; var5 < var4 - 1; ++var5) {
            var2.print(var3.charAt(var5) == '\t' ? "\t" : " ");
         }

         var2.println("^");
         var2.flush();
      }
   }

   public void printNewline() {
      this.noticeWriter.println();
   }

   public void printNewline(WriterKind var1) {
      this.getWriter(var1).println();
   }

   public void printLines(String var1, Object... var2) {
      printRawLines(this.noticeWriter, this.localize(var1, var2));
   }

   public void printLines(PrefixKind var1, String var2, Object... var3) {
      printRawLines(this.noticeWriter, this.localize(var1, var2, var3));
   }

   public void printLines(WriterKind var1, String var2, Object... var3) {
      printRawLines(this.getWriter(var1), this.localize(var2, var3));
   }

   public void printLines(WriterKind var1, PrefixKind var2, String var3, Object... var4) {
      printRawLines(this.getWriter(var1), this.localize(var2, var3, var4));
   }

   public void printRawLines(String var1) {
      printRawLines(this.noticeWriter, var1);
   }

   public void printRawLines(WriterKind var1, String var2) {
      printRawLines(this.getWriter(var1), var2);
   }

   public static void printRawLines(PrintWriter var0, String var1) {
      int var2;
      while((var2 = var1.indexOf(10)) != -1) {
         var0.println(var1.substring(0, var2));
         var1 = var1.substring(var2 + 1);
      }

      if (var1.length() != 0) {
         var0.println(var1);
      }

   }

   public void printVerbose(String var1, Object... var2) {
      printRawLines(this.noticeWriter, this.localize("verbose." + var1, var2));
   }

   protected void directError(String var1, Object... var2) {
      printRawLines(this.errWriter, this.localize(var1, var2));
      this.errWriter.flush();
   }

   public void strictWarning(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      this.writeDiagnostic(this.diags.warning(this.source, var1, var2, var3));
      ++this.nwarnings;
   }

   public void report(JCDiagnostic var1) {
      this.diagnosticHandler.report(var1);
   }

   protected void writeDiagnostic(JCDiagnostic var1) {
      if (this.diagListener != null) {
         this.diagListener.report(var1);
      } else {
         PrintWriter var2 = this.getWriterForDiagnosticType(var1.getType());
         printRawLines(var2, this.diagFormatter.format(var1, this.messages.getCurrentLocale()));
         if (this.promptOnError) {
            switch (var1.getType()) {
               case WARNING:
               case ERROR:
                  this.prompt();
            }
         }

         if (this.dumpOnError) {
            (new RuntimeException()).printStackTrace(var2);
         }

         var2.flush();
      }
   }

   /** @deprecated */
   @Deprecated
   protected PrintWriter getWriterForDiagnosticType(JCDiagnostic.DiagnosticType var1) {
      switch (var1) {
         case FRAGMENT:
            throw new IllegalArgumentException();
         case NOTE:
            return this.noticeWriter;
         case WARNING:
            return this.warnWriter;
         case ERROR:
            return this.errWriter;
         default:
            throw new Error();
      }
   }

   public static String getLocalizedString(String var0, Object... var1) {
      return JavacMessages.getDefaultLocalizedString(Log.PrefixKind.COMPILER_MISC.key(var0), var1);
   }

   public String localize(String var1, Object... var2) {
      return this.localize(Log.PrefixKind.COMPILER_MISC, var1, var2);
   }

   public String localize(PrefixKind var1, String var2, Object... var3) {
      return useRawMessages ? var1.key(var2) : this.messages.getLocalizedString(var1.key(var2), var3);
   }

   private void printRawError(int var1, String var2) {
      if (this.source != null && var1 != -1) {
         int var3 = this.source.getLineNumber(var1);
         JavaFileObject var4 = this.source.getFile();
         if (var4 != null) {
            printRawLines(this.errWriter, var4.getName() + ":" + var3 + ": " + var2);
         }

         this.printErrLine(var1, this.errWriter);
      } else {
         printRawLines(this.errWriter, "error: " + var2);
      }

      this.errWriter.flush();
   }

   public void rawError(int var1, String var2) {
      if (this.nerrors < this.MaxErrors && this.shouldReport(this.currentSourceFile(), var1)) {
         this.printRawError(var1, var2);
         this.prompt();
         ++this.nerrors;
      }

      this.errWriter.flush();
   }

   public void rawWarning(int var1, String var2) {
      if (this.nwarnings < this.MaxWarnings && this.emitWarnings) {
         this.printRawError(var1, "warning: " + var2);
      }

      this.prompt();
      ++this.nwarnings;
      this.errWriter.flush();
   }

   public static String format(String var0, Object... var1) {
      return String.format((Locale)null, var0, var1);
   }

   private class DefaultDiagnosticHandler extends DiagnosticHandler {
      private DefaultDiagnosticHandler() {
      }

      public void report(JCDiagnostic var1) {
         if (Log.this.expectDiagKeys != null) {
            Log.this.expectDiagKeys.remove(var1.getCode());
         }

         switch (var1.getType()) {
            case FRAGMENT:
               throw new IllegalArgumentException();
            case NOTE:
               if ((Log.this.emitWarnings || var1.isMandatory()) && !Log.this.suppressNotes) {
                  Log.this.writeDiagnostic(var1);
               }
               break;
            case WARNING:
               if ((Log.this.emitWarnings || var1.isMandatory()) && Log.this.nwarnings < Log.this.MaxWarnings) {
                  Log.this.writeDiagnostic(var1);
                  ++Log.this.nwarnings;
               }
               break;
            case ERROR:
               if (Log.this.nerrors < Log.this.MaxErrors && Log.this.shouldReport(var1.getSource(), var1.getIntPosition())) {
                  Log.this.writeDiagnostic(var1);
                  ++Log.this.nerrors;
               }
         }

         if (var1.isFlagSet(JCDiagnostic.DiagnosticFlag.COMPRESSED)) {
            Log.this.compressedOutput = true;
         }

      }

      // $FF: synthetic method
      DefaultDiagnosticHandler(Object var2) {
         this();
      }
   }

   public static enum WriterKind {
      NOTICE,
      WARNING,
      ERROR;
   }

   public static class DeferredDiagnosticHandler extends DiagnosticHandler {
      private Queue deferred;
      private final Filter filter;

      public DeferredDiagnosticHandler(Log var1) {
         this(var1, (Filter)null);
      }

      public DeferredDiagnosticHandler(Log var1, Filter var2) {
         this.deferred = new ListBuffer();
         this.filter = var2;
         this.install(var1);
      }

      public void report(JCDiagnostic var1) {
         if (var1.isFlagSet(JCDiagnostic.DiagnosticFlag.NON_DEFERRABLE) || this.filter != null && !this.filter.accepts(var1)) {
            this.prev.report(var1);
         } else {
            this.deferred.add(var1);
         }

      }

      public Queue getDiagnostics() {
         return this.deferred;
      }

      public void reportDeferredDiagnostics() {
         this.reportDeferredDiagnostics(EnumSet.allOf(Diagnostic.Kind.class));
      }

      public void reportDeferredDiagnostics(Set var1) {
         JCDiagnostic var2;
         while((var2 = (JCDiagnostic)this.deferred.poll()) != null) {
            if (var1.contains(var2.getKind())) {
               this.prev.report(var2);
            }
         }

         this.deferred = null;
      }
   }

   public static class DiscardDiagnosticHandler extends DiagnosticHandler {
      public DiscardDiagnosticHandler(Log var1) {
         this.install(var1);
      }

      public void report(JCDiagnostic var1) {
      }
   }

   public abstract static class DiagnosticHandler {
      protected DiagnosticHandler prev;

      protected void install(Log var1) {
         this.prev = var1.diagnosticHandler;
         var1.diagnosticHandler = this;
      }

      public abstract void report(JCDiagnostic var1);
   }

   public static enum PrefixKind {
      JAVAC("javac."),
      COMPILER_MISC("compiler.misc.");

      final String value;

      private PrefixKind(String var3) {
         this.value = var3;
      }

      public String key(String var1) {
         return this.value + var1;
      }
   }
}
