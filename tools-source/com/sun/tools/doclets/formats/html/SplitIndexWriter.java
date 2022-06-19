package com.sun.tools.doclets.formats.html;

import com.sun.tools.doclets.formats.html.markup.HtmlStyle;
import com.sun.tools.doclets.formats.html.markup.HtmlTag;
import com.sun.tools.doclets.formats.html.markup.HtmlTree;
import com.sun.tools.doclets.formats.html.markup.StringContent;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocPath;
import com.sun.tools.doclets.internal.toolkit.util.DocPaths;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.IndexBuilder;
import java.io.IOException;

public class SplitIndexWriter extends AbstractIndexWriter {
   protected int prev;
   protected int next;

   public SplitIndexWriter(ConfigurationImpl var1, DocPath var2, IndexBuilder var3, int var4, int var5) throws IOException {
      super(var1, var2, var3);
      this.prev = var4;
      this.next = var5;
   }

   public static void generate(ConfigurationImpl var0, IndexBuilder var1) {
      DocPath var3 = DocPath.empty;
      DocPath var4 = DocPaths.INDEX_FILES;

      try {
         for(int var5 = 0; var5 < var1.elements().length; ++var5) {
            int var6 = var5 + 1;
            int var7 = var6 == 1 ? -1 : var5;
            int var8 = var6 == var1.elements().length ? -1 : var6 + 1;
            var3 = DocPaths.indexN(var6);
            SplitIndexWriter var2 = new SplitIndexWriter(var0, var4.resolve(var3), var1, var7, var8);
            var2.generateIndexFile((Character)var1.elements()[var5]);
            var2.close();
         }

      } catch (IOException var9) {
         var0.standardmessage.error("doclet.exception_encountered", var9.toString(), var3.getPath());
         throw new DocletAbortException(var9);
      }
   }

   protected void generateIndexFile(Character var1) throws IOException {
      String var2 = this.configuration.getText("doclet.Window_Split_Index", var1.toString());
      HtmlTree var3 = this.getBody(true, this.getWindowTitle(var2));
      this.addTop(var3);
      this.addNavLinks(true, var3);
      HtmlTree var4 = new HtmlTree(HtmlTag.DIV);
      var4.addStyle(HtmlStyle.contentContainer);
      this.addLinksForIndexes(var4);
      this.addContents(var1, this.indexbuilder.getMemberList(var1), var4);
      this.addLinksForIndexes(var4);
      var3.addContent((Content)var4);
      this.addNavLinks(false, var3);
      this.addBottom(var3);
      this.printHtmlDocument((String[])null, true, var3);
   }

   protected void addLinksForIndexes(Content var1) {
      Object[] var2 = this.indexbuilder.elements();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         int var4 = var3 + 1;
         var1.addContent(this.getHyperLink(DocPaths.indexN(var4), new StringContent(var2[var3].toString())));
         var1.addContent(this.getSpace());
      }

   }

   public Content getNavLinkPrevious() {
      Content var1 = this.getResource("doclet.Prev_Letter");
      if (this.prev == -1) {
         return HtmlTree.LI(var1);
      } else {
         Content var2 = this.getHyperLink(DocPaths.indexN(this.prev), var1);
         return HtmlTree.LI(var2);
      }
   }

   public Content getNavLinkNext() {
      Content var1 = this.getResource("doclet.Next_Letter");
      if (this.next == -1) {
         return HtmlTree.LI(var1);
      } else {
         Content var2 = this.getHyperLink(DocPaths.indexN(this.next), var1);
         return HtmlTree.LI(var2);
      }
   }
}
