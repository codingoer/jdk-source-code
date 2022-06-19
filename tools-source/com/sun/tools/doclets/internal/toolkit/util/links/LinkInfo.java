package com.sun.tools.doclets.internal.toolkit.util.links;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.Type;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.Content;

public abstract class LinkInfo {
   public ClassDoc classDoc;
   public ExecutableMemberDoc executableMemberDoc;
   public Type type;
   public boolean isVarArg = false;
   public boolean isTypeBound = false;
   public boolean isJava5DeclarationLocation = true;
   public Content label;
   public boolean isStrong = false;
   public boolean includeTypeInClassLinkLabel = true;
   public boolean includeTypeAsSepLink = false;
   public boolean excludeTypeBounds = false;
   public boolean excludeTypeParameterLinks = false;
   public boolean excludeTypeBoundsLinks = false;
   public boolean linkToSelf = true;

   protected abstract Content newContent();

   public abstract boolean isLinkable();

   public Content getClassLinkLabel(Configuration var1) {
      if (this.label != null && !this.label.isEmpty()) {
         return this.label;
      } else {
         Content var2;
         if (this.isLinkable()) {
            var2 = this.newContent();
            var2.addContent(this.classDoc.name());
            return var2;
         } else {
            var2 = this.newContent();
            var2.addContent(var1.getClassName(this.classDoc));
            return var2;
         }
      }
   }
}
