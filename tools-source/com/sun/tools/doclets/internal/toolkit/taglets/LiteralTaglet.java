package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;
import java.util.Map;

public class LiteralTaglet extends BaseInlineTaglet {
   private static final String NAME = "literal";

   public static void register(Map var0) {
      var0.remove("literal");
      var0.put("literal", new LiteralTaglet());
   }

   public String getName() {
      return "literal";
   }

   public Content getTagletOutput(Tag var1, TagletWriter var2) {
      return var2.literalTagOutput(var1);
   }
}
