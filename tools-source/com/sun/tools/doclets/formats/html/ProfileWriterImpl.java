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
import com.sun.tools.doclets.internal.toolkit.ProfileSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.javac.jvm.Profile;
import java.io.IOException;

public class ProfileWriterImpl extends HtmlDocletWriter implements ProfileSummaryWriter {
   protected Profile prevProfile;
   protected Profile nextProfile;
   protected Profile profile;

   public ProfileWriterImpl(ConfigurationImpl var1, Profile var2, Profile var3, Profile var4) throws IOException {
      super(var1, DocPaths.profileSummary(var2.name));
      this.prevProfile = var3;
      this.nextProfile = var4;
      this.profile = var2;
   }

   public Content getProfileHeader(String var1) {
      String var2 = this.profile.name;
      HtmlTree var3 = this.getBody(true, this.getWindowTitle(var2));
      this.addTop(var3);
      this.addNavLinks(true, var3);
      HtmlTree var4 = new HtmlTree(HtmlTag.DIV);
      var4.addStyle(HtmlStyle.header);
      HtmlTree var5 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, true, HtmlStyle.title, this.profileLabel);
      var5.addContent(this.getSpace());
      RawHtml var6 = new RawHtml(var1);
      var5.addContent((Content)var6);
      var4.addContent((Content)var5);
      var3.addContent((Content)var4);
      return var3;
   }

   public Content getContentHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.DIV);
      var1.addStyle(HtmlStyle.contentContainer);
      return var1;
   }

   public Content getSummaryHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.LI);
      var1.addStyle(HtmlStyle.blockList);
      return var1;
   }

   public Content getSummaryTree(Content var1) {
      HtmlTree var2 = HtmlTree.UL(HtmlStyle.blockList, var1);
      HtmlTree var3 = HtmlTree.DIV(HtmlStyle.summary, var2);
      return var3;
   }

   public Content getPackageSummaryHeader(PackageDoc var1) {
      Content var2 = this.getTargetProfilePackageLink(var1, "classFrame", new StringContent(var1.name()), this.profile.name);
      HtmlTree var3 = HtmlTree.HEADING(HtmlTag.H3, var2);
      HtmlTree var4 = HtmlTree.LI(HtmlStyle.blockList, var3);
      this.addPackageDeprecationInfo(var4, var1);
      return var4;
   }

   public Content getPackageSummaryTree(Content var1) {
      HtmlTree var2 = HtmlTree.UL(HtmlStyle.blockList, var1);
      return var2;
   }

   public void addClassesSummary(ClassDoc[] var1, String var2, String var3, String[] var4, Content var5) {
      this.addClassesSummary(var1, var2, var3, var4, var5, this.profile.value);
   }

   public void addProfileFooter(Content var1) {
      this.addNavLinks(false, var1);
      this.addBottom(var1);
   }

   public void printDocument(Content var1) throws IOException {
      this.printHtmlDocument(this.configuration.metakeywords.getMetaKeywords(this.profile), true, var1);
   }

   public void addPackageDeprecationInfo(Content var1, PackageDoc var2) {
      if (Util.isDeprecated(var2)) {
         Tag[] var3 = var2.tags("deprecated");
         HtmlTree var4 = new HtmlTree(HtmlTag.DIV);
         var4.addStyle(HtmlStyle.deprecatedContent);
         HtmlTree var5 = HtmlTree.SPAN(HtmlStyle.deprecatedLabel, this.deprecatedPhrase);
         var4.addContent((Content)var5);
         if (var3.length > 0) {
            Tag[] var6 = var3[0].inlineTags();
            if (var6.length > 0) {
               this.addInlineDeprecatedComment(var2, var3[0], var4);
            }
         }

         var1.addContent((Content)var4);
      }

   }

   public Content getNavLinkPrevious() {
      HtmlTree var1;
      if (this.prevProfile == null) {
         var1 = HtmlTree.LI(this.prevprofileLabel);
      } else {
         var1 = HtmlTree.LI(this.getHyperLink(this.pathToRoot.resolve(DocPaths.profileSummary(this.prevProfile.name)), this.prevprofileLabel, "", ""));
      }

      return var1;
   }

   public Content getNavLinkNext() {
      HtmlTree var1;
      if (this.nextProfile == null) {
         var1 = HtmlTree.LI(this.nextprofileLabel);
      } else {
         var1 = HtmlTree.LI(this.getHyperLink(this.pathToRoot.resolve(DocPaths.profileSummary(this.nextProfile.name)), this.nextprofileLabel, "", ""));
      }

      return var1;
   }
}
