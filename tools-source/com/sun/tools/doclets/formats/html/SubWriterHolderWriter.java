package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.MethodTypes;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class SubWriterHolderWriter extends HtmlDocletWriter {
   public SubWriterHolderWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
   }

   public void addSummaryHeader(AbstractMemberWriter var1, ClassDoc var2, Content var3) {
      var1.addSummaryAnchor(var2, var3);
      var1.addSummaryLabel(var3);
   }

   public Content getSummaryTableTree(AbstractMemberWriter var1, ClassDoc var2, List var3, boolean var4) {
      Content var5;
      if (var4) {
         var5 = this.getTableCaption(var1.methodTypes);
         this.generateMethodTypesScript(var1.typeMap, var1.methodTypes);
      } else {
         var5 = this.getTableCaption(var1.getCaption());
      }

      HtmlTree var6 = HtmlTree.TABLE(HtmlStyle.memberSummary, 0, 3, 0, var1.getTableSummary(), var5);
      var6.addContent(this.getSummaryTableHeader(var1.getSummaryTableHeader(var2), "col"));

      for(int var7 = 0; var7 < var3.size(); ++var7) {
         var6.addContent((Content)var3.get(var7));
      }

      return var6;
   }

   public Content getTableCaption(Set var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.CAPTION);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         MethodTypes var4 = (MethodTypes)var3.next();
         HtmlTree var5;
         HtmlTree var6;
         if (var4.isDefaultTab()) {
            var5 = HtmlTree.SPAN(this.configuration.getResource(var4.resourceKey()));
            var6 = HtmlTree.SPAN(var4.tabId(), HtmlStyle.activeTableTab, var5);
         } else {
            var5 = HtmlTree.SPAN(this.getMethodTypeLinks(var4));
            var6 = HtmlTree.SPAN(var4.tabId(), HtmlStyle.tableTab, var5);
         }

         HtmlTree var7 = HtmlTree.SPAN(HtmlStyle.tabEnd, this.getSpace());
         var6.addContent((Content)var7);
         var2.addContent((Content)var6);
      }

      return var2;
   }

   public Content getMethodTypeLinks(MethodTypes var1) {
      String var2 = "javascript:show(" + var1.value() + ");";
      HtmlTree var3 = HtmlTree.A(var2, this.configuration.getResource(var1.resourceKey()));
      return var3;
   }

   public void addInheritedSummaryHeader(AbstractMemberWriter var1, ClassDoc var2, Content var3) {
      var1.addInheritedSummaryAnchor(var2, var3);
      var1.addInheritedSummaryLabel(var2, var3);
   }

   protected void addIndexComment(Doc var1, Content var2) {
      this.addIndexComment(var1, var1.firstSentenceTags(), var2);
   }

   protected void addIndexComment(Doc var1, Tag[] var2, Content var3) {
      Tag[] var4 = var1.tags("deprecated");
      HtmlTree var5;
      if (Util.isDeprecated((ProgramElementDoc)var1)) {
         HtmlTree var8 = HtmlTree.SPAN(HtmlStyle.deprecatedLabel, this.deprecatedPhrase);
         var5 = HtmlTree.DIV(HtmlStyle.block, var8);
         var5.addContent(this.getSpace());
         if (var4.length > 0) {
            this.addInlineDeprecatedComment(var1, var4[0], var5);
         }

         var3.addContent((Content)var5);
      } else {
         ClassDoc var6 = ((ProgramElementDoc)var1).containingClass();
         if (var6 != null && Util.isDeprecated(var6)) {
            HtmlTree var7 = HtmlTree.SPAN(HtmlStyle.deprecatedLabel, this.deprecatedPhrase);
            var5 = HtmlTree.DIV(HtmlStyle.block, var7);
            var5.addContent(this.getSpace());
            var3.addContent((Content)var5);
         }

         this.addSummaryComment(var1, var2, var3);
      }
   }

   public void addSummaryType(AbstractMemberWriter var1, ProgramElementDoc var2, Content var3) {
      var1.addSummaryType(var2, var3);
   }

   public void addSummaryLinkComment(AbstractMemberWriter var1, ProgramElementDoc var2, Content var3) {
      this.addSummaryLinkComment(var1, var2, var2.firstSentenceTags(), var3);
   }

   public void addSummaryLinkComment(AbstractMemberWriter var1, ProgramElementDoc var2, Tag[] var3, Content var4) {
      this.addIndexComment(var2, var3, var4);
   }

   public void addInheritedMemberSummary(AbstractMemberWriter var1, ClassDoc var2, ProgramElementDoc var3, boolean var4, Content var5) {
      if (!var4) {
         var5.addContent(", ");
      }

      var1.addInheritedSummaryLink(var2, var3, var5);
   }

   public Content getContentHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.DIV);
      var1.addStyle(HtmlStyle.contentContainer);
      return var1;
   }

   public Content getMemberTreeHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.LI);
      var1.addStyle(HtmlStyle.blockList);
      return var1;
   }

   public Content getMemberTree(Content var1) {
      HtmlTree var2 = HtmlTree.UL(HtmlStyle.blockList, var1);
      return var2;
   }

   public Content getMemberSummaryTree(Content var1) {
      return this.getMemberTree(HtmlStyle.summary, var1);
   }

   public Content getMemberDetailsTree(Content var1) {
      return this.getMemberTree(HtmlStyle.details, var1);
   }

   public Content getMemberTree(HtmlStyle var1, Content var2) {
      HtmlTree var3 = HtmlTree.DIV(var1, this.getMemberTree(var2));
      return var3;
   }
}
