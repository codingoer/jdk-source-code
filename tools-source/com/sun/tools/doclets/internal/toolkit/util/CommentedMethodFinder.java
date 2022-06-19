package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.MethodDoc;

public class CommentedMethodFinder extends MethodFinder {
   public boolean isCorrectMethod(MethodDoc var1) {
      return var1.inlineTags().length > 0;
   }
}
