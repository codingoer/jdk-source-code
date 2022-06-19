package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;

public abstract class BaseTaglet implements Taglet {
   protected String name = "Default";

   public boolean inConstructor() {
      return true;
   }

   public boolean inField() {
      return true;
   }

   public boolean inMethod() {
      return true;
   }

   public boolean inOverview() {
      return true;
   }

   public boolean inPackage() {
      return true;
   }

   public boolean inType() {
      return true;
   }

   public boolean isInlineTag() {
      return false;
   }

   public String getName() {
      return this.name;
   }

   public Content getTagletOutput(Tag var1, TagletWriter var2) {
      throw new IllegalArgumentException("Method not supported in taglet " + this.getName() + ".");
   }

   public Content getTagletOutput(Doc var1, TagletWriter var2) {
      throw new IllegalArgumentException("Method not supported in taglet " + this.getName() + ".");
   }
}
