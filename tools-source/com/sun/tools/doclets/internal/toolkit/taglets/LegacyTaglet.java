package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.formats.html.markup.RawHtml;
import com.sun.tools.doclets.internal.toolkit.Content;

public class LegacyTaglet implements Taglet {
   private com.sun.tools.doclets.Taglet legacyTaglet;

   public LegacyTaglet(com.sun.tools.doclets.Taglet var1) {
      this.legacyTaglet = var1;
   }

   public boolean inField() {
      return this.legacyTaglet.isInlineTag() || this.legacyTaglet.inField();
   }

   public boolean inConstructor() {
      return this.legacyTaglet.isInlineTag() || this.legacyTaglet.inConstructor();
   }

   public boolean inMethod() {
      return this.legacyTaglet.isInlineTag() || this.legacyTaglet.inMethod();
   }

   public boolean inOverview() {
      return this.legacyTaglet.isInlineTag() || this.legacyTaglet.inOverview();
   }

   public boolean inPackage() {
      return this.legacyTaglet.isInlineTag() || this.legacyTaglet.inPackage();
   }

   public boolean inType() {
      return this.legacyTaglet.isInlineTag() || this.legacyTaglet.inType();
   }

   public boolean isInlineTag() {
      return this.legacyTaglet.isInlineTag();
   }

   public String getName() {
      return this.legacyTaglet.getName();
   }

   public Content getTagletOutput(Tag var1, TagletWriter var2) throws IllegalArgumentException {
      Content var3 = var2.getOutputInstance();
      var3.addContent((Content)(new RawHtml(this.legacyTaglet.toString(var1))));
      return var3;
   }

   public Content getTagletOutput(Doc var1, TagletWriter var2) throws IllegalArgumentException {
      Content var3 = var2.getOutputInstance();
      Tag[] var4 = var1.tags(this.getName());
      if (var4.length > 0) {
         String var5 = this.legacyTaglet.toString(var4);
         if (var5 != null) {
            var3.addContent((Content)(new RawHtml(var5)));
         }
      }

      return var3;
   }
}
