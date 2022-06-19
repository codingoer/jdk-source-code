package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Doc;
import com.sun.tools.doclets.internal.toolkit.Content;

public class DeprecatedTaglet extends BaseTaglet {
   public DeprecatedTaglet() {
      this.name = "deprecated";
   }

   public Content getTagletOutput(Doc var1, TagletWriter var2) {
      return var2.deprecatedTagOutput(var1);
   }
}
