package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeRequiredMemberWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import java.io.IOException;

public class AnnotationTypeRequiredMemberWriterImpl extends AbstractMemberWriter implements AnnotationTypeRequiredMemberWriter, MemberSummaryWriter {
   public AnnotationTypeRequiredMemberWriterImpl(SubWriterHolderWriter var1, AnnotationTypeDoc var2) {
      super(var1, var2);
   }

   public Content getMemberSummaryHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_ANNOTATION_TYPE_REQUIRED_MEMBER_SUMMARY);
      Content var3 = this.writer.getMemberTreeHeader();
      this.writer.addSummaryHeader(this, var1, var3);
      return var3;
   }

   public Content getMemberTreeHeader() {
      return this.writer.getMemberTreeHeader();
   }

   public void addAnnotationDetailsMarker(Content var1) {
      var1.addContent(HtmlConstants.START_OF_ANNOTATION_TYPE_DETAILS);
   }

   public void addAnnotationDetailsTreeHeader(ClassDoc var1, Content var2) {
      if (!this.writer.printedAnnotationHeading) {
         var2.addContent(this.writer.getMarkerAnchor(SectionName.ANNOTATION_TYPE_ELEMENT_DETAIL));
         HtmlTree var3 = HtmlTree.HEADING(HtmlConstants.DETAILS_HEADING, this.writer.annotationTypeDetailsLabel);
         var2.addContent((Content)var3);
         this.writer.printedAnnotationHeading = true;
      }

   }

   public Content getAnnotationDocTreeHeader(MemberDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(var1.name() + ((ExecutableMemberDoc)var1).signature()));
      Content var3 = this.writer.getMemberTreeHeader();
      HtmlTree var4 = new HtmlTree(HtmlConstants.MEMBER_HEADING);
      var4.addContent(var1.name());
      var3.addContent((Content)var4);
      return var3;
   }

   public Content getSignature(MemberDoc var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.PRE);
      this.writer.addAnnotationInfo(var1, var2);
      this.addModifiers(var1, var2);
      Content var3 = this.writer.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.MEMBER, this.getType(var1)));
      var2.addContent(var3);
      var2.addContent(this.writer.getSpace());
      if (this.configuration.linksource) {
         StringContent var4 = new StringContent(var1.name());
         this.writer.addSrcLink(var1, var4, var2);
      } else {
         this.addName(var1.name(), var2);
      }

      return var2;
   }

   public void addDeprecated(MemberDoc var1, Content var2) {
      this.addDeprecatedInfo(var1, var2);
   }

   public void addComments(MemberDoc var1, Content var2) {
      this.addComment(var1, var2);
   }

   public void addTags(MemberDoc var1, Content var2) {
      this.writer.addTagsInfo(var1, var2);
   }

   public Content getAnnotationDetails(Content var1) {
      return this.getMemberTree(var1);
   }

   public Content getAnnotationDoc(Content var1, boolean var2) {
      return this.getMemberTree(var1, var2);
   }

   public void close() throws IOException {
      this.writer.close();
   }

   public void addSummaryLabel(Content var1) {
      HtmlTree var2 = HtmlTree.HEADING(HtmlConstants.SUMMARY_HEADING, this.writer.getResource("doclet.Annotation_Type_Required_Member_Summary"));
      var1.addContent((Content)var2);
   }

   public String getTableSummary() {
      return this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Annotation_Type_Required_Member_Summary"), this.configuration.getText("doclet.annotation_type_required_members"));
   }

   public Content getCaption() {
      return this.configuration.getResource("doclet.Annotation_Type_Required_Members");
   }

   public String[] getSummaryTableHeader(ProgramElementDoc var1) {
      String[] var2 = new String[]{this.writer.getModifierTypeHeader(), this.configuration.getText("doclet.0_and_1", this.configuration.getText("doclet.Annotation_Type_Required_Member"), this.configuration.getText("doclet.Description"))};
      return var2;
   }

   public void addSummaryAnchor(ClassDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(SectionName.ANNOTATION_TYPE_REQUIRED_ELEMENT_SUMMARY));
   }

   public void addInheritedSummaryAnchor(ClassDoc var1, Content var2) {
   }

   public void addInheritedSummaryLabel(ClassDoc var1, Content var2) {
   }

   protected void addSummaryLink(LinkInfoImpl.Kind var1, ClassDoc var2, ProgramElementDoc var3, Content var4) {
      HtmlTree var5 = HtmlTree.SPAN(HtmlStyle.memberNameLink, this.writer.getDocLink(var1, (MemberDoc)var3, var3.name(), false));
      HtmlTree var6 = HtmlTree.CODE(var5);
      var4.addContent((Content)var6);
   }

   protected void addInheritedSummaryLink(ClassDoc var1, ProgramElementDoc var2, Content var3) {
   }

   protected void addSummaryType(ProgramElementDoc var1, Content var2) {
      MemberDoc var3 = (MemberDoc)var1;
      this.addModifierAndType(var3, this.getType(var3), var2);
   }

   protected Content getDeprecatedLink(ProgramElementDoc var1) {
      return this.writer.getDocLink(LinkInfoImpl.Kind.MEMBER, (MemberDoc)var1, ((MemberDoc)var1).qualifiedName());
   }

   protected Content getNavSummaryLink(ClassDoc var1, boolean var2) {
      return var2 ? this.writer.getHyperLink(SectionName.ANNOTATION_TYPE_REQUIRED_ELEMENT_SUMMARY, this.writer.getResource("doclet.navAnnotationTypeRequiredMember")) : this.writer.getResource("doclet.navAnnotationTypeRequiredMember");
   }

   protected void addNavDetailLink(boolean var1, Content var2) {
      if (var1) {
         var2.addContent(this.writer.getHyperLink(SectionName.ANNOTATION_TYPE_ELEMENT_DETAIL, this.writer.getResource("doclet.navAnnotationTypeMember")));
      } else {
         var2.addContent(this.writer.getResource("doclet.navAnnotationTypeMember"));
      }

   }

   private Type getType(MemberDoc var1) {
      return var1 instanceof FieldDoc ? ((FieldDoc)var1).type() : ((MethodDoc)var1).returnType();
   }
}
