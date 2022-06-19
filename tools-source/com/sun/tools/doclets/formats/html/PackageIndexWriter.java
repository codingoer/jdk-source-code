package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;
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
import com.sun.tools.javac.jvm.Profile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PackageIndexWriter extends AbstractPackageIndexWriter {
   private RootDoc root;
   private Map groupPackageMap;
   private List groupList;

   public PackageIndexWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
      this.root = var1.root;
      this.groupPackageMap = var1.group.groupPackages(this.packages);
      this.groupList = var1.group.getGroupList();
   }

   public static void generate(ConfigurationImpl var0) {
      DocPath var2 = DocPaths.OVERVIEW_SUMMARY;

      try {
         PackageIndexWriter var1 = new PackageIndexWriter(var0, var2);
         var1.buildPackageIndexFile("doclet.Window_Overview_Summary", true);
         var1.close();
      } catch (IOException var4) {
         var0.standardmessage.error("doclet.exception_encountered", var4.toString(), var2);
         throw new DocletAbortException(var4);
      }
   }

   protected void addIndex(Content var1) {
      for(int var2 = 0; var2 < this.groupList.size(); ++var2) {
         String var3 = (String)this.groupList.get(var2);
         List var4 = (List)this.groupPackageMap.get(var3);
         if (var4 != null && var4.size() > 0) {
            this.addIndexContents((PackageDoc[])var4.toArray(new PackageDoc[var4.size()]), var3, this.configuration.getText("doclet.Member_Table_Summary", var3, this.configuration.getText("doclet.packages")), var1);
         }
      }

   }

   protected void addProfilesList(Content var1, Content var2) {
      HtmlTree var3 = HtmlTree.HEADING(HtmlTag.H2, var1);
      HtmlTree var4 = HtmlTree.DIV(var3);
      HtmlTree var5 = new HtmlTree(HtmlTag.UL);

      for(int var7 = 1; var7 < this.configuration.profiles.getProfileCount(); ++var7) {
         String var6 = Profile.lookup(var7).name;
         if (this.configuration.shouldDocumentProfile(var6)) {
            Content var8 = this.getTargetProfileLink("classFrame", new StringContent(var6), var6);
            HtmlTree var9 = HtmlTree.LI(var8);
            var5.addContent((Content)var9);
         }
      }

      var4.addContent((Content)var5);
      HtmlTree var10 = HtmlTree.DIV(HtmlStyle.contentContainer, var4);
      var2.addContent((Content)var10);
   }

   protected void addPackagesList(PackageDoc[] var1, String var2, String var3, Content var4) {
      HtmlTree var5 = HtmlTree.TABLE(HtmlStyle.overviewSummary, 0, 3, 0, var3, this.getTableCaption(new RawHtml(var2)));
      var5.addContent(this.getSummaryTableHeader(this.packageTableHeader, "col"));
      HtmlTree var6 = new HtmlTree(HtmlTag.TBODY);
      this.addPackagesList(var1, var6);
      var5.addContent((Content)var6);
      HtmlTree var7 = HtmlTree.DIV(HtmlStyle.contentContainer, var5);
      var4.addContent((Content)var7);
   }

   protected void addPackagesList(PackageDoc[] var1, Content var2) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3] != null && var1[var3].name().length() > 0 && (!this.configuration.nodeprecated || !Util.isDeprecated(var1[var3]))) {
            Content var4 = this.getPackageLink(var1[var3], this.getPackageName(var1[var3]));
            HtmlTree var5 = HtmlTree.TD(HtmlStyle.colFirst, var4);
            HtmlTree var6 = new HtmlTree(HtmlTag.TD);
            var6.addStyle(HtmlStyle.colLast);
            this.addSummaryComment(var1[var3], var6);
            HtmlTree var7 = HtmlTree.TR(var5);
            var7.addContent((Content)var6);
            if (var3 % 2 == 0) {
               var7.addStyle(HtmlStyle.altColor);
            } else {
               var7.addStyle(HtmlStyle.rowColor);
            }

            var2.addContent((Content)var7);
         }
      }

   }

   protected void addOverviewHeader(Content var1) {
      if (this.root.inlineTags().length > 0) {
         HtmlTree var2 = new HtmlTree(HtmlTag.DIV);
         var2.addStyle(HtmlStyle.subTitle);
         this.addSummaryComment(this.root, var2);
         HtmlTree var3 = HtmlTree.DIV(HtmlStyle.header, var2);
         Content var4 = this.seeLabel;
         var4.addContent(" ");
         HtmlTree var5 = HtmlTree.P(var4);
         Content var6 = this.getHyperLink(this.getDocLink(SectionName.OVERVIEW_DESCRIPTION), this.descriptionLabel, "", "");
         var5.addContent(var6);
         var3.addContent((Content)var5);
         var1.addContent((Content)var3);
      }

   }

   protected void addOverviewComment(Content var1) {
      if (this.root.inlineTags().length > 0) {
         var1.addContent(this.getMarkerAnchor(SectionName.OVERVIEW_DESCRIPTION));
         this.addInlineComment(this.root, var1);
      }

   }

   protected void addOverview(Content var1) throws IOException {
      HtmlTree var2 = new HtmlTree(HtmlTag.DIV);
      var2.addStyle(HtmlStyle.contentContainer);
      this.addOverviewComment(var2);
      this.addTagsInfo(this.root, var2);
      var1.addContent((Content)var2);
   }

   protected void addNavigationBarHeader(Content var1) {
      this.addTop(var1);
      this.addNavLinks(true, var1);
      this.addConfigurationTitle(var1);
   }

   protected void addNavigationBarFooter(Content var1) {
      this.addNavLinks(false, var1);
      this.addBottom(var1);
   }
}
