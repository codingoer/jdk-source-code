package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.builders.MemberSummaryBuilder;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;

public class AnnotationTypeWriterImpl extends SubWriterHolderWriter implements AnnotationTypeWriter {
   protected AnnotationTypeDoc annotationType;
   protected Type prev;
   protected Type next;

   public AnnotationTypeWriterImpl(ConfigurationImpl var1, AnnotationTypeDoc var2, Type var3, Type var4) throws Exception {
      super(var1, DocPath.forClass(var2));
      this.annotationType = var2;
      var1.currentcd = var2.asClassDoc();
      this.prev = var3;
      this.next = var4;
   }

   protected Content getNavLinkPackage() {
      Content var1 = this.getHyperLink(DocPaths.PACKAGE_SUMMARY, this.packageLabel);
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected Content getNavLinkClass() {
      HtmlTree var1 = HtmlTree.LI(HtmlStyle.navBarCell1Rev, this.classLabel);
      return var1;
   }

   protected Content getNavLinkClassUse() {
      Content var1 = this.getHyperLink(DocPaths.CLASS_USE.resolve(this.filename), this.useLabel);
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   public Content getNavLinkPrevious() {
      HtmlTree var1;
      if (this.prev != null) {
         Content var2 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS, this.prev.asClassDoc())).label(this.prevclassLabel).strong(true));
         var1 = HtmlTree.LI(var2);
      } else {
         var1 = HtmlTree.LI(this.prevclassLabel);
      }

