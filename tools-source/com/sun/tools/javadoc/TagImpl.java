package com.sun.tools.javadoc;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

class TagImpl implements Tag {
   protected final String text;
   protected final String name;
   protected final DocImpl holder;
   private Tag[] firstSentence;
   private Tag[] inlineTags;

   TagImpl(DocImpl var1, String var2, String var3) {
      this.holder = var1;
      this.name = var2;
      this.text = var3;
   }

   public String name() {
      return this.name;
   }

   public Doc holder() {
      return this.holder;
   }

   public String kind() {
      return this.name;
   }

   public String text() {
      return this.text;
   }

   DocEnv docenv() {
      return this.holder.env;
   }

   String[] divideAtWhite() {
      String[] var1 = new String[2];
      int var2 = this.text.length();
      var1[0] = this.text;
      var1[1] = "";

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = this.text.charAt(var3);
         if (Character.isWhitespace(var4)) {
            for(var1[0] = this.text.substring(0, var3); var3 < var2; ++var3) {
               var4 = this.text.charAt(var3);
               if (!Character.isWhitespace(var4)) {
                  var1[1] = this.text.substring(var3, var2);
                  return var1;
               }
            }

            return var1;
         }
      }

      return var1;
   }

   public String toString() {
      return this.name + ":" + this.text;
   }

   public Tag[] inlineTags() {
      if (this.inlineTags == null) {
         this.inlineTags = Comment.getInlineTags(this.holder, this.text);
      }

      return this.inlineTags;
   }

   public Tag[] firstSentenceTags() {
      if (this.firstSentence == null) {
         this.inlineTags();

         try {
            this.docenv().setSilent(true);
            this.firstSentence = Comment.firstSentenceTags(this.holder, this.text);
         } finally {
            this.docenv().setSilent(false);
         }
      }

      return this.firstSentence;
   }

   public SourcePosition position() {
      return this.holder.position();
   }
}
