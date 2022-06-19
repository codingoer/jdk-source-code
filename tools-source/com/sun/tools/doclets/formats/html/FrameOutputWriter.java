package com.sun.tools.doclets.formats.html;

import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import java.io.IOException;

public class FrameOutputWriter extends HtmlDocletWriter {
   int noOfPackages;
   private final String SCROLL_YES = "yes";

   public FrameOutputWriter(ConfigurationImpl var1, DocPath var2) throws IOException {
      super(var1, var2);
      this.noOfPackages = var1.packages.length;
   }

   public static void generate(ConfigurationImpl var0) {
      DocPath var2 = DocPath.empty;

      try {
         var2 = DocPaths.INDEX;
         FrameOutputWriter var1 = new FrameOutputWriter(var0, var2);
         var1.generateFrameFile();
         var1.close();
      } catch (IOException var4) {
         var0.standardmessage.error("doclet.exception_encountered", var4.toString(), var2);
         throw new DocletAbortException(var4);
      }
   }

   protected void generateFrameFile() throws IOException {
      Content var1 = this.getFrameDetails();
      if (this.configuration.windowtitle.length() > 0) {
         this.printFramesetDocument(this.configuration.windowtitle, this.configuration.notimestamp, var1);
      } else {
         this.printFramesetDocument(this.configuration.getText("doclet.Generated_Docs_Untitled"), this.configuration.notimestamp, var1);
      }

   }

   protected void addFrameWarning(Content var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.NOFRAMES);
      HtmlTree var3 = HtmlTree.NOSCRIPT(HtmlTree.DIV(this.getResource("doclet.No_Script_Message")));
      var2.addContent((Content)var3);
      HtmlTree var4 = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING, this.getResource("doclet.Frame_Alert"));
      var2.addContent((Content)var4);
      HtmlTree var5 = HtmlTree.P(this.getResource("doclet.Frame_Warning_Message", this.getHyperLink(this.configuration.topFile, this.configuration.getText("doclet.Non_Frame_Version"))));
      var2.addContent((Content)var5);
      var1.addContent((Content)var2);
   }

   protected Content getFrameDetails() {
      HtmlTree var1 = HtmlTree.FRAMESET("20%,80%", (String)null, "Documentation frame", "top.loadFrames()");
      if (this.noOfPackages <= 1) {
         this.addAllClassesFrameTag(var1);
      } else if (this.noOfPackages > 1) {
         HtmlTree var2 = HtmlTree.FRAMESET((String)null, "30%,70%", "Left frames", "top.loadFrames()");
         this.addAllPackagesFrameTag(var2);
         this.addAllClassesFrameTag(var2);
         var1.addContent((Content)var2);
      }

      this.addClassFrameTag(var1);
      this.addFrameWarning(var1);
      return var1;
   }

   private void addAllPackagesFrameTag(Content var1) {
      HtmlTree var2 = HtmlTree.FRAME(DocPaths.OVERVIEW_FRAME.getPath(), "packageListFrame", this.configuration.getText("doclet.All_Packages"));
      var1.addContent((Content)var2);
   }

   private void addAllClassesFrameTag(Content var1) {
      HtmlTree var2 = HtmlTree.FRAME(DocPaths.ALLCLASSES_FRAME.getPath(), "packageFrame", this.configuration.getText("doclet.All_classes_and_interfaces"));
      var1.addContent((Content)var2);
   }

   private void addClassFrameTag(Content var1) {
      HtmlTree var2 = HtmlTree.FRAME(this.configuration.topFile.getPath(), "classFrame", this.configuration.getText("doclet.Package_class_and_interface_descriptions"), "yes");
      var1.addContent((Content)var2);
   }
}
