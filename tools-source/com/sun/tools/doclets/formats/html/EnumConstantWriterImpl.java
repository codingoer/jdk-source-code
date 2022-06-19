package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.EnumConstantWriter;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import java.io.IOException;

public class EnumConstantWriterImpl extends AbstractMemberWriter implements EnumConstantWriter, MemberSummaryWriter {
   public EnumConstantWriterImpl(SubWriterHolderWriter var1, ClassDoc var2) {
      super(var1, var2);
   }

   public EnumConstantWriterImpl(SubWriterHolderWriter var1) {
      super(var1);
   }

   public Content getMemberSummaryHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_ENUM_CONSTANT_SUMMARY);
      Content var3 = this.writer.getMemberTreeHeader();
      this.writer.addSummaryHeader(this, var1, var3);
      return var3;
   }

   public Content getEnumConstantsDetailsTreeHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_ENUM_CONSTANT_DETAILS);
      Content var3 = this.writer.getMemberTreeHeader();
      var3.addContent(this.writer.getMarkerAnchor(SectionName.ENUM_CONSTANT_DETAIL));
      HtmlTree var4 = HtmlTree.HEADING(HtmlConstants.DETAILS_HEADING, this.writer.enumConstantsDetailsLabel);
      var3.addContent((Content)var4);
      return var3;
   }

   public Content getEnumConstantsTreeHeader(FieldDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(var1.name()));
      Content var3 = this.writer.getMemberTreeHeader();
      HtmlTree var4 = new HtmlTree(HtmlConstants.MEMBER_HEADING);
      var4.addContent(var1.name());
      var3.addContent((Content)var4);
      return var3;
   }

   public Content getSignature(FieldDoc var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.PRE);
      this.writer.addAnnotationInfo(var1, var2);
      this.addModifiers(var1, var2);
      Content var3 = this.writer.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.MEMBER, var1.type()));
      var2.addContent(var3);
      var2.addContent(" ");
      if (this.configuration.linksource) {
         StringContent var4 = new StringContent(var1.name());
         this.writer.addSrcLink(var1, var4, var2);
      } else {
         this.addName(var1.name(), var2);
      }

      return var2;
   }

   public void addDeprecated(FieldDoc var1, Content var2) {
      this.addDeprecatedInfo(var1, var2);
   }

   public void addComments(FieldDoc var1, Content var2) {
      this.addComment(var1, var2);
   }

   public void addTags(FieldDoc var1, Content var2) {
      this.writer.addTagsInfo(var1, var2);
   }

   public Content getEnumConstantsDetails(Content var1) {
      return this.getMemberTree(var1);
   }

   public Content getEnumConstants(Content var1, boolean var2) {
      return this.getMemberTree(var1, var2);
   }

   public void close() throws IOException {
      this.writer.close();
   }

   public int getMemberKind() {
      return 1;
   }

   public void addSummaryLabel(Content var1) {
      HtmlTree var2 = HtmlTree.HEADING(HtmlConstants.SUMMARY_HEADING, this.writer.getResource("doclet.Enum_Constant_Summary"));
      var1.addContent((Content)var2);
   }

   public String getTableSummary() {
      return this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Enum_Constant_Summary"), this.configuration.getText("doclet.enum_constants"));
   }

   public Content getCaption() {
      return this.configuration.getResource("doclet.Enum_Constants");
   }

   public String[] getSummaryTableHeader(ProgramElementDoc var1) {
      String[] var2 = new String[]{this.configuration.getText("doclet.0_and_1", this.configuration.getText("doclet.Enum_Constant"), this.configuration.getText("doclet.Description"))};
      return var2;
   }

   public void addSummaryAnchor(ClassDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(SectionName.ENUM_CONSTANT_SUMMARY));
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

   public void setSummaryColumnStyle(HtmlTree var1) {
      var1.addStyle(HtmlStyle.colOne);
   }

   protected void addInheritedSummaryLink(ClassDoc var1, ProgramElementDoc var2, Content var3) {
   }

   protected void addSummaryType(ProgramElementDoc var1, Content var2) {
   }

   protected Content getDeprecatedLink(ProgramElementDoc var1) {
      return this.writer.getDocLink(LinkInfoImpl.Kind.MEMBER, (MemberDoc)var1, ((FieldDoc)var1).qualifiedName());
   }

   protected Content getNavSummaryLink(ClassDoc var1, boolean var2) {
      if (var2) {
         return var1 == null ? this.writer.getHyperLink(SectionName.ENUM_CONSTANT_SUMMARY, this.writer.getResource("doclet.navEnum")) : this.writer.getHyperLink(SectionName.ENUM_CONSTANTS_INHERITANCE, this.configuration.getClassName(var1), this.writer.getResource("doclet.navEnum"));
      } else {
         return this.writer.getResource("doclet.navEnum");
      }
   }

   protected void addNavDetailLink(boolean var1, Content var2) {
      if (var1) {
         var2.addContent(this.writer.getHyperLink(SectionName.ENUM_CONSTANT_DETAIL, this.writer.getResource("doclet.navEnum")));
      } else {
         var2.addContent(this.writer.getResource("doclet.navEnum"));
      }

   }
}
