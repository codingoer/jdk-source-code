package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocFile;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import com.sun.tools.doclets.internal.toolkit.util.MethodTypes;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HtmlWriter {
   protected String winTitle;
   protected Configuration configuration;
   protected boolean memberDetailsListPrinted;
   protected final String[] profileTableHeader;
   protected final String[] packageTableHeader;
   protected final String useTableSummary;
   protected final String modifierTypeHeader;
   public final Content overviewLabel;
   public final Content defaultPackageLabel;
   public final Content packageLabel;
   public final Content profileLabel;
   public final Content useLabel;
   public final Content prevLabel;
   public final Content nextLabel;
   public final Content prevclassLabel;
   public final Content nextclassLabel;
   public final Content summaryLabel;
   public final Content detailLabel;
   public final Content framesLabel;
   public final Content noframesLabel;
   public final Content treeLabel;
   public final Content classLabel;
   public final Content deprecatedLabel;
   public final Content deprecatedPhrase;
   public final Content allclassesLabel;
   public final Content allpackagesLabel;
   public final Content allprofilesLabel;
   public final Content indexLabel;
   public final Content helpLabel;
   public final Content seeLabel;
   public final Content descriptionLabel;
   public final Content prevpackageLabel;
   public final Content nextpackageLabel;
   public final Content prevprofileLabel;
   public final Content nextprofileLabel;
   public final Content packagesLabel;
   public final Content profilesLabel;
   public final Content methodDetailsLabel;
   public final Content annotationTypeDetailsLabel;
   public final Content fieldDetailsLabel;
   public final Content propertyDetailsLabel;
   public final Content constructorDetailsLabel;
   public final Content enumConstantsDetailsLabel;
   public final Content specifiedByLabel;
   public final Content overridesLabel;
   public final Content descfrmClassLabel;
   public final Content descfrmInterfaceLabel;
   private final DocFile file;
   private Writer writer;
   private Content script;

   public HtmlWriter(Configuration var1, DocPath var2) throws IOException, UnsupportedEncodingException {
      this.file = DocFile.createFileForOutput(var1, var2);
      this.configuration = var1;
      this.memberDetailsListPrinted = false;
      this.profileTableHeader = new String[]{var1.getText("doclet.Profile"), var1.getText("doclet.Description")};
      this.packageTableHeader = new String[]{var1.getText("doclet.Package"), var1.getText("doclet.Description")};
      this.useTableSummary = var1.getText("doclet.Use_Table_Summary", var1.getText("doclet.packages"));
      this.modifierTypeHeader = var1.getText("doclet.0_and_1", var1.getText("doclet.Modifier"), var1.getText("doclet.Type"));
      this.overviewLabel = this.getResource("doclet.Overview");
      this.defaultPackageLabel = new StringContent("<Unnamed>");
      this.packageLabel = this.getResource("doclet.Package");
      this.profileLabel = this.getResource("doclet.Profile");
      this.useLabel = this.getResource("doclet.navClassUse");
      this.prevLabel = this.getResource("doclet.Prev");
      this.nextLabel = this.getResource("doclet.Next");
      this.prevclassLabel = this.getNonBreakResource("doclet.Prev_Class");
      this.nextclassLabel = this.getNonBreakResource("doclet.Next_Class");
      this.summaryLabel = this.getResource("doclet.Summary");
      this.detailLabel = this.getResource("doclet.Detail");
      this.framesLabel = this.getResource("doclet.Frames");
      this.noframesLabel = this.getNonBreakResource("doclet.No_Frames");
      this.treeLabel = this.getResource("doclet.Tree");
      this.classLabel = this.getResource("doclet.Class");
      this.deprecatedLabel = this.getResource("doclet.navDeprecated");
      this.deprecatedPhrase = this.getResource("doclet.Deprecated");
      this.allclassesLabel = this.getNonBreakResource("doclet.All_Classes");
      this.allpackagesLabel = this.getNonBreakResource("doclet.All_Packages");
      this.allprofilesLabel = this.getNonBreakResource("doclet.All_Profiles");
      this.indexLabel = this.getResource("doclet.Index");
      this.helpLabel = this.getResource("doclet.Help");
      this.seeLabel = this.getResource("doclet.See");
      this.descriptionLabel = this.getResource("doclet.Description");
      this.prevpackageLabel = this.getNonBreakResource("doclet.Prev_Package");
      this.nextpackageLabel = this.getNonBreakResource("doclet.Next_Package");
      this.prevprofileLabel = this.getNonBreakResource("doclet.Prev_Profile");
      this.nextprofileLabel = this.getNonBreakResource("doclet.Next_Profile");
      this.packagesLabel = this.getResource("doclet.Packages");
      this.profilesLabel = this.getResource("doclet.Profiles");
      this.methodDetailsLabel = this.getResource("doclet.Method_Detail");
      this.annotationTypeDetailsLabel = this.getResource("doclet.Annotation_Type_Member_Detail");
      this.fieldDetailsLabel = this.getResource("doclet.Field_Detail");
      this.propertyDetailsLabel = this.getResource("doclet.Property_Detail");
      this.constructorDetailsLabel = this.getResource("doclet.Constructor_Detail");
      this.enumConstantsDetailsLabel = this.getResource("doclet.Enum_Constant_Detail");
      this.specifiedByLabel = this.getResource("doclet.Specified_By");
      this.overridesLabel = this.getResource("doclet.Overrides");
      this.descfrmClassLabel = this.getResource("doclet.Description_From_Class");
      this.descfrmInterfaceLabel = this.getResource("doclet.Description_From_Interface");
   }

   public void write(Content var1) throws IOException {
      this.writer = this.file.openWriter();
      var1.write(this.writer, true);
   }

   public void close() throws IOException {
      this.writer.close();
   }

   public Content getResource(String var1) {
      return this.configuration.getResource(var1);
   }

   public Content getNonBreakResource(String var1) {
      String var2 = this.configuration.getText(var1);
      Content var3 = this.configuration.newContent();

      int var4;
      int var5;
      for(var4 = 0; (var5 = var2.indexOf(" ", var4)) != -1; var4 = var5 + 1) {
         var3.addContent(var2.substring(var4, var5));
         var3.addContent(RawHtml.nbsp);
      }

      var3.addContent(var2.substring(var4));
      return var3;
   }

   public Content getResource(String var1, Object var2) {
      return this.configuration.getResource(var1, var2);
   }

   public Content getResource(String var1, Object var2, Object var3) {
      return this.configuration.getResource(var1, var2, var3);
   }

   protected HtmlTree getWinTitleScript() {
      HtmlTree var1 = new HtmlTree(HtmlTag.SCRIPT);
      if (this.winTitle != null && this.winTitle.length() > 0) {
         var1.addAttr(HtmlAttr.TYPE, "text/javascript");
         String var2 = "<!--" + DocletConstants.NL + "    try {" + DocletConstants.NL + "        if (location.href.indexOf('is-external=true') == -1) {" + DocletConstants.NL + "            parent.document.title=\"" + escapeJavaScriptChars(this.winTitle) + "\";" + DocletConstants.NL + "        }" + DocletConstants.NL + "    }" + DocletConstants.NL + "    catch(err) {" + DocletConstants.NL + "    }" + DocletConstants.NL + "//-->" + DocletConstants.NL;
         RawHtml var3 = new RawHtml(var2);
         var1.addContent((Content)var3);
      }

      return var1;
   }

   private static String escapeJavaScriptChars(String var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         switch (var3) {
            case '\b':
               var1.append("\\b");
               break;
            case '\t':
               var1.append("\\t");
               break;
            case '\n':
               var1.append("\\n");
               break;
            case '\f':
               var1.append("\\f");
               break;
            case '\r':
               var1.append("\\r");
               break;
            case '"':
               var1.append("\\\"");
               break;
            case '\'':
               var1.append("\\'");
               break;
            case '\\':
               var1.append("\\\\");
               break;
            default:
               if (var3 >= ' ' && var3 < 127) {
                  var1.append(var3);
               } else {
                  var1.append(String.format("\\u%04X", Integer.valueOf(var3)));
               }
         }
      }

      return var1.toString();
   }

   protected Content getFramesetJavaScript() {
      HtmlTree var1 = new HtmlTree(HtmlTag.SCRIPT);
      var1.addAttr(HtmlAttr.TYPE, "text/javascript");
      String var2 = DocletConstants.NL + "    tmpTargetPage = \"\" + window.location.search;" + DocletConstants.NL + "    if (tmpTargetPage != \"\" && tmpTargetPage != \"undefined\")" + DocletConstants.NL + "        tmpTargetPage = tmpTargetPage.substring(1);" + DocletConstants.NL + "    if (tmpTargetPage.indexOf(\":\") != -1 || (tmpTargetPage != \"\" && !validURL(tmpTargetPage)))" + DocletConstants.NL + "        tmpTargetPage = \"undefined\";" + DocletConstants.NL + "    targetPage = tmpTargetPage;" + DocletConstants.NL + "    function validURL(url) {" + DocletConstants.NL + "        try {" + DocletConstants.NL + "            url = decodeURIComponent(url);" + DocletConstants.NL + "        }" + DocletConstants.NL + "        catch (error) {" + DocletConstants.NL + "            return false;" + DocletConstants.NL + "        }" + DocletConstants.NL + "        var pos = url.indexOf(\".html\");" + DocletConstants.NL + "        if (pos == -1 || pos != url.length - 5)" + DocletConstants.NL + "            return false;" + DocletConstants.NL + "        var allowNumber = false;" + DocletConstants.NL + "        var allowSep = false;" + DocletConstants.NL + "        var seenDot = false;" + DocletConstants.NL + "        for (var i = 0; i < url.length - 5; i++) {" + DocletConstants.NL + "            var ch = url.charAt(i);" + DocletConstants.NL + "            if ('a' <= ch && ch <= 'z' ||" + DocletConstants.NL + "                    'A' <= ch && ch <= 'Z' ||" + DocletConstants.NL + "                    ch == '$' ||" + DocletConstants.NL + "                    ch == '_' ||" + DocletConstants.NL + "                    ch.charCodeAt(0) > 127) {" + DocletConstants.NL + "                allowNumber = true;" + DocletConstants.NL + "                allowSep = true;" + DocletConstants.NL + "            } else if ('0' <= ch && ch <= '9'" + DocletConstants.NL + "                    || ch == '-') {" + DocletConstants.NL + "                if (!allowNumber)" + DocletConstants.NL + "                     return false;" + DocletConstants.NL + "            } else if (ch == '/' || ch == '.') {" + DocletConstants.NL + "                if (!allowSep)" + DocletConstants.NL + "                    return false;" + DocletConstants.NL + "                allowNumber = false;" + DocletConstants.NL + "                allowSep = false;" + DocletConstants.NL + "                if (ch == '.')" + DocletConstants.NL + "                     seenDot = true;" + DocletConstants.NL + "                if (ch == '/' && seenDot)" + DocletConstants.NL + "                     return false;" + DocletConstants.NL + "            } else {" + DocletConstants.NL + "                return false;" + DocletConstants.NL + "            }" + DocletConstants.NL + "        }" + DocletConstants.NL + "        return true;" + DocletConstants.NL + "    }" + DocletConstants.NL + "    function loadFrames() {" + DocletConstants.NL + "        if (targetPage != \"\" && targetPage != \"undefined\")" + DocletConstants.NL + "             top.classFrame.location = top.targetPage;" + DocletConstants.NL + "    }" + DocletConstants.NL;
      RawHtml var3 = new RawHtml(var2);
      var1.addContent((Content)var3);
      return var1;
   }

   public HtmlTree getBody(boolean var1, String var2) {
      HtmlTree var3 = new HtmlTree(HtmlTag.BODY);
      this.winTitle = var2;
      if (var1) {
         this.script = this.getWinTitleScript();
         var3.addContent(this.script);
         HtmlTree var4 = HtmlTree.NOSCRIPT(HtmlTree.DIV(this.getResource("doclet.No_Script_Message")));
         var3.addContent((Content)var4);
      }

      return var3;
   }

   public void generateMethodTypesScript(Map var1, Set var2) {
      String var3 = "";
      StringBuilder var4 = new StringBuilder("var methods = {");
      Iterator var5 = var1.entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry var6 = (Map.Entry)var5.next();
         var4.append(var3);
         var3 = ",";
         var4.append("\"").append((String)var6.getKey()).append("\":").append(var6.getValue());
      }

      var4.append("};").append(DocletConstants.NL);
      var3 = "";
      var4.append("var tabs = {");
      var5 = var2.iterator();

      while(var5.hasNext()) {
         MethodTypes var7 = (MethodTypes)var5.next();
         var4.append(var3);
         var3 = ",";
         var4.append(var7.value()).append(":").append("[").append("\"").append(var7.tabId()).append("\"").append(var3).append("\"").append(this.configuration.getText(var7.resourceKey())).append("\"]");
      }

      var4.append("};").append(DocletConstants.NL);
      this.addStyles(HtmlStyle.altColor, var4);
      this.addStyles(HtmlStyle.rowColor, var4);
      this.addStyles(HtmlStyle.tableTab, var4);
      this.addStyles(HtmlStyle.activeTableTab, var4);
      this.script.addContent((Content)(new RawHtml(var4.toString())));
   }

   public void addStyles(HtmlStyle var1, StringBuilder var2) {
      var2.append("var ").append(var1).append(" = \"").append(var1).append("\";").append(DocletConstants.NL);
   }

   public HtmlTree getTitle() {
      HtmlTree var1 = HtmlTree.TITLE(new StringContent(this.winTitle));
      return var1;
   }

   public String codeText(String var1) {
      return "<code>" + var1 + "</code>";
   }

   public Content getSpace() {
      return RawHtml.nbsp;
   }

   public String getModifierTypeHeader() {
      return this.modifierTypeHeader;
   }
}
