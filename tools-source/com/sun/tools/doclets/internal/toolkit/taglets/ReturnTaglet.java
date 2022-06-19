package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;

public class ReturnTaglet extends BaseExecutableMemberTaglet implements InheritableTaglet {
   public ReturnTaglet() {
      this.name = "return";
   }

   public void inherit(DocFinder.Input var1, DocFinder.Output var2) {
      Tag[] var3 = var1.element.tags("return");
      if (var3.length > 0) {
         var2.holder = var1.element;
         var2.holderTag = var3[0];
         var2.inlineTags = var1.isFirstSentence ? var3[0].firstSentenceTags() : var3[0].inlineTags();
      }

   }

   public boolean inConstructor() {
      return false;
   }

   public Content getTagletOutput(Doc var1, TagletWriter var2) {
      Type var3 = ((MethodDoc)var1).returnType();
      Tag[] var4 = var1.tags(this.name);
      if (var3.isPrimitive() && var3.typeName().equals("void")) {
         if (var4.length > 0) {
            var2.getMsgRetriever().warning(var1.position(), "doclet.Return_tag_on_void_method");
         }

         return null;
      } else {
         if (var4.length == 0) {
            DocFinder.Output var5 = DocFinder.search(new DocFinder.Input((MethodDoc)var1, this));
            var4 = var5.holderTag == null ? var4 : new Tag[]{var5.holderTag};
         }

         return var4.length > 0 ? var2.returnTagOutput(var4[0]) : null;
      }
   }
}
