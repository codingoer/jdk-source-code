package com.sun.tools.doclets.formats.html;

import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.javac.sym.Profiles;
import java.io.IOException;

public abstract class AbstractProfileIndexWriter extends HtmlDocletWriter {
   protected Profiles profiles;

   public AbstractProfileIndexWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
      this.profiles = var1.profiles;
   }

   protected abstract void addNavigationBarHeader(Content var1);

   protected abstract void addNavigationBarFooter(Content var1);

   protected abstract void addOverviewHeader(Content var1);

   protected abstract void addProfilesList(Profiles var1, String var2, String var3, Content var4);

   protected abstract void addProfilePackagesList(Profiles var1, String var2, String var3, Content var4, String var5);

   protected void buildProfileIndexFile(String var1, boolean var2) throws IOException {
      String var3 = this.configuration.getText(var1);
      HtmlTree var4 = this.getBody(var2, this.getWindowTitle(var3));
      this.addNavigationBarHeader(var4);
      this.addOverviewHeader(var4);
      this.addIndex(var4);
      this.addOverview(var4);
      this.addNavigationBarFooter(var4);
      this.printHtmlDocument(this.configuration.metakeywords.getOverviewMetaKeywords(var1, this.configuration.doctitle), var2, var4);
   }

   protected void buildProfilePackagesIndexFile(String var1, boolean var2, String var3) throws IOException {
      String var4 = this.configuration.getText(var1);
      HtmlTree var5 = this.getBody(var2, this.getWindowTitle(var4));
      this.addNavigationBarHeader(var5);
      this.addOverviewHeader(var5);
      this.addProfilePackagesIndex(var5, var3);
      this.addOverview(var5);
      this.addNavigationBarFooter(var5);
      this.printHtmlDocument(this.configuration.metakeywords.getOverviewMetaKeywords(var1, this.configuration.doctitle), var2, var5);
   }

   protected void addOverview(Content var1) throws IOException {
   }

   protected void addIndex(Content var1) {
      this.addIndexContents(this.profiles, "doclet.Profile_Summary", this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Profile_Summary"), this.configuration.getText("doclet.profiles")), var1);
   }

   protected void addProfilePackagesIndex(Content var1, String var2) {
      this.addProfilePackagesIndexContents(this.profiles, "doclet.Profile_Summary", this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Profile_Summary"), this.configuration.getText("doclet.profiles")), var1, var2);
   }

   protected void addIndexContents(Profiles var1, String var2, String var3, Content var4) {
      if (var1.getProfileCount() > 0) {
         HtmlTree var5 = new HtmlTree(HtmlTag.DIV);
         var5.addStyle(HtmlStyle.indexHeader);
         this.addAllClassesLink(var5);
         this.addAllPackagesLink(var5);
         var4.addContent((Content)var5);
         this.addProfilesList(var1, var2, var3, var4);
      }

   }

   protected void addProfilePackagesIndexContents(Profiles var1, String var2, String var3, Content var4, String var5) {
      HtmlTree var6 = new HtmlTree(HtmlTag.DIV);
      var6.addStyle(HtmlStyle.indexHeader);
      this.addAllClassesLink(var6);
      this.addAllPackagesLink(var6);
      this.addAllProfilesLink(var6);
      var4.addContent((Content)var6);
      this.addProfilePackagesList(var1, var2, var3, var4, var5);
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

   protected void addAllPackagesLink(Content var1) {
   }

   protected void addAllProfilesLink(Content var1) {
   }
}
