package com.sun.tools.internal.xjc.model;

import com.sun.xml.internal.xsom.XSComponent;
import javax.xml.bind.annotation.XmlTransient;
import org.xml.sax.Locator;

abstract class AbstractCElement extends AbstractCTypeInfoImpl implements CElement {
   @XmlTransient
   private final Locator locator;
   private boolean isAbstract;

   protected AbstractCElement(Model model, XSComponent source, Locator locator, CCustomizations customizations) {
      super(model, source, customizations);
      this.locator = locator;
   }

   public Locator getLocator() {
      return this.locator;
   }

   public boolean isAbstract() {
      return this.isAbstract;
   }

   public void setAbstract() {
      this.isAbstract = true;
   }
}
