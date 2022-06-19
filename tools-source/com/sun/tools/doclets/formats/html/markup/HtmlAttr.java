package com.sun.tools.doclets.formats.html.markup;

import com.sun.tools.javac.util.StringUtils;

public enum HtmlAttr {
   ALT,
   BORDER,
   CELLPADDING,
   CELLSPACING,
   CLASS,
   CLEAR,
   COLS,
   CONTENT,
   HREF,
   HTTP_EQUIV("http-equiv"),
   ID,
   LANG,
   NAME,
   ONLOAD,
   REL,
   ROWS,
   SCOPE,
   SCROLLING,
   SRC,
   SUMMARY,
   TARGET,
   TITLE,
   TYPE,
   WIDTH;

   private final String value;

   private HtmlAttr() {
      this.value = StringUtils.toLowerCase(this.name());
   }

   private HtmlAttr(String var3) {
      this.value = var3;
   }

   public String toString() {
      return this.value;
   }
}
