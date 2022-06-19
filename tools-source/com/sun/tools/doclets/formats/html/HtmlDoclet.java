package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.tools.doclets.internal.toolkit.AbstractDoclet;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.builders.AbstractBuilder;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.DocFile;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.FatalError;
import com.sun.tools.doclets.internal.toolkit.util.IndexBuilder;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.javac.jvm.Profile;
import java.io.IOException;
import java.util.Arrays;

public class HtmlDoclet extends AbstractDoclet {
   private static HtmlDoclet docletToStart = null;
   public final ConfigurationImpl configuration = new ConfigurationImpl();
   public static final ConfigurationImpl sharedInstanceForOptions = new ConfigurationImpl();

   public static boolean start(RootDoc var0) {
      HtmlDoclet var1;
      if (docletToStart != null) {
         var1 = docletToStart;
         docletToStart = null;
      } else {
         var1 = new HtmlDoclet();
      }

      return var1.start(var1, var0);
   }

   public Configuration configuration() {
      return this.configuration;
   }

   protected void generateOtherFiles(RootDoc var1, ClassTree var2) throws Exception {
      super.generateOtherFiles(var1, var2);
      if (this.configuration.linksource) {
         SourceToHTMLConverter.convertRoot(this.configuration, var1, DocPaths.SOURCE_OUTPUT);
      }

      if (this.configuration.topFile.isEmpty()) {
         this.configuration.standardmessage.error("doclet.No_Non_Deprecated_Classes_To_Document");
      } else {
         boolean var3 = this.configuration.nodeprecated;
         this.performCopy(this.configuration.helpfile);
         this.performCopy(this.configuration.stylesheetfile);
         if (this.configuration.classuse) {
            ClassUseWriter.generate(this.configuration, var2);
         }

         IndexBuilder var4 = new IndexBuilder(this.configuration, var3);
         if (this.configuration.createtree) {
            TreeWriter.generate(this.configuration, var2);
         }

         if (this.configuration.createindex) {
            if (this.configuration.splitindex) {
               SplitIndexWriter.generate(this.configuration, var4);
            } else {
               SingleIndexWriter.generate(this.configuration, var4);
            }
         }

         if (!this.configuration.nodeprecatedlist && !var3) {
            DeprecatedListWriter.generate(this.configuration);
         }

         AllClassesFrameWriter.generate(this.configuration, new IndexBuilder(this.configuration, var3, true));
         FrameOutputWriter.generate(this.configuration);
         if (this.configuration.createoverview) {
            PackageIndexWriter.generate(this.configuration);
         }

         if (this.configuration.helpfile.length() == 0 && !this.configuration.nohelp) {
            HelpWriter.generate(this.configuration);
         }

         DocFile var5;
         if (this.configuration.stylesheetfile.length() == 0) {
            var5 = DocFile.createFileForOutput(this.configuration, DocPaths.STYLESHEET);
            var5.copyResource(DocPaths.RESOURCES.resolve(DocPaths.STYLESHEET), false, true);
         }

         var5 = DocFile.createFileForOutput(this.configuration, DocPaths.JAVASCRIPT);
         var5.copyResource(DocPaths.RESOURCES.resolve(DocPaths.JAVASCRIPT), true, true);
      }
   }

