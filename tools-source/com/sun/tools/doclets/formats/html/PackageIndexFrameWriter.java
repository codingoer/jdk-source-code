package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;

public class PackageIndexFrameWriter extends AbstractPackageIndexWriter {
   public PackageIndexFrameWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
   }

   public static void generate(ConfigurationImpl var0) {
      DocPath var2 = DocPaths.OVERVIEW_FRAME;

      try {
         PackageIndexFrameWriter var1 = new PackageIndexFrameWriter(var0, var2);
         var1.buildPackageIndexFile("doclet.Window_Overview", false);
         var1.close();
      } catch (IOException var4) {
         var0.standardmessage.error("doclet.exception_encountered", var4.toString(), var2);
         throw new DocletAbortException(var4);
      }
   }

   protected void addPackagesList(PackageDoc[] var1, String var2, String var3, Content var4) {
      HtmlTree var5 = HtmlTree.HEADING(HtmlConstants.PACKAGE_HEADING, true, this.packagesLabel);
      HtmlTree var6 = HtmlTree.DIV(HtmlStyle.indexContainer, var5);
      HtmlTree var7 = new HtmlTree(HtmlTag.UL);
      var7.setTitle(this.packagesLabel);

      for(int var8 = 0; var8 < var1.length; ++var8) {
         if (var1[var8] != null && (!this.configuration.nodeprecated || !Util.isDeprecated(var1[var8]))) {
            var7.addContent(this.getPackage(var1[var8]));
         }
      }

      var6.addContent((Content)var7);
      var4.addContent((Content)var6);
   }

   protected Content getPackage(PackageDoc var1) {
      Content var2;
      if (var1.name().length() > 0) {
         Content var3 = this.getPackageLabel(var1.name());
         var2 = this.getHyperLink(this.pathString(var1, DocPaths.PACKAGE_FRAME), var3, "", "packageFrame");
      } else {
         StringContent var5 = new StringContent("<unnamed package>");
         var2 = this.getHyperLink(DocPaths.PACKAGE_FRAME, var5, "", "packageFrame");
      }

      HtmlTree var4 = HtmlTree.LI(var2);
      return var4;
   }

   protected void addNavigationBarHeader(Content var1) {
      RawHtml var2;
      if (this.configuration.packagesheader.length() > 0) {
         var2 = new RawHtml(this.replaceDocRootDir(this.configuration.packagesheader));
      } else {
         var2 = new RawHtml(this.replaceDocRootDir(this.configuration.header));
      }

      HtmlTree var3 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, true, HtmlStyle.bar, var2);
      var1.addContent((Content)var3);
   }

   protected void addOverviewHeader(Content var1) {
   }

   protected void addAllClassesLink(Content var1) {
      Content var2 = this.getHyperLink(DocPaths.ALLCLASSES_FRAME, this.allclassesLabel, "", "packageFrame");
      HtmlTree var3 = HtmlTree.SPAN(var2);
      var1.addContent((Content)var3);
   }

   protected void addAllProfilesLink(Content var1) {
      Content var2 = this.getHyperLink(DocPaths.PROFILE_OVERVIEW_FRAME, this.allprofilesLabel, "", "packageListFrame");
      HtmlTree var3 = HtmlTree.SPAN(var2);
      var1.addContent((Content)var3);
   }

   protected void addNavigationBarFooter(Content var1) {
      HtmlTree var2 = HtmlTree.P(this.getSpace());
      var1.addContent((Content)var2);
   }
}
