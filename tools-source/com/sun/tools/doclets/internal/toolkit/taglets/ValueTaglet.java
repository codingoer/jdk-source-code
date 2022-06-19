package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;
import java.util.StringTokenizer;

public class ValueTaglet extends BaseInlineTaglet {
   public ValueTaglet() {
      this.name = "value";
   }

   public boolean inMethod() {
      return true;
   }

   public boolean inConstructor() {
      return true;
   }

   public boolean inOverview() {
      return true;
   }

   public boolean inPackage() {
      return true;
   }

   public boolean inType() {
      return true;
   }

   private FieldDoc getFieldDoc(Configuration var1, Tag var2, String var3) {
      if (var3 != null && var3.length() != 0) {
         StringTokenizer var4 = new StringTokenizer(var3, "#");
         String var5 = null;
         ClassDoc var6 = null;
         if (var4.countTokens() == 1) {
            Doc var7 = var2.holder();
            if (var7 instanceof MemberDoc) {
               var6 = ((MemberDoc)var7).containingClass();
            } else if (var7 instanceof ClassDoc) {
               var6 = (ClassDoc)var7;
            }

            var5 = var4.nextToken();
         } else {
            var6 = var1.root.classNamed(var4.nextToken());
            var5 = var4.nextToken();
         }

         if (var6 == null) {
            return null;
         } else {
            FieldDoc[] var9 = var6.fields();

            for(int var8 = 0; var8 < var9.length; ++var8) {
               if (var9[var8].name().equals(var5)) {
                  return var9[var8];
               }
            }

            return null;
         }
      } else {
         return var2.holder() instanceof FieldDoc ? (FieldDoc)var2.holder() : null;
      }
   }

   public Content getTagletOutput(Tag var1, TagletWriter var2) {
      FieldDoc var3 = this.getFieldDoc(var2.configuration(), var1, var1.text());
      if (var3 == null) {
         if (var1.text().isEmpty()) {
            var2.getMsgRetriever().warning(var1.holder().position(), "doclet.value_tag_invalid_use");
         } else {
            var2.getMsgRetriever().warning(var1.holder().position(), "doclet.value_tag_invalid_reference", var1.text());
         }
      } else {
         if (var3.constantValue() != null) {
            return var2.valueTagOutput(var3, var3.constantValueExpression(), !var3.equals(var1.holder()));
         }

         var2.getMsgRetriever().warning(var1.holder().position(), "doclet.value_tag_invalid_constant", var3.name());
      }

      return var2.getOutputInstance();
   }
}
