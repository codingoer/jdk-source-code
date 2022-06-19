package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.SerializedFormWriter;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletManager;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;

public class HtmlSerialMethodWriter extends MethodWriterImpl implements SerializedFormWriter.SerialMethodWriter {
   public HtmlSerialMethodWriter(SubWriterHolderWriter var1, ClassDoc var2) {
      super(var1, var2);
   }

   public Content getSerializableMethodsHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.UL);
      var1.addStyle(HtmlStyle.blockList);
      return var1;
   }

   public Content getMethodsContentHeader(boolean var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.LI);
      if (var1) {
         var2.addStyle(HtmlStyle.blockListLast);
      } else {
         var2.addStyle(HtmlStyle.blockList);
      }

      return var2;
   }

   public Content getSerializableMethods(String var1, Content var2) {
      StringContent var3 = new StringContent(var1);
      HtmlTree var4 = HtmlTree.HEADING(HtmlConstants.SERIALIZED_MEMBER_HEADING, var3);
      HtmlTree var5 = HtmlTree.LI(HtmlStyle.blockList, var4);
      var5.addContent(var2);
      return var5;
   }

   public Content getNoCustomizationMsg(String var1) {
      StringContent var2 = new StringContent(var1);
      return var2;
   }

   public void addMemberHeader(MethodDoc var1, Content var2) {
      var2.addContent(this.getHead(var1));
      var2.addContent(this.getSignature(var1));
   }

   public void addDeprecatedMemberInfo(MethodDoc var1, Content var2) {
      this.addDeprecatedInfo(var1, var2);
   }

   public void addMemberDescription(MethodDoc var1, Content var2) {
      this.addComment(var1, var2);
   }

   public void addMemberTags(MethodDoc var1, Content var2) {
      ContentBuilder var3 = new ContentBuilder();
      TagletManager var4 = this.configuration.tagletManager;
      TagletWriter.genTagOuput(var4, var1, var4.getSerializedFormTaglets(), this.writer.getTagletWriterInstance(false), var3);
      HtmlTree var5 = new HtmlTree(HtmlTag.DL);
      var5.addContent((Content)var3);
      var2.addContent((Content)var5);
      if (var1.name().compareTo("writeExternal") == 0 && var1.tags("serialData").length == 0) {
         this.serialWarning(var1.position(), "doclet.MissingSerialDataTag", var1.containingClass().qualifiedName(), var1.name());
      }

   }
}
