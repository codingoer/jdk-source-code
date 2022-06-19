package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.SeeTag;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.MessageRetriever;

public abstract class TagletWriter {
   protected final boolean isFirstSentence;

   protected TagletWriter(boolean var1) {
      this.isFirstSentence = var1;
   }

   public abstract Content getOutputInstance();

   protected abstract Content codeTagOutput(Tag var1);

   protected abstract Content getDocRootOutput();

   protected abstract Content deprecatedTagOutput(Doc var1);

   protected abstract Content literalTagOutput(Tag var1);

   protected abstract MessageRetriever getMsgRetriever();

   protected abstract Content getParamHeader(String var1);

   protected abstract Content paramTagOutput(ParamTag var1, String var2);

   protected abstract Content propertyTagOutput(Tag var1, String var2);

   protected abstract Content returnTagOutput(Tag var1);

   protected abstract Content seeTagOutput(Doc var1, SeeTag[] var2);

   protected abstract Content simpleTagOutput(Tag[] var1, String var2);

   protected abstract Content simpleTagOutput(Tag var1, String var2);

   protected abstract Content getThrowsHeader();

   protected abstract Content throwsTagOutput(ThrowsTag var1);

   protected abstract Content throwsTagOutput(Type var1);

   protected abstract Content valueTagOutput(FieldDoc var1, String var2, boolean var3);

   public static void genTagOuput(TagletManager var0, Doc var1, Taglet[] var2, TagletWriter var3, Content var4) {
      var0.checkTags(var1, var1.tags(), false);
      var0.checkTags(var1, var1.inlineTags(), true);
      Content var5 = null;

      for(int var6 = 0; var6 < var2.length; ++var6) {
         var5 = null;
         if ((!(var1 instanceof ClassDoc) || !(var2[var6] instanceof ParamTaglet)) && !(var2[var6] instanceof DeprecatedTaglet)) {
            try {
               var5 = var2[var6].getTagletOutput(var1, var3);
            } catch (IllegalArgumentException var9) {
               Tag[] var8 = var1.tags(var2[var6].getName());
               if (var8.length > 0) {
                  var5 = var2[var6].getTagletOutput(var8[0], var3);
               }
            }

            if (var5 != null) {
               var0.seenCustomTag(var2[var6].getName());
               var4.addContent(var5);
            }
         }
      }

   }

   public static Content getInlineTagOuput(TagletManager var0, Tag var1, Tag var2, TagletWriter var3) {
      Taglet[] var4 = var0.getInlineCustomTaglets();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         if (("@" + var4[var5].getName()).equals(var2.name())) {
            var0.seenCustomTag(var4[var5].getName());
            Content var6 = var4[var5].getTagletOutput(var1 != null && var4[var5].getName().equals("inheritDoc") ? var1 : var2, var3);
            return var6;
         }
      }

      return null;
   }

   public abstract Content commentTagsToOutput(Tag var1, Tag[] var2);

   public abstract Content commentTagsToOutput(Doc var1, Tag[] var2);

   public abstract Content commentTagsToOutput(Tag var1, Doc var2, Tag[] var3, boolean var4);

   public abstract Configuration configuration();
}
