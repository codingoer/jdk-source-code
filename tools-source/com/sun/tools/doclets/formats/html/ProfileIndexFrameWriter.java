package com.sun.tools.doclets.formats.html;

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
import com.sun.tools.javac.jvm.Profile;
import com.sun.tools.javac.sym.Profiles;
import java.io.IOException;

public class ProfileIndexFrameWriter extends AbstractProfileIndexWriter {
   public ProfileIndexFrameWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
   }

   public static void generate(ConfigurationImpl var0) {
      DocPath var2 = DocPaths.PROFILE_OVERVIEW_FRAME;

      try {
         ProfileIndexFrameWriter var1 = new ProfileIndexFrameWriter(var0, var2);
         var1.buildProfileIndexFile("doclet.Window_Overview", false);
         var1.close();
      } catch (IOException var4) {
         var0.standardmessage.error("doclet.exception_encountered", var4.toString(), var2);
         throw new DocletAbortException(var4);
      }
   }

   protected void addProfilesList(Profiles var1, String var2, String var3, Content var4) {
      HtmlTree var5 = HtmlTree.HEADING(HtmlConstants.PROFILE_HEADING, true, this.profilesLabel);
      HtmlTree var6 = HtmlTree.DIV(HtmlStyle.indexContainer, var5);
      HtmlTree var7 = new HtmlTree(HtmlTag.UL);
      var7.setTitle(this.profilesLabel);

      for(int var9 = 1; var9 < var1.getProfileCount(); ++var9) {
         String var8 = Profile.lookup(var9).name;
         if (this.configuration.shouldDocumentProfile(var8)) {
            var7.addContent(this.getProfile(var8));
         }
      }

      var6.addContent((Content)var7);
      var4.addContent((Content)var6);
   }

   protected Content getProfile(String var1) {
      StringContent var3 = new StringContent(var1);
      Content var2 = this.getHyperLink(DocPaths.profileFrame(var1), var3, "", "packageListFrame");
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

   protected void addAllPackagesLink(Content var1) {
      Content var2 = this.getHyperLink(DocPaths.OVERVIEW_FRAME, this.allpackagesLabel, "", "packageListFrame");
      HtmlTree var3 = HtmlTree.SPAN(var2);
      var1.addContent((Content)var3);
   }

   protected void addNavigationBarFooter(Content var1) {
      HtmlTree var2 = HtmlTree.P(this.getSpace());
      var1.addContent((Content)var2);
   }

   protected void addProfilePackagesList(Profiles var1, String var2, String var3, Content var4, String var5) {
   }
}
