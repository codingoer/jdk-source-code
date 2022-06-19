package com.sun.tools.javadoc;

import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Tag;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ParamTagImpl extends TagImpl implements ParamTag {
   private static final Pattern typeParamRE = Pattern.compile("<([^<>]+)>");
   private final String parameterName;
   private final String parameterComment;
   private final boolean isTypeParameter;
   private Tag[] inlineTags;

   ParamTagImpl(DocImpl var1, String var2, String var3) {
      super(var1, var2, var3);
      String[] var4 = this.divideAtWhite();
      Matcher var5 = typeParamRE.matcher(var4[0]);
      this.isTypeParameter = var5.matches();
      this.parameterName = this.isTypeParameter ? var5.group(1) : var4[0];
      this.parameterComment = var4[1];
   }

   public String parameterName() {
      return this.parameterName;
   }

   public String parameterComment() {
      return this.parameterComment;
   }

   public String kind() {
      return "@param";
   }

   public boolean isTypeParameter() {
      return this.isTypeParameter;
   }

   public String toString() {
      return this.name + ":" + this.text;
   }

   public Tag[] inlineTags() {
      if (this.inlineTags == null) {
         this.inlineTags = Comment.getInlineTags(this.holder, this.parameterComment);
      }

      return this.inlineTags;
   }
}
