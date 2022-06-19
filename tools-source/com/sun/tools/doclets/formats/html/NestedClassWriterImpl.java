package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import java.io.IOException;

public class NestedClassWriterImpl extends AbstractMemberWriter implements MemberSummaryWriter {
   public NestedClassWriterImpl(SubWriterHolderWriter var1, ClassDoc var2) {
      super(var1, var2);
   }

   public NestedClassWriterImpl(SubWriterHolderWriter var1) {
      super(var1);
   }

   public Content getMemberSummaryHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_NESTED_CLASS_SUMMARY);
      Content var3 = this.writer.getMemberTreeHeader();
      this.writer.addSummaryHeader(this, var1, var3);
      return var3;
   }

   public void close() throws IOException {
      this.writer.close();
   }

   public int getMemberKind() {
      return 0;
   }

   public void addSummaryLabel(Content var1) {
      HtmlTree var2 = HtmlTree.HEADING(HtmlConstants.SUMMARY_HEADING, this.writer.getResource("doclet.Nested_Class_Summary"));
      var1.addContent((Content)var2);
   }

   public String getTableSummary() {
      return this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Nested_Class_Summary"), this.configuration.getText("doclet.nested_classes"));
   }

   public Content getCaption() {
      return this.configuration.getResource("doclet.Nested_Classes");
   }

   public String[] getSummaryTableHeader(ProgramElementDoc var1) {
      String[] var2;
      if (var1.isInterface()) {
         var2 = new String[]{this.writer.getModifierTypeHeader(), this.configuration.getText("doclet.0_and_1", this.configuration.getText("doclet.Interface"), this.configuration.getText("doclet.Description"))};
      } else {
         var2 = new String[]{this.writer.getModifierTypeHeader(), this.configuration.getText("doclet.0_and_1", this.configuration.getText("doclet.Class"), this.configuration.getText("doclet.Description"))};
      }

      return var2;
   }

   public void addSummaryAnchor(ClassDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(SectionName.NESTED_CLASS_SUMMARY));
   }

   public void addInheritedSummaryAnchor(ClassDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(SectionName.NESTED_CLASSES_INHERITANCE, var1.qualifiedName()));
   }

   public void addInheritedSummaryLabel(ClassDoc var1, Content var2) {
      Content var3 = this.writer.getPreQualifiedClassLink(LinkInfoImpl.Kind.MEMBER, var1, false);
      StringContent var4 = new StringContent(var1.isInterface() ? this.configuration.getText("doclet.Nested_Classes_Interface_Inherited_From_Interface") : this.configuration.getText("doclet.Nested_Classes_Interfaces_Inherited_From_Class"));
      HtmlTree var5 = HtmlTree.HEADING(HtmlConstants.INHERITED_SUMMARY_HEADING, var4);
      var5.addContent(this.writer.getSpace());
      var5.addContent(var3);
      var2.addContent((Content)var5);
   }

   protected void addSummaryLink(LinkInfoImpl.Kind var1, ClassDoc var2, ProgramElementDoc var3, Content var4) {
      HtmlTree var5 = HtmlTree.SPAN(HtmlStyle.memberNameLink, this.writer.getLink(new LinkInfoImpl(this.configuration, var1, (ClassDoc)var3)));
      HtmlTree var6 = HtmlTree.CODE(var5);
      var4.addContent((Content)var6);
   }

   protected void addInheritedSummaryLink(ClassDoc var1, ProgramElementDoc var2, Content var3) {
      var3.addContent(this.writer.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.MEMBER, (ClassDoc)var2)));
   }

   protected void addSummaryType(ProgramElementDoc var1, Content var2) {
      ClassDoc var3 = (ClassDoc)var1;
      this.addModifierAndType(var3, (Type)null, var2);
   }

   protected Content getDeprecatedLink(ProgramElementDoc var1) {
      return this.writer.getQualifiedClassLink(LinkInfoImpl.Kind.MEMBER, (ClassDoc)var1);
   }

   protected Content getNavSummaryLink(ClassDoc var1, boolean var2) {
      if (var2) {
         return var1 == null ? this.writer.getHyperLink(SectionName.NESTED_CLASS_SUMMARY, this.writer.getResource("doclet.navNested")) : this.writer.getHyperLink(SectionName.NESTED_CLASSES_INHERITANCE, var1.qualifiedName(), this.writer.getResource("doclet.navNested"));
      } else {
         return this.writer.getResource("doclet.navNested");
      }
   }

   protected void addNavDetailLink(boolean var1, Content var2) {
   }
}
