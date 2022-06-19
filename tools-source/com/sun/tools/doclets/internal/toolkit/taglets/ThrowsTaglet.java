package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ThrowsTaglet extends BaseExecutableMemberTaglet implements InheritableTaglet {
   public ThrowsTaglet() {
      this.name = "throws";
   }

   public void inherit(DocFinder.Input var1, DocFinder.Output var2) {
      ClassDoc var3;
      if (var1.tagId == null) {
         ThrowsTag var4 = (ThrowsTag)var1.tag;
         var3 = var4.exception();
         var1.tagId = var3 == null ? var4.exceptionName() : var4.exception().qualifiedName();
      } else {
         var3 = var1.element.containingClass().findClass(var1.tagId);
      }

      ThrowsTag[] var6 = ((MethodDoc)var1.element).throwsTags();

      for(int var5 = 0; var5 < var6.length; ++var5) {
         if (var1.tagId.equals(var6[var5].exceptionName()) || var6[var5].exception() != null && var1.tagId.equals(var6[var5].exception().qualifiedName())) {
            var2.holder = var1.element;
            var2.holderTag = var6[var5];
            var2.inlineTags = var1.isFirstSentence ? var6[var5].firstSentenceTags() : var6[var5].inlineTags();
            var2.tagList.add(var6[var5]);
         } else if (var3 != null && var6[var5].exception() != null && var6[var5].exception().subclassOf(var3)) {
            var2.tagList.add(var6[var5]);
         }
      }

   }

   private Content linkToUndocumentedDeclaredExceptions(Type[] var1, Set var2, TagletWriter var3) {
      Content var4 = var3.getOutputInstance();

      for(int var5 = 0; var5 < var1.length; ++var5) {
         if (var1[var5].asClassDoc() != null && !var2.contains(var1[var5].asClassDoc().name()) && !var2.contains(var1[var5].asClassDoc().qualifiedName())) {
            if (var2.size() == 0) {
               var4.addContent(var3.getThrowsHeader());
            }

            var4.addContent(var3.throwsTagOutput(var1[var5]));
            var2.add(var1[var5].asClassDoc().name());
         }
      }

      return var4;
   }

   private Content inheritThrowsDocumentation(Doc var1, Type[] var2, Set var3, TagletWriter var4) {
      Content var5 = var4.getOutputInstance();
      if (var1 instanceof MethodDoc) {
         LinkedHashSet var6 = new LinkedHashSet();

         for(int var7 = 0; var7 < var2.length; ++var7) {
            DocFinder.Output var8 = DocFinder.search(new DocFinder.Input((MethodDoc)var1, this, var2[var7].typeName()));
            if (var8.tagList.size() == 0) {
               var8 = DocFinder.search(new DocFinder.Input((MethodDoc)var1, this, var2[var7].qualifiedTypeName()));
            }

            var6.addAll(var8.tagList);
         }

         var5.addContent(this.throwsTagsOutput((ThrowsTag[])var6.toArray(new ThrowsTag[0]), var4, var3, false));
      }

      return var5;
   }

   public Content getTagletOutput(Doc var1, TagletWriter var2) {
      ExecutableMemberDoc var3 = (ExecutableMemberDoc)var1;
      ThrowsTag[] var4 = var3.throwsTags();
      Content var5 = var2.getOutputInstance();
      HashSet var6 = new HashSet();
      if (var4.length > 0) {
         var5.addContent(this.throwsTagsOutput(var3.throwsTags(), var2, var6, true));
      }

      var5.addContent(this.inheritThrowsDocumentation(var1, var3.thrownExceptionTypes(), var6, var2));
      var5.addContent(this.linkToUndocumentedDeclaredExceptions(var3.thrownExceptionTypes(), var6, var2));
      return var5;
   }

   protected Content throwsTagsOutput(ThrowsTag[] var1, TagletWriter var2, Set var3, boolean var4) {
      Content var5 = var2.getOutputInstance();
      if (var1.length > 0) {
         for(int var6 = 0; var6 < var1.length; ++var6) {
            ThrowsTag var7 = var1[var6];
            ClassDoc var8 = var7.exception();
            if (var4 || !var3.contains(var7.exceptionName()) && (var8 == null || !var3.contains(var8.qualifiedName()))) {
               if (var3.size() == 0) {
                  var5.addContent(var2.getThrowsHeader());
               }

               var5.addContent(var2.throwsTagOutput(var7));
               var3.add(var8 != null ? var8.qualifiedName() : var7.exceptionName());
            }
         }
      }

      return var5;
   }
}
