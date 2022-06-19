package com.sun.tools.internal.xjc.model;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.xsom.XSComponent;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public final class CAttributePropertyInfo extends CSingleTypePropertyInfo implements AttributePropertyInfo {
   private final QName attName;
   private final boolean isRequired;

   public CAttributePropertyInfo(String name, XSComponent source, CCustomizations customizations, Locator locator, QName attName, TypeUse type, @Nullable QName typeName, boolean required) {
      super(name, type, typeName, source, customizations, locator);
      this.isRequired = required;
      this.attName = attName;
   }

   public boolean isRequired() {
      return this.isRequired;
   }

   public QName getXmlName() {
      return this.attName;
   }

   public boolean isUnboxable() {
      return !this.isRequired ? false : super.isUnboxable();
   }

   public boolean isOptionalPrimitive() {
      return !this.isRequired && super.isUnboxable();
   }

   public Object accept(CPropertyVisitor visitor) {
      return visitor.onAttribute(this);
   }

   public final PropertyKind kind() {
      return PropertyKind.ATTRIBUTE;
   }
}
