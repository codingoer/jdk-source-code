package com.sun.tools.internal.xjc.model;

import org.w3c.dom.Element;
import org.xml.sax.Locator;

public class CPluginCustomization {
   public final Element element;
   public final Locator locator;
   private boolean acknowledged;

   public void markAsAcknowledged() {
      this.acknowledged = true;
   }

   public CPluginCustomization(Element element, Locator locator) {
      this.element = element;
      this.locator = locator;
   }

   public boolean isAcknowledged() {
      return this.acknowledged;
   }
}
