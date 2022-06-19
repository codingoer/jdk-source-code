package com.sun.tools.javac.util;

import com.sun.tools.javac.api.DiagnosticFormatter;
import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

public class JCDiagnostic implements Diagnostic {
   private final DiagnosticType type;
   private final DiagnosticSource source;
   private final DiagnosticPosition position;
   private final String key;
   protected final Object[] args;
   private final Set flags;
   private final Lint.LintCategory lintCategory;
   private SourcePosition sourcePosition;
   private DiagnosticFormatter defaultFormatter;
   /** @deprecated */
   @Deprecated
   private static DiagnosticFormatter fragmentFormatter;

   /** @deprecated */
   @Deprecated
   public static JCDiagnostic fragment(String var0, Object... var1) {
      return new JCDiagnostic(getFragmentFormatter(), JCDiagnostic.DiagnosticType.FRAGMENT, (Lint.LintCategory)null, EnumSet.noneOf(DiagnosticFlag.class), (DiagnosticSource)null, (DiagnosticPosition)null, "compiler." + JCDiagnostic.DiagnosticType.FRAGMENT.key + "." + var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static DiagnosticFormatter getFragmentFormatter() {
      if (fragmentFormatter == null) {
         fragmentFormatter = new BasicDiagnosticFormatter(JavacMessages.getDefaultMessages());
      }

      return fragmentFormatter;
   }

   protected JCDiagnostic(DiagnosticFormatter var1, DiagnosticType var2, Lint.LintCategory var3, Set var4, DiagnosticSource var5, DiagnosticPosition var6, String var7, Object... var8) {
      if (var5 == null && var6 != null && var6.getPreferredPosition() != -1) {
         throw new IllegalArgumentException();
      } else {
         this.defaultFormatter = var1;
         this.type = var2;
         this.lintCategory = var3;
         this.flags = var4;
         this.source = var5;
         this.position = var6;
         this.key = var7;
         this.args = var8;
      }
   }

   public DiagnosticType getType() {
      return this.type;
   }

   public List getSubdiagnostics() {
      return List.nil();
   }

   public boolean isMultiline() {
      return false;
   }

   public boolean isMandatory() {
      return this.flags.contains(JCDiagnostic.DiagnosticFlag.MANDATORY);
   }

   public boolean hasLintCategory() {
      return this.lintCategory != null;
   }

   public Lint.LintCategory getLintCategory() {
      return this.lintCategory;
   }

   public JavaFileObject getSource() {
      return this.source == null ? null : this.source.getFile();
   }

   public DiagnosticSource getDiagnosticSource() {
      return this.source;
   }

   protected int getIntStartPosition() {
      return this.position == null ? -1 : this.position.getStartPosition();
   }

   protected int getIntPosition() {
      return this.position == null ? -1 : this.position.getPreferredPosition();
   }

   protected int getIntEndPosition() {
      return this.position == null ? -1 : this.position.getEndPosition(this.source.getEndPosTable());
   }

   public long getStartPosition() {
      return (long)this.getIntStartPosition();
   }

   public long getPosition() {
      return (long)this.getIntPosition();
   }

   public long getEndPosition() {
      return (long)this.getIntEndPosition();
   }

   public DiagnosticPosition getDiagnosticPosition() {
      return this.position;
   }

   public long getLineNumber() {
      if (this.sourcePosition == null) {
         this.sourcePosition = new SourcePosition();
      }

      return (long)this.sourcePosition.getLineNumber();
   }

   public long getColumnNumber() {
      if (this.sourcePosition == null) {
         this.sourcePosition = new SourcePosition();
      }

      return (long)this.sourcePosition.getColumnNumber();
   }

   public Object[] getArgs() {
      return this.args;
   }

   public String getPrefix() {
      return this.getPrefix(this.type);
   }

   public String getPrefix(DiagnosticType var1) {
      return this.defaultFormatter.formatKind(this, Locale.getDefault());
   }

   public String toString() {
      return this.defaultFormatter.format(this, Locale.getDefault());
   }

   public Diagnostic.Kind getKind() {
      switch (this.type) {
         case NOTE:
            return Kind.NOTE;
         case WARNING:
            return this.flags.contains(JCDiagnostic.DiagnosticFlag.MANDATORY) ? Kind.MANDATORY_WARNING : Kind.WARNING;
         case ERROR:
            return Kind.ERROR;
         default:
            return Kind.OTHER;
      }
   }

   public String getCode() {
      return this.key;
   }

   public String getMessage(Locale var1) {
      return this.defaultFormatter.formatMessage(this, var1);
   }

   public void setFlag(DiagnosticFlag var1) {
      this.flags.add(var1);
      if (this.type == JCDiagnostic.DiagnosticType.ERROR) {
         switch (var1) {
            case SYNTAX:
               this.flags.remove(JCDiagnostic.DiagnosticFlag.RECOVERABLE);
               break;
            case RESOLVE_ERROR:
               this.flags.add(JCDiagnostic.DiagnosticFlag.RECOVERABLE);
         }
      }

   }

   public boolean isFlagSet(DiagnosticFlag var1) {
      return this.flags.contains(var1);
   }

   public static class MultilineDiagnostic extends JCDiagnostic {
      private final List subdiagnostics;

      public MultilineDiagnostic(JCDiagnostic var1, List var2) {
         super(var1.defaultFormatter, var1.getType(), var1.getLintCategory(), var1.flags, var1.getDiagnosticSource(), var1.position, var1.getCode(), var1.getArgs());
         this.subdiagnostics = var2;
      }

      public List getSubdiagnostics() {
         return this.subdiagnostics;
      }

      public boolean isMultiline() {
         return true;
      }
   }

   class SourcePosition {
      private final int line;
      private final int column;

      SourcePosition() {
         int var2 = JCDiagnostic.this.position == null ? -1 : JCDiagnostic.this.position.getPreferredPosition();
         if (var2 != -1 && JCDiagnostic.this.source != null) {
            this.line = JCDiagnostic.this.source.getLineNumber(var2);
            this.column = JCDiagnostic.this.source.getColumnNumber(var2, true);
         } else {
            this.line = this.column = -1;
         }

      }

      public int getLineNumber() {
         return this.line;
      }

      public int getColumnNumber() {
         return this.column;
      }
   }

   public static enum DiagnosticFlag {
      MANDATORY,
      RESOLVE_ERROR,
      SYNTAX,
      RECOVERABLE,
      NON_DEFERRABLE,
      COMPRESSED;
   }

   public static class SimpleDiagnosticPosition implements DiagnosticPosition {
      private final int pos;

      public SimpleDiagnosticPosition(int var1) {
         this.pos = var1;
      }

      public JCTree getTree() {
         return null;
      }

      public int getStartPosition() {
         return this.pos;
      }

      public int getPreferredPosition() {
         return this.pos;
      }

      public int getEndPosition(EndPosTable var1) {
         return this.pos;
      }
   }

   public interface DiagnosticPosition {
      JCTree getTree();

      int getStartPosition();

      int getPreferredPosition();

      int getEndPosition(EndPosTable var1);
   }

   public static enum DiagnosticType {
      FRAGMENT("misc"),
      NOTE("note"),
      WARNING("warn"),
      ERROR("err");

      final String key;

      private DiagnosticType(String var3) {
         this.key = var3;
      }
   }

   public static class Factory {
      protected static final Context.Key diagnosticFactoryKey = new Context.Key();
      DiagnosticFormatter formatter;
      final String prefix;
      final Set defaultErrorFlags;

      public static Factory instance(Context var0) {
         Factory var1 = (Factory)var0.get(diagnosticFactoryKey);
         if (var1 == null) {
            var1 = new Factory(var0);
         }

         return var1;
      }

      protected Factory(Context var1) {
         this(JavacMessages.instance(var1), "compiler");
         var1.put((Context.Key)diagnosticFactoryKey, (Object)this);
         final Options var2 = Options.instance(var1);
         this.initOptions(var2);
         var2.addListener(new Runnable() {
            public void run() {
               Factory.this.initOptions(var2);
            }
         });
      }

      private void initOptions(Options var1) {
         if (var1.isSet("onlySyntaxErrorsUnrecoverable")) {
            this.defaultErrorFlags.add(JCDiagnostic.DiagnosticFlag.RECOVERABLE);
         }

      }

      public Factory(JavacMessages var1, String var2) {
         this.prefix = var2;
         this.formatter = new BasicDiagnosticFormatter(var1);
         this.defaultErrorFlags = EnumSet.of(JCDiagnostic.DiagnosticFlag.MANDATORY);
      }

      public JCDiagnostic error(DiagnosticSource var1, DiagnosticPosition var2, String var3, Object... var4) {
         return this.create(JCDiagnostic.DiagnosticType.ERROR, (Lint.LintCategory)null, this.defaultErrorFlags, var1, var2, var3, var4);
      }

      public JCDiagnostic mandatoryWarning(DiagnosticSource var1, DiagnosticPosition var2, String var3, Object... var4) {
         return this.create(JCDiagnostic.DiagnosticType.WARNING, (Lint.LintCategory)null, EnumSet.of(JCDiagnostic.DiagnosticFlag.MANDATORY), var1, var2, var3, var4);
      }

      public JCDiagnostic mandatoryWarning(Lint.LintCategory var1, DiagnosticSource var2, DiagnosticPosition var3, String var4, Object... var5) {
         return this.create(JCDiagnostic.DiagnosticType.WARNING, var1, EnumSet.of(JCDiagnostic.DiagnosticFlag.MANDATORY), var2, var3, var4, var5);
      }

      public JCDiagnostic warning(Lint.LintCategory var1, String var2, Object... var3) {
         return this.create(JCDiagnostic.DiagnosticType.WARNING, var1, EnumSet.noneOf(DiagnosticFlag.class), (DiagnosticSource)null, (DiagnosticPosition)null, var2, var3);
      }

      public JCDiagnostic warning(DiagnosticSource var1, DiagnosticPosition var2, String var3, Object... var4) {
         return this.create(JCDiagnostic.DiagnosticType.WARNING, (Lint.LintCategory)null, EnumSet.noneOf(DiagnosticFlag.class), var1, var2, var3, var4);
      }

      public JCDiagnostic warning(Lint.LintCategory var1, DiagnosticSource var2, DiagnosticPosition var3, String var4, Object... var5) {
         return this.create(JCDiagnostic.DiagnosticType.WARNING, var1, EnumSet.noneOf(DiagnosticFlag.class), var2, var3, var4, var5);
      }

      public JCDiagnostic mandatoryNote(DiagnosticSource var1, String var2, Object... var3) {
         return this.create(JCDiagnostic.DiagnosticType.NOTE, (Lint.LintCategory)null, EnumSet.of(JCDiagnostic.DiagnosticFlag.MANDATORY), var1, (DiagnosticPosition)null, var2, var3);
      }

      public JCDiagnostic note(String var1, Object... var2) {
         return this.create(JCDiagnostic.DiagnosticType.NOTE, (Lint.LintCategory)null, EnumSet.noneOf(DiagnosticFlag.class), (DiagnosticSource)null, (DiagnosticPosition)null, var1, var2);
      }

      public JCDiagnostic note(DiagnosticSource var1, DiagnosticPosition var2, String var3, Object... var4) {
         return this.create(JCDiagnostic.DiagnosticType.NOTE, (Lint.LintCategory)null, EnumSet.noneOf(DiagnosticFlag.class), var1, var2, var3, var4);
      }

      public JCDiagnostic fragment(String var1, Object... var2) {
         return this.create(JCDiagnostic.DiagnosticType.FRAGMENT, (Lint.LintCategory)null, EnumSet.noneOf(DiagnosticFlag.class), (DiagnosticSource)null, (DiagnosticPosition)null, var1, var2);
      }

      public JCDiagnostic create(DiagnosticType var1, DiagnosticSource var2, DiagnosticPosition var3, String var4, Object... var5) {
         return this.create(var1, (Lint.LintCategory)null, EnumSet.noneOf(DiagnosticFlag.class), var2, var3, var4, var5);
      }

      public JCDiagnostic create(DiagnosticType var1, Lint.LintCategory var2, Set var3, DiagnosticSource var4, DiagnosticPosition var5, String var6, Object... var7) {
         return new JCDiagnostic(this.formatter, var1, var2, var3, var4, var5, this.qualify(var1, var6), var7);
      }

      protected String qualify(DiagnosticType var1, String var2) {
         return this.prefix + "." + var1.key + "." + var2;
      }
   }
}
