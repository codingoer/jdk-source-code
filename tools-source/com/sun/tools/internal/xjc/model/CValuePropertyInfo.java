package com.sun.tools.internal.xjc.model;

import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;
import com.sun.xml.internal.xsom.XSComponent;
import javax.xml.namespace.QName;
import org.xml.sax.Locator;

public final class CValuePropertyInfo extends CSingleTypePropertyInfo implements ValuePropertyInfo {
   public CValuePropertyInfo(String name, XSComponent source, CCustomizations customizations, Locator locator, TypeUse type, QName typeName) {
      super(name, type, typeName, source, customizations, locator);
   }

   public final PropertyKind kind() {
      return PropertyKind.VALUE;
   }

   public Object accept(CPropertyVisitor visitor) {
      return visitor.onValue(this);
   }
}
