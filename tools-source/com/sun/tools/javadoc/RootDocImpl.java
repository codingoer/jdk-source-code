package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.SourcePosition;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Position;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

public class RootDocImpl extends DocImpl implements RootDoc {
   private List cmdLineClasses;
   private List cmdLinePackages;
   private List options;

   public RootDocImpl(DocEnv var1, List var2, List var3, List var4) {
      super(var1, (TreePath)null);
      this.options = var4;
      this.setPackages(var1, var3);
      this.setClasses(var1, var2);
   }

   public RootDocImpl(DocEnv var1, List var2, List var3) {
      super(var1, (TreePath)null);
      this.options = var3;
      this.cmdLinePackages = List.nil();
      ListBuffer var4 = new ListBuffer();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         ClassDocImpl var7 = var1.loadClass(var6);
         if (var7 == null) {
            var1.error((DocImpl)null, "javadoc.class_not_found", var6);
         } else {
            var4 = var4.append(var7);
         }
      }

      this.cmdLineClasses = var4.toList();
   }

   private void setClasses(DocEnv var1, List var2) {
      ListBuffer var3 = new ListBuffer();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         JCTree.JCClassDecl var5 = (JCTree.JCClassDecl)var4.next();
         if (var1.shouldDocument(var5.sym)) {
            ClassDocImpl var6 = var1.getClassDoc(var5.sym);
            if (var6 != null) {
               var6.isIncluded = true;
               var3.append(var6);
            }
         }
      }

      this.cmdLineClasses = var3.toList();
   }

   private void setPackages(DocEnv var1, List var2) {
      ListBuffer var3 = new ListBuffer();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         PackageDocImpl var6 = var1.lookupPackage(var5);
         if (var6 != null) {
            var6.isIncluded = true;
            var3.append(var6);
         } else {
            var1.warning((DocImpl)null, "main.no_source_files_for_package", var5);
         }
      }

      this.cmdLinePackages = var3.toList();
   }

   public String[][] options() {
      return (String[][])this.options.toArray(new String[this.options.length()][]);
   }

   public PackageDoc[] specifiedPackages() {
      return (PackageDoc[])((PackageDoc[])this.cmdLinePackages.toArray(new PackageDocImpl[this.cmdLinePackages.length()]));
   }

   public ClassDoc[] specifiedClasses() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.cmdLineClasses.iterator();

      while(var2.hasNext()) {
         ClassDocImpl var3 = (ClassDocImpl)var2.next();
         var3.addAllClasses(var1, true);
      }

      return (ClassDoc[])((ClassDoc[])var1.toArray(new ClassDocImpl[var1.length()]));
   }

   public ClassDoc[] classes() {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = this.cmdLineClasses.iterator();

      while(var2.hasNext()) {
         ClassDocImpl var3 = (ClassDocImpl)var2.next();
         var3.addAllClasses(var1, true);
      }

      var2 = this.cmdLinePackages.iterator();

      while(var2.hasNext()) {
         PackageDocImpl var4 = (PackageDocImpl)var2.next();
         var4.addAllClassesTo(var1);
      }

      return (ClassDoc[])var1.toArray(new ClassDocImpl[var1.length()]);
   }

   public ClassDoc classNamed(String var1) {
      return this.env.lookupClass(var1);
   }

   public PackageDoc packageNamed(String var1) {
      return this.env.lookupPackage(var1);
   }

   public String name() {
      return "*RootDocImpl*";
   }

   public String qualifiedName() {
      return "*RootDocImpl*";
   }

   public boolean isIncluded() {
      return false;
   }

   public void printError(String var1) {
      this.env.printError(var1);
   }

   public void printError(SourcePosition var1, String var2) {
      this.env.printError(var1, var2);
   }

   public void printWarning(String var1) {
      this.env.printWarning(var1);
   }

   public void printWarning(SourcePosition var1, String var2) {
      this.env.printWarning(var1, var2);
   }

   public void printNotice(String var1) {
      this.env.printNotice(var1);
   }

   public void printNotice(SourcePosition var1, String var2) {
      this.env.printNotice(var1, var2);
   }

   private JavaFileObject getOverviewPath() {
      Iterator var1 = this.options.iterator();

      String[] var2;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         var2 = (String[])var1.next();
      } while(!var2[0].equals("-overview") || !(this.env.fileManager instanceof StandardJavaFileManager));

      StandardJavaFileManager var3 = (StandardJavaFileManager)this.env.fileManager;
      return (JavaFileObject)var3.getJavaFileObjects(new String[]{var2[1]}).iterator().next();
   }

   protected String documentation() {
      if (this.documentation == null) {
         JavaFileObject var1 = this.getOverviewPath();
         if (var1 == null) {
            this.documentation = "";
         } else {
            try {
               this.documentation = this.readHTMLDocumentation(var1.openInputStream(), var1);
            } catch (IOException var3) {
               this.documentation = "";
               this.env.error((DocImpl)null, "javadoc.File_Read_Error", var1.getName());
            }
         }
      }

      return this.documentation;
   }

   public SourcePosition position() {
      JavaFileObject var1;
      return (var1 = this.getOverviewPath()) == null ? null : SourcePositionImpl.make(var1, -1, (Position.LineMap)null);
   }

   public Locale getLocale() {
      return this.env.doclocale.locale;
   }

   public JavaFileManager getFileManager() {
      return this.env.fileManager;
   }

   public void initDocLint(Collection var1, Collection var2) {
      this.env.initDoclint(var1, var2);
   }

   public JavaScriptScanner initJavaScriptScanner(boolean var1) {
      return this.env.initJavaScriptScanner(var1);
   }

   public boolean isFunctionalInterface(AnnotationDesc var1) {
      return var1.annotationType().qualifiedName().equals(this.env.syms.functionalInterfaceType.toString()) && this.env.source.allowLambda();
   }

   public boolean showTagMessages() {
      return this.env.showTagMessages();
   }
}
