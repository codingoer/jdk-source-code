package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.tools.internal.xjc.model.CClass;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.reader.RawTypeSet;
import com.sun.tools.internal.xjc.reader.xmlschema.RawTypeSetBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSType;

final class MixedExtendedComplexTypeBuilder extends AbstractExtendedComplexTypeBuilder {
   public boolean isApplicable(XSComplexType ct) {
      if (!this.bgmBuilder.isGenerateMixedExtensions()) {
         return false;
      } else {
         XSType bt = ct.getBaseType();
         return bt.isComplexType() && bt.asComplexType().isMixed() && ct.isMixed() && ct.getDerivationMethod() == 1 && ct.getContentType().asParticle() != null && ct.getExplicitContent().asEmpty() == null;
      }
   }

   public void build(XSComplexType ct) {
      XSComplexType baseType = ct.getBaseType().asComplexType();
      CClass baseClass = this.selector.bindToType(baseType, ct, true);

      assert baseClass != null;

      if (!this.checkIfExtensionSafe(baseType, ct)) {
         this.errorReceiver.error(ct.getLocator(), Messages.ERR_NO_FURTHER_EXTENSION.format(baseType.getName(), ct.getName()));
      } else {
         this.selector.getCurrentBean().setBaseClass(baseClass);
         this.builder.recordBindingMode(ct, ComplexTypeBindingMode.FALLBACK_EXTENSION);
         BIProperty prop = BIProperty.getCustomization(ct);
         RawTypeSet ts = RawTypeSetBuilder.build(ct.getContentType().asParticle(), false);
         CPropertyInfo p = prop.createDummyExtendedMixedReferenceProperty("contentOverrideFor" + ct.getName(), ct, ts);
         this.selector.getCurrentBean().addProperty(p);
         this.green.attContainer(ct);
      }
   }
}
