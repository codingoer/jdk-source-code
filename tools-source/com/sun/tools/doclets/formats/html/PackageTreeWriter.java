package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;

public class PackageTreeWriter extends AbstractTreeWriter {
   protected PackageDoc packagedoc;
   protected PackageDoc prev;
   protected PackageDoc next;

   public PackageTreeWriter(ConfigurationImpl var1, DocPath var2, PackageDoc var3, PackageDoc var4, PackageDoc var5) throws IOException {
      super(var1, var2, new ClassTree(var1.classDocCatalog.allClasses(var3), var1));
      this.packagedoc = var3;
      this.prev = var4;
      this.next = var5;
   }

   public static void generate(ConfigurationImpl var0, PackageDoc var1, PackageDoc var2, PackageDoc var3, boolean var4) {
      DocPath var6 = DocPath.forPackage(var1).resolve(DocPaths.PACKAGE_TREE);

      try {
         PackageTreeWriter var5 = new PackageTreeWriter(var0, var6, var1, var2, var3);
         var5.generatePackageTreeFile();
         var5.close();
      } catch (IOException var8) {
         var0.standardmessage.error("doclet.exception_encountered", var8.toString(), var6.getPath());
         throw new DocletAbortException(var8);
      }
   }

   protected void generatePackageTreeFile() throws IOException {
      Content var1 = this.getPackageTreeHeader();
      Content var2 = this.getResource("doclet.Hierarchy_For_Package", Util.getPackageName(this.packagedoc));
      HtmlTree var3 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, false, HtmlStyle.title, var2);
      HtmlTree var4 = HtmlTree.DIV(HtmlStyle.header, var3);
      if (this.configuration.packages.length > 1) {
         this.addLinkToMainTree(var4);
      }

      var1.addContent((Content)var4);
      HtmlTree var5 = new HtmlTree(HtmlTag.DIV);
      var5.addStyle(HtmlStyle.contentContainer);
      this.addTree(this.classtree.baseclasses(), "doclet.Class_Hierarchy", var5);
      this.addTree(this.classtree.baseinterfaces(), "doclet.Interface_Hierarchy", var5);
      this.addTree(this.classtree.baseAnnotationTypes(), "doclet.Annotation_Type_Hierarchy", var5);
      this.addTree(this.classtree.baseEnums(), "doclet.Enum_Hierarchy", var5);
      var1.addContent((Content)var5);
      this.addNavLinks(false, var1);
      this.addBottom(var1);
      this.printHtmlDocument((String[])null, true, var1);
   }

   protected Content getPackageTreeHeader() {
      String var1 = this.packagedoc.name() + " " + this.configuration.getText("doclet.Window_Class_Hierarchy");
      HtmlTree var2 = this.getBody(true, this.getWindowTitle(var1));
      this.addTop(var2);
      this.addNavLinks(true, var2);
      return var2;
   }

   protected void addLinkToMainTree(Content var1) {
      HtmlTree var2 = HtmlTree.SPAN(HtmlStyle.packageHierarchyLabel, this.getResource("doclet.Package_Hierarchies"));
      var1.addContent((Content)var2);
      HtmlTree var3 = new HtmlTree(HtmlTag.UL);
      var3.addStyle(HtmlStyle.horizontal);
      var3.addContent(this.getNavLinkMainTree(this.configuration.getText("doclet.All_Packages")));
      var1.addContent((Content)var3);
   }

   protected Content getNavLinkPrevious() {
      if (this.prev == null) {
         return this.getNavLinkPrevious((DocPath)null);
      } else {
         DocPath var1 = DocPath.relativePath(this.packagedoc, this.prev);
         return this.getNavLinkPrevious(var1.resolve(DocPaths.PACKAGE_TREE));
      }
   }

   protected Content getNavLinkNext() {
      if (this.next == null) {
         return this.getNavLinkNext((DocPath)null);
      } else {
         DocPath var1 = DocPath.relativePath(this.packagedoc, this.next);
         return this.getNavLinkNext(var1.resolve(DocPaths.PACKAGE_TREE));
      }
   }

   protected Content getNavLinkPackage() {
      Content var1 = this.getHyperLink(DocPaths.PACKAGE_SUMMARY, this.packageLabel);
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }
}
