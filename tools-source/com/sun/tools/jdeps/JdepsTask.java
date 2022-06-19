package com.sun.tools.jdeps;

import com.sun.tools.classfile.AccessFlags;
import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.Dependencies;
import com.sun.tools.classfile.Dependency;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

class JdepsTask {
   static Option[] recognizedOptions = new Option[]{new Option(false, new String[]{"-h", "-?", "-help"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.help = true;
      }
   }, new Option(true, new String[]{"-dotoutput"}) {
      void process(JdepsTask var1, String var2, String var3) throws BadArgs {
         Path var4 = Paths.get(var3);
         if (!Files.exists(var4, new LinkOption[0]) || Files.isDirectory(var4, new LinkOption[0]) && Files.isWritable(var4)) {
            var1.options.dotOutputDir = var3;
         } else {
            throw new BadArgs("err.invalid.path", new Object[]{var3});
         }
      }
   }, new Option(false, new String[]{"-s", "-summary"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.showSummary = true;
         var1.options.verbose = Analyzer.Type.SUMMARY;
      }
   }, new Option(false, new String[]{"-v", "-verbose", "-verbose:package", "-verbose:class"}) {
      void process(JdepsTask var1, String var2, String var3) throws BadArgs {
         switch (var2) {
            case "-v":
            case "-verbose":
               var1.options.verbose = Analyzer.Type.VERBOSE;
               var1.options.filterSameArchive = false;
               var1.options.filterSamePackage = false;
               break;
            case "-verbose:package":
               var1.options.verbose = Analyzer.Type.PACKAGE;
               break;
            case "-verbose:class":
               var1.options.verbose = Analyzer.Type.CLASS;
               break;
            default:
               throw new BadArgs("err.invalid.arg.for.option", new Object[]{var2});
         }

      }
   }, new Option(true, new String[]{"-cp", "-classpath"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.classpath = var3;
      }
   }, new Option(true, new String[]{"-p", "-package"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.packageNames.add(var3);
      }
   }, new Option(true, new String[]{"-e", "-regex"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.regex = var3;
      }
   }, new Option(true, new String[]{"-f", "-filter"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.filterRegex = var3;
      }
   }, new Option(false, new String[]{"-filter:package", "-filter:archive", "-filter:none"}) {
      void process(JdepsTask var1, String var2, String var3) {
         switch (var2) {
            case "-filter:package":
               var1.options.filterSamePackage = true;
               var1.options.filterSameArchive = false;
               break;
            case "-filter:archive":
               var1.options.filterSameArchive = true;
               var1.options.filterSamePackage = false;
               break;
            case "-filter:none":
               var1.options.filterSameArchive = false;
               var1.options.filterSamePackage = false;
         }

      }
   }, new Option(true, new String[]{"-include"}) {
      void process(JdepsTask var1, String var2, String var3) throws BadArgs {
         var1.options.includePattern = Pattern.compile(var3);
      }
   }, new Option(false, new String[]{"-P", "-profile"}) {
      void process(JdepsTask var1, String var2, String var3) throws BadArgs {
         var1.options.showProfile = true;
         if (Profile.getProfileCount() == 0) {
            throw new BadArgs("err.option.unsupported", new Object[]{var2, JdepsTask.getMessage("err.profiles.msg")});
         }
      }
   }, new Option(false, new String[]{"-apionly"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.apiOnly = true;
      }
   }, new Option(false, new String[]{"-R", "-recursive"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.depth = 0;
         var1.options.filterSameArchive = false;
         var1.options.filterSamePackage = false;
      }
   }, new Option(false, new String[]{"-jdkinternals"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.findJDKInternals = true;
         var1.options.verbose = Analyzer.Type.CLASS;
         if (var1.options.includePattern == null) {
            var1.options.includePattern = Pattern.compile(".*");
         }

      }
   }, new Option(false, new String[]{"-version"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.version = true;
      }
   }, new HiddenOption(false, new String[]{"-fullversion"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.fullVersion = true;
      }
   }, new HiddenOption(false, new String[]{"-showlabel"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.showLabel = true;
      }
   }, new HiddenOption(false, new String[]{"-q", "-quiet"}) {
      void process(JdepsTask var1, String var2, String var3) {
         var1.options.nowarning = true;
      }
   }, new HiddenOption(true, new String[]{"-depth"}) {
      void process(JdepsTask var1, String var2, String var3) throws BadArgs {
         try {
            var1.options.depth = Integer.parseInt(var3);
         } catch (NumberFormatException var5) {
            throw new BadArgs("err.invalid.arg.for.option", new Object[]{var2});
         }
      }
   }};
   private static final String PROGNAME = "jdeps";
   private final Options options = new Options();
   private final List classes = new ArrayList();
   private PrintWriter log;
   static final int EXIT_OK = 0;
   static final int EXIT_ERROR = 1;
   static final int EXIT_CMDERR = 2;
   static final int EXIT_SYSERR = 3;
   static final int EXIT_ABNORMAL = 4;
   private final List sourceLocations = new ArrayList();

