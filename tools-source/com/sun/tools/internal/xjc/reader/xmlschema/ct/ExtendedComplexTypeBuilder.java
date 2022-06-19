package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.tools.internal.xjc.model.CClass;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSContentType;
import com.sun.xml.internal.xsom.XSType;

final class ExtendedComplexTypeBuilder extends AbstractExtendedComplexTypeBuilder {
   public boolean isApplicable(XSComplexType ct) {
      XSType baseType = ct.getBaseType();
      return baseType != this.schemas.getAnyType() && baseType.isComplexType() && ct.getDerivationMethod() == 1;
   }

   public void build(XSComplexType ct) {
      XSComplexType baseType = ct.getBaseType().asComplexType();
      CClass baseClass = this.selector.bindToType(baseType, ct, true);

      assert baseClass != null;

      this.selector.getCurrentBean().setBaseClass(baseClass);
      ComplexTypeBindingMode baseTypeFlag = this.builder.getBindingMode(baseType);
      XSContentType explicitContent = ct.getExplicitContent();
      if (!this.checkIfExtensionSafe(baseType, ct)) {
         this.errorReceiver.error(ct.getLocator(), Messages.ERR_NO_FURTHER_EXTENSION.format(baseType.getName(), ct.getName()));
      } else {
         if (explicitContent != null && explicitContent.asParticle() != null) {
            if (baseTypeFlag == ComplexTypeBindingMode.NORMAL) {
               this.builder.recordBindingMode(ct, this.bgmBuilder.getParticleBinder().checkFallback(explicitContent.asParticle()) ? ComplexTypeBindingMode.FALLBACK_REST : ComplexTypeBindingMode.NORMAL);
               this.bgmBuilder.getParticleBinder().build(explicitContent.asParticle());
            } else {
               this.builder.recordBindingMode(ct, baseTypeFlag);
            }
         } else {
            this.builder.recordBindingMode(ct, baseTypeFlag);
         }

         this.green.attContainer(ct);
      }
   }
}
