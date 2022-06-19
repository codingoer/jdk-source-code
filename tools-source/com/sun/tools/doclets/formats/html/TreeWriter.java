package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;

public class TreeWriter extends AbstractTreeWriter {
   private PackageDoc[] packages;
   private boolean classesonly;

   public TreeWriter(ConfigurationImpl var1, DocPath var2, ClassTree var3) throws IOException {
      super(var1, var2, var3);
      this.packages = var1.packages;
      this.classesonly = this.packages.length == 0;
   }

   public static void generate(ConfigurationImpl var0, ClassTree var1) {
      DocPath var3 = DocPaths.OVERVIEW_TREE;

      try {
         TreeWriter var2 = new TreeWriter(var0, var3, var1);
         var2.generateTreeFile();
         var2.close();
      } catch (IOException var5) {
         var0.standardmessage.error("doclet.exception_encountered", var5.toString(), var3);
         throw new DocletAbortException(var5);
      }
   }

   public void generateTreeFile() throws IOException {
      Content var1 = this.getTreeHeader();
      Content var2 = this.getResource("doclet.Hierarchy_For_All_Packages");
      HtmlTree var3 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, false, HtmlStyle.title, var2);
      HtmlTree var4 = HtmlTree.DIV(HtmlStyle.header, var3);
      this.addPackageTreeLinks(var4);
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

   protected void addPackageTreeLinks(Content var1) {
      if (this.packages.length != 1 || this.packages[0].name().length() != 0) {
         if (!this.classesonly) {
            HtmlTree var2 = HtmlTree.SPAN(HtmlStyle.packageHierarchyLabel, this.getResource("doclet.Package_Hierarchies"));
            var1.addContent((Content)var2);
            HtmlTree var3 = new HtmlTree(HtmlTag.UL);
            var3.addStyle(HtmlStyle.horizontal);

            for(int var4 = 0; var4 < this.packages.length; ++var4) {
               if (this.packages[var4].name().length() != 0 && (!this.configuration.nodeprecated || !Util.isDeprecated(this.packages[var4]))) {
                  DocPath var5 = this.pathString(this.packages[var4], DocPaths.PACKAGE_TREE);
                  HtmlTree var6 = HtmlTree.LI(this.getHyperLink(var5, new StringContent(this.packages[var4].name())));
                  if (var4 < this.packages.length - 1) {
                     var6.addContent(", ");
                  }

                  var3.addContent((Content)var6);
               }
            }

            var1.addContent((Content)var3);
         }

      }
   }

   protected Content getTreeHeader() {
      String var1 = this.configuration.getText("doclet.Window_Class_Hierarchy");
      HtmlTree var2 = this.getBody(true, this.getWindowTitle(var1));
      this.addTop(var2);
      this.addNavLinks(true, var2);
      return var2;
   }
}
