package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.MethodWriter;
import com.sun.tools.doclets.internal.toolkit.util.ImplementedMethods;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;

public class MethodWriterImpl extends AbstractExecutableMemberWriter implements MethodWriter, MemberSummaryWriter {
   public MethodWriterImpl(SubWriterHolderWriter var1, ClassDoc var2) {
      super(var1, var2);
   }

   public MethodWriterImpl(SubWriterHolderWriter var1) {
      super(var1);
   }

   public Content getMemberSummaryHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_METHOD_SUMMARY);
      Content var3 = this.writer.getMemberTreeHeader();
      this.writer.addSummaryHeader(this, var1, var3);
      return var3;
   }

   public Content getMethodDetailsTreeHeader(ClassDoc var1, Content var2) {
      var2.addContent(HtmlConstants.START_OF_METHOD_DETAILS);
      Content var3 = this.writer.getMemberTreeHeader();
      var3.addContent(this.writer.getMarkerAnchor(SectionName.METHOD_DETAIL));
      HtmlTree var4 = HtmlTree.HEADING(HtmlConstants.DETAILS_HEADING, this.writer.methodDetailsLabel);
      var3.addContent((Content)var4);
      return var3;
   }

   public Content getMethodDocTreeHeader(MethodDoc var1, Content var2) {
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

   public Content getSignature(MethodDoc var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.PRE);
      this.writer.addAnnotationInfo(var1, var2);
      this.addModifiers(var1, var2);
      this.addTypeParameters(var1, var2);
      this.addReturnType(var1, var2);
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

   public void addDeprecated(MethodDoc var1, Content var2) {
      this.addDeprecatedInfo(var1, var2);
   }

   public void addComments(Type var1, MethodDoc var2, Content var3) {
      ClassDoc var4 = var1.asClassDoc();
      if (var2.inlineTags().length > 0) {
         if (var1.asClassDoc().equals(this.classdoc) || !var4.isPublic() && !Util.isLinkable(var4, this.configuration)) {
            this.writer.addInlineComment(var2, var3);
         } else {
            Content var5 = this.writer.getDocLink(LinkInfoImpl.Kind.METHOD_DOC_COPY, var1.asClassDoc(), var2, var1.asClassDoc().isIncluded() ? var1.typeName() : var1.qualifiedTypeName(), false);
            HtmlTree var6 = HtmlTree.CODE(var5);
            HtmlTree var7 = HtmlTree.SPAN(HtmlStyle.descfrmTypeLabel, var1.asClassDoc().isClass() ? this.writer.descfrmClassLabel : this.writer.descfrmInterfaceLabel);
            var7.addContent(this.writer.getSpace());
            var7.addContent((Content)var6);
            var3.addContent((Content)HtmlTree.DIV(HtmlStyle.block, var7));
            this.writer.addInlineComment(var2, var3);
         }
      }

   }

   public void addTags(MethodDoc var1, Content var2) {
      this.writer.addTagsInfo(var1, var2);
   }

   public Content getMethodDetails(Content var1) {
      return this.getMemberTree(var1);
   }

   public Content getMethodDoc(Content var1, boolean var2) {
      return this.getMemberTree(var1, var2);
   }

   public void close() throws IOException {
      this.writer.close();
   }

   public int getMemberKind() {
      return 4;
   }

   public void addSummaryLabel(Content var1) {
      HtmlTree var2 = HtmlTree.HEADING(HtmlConstants.SUMMARY_HEADING, this.writer.getResource("doclet.Method_Summary"));
      var1.addContent((Content)var2);
   }

   public String getTableSummary() {
      return this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText("doclet.Method_Summary"), this.configuration.getText("doclet.methods"));
   }

   public Content getCaption() {
      return this.configuration.getResource("doclet.Methods");
   }

   public String[] getSummaryTableHeader(ProgramElementDoc var1) {
      String[] var2 = new String[]{this.writer.getModifierTypeHeader(), this.configuration.getText("doclet.0_and_1", this.configuration.getText("doclet.Method"), this.configuration.getText("doclet.Description"))};
      return var2;
   }

   public void addSummaryAnchor(ClassDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(SectionName.METHOD_SUMMARY));
   }

   public void addInheritedSummaryAnchor(ClassDoc var1, Content var2) {
      var2.addContent(this.writer.getMarkerAnchor(SectionName.METHODS_INHERITANCE, this.configuration.getClassName(var1)));
   }

   public void addInheritedSummaryLabel(ClassDoc var1, Content var2) {
      Content var3 = this.writer.getPreQualifiedClassLink(LinkInfoImpl.Kind.MEMBER, var1, false);
      StringContent var4 = new StringContent(var1.isClass() ? this.configuration.getText("doclet.Methods_Inherited_From_Class") : this.configuration.getText("doclet.Methods_Inherited_From_Interface"));
      HtmlTree var5 = HtmlTree.HEADING(HtmlConstants.INHERITED_SUMMARY_HEADING, var4);
      var5.addContent(this.writer.getSpace());
      var5.addContent(var3);
      var2.addContent((Content)var5);
   }

   protected void addSummaryType(ProgramElementDoc var1, Content var2) {
      MethodDoc var3 = (MethodDoc)var1;
      this.addModifierAndType(var3, var3.returnType(), var2);
   }

   protected static void addOverridden(HtmlDocletWriter var0, Type var1, MethodDoc var2, Content var3) {
      if (!var0.configuration.nocomment) {
         ClassDoc var4 = var1.asClassDoc();
         if (var4.isPublic() || Util.isLinkable(var4, var0.configuration)) {
            if (!var1.asClassDoc().isIncluded() || var2.isIncluded()) {
               Content var5 = var0.overridesLabel;
               LinkInfoImpl.Kind var6 = LinkInfoImpl.Kind.METHOD_OVERRIDES;
               if (var2 != null) {
                  if (var1.asClassDoc().isAbstract() && var2.isAbstract()) {
                     var5 = var0.specifiedByLabel;
                     var6 = LinkInfoImpl.Kind.METHOD_SPECIFIED_BY;
                  }

                  HtmlTree var7 = HtmlTree.DT(HtmlTree.SPAN(HtmlStyle.overrideSpecifyLabel, var5));
                  var3.addContent((Content)var7);
                  Content var8 = var0.getLink(new LinkInfoImpl(var0.configuration, var6, var1));
                  HtmlTree var9 = HtmlTree.CODE(var8);
                  String var10 = var2.name();
                  Content var11 = var0.getLink((new LinkInfoImpl(var0.configuration, LinkInfoImpl.Kind.MEMBER, var1.asClassDoc())).where(var0.getName(var0.getAnchor(var2))).label(var10));
                  HtmlTree var12 = HtmlTree.CODE(var11);
                  HtmlTree var13 = HtmlTree.DD(var12);
                  var13.addContent(var0.getSpace());
                  var13.addContent(var0.getResource("doclet.in_class"));
                  var13.addContent(var0.getSpace());
                  var13.addContent((Content)var9);
                  var3.addContent((Content)var13);
               }

            }
         }
      }
   }

   protected static void addImplementsInfo(HtmlDocletWriter var0, MethodDoc var1, Content var2) {
      if (!var0.configuration.nocomment) {
         ImplementedMethods var3 = new ImplementedMethods(var1, var0.configuration);
         MethodDoc[] var4 = var3.build();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            MethodDoc var6 = var4[var5];
            Type var7 = var3.getMethodHolder(var6);
            Content var8 = var0.getLink(new LinkInfoImpl(var0.configuration, LinkInfoImpl.Kind.METHOD_SPECIFIED_BY, var7));
            HtmlTree var9 = HtmlTree.CODE(var8);
            HtmlTree var10 = HtmlTree.DT(HtmlTree.SPAN(HtmlStyle.overrideSpecifyLabel, var0.specifiedByLabel));
            var2.addContent((Content)var10);
            Content var11 = var0.getDocLink(LinkInfoImpl.Kind.MEMBER, var6, var6.name(), false);
            HtmlTree var12 = HtmlTree.CODE(var11);
            HtmlTree var13 = HtmlTree.DD(var12);
            var13.addContent(var0.getSpace());
            var13.addContent(var0.getResource("doclet.in_interface"));
            var13.addContent(var0.getSpace());
            var13.addContent((Content)var9);
            var2.addContent((Content)var13);
         }

      }
   }

   protected void addReturnType(MethodDoc var1, Content var2) {
      Type var3 = var1.returnType();
      if (var3 != null) {
         Content var4 = this.writer.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.RETURN_TYPE, var3));
         var2.addContent(var4);
         var2.addContent(this.writer.getSpace());
      }

   }

   protected Content getNavSummaryLink(ClassDoc var1, boolean var2) {
      if (var2) {
         return var1 == null ? this.writer.getHyperLink(SectionName.METHOD_SUMMARY, this.writer.getResource("doclet.navMethod")) : this.writer.getHyperLink(SectionName.METHODS_INHERITANCE, this.configuration.getClassName(var1), this.writer.getResource("doclet.navMethod"));
      } else {
         return this.writer.getResource("doclet.navMethod");
      }
   }

   protected void addNavDetailLink(boolean var1, Content var2) {
      if (var1) {
         var2.addContent(this.writer.getHyperLink(SectionName.METHOD_DETAIL, this.writer.getResource("doclet.navMethod")));
      } else {
         var2.addContent(this.writer.getResource("doclet.navMethod"));
      }

   }
}
