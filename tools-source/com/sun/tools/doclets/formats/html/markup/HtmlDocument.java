package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HtmlDocument extends Content {
   private List docContent = Collections.emptyList();

   public HtmlDocument(Content var1, Content var2, Content var3) {
      this.docContent = new ArrayList();
      this.addContent((Content)nullCheck(var1));
      this.addContent((Content)nullCheck(var2));
      this.addContent((Content)nullCheck(var3));
   }

   public HtmlDocument(Content var1, Content var2) {
      this.docContent = new ArrayList();
      this.addContent((Content)nullCheck(var1));
      this.addContent((Content)nullCheck(var2));
   }

   public final void addContent(Content var1) {
      if (var1.isValid()) {
         this.docContent.add(var1);
      }

   }

   public void addContent(String var1) {
      throw new DocletAbortException("not supported");
   }

   public boolean isEmpty() {
      return this.docContent.isEmpty();
   }

   public boolean write(Writer var1, boolean var2) throws IOException {
      Content var4;
      for(Iterator var3 = this.docContent.iterator(); var3.hasNext(); var2 = var4.write(var1, var2)) {
         var4 = (Content)var3.next();
      }

      return var2;
   }
}
