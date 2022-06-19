package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.IndexBuilder;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;
import java.util.List;

public class AbstractIndexWriter extends HtmlDocletWriter {
   protected IndexBuilder indexbuilder;

   protected AbstractIndexWriter(ConfigurationImpl var1, DocPath var2, IndexBuilder var3) throws IOException {
      super(var1, var2);
      this.indexbuilder = var3;
   }

   protected Content getNavLinkIndex() {
      HtmlTree var1 = HtmlTree.LI(HtmlStyle.navBarCell1Rev, this.indexLabel);
      return var1;
   }

   protected void addContents(Character var1, List var2, Content var3) {
      String var4 = var1.toString();
      var3.addContent(this.getMarkerAnchorForIndex(var4));
      StringContent var5 = new StringContent(var4);
      HtmlTree var6 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, false, HtmlStyle.title, var5);
      var3.addContent((Content)var6);
      int var7 = var2.size();
      if (var7 > 0) {
         HtmlTree var8 = new HtmlTree(HtmlTag.DL);

         for(int var9 = 0; var9 < var7; ++var9) {
            Doc var10 = (Doc)var2.get(var9);
            if (var10 instanceof MemberDoc) {
               this.addDescription((MemberDoc)((MemberDoc)var10), var8);
            } else if (var10 instanceof ClassDoc) {
               this.addDescription((ClassDoc)((ClassDoc)var10), var8);
            } else if (var10 instanceof PackageDoc) {
               this.addDescription((PackageDoc)((PackageDoc)var10), var8);
            }
         }

         var3.addContent((Content)var8);
      }

   }

   protected void addDescription(PackageDoc var1, Content var2) {
      Content var3 = this.getPackageLink(var1, new StringContent(Util.getPackageName(var1)));
      HtmlTree var4 = HtmlTree.DT(var3);
      var4.addContent(" - ");
      var4.addContent(this.getResource("doclet.package"));
      var4.addContent(" " + var1.name());
      var2.addContent((Content)var4);
      HtmlTree var5 = new HtmlTree(HtmlTag.DD);
      this.addSummaryComment(var1, var5);
      var2.addContent((Content)var5);
   }

   protected void addDescription(ClassDoc var1, Content var2) {
      Content var3 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.INDEX, var1)).strong(true));
      HtmlTree var4 = HtmlTree.DT(var3);
      var4.addContent(" - ");
      this.addClassInfo(var1, var4);
      var2.addContent((Content)var4);
      HtmlTree var5 = new HtmlTree(HtmlTag.DD);
      this.addComment(var1, var5);
      var2.addContent((Content)var5);
   }

   protected void addClassInfo(ClassDoc var1, Content var2) {
      var2.addContent(this.getResource("doclet.in", Util.getTypeName(this.configuration, var1, false), this.getPackageLink(var1.containingPackage(), Util.getPackageName(var1.containingPackage()))));
   }

   protected void addDescription(MemberDoc var1, Content var2) {
      String var3 = var1 instanceof ExecutableMemberDoc ? var1.name() + ((ExecutableMemberDoc)var1).flatSignature() : var1.name();
      HtmlTree var4 = HtmlTree.SPAN(HtmlStyle.memberNameLink, this.getDocLink(LinkInfoImpl.Kind.INDEX, var1, var3));
      HtmlTree var5 = HtmlTree.DT(var4);
      var5.addContent(" - ");
      this.addMemberDesc(var1, var5);
      var2.addContent((Content)var5);
      HtmlTree var6 = new HtmlTree(HtmlTag.DD);
      this.addComment(var1, var6);
      var2.addContent((Content)var6);
   }

   protected void addComment(ProgramElementDoc var1, Content var2) {
      HtmlTree var4 = HtmlTree.SPAN(HtmlStyle.deprecatedLabel, this.deprecatedPhrase);
      HtmlTree var5 = new HtmlTree(HtmlTag.DIV);
      var5.addStyle(HtmlStyle.block);
      if (Util.isDeprecated(var1)) {
         var5.addContent((Content)var4);
         Tag[] var3;
         if ((var3 = var1.tags("deprecated")).length > 0) {
            this.addInlineDeprecatedComment(var1, var3[0], var5);
         }

         var2.addContent((Content)var5);
      } else {
         for(ClassDoc var6 = var1.containingClass(); var6 != null; var6 = var6.containingClass()) {
            if (Util.isDeprecated(var6)) {
               var5.addContent((Content)var4);
               var2.addContent((Content)var5);
               break;
            }
         }

         this.addSummaryComment(var1, var2);
      }

   }

   protected void addMemberDesc(MemberDoc var1, Content var2) {
      ClassDoc var3 = var1.containingClass();
      String var4 = Util.getTypeName(this.configuration, var3, true) + " ";
      if (var1.isField()) {
         if (var1.isStatic()) {
            var2.addContent(this.getResource("doclet.Static_variable_in", var4));
         } else {
            var2.addContent(this.getResource("doclet.Variable_in", var4));
         }
      } else if (var1.isConstructor()) {
         var2.addContent(this.getResource("doclet.Constructor_for", var4));
      } else if (var1.isMethod()) {
         if (var1.isStatic()) {
            var2.addContent(this.getResource("doclet.Static_method_in", var4));
         } else {
            var2.addContent(this.getResource("doclet.Method_in", var4));
         }
      }

      this.addPreQualifiedClassLink(LinkInfoImpl.Kind.INDEX, var3, false, var2);
   }

   public Content getMarkerAnchorForIndex(String var1) {
      return this.getMarkerAnchor(this.getNameForIndex(var1), (Content)null);
   }

   public String getNameForIndex(String var1) {
      return "I:" + this.getName(var1);
   }
}
