package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;

public abstract class BasePropertyTaglet extends BaseTaglet {
   abstract String getText(TagletWriter var1);

   public Content getTagletOutput(Tag var1, TagletWriter var2) {
      return var2.propertyTagOutput(var1, this.getText(var2));
   }

   public boolean inConstructor() {
      return false;
   }

   public boolean inOverview() {
      return false;
   }

   public boolean inPackage() {
      return false;
   }

   public boolean inType() {
      return false;
   }

   public boolean isInlineTag() {
      return false;
   }
}
