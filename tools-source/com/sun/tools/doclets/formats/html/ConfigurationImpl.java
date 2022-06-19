package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.WriterFactory;
import com.sun.tools.doclets.internal.toolkit.util.DocFile;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.FatalError;
import com.sun.tools.doclets.internal.toolkit.util.MessageRetriever;
import com.sun.tools.doclint.DocLint;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.StringUtils;
import com.sun.tools.javadoc.JavaScriptScanner;
import com.sun.tools.javadoc.RootDocImpl;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.tools.JavaFileManager;

public class ConfigurationImpl extends Configuration {
   public static final String BUILD_DATE = System.getProperty("java.version");
   public String header = "";
   public String packagesheader = "";
   public String footer = "";
   public String doctitle = "";
   public String windowtitle = "";
   public String top = "";
   public String bottom = "";
   public String helpfile = "";
   public String stylesheetfile = "";
   public String docrootparent = "";
   public boolean nohelp = false;
   public boolean splitindex = false;
   public boolean createindex = true;
   public boolean classuse = false;
   public boolean createtree = true;
   public boolean nodeprecatedlist = false;
   public boolean nonavbar = false;
   private boolean nooverview = false;
   public boolean overview = false;
   public boolean createoverview = false;
   public Set doclintOpts = new LinkedHashSet();
   private boolean allowScriptInComments;
   public final MessageRetriever standardmessage;
   public DocPath topFile;
   public ClassDoc currentcd;
   private final String versionRBName;
   private ResourceBundle versionRB;
   private JavaFileManager fileManager;

   public ConfigurationImpl() {
      this.topFile = DocPath.empty;
      this.currentcd = null;
      this.versionRBName = "com.sun.tools.javadoc.resources.version";
      this.standardmessage = new MessageRetriever(this, "com.sun.tools.doclets.formats.html.resources.standard");
   }

   public String getDocletSpecificBuildDate() {
      if (this.versionRB == null) {
         try {
            this.versionRB = ResourceBundle.getBundle("com.sun.tools.javadoc.resources.version");
         } catch (MissingResourceException var3) {
            return BUILD_DATE;
         }
      }

      try {
         return this.versionRB.getString("release");
      } catch (MissingResourceException var2) {
         return BUILD_DATE;
      }
   }

   public void setSpecificDocletOptions(String[][] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         String[] var3 = var1[var2];
         String var4 = StringUtils.toLowerCase(var3[0]);
         if (var4.equals("-footer")) {
            this.footer = var3[1];
         } else if (var4.equals("-header")) {
            this.header = var3[1];
         } else if (var4.equals("-packagesheader")) {
            this.packagesheader = var3[1];
         } else if (var4.equals("-doctitle")) {
            this.doctitle = var3[1];
         } else if (var4.equals("-windowtitle")) {
            this.windowtitle = var3[1].replaceAll("\\<.*?>", "");
         } else if (var4.equals("-top")) {
            this.top = var3[1];
         } else if (var4.equals("-bottom")) {
            this.bottom = var3[1];
         } else if (var4.equals("-helpfile")) {
            this.helpfile = var3[1];
         } else if (var4.equals("-stylesheetfile")) {
            this.stylesheetfile = var3[1];
         } else if (var4.equals("-charset")) {
            this.charset = var3[1];
         } else if (var4.equals("-xdocrootparent")) {
            this.docrootparent = var3[1];
         } else if (var4.equals("-nohelp")) {
            this.nohelp = true;
         } else if (var4.equals("-splitindex")) {
            this.splitindex = true;
         } else if (var4.equals("-noindex")) {
            this.createindex = false;
         } else if (var4.equals("-use")) {
            this.classuse = true;
         } else if (var4.equals("-notree")) {
            this.createtree = false;
         } else if (var4.equals("-nodeprecatedlist")) {
            this.nodeprecatedlist = true;
         } else if (var4.equals("-nonavbar")) {
            this.nonavbar = true;
         } else if (var4.equals("-nooverview")) {
            this.nooverview = true;
         } else if (var4.equals("-overview")) {
            this.overview = true;
         } else if (var4.equals("-xdoclint")) {
            this.doclintOpts.add((Object)null);
         } else if (var4.startsWith("-xdoclint:")) {
            this.doclintOpts.add(var4.substring(var4.indexOf(":") + 1));
         } else if (var4.equals("--allow-script-in-comments")) {
            this.allowScriptInComments = true;
         }
      }

