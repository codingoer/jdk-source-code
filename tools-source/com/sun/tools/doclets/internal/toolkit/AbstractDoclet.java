package com.sun.tools.doclets.internal.toolkit;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.tools.doclets.formats.html.HtmlDoclet;
import com.sun.tools.doclets.internal.toolkit.builders.AbstractBuilder;
import com.sun.tools.doclets.internal.toolkit.builders.BuilderFactory;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.FatalError;
import com.sun.tools.doclets.internal.toolkit.util.PackageListWriter;
import com.sun.tools.doclets.internal.toolkit.util.Util;

public abstract class AbstractDoclet {
   public Configuration configuration;
   private static final String TOOLKIT_DOCLET_NAME = HtmlDoclet.class.getName();

   private boolean isValidDoclet(AbstractDoclet var1) {
      if (!var1.getClass().getName().equals(TOOLKIT_DOCLET_NAME)) {
         this.configuration.message.error("doclet.Toolkit_Usage_Violation", TOOLKIT_DOCLET_NAME);
         return false;
      } else {
         return true;
      }
   }

   public boolean start(AbstractDoclet var1, RootDoc var2) {
      this.configuration = this.configuration();
      this.configuration.root = var2;
      if (!this.isValidDoclet(var1)) {
         return false;
      } else {
         try {
            var1.startGeneration(var2);
            return true;
         } catch (Configuration.Fault var5) {
            var2.printError(var5.getMessage());
            return false;
         } catch (FatalError var6) {
            return false;
         } catch (DocletAbortException var7) {
            Throwable var4 = var7.getCause();
            if (var4 != null) {
               if (var4.getLocalizedMessage() != null) {
                  var2.printError(var4.getLocalizedMessage());
               } else {
                  var2.printError(var4.toString());
               }
            }

            return false;
         } catch (Exception var8) {
            var8.printStackTrace();
            return false;
         }
      }
   }

   public static LanguageVersion languageVersion() {
      return LanguageVersion.JAVA_1_5;
   }

   public abstract Configuration configuration();

   private void startGeneration(RootDoc var1) throws Configuration.Fault, Exception {
      if (var1.classes().length == 0) {
         this.configuration.message.error("doclet.No_Public_Classes_To_Document");
      } else {
         this.configuration.setOptions();
         this.configuration.getDocletSpecificMsg().notice("doclet.build_version", this.configuration.getDocletSpecificBuildDate());
         ClassTree var2 = new ClassTree(this.configuration, this.configuration.nodeprecated);
         this.generateClassFiles(var1, var2);
         Util.copyDocFiles(this.configuration, DocPaths.DOC_FILES);
         PackageListWriter.generate(this.configuration);
         this.generatePackageFiles(var2);
         this.generateProfileFiles();
         this.generateOtherFiles(var1, var2);
         this.configuration.tagletManager.printReport();
      }
   }

   protected void generateOtherFiles(RootDoc var1, ClassTree var2) throws Exception {
      BuilderFactory var3 = this.configuration.getBuilderFactory();
      AbstractBuilder var4 = var3.getConstantsSummaryBuider();
      var4.build();
      AbstractBuilder var5 = var3.getSerializedFormBuilder();
      var5.build();
   }

   protected abstract void generateProfileFiles() throws Exception;

   protected abstract void generatePackageFiles(ClassTree var1) throws Exception;

   protected abstract void generateClassFiles(ClassDoc[] var1, ClassTree var2);

   protected void generateClassFiles(RootDoc var1, ClassTree var2) {
      this.generateClassFiles(var2);
      PackageDoc[] var3 = var1.specifiedPackages();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         this.generateClassFiles(var3[var4].allClasses(), var2);
      }

   }

   private void generateClassFiles(ClassTree var1) {
      String[] var2 = this.configuration.classDocCatalog.packageNames();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         this.generateClassFiles(this.configuration.classDocCatalog.allClasses(var2[var3]), var1);
      }

   }
}
