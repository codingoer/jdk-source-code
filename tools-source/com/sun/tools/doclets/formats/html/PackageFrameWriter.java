package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PackageFrameWriter extends HtmlDocletWriter {
   private PackageDoc packageDoc;
   private Set documentedClasses;

   public PackageFrameWriter(ConfigurationImpl var1, PackageDoc var2) throws IOException {
      super(var1, DocPath.forPackage(var2).resolve(DocPaths.PACKAGE_FRAME));
      this.packageDoc = var2;
      if (var1.root.specifiedPackages().length == 0) {
         this.documentedClasses = new HashSet(Arrays.asList(var1.root.classes()));
      }

   }

   public static void generate(ConfigurationImpl var0, PackageDoc var1) {
      try {
         PackageFrameWriter var2 = new PackageFrameWriter(var0, var1);
         String var3 = Util.getPackageName(var1);
         HtmlTree var4 = var2.getBody(false, var2.getWindowTitle(var3));
         StringContent var5 = new StringContent(var3);
         HtmlTree var6 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, HtmlStyle.bar, var2.getTargetPackageLink(var1, "classFrame", var5));
         var4.addContent((Content)var6);
         HtmlTree var7 = new HtmlTree(HtmlTag.DIV);
         var7.addStyle(HtmlStyle.indexContainer);
         var2.addClassListing(var7);
         var4.addContent((Content)var7);
         var2.printHtmlDocument(var0.metakeywords.getMetaKeywords(var1), false, var4);
         var2.close();
      } catch (IOException var8) {
         var0.standardmessage.error("doclet.exception_encountered", var8.toString(), DocPaths.PACKAGE_FRAME.getPath());
         throw new DocletAbortException(var8);
      }
   }

   protected void addClassListing(Content var1) {
      ConfigurationImpl var2 = this.configuration;
      if (this.packageDoc.isIncluded()) {
         this.addClassKindListing(this.packageDoc.interfaces(), this.getResource("doclet.Interfaces"), var1);
         this.addClassKindListing(this.packageDoc.ordinaryClasses(), this.getResource("doclet.Classes"), var1);
         this.addClassKindListing(this.packageDoc.enums(), this.getResource("doclet.Enums"), var1);
         this.addClassKindListing(this.packageDoc.exceptions(), this.getResource("doclet.Exceptions"), var1);
         this.addClassKindListing(this.packageDoc.errors(), this.getResource("doclet.Errors"), var1);
         this.addClassKindListing(this.packageDoc.annotationTypes(), this.getResource("doclet.AnnotationTypes"), var1);
      } else {
         String var3 = Util.getPackageName(this.packageDoc);
         this.addClassKindListing(var2.classDocCatalog.interfaces(var3), this.getResource("doclet.Interfaces"), var1);
         this.addClassKindListing(var2.classDocCatalog.ordinaryClasses(var3), this.getResource("doclet.Classes"), var1);
         this.addClassKindListing(var2.classDocCatalog.enums(var3), this.getResource("doclet.Enums"), var1);
         this.addClassKindListing(var2.classDocCatalog.exceptions(var3), this.getResource("doclet.Exceptions"), var1);
         this.addClassKindListing(var2.classDocCatalog.errors(var3), this.getResource("doclet.Errors"), var1);
         this.addClassKindListing(var2.classDocCatalog.annotationTypes(var3), this.getResource("doclet.AnnotationTypes"), var1);
      }

   }

   protected void addClassKindListing(ClassDoc[] var1, Content var2, Content var3) {
      var1 = Util.filterOutPrivateClasses(var1, this.configuration.javafx);
      if (var1.length > 0) {
         Arrays.sort(var1);
         boolean var4 = false;
         HtmlTree var5 = new HtmlTree(HtmlTag.UL);
         var5.setTitle(var2);

         for(int var6 = 0; var6 < var1.length; ++var6) {
            if ((this.documentedClasses == null || this.documentedClasses.contains(var1[var6])) && Util.isCoreClass(var1[var6]) && this.configuration.isGeneratedDoc(var1[var6])) {
               if (!var4) {
                  HtmlTree var7 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, true, var2);
                  var3.addContent((Content)var7);
                  var4 = true;
               }

               Object var10 = new StringContent(var1[var6].name());
               if (var1[var6].isInterface()) {
                  var10 = HtmlTree.SPAN(HtmlStyle.interfaceName, (Content)var10);
               }

               Content var8 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.PACKAGE_FRAME, var1[var6])).label((Content)var10).target("classFrame"));
               HtmlTree var9 = HtmlTree.LI(var8);
               var5.addContent((Content)var9);
            }
         }

         var3.addContent((Content)var5);
      }

   }
}
