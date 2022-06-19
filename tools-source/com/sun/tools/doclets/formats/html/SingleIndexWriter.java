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

public class SingleIndexWriter extends AbstractIndexWriter {
   public SingleIndexWriter(ConfigurationImpl var1, DocPath var2, IndexBuilder var3) throws IOException {
      super(var1, var2, var3);
   }

   public static void generate(ConfigurationImpl var0, IndexBuilder var1) {
      DocPath var3 = DocPaths.INDEX_ALL;

      try {
         SingleIndexWriter var2 = new SingleIndexWriter(var0, var3, var1);
         var2.generateIndexFile();
         var2.close();
      } catch (IOException var5) {
         var0.standardmessage.error("doclet.exception_encountered", var5.toString(), var3);
         throw new DocletAbortException(var5);
      }
   }

   protected void generateIndexFile() throws IOException {
      String var1 = this.configuration.getText("doclet.Window_Single_Index");
      HtmlTree var2 = this.getBody(true, this.getWindowTitle(var1));
      this.addTop(var2);
      this.addNavLinks(true, var2);
      HtmlTree var3 = new HtmlTree(HtmlTag.DIV);
      var3.addStyle(HtmlStyle.contentContainer);
      this.addLinksForIndexes(var3);

      for(int var4 = 0; var4 < this.indexbuilder.elements().length; ++var4) {
         Character var5 = (Character)((Character)this.indexbuilder.elements()[var4]);
         this.addContents(var5, this.indexbuilder.getMemberList(var5), var3);
      }

      this.addLinksForIndexes(var3);
      var2.addContent((Content)var3);
      this.addNavLinks(false, var2);
      this.addBottom(var2);
      this.printHtmlDocument((String[])null, true, var2);
   }

   protected void addLinksForIndexes(Content var1) {
      for(int var2 = 0; var2 < this.indexbuilder.elements().length; ++var2) {
         String var3 = this.indexbuilder.elements()[var2].toString();
         var1.addContent(this.getHyperLink(this.getNameForIndex(var3), new StringContent(var3)));
         var1.addContent(this.getSpace());
      }

   }
}
