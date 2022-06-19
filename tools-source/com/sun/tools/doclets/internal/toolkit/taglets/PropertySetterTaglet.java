package com.sun.tools.doclets.internal.toolkit.taglets;

public class PropertySetterTaglet extends BasePropertyTaglet {
   public PropertySetterTaglet() {
      this.name = "propertySetter";
   }

   String getText(TagletWriter var1) {
      return var1.configuration().getText("doclet.PropertySetter");
   }
}
