package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
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
import com.sun.tools.javac.jvm.Profile;
import java.io.IOException;
import java.util.Arrays;

public class ProfilePackageFrameWriter extends HtmlDocletWriter {
   private PackageDoc packageDoc;

   public ProfilePackageFrameWriter(ConfigurationImpl var1, PackageDoc var2, String var3) throws IOException {
      super(var1, DocPath.forPackage(var2).resolve(DocPaths.profilePackageFrame(var3)));
      this.packageDoc = var2;
   }

   public static void generate(ConfigurationImpl var0, PackageDoc var1, int var2) {
      try {
         String var4 = Profile.lookup(var2).name;
         ProfilePackageFrameWriter var3 = new ProfilePackageFrameWriter(var0, var1, var4);
         StringBuilder var5 = new StringBuilder(var4);
         String var6 = " - ";
         var5.append(var6);
         String var7 = Util.getPackageName(var1);
         var5.append(var7);
         HtmlTree var8 = var3.getBody(false, var3.getWindowTitle(var5.toString()));
         StringContent var9 = new StringContent(var4);
         StringContent var10 = new StringContent(var6);
         RawHtml var11 = new RawHtml(var7);
         HtmlTree var12 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, HtmlStyle.bar, var3.getTargetProfileLink("classFrame", var9, var4));
         var12.addContent((Content)var10);
         var12.addContent(var3.getTargetProfilePackageLink(var1, "classFrame", var11, var4));
         var8.addContent((Content)var12);
         HtmlTree var13 = new HtmlTree(HtmlTag.DIV);
         var13.addStyle(HtmlStyle.indexContainer);
         var3.addClassListing(var13, var2);
         var8.addContent((Content)var13);
         var3.printHtmlDocument(var0.metakeywords.getMetaKeywords(var1), false, var8);
         var3.close();
      } catch (IOException var14) {
         var0.standardmessage.error("doclet.exception_encountered", var14.toString(), DocPaths.PACKAGE_FRAME.getPath());
         throw new DocletAbortException(var14);
      }
   }

   protected void addClassListing(Content var1, int var2) {
      if (this.packageDoc.isIncluded()) {
         this.addClassKindListing(this.packageDoc.interfaces(), this.getResource("doclet.Interfaces"), var1, var2);
         this.addClassKindListing(this.packageDoc.ordinaryClasses(), this.getResource("doclet.Classes"), var1, var2);
         this.addClassKindListing(this.packageDoc.enums(), this.getResource("doclet.Enums"), var1, var2);
         this.addClassKindListing(this.packageDoc.exceptions(), this.getResource("doclet.Exceptions"), var1, var2);
         this.addClassKindListing(this.packageDoc.errors(), this.getResource("doclet.Errors"), var1, var2);
         this.addClassKindListing(this.packageDoc.annotationTypes(), this.getResource("doclet.AnnotationTypes"), var1, var2);
      }

   }

   protected void addClassKindListing(ClassDoc[] var1, Content var2, Content var3, int var4) {
      if (var1.length > 0) {
         Arrays.sort(var1);
         boolean var5 = false;
         HtmlTree var6 = new HtmlTree(HtmlTag.UL);
         var6.setTitle(var2);

         for(int var7 = 0; var7 < var1.length; ++var7) {
            if (this.isTypeInProfile(var1[var7], var4) && Util.isCoreClass(var1[var7]) && this.configuration.isGeneratedDoc(var1[var7])) {
               if (!var5) {
                  HtmlTree var8 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, true, var2);
                  var3.addContent((Content)var8);
                  var5 = true;
               }

               Object var11 = new StringContent(var1[var7].name());
               if (var1[var7].isInterface()) {
                  var11 = HtmlTree.SPAN(HtmlStyle.interfaceName, (Content)var11);
               }

               Content var9 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.PACKAGE_FRAME, var1[var7])).label((Content)var11).target("classFrame"));
               HtmlTree var10 = HtmlTree.LI(var9);
               var6.addContent((Content)var10);
            }
         }

         var3.addContent((Content)var6);
      }

   }
}
