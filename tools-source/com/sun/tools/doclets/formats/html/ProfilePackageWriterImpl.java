package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.ProfilePackageSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.javac.jvm.Profile;
import java.io.IOException;

public class ProfilePackageWriterImpl extends HtmlDocletWriter implements ProfilePackageSummaryWriter {
   protected PackageDoc prev;
   protected PackageDoc next;
   protected PackageDoc packageDoc;
   protected String profileName;
   protected int profileValue;

   public ProfilePackageWriterImpl(ConfigurationImpl var1, PackageDoc var2, PackageDoc var3, PackageDoc var4, Profile var5) throws IOException {
      super(var1, DocPath.forPackage(var2).resolve(DocPaths.profilePackageSummary(var5.name)));
      this.prev = var3;
      this.next = var4;
      this.packageDoc = var2;
      this.profileName = var5.name;
      this.profileValue = var5.value;
   }

   public Content getPackageHeader(String var1) {
      String var2 = this.packageDoc.name();
      HtmlTree var3 = this.getBody(true, this.getWindowTitle(var2));
      this.addTop(var3);
      this.addNavLinks(true, var3);
      HtmlTree var4 = new HtmlTree(HtmlTag.DIV);
      var4.addStyle(HtmlStyle.header);
      StringContent var5 = new StringContent(this.profileName);
      HtmlTree var6 = HtmlTree.DIV(HtmlStyle.subTitle, var5);
      var4.addContent((Content)var6);
      HtmlTree var7 = new HtmlTree(HtmlTag.P);
      this.addAnnotationInfo(this.packageDoc, var7);
      var4.addContent((Content)var7);
      HtmlTree var8 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, true, HtmlStyle.title, this.packageLabel);
      var8.addContent(this.getSpace());
      RawHtml var9 = new RawHtml(var1);
      var8.addContent((Content)var9);
      var4.addContent((Content)var8);
      this.addDeprecationInfo(var4);
      if (this.packageDoc.inlineTags().length > 0 && !this.configuration.nocomment) {
         HtmlTree var10 = new HtmlTree(HtmlTag.DIV);
         var10.addStyle(HtmlStyle.docSummary);
         this.addSummaryComment(this.packageDoc, var10);
         var4.addContent((Content)var10);
         Content var11 = this.getSpace();
         Content var12 = this.getHyperLink(this.getDocLink(SectionName.PACKAGE_DESCRIPTION), this.descriptionLabel, "", "");
         HtmlTree var13 = new HtmlTree(HtmlTag.P, new Content[]{this.seeLabel, var11, var12});
         var4.addContent((Content)var13);
      }

      var3.addContent((Content)var4);
      return var3;
   }

   public Content getContentHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.DIV);
      var1.addStyle(HtmlStyle.contentContainer);
      return var1;
   }

   public void addDeprecationInfo(Content var1) {
      Tag[] var2 = this.packageDoc.tags("deprecated");
      if (Util.isDeprecated(this.packageDoc)) {
         HtmlTree var3 = new HtmlTree(HtmlTag.DIV);
         var3.addStyle(HtmlStyle.deprecatedContent);
         HtmlTree var4 = HtmlTree.SPAN(HtmlStyle.deprecatedLabel, this.deprecatedPhrase);
         var3.addContent((Content)var4);
         if (var2.length > 0) {
            Tag[] var5 = var2[0].inlineTags();
            if (var5.length > 0) {
               this.addInlineDeprecatedComment(this.packageDoc, var2[0], var3);
            }
         }

         var1.addContent((Content)var3);
      }

   }

   public void addClassesSummary(ClassDoc[] var1, String var2, String var3, String[] var4, Content var5) {
      HtmlTree var6 = new HtmlTree(HtmlTag.LI);
      var6.addStyle(HtmlStyle.blockList);
      this.addClassesSummary(var1, var2, var3, var4, var6, this.profileValue);
      var5.addContent((Content)var6);
   }

   public Content getSummaryHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.UL);
      var1.addStyle(HtmlStyle.blockList);
      return var1;
   }

   public void addPackageDescription(Content var1) {
      if (this.packageDoc.inlineTags().length > 0) {
         var1.addContent(this.getMarkerAnchor(SectionName.PACKAGE_DESCRIPTION));
         StringContent var2 = new StringContent(this.configuration.getText("doclet.Package_Description", this.packageDoc.name()));
         var1.addContent((Content)HtmlTree.HEADING(HtmlConstants.PACKAGE_HEADING, true, var2));
         this.addInlineComment(this.packageDoc, var1);
      }

   }

   public void addPackageTags(Content var1) {
      this.addTagsInfo(this.packageDoc, var1);
   }

   public void addPackageFooter(Content var1) {
      this.addNavLinks(false, var1);
      this.addBottom(var1);
   }

   public void printDocument(Content var1) throws IOException {
      this.printHtmlDocument(this.configuration.metakeywords.getMetaKeywords(this.packageDoc), true, var1);
   }

   protected Content getNavLinkClassUse() {
      Content var1 = this.getHyperLink(DocPaths.PACKAGE_USE, this.useLabel, "", "");
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   public Content getNavLinkPrevious() {
      HtmlTree var1;
      if (this.prev == null) {
         var1 = HtmlTree.LI(this.prevpackageLabel);
      } else {
         DocPath var2 = DocPath.relativePath(this.packageDoc, this.prev);
         var1 = HtmlTree.LI(this.getHyperLink(var2.resolve(DocPaths.profilePackageSummary(this.profileName)), this.prevpackageLabel, "", ""));
      }

      return var1;
   }

   public Content getNavLinkNext() {
      HtmlTree var1;
      if (this.next == null) {
         var1 = HtmlTree.LI(this.nextpackageLabel);
      } else {
         DocPath var2 = DocPath.relativePath(this.packageDoc, this.next);
         var1 = HtmlTree.LI(this.getHyperLink(var2.resolve(DocPaths.profilePackageSummary(this.profileName)), this.nextpackageLabel, "", ""));
      }

      return var1;
   }

   protected Content getNavLinkTree() {
      Content var1 = this.getHyperLink(DocPaths.PACKAGE_TREE, this.treeLabel, "", "");
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected Content getNavLinkPackage() {
      HtmlTree var1 = HtmlTree.LI(HtmlStyle.navBarCell1Rev, this.packageLabel);
      return var1;
   }
}
