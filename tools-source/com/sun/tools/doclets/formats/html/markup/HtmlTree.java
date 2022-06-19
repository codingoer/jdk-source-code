package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocletConstants;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HtmlTree extends Content {
   private HtmlTag htmlTag;
   private Map attrs;
   private List content;
   public static final Content EMPTY = new StringContent("");
   public static final BitSet NONENCODING_CHARS = new BitSet(256);

   public HtmlTree(HtmlTag var1) {
      this.attrs = Collections.emptyMap();
      this.content = Collections.emptyList();
      this.htmlTag = (HtmlTag)nullCheck(var1);
   }

   public HtmlTree(HtmlTag var1, Content... var2) {
      this(var1);
      Content[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Content var6 = var3[var5];
         this.addContent(var6);
      }

   }

   public void addAttr(HtmlAttr var1, String var2) {
      if (this.attrs.isEmpty()) {
         this.attrs = new LinkedHashMap(3);
      }

      this.attrs.put(nullCheck(var1), escapeHtmlChars(var2));
   }

   public void setTitle(Content var1) {
      this.addAttr(HtmlAttr.TITLE, stripHtml(var1));
   }

   public void addStyle(HtmlStyle var1) {
      this.addAttr(HtmlAttr.CLASS, var1.toString());
   }

   public void addContent(Content var1) {
      if (var1 instanceof ContentBuilder) {
         Iterator var2 = ((ContentBuilder)var1).contents.iterator();

         while(var2.hasNext()) {
            Content var3 = (Content)var2.next();
            this.addContent(var3);
         }
      } else if (var1 == EMPTY || var1.isValid()) {
         if (this.content.isEmpty()) {
            this.content = new ArrayList();
         }

         this.content.add(var1);
      }

   }

   public void addContent(String var1) {
      if (!this.content.isEmpty()) {
         Content var2 = (Content)this.content.get(this.content.size() - 1);
         if (var2 instanceof StringContent) {
            var2.addContent(var1);
         } else {
            this.addContent((Content)(new StringContent(var1)));
         }
      } else {
         this.addContent((Content)(new StringContent(var1)));
      }

   }

   public int charCount() {
      int var1 = 0;

      Content var3;
      for(Iterator var2 = this.content.iterator(); var2.hasNext(); var1 += var3.charCount()) {
         var3 = (Content)var2.next();
      }

      return var1;
   }

   private static String escapeHtmlChars(String var0) {
      int var1 = 0;

      while(var1 < var0.length()) {
         char var2 = var0.charAt(var1);
         switch (var2) {
            case '&':
            case '<':
            case '>':
               StringBuilder var3;
               for(var3 = new StringBuilder(var0.substring(0, var1)); var1 < var0.length(); ++var1) {
                  var2 = var0.charAt(var1);
                  switch (var2) {
                     case '&':
                        var3.append("&amp;");
                        break;
                     case '<':
                        var3.append("&lt;");
                        break;
                     case '>':
                        var3.append("&gt;");
                        break;
                     default:
                        var3.append(var2);
                  }
               }

               return var3.toString();
            default:
               ++var1;
         }
      }

      return var0;
   }

   private static String encodeURL(String var0) {
      byte[] var1 = var0.getBytes(Charset.forName("UTF-8"));
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < var1.length; ++var3) {
         byte var4 = var1[var3];
         if (NONENCODING_CHARS.get(var4 & 255)) {
            var2.append((char)var4);
         } else {
            var2.append(String.format("%%%02X", var4 & 255));
         }
      }

      return var2.toString();
   }

   public static HtmlTree A(String var0, Content var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.A, new Content[]{(Content)nullCheck(var1)});
      var2.addAttr(HtmlAttr.HREF, encodeURL(var0));
      return var2;
   }

   public static HtmlTree A_NAME(String var0, Content var1) {
      HtmlTree var2 = A_NAME(var0);
      var2.addContent((Content)nullCheck(var1));
      return var2;
   }

   public static HtmlTree A_NAME(String var0) {
      HtmlTree var1 = new HtmlTree(HtmlTag.A);
      var1.addAttr(HtmlAttr.NAME, (String)nullCheck(var0));
      return var1;
   }

   public static HtmlTree CAPTION(Content var0) {
      HtmlTree var1 = new HtmlTree(HtmlTag.CAPTION, new Content[]{(Content)nullCheck(var0)});
      return var1;
   }

   public static HtmlTree CODE(Content var0) {
      HtmlTree var1 = new HtmlTree(HtmlTag.CODE, new Content[]{(Content)nullCheck(var0)});
      return var1;
   }

   public static HtmlTree DD(Content var0) {
      HtmlTree var1 = new HtmlTree(HtmlTag.DD, new Content[]{(Content)nullCheck(var0)});
      return var1;
   }

   public static HtmlTree DL(Content var0) {
      HtmlTree var1 = new HtmlTree(HtmlTag.DL, new Content[]{(Content)nullCheck(var0)});
      return var1;
   }

   public static HtmlTree DIV(HtmlStyle var0, Content var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.DIV, new Content[]{(Content)nullCheck(var1)});
      if (var0 != null) {
         var2.addStyle(var0);
      }

      return var2;
   }

   public static HtmlTree DIV(Content var0) {
      return DIV((HtmlStyle)null, var0);
   }

   public static HtmlTree DT(Content var0) {
      HtmlTree var1 = new HtmlTree(HtmlTag.DT, new Content[]{(Content)nullCheck(var0)});
      return var1;
   }

   public static HtmlTree FRAME(String var0, String var1, String var2, String var3) {
      HtmlTree var4 = new HtmlTree(HtmlTag.FRAME);
      var4.addAttr(HtmlAttr.SRC, (String)nullCheck(var0));
      var4.addAttr(HtmlAttr.NAME, (String)nullCheck(var1));
      var4.addAttr(HtmlAttr.TITLE, (String)nullCheck(var2));
      if (var3 != null) {
         var4.addAttr(HtmlAttr.SCROLLING, var3);
      }

      return var4;
   }

   public static HtmlTree FRAME(String var0, String var1, String var2) {
      return FRAME(var0, var1, var2, (String)null);
   }

   public static HtmlTree FRAMESET(String var0, String var1, String var2, String var3) {
      HtmlTree var4 = new HtmlTree(HtmlTag.FRAMESET);
      if (var0 != null) {
         var4.addAttr(HtmlAttr.COLS, var0);
      }

      if (var1 != null) {
         var4.addAttr(HtmlAttr.ROWS, var1);
      }

      var4.addAttr(HtmlAttr.TITLE, (String)nullCheck(var2));
      var4.addAttr(HtmlAttr.ONLOAD, (String)nullCheck(var3));
      return var4;
   }

   public static HtmlTree HEADING(HtmlTag var0, boolean var1, HtmlStyle var2, Content var3) {
      HtmlTree var4 = new HtmlTree(var0, new Content[]{(Content)nullCheck(var3)});
      if (var1) {
         var4.setTitle(var3);
      }

      if (var2 != null) {
         var4.addStyle(var2);
      }

      return var4;
   }

   public static HtmlTree HEADING(HtmlTag var0, HtmlStyle var1, Content var2) {
      return HEADING(var0, false, var1, var2);
   }

   public static HtmlTree HEADING(HtmlTag var0, boolean var1, Content var2) {
      return HEADING(var0, var1, (HtmlStyle)null, var2);
   }

   public static HtmlTree HEADING(HtmlTag var0, Content var1) {
      return HEADING(var0, false, (HtmlStyle)null, var1);
   }

   public static HtmlTree HTML(String var0, Content var1, Content var2) {
      HtmlTree var3 = new HtmlTree(HtmlTag.HTML, new Content[]{(Content)nullCheck(var1), (Content)nullCheck(var2)});
      var3.addAttr(HtmlAttr.LANG, (String)nullCheck(var0));
      return var3;
   }

   public static HtmlTree LI(Content var0) {
      return LI((HtmlStyle)null, var0);
   }

   public static HtmlTree LI(HtmlStyle var0, Content var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.LI, new Content[]{(Content)nullCheck(var1)});
      if (var0 != null) {
         var2.addStyle(var0);
      }

      return var2;
   }

   public static HtmlTree LINK(String var0, String var1, String var2, String var3) {
      HtmlTree var4 = new HtmlTree(HtmlTag.LINK);
      var4.addAttr(HtmlAttr.REL, (String)nullCheck(var0));
      var4.addAttr(HtmlAttr.TYPE, (String)nullCheck(var1));
      var4.addAttr(HtmlAttr.HREF, (String)nullCheck(var2));
      var4.addAttr(HtmlAttr.TITLE, (String)nullCheck(var3));
      return var4;
   }

   public static HtmlTree META(String var0, String var1, String var2) {
      HtmlTree var3 = new HtmlTree(HtmlTag.META);
      String var4 = var1 + "; charset=" + var2;
      var3.addAttr(HtmlAttr.HTTP_EQUIV, (String)nullCheck(var0));
      var3.addAttr(HtmlAttr.CONTENT, var4);
      return var3;
   }

   public static HtmlTree META(String var0, String var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.META);
      var2.addAttr(HtmlAttr.NAME, (String)nullCheck(var0));
      var2.addAttr(HtmlAttr.CONTENT, (String)nullCheck(var1));
      return var2;
   }

   public static HtmlTree NOSCRIPT(Content var0) {
      HtmlTree var1 = new HtmlTree(HtmlTag.NOSCRIPT, new Content[]{(Content)nullCheck(var0)});
      return var1;
   }

   public static HtmlTree P(Content var0) {
      return P((HtmlStyle)null, var0);
   }

   public static HtmlTree P(HtmlStyle var0, Content var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.P, new Content[]{(Content)nullCheck(var1)});
      if (var0 != null) {
         var2.addStyle(var0);
      }

      return var2;
   }

   public static HtmlTree SCRIPT(String var0, String var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.SCRIPT);
      var2.addAttr(HtmlAttr.TYPE, (String)nullCheck(var0));
      var2.addAttr(HtmlAttr.SRC, (String)nullCheck(var1));
      return var2;
   }

   public static HtmlTree SMALL(Content var0) {
      HtmlTree var1 = new HtmlTree(HtmlTag.SMALL, new Content[]{(Content)nullCheck(var0)});
      return var1;
   }

   public static HtmlTree SPAN(Content var0) {
      return SPAN((HtmlStyle)null, var0);
   }

   public static HtmlTree SPAN(HtmlStyle var0, Content var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.SPAN, new Content[]{(Content)nullCheck(var1)});
      if (var0 != null) {
         var2.addStyle(var0);
      }

      return var2;
   }

   public static HtmlTree SPAN(String var0, HtmlStyle var1, Content var2) {
      HtmlTree var3 = new HtmlTree(HtmlTag.SPAN, new Content[]{(Content)nullCheck(var2)});
      var3.addAttr(HtmlAttr.ID, (String)nullCheck(var0));
      if (var1 != null) {
         var3.addStyle(var1);
      }

      return var3;
   }

   public static HtmlTree TABLE(HtmlStyle var0, int var1, int var2, int var3, String var4, Content var5) {
      HtmlTree var6 = new HtmlTree(HtmlTag.TABLE, new Content[]{(Content)nullCheck(var5)});
      if (var0 != null) {
         var6.addStyle(var0);
      }

      var6.addAttr(HtmlAttr.BORDER, Integer.toString(var1));
      var6.addAttr(HtmlAttr.CELLPADDING, Integer.toString(var2));
      var6.addAttr(HtmlAttr.CELLSPACING, Integer.toString(var3));
      var6.addAttr(HtmlAttr.SUMMARY, (String)nullCheck(var4));
      return var6;
   }

   public static HtmlTree TD(HtmlStyle var0, Content var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.TD, new Content[]{(Content)nullCheck(var1)});
      if (var0 != null) {
         var2.addStyle(var0);
      }

      return var2;
   }

   public static HtmlTree TD(Content var0) {
      return TD((HtmlStyle)null, var0);
   }

   public static HtmlTree TH(HtmlStyle var0, String var1, Content var2) {
      HtmlTree var3 = new HtmlTree(HtmlTag.TH, new Content[]{(Content)nullCheck(var2)});
      if (var0 != null) {
         var3.addStyle(var0);
      }

      var3.addAttr(HtmlAttr.SCOPE, (String)nullCheck(var1));
      return var3;
   }

   public static HtmlTree TH(String var0, Content var1) {
      return TH((HtmlStyle)null, var0, var1);
   }

   public static HtmlTree TITLE(Content var0) {
      HtmlTree var1 = new HtmlTree(HtmlTag.TITLE, new Content[]{(Content)nullCheck(var0)});
      return var1;
   }

   public static HtmlTree TR(Content var0) {
      HtmlTree var1 = new HtmlTree(HtmlTag.TR, new Content[]{(Content)nullCheck(var0)});
      return var1;
   }

   public static HtmlTree UL(HtmlStyle var0, Content var1) {
      HtmlTree var2 = new HtmlTree(HtmlTag.UL, new Content[]{(Content)nullCheck(var1)});
      var2.addStyle((HtmlStyle)nullCheck(var0));
      return var2;
   }

   public boolean isEmpty() {
      return !this.hasContent() && !this.hasAttrs();
   }

   public boolean hasContent() {
      return !this.content.isEmpty();
   }

   public boolean hasAttrs() {
      return !this.attrs.isEmpty();
   }

   public boolean hasAttr(HtmlAttr var1) {
      return this.attrs.containsKey(var1);
   }

   public boolean isValid() {
      switch (this.htmlTag) {
         case A:
            return this.hasAttr(HtmlAttr.NAME) || this.hasAttr(HtmlAttr.HREF) && this.hasContent();
         case BR:
            return !this.hasContent() && (!this.hasAttrs() || this.hasAttr(HtmlAttr.CLEAR));
         case FRAME:
            return this.hasAttr(HtmlAttr.SRC) && !this.hasContent();
         case HR:
            return !this.hasContent();
         case IMG:
            return this.hasAttr(HtmlAttr.SRC) && this.hasAttr(HtmlAttr.ALT) && !this.hasContent();
         case LINK:
            return this.hasAttr(HtmlAttr.HREF) && !this.hasContent();
         case META:
            return this.hasAttr(HtmlAttr.CONTENT) && !this.hasContent();
         case SCRIPT:
            return this.hasAttr(HtmlAttr.TYPE) && this.hasAttr(HtmlAttr.SRC) && !this.hasContent() || this.hasAttr(HtmlAttr.TYPE) && this.hasContent();
         default:
            return this.hasContent();
      }
   }

   public boolean isInline() {
      return this.htmlTag.blockType == HtmlTag.BlockType.INLINE;
   }

   public boolean write(Writer var1, boolean var2) throws IOException {
      if (!this.isInline() && !var2) {
         var1.write(DocletConstants.NL);
      }

      String var3 = this.htmlTag.toString();
      var1.write("<");
      var1.write(var3);
      Iterator var4 = this.attrs.keySet().iterator();

      while(var4.hasNext()) {
         HtmlAttr var5 = (HtmlAttr)var4.next();
         String var6 = (String)this.attrs.get(var5);
         var1.write(" ");
         var1.write(var5.toString());
         if (!var6.isEmpty()) {
            var1.write("=\"");
            var1.write(var6);
            var1.write("\"");
         }
      }

      var1.write(">");
      boolean var7 = false;

      Content var9;
      for(Iterator var8 = this.content.iterator(); var8.hasNext(); var7 = var9.write(var1, var7)) {
         var9 = (Content)var8.next();
      }

      if (this.htmlTag.endTagRequired()) {
         var1.write("</");
         var1.write(var3);
         var1.write(">");
      }

      if (!this.isInline()) {
         var1.write(DocletConstants.NL);
         return true;
      } else {
         return false;
      }
   }

   private static String stripHtml(Content var0) {
      String var1 = var0.toString();
      var1 = var1.replaceAll("\\<.*?>", " ");
      var1 = var1.replaceAll("\\b\\s{2,}\\b", " ");
      return var1.trim();
   }

   static {
      int var0;
      for(var0 = 97; var0 <= 122; ++var0) {
         NONENCODING_CHARS.set(var0);
      }

      for(var0 = 65; var0 <= 90; ++var0) {
         NONENCODING_CHARS.set(var0);
      }

      for(var0 = 48; var0 <= 57; ++var0) {
         NONENCODING_CHARS.set(var0);
      }

      String var2 = ":/?#[]@!$&'()*+,;=";
      var2 = var2 + "-._~";

      for(int var1 = 0; var1 < var2.length(); ++var1) {
         NONENCODING_CHARS.set(var2.charAt(var1));
      }

   }
}
