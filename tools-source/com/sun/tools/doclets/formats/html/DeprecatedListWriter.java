package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DeprecatedAPIListBuilder;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import java.io.IOException;

public class DeprecatedListWriter extends SubWriterHolderWriter {
   private static final String[] ANCHORS = new String[]{"package", "interface", "class", "enum", "exception", "error", "annotation.type", "field", "method", "constructor", "enum.constant", "annotation.type.member"};
   private static final String[] HEADING_KEYS = new String[]{"doclet.Deprecated_Packages", "doclet.Deprecated_Interfaces", "doclet.Deprecated_Classes", "doclet.Deprecated_Enums", "doclet.Deprecated_Exceptions", "doclet.Deprecated_Errors", "doclet.Deprecated_Annotation_Types", "doclet.Deprecated_Fields", "doclet.Deprecated_Methods", "doclet.Deprecated_Constructors", "doclet.Deprecated_Enum_Constants", "doclet.Deprecated_Annotation_Type_Members"};
   private static final String[] SUMMARY_KEYS = new String[]{"doclet.deprecated_packages", "doclet.deprecated_interfaces", "doclet.deprecated_classes", "doclet.deprecated_enums", "doclet.deprecated_exceptions", "doclet.deprecated_errors", "doclet.deprecated_annotation_types", "doclet.deprecated_fields", "doclet.deprecated_methods", "doclet.deprecated_constructors", "doclet.deprecated_enum_constants", "doclet.deprecated_annotation_type_members"};
   private static final String[] HEADER_KEYS = new String[]{"doclet.Package", "doclet.Interface", "doclet.Class", "doclet.Enum", "doclet.Exceptions", "doclet.Errors", "doclet.AnnotationType", "doclet.Field", "doclet.Method", "doclet.Constructor", "doclet.Enum_Constant", "doclet.Annotation_Type_Member"};
   private AbstractMemberWriter[] writers;
   private ConfigurationImpl configuration;

   public DeprecatedListWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
      this.configuration = var1;
      NestedClassWriterImpl var3 = new NestedClassWriterImpl(this);
      this.writers = new AbstractMemberWriter[]{var3, var3, var3, var3, var3, var3, new FieldWriterImpl(this), new MethodWriterImpl(this), new ConstructorWriterImpl(this), new EnumConstantWriterImpl(this), new AnnotationTypeOptionalMemberWriterImpl(this, (AnnotationTypeDoc)null)};
   }

   public static void generate(ConfigurationImpl var0) {
      DocPath var1 = DocPaths.DEPRECATED_LIST;

      try {
         DeprecatedListWriter var2 = new DeprecatedListWriter(var0, var1);
         var2.generateDeprecatedListFile(new DeprecatedAPIListBuilder(var0));
         var2.close();
      } catch (IOException var3) {
         var0.standardmessage.error("doclet.exception_encountered", var3.toString(), var1);
         throw new DocletAbortException(var3);
      }
   }

   protected void generateDeprecatedListFile(DeprecatedAPIListBuilder var1) throws IOException {
      Content var2 = this.getHeader();
      var2.addContent(this.getContentsList(var1));
      String[] var4 = new String[1];
      HtmlTree var5 = new HtmlTree(HtmlTag.DIV);
      var5.addStyle(HtmlStyle.contentContainer);

      for(int var6 = 0; var6 < 12; ++var6) {
         if (var1.hasDocumentation(var6)) {
            this.addAnchor(var1, var6, var5);
            String var3 = this.configuration.getText("doclet.Member_Table_Summary", this.configuration.getText(HEADING_KEYS[var6]), this.configuration.getText(SUMMARY_KEYS[var6]));
            var4[0] = this.configuration.getText("doclet.0_and_1", this.configuration.getText(HEADER_KEYS[var6]), this.configuration.getText("doclet.Description"));
            if (var6 == 0) {
               this.addPackageDeprecatedAPI(var1.getList(var6), HEADING_KEYS[var6], var3, var4, var5);
            } else {
               this.writers[var6 - 1].addDeprecatedAPI(var1.getList(var6), HEADING_KEYS[var6], var3, var4, var5);
            }
         }
      }

      var2.addContent((Content)var5);
      this.addNavLinks(false, var2);
      this.addBottom(var2);
      this.printHtmlDocument((String[])null, true, var2);
   }

   private void addIndexLink(DeprecatedAPIListBuilder var1, int var2, Content var3) {
      if (var1.hasDocumentation(var2)) {
         HtmlTree var4 = HtmlTree.LI(this.getHyperLink(ANCHORS[var2], this.getResource(HEADING_KEYS[var2])));
         var3.addContent((Content)var4);
      }

   }

   public Content getContentsList(DeprecatedAPIListBuilder var1) {
      Content var2 = this.getResource("doclet.Deprecated_API");
      HtmlTree var3 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, true, HtmlStyle.title, var2);
      HtmlTree var4 = HtmlTree.DIV(HtmlStyle.header, var3);
      Content var5 = this.getResource("doclet.Contents");
      var4.addContent((Content)HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, true, var5));
      HtmlTree var6 = new HtmlTree(HtmlTag.UL);

      for(int var7 = 0; var7 < 12; ++var7) {
         this.addIndexLink(var1, var7, var6);
      }

      var4.addContent((Content)var6);
      return var4;
   }

   private void addAnchor(DeprecatedAPIListBuilder var1, int var2, Content var3) {
      if (var1.hasDocumentation(var2)) {
         var3.addContent(this.getMarkerAnchor(ANCHORS[var2]));
      }

   }

   public Content getHeader() {
      String var1 = this.configuration.getText("doclet.Window_Deprecated_List");
      HtmlTree var2 = this.getBody(true, this.getWindowTitle(var1));
      this.addTop(var2);
      this.addNavLinks(true, var2);
      return var2;
   }

   protected Content getNavLinkDeprecated() {
      HtmlTree var1 = HtmlTree.LI(HtmlStyle.navBarCell1Rev, this.deprecatedLabel);
      return var1;
   }
}
