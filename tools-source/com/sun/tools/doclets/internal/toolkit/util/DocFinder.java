package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.taglets.InheritableTaglet;
import java.util.ArrayList;
import java.util.List;

public class DocFinder {
   public static Output search(Input var0) {
      Output var1 = new Output();
      if (!var0.isInheritDocTag) {
         if (var0.taglet == null) {
            var1.inlineTags = var0.isFirstSentence ? var0.element.firstSentenceTags() : var0.element.inlineTags();
            var1.holder = var0.element;
         } else {
            var0.taglet.inherit(var0, var1);
         }
      }

      if (var1.inlineTags != null && var1.inlineTags.length > 0) {
         return var1;
      } else {
         var1.isValidInheritDocTag = false;
         Input var2 = var0.copy();
         var2.isInheritDocTag = false;
         if (var0.element instanceof MethodDoc) {
            MethodDoc var3 = ((MethodDoc)var0.element).overriddenMethod();
            if (var3 != null) {
               var2.element = var3;
               var1 = search(var2);
               var1.isValidInheritDocTag = true;
               if (var1.inlineTags.length > 0) {
                  return var1;
               }
            }

            MethodDoc[] var4 = (new ImplementedMethods((MethodDoc)var0.element, (Configuration)null)).build(false);

            for(int var5 = 0; var5 < var4.length; ++var5) {
               var2.element = var4[var5];
               var1 = search(var2);
               var1.isValidInheritDocTag = true;
               if (var1.inlineTags.length > 0) {
                  return var1;
               }
            }
         } else if (var0.element instanceof ClassDoc) {
            ClassDoc var6 = ((ClassDoc)var0.element).superclass();
            if (var6 != null) {
               var2.element = var6;
               var1 = search(var2);
               var1.isValidInheritDocTag = true;
               if (var1.inlineTags.length > 0) {
                  return var1;
               }
            }
         }

         return var1;
      }
   }

   public static class Output {
      public Tag holderTag;
      public Doc holder;
      public Tag[] inlineTags = new Tag[0];
      public boolean isValidInheritDocTag = true;
      public List tagList = new ArrayList();
   }

   public static class Input {
      public ProgramElementDoc element;
      public InheritableTaglet taglet;
      public String tagId;
      public Tag tag;
      public boolean isFirstSentence;
      public boolean isInheritDocTag;
      public boolean isTypeVariableParamTag;

      public Input(ProgramElementDoc var1, InheritableTaglet var2, Tag var3, boolean var4, boolean var5) {
         this(var1);
         this.taglet = var2;
         this.tag = var3;
         this.isFirstSentence = var4;
         this.isInheritDocTag = var5;
      }

      public Input(ProgramElementDoc var1, InheritableTaglet var2, String var3) {
         this(var1);
         this.taglet = var2;
         this.tagId = var3;
      }

      public Input(ProgramElementDoc var1, InheritableTaglet var2, String var3, boolean var4) {
         this(var1);
         this.taglet = var2;
         this.tagId = var3;
         this.isTypeVariableParamTag = var4;
      }

      public Input(ProgramElementDoc var1, InheritableTaglet var2) {
         this(var1);
         this.taglet = var2;
      }

      public Input(ProgramElementDoc var1) {
         this.taglet = null;
         this.tagId = null;
         this.tag = null;
         this.isFirstSentence = false;
         this.isInheritDocTag = false;
         this.isTypeVariableParamTag = false;
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.element = var1;
         }
      }

      public Input(ProgramElementDoc var1, boolean var2) {
         this(var1);
         this.isFirstSentence = var2;
      }

      public Input copy() {
         Input var1 = new Input(this.element);
         var1.taglet = this.taglet;
         var1.tagId = this.tagId;
         var1.tag = this.tag;
         var1.isFirstSentence = this.isFirstSentence;
         var1.isInheritDocTag = this.isInheritDocTag;
         var1.isTypeVariableParamTag = this.isTypeVariableParamTag;
         if (var1.element == null) {
            throw new NullPointerException();
         } else {
            return var1;
         }
      }
   }
}
