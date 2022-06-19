package com.sun.tools.doclets.internal.toolkit.taglets;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import com.sun.tools.javac.util.StringUtils;

public class SimpleTaglet extends BaseTaglet implements InheritableTaglet {
   public static final String EXCLUDED = "x";
   public static final String PACKAGE = "p";
   public static final String TYPE = "t";
   public static final String CONSTRUCTOR = "c";
   public static final String FIELD = "f";
   public static final String METHOD = "m";
   public static final String OVERVIEW = "o";
   public static final String ALL = "a";
   protected String tagName;
   protected String header;
   protected String locations;

   public SimpleTaglet(String var1, String var2, String var3) {
      this.tagName = var1;
      this.header = var2;
      var3 = StringUtils.toLowerCase(var3);
      if (var3.indexOf("a") != -1 && var3.indexOf("x") == -1) {
         this.locations = "ptfmco";
      } else {
         this.locations = var3;
      }

   }

   public String getName() {
      return this.tagName;
   }

   public boolean inConstructor() {
      return this.locations.indexOf("c") != -1 && this.locations.indexOf("x") == -1;
   }

   public boolean inField() {
      return this.locations.indexOf("f") != -1 && this.locations.indexOf("x") == -1;
   }

   public boolean inMethod() {
      return this.locations.indexOf("m") != -1 && this.locations.indexOf("x") == -1;
   }

   public boolean inOverview() {
      return this.locations.indexOf("o") != -1 && this.locations.indexOf("x") == -1;
   }

   public boolean inPackage() {
      return this.locations.indexOf("p") != -1 && this.locations.indexOf("x") == -1;
   }

   public boolean inType() {
      return this.locations.indexOf("t") != -1 && this.locations.indexOf("x") == -1;
   }

   public boolean isInlineTag() {
      return false;
   }

   public void inherit(DocFinder.Input var1, DocFinder.Output var2) {
      Tag[] var3 = var1.element.tags(this.tagName);
      if (var3.length > 0) {
         var2.holder = var1.element;
         var2.holderTag = var3[0];
         var2.inlineTags = var1.isFirstSentence ? var3[0].firstSentenceTags() : var3[0].inlineTags();
      }

   }

   public Content getTagletOutput(Tag var1, TagletWriter var2) {
      return this.header != null && var1 != null ? var2.simpleTagOutput(var1, this.header) : null;
   }

   public Content getTagletOutput(Doc var1, TagletWriter var2) {
      return this.header != null && var1.tags(this.getName()).length != 0 ? var2.simpleTagOutput(var1.tags(this.getName()), this.header) : null;
   }
}
