package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.builders.SerializedFormBuilder;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocLink;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import com.sun.tools.doclets.internal.toolkit.util.MessageRetriever;
import com.sun.tools.doclets.internal.toolkit.util.Util;

public class TagletWriterImpl extends TagletWriter {
   private final HtmlDocletWriter htmlWriter;
   private final ConfigurationImpl configuration;

   public TagletWriterImpl(HtmlDocletWriter var1, boolean var2) {
      super(var2);
      this.htmlWriter = var1;
      this.configuration = var1.configuration;
   }

   public Content getOutputInstance() {
      return new ContentBuilder();
   }

   protected Content codeTagOutput(Tag var1) {
      HtmlTree var2 = HtmlTree.CODE(new StringContent(Util.normalizeNewlines(var1.text())));
      return var2;
   }

   public Content getDocRootOutput() {
      String var1;
      if (this.htmlWriter.pathToRoot.isEmpty()) {
         var1 = ".";
      } else {
         var1 = this.htmlWriter.pathToRoot.getPath();
      }

      return new StringContent(var1);
   }

   public Content deprecatedTagOutput(Doc var1) {
      ContentBuilder var2 = new ContentBuilder();
      Tag[] var3 = var1.tags("deprecated");
      if (var1 instanceof ClassDoc) {
         if (Util.isDeprecated((ProgramElementDoc)var1)) {
            var2.addContent((Content)HtmlTree.SPAN(HtmlStyle.deprecatedLabel, new StringContent(this.configuration.getText("doclet.Deprecated"))));
            var2.addContent(RawHtml.nbsp);
            if (var3.length > 0) {
               Tag[] var4 = var3[0].inlineTags();
               if (var4.length > 0) {
                  var2.addContent(this.commentTagsToOutput((Tag)null, var1, var3[0].inlineTags(), false));
               }
            }
         }
      } else {
         MemberDoc var6 = (MemberDoc)var1;
         if (Util.isDeprecated((ProgramElementDoc)var1)) {
            var2.addContent((Content)HtmlTree.SPAN(HtmlStyle.deprecatedLabel, new StringContent(this.configuration.getText("doclet.Deprecated"))));
            var2.addContent(RawHtml.nbsp);
            if (var3.length > 0) {
               Content var5 = this.commentTagsToOutput((Tag)null, var1, var3[0].inlineTags(), false);
               if (!var5.isEmpty()) {
                  var2.addContent((Content)HtmlTree.SPAN(HtmlStyle.deprecationComment, var5));
               }
            }
         } else if (Util.isDeprecated(var6.containingClass())) {
            var2.addContent((Content)HtmlTree.SPAN(HtmlStyle.deprecatedLabel, new StringContent(this.configuration.getText("doclet.Deprecated"))));
            var2.addContent(RawHtml.nbsp);
         }
      }

      return var2;
   }

   protected Content literalTagOutput(Tag var1) {
      StringContent var2 = new StringContent(Util.normalizeNewlines(var1.text()));
      return var2;
   }

   public MessageRetriever getMsgRetriever() {
      return this.configuration.message;
   }

   public Content getParamHeader(String var1) {
      HtmlTree var2 = HtmlTree.DT(HtmlTree.SPAN(HtmlStyle.paramLabel, new StringContent(var1)));
      return var2;
   }

   public Content paramTagOutput(ParamTag var1, String var2) {
      ContentBuilder var3 = new ContentBuilder();
      var3.addContent((Content)HtmlTree.CODE(new RawHtml(var2)));
      var3.addContent(" - ");
      var3.addContent(this.htmlWriter.commentTagsToContent(var1, (Doc)null, var1.inlineTags(), false));
      HtmlTree var4 = HtmlTree.DD(var3);
      return var4;
   }

   public Content propertyTagOutput(Tag var1, String var2) {
      ContentBuilder var3 = new ContentBuilder();
      var3.addContent((Content)(new RawHtml(var2)));
      var3.addContent(" ");
      var3.addContent((Content)HtmlTree.CODE(new RawHtml(var1.text())));
      var3.addContent(".");
      HtmlTree var4 = HtmlTree.P(var3);
      return var4;
   }

   public Content returnTagOutput(Tag var1) {
      ContentBuilder var2 = new ContentBuilder();
      var2.addContent((Content)HtmlTree.DT(HtmlTree.SPAN(HtmlStyle.returnLabel, new StringContent(this.configuration.getText("doclet.Returns")))));
      var2.addContent((Content)HtmlTree.DD(this.htmlWriter.commentTagsToContent(var1, (Doc)null, var1.inlineTags(), false)));
      return var2;
   }

