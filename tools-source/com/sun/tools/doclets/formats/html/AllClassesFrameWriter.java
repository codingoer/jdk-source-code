package com.sun.tools.doclets.formats.html;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.doclets.formats.html.markup.HtmlConstants;
import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.IndexBuilder;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import java.io.IOException;
import java.util.List;

public class AllClassesFrameWriter extends HtmlDocletWriter {
   protected IndexBuilder indexbuilder;
   final HtmlTree BR;

   public AllClassesFrameWriter(ConfigurationImpl var1, DocPath var2, IndexBuilder var3) throws IOException {
      super(var1, var2);
      this.BR = new HtmlTree(HtmlTag.BR);
      this.indexbuilder = var3;
   }

   public static void generate(ConfigurationImpl var0, IndexBuilder var1) {
      DocPath var3 = DocPaths.ALLCLASSES_FRAME;

      try {
         AllClassesFrameWriter var2 = new AllClassesFrameWriter(var0, var3, var1);
         var2.buildAllClassesFile(true);
         var2.close();
         var3 = DocPaths.ALLCLASSES_NOFRAME;
         var2 = new AllClassesFrameWriter(var0, var3, var1);
         var2.buildAllClassesFile(false);
         var2.close();
      } catch (IOException var5) {
         var0.standardmessage.error("doclet.exception_encountered", var5.toString(), var3);
         throw new DocletAbortException(var5);
      }
   }

   protected void buildAllClassesFile(boolean var1) throws IOException {
      String var2 = this.configuration.getText("doclet.All_Classes");
      HtmlTree var3 = this.getBody(false, this.getWindowTitle(var2));
      HtmlTree var4 = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, HtmlStyle.bar, this.allclassesLabel);
      var3.addContent((Content)var4);
      HtmlTree var5 = new HtmlTree(HtmlTag.UL);
      this.addAllClasses(var5, var1);
      HtmlTree var6 = HtmlTree.DIV(HtmlStyle.indexContainer, var5);
      var3.addContent((Content)var6);
      this.printHtmlDocument((String[])null, false, var3);
   }

   protected void addAllClasses(Content var1, boolean var2) {
      for(int var3 = 0; var3 < this.indexbuilder.elements().length; ++var3) {
         Character var4 = (Character)((Character)this.indexbuilder.elements()[var3]);
         this.addContents(this.indexbuilder.getMemberList(var4), var2, var1);
      }

   }

   protected void addContents(List var1, boolean var2, Content var3) {
      for(int var4 = 0; var4 < var1.size(); ++var4) {
         ClassDoc var5 = (ClassDoc)var1.get(var4);
         if (Util.isCoreClass(var5)) {
            Content var6 = this.italicsClassName(var5, false);
            Content var7;
            if (var2) {
               var7 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.ALL_CLASSES_FRAME, var5)).label(var6).target("classFrame"));
            } else {
               var7 = this.getLink((new LinkInfoImpl(this.configuration, LinkInfoImpl.Kind.DEFAULT, var5)).label(var6));
            }

            HtmlTree var8 = HtmlTree.LI(var7);
            var3.addContent((Content)var8);
         }
      }

   }
}
