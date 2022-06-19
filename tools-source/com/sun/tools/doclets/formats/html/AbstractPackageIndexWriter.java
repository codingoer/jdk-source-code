package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import java.io.IOException;
import java.util.Arrays;

public abstract class AbstractPackageIndexWriter extends HtmlDocletWriter {
   protected PackageDoc[] packages;

   public AbstractPackageIndexWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
      this.packages = var1.packages;
   }

   protected abstract void addNavigationBarHeader(Content var1);

   protected abstract void addNavigationBarFooter(Content var1);

   protected abstract void addOverviewHeader(Content var1);

   protected abstract void addPackagesList(PackageDoc[] var1, String var2, String var3, Content var4);

   protected void buildPackageIndexFile(String var1, boolean var2) throws IOException {
      String var3 = this.configuration.getText(var1);
      HtmlTree var4 = this.getBody(var2, this.getWindowTitle(var3));
      this.addNavigationBarHeader(var4);
      this.addOverviewHeader(var4);
      this.addIndex(var4);
      this.addOverview(var4);
      this.addNavigationBarFooter(var4);
      this.printHtmlDocument(this.configuration.metakeywords.getOverviewMetaKeywords(var1, this.configuration.doctitle), var2, var4);
   }

   protected void addOverview(Content var1) throws IOException {
   }

   protected void addIndex(Content var1) {
      this.addIndexContents(this.packages, "doclet.Package_Summary", this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Package_Summary"), this.configuration.getText("doclet.packages")), var1);
   }

   protected void addIndexContents(PackageDoc[] var1, String var2, String var3, Content var4) {
      if (var1.length > 0) {
         Arrays.sort(var1);
         HtmlTree var5 = new HtmlTree(HtmlTag.DIV);
         var5.addStyle(HtmlStyle.indexHeader);
         this.addAllClassesLink(var5);
         if (this.configuration.showProfiles) {
            this.addAllProfilesLink(var5);
         }

         var4.addContent((Content)var5);
         if (this.configuration.showProfiles && this.configuration.profilePackages.size() > 0) {
            Content var6 = this.configuration.getResource("doclet.Profiles");
            this.addProfilesList(var6, var4);
         }

         this.addPackagesList(var1, var2, var3, var4);
      }

   }

   protected void addConfigurationTitle(Content var1) {
      if (this.configuration.doctitle.length() > 0) {
         RawHtml var2 = new RawHtml(this.configuration.doctitle);
         HtmlTree var3 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, HtmlStyle.title, var2);
         HtmlTree var4 = HtmlTree.DIV(HtmlStyle.header, var3);
         var1.addContent((Content)var4);
      }

   }

   protected Content getNavLinkContents() {
      HtmlTree var1 = HtmlTree.LI(HtmlStyle.navBarCell1Rev, this.overviewLabel);
      return var1;
   }

   protected void addAllClassesLink(Content var1) {
   }

   protected void addAllProfilesLink(Content var1) {
   }

   protected void addProfilesList(Content var1, Content var2) {
   }
}
