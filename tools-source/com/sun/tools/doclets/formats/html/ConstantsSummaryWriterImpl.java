package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.formats.html.markup.ContentBuilder;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.ConstantsSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocLink;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class ConstantsSummaryWriterImpl extends HtmlDocletWriter implements ConstantsSummaryWriter {
   ConfigurationImpl configuration;
   private ClassDoc currentClassDoc;
   private final String constantsTableSummary;
   private final String[] constantsTableHeader;

   public ConstantsSummaryWriterImpl(ConfigurationImpl var1) throws IOException {
      super(var1, DocPaths.CONSTANT_VALUES);
      this.configuration = var1;
      this.constantsTableSummary = var1.getText("doclet.Constants_Table_Summary", var1.getText("doclet.Constants_Summary"));
      this.constantsTableHeader = new String[]{this.getModifierTypeHeader(), var1.getText("doclet.ConstantField"), var1.getText("doclet.Value")};
   }

   public Content getHeader() {
      String var1 = this.configuration.getText("doclet.Constants_Summary");
      HtmlTree var2 = this.getBody(true, this.getWindowTitle(var1));
      this.addTop(var2);
      this.addNavLinks(true, var2);
      return var2;
   }

   public Content getContentsHeader() {
      return new HtmlTree(HtmlTag.UL);
   }

   public void addLinkToPackageContent(PackageDoc var1, String var2, Set var3, Content var4) {
      String var5 = var1.name();
      Content var6;
      if (var5.length() == 0) {
         var6 = this.getHyperLink(this.getDocLink(SectionName.UNNAMED_PACKAGE_ANCHOR), this.defaultPackageLabel, "", "");
      } else {
         Content var7 = this.getPackageLabel(var2);
         var7.addContent(".*");
         var6 = this.getHyperLink(DocLink.fragment(var2), var7, "", "");
         var3.add(var2);
      }

      var4.addContent((Content)HtmlTree.LI(var6));
   }

   public Content getContentsList(Content var1) {
      Content var2 = this.getResource("doclet.Constants_Summary");
      HtmlTree var3 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, true, HtmlStyle.title, var2);
      HtmlTree var4 = HtmlTree.DIV(HtmlStyle.header, var3);
      Content var5 = this.getResource("doclet.Contents");
      var4.addContent((Content)HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, true, var5));
      var4.addContent(var1);
      return var4;
   }

   public Content getConstantSummaries() {
      HtmlTree var1 = new HtmlTree(HtmlTag.DIV);
      var1.addStyle(HtmlStyle.constantValuesContainer);
      return var1;
   }

   public void addPackageName(PackageDoc var1, String var2, Content var3) {
      Content var4;
      if (var2.length() == 0) {
         var3.addContent(this.getMarkerAnchor(SectionName.UNNAMED_PACKAGE_ANCHOR));
         var4 = this.defaultPackageLabel;
      } else {
         var3.addContent(this.getMarkerAnchor(var2));
         var4 = this.getPackageLabel(var2);
      }

      StringContent var5 = new StringContent(".*");
      HtmlTree var6 = HtmlTree.HEADING(HtmlConstants.PACKAGE_HEADING, true, var4);
      var6.addContent((Content)var5);
      var3.addContent((Content)var6);
   }

   public Content getClassConstantHeader() {
      HtmlTree var1 = new HtmlTree(HtmlTag.UL);
      var1.addStyle(HtmlStyle.blockList);
      return var1;
   }

   public Content getConstantMembersHeader(ClassDoc var1) {
      Object var2 = !var1.isPublic() && !var1.isProtected() ? new StringContent(var1.qualifiedName()) : this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CONSTANT_SUMMARY, var1));
      String var3 = var1.containingPackage().name();
      if (var3.length() > 0) {
         ContentBuilder var4 = new ContentBuilder();
         var4.addContent(var3);
         var4.addContent(".");
         var4.addContent((Content)var2);
         return this.getClassName(var4);
      } else {
         return this.getClassName((Content)var2);
      }
   }

   protected Content getClassName(Content var1) {
      HtmlTree var2 = HtmlTree.TABLE(HtmlStyle.constantsSummary, 0, 3, 0, this.constantsTableSummary, this.getTableCaption(var1));
      var2.addContent(this.getSummaryTableHeader(this.constantsTableHeader, "col"));
      return var2;
   }

   public void addConstantMembers(ClassDoc var1, List var2, Content var3) {
      this.currentClassDoc = var1;
      HtmlTree var4 = new HtmlTree(HtmlTag.TBODY);

      HtmlTree var6;
      for(int var5 = 0; var5 < var2.size(); ++var5) {
         var6 = new HtmlTree(HtmlTag.TR);
         if (var5 % 2 == 0) {
            var6.addStyle(HtmlStyle.altColor);
         } else {
            var6.addStyle(HtmlStyle.rowColor);
         }

         this.addConstantMember((FieldDoc)var2.get(var5), var6);
         var4.addContent((Content)var6);
      }

      Content var7 = this.getConstantMembersHeader(var1);
      var7.addContent((Content)var4);
      var6 = HtmlTree.LI(HtmlStyle.blockList, var7);
      var3.addContent((Content)var6);
   }

   private void addConstantMember(FieldDoc var1, HtmlTree var2) {
      var2.addContent(this.getTypeColumn(var1));
      var2.addContent(this.getNameColumn(var1));
      var2.addContent(this.getValue(var1));
   }

   private Content getTypeColumn(FieldDoc var1) {
      Content var2 = this.getMarkerAnchor(this.currentClassDoc.qualifiedName() + "." + var1.name());
      HtmlTree var3 = HtmlTree.TD(HtmlStyle.colFirst, var2);
      HtmlTree var4 = new HtmlTree(HtmlTag.CODE);
      StringTokenizer var5 = new StringTokenizer(var1.modifiers());

      while(var5.hasMoreTokens()) {
         StringContent var6 = new StringContent(var5.nextToken());
         var4.addContent((Content)var6);
         var4.addContent(this.getSpace());
      }

      Content var7 = this.getLink(new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.CONSTANT_SUMMARY, var1.type()));
      var4.addContent(var7);
      var3.addContent((Content)var4);
      return var3;
   }

   private Content getNameColumn(FieldDoc var1) {
      Content var2 = this.getDocLink(LinkInfoImpl.Kind.CONSTANT_SUMMARY, var1, var1.name(), false);
      HtmlTree var3 = HtmlTree.CODE(var2);
      return HtmlTree.TD(var3);
   }

   private Content getValue(FieldDoc var1) {
      StringContent var2 = new StringContent(var1.constantValueExpression());
      HtmlTree var3 = HtmlTree.CODE(var2);
      return HtmlTree.TD(HtmlStyle.colLast, var3);
   }

   public void addFooter(Content var1) {
      this.addNavLinks(false, var1);
      this.addBottom(var1);
   }

   public void printDocument(Content var1) throws IOException {
      this.printHtmlDocument((String[])null, true, var1);
   }
}
