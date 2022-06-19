package com.sun.tools.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;

class ThrowsTagImpl extends TagImpl implements ThrowsTag {
   private final String exceptionName;
   private final String exceptionComment;
   private Tag[] inlineTags;

   ThrowsTagImpl(DocImpl var1, String var2, String var3) {
      super(var1, var2, var3);
      String[] var4 = this.divideAtWhite();
      this.exceptionName = var4[0];
      this.exceptionComment = var4[1];
   }

   public String exceptionName() {
      return this.exceptionName;
   }

   public String exceptionComment() {
      return this.exceptionComment;
   }

   public ClassDoc exception() {
      ClassDocImpl var1;
      if (!(this.holder instanceof ExecutableMemberDoc)) {
         var1 = null;
      } else {
         ExecutableMemberDocImpl var2 = (ExecutableMemberDocImpl)this.holder;
         ClassDocImpl var3 = (ClassDocImpl)var2.containingClass();
         var1 = (ClassDocImpl)var3.findClass(this.exceptionName);
      }

      return var1;
   }

   public Type exceptionType() {
      return this.exception();
   }

   public String kind() {
      return "@throws";
   }

   public Tag[] inlineTags() {
      if (this.inlineTags == null) {
         this.inlineTags = Comment.getInlineTags(this.holder, this.exceptionComment());
      }

      return this.inlineTags;
   }
}
