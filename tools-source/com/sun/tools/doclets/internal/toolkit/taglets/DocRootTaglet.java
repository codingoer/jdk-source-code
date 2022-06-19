package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;

public class DocRootTaglet extends BaseInlineTaglet {
   public DocRootTaglet() {
      this.name = "docRoot";
   }

   public Content getTagletOutput(Tag var1, TagletWriter var2) {
      return var2.getDocRootOutput();
   }
}
