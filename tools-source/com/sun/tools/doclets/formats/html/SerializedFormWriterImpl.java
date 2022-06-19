package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.SerializedFormWriter;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import java.io.IOException;

public class SerializedFormWriterImpl extends SubWriterHolderWriter implements SerializedFormWriter {
   public SerializedFormWriterImpl(ConfigurationImpl var1) throws IOException {
      super(var1, DocPaths.SERIALIZED_FORM);
   }

   public Content getHeader(String var1) {
      HtmlTree var2 = this.getBody(true, this.getWindowTitle(var1));
      this.addTop(var2);
      this.addNavLinks(true, var2);
      StringContent var3 = new StringContent(var1);
      HtmlTree var4 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, true, HtmlStyle.title, var3);
      HtmlTree var5 = HtmlTree.DIV(HtmlStyle.header, var4);
      var2.addContent((Content)var5);
      return var2;
   }

   public Content getSerializedSummariesHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.UL);
      var1.addStyle(HtmlStyle.blockList);
      return var1;
   }

   public Content getPackageSerializedHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.LI);
      var1.addStyle(HtmlStyle.blockList);
      return var1;
   }

   public Content getPackageHeader(String var1) {
      HtmlTree var2 = HtmlTree.HEADING(HtmlConstants.PACKAGE_HEADING, true, this.packageLabel);
      var2.addContent(this.getSpace());
      var2.addContent(var1);
      return var2;
   }

   public Content getClassSerializedHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.UL);
      var1.addStyle(HtmlStyle.blockList);
      return var1;
   }

   public Content getClassHeader(ClassDoc var1) {
      Object var2 = !var1.isPublic() && !var1.isProtected() ? new StringContent(var1.qualifiedName()) : this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.DEFAULT, var1)).label(this.configuration.getClassName(var1)));
      HtmlTree var3 = HtmlTree.LI(HtmlStyle.blockList, this.getMarkerAnchor(var1.qualifiedName()));
      Content var4 = var1.superclassType() != null ? this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.SERIALIZED_FORM, var1.superclassType())) : null;
      Content var5 = var4 == null ? this.configuration.getResource("doclet.Class_0_implements_serializable", var2) : this.configuration.getResource("doclet.Class_0_extends_implements_serializable", var2, var4);
      var3.addContent((Content)HtmlTree.HEADING(HtmlConstants.SERIALIZED_MEMBER_HEADING, var5));
      return var3;
   }

   public Content getSerialUIDInfoHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.DL);
      var1.addStyle(HtmlStyle.nameValue);
      return var1;
   }

   public void addSerialUIDInfo(String var1, String var2, Content var3) {
      StringContent var4 = new StringContent(var1);
      var3.addContent((Content)HtmlTree.DT(var4));
      StringContent var5 = new StringContent(var2);
      var3.addContent((Content)HtmlTree.DD(var5));
   }

   public Content getClassContentHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.UL);
      var1.addStyle(HtmlStyle.blockList);
      return var1;
   }

   public Content getSerializedContent(Content var1) {
      HtmlTree var2 = HtmlTree.DIV(HtmlStyle.serializedFormContainer, var1);
      return var2;
   }

   public void addFooter(Content var1) {
      this.addNavLinks(false, var1);
      this.addBottom(var1);
   }

   public void printDocument(Content var1) throws IOException {
      this.printHtmlDocument((String[])null, true, var1);
   }

   public SerializedFormWriter.SerialFieldWriter getSerialFieldWriter(ClassDoc var1) {
      return new HtmlSerialFieldWriter(this, var1);
   }

   public SerializedFormWriter.SerialMethodWriter getSerialMethodWriter(ClassDoc var1) {
      return new HtmlSerialMethodWriter(this, var1);
   }
}