   public Content seeTagOutput(Doc var1, SeeTag[] var2) {
      ContentBuilder var3 = new ContentBuilder();
      if (var2.length > 0) {
         for(int var4 = 0; var4 < var2.length; ++var4) {
            this.appendSeparatorIfNotEmpty(var3);
            var3.addContent(this.htmlWriter.seeTagToContent(var2[var4]));
         }
      }

      DocPath var7;
      if (var1.isField() && ((FieldDoc)var1).constantValue() != null && this.htmlWriter instanceof ClassWriterImpl) {
         this.appendSeparatorIfNotEmpty(var3);
         var7 = this.htmlWriter.pathToRoot.resolve(DocPaths.CONSTANT_VALUES);
         String var5 = ((ClassWriterImpl)this.htmlWriter).getClassDoc().qualifiedName() + "." + ((FieldDoc)var1).name();
         DocLink var6 = var7.fragment(var5);
         var3.addContent(this.htmlWriter.getHyperLink(var6, new StringContent(this.configuration.getText("doclet.Constants_Summary"))));
      }

      if (var1.isClass() && ((ClassDoc)var1).isSerializable() && SerializedFormBuilder.serialInclude(var1) && SerializedFormBuilder.serialInclude(((ClassDoc)var1).containingPackage())) {
         this.appendSeparatorIfNotEmpty(var3);
         var7 = this.htmlWriter.pathToRoot.resolve(DocPaths.SERIALIZED_FORM);
         DocLink var8 = var7.fragment(((ClassDoc)var1).qualifiedName());
         var3.addContent(this.htmlWriter.getHyperLink(var8, new StringContent(this.configuration.getText("doclet.Serialized_Form"))));
      }

      if (var3.isEmpty()) {
         return var3;
      } else {
         ContentBuilder var9 = new ContentBuilder();
         var9.addContent((Content)HtmlTree.DT(HtmlTree.SPAN(HtmlStyle.seeLabel, new StringContent(this.configuration.getText("doclet.See_Also")))));
         var9.addContent((Content)HtmlTree.DD(var3));
         return var9;
      }
   }

   private void appendSeparatorIfNotEmpty(ContentBuilder var1) {
      if (!var1.isEmpty()) {
         var1.addContent(", ");
         var1.addContent(DocletConstants.NL);
      }

   }

   public Content simpleTagOutput(Tag[] var1, String var2) {
      ContentBuilder var3 = new ContentBuilder();
      var3.addContent((Content)HtmlTree.DT(HtmlTree.SPAN(HtmlStyle.simpleTagLabel, new RawHtml(var2))));
      ContentBuilder var4 = new ContentBuilder();

      for(int var5 = 0; var5 < var1.length; ++var5) {
         if (var5 > 0) {
            var4.addContent(", ");
         }

         var4.addContent(this.htmlWriter.commentTagsToContent(var1[var5], (Doc)null, var1[var5].inlineTags(), false));
      }

      var3.addContent((Content)HtmlTree.DD(var4));
      return var3;
   }

   public Content simpleTagOutput(Tag var1, String var2) {
      ContentBuilder var3 = new ContentBuilder();
      var3.addContent((Content)HtmlTree.DT(HtmlTree.SPAN(HtmlStyle.simpleTagLabel, new RawHtml(var2))));
      Content var4 = this.htmlWriter.commentTagsToContent(var1, (Doc)null, var1.inlineTags(), false);
      var3.addContent((Content)HtmlTree.DD(var4));
      return var3;
   }

   public Content getThrowsHeader() {
      HtmlTree var1 = HtmlTree.DT(HtmlTree.SPAN(HtmlStyle.throwsLabel, new StringContent(this.configuration.getText("doclet.Throws"))));
      return var1;
   }

   public Content throwsTagOutput(ThrowsTag var1) {
      ContentBuilder var2 = new ContentBuilder();
      Object var3 = var1.exceptionType() == null ? new RawHtml(var1.exceptionName()) : this.htmlWriter.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.MEMBER, var1.exceptionType()));
      var2.addContent((Content)HtmlTree.CODE((Content)var3));
      Content var4 = this.htmlWriter.commentTagsToContent(var1, (Doc)null, var1.inlineTags(), false);
      if (var4 != null && !var4.isEmpty()) {
         var2.addContent(" - ");
         var2.addContent(var4);
      }

      HtmlTree var5 = HtmlTree.DD(var2);
      return var5;
   }

   public Content throwsTagOutput(Type var1) {
      HtmlTree var2 = HtmlTree.DD(HtmlTree.CODE(this.htmlWriter.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.MEMBER, var1))));
      return var2;
   }

   public Content valueTagOutput(FieldDoc var1, String var2, boolean var3) {
      return (Content)(var3 ? this.htmlWriter.getDocLink(LinkInfoImpl.Kind.VALUE_TAG, var1, var2, false) : new RawHtml(var2));
   }

   public Content commentTagsToOutput(Tag var1, Tag[] var2) {
      return this.commentTagsToOutput(var1, (Doc)null, var2, false);
   }

   public Content commentTagsToOutput(Doc var1, Tag[] var2) {
      return this.commentTagsToOutput((Tag)null, var1, var2, false);
   }

   public Content commentTagsToOutput(Tag var1, Doc var2, Tag[] var3, boolean var4) {
      return this.htmlWriter.commentTagsToContent(var1, var2, var3, var4);
   }

   public Configuration configuration() {
      return this.configuration;
   }
}