      if (this.root.specifiedClasses().length > 0) {
         HashMap var6 = new HashMap();
         ClassDoc[] var9 = this.root.classes();

         for(int var5 = 0; var5 < var9.length; ++var5) {
            PackageDoc var8 = var9[var5].containingPackage();
            if (!var6.containsKey(var8.name())) {
               var6.put(var8.name(), var8);
            }
         }
      }

      this.setCreateOverview();
      this.setTopFile(this.root);
      if (this.root instanceof RootDocImpl) {
         ((RootDocImpl)this.root).initDocLint(this.doclintOpts, this.tagletManager.getCustomTagNames());
         JavaScriptScanner var7 = ((RootDocImpl)this.root).initJavaScriptScanner(this.isAllowScriptInComments());
         if (var7 != null) {
            this.checkJavaScript(var7, "-header", this.header);
            this.checkJavaScript(var7, "-footer", this.footer);
            this.checkJavaScript(var7, "-top", this.top);
            this.checkJavaScript(var7, "-bottom", this.bottom);
            this.checkJavaScript(var7, "-doctitle", this.doctitle);
            this.checkJavaScript(var7, "-packagesheader", this.packagesheader);
         }
      }

   }

   private void checkJavaScript(JavaScriptScanner var1, final String var2, String var3) {
      var1.parse(var3, new JavaScriptScanner.Reporter() {
         public void report() {
            ConfigurationImpl.this.root.printError(ConfigurationImpl.this.getText("doclet.JavaScript_in_option", var2));
            throw new FatalError();
         }
      });
   }

   public int optionLength(String var1) {
      boolean var2 = true;
      int var3;
      if ((var3 = super.optionLength(var1)) > 0) {
         return var3;
      } else {
         var1 = StringUtils.toLowerCase(var1);
         if (!var1.equals("-nodeprecatedlist") && !var1.equals("-noindex") && !var1.equals("-notree") && !var1.equals("-nohelp") && !var1.equals("-splitindex") && !var1.equals("-serialwarn") && !var1.equals("-use") && !var1.equals("-nonavbar") && !var1.equals("-nooverview") && !var1.equals("-xdoclint") && !var1.startsWith("-xdoclint:") && !var1.equals("--allow-script-in-comments")) {
            if (var1.equals("-help")) {
               System.out.println(this.getText("doclet.usage"));
               return 1;
            } else if (var1.equals("-x")) {
               System.out.println(this.getText("doclet.X.usage"));
               return 1;
            } else {
               return !var1.equals("-footer") && !var1.equals("-header") && !var1.equals("-packagesheader") && !var1.equals("-doctitle") && !var1.equals("-windowtitle") && !var1.equals("-top") && !var1.equals("-bottom") && !var1.equals("-helpfile") && !var1.equals("-stylesheetfile") && !var1.equals("-charset") && !var1.equals("-overview") && !var1.equals("-xdocrootparent") ? 0 : 2;
            }
         } else {
            return 1;
         }
      }
   }

   public boolean validOptions(String[][] var1, DocErrorReporter var2) {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      boolean var7 = false;
      boolean var8 = false;
      if (!this.generalValidOptions(var1, var2)) {
         return false;
      } else {
         for(int var9 = 0; var9 < var1.length; ++var9) {
            String[] var10 = var1[var9];
            String var11 = StringUtils.toLowerCase(var10[0]);
            if (var11.equals("-helpfile")) {
               if (var4) {
                  var2.printError(this.getText("doclet.Option_conflict", "-helpfile", "-nohelp"));
                  return false;
               }

               if (var3) {
                  var2.printError(this.getText("doclet.Option_reuse", "-helpfile"));
                  return false;
               }

               DocFile var12 = DocFile.createFileForInput(this, var10[1]);
               if (!var12.exists()) {
                  var2.printError(this.getText("doclet.File_not_found", var10[1]));
                  return false;
               }

               var3 = true;
            } else if (var11.equals("-nohelp")) {
               if (var3) {
                  var2.printError(this.getText("doclet.Option_conflict", "-nohelp", "-helpfile"));
                  return false;
               }

               var4 = true;
            } else if (var11.equals("-xdocrootparent")) {
               try {
                  new URL(var10[1]);
               } catch (MalformedURLException var13) {
                  var2.printError(this.getText("doclet.MalformedURL", var10[1]));
                  return false;
               }
            } else if (var11.equals("-overview")) {
               if (var6) {
                  var2.printError(this.getText("doclet.Option_conflict", "-overview", "-nooverview"));
                  return false;
               }

               if (var5) {
                  var2.printError(this.getText("doclet.Option_reuse", "-overview"));
                  return false;
               }

               var5 = true;
            } else if (var11.equals("-nooverview")) {
               if (var5) {
                  var2.printError(this.getText("doclet.Option_conflict", "-nooverview", "-overview"));
                  return false;
               }

               var6 = true;
            } else if (var11.equals("-splitindex")) {
               if (var8) {
                  var2.printError(this.getText("doclet.Option_conflict", "-splitindex", "-noindex"));
                  return false;
               }

               var7 = true;
            } else if (var11.equals("-noindex")) {
               if (var7) {
                  var2.printError(this.getText("doclet.Option_conflict", "-noindex", "-splitindex"));
                  return false;
               }

               var8 = true;
            } else if (var11.startsWith("-xdoclint:")) {
               if (var11.contains("/")) {
                  var2.printError(this.getText("doclet.Option_doclint_no_qualifiers"));
                  return false;
               }

               if (!DocLint.isValidOption(var11.replace("-xdoclint:", "-Xmsgs:"))) {
                  var2.printError(this.getText("doclet.Option_doclint_invalid_arg"));
                  return false;
               }
            }
         }

         return true;
      }
   }

   public MessageRetriever getDocletSpecificMsg() {
      return this.standardmessage;
   }

   protected void setTopFile(RootDoc var1) {
      if (this.checkForDeprecation(var1)) {
         if (this.createoverview) {
            this.topFile = DocPaths.OVERVIEW_SUMMARY;
         } else if (this.packages.length == 1 && this.packages[0].name().equals("")) {
            if (var1.classes().length > 0) {
               ClassDoc[] var2 = var1.classes();
               Arrays.sort(var2);
               ClassDoc var3 = this.getValidClass(var2);
               this.topFile = DocPath.forClass(var3);
            }
         } else {
            this.topFile = DocPath.forPackage(this.packages[0]).resolve(DocPaths.PACKAGE_SUMMARY);
         }

      }
   }

   protected ClassDoc getValidClass(ClassDoc[] var1) {
      if (!this.nodeprecated) {
         return var1[0];
      } else {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2].tags("deprecated").length == 0) {
               return var1[var2];
            }
         }

         return null;
      }
   }

   protected boolean checkForDeprecation(RootDoc var1) {
      ClassDoc[] var2 = var1.classes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (this.isGeneratedDoc(var2[var3])) {
            return true;
         }
      }

      return false;
   }

   protected void setCreateOverview() {
      if ((this.overview || this.packages.length > 1) && !this.nooverview) {
         this.createoverview = true;
      }

   }

   public WriterFactory getWriterFactory() {
      return new WriterFactoryImpl(this);
   }

   public Comparator getMemberComparator() {
      return null;
   }

   public Locale getLocale() {
      return this.root instanceof RootDocImpl ? ((RootDocImpl)this.root).getLocale() : Locale.getDefault();
   }

   public JavaFileManager getFileManager() {
      if (this.fileManager == null) {
         if (this.root instanceof RootDocImpl) {
            this.fileManager = ((RootDocImpl)this.root).getFileManager();
         } else {
            this.fileManager = new JavacFileManager(new Context(), false, (Charset)null);
         }
      }

      return this.fileManager;
   }

   public boolean showMessage(SourcePosition var1, String var2) {
      if (!(this.root instanceof RootDocImpl)) {
         return true;
      } else {
         return var1 == null || ((RootDocImpl)this.root).showTagMessages();
      }
   }

   public Content newContent() {
      return new ContentBuilder();
   }

   public boolean isAllowScriptInComments() {
      return this.allowScriptInComments;
   }
}
