package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

public class TextTag implements Tag {
   protected final String text;
   protected final String name = "Text";
   protected final Doc holder;

   public TextTag(Doc var1, String var2) {
      this.holder = var1;
      this.text = var2;
   }

   public String name() {
      return "Text";
   }

   public Doc holder() {
      return this.holder;
   }

   public String kind() {
      return "Text";
   }

   public String text() {
      return this.text;
   }

   public String toString() {
      return "Text:" + this.text;
   }

   public Tag[] inlineTags() {
      return new Tag[]{this};
   }

   public Tag[] firstSentenceTags() {
      return new Tag[]{this};
   }

   public SourcePosition position() {
      return this.holder.position();
   }
}
