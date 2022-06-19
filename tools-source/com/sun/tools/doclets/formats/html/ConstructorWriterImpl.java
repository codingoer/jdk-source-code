package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.ConstructorWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;
import java.io.IOException;
import java.util.ArrayList;

public class ConstructorWriterImpl extends AbstractExecutableMemberWriter implements ConstructorWriter, MemberSummaryWriter {
   private boolean foundNonPubConstructor = false;

   public ConstructorWriterImpl(SubWriterHolderWriter var1, ClassDoc var2) {
      super(var1, var2);
      VisibleMemberMap var3 = new VisibleMemberMap(var2, 3, this.configuration);
      ArrayList var4 = new ArrayList(var3.getMembersFor(var2));

      for(int var5 = 0; var5 < var4.size(); ++var5) {
         if (((ProgramElementDoc)var4.get(var5)).isProtected() || ((ProgramElementDoc)var4.get(var5)).isPrivate()) {
            this.setFoundNonPubConstructor(true);
         }
      }

   }

   public ConstructorWriterImpl(SubWriterHolderWriter var1) {
      super(var1);
   }

   public Content getMemberSummaryHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_CONSTRUCTOR_SUMMARY);
      Content var3 = this.writer.getMemberTreeHeader();
      this.writer.addSummaryHeader(this, var1, var3);
      return var3;
   }

   public Content getConstructorDetailsTreeHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_CONSTRUCTOR_DETAILS);
      Content var3 = this.writer.getMemberTreeHeader();
      var3.addContent(this.writer.getMarkerAnchor(SectionName.CONSTRUCTOR_DETAIL));
      HtmlTree var4 = HtmlTree.HEADING(HtmlConstants.DETAILS_HEADING, this.writer.constructorDetailsLabel);
      var3.addContent((Content)var4);
      return var3;
   }

   public Content getConstructorDocTreeHeader(ConstructorDoc var1, Content var2) {
      String var3;
      if ((var3 = this.getErasureAnchor(var1)) != null) {
         var2.addContent(this.writer.getMarkerAnchor(var3));
      }

      var2.addContent(this.writer.getMarkerAnchor(this.writer.getAnchor(var1)));
      Content var4 = this.writer.getMemberTreeHeader();
      HtmlTree var5 = new HtmlTree(HtmlConstants.MEMBER_HEADING);
      var5.addContent(var1.name());
      var4.addContent((Content)var5);
      return var4;
   }

   public Content getSignature(ConstructorDoc var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.PRE);
      this.writer.addAnnotationInfo(var1, var2);
      this.addModifiers(var1, var2);
      if (this.configuration.linksource) {
         StringContent var3 = new StringContent(var1.name());
         this.writer.addSrcLink(var1, var3, var2);
      } else {
         this.addName(var1.name(), var2);
      }

      int var4 = var2.charCount();
      this.addParameters(var1, var2, var4);
      this.addExceptions(var1, var2, var4);
      return var2;
   }

   public void setSummaryColumnStyle(HtmlTree var1) {
      if (this.foundNonPubConstructor) {
         var1.addStyle(HtmlStyle.colLast);
      } else {
         var1.addStyle(HtmlStyle.colOne);
      }

   }

   public void addDeprecated(ConstructorDoc var1, Content var2) {
      this.addDeprecatedInfo(var1, var2);
   }

   public void addComments(ConstructorDoc var1, Content var2) {
      this.addComment(var1, var2);
   }

   public void addTags(ConstructorDoc var1, Content var2) {
      this.writer.addTagsInfo(var1, var2);
   }

   public Content getConstructorDetails(Content var1) {
      return this.getMemberTree(var1);
   }

   public Content getConstructorDoc(Content var1, boolean var2) {
      return this.getMemberTree(var1, var2);
   }

   public void close() throws IOException {
      this.writer.close();
   }

   public void setFoundNonPubConstructor(boolean var1) {
      this.foundNonPubConstructor = var1;
   }

   public void addSummaryLabel(Content var1) {
      HtmlTree var2 = HtmlTree.HEADING(HtmlConstants.SUMMARY_HEADING, this.writer.getResource("doclet.Constructor_Summary"));
      var1.addContent((Content)var2);
   }

   public String getTableSummary() {
      return this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Constructor_Summary"), this.configuration.getText("doclet.constructors"));
   }

   public Content getCaption() {
      return this.configuration.getResource("doclet.Constructors");
   }

   public String[] getSummaryTableHeader(ProgramElementDoc var1) {
      String[] var2;
      if (this.foundNonPubConstructor) {
         var2 = new String[]{this.configuration.getText("doclet.Modifier"), this.configuration.getText("doclet.0_and_1", this.configuration.getText("doclet.Constructor"), this.configuration.getText("doclet.Description"))};
      } else {
         var2 = new String[]{this.configuration.getText("doclet.0_and_1", this.configuration.getText("doclet.Constructor"), this.configuration.getText("doclet.Description"))};
      }

      return var2;
   }

   public void addSummaryAnchor(ClassDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(SectionName.CONSTRUCTOR_SUMMARY));
   }

   public void addInheritedSummaryAnchor(ClassDoc var1, Content var2) {
   }

   public void addInheritedSummaryLabel(ClassDoc var1, Content var2) {
   }

   public int getMemberKind() {
      return 3;
   }

   protected Content getNavSummaryLink(ClassDoc var1, boolean var2) {
      return var2 ? this.writer.getHyperLink(SectionName.CONSTRUCTOR_SUMMARY, this.writer.getResource("doclet.navConstructor")) : this.writer.getResource("doclet.navConstructor");
   }

   protected void addNavDetailLink(boolean var1, Content var2) {
      if (var1) {
         var2.addContent(this.writer.getHyperLink(SectionName.CONSTRUCTOR_DETAIL, this.writer.getResource("doclet.navConstructor")));
      } else {
         var2.addContent(this.writer.getResource("doclet.navConstructor"));
      }

   }

   protected void addSummaryType(ProgramElementDoc var1, Content var2) {
      if (this.foundNonPubConstructor) {
         HtmlTree var3 = new HtmlTree(HtmlTag.CODE);
         if (var1.isProtected()) {
            var3.addContent("protected ");
         } else if (var1.isPrivate()) {
            var3.addContent("private ");
         } else if (var1.isPublic()) {
            var3.addContent(this.writer.getSpace());
         } else {
            var3.addContent(this.configuration.getText("doclet.Package_private"));
         }

         var2.addContent((Content)var3);
      }

   }
}