   protected void generateClassFiles(ClassDoc[] var1, ClassTree var2) {
      Arrays.sort(var1);

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (this.configuration.isGeneratedDoc(var1[var3]) && var1[var3].isIncluded()) {
            ClassDoc var4 = var3 == 0 ? null : var1[var3 - 1];
            ClassDoc var5 = var1[var3];
            ClassDoc var6 = var3 + 1 == var1.length ? null : var1[var3 + 1];

            try {
               AbstractBuilder var7;
               if (var5.isAnnotationType()) {
                  var7 = this.configuration.getBuilderFactory().getAnnotationTypeBuilder((AnnotationTypeDoc)var5, var4, var6);
                  var7.build();
               } else {
                  var7 = this.configuration.getBuilderFactory().getClassBuilder(var5, var4, var6, var2);
                  var7.build();
               }
            } catch (IOException var8) {
               throw new DocletAbortException(var8);
            } catch (FatalError var9) {
               throw var9;
            } catch (DocletAbortException var10) {
               throw var10;
            } catch (Exception var11) {
               var11.printStackTrace();
               throw new DocletAbortException(var11);
            }
         }
      }

   }

   protected void generateProfileFiles() throws Exception {
      if (this.configuration.showProfiles && this.configuration.profilePackages.size() > 0) {
         ProfileIndexFrameWriter.generate(this.configuration);
         Profile var1 = null;

         for(int var4 = 1; var4 < this.configuration.profiles.getProfileCount(); ++var4) {
            String var3 = Profile.lookup(var4).name;
            if (this.configuration.shouldDocumentProfile(var3)) {
               ProfilePackageIndexFrameWriter.generate(this.configuration, var3);
               PackageDoc[] var5 = (PackageDoc[])this.configuration.profilePackages.get(var3);
               PackageDoc var6 = null;

               for(int var8 = 0; var8 < var5.length; ++var8) {
                  if (!this.configuration.nodeprecated || !Util.isDeprecated(var5[var8])) {
                     ProfilePackageFrameWriter.generate(this.configuration, var5[var8], var4);
                     PackageDoc var7 = var8 + 1 < var5.length && var5[var8 + 1].name().length() > 0 ? var5[var8 + 1] : null;
                     AbstractBuilder var9 = this.configuration.getBuilderFactory().getProfilePackageSummaryBuilder(var5[var8], var6, var7, Profile.lookup(var4));
                     var9.build();
                     var6 = var5[var8];
                  }
               }

               Profile var2 = var4 + 1 < this.configuration.profiles.getProfileCount() ? Profile.lookup(var4 + 1) : null;
               AbstractBuilder var10 = this.configuration.getBuilderFactory().getProfileSummaryBuilder(Profile.lookup(var4), var1, var2);
               var10.build();
               var1 = Profile.lookup(var4);
            }
         }
      }

   }

   protected void generatePackageFiles(ClassTree var1) throws Exception {
      PackageDoc[] var2 = this.configuration.packages;
      if (var2.length > 1) {
         PackageIndexFrameWriter.generate(this.configuration);
      }

      PackageDoc var3 = null;

      for(int var5 = 0; var5 < var2.length; ++var5) {
         if (!this.configuration.nodeprecated || !Util.isDeprecated(var2[var5])) {
            PackageFrameWriter.generate(this.configuration, var2[var5]);
            PackageDoc var4 = var5 + 1 < var2.length && var2[var5 + 1].name().length() > 0 ? var2[var5 + 1] : null;
            var4 = var5 + 2 < var2.length && var4 == null ? var2[var5 + 2] : var4;
            AbstractBuilder var6 = this.configuration.getBuilderFactory().getPackageSummaryBuilder(var2[var5], var3, var4);
            var6.build();
            if (this.configuration.createtree) {
               PackageTreeWriter.generate(this.configuration, var2[var5], var3, var4, this.configuration.nodeprecated);
            }

            var3 = var2[var5];
         }
      }

   }

   public static int optionLength(String var0) {
      return sharedInstanceForOptions.optionLength(var0);
   }

   public static boolean validOptions(String[][] var0, DocErrorReporter var1) {
      docletToStart = new HtmlDoclet();
      return docletToStart.configuration.validOptions(var0, var1);
   }

   private void performCopy(String var1) {
      if (!var1.isEmpty()) {
         try {
            DocFile var2 = DocFile.createFileForInput(this.configuration, var1);
            DocPath var3 = DocPath.create(var2.getName());
            DocFile var4 = DocFile.createFileForOutput(this.configuration, var3);
            if (!var4.isSameFile(var2)) {
               this.configuration.message.notice((SourcePosition)null, "doclet.Copying_File_0_To_File_1", var2.toString(), var3.getPath());
               var4.copyFile(var2);
            }
         } catch (IOException var5) {
            this.configuration.message.error((SourcePosition)null, "doclet.perform_copy_exception_encountered", var5.toString());
            throw new DocletAbortException(var5);
         }
      }
   }
}
