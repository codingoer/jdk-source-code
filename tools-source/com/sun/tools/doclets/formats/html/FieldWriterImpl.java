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
import com.sun.tools.doclets.internal.toolkit.FieldWriter;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;

public class FieldWriterImpl extends AbstractMemberWriter implements FieldWriter, MemberSummaryWriter {
   public FieldWriterImpl(SubWriterHolderWriter var1, ClassDoc var2) {
      super(var1, var2);
   }

   public FieldWriterImpl(SubWriterHolderWriter var1) {
      super(var1);
   }

   public Content getMemberSummaryHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_FIELD_SUMMARY);
      Content var3 = this.writer.getMemberTreeHeader();
      this.writer.addSummaryHeader(this, var1, var3);
      return var3;
   }

   public Content getFieldDetailsTreeHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_FIELD_DETAILS);
      Content var3 = this.writer.getMemberTreeHeader();
      var3.addContent(this.writer.getMarkerAnchor(SectionName.FIELD_DETAIL));
      HtmlTree var4 = HtmlTree.HEADING(HtmlConstants.DETAILS_HEADING, this.writer.fieldDetailsLabel);
      var3.addContent((Content)var4);
      return var3;
   }

   public Content getFieldDocTreeHeader(FieldDoc var1, Content var2) {
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
      ClassDoc var3 = var1.containingClass();
      if (var1.inlineTags().length > 0) {
         if (var3.equals(this.classdoc) || !var3.isPublic() && !Util.isLinkable(var3, this.configuration)) {
            this.writer.addInlineComment(var1, var2);
         } else {
            Content var4 = this.writer.getDocLink(LinkInfoImpl.Kind.FIELD_DOC_COPY, var3, var1, var3.isIncluded() ? var3.typeName() : var3.qualifiedTypeName(), false);
            HtmlTree var5 = HtmlTree.CODE(var4);
            HtmlTree var6 = HtmlTree.SPAN(HtmlStyle.descfrmTypeLabel, var3.isClass() ? this.writer.descfrmClassLabel : this.writer.descfrmInterfaceLabel);
            var6.addContent(this.writer.getSpace());
            var6.addContent((Content)var5);
            var2.addContent((Content)HtmlTree.DIV(HtmlStyle.block, var6));
            this.writer.addInlineComment(var1, var2);
         }
      }

   }

   public void addTags(FieldDoc var1, Content var2) {
      this.writer.addTagsInfo(var1, var2);
   }

   public Content getFieldDetails(Content var1) {
      return this.getMemberTree(var1);
   }

   public Content getFieldDoc(Content var1, boolean var2) {
      return this.getMemberTree(var1, var2);
   }

   public void close() throws IOException {
      this.writer.close();
   }

   public int getMemberKind() {
      return 2;
   }

   public void addSummaryLabel(Content var1) {
      HtmlTree var2 = HtmlTree.HEADING(HtmlConstants.SUMMARY_HEADING, this.writer.getResource("doclet.Field_Summary"));
      var1.addContent((Content)var2);
   }

   public String getTableSummary() {
      return this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Field_Summary"), this.configuration.getText("doclet.fields"));
   }

   public Content getCaption() {
      return this.configuration.getResource("doclet.Fields");
   }

   public String[] getSummaryTableHeader(ProgramElementDoc var1) {
      String[] var2 = new String[]{this.writer.getModifierTypeHeader(), this.configuration.getText("doclet.0_and_1", this.configuration.getText("doclet.Field"), this.configuration.getText("doclet.Description"))};
      return var2;
   }

   public void addSummaryAnchor(ClassDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(SectionName.FIELD_SUMMARY));
   }

   public void addInheritedSummaryAnchor(ClassDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(SectionName.FIELDS_INHERITANCE, this.configuration.getClassName(var1)));
   }

   public void addInheritedSummaryLabel(ClassDoc var1, Content var2) {
      Content var3 = this.writer.getPreQualifiedClassLink(LinkInfoImpl.Kind.MEMBER, var1, false);
      StringContent var4 = new StringContent(var1.isClass() ? this.configuration.getText("doclet.Fields_Inherited_From_Class") : this.configuration.getText("doclet.Fields_Inherited_From_Interface"));
      HtmlTree var5 = HtmlTree.HEADING(HtmlConstants.INHERITED_SUMMARY_HEADING, var4);
      var5.addContent(this.writer.getSpace());
      var5.addContent(var3);
      var2.addContent((Content)var5);
   }

   protected void addSummaryLink(LinkInfoImpl.Kind var1, ClassDoc var2, ProgramElementDoc var3, Content var4) {
      HtmlTree var5 = HtmlTree.SPAN(HtmlStyle.memberNameLink, this.writer.getDocLink(var1, var2, (MemberDoc)var3, var3.name(), false));
      HtmlTree var6 = HtmlTree.CODE(var5);
      var4.addContent((Content)var6);
   }

   protected void addInheritedSummaryLink(ClassDoc var1, ProgramElementDoc var2, Content var3) {
      var3.addContent(this.writer.getDocLink(LinkInfoImpl.Kind.MEMBER, var1, (MemberDoc)var2, var2.name(), false));
   }

   protected void addSummaryType(ProgramElementDoc var1, Content var2) {
      FieldDoc var3 = (FieldDoc)var1;
      this.addModifierAndType(var3, var3.type(), var2);
   }

   protected Content getDeprecatedLink(ProgramElementDoc var1) {
      return this.writer.getDocLink(LinkInfoImpl.Kind.MEMBER, (MemberDoc)var1, ((FieldDoc)var1).qualifiedName());
   }

   protected Content getNavSummaryLink(ClassDoc var1, boolean var2) {
      if (var2) {
         return var1 == null ? this.writer.getHyperLink(SectionName.FIELD_SUMMARY, this.writer.getResource("doclet.navField")) : this.writer.getHyperLink(SectionName.FIELDS_INHERITANCE, this.configuration.getClassName(var1), this.writer.getResource("doclet.navField"));
      } else {
         return this.writer.getResource("doclet.navField");
      }
   }

   protected void addNavDetailLink(boolean var1, Content var2) {
      if (var1) {
         var2.addContent(this.writer.getHyperLink(SectionName.FIELD_DETAIL, this.writer.getResource("doclet.navField")));
      } else {
         var2.addContent(this.writer.getResource("doclet.navField"));
      }

   }
}
