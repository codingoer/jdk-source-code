package com.sun.tools.javac.main;

import com.sun.tools.doclint.DocLint;
import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.jvm.Profile;
import com.sun.tools.javac.jvm.Target;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.StringUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.lang.model.SourceVersion;

public enum Option {
   G("-g", "opt.g", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC),
   G_NONE("-g:none", "opt.g.none", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC) {
      public boolean process(OptionHelper var1, String var2) {
         var1.put("-g:", "none");
         return false;
      }
   },
   G_CUSTOM("-g:", "opt.g.lines.vars.source", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC, Option.ChoiceKind.ANYOF, new String[]{"lines", "vars", "source"}),
   XLINT("-Xlint", "opt.Xlint", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC),
   XLINT_CUSTOM("-Xlint:", "opt.Xlint.suboptlist", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC, Option.ChoiceKind.ANYOF, getXLintChoices()),
   XDOCLINT("-Xdoclint", "opt.Xdoclint", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC),
   XDOCLINT_CUSTOM("-Xdoclint:", "opt.Xdoclint.subopts", "opt.Xdoclint.custom", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC) {
      public boolean matches(String var1) {
         return DocLint.isValidOption(var1.replace(XDOCLINT_CUSTOM.text, "-Xmsgs:"));
      }

      public boolean process(OptionHelper var1, String var2) {
         String var3 = var1.get(XDOCLINT_CUSTOM);
         String var4 = var3 == null ? var2 : var3 + " " + var2;
         var1.put(XDOCLINT_CUSTOM.text, var4);
         return false;
      }
   },
   NOWARN("-nowarn", "opt.nowarn", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC) {
      public boolean process(OptionHelper var1, String var2) {
         var1.put("-Xlint:none", var2);
         return false;
      }
   },
   VERBOSE("-verbose", "opt.verbose", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC),
   DEPRECATION("-deprecation", "opt.deprecation", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC) {
      public boolean process(OptionHelper var1, String var2) {
         var1.put("-Xlint:deprecation", var2);
         return false;
      }
   },
   CLASSPATH("-classpath", "opt.arg.path", "opt.classpath", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER),
   CP("-cp", "opt.arg.path", "opt.classpath", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER) {
      public boolean process(OptionHelper var1, String var2, String var3) {
         return super.process(var1, "-classpath", var3);
      }
   },
   SOURCEPATH("-sourcepath", "opt.arg.path", "opt.sourcepath", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER),
   BOOTCLASSPATH("-bootclasspath", "opt.arg.path", "opt.bootclasspath", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER) {
      public boolean process(OptionHelper var1, String var2, String var3) {
         var1.remove("-Xbootclasspath/p:");
         var1.remove("-Xbootclasspath/a:");
         return super.process(var1, var2, var3);
      }
   },
   XBOOTCLASSPATH_PREPEND("-Xbootclasspath/p:", "opt.arg.path", "opt.Xbootclasspath.p", Option.OptionKind.EXTENDED, Option.OptionGroup.FILEMANAGER),
   XBOOTCLASSPATH_APPEND("-Xbootclasspath/a:", "opt.arg.path", "opt.Xbootclasspath.a", Option.OptionKind.EXTENDED, Option.OptionGroup.FILEMANAGER),
   XBOOTCLASSPATH("-Xbootclasspath:", "opt.arg.path", "opt.bootclasspath", Option.OptionKind.EXTENDED, Option.OptionGroup.FILEMANAGER) {
      public boolean process(OptionHelper var1, String var2, String var3) {
         var1.remove("-Xbootclasspath/p:");
         var1.remove("-Xbootclasspath/a:");
         return super.process(var1, "-bootclasspath", var3);
      }
   },
   EXTDIRS("-extdirs", "opt.arg.dirs", "opt.extdirs", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER),
   DJAVA_EXT_DIRS("-Djava.ext.dirs=", "opt.arg.dirs", "opt.extdirs", Option.OptionKind.EXTENDED, Option.OptionGroup.FILEMANAGER) {
      public boolean process(OptionHelper var1, String var2, String var3) {
         return super.process(var1, "-extdirs", var3);
      }
   },
   ENDORSEDDIRS("-endorseddirs", "opt.arg.dirs", "opt.endorseddirs", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER),
   DJAVA_ENDORSED_DIRS("-Djava.endorsed.dirs=", "opt.arg.dirs", "opt.endorseddirs", Option.OptionKind.EXTENDED, Option.OptionGroup.FILEMANAGER) {
      public boolean process(OptionHelper var1, String var2, String var3) {
         return super.process(var1, "-endorseddirs", var3);
      }
   },
   PROC("-proc:", "opt.proc.none.only", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC, Option.ChoiceKind.ONEOF, new String[]{"none", "only"}),
   PROCESSOR("-processor", "opt.arg.class.list", "opt.processor", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC),
   PROCESSORPATH("-processorpath", "opt.arg.path", "opt.processorpath", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER),
   PARAMETERS("-parameters", "opt.parameters", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC),
   D("-d", "opt.arg.directory", "opt.d", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER),
   S("-s", "opt.arg.directory", "opt.sourceDest", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER),
   H("-h", "opt.arg.directory", "opt.headerDest", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER),
   IMPLICIT("-implicit:", "opt.implicit", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC, Option.ChoiceKind.ONEOF, new String[]{"none", "class"}),
   ENCODING("-encoding", "opt.arg.encoding", "opt.encoding", Option.OptionKind.STANDARD, Option.OptionGroup.FILEMANAGER) {
      public boolean process(OptionHelper var1, String var2, String var3) {
         return super.process(var1, var2, var3);
      }
   },
   SOURCE("-source", "opt.arg.release", "opt.source", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC) {
      public boolean process(OptionHelper var1, String var2, String var3) {
         Source var4 = Source.lookup(var3);
         if (var4 == null) {
            var1.error("err.invalid.source", var3);
            return true;
         } else {
            return super.process(var1, var2, var3);
         }
      }
   },
   TARGET("-target", "opt.arg.release", "opt.target", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC) {
      public boolean process(OptionHelper var1, String var2, String var3) {
         Target var4 = Target.lookup(var3);
         if (var4 == null) {
            var1.error("err.invalid.target", var3);
            return true;
         } else {
            return super.process(var1, var2, var3);
         }
      }
   },
   PROFILE("-profile", "opt.arg.profile", "opt.profile", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC) {
      public boolean process(OptionHelper var1, String var2, String var3) {
         Profile var4 = Profile.lookup(var3);
         if (var4 == null) {
            var1.error("err.invalid.profile", var3);
            return true;
         } else {
            return super.process(var1, var2, var3);
         }
      }
   },
   VERSION("-version", "opt.version", Option.OptionKind.STANDARD, Option.OptionGroup.INFO) {
      public boolean process(OptionHelper var1, String var2) {
         Log var3 = var1.getLog();
         String var4 = var1.getOwnName();
         var3.printLines(Log.PrefixKind.JAVAC, "version", var4, JavaCompiler.version());
         return super.process(var1, var2);
      }
   },
   FULLVERSION("-fullversion", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.INFO) {
      public boolean process(OptionHelper var1, String var2) {
         Log var3 = var1.getLog();
         String var4 = var1.getOwnName();
         var3.printLines(Log.PrefixKind.JAVAC, "fullVersion", var4, JavaCompiler.fullVersion());
         return super.process(var1, var2);
      }
   },
   DIAGS("-XDdiags=", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.INFO) {
      public boolean process(OptionHelper var1, String var2) {
         var2 = var2.substring(var2.indexOf(61) + 1);
         String var3 = var2.contains("%") ? "-XDdiagsFormat=" : "-XDdiags=";
         var3 = var3 + var2;
         return XD.matches(var3) ? XD.process(var1, var3) : false;
      }
   },
   HELP("-help", "opt.help", Option.OptionKind.STANDARD, Option.OptionGroup.INFO) {
      public boolean process(OptionHelper var1, String var2) {
         Log var3 = var1.getLog();
         String var4 = var1.getOwnName();
         var3.printLines(Log.PrefixKind.JAVAC, "msg.usage.header", var4);
         Iterator var5 = getJavaCompilerOptions().iterator();

         while(var5.hasNext()) {
            Option var6 = (Option)var5.next();
            var6.help(var3, Option.OptionKind.STANDARD);
         }

         var3.printNewline();
         return super.process(var1, var2);
      }
   },
   A("-A", "opt.arg.key.equals.value", "opt.A", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC, true) {
      public boolean matches(String var1) {
         return var1.startsWith("-A");
      }

      public boolean hasArg() {
         return false;
      }

      public boolean process(OptionHelper var1, String var2) {
         int var3 = var2.length();
         if (var3 == 2) {
            var1.error("err.empty.A.argument");
            return true;
         } else {
            int var4 = var2.indexOf(61);
            String var5 = var2.substring(2, var4 != -1 ? var4 : var3);
            if (!JavacProcessingEnvironment.isValidOptionName(var5)) {
               var1.error("err.invalid.A.key", var2);
               return true;
            } else {
               return this.process(var1, var2, var2);
            }
         }
      }
   },
   X("-X", "opt.X", Option.OptionKind.STANDARD, Option.OptionGroup.INFO) {
      public boolean process(OptionHelper var1, String var2) {
         Log var3 = var1.getLog();
         Iterator var4 = getJavaCompilerOptions().iterator();

         while(var4.hasNext()) {
            Option var5 = (Option)var4.next();
            var5.help(var3, Option.OptionKind.EXTENDED);
         }

         var3.printNewline();
         var3.printLines(Log.PrefixKind.JAVAC, "msg.usage.nonstandard.footer");
         return super.process(var1, var2);
      }
   },
   J("-J", "opt.arg.flag", "opt.J", Option.OptionKind.STANDARD, Option.OptionGroup.INFO, true) {
      public boolean process(OptionHelper var1, String var2) {
         throw new AssertionError("the -J flag should be caught by the launcher.");
      }
   },
   MOREINFO("-moreinfo", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.BASIC) {
      public boolean process(OptionHelper var1, String var2) {
         Type.moreInfo = true;
         return super.process(var1, var2);
      }
   },
   WERROR("-Werror", "opt.Werror", Option.OptionKind.STANDARD, Option.OptionGroup.BASIC),
   PROMPT("-prompt", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.BASIC),
   DOE("-doe", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.BASIC),
   PRINTSOURCE("-printsource", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.BASIC),
   WARNUNCHECKED("-warnunchecked", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.BASIC) {
      public boolean process(OptionHelper var1, String var2) {
         var1.put("-Xlint:unchecked", var2);
         return false;
      }
   },
   XMAXERRS("-Xmaxerrs", "opt.arg.number", "opt.maxerrs", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC),
   XMAXWARNS("-Xmaxwarns", "opt.arg.number", "opt.maxwarns", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC),
   XSTDOUT("-Xstdout", "opt.arg.file", "opt.Xstdout", Option.OptionKind.EXTENDED, Option.OptionGroup.INFO) {
      public boolean process(OptionHelper var1, String var2, String var3) {
         try {
            Log var4 = var1.getLog();
            var4.setWriters(new PrintWriter(new FileWriter(var3), true));
         } catch (IOException var5) {
            var1.error("err.error.writing.file", var3, var5);
            return true;
         }

         return super.process(var1, var2, var3);
      }
   },
   XPRINT("-Xprint", "opt.print", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC),
   XPRINTROUNDS("-XprintRounds", "opt.printRounds", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC),
   XPRINTPROCESSORINFO("-XprintProcessorInfo", "opt.printProcessorInfo", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC),
   XPREFER("-Xprefer:", "opt.prefer", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC, Option.ChoiceKind.ONEOF, new String[]{"source", "newer"}),
   XPKGINFO("-Xpkginfo:", "opt.pkginfo", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC, Option.ChoiceKind.ONEOF, new String[]{"always", "legacy", "nonempty"}),
   O("-O", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.BASIC),
   XJCOV("-Xjcov", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.BASIC),
   PLUGIN("-Xplugin:", "opt.arg.plugin", "opt.plugin", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC) {
      public boolean process(OptionHelper var1, String var2) {
         String var3 = var2.substring(var2.indexOf(58) + 1);
         String var4 = var1.get(PLUGIN);
         var1.put(PLUGIN.text, var4 == null ? var3 : var4 + '\u0000' + var3.trim());
         return false;
      }
   },
   XDIAGS("-Xdiags:", "opt.diags", Option.OptionKind.EXTENDED, Option.OptionGroup.BASIC, Option.ChoiceKind.ONEOF, new String[]{"compact", "verbose"}),
   XD("-XD", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.BASIC) {
      public boolean matches(String var1) {
         return var1.startsWith(this.text);
      }

      public boolean process(OptionHelper var1, String var2) {
         var2 = var2.substring(this.text.length());
         int var3 = var2.indexOf(61);
         String var4 = var3 < 0 ? var2 : var2.substring(0, var3);
         String var5 = var3 < 0 ? var2 : var2.substring(var3 + 1);
         var1.put(var4, var5);
         return false;
      }
   },
   AT("@", "opt.arg.file", "opt.AT", Option.OptionKind.STANDARD, Option.OptionGroup.INFO, true) {
      public boolean process(OptionHelper var1, String var2) {
         throw new AssertionError("the @ flag should be caught by CommandLine.");
      }
   },
   SOURCEFILE("sourcefile", (String)null, Option.OptionKind.HIDDEN, Option.OptionGroup.INFO) {
      public boolean matches(String var1) {
         return var1.endsWith(".java") || SourceVersion.isName(var1);
      }

      public boolean process(OptionHelper var1, String var2) {
         if (var2.endsWith(".java")) {
            File var3 = new File(var2);
            if (!var3.exists()) {
               var1.error("err.file.not.found", var3);
               return true;
            }

            if (!var3.isFile()) {
               var1.error("err.file.not.file", var3);
               return true;
            }

            var1.addFile(var3);
         } else {
            var1.addClassName(var2);
         }

         return false;
      }
   };

