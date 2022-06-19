package com.sun.tools.doclets.internal.toolkit.taglets;

public class PropertyGetterTaglet extends BasePropertyTaglet {
   public PropertyGetterTaglet() {
      this.name = "propertyGetter";
   }

   String getText(TagletWriter var1) {
      return var1.configuration().getText("doclet.PropertyGetter");
   }
}
