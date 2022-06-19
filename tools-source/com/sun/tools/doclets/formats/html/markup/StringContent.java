package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import java.io.IOException;
import java.io.Writer;

public class StringContent extends Content {
   private StringBuilder stringContent = new StringBuilder();

   public StringContent() {
   }

   public StringContent(String var1) {
      this.appendChars(var1);
   }

   public void addContent(Content var1) {
      throw new DocletAbortException("not supported");
   }

   public void addContent(String var1) {
      this.appendChars(var1);
   }

   public boolean isEmpty() {
      return this.stringContent.length() == 0;
   }

   public int charCount() {
      return RawHtml.charCount(this.stringContent.toString());
   }

   public String toString() {
      return this.stringContent.toString();
   }

   public boolean write(Writer var1, boolean var2) throws IOException {
      String var3 = this.stringContent.toString();
      var1.write(var3);
      return var3.endsWith(DocletConstants.NL);
   }

   private void appendChars(String var1) {
      for(int var2 = 0; var2 < var1.length(); ++var2) {
         char var3 = var1.charAt(var2);
         switch (var3) {
            case '&':
               this.stringContent.append("&amp;");
               break;
            case '<':
               this.stringContent.append("&lt;");
               break;
            case '>':
               this.stringContent.append("&gt;");
               break;
            default:
               this.stringContent.append(var3);
         }
      }

   }
}