   public final String text;
   final OptionKind kind;
   final OptionGroup group;
   final String argsNameKey;
   final String descrKey;
   final boolean hasSuffix;
   final ChoiceKind choiceKind;
   final Map choices;

   private Option(String var3, String var4, OptionKind var5, OptionGroup var6) {
      this(var3, (String)null, var4, var5, var6, (ChoiceKind)null, (Map)null, false);
   }

   private Option(String var3, String var4, String var5, OptionKind var6, OptionGroup var7) {
      this(var3, var4, var5, var6, var7, (ChoiceKind)null, (Map)null, false);
   }

   private Option(String var3, String var4, String var5, OptionKind var6, OptionGroup var7, boolean var8) {
      this(var3, var4, var5, var6, var7, (ChoiceKind)null, (Map)null, var8);
   }

   private Option(String var3, String var4, OptionKind var5, OptionGroup var6, ChoiceKind var7, Map var8) {
      this(var3, (String)null, var4, var5, var6, var7, var8, false);
   }

   private Option(String var3, String var4, OptionKind var5, OptionGroup var6, ChoiceKind var7, String... var8) {
      this(var3, (String)null, var4, var5, var6, var7, createChoices(var8), false);
   }

   private static Map createChoices(String... var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      String[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         var1.put(var5, false);
      }

      return var1;
   }

