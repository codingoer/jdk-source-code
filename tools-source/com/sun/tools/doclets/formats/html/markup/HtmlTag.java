package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.javac.util.StringUtils;

public enum HtmlTag {
   A(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.END),
   BLOCKQUOTE,
   BODY(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.END),
   BR(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.NOEND),
   CAPTION,
   CENTER,
   CODE(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.END),
   DD,
   DIR,
   DIV,
   DL,
   DT,
   EM(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.END),
   FONT(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.END),
   FRAME(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.NOEND),
   FRAMESET(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.END),
   H1,
   H2,
   H3,
   H4,
   H5,
   H6,
   HEAD(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.END),
   HR(HtmlTag.BlockType.BLOCK, HtmlTag.EndTag.NOEND),
   HTML(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.END),
   I(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.END),
   IMG(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.NOEND),
   LI,
   LISTING,
   LINK(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.NOEND),
   MENU,
   META(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.NOEND),
   NOFRAMES(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.END),
   NOSCRIPT(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.END),
   OL,
   P,
   PRE,
   SCRIPT(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.END),
   SMALL(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.END),
   SPAN(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.END),
   STRONG(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.END),
   SUB(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.END),
   TABLE,
   TBODY,
   TD,
   TH,
   TITLE(HtmlTag.BlockType.OTHER, HtmlTag.EndTag.END),
   TR,
   TT(HtmlTag.BlockType.INLINE, HtmlTag.EndTag.END),
   UL;

   public final BlockType blockType;
   public final EndTag endTag;
   public final String value;

   private HtmlTag() {
      this(HtmlTag.BlockType.BLOCK, HtmlTag.EndTag.END);
   }

   private HtmlTag(BlockType var3, EndTag var4) {
      this.blockType = var3;
      this.endTag = var4;
      this.value = StringUtils.toLowerCase(this.name());
   }

   public boolean endTagRequired() {
      return this.endTag == HtmlTag.EndTag.END;
   }

   public String toString() {
      return this.value;
   }

   public static enum EndTag {
      END,
      NOEND;
   }

   public static enum BlockType {
      BLOCK,
      INLINE,
      OTHER;
   }
}
