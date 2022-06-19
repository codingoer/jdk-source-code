package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import java.io.IOException;
import java.io.Writer;

public class RawHtml extends Content {
   private String rawHtmlContent;
   public static final Content nbsp = new RawHtml("&nbsp;");

   public RawHtml(String var1) {
      this.rawHtmlContent = (String)nullCheck(var1);
   }

   public void addContent(Content var1) {
      throw new DocletAbortException("not supported");
   }

   public void addContent(String var1) {
      throw new DocletAbortException("not supported");
   }

   public boolean isEmpty() {
      return this.rawHtmlContent.isEmpty();
   }

   public String toString() {
      return this.rawHtmlContent;
   }

   public int charCount() {
      return charCount(this.rawHtmlContent);
   }

   static int charCount(String var0) {
      State var1 = RawHtml.State.TEXT;
      int var2 = 0;

      for(int var3 = 0; var3 < var0.length(); ++var3) {
         char var4 = var0.charAt(var3);
         switch (var1) {
            case TEXT:
               switch (var4) {
                  case '&':
                     var1 = RawHtml.State.ENTITY;
                     ++var2;
                     continue;
                  case '<':
                     var1 = RawHtml.State.TAG;
                     continue;
                  default:
                     ++var2;
                     continue;
               }
            case ENTITY:
               if (!Character.isLetterOrDigit(var4)) {
                  var1 = RawHtml.State.TEXT;
               }
               break;
            case TAG:
               switch (var4) {
                  case '"':
                     var1 = RawHtml.State.STRING;
                     continue;
                  case '>':
                     var1 = RawHtml.State.TEXT;
                  default:
                     continue;
               }
            case STRING:
               switch (var4) {
                  case '"':
                     var1 = RawHtml.State.TAG;
               }
         }
      }

      return var2;
   }

   public boolean write(Writer var1, boolean var2) throws IOException {
      var1.write(this.rawHtmlContent);
      return this.rawHtmlContent.endsWith(DocletConstants.NL);
   }

   private static enum State {
      TEXT,
      ENTITY,
      TAG,
      STRING;
   }
}
