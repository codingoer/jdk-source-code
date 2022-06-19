package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.MethodDoc;

public class TaggedMethodFinder extends MethodFinder {
   public boolean isCorrectMethod(MethodDoc var1) {
      return var1.paramTags().length + var1.tags("return").length + var1.throwsTags().length + var1.seeTags().length > 0;
   }
}
