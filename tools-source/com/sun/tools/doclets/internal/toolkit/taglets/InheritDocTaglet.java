package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;

public class InheritDocTaglet extends BaseInlineTaglet {
   public static final String INHERIT_DOC_INLINE_TAG = "{@inheritDoc}";

   public InheritDocTaglet() {
      this.name = "inheritDoc";
   }

   public boolean inField() {
      return false;
   }

   public boolean inConstructor() {
      return false;
   }

   public boolean inOverview() {
      return false;
   }

   public boolean inPackage() {
      return false;
   }

   public boolean inType() {
      return true;
   }

   private Content retrieveInheritedDocumentation(TagletWriter var1, ProgramElementDoc var2, Tag var3, boolean var4) {
      Content var5 = var1.getOutputInstance();
      Configuration var6 = var1.configuration();
      Taglet var7 = var3 == null ? null : var6.tagletManager.getTaglet(var3.name());
      if (var7 != null && !(var7 instanceof InheritableTaglet)) {
         String var8 = var2.name() + (var2 instanceof ExecutableMemberDoc ? ((ExecutableMemberDoc)var2).flatSignature() : "");
         var6.message.warning(var2.position(), "doclet.noInheritedDoc", var8);
      }

      DocFinder.Output var10 = DocFinder.search(new DocFinder.Input(var2, (InheritableTaglet)var7, var3, var4, true));
      if (var10.isValidInheritDocTag) {
         if (var10.inlineTags.length > 0) {
            var5 = var1.commentTagsToOutput(var10.holderTag, var10.holder, var10.inlineTags, var4);
         }
      } else {
         String var9 = var2.name() + (var2 instanceof ExecutableMemberDoc ? ((ExecutableMemberDoc)var2).flatSignature() : "");
         var6.message.warning(var2.position(), "doclet.noInheritedDoc", var9);
      }

      return var5;
   }

   public Content getTagletOutput(Tag var1, TagletWriter var2) {
      if (!(var1.holder() instanceof ProgramElementDoc)) {
         return var2.getOutputInstance();
      } else {
         return var1.name().equals("@inheritDoc") ? this.retrieveInheritedDocumentation(var2, (ProgramElementDoc)var1.holder(), (Tag)null, var2.isFirstSentence) : this.retrieveInheritedDocumentation(var2, (ProgramElementDoc)var1.holder(), var1, var2.isFirstSentence);
      }
   }
}
