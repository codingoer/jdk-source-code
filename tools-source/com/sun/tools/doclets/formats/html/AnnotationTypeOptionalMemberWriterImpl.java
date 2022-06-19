package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeOptionalMemberWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import java.io.IOException;

public class AnnotationTypeOptionalMemberWriterImpl extends AnnotationTypeRequiredMemberWriterImpl implements AnnotationTypeOptionalMemberWriter, MemberSummaryWriter {
   public AnnotationTypeOptionalMemberWriterImpl(SubWriterHolderWriter var1, AnnotationTypeDoc var2) {
      super(var1, var2);
   }

   public Content getMemberSummaryHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_ANNOTATION_TYPE_OPTIONAL_MEMBER_SUMMARY);
      Content var3 = this.writer.getMemberTreeHeader();
      this.writer.addSummaryHeader(this, var1, var3);
      return var3;
   }

   public void addDefaultValueInfo(MemberDoc var1, Content var2) {
      if (((AnnotationTypeElementDoc)var1).defaultValue() != null) {
         HtmlTree var3 = HtmlTree.DT(this.writer.getResource("doclet.Default"));
         HtmlTree var4 = HtmlTree.DL(var3);
         HtmlTree var5 = HtmlTree.DD(new StringContent(((AnnotationTypeElementDoc)var1).defaultValue().toString()));
         var4.addContent((Content)var5);
         var2.addContent((Content)var4);
      }

   }

   public void close() throws IOException {
      this.writer.close();
   }

   public void addSummaryLabel(Content var1) {
      HtmlTree var2 = HtmlTree.HEADING(HtmlConstants.SUMMARY_HEADING, this.writer.getResource("doclet.Annotation_Type_Optional_Member_Summary"));
      var1.addContent((Content)var2);
   }

   public String getTableSummary() {
      return this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Annotation_Type_Optional_Member_Summary"), this.configuration.getText("doclet.annotation_type_optional_members"));
   }

   public Content getCaption() {
      return this.configuration.getResource("doclet.Annotation_Type_Optional_Members");
   }

   public String[] getSummaryTableHeader(ProgramElementDoc var1) {
      String[] var2 = new String[]{this.writer.getModifierTypeHeader(), this.configuration.getText("doclet.0_and_1", this.configuration.getText("doclet.Annotation_Type_Optional_Member"), this.configuration.getText("doclet.Description"))};
      return var2;
   }

   public void addSummaryAnchor(ClassDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(SectionName.ANNOTATION_TYPE_OPTIONAL_ELEMENT_SUMMARY));
   }

   protected Content getNavSummaryLink(ClassDoc var1, boolean var2) {
      return var2 ? this.writer.getHyperLink(SectionName.ANNOTATION_TYPE_OPTIONAL_ELEMENT_SUMMARY, this.writer.getResource("doclet.navAnnotationTypeOptionalMember")) : this.writer.getResource("doclet.navAnnotationTypeOptionalMember");
   }
}
