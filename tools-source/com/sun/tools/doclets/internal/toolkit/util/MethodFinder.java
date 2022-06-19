package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;

public abstract class MethodFinder {
   abstract boolean isCorrectMethod(MethodDoc var1);

   public MethodDoc search(ClassDoc var1, MethodDoc var2) {
      MethodDoc var3 = this.searchInterfaces(var1, var2);
      if (var3 != null) {
         return var3;
      } else {
         ClassDoc var4 = var1.superclass();
         if (var4 != null) {
            var3 = Util.findMethod(var4, var2);
            return var3 != null && this.isCorrectMethod(var3) ? var3 : this.search(var4, var2);
         } else {
            return null;
         }
      }
   }

   public MethodDoc searchInterfaces(ClassDoc var1, MethodDoc var2) {
      MethodDoc[] var3 = (new ImplementedMethods(var2, (Configuration)null)).build();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         if (this.isCorrectMethod(var3[var4])) {
            return var3[var4];
         }
      }

      return null;
   }
}