      return var1;
   }

   public Content getNavLinkNext() {
      HtmlTree var1;
      if (this.next != null) {
         Content var2 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS, this.next.asClassDoc())).label(this.nextclassLabel).strong(true));
         var1 = HtmlTree.LI(var2);
      } else {
         var1 = HtmlTree.LI(this.nextclassLabel);
      }

      return var1;
   }

   public Content getHeader(String var1) {
      String var2 = this.annotationType.containingPackage() != null ? this.annotationType.containingPackage().name() : "";
      String var3 = this.annotationType.name();
      HtmlTree var4 = this.getBody(true, this.getWindowTitle(var3));
      this.addTop(var4);
      this.addNavLinks(true, var4);
      var4.addContent(HtmlConstants.START_OF_CLASS_DATA);
      HtmlTree var5 = new HtmlTree(HtmlTag.DIV);
      var5.addStyle(HtmlStyle.header);
      if (var2.length() > 0) {
         StringContent var6 = new StringContent(var2);
         HtmlTree var7 = HtmlTree.DIV(HtmlStyle.subTitle, var6);
         var5.addContent((Content)var7);
      }

      LinkInfoImpl var9 = new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_HEADER, this.annotationType);
      StringContent var10 = new StringContent(var1);
      HtmlTree var8 = HtmlTree.HEADING(HtmlConstants.CLASS_PAGE_HEADING, true, HtmlStyle.title, var10);
      var8.addContent(this.getTypeParameterLinks(var9));
      var5.addContent((Content)var8);
      var4.addContent((Content)var5);
      return var4;
   }

   public Content getAnnotationContentHeader() {
      return this.getContentHeader();
   }

   public void addFooter(Content var1) {
      var1.addContent(HtmlConstants.END_OF_CLASS_DATA);
      this.addNavLinks(false, var1);
      this.addBottom(var1);
   }

   public void printDocument(Content var1) throws IOException {
      this.printHtmlDocument(this.configuration.metakeywords.getMetaKeywords((ClassDoc)this.annotationType), true, var1);
   }

   public Content getAnnotationInfoTreeHeader() {
      return this.getMemberTreeHeader();
   }

   public Content getAnnotationInfo(Content var1) {
      return this.getMemberTree(HtmlStyle.description, var1);
   }

   public void addAnnotationTypeSignature(String var1, Content var2) {
      var2.addContent((Content)(new HtmlTree(HtmlTag.BR)));
      HtmlTree var3 = new HtmlTree(HtmlTag.PRE);
      this.addAnnotationInfo(this.annotationType, var3);
      var3.addContent(var1);
      LinkInfoImpl var4 = new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CLASS_SIGNATURE, this.annotationType);
      StringContent var5 = new StringContent(this.annotationType.name());
      Content var6 = this.getTypeParameterLinks(var4);
      if (this.configuration.linksource) {
         this.addSrcLink(this.annotationType, var5, var3);
         var3.addContent(var6);
      } else {
         HtmlTree var7 = HtmlTree.SPAN(HtmlStyle.memberNameLabel, var5);
         var7.addContent(var6);
         var3.addContent((Content)var7);
      }

      var2.addContent((Content)var3);
   }

   public void addAnnotationTypeDescription(Content var1) {
      if (!this.configuration.nocomment && this.annotationType.inlineTags().length > 0) {
         this.addInlineComment(this.annotationType, var1);
      }

   }

   public void addAnnotationTypeTagInfo(Content var1) {
      if (!this.configuration.nocomment) {
         this.addTagsInfo(this.annotationType, var1);
      }

   }

   public void addAnnotationTypeDeprecationInfo(Content var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.HR);
      var1.addContent((Content)var2);
      Tag[] var3 = this.annotationType.tags("deprecated");
      if (Util.isDeprecated(this.annotationType)) {
         HtmlTree var4 = HtmlTree.SPAN(HtmlStyle.deprecatedLabel, this.deprecatedPhrase);
         HtmlTree var5 = HtmlTree.DIV(HtmlStyle.block, var4);
         if (var3.length > 0) {
            Tag[] var6 = var3[0].inlineTags();
            if (var6.length > 0) {
               var5.addContent(this.getSpace());
               this.addInlineDeprecatedComment(this.annotationType, var3[0], var5);
            }
         }

         var1.addContent((Content)var5);
      }

   }

   protected Content getNavLinkTree() {
      Content var1 = this.getHyperLink(DocPaths.PACKAGE_TREE, this.treeLabel, "", "");
      HtmlTree var2 = HtmlTree.LI(var1);
      return var2;
   }

   protected void addSummaryDetailLinks(Content var1) {
      try {
         HtmlTree var2 = HtmlTree.DIV(this.getNavSummaryLinks());
         var2.addContent(this.getNavDetailLinks());
         var1.addContent((Content)var2);
      } catch (Exception var3) {
         var3.printStackTrace();
         throw new DocletAbortException(var3);
      }
   }

   protected Content getNavSummaryLinks() throws Exception {
      HtmlTree var1 = HtmlTree.LI(this.summaryLabel);
      var1.addContent(this.getSpace());
      HtmlTree var2 = HtmlTree.UL(HtmlStyle.subNavList, var1);
      MemberSummaryBuilder var3 = (MemberSummaryBuilder)this.configuration.getBuilderFactory().getMemberSummaryBuilder((AnnotationTypeWriter)this);
      HtmlTree var4 = new HtmlTree(HtmlTag.LI);
      this.addNavSummaryLink(var3, "doclet.navField", 5, var4);
      this.addNavGap(var4);
      var2.addContent((Content)var4);
      HtmlTree var5 = new HtmlTree(HtmlTag.LI);
      this.addNavSummaryLink(var3, "doclet.navAnnotationTypeRequiredMember", 7, var5);
      this.addNavGap(var5);
      var2.addContent((Content)var5);
      HtmlTree var6 = new HtmlTree(HtmlTag.LI);
      this.addNavSummaryLink(var3, "doclet.navAnnotationTypeOptionalMember", 6, var6);
      var2.addContent((Content)var6);
      return var2;
   }

   protected void addNavSummaryLink(MemberSummaryBuilder var1, String var2, int var3, Content var4) {
      AbstractMemberWriter var5 = (AbstractMemberWriter)var1.getMemberSummaryWriter(var3);
      if (var5 == null) {
         var4.addContent(this.getResource(var2));
      } else {
         var4.addContent(var5.getNavSummaryLink((ClassDoc)null, !var1.getVisibleMemberMap(var3).noVisibleMembers()));
      }

   }

   protected Content getNavDetailLinks() throws Exception {
      HtmlTree var1 = HtmlTree.LI(this.detailLabel);
      var1.addContent(this.getSpace());
      HtmlTree var2 = HtmlTree.UL(HtmlStyle.subNavList, var1);
      MemberSummaryBuilder var3 = (MemberSummaryBuilder)this.configuration.getBuilderFactory().getMemberSummaryBuilder((AnnotationTypeWriter)this);
      AbstractMemberWriter var4 = (AbstractMemberWriter)var3.getMemberSummaryWriter(5);
      AbstractMemberWriter var5 = (AbstractMemberWriter)var3.getMemberSummaryWriter(6);
      AbstractMemberWriter var6 = (AbstractMemberWriter)var3.getMemberSummaryWriter(7);
      HtmlTree var7 = new HtmlTree(HtmlTag.LI);
      if (var4 != null) {
         var4.addNavDetailLink(this.annotationType.fields().length > 0, var7);
      } else {
         var7.addContent(this.getResource("doclet.navField"));
      }

      this.addNavGap(var7);
      var2.addContent((Content)var7);
      HtmlTree var8;
      if (var5 != null) {
         var8 = new HtmlTree(HtmlTag.LI);
         var5.addNavDetailLink(this.annotationType.elements().length > 0, var8);
         var2.addContent((Content)var8);
      } else if (var6 != null) {
         var8 = new HtmlTree(HtmlTag.LI);
         var6.addNavDetailLink(this.annotationType.elements().length > 0, var8);
         var2.addContent((Content)var8);
      } else {
         var8 = HtmlTree.LI(this.getResource("doclet.navAnnotationTypeMember"));
         var2.addContent((Content)var8);
      }

      return var2;
   }

   protected void addNavGap(Content var1) {
      var1.addContent(this.getSpace());
      var1.addContent("|");
      var1.addContent(this.getSpace());
   }

   public AnnotationTypeDoc getAnnotationTypeDoc() {
      return this.annotationType;
   }
}