   void setLog(PrintWriter var1) {
      this.log = var1;
   }

   int run(String[] var1) {
      if (this.log == null) {
         this.log = new PrintWriter(System.out);
      }

      int var3;
      try {
         this.handleOptions(var1);
         if (this.options.help) {
            this.showHelp();
         }

         if (this.options.version || this.options.fullVersion) {
            this.showVersion(this.options.fullVersion);
         }

         byte var11;
         if (this.classes.isEmpty() && this.options.includePattern == null) {
            if (!this.options.help && !this.options.version && !this.options.fullVersion) {
               this.showHelp();
               var11 = 2;
               return var11;
            }

            var11 = 0;
            return var11;
         }

         if (this.options.regex != null && this.options.packageNames.size() > 0) {
            this.showHelp();
            var11 = 2;
            return var11;
         }

         if (this.options.findJDKInternals && (this.options.regex != null || this.options.packageNames.size() > 0 || this.options.showSummary)) {
            this.showHelp();
            var11 = 2;
            return var11;
         }

         if (this.options.showSummary && this.options.verbose != Analyzer.Type.SUMMARY) {
            this.showHelp();
            var11 = 2;
            return var11;
         }

         boolean var2 = this.run();
         var3 = var2 ? 0 : 1;
         return var3;
      } catch (BadArgs var8) {
         this.reportError(var8.key, var8.args);
         if (var8.showUsage) {
            this.log.println(getMessage("main.usage.summary", "jdeps"));
         }

         var3 = 2;
      } catch (IOException var9) {
         var3 = 4;
         return var3;
      } finally {
         this.log.flush();
      }

      return var3;
   }

   private boolean run() throws IOException {
      this.findDependencies();
      Analyzer var1 = new Analyzer(this.options.verbose, new Analyzer.Filter() {
         public boolean accepts(Dependency.Location var1, Archive var2, Dependency.Location var3, Archive var4) {
            if (!JdepsTask.this.options.findJDKInternals) {
               if (JdepsTask.this.options.filterSameArchive) {
                  return var2 != var4;
               } else {
                  return true;
               }
            } else {
               return JdepsTask.this.isJDKArchive(var4) && !((PlatformClassPath.JDKArchive)var4).isExported(var3.getClassName());
            }
         }
      });
      var1.run(this.sourceLocations);
      if (this.options.dotOutputDir != null) {
         Path var2 = Paths.get(this.options.dotOutputDir);
         Files.createDirectories(var2);
         this.generateDotFiles(var2, var1);
      } else {
         this.printRawOutput(this.log, var1);
      }

      if (this.options.findJDKInternals && !this.options.nowarning) {
         this.showReplacements(var1);
      }

      return true;
   }

