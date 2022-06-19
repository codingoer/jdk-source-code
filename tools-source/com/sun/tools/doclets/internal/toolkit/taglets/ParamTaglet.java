package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.TypeVariable;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParamTaglet extends BaseTaglet implements InheritableTaglet {
   public ParamTaglet() {
      this.name = "param";
   }

   private static Map getRankMap(Object[] var0) {
      if (var0 == null) {
         return null;
      } else {
         HashMap var1 = new HashMap();

         for(int var2 = 0; var2 < var0.length; ++var2) {
            String var3 = var0[var2] instanceof Parameter ? ((Parameter)var0[var2]).name() : ((TypeVariable)var0[var2]).typeName();
            var1.put(var3, String.valueOf(var2));
         }

         return var1;
      }
   }

   public void inherit(DocFinder.Input var1, DocFinder.Output var2) {
      int var5;
      if (var1.tagId == null) {
         var1.isTypeVariableParamTag = ((ParamTag)var1.tag).isTypeParameter();
         Object[] var3 = var1.isTypeVariableParamTag ? (Object[])((MethodDoc)var1.tag.holder()).typeParameters() : (Object[])((MethodDoc)var1.tag.holder()).parameters();
         String var4 = ((ParamTag)var1.tag).parameterName();

         for(var5 = 0; var5 < var3.length; ++var5) {
            String var6 = var3[var5] instanceof Parameter ? ((Parameter)var3[var5]).name() : ((TypeVariable)var3[var5]).typeName();
            if (var6.equals(var4)) {
               var1.tagId = String.valueOf(var5);
               break;
            }
         }

         if (var5 == var3.length) {
            return;
         }
      }

      ParamTag[] var7 = var1.isTypeVariableParamTag ? ((MethodDoc)var1.element).typeParamTags() : ((MethodDoc)var1.element).paramTags();
      Map var8 = getRankMap(var1.isTypeVariableParamTag ? (Object[])((MethodDoc)var1.element).typeParameters() : (Object[])((MethodDoc)var1.element).parameters());

      for(var5 = 0; var5 < var7.length; ++var5) {
         if (var8.containsKey(var7[var5].parameterName()) && ((String)var8.get(var7[var5].parameterName())).equals(var1.tagId)) {
            var2.holder = var1.element;
            var2.holderTag = var7[var5];
            var2.inlineTags = var1.isFirstSentence ? var7[var5].firstSentenceTags() : var7[var5].inlineTags();
            return;
         }
      }

   }

   public boolean inField() {
      return false;
   }

   public boolean inMethod() {
      return true;
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

   public boolean isInlineTag() {
      return false;
   }

   public Content getTagletOutput(Doc var1, TagletWriter var2) {
      if (var1 instanceof ExecutableMemberDoc) {
         ExecutableMemberDoc var5 = (ExecutableMemberDoc)var1;
         Content var4 = this.getTagletOutput(false, var5, var2, var5.typeParameters(), var5.typeParamTags());
         var4.addContent(this.getTagletOutput(true, var5, var2, var5.parameters(), var5.paramTags()));
         return var4;
      } else {
         ClassDoc var3 = (ClassDoc)var1;
         return this.getTagletOutput(false, var3, var2, var3.typeParameters(), var3.typeParamTags());
      }
   }

   private Content getTagletOutput(boolean var1, Doc var2, TagletWriter var3, Object[] var4, ParamTag[] var5) {
      Content var6 = var3.getOutputInstance();
      HashSet var7 = new HashSet();
      if (var5.length > 0) {
         var6.addContent(this.processParamTags(var1, var5, getRankMap(var4), var3, var7));
      }

      if (var7.size() != var4.length) {
         var6.addContent(this.getInheritedTagletOutput(var1, var2, var3, var4, var7));
      }

      return var6;
   }

   private Content getInheritedTagletOutput(boolean var1, Doc var2, TagletWriter var3, Object[] var4, Set var5) {
      Content var6 = var3.getOutputInstance();
      if (!var5.contains((Object)null) && var2 instanceof MethodDoc) {
         for(int var7 = 0; var7 < var4.length; ++var7) {
            if (!var5.contains(String.valueOf(var7))) {
               DocFinder.Output var8 = DocFinder.search(new DocFinder.Input((MethodDoc)var2, this, String.valueOf(var7), !var1));
               if (var8.inlineTags != null && var8.inlineTags.length > 0) {
                  var6.addContent(this.processParamTag(var1, var3, (ParamTag)var8.holderTag, var1 ? ((Parameter)var4[var7]).name() : ((TypeVariable)var4[var7]).typeName(), var5.size() == 0));
               }

               var5.add(String.valueOf(var7));
            }
         }
      }

      return var6;
   }

   private Content processParamTags(boolean var1, ParamTag[] var2, Map var3, TagletWriter var4, Set var5) {
      Content var6 = var4.getOutputInstance();
      if (var2.length > 0) {
         for(int var7 = 0; var7 < var2.length; ++var7) {
            ParamTag var8 = var2[var7];
            String var9 = var1 ? var8.parameterName() : "<" + var8.parameterName() + ">";
            if (!var3.containsKey(var8.parameterName())) {
               var4.getMsgRetriever().warning(var8.position(), var1 ? "doclet.Parameters_warn" : "doclet.Type_Parameters_warn", var9);
            }

            String var10 = (String)var3.get(var8.parameterName());
            if (var10 != null && var5.contains(var10)) {
               var4.getMsgRetriever().warning(var8.position(), var1 ? "doclet.Parameters_dup_warn" : "doclet.Type_Parameters_dup_warn", var9);
            }

            var6.addContent(this.processParamTag(var1, var4, var8, var8.parameterName(), var5.size() == 0));
            var5.add(var10);
         }
      }

      return var6;
   }

   private Content processParamTag(boolean var1, TagletWriter var2, ParamTag var3, String var4, boolean var5) {
      Content var6 = var2.getOutputInstance();
      String var7 = var2.configuration().getText(var1 ? "doclet.Parameters" : "doclet.TypeParameters");
      if (var5) {
         var6.addContent(var2.getParamHeader(var7));
      }

      var6.addContent(var2.paramTagOutput(var3, var4));
      return var6;
   }
}
