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
import com.sun.tools.javac.sym.Profiles;
import java.io.IOException;

public class ProfilePackageIndexFrameWriter extends AbstractProfileIndexWriter {
   public ProfilePackageIndexFrameWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
   }

   public static void generate(ConfigurationImpl var0, String var1) {
      DocPath var3 = DocPaths.profileFrame(var1);

      try {
         ProfilePackageIndexFrameWriter var2 = new ProfilePackageIndexFrameWriter(var0, var3);
         var2.buildProfilePackagesIndexFile("doclet.Window_Overview", false, var1);
         var2.close();
      } catch (IOException var5) {
         var0.standardmessage.error("doclet.exception_encountered", var5.toString(), var3);
         throw new DocletAbortException(var5);
      }
   }

   protected void addProfilePackagesList(Profiles var1, String var2, String var3, Content var4, String var5) {
      StringContent var6 = new StringContent(var5);
      HtmlTree var7 = HtmlTree.HEADING(HtmlConstants.PACKAGE_HEADING, true, this.getTargetProfileLink("classFrame", var6, var5));
      var7.addContent(this.getSpace());
      var7.addContent(this.packagesLabel);
      HtmlTree var8 = HtmlTree.DIV(HtmlStyle.indexContainer, var7);
      HtmlTree var9 = new HtmlTree(HtmlTag.UL);
      var9.setTitle(this.packagesLabel);
      PackageDoc[] var10 = (PackageDoc[])this.configuration.profilePackages.get(var5);

      for(int var11 = 0; var11 < var10.length; ++var11) {
         if (!this.configuration.nodeprecated || !Util.isDeprecated(var10[var11])) {
            var9.addContent(this.getPackage(var10[var11], var5));
         }
      }

      var8.addContent((Content)var9);
      var4.addContent((Content)var8);
   }

   protected Content getPackage(PackageDoc var1, String var2) {
      Content var3;
      if (var1.name().length() > 0) {
         Content var4 = this.getPackageLabel(var1.name());
         var3 = this.getHyperLink(this.pathString(var1, DocPaths.profilePackageFrame(var2)), var4, "", "packageFrame");
      } else {
         StringContent var6 = new StringContent("<unnamed package>");
         var3 = this.getHyperLink(DocPaths.PACKAGE_FRAME, var6, "", "packageFrame");
      }

      HtmlTree var5 = HtmlTree.LI(var3);
      return var5;
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

   protected void addProfilesList(Profiles var1, String var2, String var3, Content var4) {
   }

   protected void addAllClassesLink(Content var1) {
      Content var2 = this.getHyperLink(DocPaths.ALLCLASSES_FRAME, this.allclassesLabel, "", "packageFrame");
      HtmlTree var3 = HtmlTree.SPAN(var2);
      var1.addContent((Content)var3);
   }

   protected void addAllPackagesLink(Content var1) {
      Content var2 = this.getHyperLink(DocPaths.OVERVIEW_FRAME, this.allpackagesLabel, "", "packageListFrame");
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
