package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;
import java.util.Map;

public class CodeTaglet extends BaseInlineTaglet {
   private static final String NAME = "code";

   public static void register(Map var0) {
      var0.remove("code");
      var0.put("code", new CodeTaglet());
   }

   public String getName() {
      return "code";
   }

   public Content getTagletOutput(Tag var1, TagletWriter var2) {
      return var2.codeTagOutput(var1);
   }
}
