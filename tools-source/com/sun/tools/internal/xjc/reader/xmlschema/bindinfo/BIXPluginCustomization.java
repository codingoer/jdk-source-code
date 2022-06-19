package com.sun.tools.internal.xjc.reader.xmlschema.bindinfo;

import com.sun.tools.internal.xjc.model.Model;
import com.sun.tools.internal.xjc.reader.Ring;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.xml.sax.Locator;

public final class BIXPluginCustomization extends AbstractDeclarationImpl {
   public final Element element;
   private QName name;

   public BIXPluginCustomization(Element e, Locator _loc) {
      super(_loc);
      this.element = e;
   }

   public void onSetOwner() {
      super.onSetOwner();
      if (!((Model)Ring.get(Model.class)).options.pluginURIs.contains(this.element.getNamespaceURI())) {
         this.markAsAcknowledged();
      }

   }

   public final QName getName() {
      if (this.name == null) {
         this.name = new QName(this.element.getNamespaceURI(), this.element.getLocalName());
      }

      return this.name;
   }
}
