package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import java.io.IOException;
import java.io.Writer;

public class Comment extends Content {
   private String commentText;

   public Comment(String var1) {
      this.commentText = (String)nullCheck(var1);
   }

   public void addContent(Content var1) {
      throw new DocletAbortException("not supported");
   }

   public void addContent(String var1) {
      throw new DocletAbortException("not supported");
   }

   public boolean isEmpty() {
      return this.commentText.isEmpty();
   }

   public boolean write(Writer var1, boolean var2) throws IOException {
      if (!var2) {
         var1.write(DocletConstants.NL);
      }

      var1.write("<!-- ");
      var1.write(this.commentText);
      var1.write(" -->" + DocletConstants.NL);
      return true;
   }
}
