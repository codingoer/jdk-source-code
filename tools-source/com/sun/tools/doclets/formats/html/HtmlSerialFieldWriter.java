package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SerialFieldTag;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.SerializedFormWriter;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.util.Arrays;
import java.util.List;

public class HtmlSerialFieldWriter extends FieldWriterImpl implements SerializedFormWriter.SerialFieldWriter {
   ProgramElementDoc[] members = null;

   public HtmlSerialFieldWriter(SubWriterHolderWriter var1, ClassDoc var2) {
      super(var1, var2);
   }

   public List members(ClassDoc var1) {
      return Arrays.asList(var1.serializableFields());
   }

   public Content getSerializableFieldsHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.UL);
      var1.addStyle(HtmlStyle.blockList);
      return var1;
   }

   public Content getFieldsContentHeader(boolean var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.LI);
      if (var1) {
         var2.addStyle(HtmlStyle.blockListLast);
      } else {
         var2.addStyle(HtmlStyle.blockList);
      }

      return var2;
   }

   public Content getSerializableFields(String var1, Content var2) {
      HtmlTree var3 = new HtmlTree(HtmlTag.LI);
      var3.addStyle(HtmlStyle.blockList);
      if (var2.isValid()) {
         StringContent var4 = new StringContent(var1);
         HtmlTree var5 = HtmlTree.HEADING(HtmlConstants.SERIALIZED_MEMBER_HEADING, var4);
         var3.addContent((Content)var5);
         var3.addContent(var2);
      }

      return var3;
   }

   public void addMemberHeader(ClassDoc var1, String var2, String var3, String var4, Content var5) {
      RawHtml var6 = new RawHtml(var4);
      HtmlTree var7 = HtmlTree.HEADING(HtmlConstants.MEMBER_HEADING, var6);
      var5.addContent((Content)var7);
      HtmlTree var8 = new HtmlTree(HtmlTag.PRE);
      if (var1 == null) {
         var8.addContent(var2);
      } else {
         Content var9 = this.writer.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.SERIAL_MEMBER, var1));
         var8.addContent(var9);
      }

      var8.addContent(var3 + " ");
      var8.addContent(var4);
      var5.addContent((Content)var8);
   }

   public void addMemberDeprecatedInfo(FieldDoc var1, Content var2) {
      this.addDeprecatedInfo(var1, var2);
   }

   public void addMemberDescription(FieldDoc var1, Content var2) {
      if (var1.inlineTags().length > 0) {
         this.writer.addInlineComment(var1, var2);
      }

      Tag[] var3 = var1.tags("serial");
      if (var3.length > 0) {
         this.writer.addInlineComment(var1, var3[0], var2);
      }

   }

   public void addMemberDescription(SerialFieldTag var1, Content var2) {
      String var3 = var1.description().trim();
      if (!var3.isEmpty()) {
         RawHtml var4 = new RawHtml(var3);
         HtmlTree var5 = HtmlTree.DIV(HtmlStyle.block, var4);
         var2.addContent((Content)var5);
      }

   }

   public void addMemberTags(FieldDoc var1, Content var2) {
      ContentBuilder var3 = new ContentBuilder();
      TagletWriter.genTagOuput(this.configuration.tagletManager, var1, this.configuration.tagletManager.getCustomTaglets(var1), this.writer.getTagletWriterInstance(false), var3);
      HtmlTree var4 = new HtmlTree(HtmlTag.DL);
      var4.addContent((Content)var3);
      var2.addContent((Content)var4);
   }

   public boolean shouldPrintOverview(FieldDoc var1) {
      if (this.configuration.nocomment || var1.commentText().isEmpty() && !this.writer.hasSerializationOverviewTags(var1)) {
         return var1.tags("deprecated").length > 0;
      } else {
         return true;
      }
   }
}
