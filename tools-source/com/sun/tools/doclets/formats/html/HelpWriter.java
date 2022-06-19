package com.sun.tools.doclets.formats.html;

import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import java.io.IOException;

public class HelpWriter extends HtmlDocletWriter {
   public HelpWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
   }

   public static void generate(ConfigurationImpl var0) {
      DocPath var2 = DocPath.empty;

      try {
         var2 = DocPaths.HELP_DOC;
         HelpWriter var1 = new HelpWriter(var0, var2);
         var1.generateHelpFile();
         var1.close();
      } catch (IOException var4) {
         var0.standardmessage.error("doclet.exception_encountered", var4.toString(), var2);
         throw new DocletAbortException(var4);
      }
   }

   protected void generateHelpFile() throws IOException {
      String var1 = this.configuration.getText("doclet.Window_Help_title");
      HtmlTree var2 = this.getBody(true, this.getWindowTitle(var1));
      this.addTop(var2);
      this.addNavLinks(true, var2);
      this.addHelpFileContents(var2);
      this.addNavLinks(false, var2);
      this.addBottom(var2);
      this.printHtmlDocument((String[])null, true, var2);
   }

   protected void addHelpFileContents(Content var1) {
      HtmlTree var2 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, false, HtmlStyle.title, this.getResource("doclet.Help_line_1"));
      HtmlTree var3 = HtmlTree.DIV(HtmlStyle.header, var2);
      HtmlTree var4 = HtmlTree.DIV(HtmlStyle.subTitle, this.getResource("doclet.Help_line_2"));
      var3.addContent((Content)var4);
      var1.addContent((Content)var3);
      HtmlTree var5 = new HtmlTree(HtmlTag.UL);
      var5.addStyle(HtmlStyle.blockList);
      HtmlTree var6;
      HtmlTree var7;
      Content var8;
      HtmlTree var9;
      if (this.configuration.createoverview) {
         var6 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Overview"));
         var7 = HtmlTree.LI(HtmlStyle.blockList, var6);
         var8 = this.getResource("doclet.Help_line_3", this.getHyperLink(DocPaths.OVERVIEW_SUMMARY, this.configuration.getText("doclet.Overview")));
         var9 = HtmlTree.P(var8);
         var7.addContent((Content)var9);
         var5.addContent((Content)var7);
      }

      var6 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Package"));
      var7 = HtmlTree.LI(HtmlStyle.blockList, var6);
      var8 = this.getResource("doclet.Help_line_4");
      var9 = HtmlTree.P(var8);
      var7.addContent((Content)var9);
      HtmlTree var10 = new HtmlTree(HtmlTag.UL);
      var10.addContent((Content)HtmlTree.LI(this.getResource("doclet.Interfaces_Italic")));
      var10.addContent((Content)HtmlTree.LI(this.getResource("doclet.Classes")));
      var10.addContent((Content)HtmlTree.LI(this.getResource("doclet.Enums")));
      var10.addContent((Content)HtmlTree.LI(this.getResource("doclet.Exceptions")));
      var10.addContent((Content)HtmlTree.LI(this.getResource("doclet.Errors")));
      var10.addContent((Content)HtmlTree.LI(this.getResource("doclet.AnnotationTypes")));
      var7.addContent((Content)var10);
      var5.addContent((Content)var7);
      HtmlTree var11 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Help_line_5"));
      HtmlTree var12 = HtmlTree.LI(HtmlStyle.blockList, var11);
      Content var13 = this.getResource("doclet.Help_line_6");
      HtmlTree var14 = HtmlTree.P(var13);
      var12.addContent((Content)var14);
      HtmlTree var15 = new HtmlTree(HtmlTag.UL);
      var15.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_line_7")));
      var15.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_line_8")));
      var15.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_line_9")));
      var15.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_line_10")));
      var15.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_line_11")));
      var15.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_line_12")));
      var12.addContent((Content)var15);
      HtmlTree var16 = new HtmlTree(HtmlTag.UL);
      var16.addContent((Content)HtmlTree.LI(this.getResource("doclet.Nested_Class_Summary")));
      var16.addContent((Content)HtmlTree.LI(this.getResource("doclet.Field_Summary")));
      var16.addContent((Content)HtmlTree.LI(this.getResource("doclet.Constructor_Summary")));
      var16.addContent((Content)HtmlTree.LI(this.getResource("doclet.Method_Summary")));
      var12.addContent((Content)var16);
      HtmlTree var17 = new HtmlTree(HtmlTag.UL);
      var17.addContent((Content)HtmlTree.LI(this.getResource("doclet.Field_Detail")));
      var17.addContent((Content)HtmlTree.LI(this.getResource("doclet.Constructor_Detail")));
      var17.addContent((Content)HtmlTree.LI(this.getResource("doclet.Method_Detail")));
      var12.addContent((Content)var17);
      Content var18 = this.getResource("doclet.Help_line_13");
      HtmlTree var19 = HtmlTree.P(var18);
      var12.addContent((Content)var19);
      var5.addContent((Content)var12);
      HtmlTree var20 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.AnnotationType"));
      HtmlTree var21 = HtmlTree.LI(HtmlStyle.blockList, var20);
      Content var22 = this.getResource("doclet.Help_annotation_type_line_1");
      HtmlTree var23 = HtmlTree.P(var22);
      var21.addContent((Content)var23);
      HtmlTree var24 = new HtmlTree(HtmlTag.UL);
      var24.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_annotation_type_line_2")));
      var24.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_annotation_type_line_3")));
      var24.addContent((Content)HtmlTree.LI(this.getResource("doclet.Annotation_Type_Required_Member_Summary")));
      var24.addContent((Content)HtmlTree.LI(this.getResource("doclet.Annotation_Type_Optional_Member_Summary")));
      var24.addContent((Content)HtmlTree.LI(this.getResource("doclet.Annotation_Type_Member_Detail")));
      var21.addContent((Content)var24);
      var5.addContent((Content)var21);
      HtmlTree var25 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Enum"));
      HtmlTree var26 = HtmlTree.LI(HtmlStyle.blockList, var25);
      Content var27 = this.getResource("doclet.Help_enum_line_1");
      HtmlTree var28 = HtmlTree.P(var27);
      var26.addContent((Content)var28);
      HtmlTree var29 = new HtmlTree(HtmlTag.UL);
      var29.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_enum_line_2")));
      var29.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_enum_line_3")));
      var29.addContent((Content)HtmlTree.LI(this.getResource("doclet.Enum_Constant_Summary")));
      var29.addContent((Content)HtmlTree.LI(this.getResource("doclet.Enum_Constant_Detail")));
      var26.addContent((Content)var29);
      var5.addContent((Content)var26);
      HtmlTree var30;
      HtmlTree var31;
      Content var32;
      HtmlTree var33;
      if (this.configuration.classuse) {
         var30 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Help_line_14"));
         var31 = HtmlTree.LI(HtmlStyle.blockList, var30);
         var32 = this.getResource("doclet.Help_line_15");
         var33 = HtmlTree.P(var32);
         var31.addContent((Content)var33);
         var5.addContent((Content)var31);
      }

      HtmlTree var34;
      if (this.configuration.createtree) {
         var30 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Help_line_16"));
         var31 = HtmlTree.LI(HtmlStyle.blockList, var30);
         var32 = this.getResource("doclet.Help_line_17_with_tree_link", this.getHyperLink(DocPaths.OVERVIEW_TREE, this.configuration.getText("doclet.Class_Hierarchy")), HtmlTree.CODE(new StringContent("java.lang.Object")));
         var33 = HtmlTree.P(var32);
         var31.addContent((Content)var33);
         var34 = new HtmlTree(HtmlTag.UL);
         var34.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_line_18")));
         var34.addContent((Content)HtmlTree.LI(this.getResource("doclet.Help_line_19")));
         var31.addContent((Content)var34);
         var5.addContent((Content)var31);
      }

      if (!this.configuration.nodeprecatedlist && !this.configuration.nodeprecated) {
         var30 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Deprecated_API"));
         var31 = HtmlTree.LI(HtmlStyle.blockList, var30);
         var32 = this.getResource("doclet.Help_line_20_with_deprecated_api_link", this.getHyperLink(DocPaths.DEPRECATED_LIST, this.configuration.getText("doclet.Deprecated_API")));
         var33 = HtmlTree.P(var32);
         var31.addContent((Content)var33);
         var5.addContent((Content)var31);
      }

      if (this.configuration.createindex) {
         Content var52;
         if (this.configuration.splitindex) {
            var52 = this.getHyperLink(DocPaths.INDEX_FILES.resolve(DocPaths.indexN(1)), this.configuration.getText("doclet.Index"));
         } else {
            var52 = this.getHyperLink(DocPaths.INDEX_ALL, this.configuration.getText("doclet.Index"));
         }

         var31 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Help_line_21"));
         HtmlTree var53 = HtmlTree.LI(HtmlStyle.blockList, var31);
         Content var54 = this.getResource("doclet.Help_line_22", var52);
         var34 = HtmlTree.P(var54);
         var53.addContent((Content)var34);
         var5.addContent((Content)var53);
      }

      var30 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Help_line_23"));
      var31 = HtmlTree.LI(HtmlStyle.blockList, var30);
      var32 = this.getResource("doclet.Help_line_24");
      var33 = HtmlTree.P(var32);
      var31.addContent((Content)var33);
      var5.addContent((Content)var31);
      var34 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Help_line_25"));
      HtmlTree var35 = HtmlTree.LI(HtmlStyle.blockList, var34);
      Content var36 = this.getResource("doclet.Help_line_26");
      HtmlTree var37 = HtmlTree.P(var36);
      var35.addContent((Content)var37);
      var5.addContent((Content)var35);
      HtmlTree var38 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.All_Classes"));
      HtmlTree var39 = HtmlTree.LI(HtmlStyle.blockList, var38);
      Content var40 = this.getResource("doclet.Help_line_27", this.getHyperLink(DocPaths.ALLCLASSES_NOFRAME, this.configuration.getText("doclet.All_Classes")));
      HtmlTree var41 = HtmlTree.P(var40);
      var39.addContent((Content)var41);
      var5.addContent((Content)var39);
      HtmlTree var42 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Serialized_Form"));
      HtmlTree var43 = HtmlTree.LI(HtmlStyle.blockList, var42);
      Content var44 = this.getResource("doclet.Help_line_28");
      HtmlTree var45 = HtmlTree.P(var44);
      var43.addContent((Content)var45);
      var5.addContent((Content)var43);
      HtmlTree var46 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Constants_Summary"));
      HtmlTree var47 = HtmlTree.LI(HtmlStyle.blockList, var46);
      Content var48 = this.getResource("doclet.Help_line_29", this.getHyperLink(DocPaths.CONSTANT_VALUES, this.configuration.getText("doclet.Constants_Summary")));
      HtmlTree var49 = HtmlTree.P(var48);
      var47.addContent((Content)var49);
      var5.addContent((Content)var47);
      HtmlTree var50 = HtmlTree.DIV(HtmlStyle.contentContainer, var5);
      HtmlTree var51 = HtmlTree.SPAN(HtmlStyle.emphasizedPhrase, this.getResource("doclet.Help_line_30"));
      var50.addContent((Content)var51);
      var1.addContent((Content)var50);
   }

   protected Content getNavLinkHelp() {
      HtmlTree var1 = HtmlTree.LI(HtmlStyle.navBarCell1Rev, this.helpLabel);
      return var1;
   }
}
