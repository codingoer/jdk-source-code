package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.model.TypeUse;
import com.sun.tools.internal.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSSimpleType;

final class STDerivedComplexTypeBuilder extends CTBuilder {
   public boolean isApplicable(XSComplexType ct) {
      return ct.getBaseType().isSimpleType();
   }

   public void build(XSComplexType ct) {
      assert ct.getDerivationMethod() == 1;

      XSSimpleType baseType = ct.getBaseType().asSimpleType();
      this.builder.recordBindingMode(ct, ComplexTypeBindingMode.NORMAL);
      this.simpleTypeBuilder.refererStack.push(ct);
      TypeUse use = this.simpleTypeBuilder.build(baseType);
      this.simpleTypeBuilder.refererStack.pop();
      BIProperty prop = BIProperty.getCustomization(ct);
      CPropertyInfo p = prop.createValueProperty("Value", false, baseType, use, BGMBuilder.getName(baseType));
      this.selector.getCurrentBean().addProperty(p);
      this.green.attContainer(ct);
   }
}
