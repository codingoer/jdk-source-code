package com.sun.tools.internal.xjc.reader.xmlschema.ct;

import com.sun.tools.internal.xjc.model.CClass;
import com.sun.tools.internal.xjc.model.CPropertyInfo;
import com.sun.tools.internal.xjc.reader.RawTypeSet;
import com.sun.tools.internal.xjc.reader.xmlschema.RawTypeSetBuilder;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.internal.xsom.XSComplexType;
import com.sun.xml.internal.xsom.XSParticle;
import com.sun.xml.internal.xsom.XSType;

final class RestrictedComplexTypeBuilder extends CTBuilder {
   public boolean isApplicable(XSComplexType ct) {
      XSType baseType = ct.getBaseType();
      return baseType != this.schemas.getAnyType() && baseType.isComplexType() && ct.getDerivationMethod() == 2;
   }

   public void build(XSComplexType ct) {
      if (this.bgmBuilder.getGlobalBinding().isRestrictionFreshType()) {
         (new FreshComplexTypeBuilder()).build(ct);
      } else {
         XSComplexType baseType = ct.getBaseType().asComplexType();
         CClass baseClass = this.selector.bindToType(baseType, ct, true);

         assert baseClass != null;

         this.selector.getCurrentBean().setBaseClass(baseClass);
         if (this.bgmBuilder.isGenerateMixedExtensions()) {
            boolean forceFallbackInExtension = baseType.isMixed() && ct.isMixed() && ct.getExplicitContent() != null && this.bgmBuilder.inExtensionMode;
            if (forceFallbackInExtension) {
               this.builder.recordBindingMode(ct, ComplexTypeBindingMode.NORMAL);
               BIProperty prop = BIProperty.getCustomization(ct);
               XSParticle particle = ct.getContentType().asParticle();
               if (particle != null) {
                  RawTypeSet ts = RawTypeSetBuilder.build(particle, false);
                  CPropertyInfo p = prop.createDummyExtendedMixedReferenceProperty("Content", ct, ts);
                  this.selector.getCurrentBean().addProperty(p);
               }
            } else {
               this.builder.recordBindingMode(ct, this.builder.getBindingMode(baseType));
            }
         } else {
            this.builder.recordBindingMode(ct, this.builder.getBindingMode(baseType));
         }

      }
   }
}
