package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.ClassUseMapper;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ClassUseWriter extends SubWriterHolderWriter {
   final ClassDoc classdoc;
   Set pkgToPackageAnnotations = null;
   final Map pkgToClassTypeParameter;
   final Map pkgToClassAnnotations;
   final Map pkgToMethodTypeParameter;
   final Map pkgToMethodArgTypeParameter;
   final Map pkgToMethodReturnTypeParameter;
   final Map pkgToMethodAnnotations;
   final Map pkgToMethodParameterAnnotations;
   final Map pkgToFieldTypeParameter;
   final Map pkgToFieldAnnotations;
   final Map pkgToSubclass;
   final Map pkgToSubinterface;
   final Map pkgToImplementingClass;
   final Map pkgToField;
   final Map pkgToMethodReturn;
   final Map pkgToMethodArgs;
   final Map pkgToMethodThrows;
   final Map pkgToConstructorAnnotations;
   final Map pkgToConstructorParameterAnnotations;
   final Map pkgToConstructorArgs;
   final Map pkgToConstructorArgTypeParameter;
   final Map pkgToConstructorThrows;
   final SortedSet pkgSet;
   final MethodWriterImpl methodSubWriter;
   final ConstructorWriterImpl constrSubWriter;
   final FieldWriterImpl fieldSubWriter;
   final NestedClassWriterImpl classSubWriter;
   final String classUseTableSummary;
   final String subclassUseTableSummary;
   final String subinterfaceUseTableSummary;
   final String fieldUseTableSummary;
   final String methodUseTableSummary;
   final String constructorUseTableSummary;

   public ClassUseWriter(ConfigurationImpl var1, ClassUseMapper var2, DocPath var3, ClassDoc var4) throws IOException {
      super(var1, var3);
      this.classdoc = var4;
      if (var2.classToPackageAnnotations.containsKey(var4.qualifiedName())) {
         this.pkgToPackageAnnotations = new TreeSet((Collection)var2.classToPackageAnnotations.get(var4.qualifiedName()));
      }

      var1.currentcd = var4;
      this.pkgSet = new TreeSet();
      this.pkgToClassTypeParameter = this.pkgDivide(var2.classToClassTypeParam);
      this.pkgToClassAnnotations = this.pkgDivide(var2.classToClassAnnotations);
      this.pkgToMethodTypeParameter = this.pkgDivide(var2.classToExecMemberDocTypeParam);
      this.pkgToMethodArgTypeParameter = this.pkgDivide(var2.classToExecMemberDocArgTypeParam);
      this.pkgToFieldTypeParameter = this.pkgDivide(var2.classToFieldDocTypeParam);
      this.pkgToFieldAnnotations = this.pkgDivide(var2.annotationToFieldDoc);
      this.pkgToMethodReturnTypeParameter = this.pkgDivide(var2.classToExecMemberDocReturnTypeParam);
      this.pkgToMethodAnnotations = this.pkgDivide(var2.classToExecMemberDocAnnotations);
      this.pkgToMethodParameterAnnotations = this.pkgDivide(var2.classToExecMemberDocParamAnnotation);
      this.pkgToSubclass = this.pkgDivide(var2.classToSubclass);
      this.pkgToSubinterface = this.pkgDivide(var2.classToSubinterface);
      this.pkgToImplementingClass = this.pkgDivide(var2.classToImplementingClass);
      this.pkgToField = this.pkgDivide(var2.classToField);
      this.pkgToMethodReturn = this.pkgDivide(var2.classToMethodReturn);
      this.pkgToMethodArgs = this.pkgDivide(var2.classToMethodArgs);
      this.pkgToMethodThrows = this.pkgDivide(var2.classToMethodThrows);
      this.pkgToConstructorAnnotations = this.pkgDivide(var2.classToConstructorAnnotations);
      this.pkgToConstructorParameterAnnotations = this.pkgDivide(var2.classToConstructorParamAnnotation);
      this.pkgToConstructorArgs = this.pkgDivide(var2.classToConstructorArgs);
      this.pkgToConstructorArgTypeParameter = this.pkgDivide(var2.classToConstructorDocArgTypeParam);
      this.pkgToConstructorThrows = this.pkgDivide(var2.classToConstructorThrows);
      if (this.pkgSet.size() > 0 && var2.classToPackage.containsKey(var4.qualifiedName()) && !this.pkgSet.equals(var2.classToPackage.get(var4.qualifiedName()))) {
         var1.root.printWarning("Internal error: package sets don't match: " + this.pkgSet + " with: " + var2.classToPackage.get(var4.qualifiedName()));
      }

      this.methodSubWriter = new MethodWriterImpl(this);
      this.constrSubWriter = new ConstructorWriterImpl(this);
      this.fieldSubWriter = new FieldWriterImpl(this);
      this.classSubWriter = new NestedClassWriterImpl(this);
      this.classUseTableSummary = var1.getText("doclet.Use_Table_Summary", var1.getText("doclet.classes"));
      this.subclassUseTableSummary = var1.getText("doclet.Use_Table_Summary", var1.getText("doclet.subclasses"));
      this.subinterfaceUseTableSummary = var1.getText("doclet.Use_Table_Summary", var1.getText("doclet.subinterfaces"));
      this.fieldUseTableSummary = var1.getText("doclet.Use_Table_Summary", var1.getText("doclet.fields"));
      this.methodUseTableSummary = var1.getText("doclet.Use_Table_Summary", var1.getText("doclet.methods"));
      this.constructorUseTableSummary = var1.getText("doclet.Use_Table_Summary", var1.getText("doclet.constructors"));
   }

   public static void generate(ConfigurationImpl var0, ClassTree var1) {
      ClassUseMapper var2 = new ClassUseMapper(var0.root, var1);
      ClassDoc[] var3 = var0.root.classes();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (!var0.nodeprecated || !Util.isDeprecated(var3[var4].containingPackage())) {
            generate(var0, var2, var3[var4]);
         }
      }

      PackageDoc[] var6 = var0.packages;

      for(int var5 = 0; var5 < var6.length; ++var5) {
         if (!var0.nodeprecated || !Util.isDeprecated(var6[var5])) {
            PackageUseWriter.generate(var0, var2, var6[var5]);
         }
      }

   }

   private Map pkgDivide(Map var1) {
      HashMap var2 = new HashMap();
      List var3 = (List)var1.get(this.classdoc.qualifiedName());
      if (var3 != null) {
         Collections.sort(var3);

         ProgramElementDoc var5;
         Object var7;
         for(Iterator var4 = var3.iterator(); var4.hasNext(); ((List)var7).add(var5)) {
            var5 = (ProgramElementDoc)var4.next();
            PackageDoc var6 = var5.containingPackage();
            this.pkgSet.add(var6);
            var7 = (List)var2.get(var6.name());
            if (var7 == null) {
               var7 = new ArrayList();
               var2.put(var6.name(), var7);
            }
         }
      }

      return var2;
   }

   public static void generate(ConfigurationImpl var0, ClassUseMapper var1, ClassDoc var2) {
      DocPath var4 = DocPath.forPackage(var2).resolve(DocPaths.CLASS_USE).resolve(DocPath.forName(var2));

      try {
         ClassUseWriter var3 = new ClassUseWriter(var0, var1, var4, var2);
         var3.generateClassUseFile();
         var3.close();
      } catch (IOException var6) {
         var0.standardmessage.error("doclet.exception_encountered", var6.toString(), var4.getPath());
         throw new DocletAbortException(var6);
      }
   }

   protected void generateClassUseFile() throws IOException {
      Content var1 = this.getClassUseHeader();
      HtmlTree var2 = new HtmlTree(HtmlTag.DIV);
      var2.addStyle(HtmlStyle.classUseContainer);
      if (this.pkgSet.size() > 0) {
         this.addClassUse(var2);
      } else {
         var2.addContent(this.getResource("doclet.ClassUse_No.usage.of.0", this.classdoc.qualifiedName()));
      }

      var1.addContent((Content)var2);
      this.addNavLinks(false, var1);
      this.addBottom(var1);
      this.printHtmlDocument((String[])null, true, var1);
   }

   protected void addClassUse(Content var1) throws IOException {
      HtmlTree var2 = new HtmlTree(HtmlTag.UL);
      var2.addStyle(HtmlStyle.blockList);
      if (this.configuration.packages.length > 1) {
         this.addPackageList(var2);
         this.addPackageAnnotationList(var2);
      }

      this.addClassList(var2);
      var1.addContent((Content)var2);
   }

   protected void addPackageList(Content var1) throws IOException {
      HtmlTree var2 = HtmlTree.TABLE(HtmlStyle.useSummary, 0, 3, 0, this.useTableSummary, this.getTableCaption(this.configuration.getResource("doclet.ClassUse_Packages.that.use.0", this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_USE_HEADER, this.classdoc)))));
      var2.addContent(this.getSummaryTableHeader(this.packageTableHeader, "col"));
      HtmlTree var3 = new HtmlTree(HtmlTag.TBODY);
      Iterator var4 = this.pkgSet.iterator();

      for(int var5 = 0; var4.hasNext(); ++var5) {
         PackageDoc var6 = (PackageDoc)var4.next();
         HtmlTree var7 = new HtmlTree(HtmlTag.TR);
         if (var5 % 2 == 0) {
            var7.addStyle(HtmlStyle.altColor);
         } else {
            var7.addStyle(HtmlStyle.rowColor);
         }

         this.addPackageUse(var6, var7);
         var3.addContent((Content)var7);
      }

      var2.addContent((Content)var3);
      HtmlTree var8 = HtmlTree.LI(HtmlStyle.blockList, var2);
      var1.addContent((Content)var8);
   }

   protected void addPackageAnnotationList(Content var1) throws IOException {
      if (this.classdoc.isAnnotationType() && this.pkgToPackageAnnotations != null && !this.pkgToPackageAnnotations.isEmpty()) {
         HtmlTree var2 = HtmlTree.TABLE(HtmlStyle.useSummary, 0, 3, 0, this.useTableSummary, this.getTableCaption(this.configuration.getResource("doclet.ClassUse_PackageAnnotation", this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_USE_HEADER, this.classdoc)))));
         var2.addContent(this.getSummaryTableHeader(this.packageTableHeader, "col"));
         HtmlTree var3 = new HtmlTree(HtmlTag.TBODY);
         Iterator var4 = this.pkgToPackageAnnotations.iterator();

         for(int var5 = 0; var4.hasNext(); ++var5) {
            PackageDoc var6 = (PackageDoc)var4.next();
            HtmlTree var7 = new HtmlTree(HtmlTag.TR);
            if (var5 % 2 == 0) {
               var7.addStyle(HtmlStyle.altColor);
            } else {
               var7.addStyle(HtmlStyle.rowColor);
            }

            HtmlTree var8 = HtmlTree.TD(HtmlStyle.colFirst, this.getPackageLink(var6, new StringContent(var6.name())));
            var7.addContent((Content)var8);
            HtmlTree var9 = new HtmlTree(HtmlTag.TD);
            var9.addStyle(HtmlStyle.colLast);
            this.addSummaryComment(var6, var9);
            var7.addContent((Content)var9);
            var3.addContent((Content)var7);
         }

         var2.addContent((Content)var3);
         HtmlTree var10 = HtmlTree.LI(HtmlStyle.blockList, var2);
         var1.addContent((Content)var10);
      }
   }

   protected void addClassList(Content var1) throws IOException {
      HtmlTree var2 = new HtmlTree(HtmlTag.UL);
      var2.addStyle(HtmlStyle.blockList);
      Iterator var3 = this.pkgSet.iterator();

      while(var3.hasNext()) {
         PackageDoc var4 = (PackageDoc)var3.next();
         HtmlTree var5 = HtmlTree.LI(HtmlStyle.blockList, this.getMarkerAnchor(var4.name()));
         Content var6 = this.getResource("doclet.ClassUse_Uses.of.0.in.1", this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_USE_HEADER, this.classdoc)), this.getPackageLink(var4, Util.getPackageName(var4)));
         HtmlTree var7 = HtmlTree.HEADING(HtmlConstants.SUMMARY_HEADING, var6);
         var5.addContent((Content)var7);
         this.addClassUse(var4, var5);
         var2.addContent((Content)var5);
      }

      HtmlTree var8 = HtmlTree.LI(HtmlStyle.blockList, var2);
      var1.addContent((Content)var8);
   }

   protected void addPackageUse(PackageDoc var1, Content var2) throws IOException {
      HtmlTree var3 = HtmlTree.TD(HtmlStyle.colFirst, this.getHyperLink(var1.name(), new StringContent(Util.getPackageName(var1))));
      var2.addContent((Content)var3);
      HtmlTree var4 = new HtmlTree(HtmlTag.TD);
      var4.addStyle(HtmlStyle.colLast);
      this.addSummaryComment(var1, var4);
      var2.addContent((Content)var4);
   }

   protected void addClassUse(PackageDoc var1, Content var2) throws IOException {
      Content var3 = this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_USE_HEADER, this.classdoc));
      Content var4 = this.getPackageLink(var1, Util.getPackageName(var1));
      this.classSubWriter.addUseInfo((List)this.pkgToClassAnnotations.get(var1.name()), this.configuration.getResource("doclet.ClassUse_Annotation", var3, var4), this.classUseTableSummary, var2);
      this.classSubWriter.addUseInfo((List)this.pkgToClassTypeParameter.get(var1.name()), this.configuration.getResource("doclet.ClassUse_TypeParameter", var3, var4), this.classUseTableSummary, var2);
      this.classSubWriter.addUseInfo((List)this.pkgToSubclass.get(var1.name()), this.configuration.getResource("doclet.ClassUse_Subclass", var3, var4), this.subclassUseTableSummary, var2);
      this.classSubWriter.addUseInfo((List)this.pkgToSubinterface.get(var1.name()), this.configuration.getResource("doclet.ClassUse_Subinterface", var3, var4), this.subinterfaceUseTableSummary, var2);
      this.classSubWriter.addUseInfo((List)this.pkgToImplementingClass.get(var1.name()), this.configuration.getResource("doclet.ClassUse_ImplementingClass", var3, var4), this.classUseTableSummary, var2);
      this.fieldSubWriter.addUseInfo((List)this.pkgToField.get(var1.name()), this.configuration.getResource("doclet.ClassUse_Field", var3, var4), this.fieldUseTableSummary, var2);
      this.fieldSubWriter.addUseInfo((List)this.pkgToFieldAnnotations.get(var1.name()), this.configuration.getResource("doclet.ClassUse_FieldAnnotations", var3, var4), this.fieldUseTableSummary, var2);
      this.fieldSubWriter.addUseInfo((List)this.pkgToFieldTypeParameter.get(var1.name()), this.configuration.getResource("doclet.ClassUse_FieldTypeParameter", var3, var4), this.fieldUseTableSummary, var2);
      this.methodSubWriter.addUseInfo((List)this.pkgToMethodAnnotations.get(var1.name()), this.configuration.getResource("doclet.ClassUse_MethodAnnotations", var3, var4), this.methodUseTableSummary, var2);
      this.methodSubWriter.addUseInfo((List)this.pkgToMethodParameterAnnotations.get(var1.name()), this.configuration.getResource("doclet.ClassUse_MethodParameterAnnotations", var3, var4), this.methodUseTableSummary, var2);
      this.methodSubWriter.addUseInfo((List)this.pkgToMethodTypeParameter.get(var1.name()), this.configuration.getResource("doclet.ClassUse_MethodTypeParameter", var3, var4), this.methodUseTableSummary, var2);
      this.methodSubWriter.addUseInfo((List)this.pkgToMethodReturn.get(var1.name()), this.configuration.getResource("doclet.ClassUse_MethodReturn", var3, var4), this.methodUseTableSummary, var2);
      this.methodSubWriter.addUseInfo((List)this.pkgToMethodReturnTypeParameter.get(var1.name()), this.configuration.getResource("doclet.ClassUse_MethodReturnTypeParameter", var3, var4), this.methodUseTableSummary, var2);
      this.methodSubWriter.addUseInfo((List)this.pkgToMethodArgs.get(var1.name()), this.configuration.getResource("doclet.ClassUse_MethodArgs", var3, var4), this.methodUseTableSummary, var2);
      this.methodSubWriter.addUseInfo((List)this.pkgToMethodArgTypeParameter.get(var1.name()), this.configuration.getResource("doclet.ClassUse_MethodArgsTypeParameters", var3, var4), this.methodUseTableSummary, var2);
      this.methodSubWriter.addUseInfo((List)this.pkgToMethodThrows.get(var1.name()), this.configuration.getResource("doclet.ClassUse_MethodThrows", var3, var4), this.methodUseTableSummary, var2);
      this.constrSubWriter.addUseInfo((List)this.pkgToConstructorAnnotations.get(var1.name()), this.configuration.getResource("doclet.ClassUse_ConstructorAnnotations", var3, var4), this.constructorUseTableSummary, var2);
      this.constrSubWriter.addUseInfo((List)this.pkgToConstructorParameterAnnotations.get(var1.name()), this.configuration.getResource("doclet.ClassUse_ConstructorParameterAnnotations", var3, var4), this.constructorUseTableSummary, var2);
      this.constrSubWriter.addUseInfo((List)this.pkgToConstructorArgs.get(var1.name()), this.configuration.getResource("doclet.ClassUse_ConstructorArgs", var3, var4), this.constructorUseTableSummary, var2);
      this.constrSubWriter.addUseInfo((List)this.pkgToConstructorArgTypeParameter.get(var1.name()), this.configuration.getResource("doclet.ClassUse_ConstructorArgsTypeParameters", var3, var4), this.constructorUseTableSummary, var2);
      this.constrSubWriter.addUseInfo((List)this.pkgToConstructorThrows.get(var1.name()), this.configuration.getResource("doclet.ClassUse_ConstructorThrows", var3, var4), this.constructorUseTableSummary, var2);
   }

   protected Content getClassUseHeader() {
      String var1 = this.configuration.getText(this.classdoc.isInterface() ? "doclet.Interface" : "doclet.Class");
      String var2 = this.classdoc.qualifiedName();
      String var3 = this.configuration.getText("doclet.Window_ClassUse_Header", var1, var2);
      HtmlTree var4 = this.getBody(true, this.getWindowTitle(var3));
      this.addTop(var4);
      this.addNavLinks(true, var4);
      ContentBuilder var5 = new ContentBuilder();
      var5.addContent(this.getResource("doclet.ClassUse_Title", var1));
      var5.addContent((Content)(new HtmlTree(HtmlTag.BR)));
      var5.addContent(var2);
      HtmlTree var6 = HtmlTree.HEADING(HtmlConstants.CLASS_PAGE_HEADING, true, HtmlStyle.title, var5);
      HtmlTree var7 = HtmlTree.DIV(HtmlStyle.header, var6);
      var4.addContent((Content)var7);
      return var4;
   }

   protected Content getNavLinkPackage() {
      Content var1 = this.getHyperLink(DocPath.parent.resolve(DocPaths.PACKAGE_SUMMARY), this.packageLabel);
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected Content getNavLinkClass() {
      Content var1 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_USE_HEADER, this.classdoc)).label(this.configuration.getText("doclet.Class")));
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected Content getNavLinkClassUse() {
      HtmlTree var1 = HtmlTree.LI(HtmlStyle.navBarCell1Rev, this.useLabel);
      return var1;
   }

   protected Content getNavLinkTree() {
      Content var1 = this.classdoc.containingPackage().isIncluded() ? this.getHyperLink(DocPath.parent.resolve(DocPaths.PACKAGE_TREE), this.treeLabel) : this.getHyperLink(this.pathToRoot.resolve(DocPaths.OVERVIEW_TREE), this.treeLabel);
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }
}