   private void generateSummaryDotFile(Path var1, Analyzer var2) throws IOException {
      Analyzer.Type var3 = this.options.verbose != Analyzer.Type.PACKAGE && this.options.verbose != Analyzer.Type.SUMMARY ? Analyzer.Type.PACKAGE : Analyzer.Type.SUMMARY;
      Path var4 = var1.resolve("summary.dot");
      PrintWriter var5 = new PrintWriter(Files.newOutputStream(var4));
      Throwable var6 = null;

      try {
         SummaryDotFile var7 = new SummaryDotFile(var5, var3);
         Throwable var8 = null;

         try {
            Iterator var9 = this.sourceLocations.iterator();

            while(var9.hasNext()) {
               Archive var10 = (Archive)var9.next();
               if (!var10.isEmpty()) {
                  if ((this.options.verbose == Analyzer.Type.PACKAGE || this.options.verbose == Analyzer.Type.SUMMARY) && this.options.showLabel) {
                     var2.visitDependences(var10, var7.labelBuilder(), Analyzer.Type.PACKAGE);
                  }

                  var2.visitDependences(var10, var7, var3);
               }
            }
         } catch (Throwable var32) {
            var8 = var32;
            throw var32;
         } finally {
            if (var7 != null) {
               if (var8 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var31) {
                     var8.addSuppressed(var31);
                  }
               } else {
                  var7.close();
               }
            }

         }
      } catch (Throwable var34) {
         var6 = var34;
         throw var34;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var30) {
                  var6.addSuppressed(var30);
               }
            } else {
               var5.close();
            }
         }

      }

   }

   private void generateDotFiles(Path var1, Analyzer var2) throws IOException {
      if (this.options.verbose != Analyzer.Type.SUMMARY) {
         Iterator var3 = this.sourceLocations.iterator();

         while(var3.hasNext()) {
            Archive var4 = (Archive)var3.next();
            if (var2.hasDependences(var4)) {
               Path var5 = var1.resolve(var4.getName() + ".dot");
               PrintWriter var6 = new PrintWriter(Files.newOutputStream(var5));
               Throwable var7 = null;

               try {
                  DotFileFormatter var8 = new DotFileFormatter(var6, var4);
                  Throwable var9 = null;

                  try {
                     var2.visitDependences(var4, var8);
                  } catch (Throwable var32) {
                     var9 = var32;
                     throw var32;
                  } finally {
                     if (var8 != null) {
                        if (var9 != null) {
                           try {
                              var8.close();
                           } catch (Throwable var31) {
                              var9.addSuppressed(var31);
                           }
                        } else {
                           var8.close();
                        }
                     }

                  }
               } catch (Throwable var34) {
                  var7 = var34;
                  throw var34;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var30) {
                           var7.addSuppressed(var30);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }
            }
         }
      }

      this.generateSummaryDotFile(var1, var2);
   }

   private void printRawOutput(PrintWriter var1, Analyzer var2) {
      RawOutputFormatter var3 = new RawOutputFormatter(var1);
      RawSummaryFormatter var4 = new RawSummaryFormatter(var1);
      Iterator var5 = this.sourceLocations.iterator();

      while(var5.hasNext()) {
         Archive var6 = (Archive)var5.next();
         if (!var6.isEmpty()) {
            var2.visitDependences(var6, var4, Analyzer.Type.SUMMARY);
            if (var2.hasDependences(var6) && this.options.verbose != Analyzer.Type.SUMMARY) {
               var2.visitDependences(var6, var3);
            }
         }
      }

   }

   private boolean isValidClassName(String var1) {
      if (!Character.isJavaIdentifierStart(var1.charAt(0))) {
         return false;
      } else {
         for(int var2 = 1; var2 < var1.length(); ++var2) {
            char var3 = var1.charAt(var2);
            if (var3 != '.' && !Character.isJavaIdentifierPart(var3)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean matches(String var1, AccessFlags var2) {
      if (this.options.apiOnly && !var2.is(1)) {
         return false;
      } else {
         return this.options.includePattern != null ? this.options.includePattern.matcher(var1.replace('/', '.')).matches() : true;
      }
   }

   private void findDependencies() throws IOException {
      Dependency.Finder var1 = this.options.apiOnly ? Dependencies.getAPIFinder(4) : Dependencies.getClassDependencyFinder();
      DependencyFilter var2 = new DependencyFilter();
      ArrayList var3 = new ArrayList();
      LinkedList var4 = new LinkedList();
      ArrayList var5 = new ArrayList();
      Iterator var6 = this.classes.iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         Path var8 = Paths.get(var7);
         if (Files.exists(var8, new LinkOption[0])) {
            var5.add(var8);
            var3.add(Archive.getInstance(var8));
         } else if (this.isValidClassName(var7)) {
            var4.add(var7);
         } else {
            this.warning("warn.invalid.arg", var7);
         }
      }

      this.sourceLocations.addAll(var3);
      ArrayList var21 = new ArrayList();
      var21.addAll(this.getClassPathArchives(this.options.classpath, var5));
      if (this.options.includePattern != null) {
         var3.addAll(var21);
      }

      var21.addAll(PlatformClassPath.getArchives());
      this.sourceLocations.addAll(var21);
      Iterator var22 = this.sourceLocations.iterator();

      while(var22.hasNext()) {
         Archive var24 = (Archive)var22.next();
         if (var24.reader().isMultiReleaseJar()) {
            this.warning("warn.mrjar.usejdk9", var24.getPathName());
         }
      }

      LinkedList var23 = new LinkedList();
      HashSet var25 = new HashSet();
      Iterator var9 = var3.iterator();

      ClassFile var12;
      String var31;
      label164:
      while(var9.hasNext()) {
         Archive var10 = (Archive)var9.next();
         Iterator var11 = var10.reader().getClassFiles().iterator();

         while(true) {
            String var13;
            do {
               if (!var11.hasNext()) {
                  continue label164;
               }

               var12 = (ClassFile)var11.next();

               try {
                  var13 = var12.getName();
               } catch (ConstantPoolException var20) {
                  throw new Dependencies.ClassFileError(var20);
               }
            } while(!this.matches(var13, var12.access_flags));

            if (!var25.contains(var13)) {
               var25.add(var13);
            }

            Iterator var14 = var1.findDependencies(var12).iterator();

            while(var14.hasNext()) {
               Dependency var15 = (Dependency)var14.next();
               if (var2.accepts(var15)) {
                  String var16 = var15.getTarget().getName();
                  if (!var25.contains(var16) && !var23.contains(var16)) {
                     var23.add(var16);
                  }

                  var10.addClass(var15.getOrigin(), var15.getTarget());
               } else {
                  var10.addClass(var15.getOrigin());
               }
            }

            var14 = var10.reader().skippedEntries().iterator();

            while(var14.hasNext()) {
               var31 = (String)var14.next();
               this.warning("warn.skipped.entry", var31, var10.getPathName());
            }
         }
      }

      LinkedList var26 = var4;
      int var27 = this.options.depth > 0 ? this.options.depth : Integer.MAX_VALUE;

      do {
         String var28;
         while((var28 = (String)var26.poll()) != null) {
            if (!var25.contains(var28)) {
               var12 = null;
               Iterator var29 = var21.iterator();

               label129:
               while(var29.hasNext()) {
                  Archive var30 = (Archive)var29.next();
                  var12 = var30.reader().getClassFile(var28);
                  if (var12 != null) {
                     try {
                        var31 = var12.getName();
                     } catch (ConstantPoolException var19) {
                        throw new Dependencies.ClassFileError(var19);
                     }

                     if (!var25.contains(var31)) {
                        var25.add(var31);
                        if (this.isJDKArchive(var30)) {
                           ((PlatformClassPath.JDKArchive)var30).processJdkExported(var12);
                        }

                        Iterator var32 = var1.findDependencies(var12).iterator();

                        while(var32.hasNext()) {
                           Dependency var17 = (Dependency)var32.next();
                           if (var27 == 0) {
                              var30.addClass(var17.getOrigin());
                              break label129;
                           }

                           if (var2.accepts(var17)) {
                              var30.addClass(var17.getOrigin(), var17.getTarget());
                              String var18 = var17.getTarget().getName();
                              if (!var25.contains(var18) && !var23.contains(var18)) {
                                 var23.add(var18);
                              }
                           } else {
                              var30.addClass(var17.getOrigin());
                           }
                        }
                     }
                     break;
                  }
               }

               if (var12 == null) {
                  var25.add(var28);
               }
            }
         }

         var26 = var23;
         var23 = new LinkedList();
      } while(!var26.isEmpty() && var27-- > 0);

   }

   public void handleOptions(String[] var1) throws BadArgs {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         String var3;
         if (var1[var2].charAt(0) == '-') {
            var3 = var1[var2];
            Option var4 = this.getOption(var3);
            String var5 = null;
            if (var4.hasArg) {
               if (var3.startsWith("-") && var3.indexOf(61) > 0) {
                  var5 = var3.substring(var3.indexOf(61) + 1, var3.length());
               } else if (var2 + 1 < var1.length) {
                  ++var2;
                  var5 = var1[var2];
               }

               if (var5 == null || var5.isEmpty() || var5.charAt(0) == '-') {
                  throw (new BadArgs("err.missing.arg", new Object[]{var3})).showUsage(true);
               }
            }

            var4.process(this, var3, var5);
            if (var4.ignoreRest()) {
               var2 = var1.length;
            }
         } else {
            while(var2 < var1.length) {
               var3 = var1[var2];
               if (var3.charAt(0) == '-') {
                  throw (new BadArgs("err.option.after.class", new Object[]{var3})).showUsage(true);
               }

               this.classes.add(var3);
               ++var2;
            }
         }
      }

   }

   private Option getOption(String var1) throws BadArgs {
      Option[] var2 = recognizedOptions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Option var5 = var2[var4];
         if (var5.matches(var1)) {
            return var5;
         }
      }

      throw (new BadArgs("err.unknown.option", new Object[]{var1})).showUsage(true);
   }

   private void reportError(String var1, Object... var2) {
      this.log.println(getMessage("error.prefix") + " " + getMessage(var1, var2));
   }

   private void warning(String var1, Object... var2) {
      this.log.println(getMessage("warn.prefix") + " " + getMessage(var1, var2));
   }

   private void showHelp() {
      this.log.println(getMessage("main.usage", "jdeps"));
      Option[] var1 = recognizedOptions;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Option var4 = var1[var3];
         String var5 = var4.aliases[0].substring(1);
         var5 = var5.charAt(0) == '-' ? var5.substring(1) : var5;
         if (!var4.isHidden() && !var5.equals("h") && !var5.startsWith("filter:")) {
            this.log.println(getMessage("main.opt." + var5));
         }
      }

   }

   private void showVersion(boolean var1) {
      this.log.println(this.version(var1 ? "full" : "release"));
   }

   private String version(String var1) {
      if (JdepsTask.ResourceBundleHelper.versionRB == null) {
         return System.getProperty("java.version");
      } else {
         try {
            return JdepsTask.ResourceBundleHelper.versionRB.getString(var1);
         } catch (MissingResourceException var3) {
            return getMessage("version.unknown", System.getProperty("java.version"));
         }
      }
   }

   static String getMessage(String var0, Object... var1) {
      try {
         return MessageFormat.format(JdepsTask.ResourceBundleHelper.bundle.getString(var0), var1);
      } catch (MissingResourceException var3) {
         throw new InternalError("Missing message: " + var0);
      }
   }

   private List getClassPathArchives(String var1, List var2) throws IOException {
      ArrayList var3 = new ArrayList();
      if (var1.isEmpty()) {
         return var3;
      } else {
         ArrayList var4 = new ArrayList();
         String[] var5 = var1.split(File.pathSeparator);
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            if (var8.length() > 0) {
               int var9 = var8.lastIndexOf(".*");
               if (var9 > 0) {
                  Path var10 = Paths.get(var8.substring(0, var9));
                  DirectoryStream var11 = Files.newDirectoryStream(var10, "*.jar");
                  Throwable var12 = null;

                  try {
                     Iterator var13 = var11.iterator();

                     while(var13.hasNext()) {
                        Path var14 = (Path)var13.next();
                        var4.add(var14);
                     }
                  } catch (Throwable var22) {
                     var12 = var22;
                     throw var22;
                  } finally {
                     if (var11 != null) {
                        if (var12 != null) {
                           try {
                              var11.close();
                           } catch (Throwable var21) {
                              var12.addSuppressed(var21);
                           }
                        } else {
                           var11.close();
                        }
                     }

                  }
               } else {
                  var4.add(Paths.get(var8));
               }
            }
         }

         Iterator var24 = var4.iterator();

         while(var24.hasNext()) {
            Path var25 = (Path)var24.next();
            if (Files.exists(var25, new LinkOption[0]) && !this.hasSameFile(var2, var25)) {
               var3.add(Archive.getInstance(var25));
            }
         }

         return var3;
      }
   }

   private boolean hasSameFile(List var1, Path var2) throws IOException {
      Iterator var3 = var1.iterator();

      Path var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (Path)var3.next();
      } while(!Files.isSameFile(var4, var2));

      return true;
   }

   private boolean isJDKArchive(Archive var1) {
      return PlatformClassPath.JDKArchive.class.isInstance(var1);
   }

   private String toTag(String var1, Archive var2, Analyzer.Type var3) {
      if (!this.isJDKArchive(var2)) {
         return var2.getName();
      } else {
         PlatformClassPath.JDKArchive var4 = (PlatformClassPath.JDKArchive)var2;
         boolean var5 = false;
         if (var3 != Analyzer.Type.CLASS && var3 != Analyzer.Type.VERBOSE) {
            var5 = var4.isExportedPackage(var1);
         } else {
            var5 = var4.isExported(var1);
         }

         Profile var6 = this.getProfile(var1, var3);
         if (!var5) {
            return "JDK internal API (" + var2.getName() + ")";
         } else {
            return this.options.showProfile && var6 != null ? var6.profileName() : "";
         }
      }
   }

   private String toTag(String var1, Archive var2) {
      return this.toTag(var1, var2, this.options.verbose);
   }

   private Profile getProfile(String var1, Analyzer.Type var2) {
      String var3 = var1;
      if (var2 == Analyzer.Type.CLASS || var2 == Analyzer.Type.VERBOSE) {
         int var4 = var1.lastIndexOf(46);
         var3 = var4 > 0 ? var1.substring(0, var4) : "";
      }

      return Profile.getProfile(var3);
   }

   private String replacementFor(String var1) {
      String var2 = var1;
      String var3 = null;

      while(var3 == null && var2 != null) {
         try {
            var3 = JdepsTask.ResourceBundleHelper.jdkinternals.getString(var2);
         } catch (MissingResourceException var6) {
            int var5 = var2.lastIndexOf(46);
            var2 = var5 > 0 ? var2.substring(0, var5) : null;
         }
      }

      return var3;
   }

   private void showReplacements(Analyzer var1) {
      TreeMap var2 = new TreeMap();
      boolean var3 = false;
      Iterator var4 = this.sourceLocations.iterator();

      while(var4.hasNext()) {
         Archive var5 = (Archive)var4.next();
         var3 = var3 || var1.hasDependences(var5);
         Iterator var6 = var1.dependences(var5).iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            String var8 = this.replacementFor(var7);
            if (var8 != null && !var2.containsKey(var7)) {
               var2.put(var7, var8);
            }
         }
      }

      if (var3) {
         this.log.println();
         this.warning("warn.replace.useJDKInternals", getMessage("jdeps.wiki.url"));
      }

      if (!var2.isEmpty()) {
         this.log.println();
         this.log.format("%-40s %s%n", "JDK Internal API", "Suggested Replacement");
         this.log.format("%-40s %s%n", "----------------", "---------------------");
         var4 = var2.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry var9 = (Map.Entry)var4.next();
            this.log.format("%-40s %s%n", var9.getKey(), var9.getValue());
         }
      }

   }

   class SummaryDotFile implements Analyzer.Visitor, AutoCloseable {
      private final PrintWriter writer;
      private final Analyzer.Type type;
      private final Map edges = new HashMap();

      SummaryDotFile(PrintWriter var2, Analyzer.Type var3) {
         this.writer = var2;
         this.type = var3;
         var2.format("digraph \"summary\" {%n");
      }

      public void close() {
         this.writer.println("}");
      }

      public void visitDependence(String var1, Archive var2, String var3, Archive var4) {
         String var5 = this.type == Analyzer.Type.PACKAGE ? var3 : var4.getName();
         String var6;
         if (this.type == Analyzer.Type.PACKAGE) {
            var6 = JdepsTask.this.toTag(var3, var4, this.type);
            if (!var6.isEmpty()) {
               var5 = var5 + " (" + var6 + ")";
            }
         } else if (JdepsTask.this.options.showProfile && PlatformClassPath.JDKArchive.isProfileArchive(var4)) {
            var5 = var5 + " (" + var3 + ")";
         }

         var6 = this.getLabel(var2, var4);
         this.writer.format("  %-50s -> \"%s\"%s;%n", String.format("\"%s\"", var1), var5, var6);
      }

      String getLabel(Archive var1, Archive var2) {
         if (this.edges.isEmpty()) {
            return "";
         } else {
            StringBuilder var3 = (StringBuilder)((Map)this.edges.get(var1)).get(var2);
            return var3 == null ? "" : String.format(" [label=\"%s\",fontsize=9]", var3.toString());
         }
      }

      Analyzer.Visitor labelBuilder() {
         return new Analyzer.Visitor() {
            public void visitDependence(String var1, Archive var2, String var3, Archive var4) {
               Object var5 = (Map)SummaryDotFile.this.edges.get(var2);
               if (!SummaryDotFile.this.edges.containsKey(var2)) {
                  SummaryDotFile.this.edges.put(var2, var5 = new HashMap());
               }

               StringBuilder var6 = (StringBuilder)((Map)var5).get(var4);
               if (var6 == null) {
                  ((Map)var5).put(var4, var6 = new StringBuilder());
               }

               String var7 = JdepsTask.this.toTag(var3, var4, Analyzer.Type.PACKAGE);
               this.addLabel(var6, var1, var3, var7);
            }

            void addLabel(StringBuilder var1, String var2, String var3, String var4) {
               var1.append(var2).append(" -> ").append(var3);
               if (!var4.isEmpty()) {
                  var1.append(" (" + var4 + ")");
               }

               var1.append("\\n");
            }
         };
      }
   }

   class DotFileFormatter implements Analyzer.Visitor, AutoCloseable {
      private final PrintWriter writer;
      private final String name;

      DotFileFormatter(PrintWriter var2, Archive var3) {
         this.writer = var2;
         this.name = var3.getName();
         var2.format("digraph \"%s\" {%n", this.name);
         var2.format("    // Path: %s%n", var3.getPathName());
      }

      public void close() {
         this.writer.println("}");
      }

      public void visitDependence(String var1, Archive var2, String var3, Archive var4) {
         String var5 = JdepsTask.this.toTag(var3, var4);
         this.writer.format("   %-50s -> \"%s\";%n", String.format("\"%s\"", var1), var5.isEmpty() ? var3 : String.format("%s (%s)", var3, var5));
      }
   }

   class RawSummaryFormatter implements Analyzer.Visitor {
      private final PrintWriter writer;

      RawSummaryFormatter(PrintWriter var2) {
         this.writer = var2;
      }

      public void visitDependence(String var1, Archive var2, String var3, Archive var4) {
         this.writer.format("%s -> %s", var2.getName(), var4.getPathName());
         if (JdepsTask.this.options.showProfile && PlatformClassPath.JDKArchive.isProfileArchive(var4)) {
            this.writer.format(" (%s)", var3);
         }

         this.writer.format("%n");
      }
   }

   class RawOutputFormatter implements Analyzer.Visitor {
      private final PrintWriter writer;
      private String pkg = "";

      RawOutputFormatter(PrintWriter var2) {
         this.writer = var2;
      }

      public void visitDependence(String var1, Archive var2, String var3, Archive var4) {
         String var5 = JdepsTask.this.toTag(var3, var4);
         if (JdepsTask.this.options.verbose == Analyzer.Type.VERBOSE) {
            this.writer.format("   %-50s -> %-50s %s%n", var1, var3, var5);
         } else {
            if (!var1.equals(this.pkg)) {
               this.pkg = var1;
               this.writer.format("   %s (%s)%n", var1, var2.getName());
            }

            this.writer.format("      -> %-50s %s%n", var3, var5);
         }

      }
   }

   private static class ResourceBundleHelper {
      static final ResourceBundle versionRB;
      static final ResourceBundle bundle;
      static final ResourceBundle jdkinternals;

      static {
         Locale var0 = Locale.getDefault();

         try {
            bundle = ResourceBundle.getBundle("com.sun.tools.jdeps.resources.jdeps", var0);
         } catch (MissingResourceException var4) {
            throw new InternalError("Cannot find jdeps resource bundle for locale " + var0);
         }

         try {
            versionRB = ResourceBundle.getBundle("com.sun.tools.jdeps.resources.version");
         } catch (MissingResourceException var3) {
            throw new InternalError("version.resource.missing");
         }

         try {
            jdkinternals = ResourceBundle.getBundle("com.sun.tools.jdeps.resources.jdkinternals");
         } catch (MissingResourceException var2) {
            throw new InternalError("Cannot find jdkinternals resource bundle");
         }
      }
   }

   private static class Options {
      boolean help;
      boolean version;
      boolean fullVersion;
      boolean showProfile;
      boolean showSummary;
      boolean apiOnly;
      boolean showLabel;
      boolean findJDKInternals;
      boolean nowarning;
      Analyzer.Type verbose;
      boolean filterSamePackage;
      boolean filterSameArchive;
      String filterRegex;
      String dotOutputDir;
      String classpath;
      int depth;
      Set packageNames;
      String regex;
      Pattern includePattern;

      private Options() {
         this.verbose = Analyzer.Type.PACKAGE;
         this.filterSamePackage = true;
         this.filterSameArchive = false;
         this.classpath = "";
         this.depth = 1;
         this.packageNames = new HashSet();
      }

      // $FF: synthetic method
      Options(Object var1) {
         this();
      }
   }

   class DependencyFilter implements Dependency.Filter {
      final Dependency.Filter filter;
      final Pattern filterPattern;

      DependencyFilter() {
         if (JdepsTask.this.options.regex != null) {
            this.filter = Dependencies.getRegexFilter(Pattern.compile(JdepsTask.this.options.regex));
         } else if (JdepsTask.this.options.packageNames.size() > 0) {
            this.filter = Dependencies.getPackageFilter(JdepsTask.this.options.packageNames, false);
         } else {
            this.filter = null;
         }

         this.filterPattern = JdepsTask.this.options.filterRegex != null ? Pattern.compile(JdepsTask.this.options.filterRegex) : null;
      }

      public boolean accepts(Dependency var1) {
         if (var1.getOrigin().equals(var1.getTarget())) {
            return false;
         } else {
            String var2 = var1.getTarget().getPackageName();
            if (JdepsTask.this.options.filterSamePackage && var1.getOrigin().getPackageName().equals(var2)) {
               return false;
            } else if (this.filterPattern != null && this.filterPattern.matcher(var2).matches()) {
               return false;
            } else {
               return this.filter != null ? this.filter.accepts(var1) : true;
            }
         }
      }
   }

   abstract static class HiddenOption extends Option {
      HiddenOption(boolean var1, String... var2) {
         super(var1, var2);
      }

      boolean isHidden() {
         return true;
      }
   }

   abstract static class Option {
      final boolean hasArg;
      final String[] aliases;

      Option(boolean var1, String... var2) {
         this.hasArg = var1;
         this.aliases = var2;
      }

      boolean isHidden() {
         return false;
      }

      boolean matches(String var1) {
         String[] var2 = this.aliases;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            if (var5.equals(var1)) {
               return true;
            }

            if (this.hasArg && var1.startsWith(var5 + "=")) {
               return true;
            }
         }

         return false;
      }

      boolean ignoreRest() {
         return false;
      }

      abstract void process(JdepsTask var1, String var2, String var3) throws BadArgs;
   }

   static class BadArgs extends Exception {
      static final long serialVersionUID = 8765093759964640721L;
      final String key;
      final Object[] args;
      boolean showUsage;

      BadArgs(String var1, Object... var2) {
         super(JdepsTask.getMessage(var1, var2));
         this.key = var1;
         this.args = var2;
      }

      BadArgs showUsage(boolean var1) {
         this.showUsage = var1;
         return this;
      }
   }
}
