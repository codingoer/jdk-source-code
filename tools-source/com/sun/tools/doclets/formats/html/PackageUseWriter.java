package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.ClassUseMapper;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class PackageUseWriter extends SubWriterHolderWriter {
   final PackageDoc pkgdoc;
   final SortedMap usingPackageToUsedClasses = new TreeMap();

   public PackageUseWriter(ConfigurationImpl var1, ClassUseMapper var2, DocPath var3, PackageDoc var4) throws IOException {
      super(var1, DocPath.forPackage(var4).resolve(var3));
      this.pkgdoc = var4;
      ClassDoc[] var5 = var4.allClasses();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         ClassDoc var7 = var5[var6];
         Set var8 = (Set)var2.classToClass.get(var7.qualifiedName());
         Object var12;
         if (var8 != null) {
            for(Iterator var9 = var8.iterator(); var9.hasNext(); ((Set)var12).add(var7)) {
               ClassDoc var10 = (ClassDoc)var9.next();
               PackageDoc var11 = var10.containingPackage();
               var12 = (Set)this.usingPackageToUsedClasses.get(var11.name());
               if (var12 == null) {
                  var12 = new TreeSet();
                  this.usingPackageToUsedClasses.put(Util.getPackageName(var11), var12);
               }
            }
         }
      }

   }

   public static void generate(ConfigurationImpl var0, ClassUseMapper var1, PackageDoc var2) {
      DocPath var4 = DocPaths.PACKAGE_USE;

      try {
         PackageUseWriter var3 = new PackageUseWriter(var0, var1, var4, var2);
         var3.generatePackageUseFile();
         var3.close();
      } catch (IOException var6) {
         var0.standardmessage.error("doclet.exception_encountered", var6.toString(), var4);
         throw new DocletAbortException(var6);
      }
   }

   protected void generatePackageUseFile() throws IOException {
      Content var1 = this.getPackageUseHeader();
      HtmlTree var2 = new HtmlTree(HtmlTag.DIV);
      var2.addStyle(HtmlStyle.contentContainer);
      if (this.usingPackageToUsedClasses.isEmpty()) {
         var2.addContent(this.getResource("doclet.ClassUse_No.usage.of.0", this.pkgdoc.name()));
      } else {
         this.addPackageUse(var2);
      }

      var1.addContent((Content)var2);
      this.addNavLinks(false, var1);
      this.addBottom(var1);
      this.printHtmlDocument((String[])null, true, var1);
   }

   protected void addPackageUse(Content var1) throws IOException {
      HtmlTree var2 = new HtmlTree(HtmlTag.UL);
      var2.addStyle(HtmlStyle.blockList);
      if (this.configuration.packages.length > 1) {
         this.addPackageList(var2);
      }

      this.addClassList(var2);
      var1.addContent((Content)var2);
   }

   protected void addPackageList(Content var1) throws IOException {
      HtmlTree var2 = HtmlTree.TABLE(HtmlStyle.useSummary, 0, 3, 0, this.useTableSummary, this.getTableCaption(this.configuration.getResource("doclet.ClassUse_Packages.that.use.0", this.getPackageLink(this.pkgdoc, Util.getPackageName(this.pkgdoc)))));
      var2.addContent(this.getSummaryTableHeader(this.packageTableHeader, "col"));
      HtmlTree var3 = new HtmlTree(HtmlTag.TBODY);
      Iterator var4 = this.usingPackageToUsedClasses.keySet().iterator();

      for(int var5 = 0; var4.hasNext(); ++var5) {
         PackageDoc var6 = this.configuration.root.packageNamed((String)var4.next());
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

   protected void addClassList(Content var1) throws IOException {
      String[] var2 = new String[]{this.configuration.getText("doclet.0_and_1", this.configuration.getText("doclet.Class"), this.configuration.getText("doclet.Description"))};
      Iterator var3 = this.usingPackageToUsedClasses.keySet().iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         PackageDoc var5 = this.configuration.root.packageNamed(var4);
         HtmlTree var6 = new HtmlTree(HtmlTag.LI);
         var6.addStyle(HtmlStyle.blockList);
         if (var5 != null) {
            var6.addContent(this.getMarkerAnchor(var5.name()));
         }

         String var7 = this.configuration.getText("doclet.Use_Table_Summary", this.configuration.getText("doclet.classes"));
         HtmlTree var8 = HtmlTree.TABLE(HtmlStyle.useSummary, 0, 3, 0, var7, this.getTableCaption(this.configuration.getResource("doclet.ClassUse_Classes.in.0.used.by.1", this.getPackageLink(this.pkgdoc, Util.getPackageName(this.pkgdoc)), this.getPackageLink(var5, Util.getPackageName(var5)))));
         var8.addContent(this.getSummaryTableHeader(var2, "col"));
         HtmlTree var9 = new HtmlTree(HtmlTag.TBODY);
         Iterator var10 = ((Set)this.usingPackageToUsedClasses.get(var4)).iterator();

         for(int var11 = 0; var10.hasNext(); ++var11) {
            HtmlTree var12 = new HtmlTree(HtmlTag.TR);
            if (var11 % 2 == 0) {
               var12.addStyle(HtmlStyle.altColor);
            } else {
               var12.addStyle(HtmlStyle.rowColor);
            }

            this.addClassRow((ClassDoc)var10.next(), var4, var12);
            var9.addContent((Content)var12);
         }

         var8.addContent((Content)var9);
         var6.addContent((Content)var8);
         var1.addContent((Content)var6);
      }

   }

   protected void addClassRow(ClassDoc var1, String var2, Content var3) {
      DocPath var4 = this.pathString(var1, DocPaths.CLASS_USE.resolve(DocPath.forName(var1)));
      HtmlTree var5 = HtmlTree.TD(HtmlStyle.colOne, this.getHyperLink(var4.fragment(var2), new StringContent(var1.name())));
      this.addIndexComment(var1, var5);
      var3.addContent((Content)var5);
   }

   protected void addPackageUse(PackageDoc var1, Content var2) throws IOException {
      HtmlTree var3 = HtmlTree.TD(HtmlStyle.colFirst, this.getHyperLink(Util.getPackageName(var1), new StringContent(Util.getPackageName(var1))));
      var2.addContent((Content)var3);
      HtmlTree var4 = new HtmlTree(HtmlTag.TD);
      var4.addStyle(HtmlStyle.colLast);
      if (var1 != null && var1.name().length() != 0) {
         this.addSummaryComment(var1, var4);
      } else {
         var4.addContent(this.getSpace());
      }

      var2.addContent((Content)var4);
   }

   protected Content getPackageUseHeader() {
      String var1 = this.configuration.getText("doclet.Package");
      String var2 = this.pkgdoc.name();
      String var3 = this.configuration.getText("doclet.Window_ClassUse_Header", var1, var2);
      HtmlTree var4 = this.getBody(true, this.getWindowTitle(var3));
      this.addTop(var4);
      this.addNavLinks(true, var4);
      ContentBuilder var5 = new ContentBuilder();
      var5.addContent(this.getResource("doclet.ClassUse_Title", var1));
      var5.addContent((Content)(new HtmlTree(HtmlTag.BR)));
      var5.addContent(var2);
      HtmlTree var6 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, true, HtmlStyle.title, var5);
      HtmlTree var7 = HtmlTree.DIV(HtmlStyle.header, var6);
      var4.addContent((Content)var7);
      return var4;
   }

   protected Content getNavLinkPackage() {
      Content var1 = this.getHyperLink(DocPaths.PACKAGE_SUMMARY, this.packageLabel);
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected Content getNavLinkClassUse() {
      HtmlTree var1 = HtmlTree.LI(HtmlStyle.navBarCell1Rev, this.useLabel);
      return var1;
   }

   protected Content getNavLinkTree() {
      Content var1 = this.getHyperLink(DocPaths.PACKAGE_TREE, this.treeLabel);
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }
}