   private Option(String var3, String var4, String var5, OptionKind var6, OptionGroup var7, ChoiceKind var8, Map var9, boolean var10) {
      this.text = var3;
      this.argsNameKey = var4;
      this.descrKey = var5;
      this.kind = var6;
      this.group = var7;
      this.choiceKind = var8;
      this.choices = var9;
      char var11 = var3.charAt(var3.length() - 1);
      this.hasSuffix = var10 || var11 == ':' || var11 == '=';
   }

   public String getText() {
      return this.text;
   }

   public OptionKind getKind() {
      return this.kind;
   }

   public boolean hasArg() {
      return this.argsNameKey != null && !this.hasSuffix;
   }

   public boolean matches(String var1) {
      if (!this.hasSuffix) {
         return var1.equals(this.text);
      } else if (!var1.startsWith(this.text)) {
         return false;
      } else {
         if (this.choices != null) {
            String var2 = var1.substring(this.text.length());
            if (this.choiceKind == Option.ChoiceKind.ONEOF) {
               return this.choices.keySet().contains(var2);
            }

            String[] var3 = var2.split(",+");
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               String var6 = var3[var5];
               if (!this.choices.keySet().contains(var6)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public boolean process(OptionHelper var1, String var2, String var3) {
      if (this.choices != null) {
         if (this.choiceKind == Option.ChoiceKind.ONEOF) {
            Iterator var4 = this.choices.keySet().iterator();

            String var5;
            while(var4.hasNext()) {
               var5 = (String)var4.next();
               var1.remove(var2 + var5);
            }

            String var9 = var2 + var3;
            var1.put(var9, var9);
            var5 = var2.substring(0, var2.length() - 1);
            var1.put(var5, var3);
         } else {
            String[] var10 = var3.split(",+");
            int var11 = var10.length;

            for(int var6 = 0; var6 < var11; ++var6) {
               String var7 = var10[var6];
               String var8 = var2 + var7;
               var1.put(var8, var8);
            }
         }
      }

      var1.put(var2, var3);
      return false;
   }

   public boolean process(OptionHelper var1, String var2) {
      return this.hasSuffix ? this.process(var1, this.text, var2.substring(this.text.length())) : this.process(var1, var2, var2);
   }

   void help(Log var1, OptionKind var2) {
      if (this.kind == var2) {
         var1.printRawLines(Log.WriterKind.NOTICE, String.format("  %-26s %s", this.helpSynopsis(var1), var1.localize(Log.PrefixKind.JAVAC, this.descrKey)));
      }
   }

   private String helpSynopsis(Log var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append(this.text);
      if (this.argsNameKey == null) {
         if (this.choices != null) {
            String var3 = "{";
            Iterator var4 = this.choices.entrySet().iterator();

            while(var4.hasNext()) {
               Map.Entry var5 = (Map.Entry)var4.next();
               if (!(Boolean)var5.getValue()) {
                  var2.append(var3);
                  var2.append((String)var5.getKey());
                  var3 = ",";
               }
            }

            var2.append("}");
         }
      } else {
         if (!this.hasSuffix) {
            var2.append(" ");
         }

         var2.append(var1.localize(Log.PrefixKind.JAVAC, this.argsNameKey));
      }

      return var2.toString();
   }

   private static Map getXLintChoices() {
      LinkedHashMap var0 = new LinkedHashMap();
      var0.put("all", false);
      Lint.LintCategory[] var1 = Lint.LintCategory.values();
      int var2 = var1.length;

      int var3;
      Lint.LintCategory var4;
      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1[var3];
         var0.put(var4.option, var4.hidden);
      }

      var1 = Lint.LintCategory.values();
      var2 = var1.length;

      for(var3 = 0; var3 < var2; ++var3) {
         var4 = var1[var3];
         var0.put("-" + var4.option, var4.hidden);
      }

      var0.put("none", false);
      return var0;
   }

   static Set getJavaCompilerOptions() {
      return EnumSet.allOf(Option.class);
   }

   public static Set getJavacFileManagerOptions() {
      return getOptions(EnumSet.of(Option.OptionGroup.FILEMANAGER));
   }

   public static Set getJavacToolOptions() {
      return getOptions(EnumSet.of(Option.OptionGroup.BASIC));
   }

   static Set getOptions(Set var0) {
      EnumSet var1 = EnumSet.noneOf(Option.class);
      Option[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Option var5 = var2[var4];
         if (var0.contains(var5.group)) {
            var1.add(var5);
         }
      }

      return Collections.unmodifiableSet(var1);
   }

   // $FF: synthetic method
   Option(String var3, String var4, OptionKind var5, OptionGroup var6, Object var7) {
      this(var3, var4, var5, var6);
   }

   // $FF: synthetic method
   Option(String var3, String var4, String var5, OptionKind var6, OptionGroup var7, Object var8) {
      this(var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   Option(String var3, String var4, String var5, OptionKind var6, OptionGroup var7, boolean var8, Object var9) {
      this(var3, var4, var5, var6, var7, var8);
   }

   public static enum PkgInfo {
      ALWAYS,
      LEGACY,
      NONEMPTY;

      public static PkgInfo get(Options var0) {
         String var1 = var0.get(Option.XPKGINFO);
         return var1 == null ? LEGACY : valueOf(StringUtils.toUpperCase(var1));
      }
   }

   static enum ChoiceKind {
      ONEOF,
      ANYOF;
   }

   static enum OptionGroup {
      BASIC,
      FILEMANAGER,
      INFO,
      OPERAND;
   }

   public static enum OptionKind {
      STANDARD,
      EXTENDED,
      HIDDEN;
   }
}
