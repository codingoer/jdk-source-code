package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JExpression;
import com.sun.tools.internal.xjc.outline.Outline;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.xsom.XSComponent;
import com.sun.xml.internal.xsom.XmlString;
import javax.activation.MimeType;

abstract class AbstractCTypeInfoImpl implements CTypeInfo {
   private final CCustomizations customizations;
   private final XSComponent source;

   protected AbstractCTypeInfoImpl(Model model, XSComponent source, CCustomizations customizations) {
      if (customizations == null) {
         customizations = CCustomizations.EMPTY;
      } else {
         customizations.setParent(model, this);
      }

      this.customizations = customizations;
      this.source = source;
   }

   public final boolean isCollection() {
      return false;
   }

   public final CAdapter getAdapterUse() {
      return null;
   }

   public final ID idUse() {
      return ID.NONE;
   }

   public final XSComponent getSchemaComponent() {
      return this.source;
   }

   /** @deprecated */
   public final boolean canBeReferencedByIDREF() {
      throw new UnsupportedOperationException();
   }

   public MimeType getExpectedMimeType() {
      return null;
   }

   public CCustomizations getCustomizations() {
      return this.customizations;
   }

   public JExpression createConstant(Outline outline, XmlString lexical) {
      return null;
   }

   public final Locatable getUpstream() {
      throw new UnsupportedOperationException();
   }

   public final Location getLocation() {
      throw new UnsupportedOperationException();
   }
}
