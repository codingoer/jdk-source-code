package com.sun.tools.internal.xjc.model;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.xsom.XSComponent;
import java.util.Collections;
import java.util.List;
import javax.activation.MimeType;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

abstract class CSingleTypePropertyInfo extends CPropertyInfo {
   protected final TypeUse type;
   private final QName schemaType;

   protected CSingleTypePropertyInfo(String name, TypeUse type, QName typeName, XSComponent source, CCustomizations customizations, Locator locator) {
      super(name, type.isCollection(), source, customizations, locator);
      this.type = type;
      if (needsExplicitTypeName(type, typeName)) {
         this.schemaType = typeName;
      } else {
         this.schemaType = null;
      }

   }

   public QName getSchemaType() {
      return this.schemaType;
   }

   public final ID id() {
      return this.type.idUse();
   }

   public final MimeType getExpectedMimeType() {
      return this.type.getExpectedMimeType();
   }

   public final List ref() {
      return Collections.singletonList(this.getTarget());
   }

   public final CNonElement getTarget() {
      CNonElement r = this.type.getInfo();

      assert r != null;

      return r;
   }

   public final CAdapter getAdapter() {
      return this.type.getAdapterUse();
   }

   public final CSingleTypePropertyInfo getSource() {
      return this;
   }
}
