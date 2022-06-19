package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.SeeTag;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;

public class SeeTaglet extends BaseTaglet implements InheritableTaglet {
   public SeeTaglet() {
      this.name = "see";
   }

   public void inherit(DocFinder.Input var1, DocFinder.Output var2) {
      SeeTag[] var3 = var1.element.seeTags();
      if (var3.length > 0) {
         var2.holder = var1.element;
         var2.holderTag = var3[0];
         var2.inlineTags = var1.isFirstSentence ? var3[0].firstSentenceTags() : var3[0].inlineTags();
      }

   }

   public Content getTagletOutput(Doc var1, TagletWriter var2) {
      SeeTag[] var3 = var1.seeTags();
      if (var3.length == 0 && var1 instanceof MethodDoc) {
         DocFinder.Output var4 = DocFinder.search(new DocFinder.Input((MethodDoc)var1, this));
         if (var4.holder != null) {
            var3 = var4.holder.seeTags();
         }
      }

      return var2.seeTagOutput(var1, var3);
   }
}
